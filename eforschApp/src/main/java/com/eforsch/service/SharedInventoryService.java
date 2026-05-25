package com.eforsch.service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eforsch.dto.AddressDTO;
import com.eforsch.dto.AddressSimpleDTO;
import com.eforsch.dto.ApproveShareRequestDTO;
import com.eforsch.dto.ApproveShareResponseDataDTO;
import com.eforsch.dto.BorrowedInventoryRequest;
import com.eforsch.dto.BorrowedItemDTO;
import com.eforsch.dto.GroupSharingRequestDTO;
import com.eforsch.dto.MarkAsReceivedRequest;
import com.eforsch.dto.PaginationDTO;
import com.eforsch.dto.ReceiverRequestDTO;
import com.eforsch.dto.RejectShareRequestDTO;
import com.eforsch.dto.RejectShareResponseDataDTO;
import com.eforsch.dto.SelectedSlotDTO;
import com.eforsch.dto.ShareProductWithTimeSlotsDTO;
import com.eforsch.dto.SharedItemRequestDTO;
import com.eforsch.dto.SharedItemsRequestListResponse;
import com.eforsch.dto.TimeSlotDTO;
import com.eforsch.dto.UnshareItemRequest;
import com.eforsch.dto.User;
import com.eforsch.entity.FineChemicalInventory;
import com.eforsch.entity.GroupSharingRequest;
import com.eforsch.entity.Inventory;
import com.eforsch.entity.InventoryTimeSlot;
import com.eforsch.entity.SharedInventory;
import com.eforsch.repository.FineChemicalInventoryRepository;
import com.eforsch.repository.GroupSharingRequestRepository;
import com.eforsch.repository.InventoryRepository;
import com.eforsch.repository.InventoryTimeSlotRepository;
import com.eforsch.repository.SharedInventoryRepository;
import com.eforsch.util.ShareInventoryVO;

@Service
public class SharedInventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Autowired
	private FineChemicalInventoryRepository fineChemicalInventoryRepository;
	
	@Autowired
	private GroupSharingRequestRepository groupSharingRequestRepository;
	
	@Autowired
	private SharedInventoryRepository sharedInventoryRepository;

	@Autowired
	private InventoryTimeSlotRepository inventoryTimeSlotRepository;

	
	public ShareInventoryVO shareProduct(ShareProductWithTimeSlotsDTO requestDTO) {
		
		Long productId = requestDTO.getProductId();
		String inventoryType = requestDTO.getInventoryType();
		User user = requestDTO.getUser();
		Integer sharedQuantity = requestDTO.getQuantity();
		
		if (productId == null) {
			throw new IllegalArgumentException("Product ID is required");
		}
		
		if (inventoryType == null || inventoryType.isEmpty()) {
			throw new IllegalArgumentException("Inventory type cannot be null or empty");
		}
		
		if (user == null) {
			throw new IllegalArgumentException("User information is required");
		}
		
		// Validate time slots if provided
		List<TimeSlotDTO> timeSlots = requestDTO.getTimeSlots();
		
		if (inventoryType.equalsIgnoreCase("generalInventory")) {
			Inventory inventory = inventoryRepository.findByProductId(productId);
			if (inventory == null) {
				throw new RuntimeException("Inventory Product not found");
			}
			
			// Use requested quantity or fall back to full inventory quantity
			Integer quantityToShare = (sharedQuantity != null && sharedQuantity > 0) ? sharedQuantity : inventory.getQuantity();
			
			// Create SharedInventory record
			SharedInventory sharedInventory = new SharedInventory();
			sharedInventory.setProductId(productId+"");
			sharedInventory.setInventoryType(inventoryType);
			sharedInventory.setSharedByUserId(user.getId());
			sharedInventory.setSharedByUserEmail(user.getEmail());
			sharedInventory.setSharedByUserName(user.getName());
			sharedInventory.setSharedByUserRole(user.getRole());
			sharedInventory.setSharedByGroupName(user.getGroupName());
			sharedInventory.setTotalSharedQuantity(quantityToShare);
			sharedInventory.setAvailableQuantity(quantityToShare);
			
			// Store address if provided
			if (requestDTO.getAddress() != null) {
				sharedInventory.setAddressLine1(requestDTO.getAddress().getLine1());
				sharedInventory.setAddressLine2(requestDTO.getAddress().getLine2());
				sharedInventory.setAddressCity(requestDTO.getAddress().getCity());
				sharedInventory.setAddressState(requestDTO.getAddress().getState());
				sharedInventory.setAddressPostalCode(requestDTO.getAddress().getPostalCode());
				sharedInventory.setAddressCountry(requestDTO.getAddress().getCountry());
			} 
			
			sharedInventoryRepository.save(sharedInventory); 
			
			// Save time slots if provided
			List<TimeSlotDTO> savedTimeSlotsDTO = new ArrayList<>();
			if (timeSlots != null && !timeSlots.isEmpty()) {
				List<InventoryTimeSlot> savedTimeSlots = new ArrayList<>();
				for (TimeSlotDTO slotDTO : timeSlots) {
					// Calculate startTime and endTime from day, fromTime, toTime
					LocalDateTime startTime = calculateDateTimeFromDayAndTime(slotDTO.getDay(), slotDTO.getFromTime());
					LocalDateTime endTime = calculateDateTimeFromDayAndTime(slotDTO.getDay(), slotDTO.getToTime());
					
					InventoryTimeSlot timeSlot = new InventoryTimeSlot(
						sharedInventory,
						slotDTO.getSlotNumber(),
						slotDTO.getDay(),
						slotDTO.getFromTime(),
						slotDTO.getToTime(),
						startTime,
						endTime
					);
					InventoryTimeSlot savedSlot = inventoryTimeSlotRepository.save(timeSlot);
					savedTimeSlots.add(savedSlot);
					
					// Create TimeSlotDTO with all fields
					TimeSlotDTO responseSlot = new TimeSlotDTO(
						savedSlot.getTimeSlotId(),
						savedSlot.getSlotNumber(),
						savedSlot.getDay(),
						savedSlot.getFromTime(),
						savedSlot.getToTime(),
						savedSlot.getStartTime(),
						savedSlot.getEndTime()
					);
					savedTimeSlotsDTO.add(responseSlot);
				}
				sharedInventory.setTimeSlots(savedTimeSlots);
			}
			
			// Update Inventory: deduct shared quantity and set shared flag
			// shared=true only if ALL quantity is shared away (remaining <= 0)
			Integer remainingQuantity = inventory.getQuantity() - quantityToShare;
			inventory.setQuantity(remainingQuantity);
			inventory.setShared(remainingQuantity < 0);
			inventory.setAddedby(user.getName());
			inventory = inventoryRepository.save(inventory);
			
			ShareInventoryVO vo = entityToShareInventoryVO(inventory);
			vo.setTimeSlots(savedTimeSlotsDTO);
			
			// Map address from SharedInventory
			if (sharedInventory.getAddressLine1() != null || sharedInventory.getAddressCity() != null) {
				AddressDTO addressDTO = new AddressDTO();
				addressDTO.setLine1(sharedInventory.getAddressLine1());
				addressDTO.setLine2(sharedInventory.getAddressLine2());
				addressDTO.setCity(sharedInventory.getAddressCity());
				addressDTO.setState(sharedInventory.getAddressState());
				addressDTO.setPostalCode(sharedInventory.getAddressPostalCode());
				addressDTO.setCountry(sharedInventory.getAddressCountry());
				vo.setAddress(addressDTO);
			}
			
			return vo;
			
		} else if(inventoryType.equalsIgnoreCase("fineChemicals")) {
			
			FineChemicalInventory fineChemicalInventory = fineChemicalInventoryRepository.findByProductId(productId);
			if (fineChemicalInventory == null) {
				throw new RuntimeException("Fine Chemical Product not found");
			}
			
			// Use requested quantity or fall back to inventory quantity
			Integer quantityToShare = sharedQuantity;
			if (quantityToShare == null || quantityToShare <= 0) {
				if(fineChemicalInventory.getQuantity() != null && !fineChemicalInventory.getQuantity().isEmpty()) {
					try {
						quantityToShare = Integer.parseInt(fineChemicalInventory.getQuantity());
					} catch (Exception e) {
						quantityToShare = 0;
					}
				} else {
					quantityToShare = 0;
				}
			}
			
			// Create SharedInventory record
			SharedInventory sharedInventory = new SharedInventory();
			sharedInventory.setProductId(productId+"");
			sharedInventory.setInventoryType("fineChemicalInventory");
			sharedInventory.setSharedByUserId(user.getId());
			sharedInventory.setSharedByUserEmail(user.getEmail());
			sharedInventory.setSharedByUserName(user.getName());
			sharedInventory.setSharedByUserRole(user.getRole());
			sharedInventory.setSharedByGroupName(user.getGroupName());
			sharedInventory.setTotalSharedQuantity(quantityToShare);
			sharedInventory.setAvailableQuantity(quantityToShare);
			
			// Store address if provided
			if (requestDTO.getAddress() != null) {
				sharedInventory.setAddressLine1(requestDTO.getAddress().getLine1());
				sharedInventory.setAddressLine2(requestDTO.getAddress().getLine2());
				sharedInventory.setAddressCity(requestDTO.getAddress().getCity());
				sharedInventory.setAddressState(requestDTO.getAddress().getState());
				sharedInventory.setAddressPostalCode(requestDTO.getAddress().getPostalCode());
				sharedInventory.setAddressCountry(requestDTO.getAddress().getCountry());
			}
			
			sharedInventoryRepository.save(sharedInventory);
			
			// Save time slots if provided
			List<TimeSlotDTO> savedTimeSlotsDTO = new ArrayList<>();
			if (timeSlots != null && !timeSlots.isEmpty()) {
				List<InventoryTimeSlot> savedTimeSlots = new ArrayList<>();
				for (TimeSlotDTO slotDTO : timeSlots) {
					// Calculate startTime and endTime from day, fromTime, toTime
					LocalDateTime startTime = calculateDateTimeFromDayAndTime(slotDTO.getDay(), slotDTO.getFromTime());
					LocalDateTime endTime = calculateDateTimeFromDayAndTime(slotDTO.getDay(), slotDTO.getToTime());
					
					InventoryTimeSlot timeSlot = new InventoryTimeSlot(
						sharedInventory,
						slotDTO.getSlotNumber(),
						slotDTO.getDay(),
						slotDTO.getFromTime(),
						slotDTO.getToTime(),
						startTime,
						endTime
					);
					InventoryTimeSlot savedSlot = inventoryTimeSlotRepository.save(timeSlot);
					savedTimeSlots.add(savedSlot);
					
					// Create TimeSlotDTO with all fields
					TimeSlotDTO responseSlot = new TimeSlotDTO(
						savedSlot.getTimeSlotId(),
						savedSlot.getSlotNumber(),
						savedSlot.getDay(),
						savedSlot.getFromTime(),
						savedSlot.getToTime(),
						savedSlot.getStartTime(),
						savedSlot.getEndTime()
					);
					savedTimeSlotsDTO.add(responseSlot);
				}
				sharedInventory.setTimeSlots(savedTimeSlots);
			}
			
			// Update FineChemicalInventory: deduct shared quantity and set shared flag
			// shared=true only if ALL quantity is shared away (remaining <= 0)
			Integer remainingFineChemQuantity = quantityToShare != null ? 
				(Integer.parseInt(fineChemicalInventory.getQuantity() != null ? fineChemicalInventory.getQuantity() : "0") - quantityToShare) : 
				Integer.parseInt(fineChemicalInventory.getQuantity() != null ? fineChemicalInventory.getQuantity() : "0");
			fineChemicalInventory.setQuantity(String.valueOf(Math.max(0, remainingFineChemQuantity)));
			fineChemicalInventory.setShared(remainingFineChemQuantity < 0);
			FineChemicalInventory inventory = fineChemicalInventoryRepository.save(fineChemicalInventory);
			
			ShareInventoryVO vo = entityToShareInventoryVO(inventory);
			vo.setTimeSlots(savedTimeSlotsDTO);
			
			// Map address from SharedInventory
			if (sharedInventory.getAddressLine1() != null || sharedInventory.getAddressCity() != null) {
				AddressDTO addressDTO = new AddressDTO();
				addressDTO.setLine1(sharedInventory.getAddressLine1());
				addressDTO.setLine2(sharedInventory.getAddressLine2());
				addressDTO.setCity(sharedInventory.getAddressCity());
				addressDTO.setState(sharedInventory.getAddressState());
				addressDTO.setPostalCode(sharedInventory.getAddressPostalCode());
				addressDTO.setCountry(sharedInventory.getAddressCountry());
				vo.setAddress(addressDTO);
			}
			
			return vo;
		}
		
		return null;
	}

	
	public ShareInventoryVO shareProduct(Long productId, String inventoryType, User user) {
		
		if (inventoryType == null || inventoryType.isEmpty()) {
			throw new IllegalArgumentException("Inventory type cannot be null or empty");
		}
		if (inventoryType.equalsIgnoreCase("generalInventory")) {
			Inventory inventory = inventoryRepository.findByProductId(productId);
			if (inventory == null) {
				throw new RuntimeException("Inventory Product not found");
			}
			
			// Create SharedInventory record
			SharedInventory sharedInventory = new SharedInventory();
			sharedInventory.setProductId(productId+"");
			sharedInventory.setInventoryType(inventoryType);
			sharedInventory.setSharedByUserId(user.getId());
			sharedInventory.setSharedByUserEmail(user.getEmail());
			sharedInventory.setSharedByUserName(user.getName());
			sharedInventory.setSharedByUserRole(user.getRole());
			sharedInventory.setSharedByGroupName(user.getGroupName());
			sharedInventory.setTotalSharedQuantity(inventory.getQuantity());
			sharedInventory.setAvailableQuantity(inventory.getQuantity());
			sharedInventoryRepository.save(sharedInventory);
			
			// Update Inventory shared flag
			inventory.setShared(true);
			inventory.setAddedby(user.getName());
			inventory = inventoryRepository.save(inventory);
			return entityToShareInventoryVO(inventory);
			
		}else if(inventoryType.equalsIgnoreCase("fineChemicalInventory") || inventoryType.equalsIgnoreCase("fineChemicals")) {
			
			FineChemicalInventory fineChemicalInventory = fineChemicalInventoryRepository.findByProductId(productId);
			if (fineChemicalInventory == null) {
				throw new RuntimeException("Fine Chemical Product not found");
			}
			
			// Create SharedInventory record
			SharedInventory sharedInventory = new SharedInventory();
			sharedInventory.setProductId(productId+"");
			sharedInventory.setInventoryType(inventoryType);
			sharedInventory.setSharedByUserId(user.getId());
			sharedInventory.setSharedByUserEmail(user.getEmail());
			sharedInventory.setSharedByUserName(user.getName());
			sharedInventory.setSharedByUserRole(user.getRole());
			sharedInventory.setSharedByGroupName(user.getGroupName());
			if (fineChemicalInventory.getQuantity() != null && !fineChemicalInventory.getQuantity().isEmpty()) {
				try {
					sharedInventory.setTotalSharedQuantity(Integer.parseInt(fineChemicalInventory.getQuantity()));
					sharedInventory.setAvailableQuantity(Integer.parseInt(fineChemicalInventory.getQuantity()));
				} catch (Exception e) {
					sharedInventory.setTotalSharedQuantity(0);
					sharedInventory.setAvailableQuantity(0);
				}
			} else {
				sharedInventory.setTotalSharedQuantity(0);
				sharedInventory.setAvailableQuantity(0);
			}
			
			sharedInventoryRepository.save(sharedInventory); 
			
			// Update FineChemicalInventory shared flag
			fineChemicalInventory.setShared(true);
			FineChemicalInventory inventory = fineChemicalInventoryRepository.save(fineChemicalInventory);
			return entityToShareInventoryVO(inventory);
		}
		
		return null;
	}

	/**
	 * @param inv
	 * @return
	 */
	public static ShareInventoryVO entityToShareInventoryVO(Inventory inv) {
        if (inv == null) return null;

        ShareInventoryVO vo = new ShareInventoryVO();

        vo.setProductId(inv.getProductId());
        vo.setProductName(inv.getProductname());
        vo.setCatalogue(inv.getCatalogue());
        vo.setCompanyName(inv.getCompanyname());

        // Quantity: if your entity has Integer, keep it, else convert String → Integer
        if (inv.getQuantity() != null) {
            try {
                vo.setQuantity(Integer.valueOf(inv.getQuantity().toString()));
            } catch (Exception e) {
                vo.setQuantity(null);
            }
        }

        vo.setGroupName(inv.getGroupName());
        vo.setCompanyInternalNo(inv.getCompanyinternalno());
        vo.setSapMaterialNo(inv.getSapmaterialno());
        vo.setWeightVolSubQty(inv.getWeightvolsubqty());
        vo.setBudgetNo(inv.getBudgetno());
        vo.setConcentration(inv.getConcentration());

        vo.setRemarks(inv.getRemarks());
        vo.setOrderDate(inv.getOrderdate());
        vo.setExpiryDate(inv.getExpirydate());

        vo.setAddedBy(inv.getAddedby());
        vo.setShared(inv.isShared());
        vo.setInventoryType("generalInventory");

        return vo;
    }
	
	public static ShareInventoryVO entityToShareInventoryVO(FineChemicalInventory inv) {
        if (inv == null) return null;

        ShareInventoryVO vo = new ShareInventoryVO();

        vo.setProductId(inv.getProductId());
        vo.setProductName(inv.getProductname());
        vo.setCatalogue(inv.getCatalogue());
        vo.setCompanyName(inv.getCompanyname());

        // Quantity: if your entity has Integer, keep it, else convert String → Integer
        if (inv.getQuantity() != null) {
            try {
                vo.setQuantity(Integer.valueOf(inv.getQuantity().toString()));
            } catch (Exception e) {
                vo.setQuantity(null);
            }
        }

        vo.setGroupName(inv.getGroupName());
        vo.setBudgetNo(inv.getBudgetno());
        vo.setConcentration(inv.getConcentration());
        vo.setOrderDate(inv.getOrderdate());
        vo.setExpiryDate(inv.getExpiryDate());
        vo.setRemarks(inv.getRemarks());
        vo.setAddedBy(inv.getOrderedby());
        vo.setShared(inv.isShared());
        vo.setInventoryType("fineChemicalInventory");

        return vo;
    }
	
	
	public Map<String, Object> getProductList(int page, int size, boolean shared, User user) {
		List<ShareInventoryVO> inventoryVOList = new ArrayList<>();
		
		// Validate user and group
		if (user == null || user.getGroupName() == null || user.getGroupName().isEmpty()) {
			throw new IllegalArgumentException("User and group name are required");
		}
		
		String userGroupName = user.getGroupName();
		
		// Fetch all SharedInventory records for this group
		List<SharedInventory> sharedInventoriesByGroup = sharedInventoryRepository.findBySharedByGroupName(userGroupName);
		
		if (sharedInventoriesByGroup == null || sharedInventoriesByGroup.isEmpty()) {
			throw new RuntimeException("No shared products found for your group");
		}
		
		// Extract product IDs and inventory types with SharedInventory reference
		Map<String, Map<String, Object>> productIdToData = new HashMap<>();
		for (SharedInventory si : sharedInventoriesByGroup) {
			Map<String, Object> data = new HashMap<>();
			data.put("inventoryType", si.getInventoryType());
			data.put("sharedInventoryId", si.getSharedInventoryId());
			data.put("sharedInventory", si);
			productIdToData.put(si.getProductId(), data);
		}
		
		// Fetch Inventory and FineChemicalInventory records with time slots
		for (Map.Entry<String, Map<String, Object>> entry : productIdToData.entrySet()) {
			String productIdStr = entry.getKey();
			Long productId = null;
			try {
				productId = Long.parseLong(productIdStr);
			} catch (NumberFormatException e) {
				// productId is not numeric, skip this product
				continue;
			}
			String inventoryType = (String) entry.getValue().get("inventoryType");
			Long sharedInventoryId = (Long) entry.getValue().get("sharedInventoryId");
			SharedInventory sharedInventory = (SharedInventory) entry.getValue().get("sharedInventory");
			
			if (inventoryType.equalsIgnoreCase("generalInventory")) {
				Inventory inventory = inventoryRepository.findByProductId(productId);
				// Include all shared items regardless of available quantity
				// This allows users to manage/unshare items even when all quantity is requested
				if (inventory != null && sharedInventory != null) {
					ShareInventoryVO inventoryVO = entityToShareInventoryVO(inventory);
					// Override quantity with available quantity from SharedInventory
					inventoryVO.setQuantity(sharedInventory.getAvailableQuantity());
					// Fetch and set time slots
					List<TimeSlotDTO> timeSlots = getTimeSlots(sharedInventoryId);
					inventoryVO.setTimeSlots(timeSlots);
					
					// Map address from SharedInventory
					if (sharedInventory != null && (sharedInventory.getAddressLine1() != null || sharedInventory.getAddressCity() != null)) {
						AddressDTO addressDTO = new AddressDTO();
						addressDTO.setLine1(sharedInventory.getAddressLine1());
						addressDTO.setLine2(sharedInventory.getAddressLine2());
						addressDTO.setCity(sharedInventory.getAddressCity());
						addressDTO.setState(sharedInventory.getAddressState());
						addressDTO.setPostalCode(sharedInventory.getAddressPostalCode());
						addressDTO.setCountry(sharedInventory.getAddressCountry());
						inventoryVO.setAddress(addressDTO);
					}
					
					// Set shared by information
					inventoryVO.setSharedByUserId(sharedInventory.getSharedByUserId());
					inventoryVO.setSharedByUserEmail(sharedInventory.getSharedByUserEmail());
					inventoryVO.setSharedByUserName(sharedInventory.getSharedByUserName());
					inventoryVO.setSharedByUserRole(sharedInventory.getSharedByUserRole());
					inventoryVO.setSharedByGroupName(sharedInventory.getSharedByGroupName());
					
					inventoryVOList.add(inventoryVO);
				}
			} else if (inventoryType.equalsIgnoreCase("fineChemicalInventory") || inventoryType.equalsIgnoreCase("fineChemicals")) {
				FineChemicalInventory fineChemicalInventory = fineChemicalInventoryRepository.findByProductId(productId);
				if (fineChemicalInventory != null) {
					ShareInventoryVO inventoryVO = entityToShareInventoryVO(fineChemicalInventory);
					// Override quantity with available quantity from SharedInventory
					if (sharedInventory != null && sharedInventory.getAvailableQuantity() != null) {
						inventoryVO.setQuantity(sharedInventory.getAvailableQuantity());
					}
					// Fetch and set time slots
					List<TimeSlotDTO> timeSlots = getTimeSlots(sharedInventoryId);
					inventoryVO.setTimeSlots(timeSlots);
					
					// Map address from SharedInventory
					if (sharedInventory != null && (sharedInventory.getAddressLine1() != null || sharedInventory.getAddressCity() != null)) {
						AddressDTO addressDTO = new AddressDTO();
						addressDTO.setLine1(sharedInventory.getAddressLine1());
						addressDTO.setLine2(sharedInventory.getAddressLine2());
						addressDTO.setCity(sharedInventory.getAddressCity());
						addressDTO.setState(sharedInventory.getAddressState());
						addressDTO.setPostalCode(sharedInventory.getAddressPostalCode());
						addressDTO.setCountry(sharedInventory.getAddressCountry());
						inventoryVO.setAddress(addressDTO);
					}
					
					// Set shared by information
					inventoryVO.setSharedByUserId(sharedInventory.getSharedByUserId());
					inventoryVO.setSharedByUserEmail(sharedInventory.getSharedByUserEmail());
					inventoryVO.setSharedByUserName(sharedInventory.getSharedByUserName());
					inventoryVO.setSharedByUserRole(sharedInventory.getSharedByUserRole());
					inventoryVO.setSharedByGroupName(sharedInventory.getSharedByGroupName());
					
					inventoryVOList.add(inventoryVO);
				}
			}
		}
		
		// Apply pagination manually
		int totalRecords = inventoryVOList.size();
		int totalPages = (int) Math.ceil((double) totalRecords / size);
		int startIndex = (page - 1) * size;
		int endIndex = Math.min(startIndex + size, totalRecords);
		
		List<ShareInventoryVO> paginatedList = new ArrayList<>();
		if (startIndex < totalRecords) {
			paginatedList = inventoryVOList.subList(startIndex, endIndex);
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("list", paginatedList);
		response.put("pagination", Map.of(
				"currentPage", page,
				"pageSize", size,
				"totalRecords", totalRecords,
				"totalPages", totalPages
		));
		
		return response;
	}
	
	/**
	 * Get all shared products from all groups (no group filtering)
	 * @param page the page number (1-indexed)
	 * @param size the page size
	 * @return map containing paginated list and pagination info
	 */
	public Map<String, Object> getAllProductList(int page, int size) {
		List<ShareInventoryVO> inventoryVOList = new ArrayList<>();
		
		// Fetch all SharedInventory records (no group filtering)
		List<SharedInventory> allSharedInventories = sharedInventoryRepository.findAll();
		
		if (allSharedInventories == null || allSharedInventories.isEmpty()) {
			throw new RuntimeException("No shared products found");
		}
		
		// Extract product IDs and inventory types with SharedInventory reference
		Map<String, Map<String, Object>> productIdToData = new HashMap<>();
		for (SharedInventory si : allSharedInventories) {
			Map<String, Object> data = new HashMap<>();
			data.put("inventoryType", si.getInventoryType());
			data.put("sharedInventoryId", si.getSharedInventoryId());
			data.put("sharedInventory", si);
			productIdToData.put(si.getProductId(), data);
		}
		
		// Fetch Inventory and FineChemicalInventory records with time slots
		for (Map.Entry<String, Map<String, Object>> entry : productIdToData.entrySet()) {
			String productIdStr = entry.getKey();
			Long productId = null;
			try {
				productId = Long.parseLong(productIdStr);
			} catch (NumberFormatException e) {
				// productId is not numeric, skip this product
				continue;
			}
			String inventoryType = (String) entry.getValue().get("inventoryType");
			Long sharedInventoryId = (Long) entry.getValue().get("sharedInventoryId");
			SharedInventory sharedInventory = (SharedInventory) entry.getValue().get("sharedInventory");
			
			if (inventoryType.equalsIgnoreCase("generalInventory")) {
				Inventory inventory = inventoryRepository.findByProductId(productId);
				// Filter: only include if available quantity is greater than 0
				if (inventory != null && !inventory.isShared() && 
					sharedInventory != null && sharedInventory.getAvailableQuantity() != null && 
					sharedInventory.getAvailableQuantity() > 0) {
					ShareInventoryVO inventoryVO = entityToShareInventoryVO(inventory);
					// Override quantity with available quantity from SharedInventory
					inventoryVO.setQuantity(sharedInventory.getAvailableQuantity());
					// Fetch and set time slots
					List<TimeSlotDTO> timeSlots = getTimeSlots(sharedInventoryId);
					inventoryVO.setTimeSlots(timeSlots);
					
					// Map address from SharedInventory
					if (sharedInventory != null && (sharedInventory.getAddressLine1() != null || sharedInventory.getAddressCity() != null)) {
						AddressDTO addressDTO = new AddressDTO();
						addressDTO.setLine1(sharedInventory.getAddressLine1());
						addressDTO.setLine2(sharedInventory.getAddressLine2());
						addressDTO.setCity(sharedInventory.getAddressCity());
						addressDTO.setState(sharedInventory.getAddressState());
						addressDTO.setPostalCode(sharedInventory.getAddressPostalCode());
						addressDTO.setCountry(sharedInventory.getAddressCountry());
						inventoryVO.setAddress(addressDTO);
					}
					
					// Set shared by information
					inventoryVO.setSharedByUserId(sharedInventory.getSharedByUserId());
					inventoryVO.setSharedByUserEmail(sharedInventory.getSharedByUserEmail());
					inventoryVO.setSharedByUserName(sharedInventory.getSharedByUserName());
					inventoryVO.setSharedByUserRole(sharedInventory.getSharedByUserRole());
					inventoryVO.setSharedByGroupName(sharedInventory.getSharedByGroupName());
					
					inventoryVOList.add(inventoryVO);
				}
			} else if (inventoryType.equalsIgnoreCase("fineChemicalInventory") || inventoryType.equalsIgnoreCase("fineChemicals")) {
				FineChemicalInventory fineChemicalInventory = fineChemicalInventoryRepository.findByProductId(productId);
				// Filter: only include if available quantity is greater than 0
				if (fineChemicalInventory != null && !fineChemicalInventory.isShared() && 
					sharedInventory != null && sharedInventory.getAvailableQuantity() != null && 
					sharedInventory.getAvailableQuantity() > 0) {
					ShareInventoryVO inventoryVO = entityToShareInventoryVO(fineChemicalInventory);
					// Override quantity with available quantity from SharedInventory
					inventoryVO.setQuantity(sharedInventory.getAvailableQuantity());
					// Fetch and set time slots
					List<TimeSlotDTO> timeSlots = getTimeSlots(sharedInventoryId);
					inventoryVO.setTimeSlots(timeSlots);
					
					// Map address from SharedInventory
					if (sharedInventory != null && (sharedInventory.getAddressLine1() != null || sharedInventory.getAddressCity() != null)) {
						AddressDTO addressDTO = new AddressDTO();
						addressDTO.setLine1(sharedInventory.getAddressLine1());
						addressDTO.setLine2(sharedInventory.getAddressLine2());
						addressDTO.setCity(sharedInventory.getAddressCity());
						addressDTO.setState(sharedInventory.getAddressState());
						addressDTO.setPostalCode(sharedInventory.getAddressPostalCode());
						addressDTO.setCountry(sharedInventory.getAddressCountry());
						inventoryVO.setAddress(addressDTO);
					}
					
					// Set shared by information
					inventoryVO.setSharedByUserId(sharedInventory.getSharedByUserId());
					inventoryVO.setSharedByUserEmail(sharedInventory.getSharedByUserEmail());
					inventoryVO.setSharedByUserName(sharedInventory.getSharedByUserName());
					inventoryVO.setSharedByUserRole(sharedInventory.getSharedByUserRole());
					inventoryVO.setSharedByGroupName(sharedInventory.getSharedByGroupName());
					
					inventoryVOList.add(inventoryVO);
				}
			}
		}
		
		// Apply pagination manually
		int totalRecords = inventoryVOList.size();
		int totalPages = (int) Math.ceil((double) totalRecords / size);
		int startIndex = (page - 1) * size;
		int endIndex = Math.min(startIndex + size, totalRecords);
		
		List<ShareInventoryVO> paginatedList = new ArrayList<>();
		if (startIndex < totalRecords) {
			paginatedList = inventoryVOList.subList(startIndex, endIndex);
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("list", paginatedList);
		response.put("pagination", Map.of(
				"currentPage", page,
				"pageSize", size,
				"totalRecords", totalRecords,
				"totalPages", totalPages
		));
		
		return response;
	}
	
	public Map<String, Object> createGroupSharingRequest(GroupSharingRequestDTO requestDTO) {
		Map<String, Object> response = new HashMap<>();
		
		// Validation 1: Quantity must be greater than zero
		if (requestDTO.getQuantity() == null || requestDTO.getQuantity() <= 0) {
			response.put("success", false);
			response.put("errorCode", "INVALID_QUANTITY");
			response.put("message", "Quantity must be greater than zero");
			return response;
		}
		
		// Validation 2: Check if time slots exist and are valid
		if (requestDTO.getTimeSlots() == null || requestDTO.getTimeSlots().isEmpty()) {
			response.put("success", false);
			response.put("errorCode", "INVALID_TIME_SLOTS");
			response.put("message", "At least one time slot must be provided");
			return response;
		}
		
		// Validation 3: Check if address is provided and valid
		if (requestDTO.getAddress() == null) {
			response.put("success", false);
			response.put("errorCode", "INVALID_ADDRESS");
			response.put("message", "Exchange address is required");
			return response;
		}
		
		// Validation 4: Check if user details are provided
		if (requestDTO.getUser() == null) {
			response.put("success", false);
			response.put("errorCode", "INVALID_USER");
			response.put("message", "User details are required");
			return response;
		}
		
		// Validation 5: Product ID must be provided
		String productId = requestDTO.getProductId();
		if (productId == null || productId.isEmpty()) {
			response.put("success", false);
			response.put("errorCode", "INVALID_PRODUCT_ID");
			response.put("message", "Product ID is required");
			return response;
		}
		
		// Check if product exists in shared inventory
		Optional<SharedInventory> sharedInventory = sharedInventoryRepository.findByProductId(productId);
		if (!sharedInventory.isPresent()) {
			response.put("success", false);
			response.put("errorCode", "PRODUCT_NOT_FOUND");
			response.put("message", "Product not found in shared inventory");
			return response;
		}
		
		SharedInventory product = sharedInventory.get();
		String inventoryType = product.getInventoryType();
		Integer availableQuantity = 0;
		
		// Get available quantity from the correct inventory table
		// Try to parse productId as Long first for backward compatibility
		Long numericProductId = null;
		try {
			numericProductId = Long.parseLong(productId);
		} catch (NumberFormatException e) {
			// productId is not numeric, that's okay - will use string version
		}
		
		if (inventoryType.equalsIgnoreCase("generalInventory")) {
			Inventory inventory = null;
			if (numericProductId != null) {
				inventory = inventoryRepository.findByProductId(numericProductId);
			}
			
			if (inventory == null) {
				response.put("success", false);
				response.put("errorCode", "ITEM_NOT_FOUND");
				response.put("message", "Item not found in General Inventory");
				return response;
			}
			
			if (product.getAvailableQuantity() != null) {
				try {
					availableQuantity = Integer.parseInt(product.getAvailableQuantity().toString());
				} catch (Exception e) {
					availableQuantity = 0;
				}
			}
		} else if (inventoryType.equalsIgnoreCase("fineChemicalInventory") || inventoryType.equalsIgnoreCase("fineChemicals")) {
			FineChemicalInventory fineChemicalInventory = null;
			if (numericProductId != null) {
				fineChemicalInventory = fineChemicalInventoryRepository.findByProductId(numericProductId);
			}
			
			if (fineChemicalInventory == null) {
				response.put("success", false);
				response.put("errorCode", "ITEM_NOT_FOUND");
				response.put("message", "Item not found in Fine Chemical Inventory");
				return response;
			}
			
			if (product.getAvailableQuantity() != null) {
				try {
					availableQuantity = Integer.parseInt(product.getAvailableQuantity().toString());
				} catch (Exception e) {
					availableQuantity = 0;
				}
			}
		}
		
		// Validation 6: Check if requested quantity is available
		if (requestDTO.getQuantity() > availableQuantity) {
			response.put("success", false);
			response.put("errorCode", "INSUFFICIENT_QUANTITY");
			response.put("message", "Requested quantity exceeds available stock");
			return response;
		}
		
		// Validation 7: Check for existing active request for this product by the same user
		Optional<GroupSharingRequest> existingRequest = groupSharingRequestRepository
			.findByProductIdAndUserIdAndStatusNot(
				productId,
				requestDTO.getUser().getId(), 
				"CANCELLED"
			);
		
		if (existingRequest.isPresent()) {
			response.put("success", false);
			response.put("errorCode", "REQUEST_ALREADY_EXISTS");
			response.put("message", "Active request already exists for this item");
			return response;
		}
		
		// Create new request
		GroupSharingRequest request = new GroupSharingRequest();
		request.setProductId(productId);
		request.setItemId(productId); // Keep itemId same as productId for compatibility
		request.setInventoryType(inventoryType);
		request.setQuantity(requestDTO.getQuantity());
		request.setStatus("PENDING");
		request.setUserId(requestDTO.getUser().getId());
		request.setUserEmail(requestDTO.getUser().getEmail());
		request.setUserName(requestDTO.getUser().getName());
		request.setRequesterGroup(requestDTO.getUser().getGroupName());
		
		// Set address fields
		request.setAddressLine1(requestDTO.getAddress().getLine1());
		request.setAddressLine2(requestDTO.getAddress().getLine2());
		request.setCity(requestDTO.getAddress().getCity());
		request.setState(requestDTO.getAddress().getState());
		request.setPostalCode(requestDTO.getAddress().getPostalCode());
		request.setCountry(requestDTO.getAddress().getCountry());
		
		// Set time slot (only one time slot expected)
		TimeSlotDTO firstTimeSlot = requestDTO.getTimeSlots().get(0);
		request.setSlotDate(firstTimeSlot.getDate());
		request.setSlotTime(firstTimeSlot.getTime());
		
		// Save request
		GroupSharingRequest savedRequest = groupSharingRequestRepository.save(request);
		
		// Deduct quantity from SharedInventory
		Integer currentAvailableQuantity = product.getAvailableQuantity() != null ? 
			Integer.parseInt(product.getAvailableQuantity().toString()) : 0;
		Integer newAvailableQuantity = currentAvailableQuantity - requestDTO.getQuantity();
		
		product.setAvailableQuantity(newAvailableQuantity);
		sharedInventoryRepository.save(product);
		
		// Build success response
		response.put("success", true);
		response.put("message", "Request created successfully");
		Map<String, Object> data = new HashMap<>();
		data.put("requestId", "req_" + savedRequest.getRequestId());
		data.put("status", savedRequest.getStatus());
		data.put("requestedQuantity", savedRequest.getQuantity());
		response.put("data", data);
		
		return response;
	}
	
	
	@Transactional
	public void revokeShareProduct(String productId, String inventoryType) {
		if (productId == null || productId.isEmpty()) {
			throw new IllegalArgumentException("Product ID cannot be null or empty");
		}
		if (inventoryType == null || inventoryType.isEmpty()) {
			throw new IllegalArgumentException("Inventory type cannot be null or empty");
		}
		
		Optional<SharedInventory> sharedInventory = sharedInventoryRepository.findByProductIdAndInventoryType(productId, inventoryType);
		
		if (sharedInventory.isPresent()) {
			SharedInventory sharedInv = sharedInventory.get();
			Integer totalSharedQuantity = sharedInv.getTotalSharedQuantity() != null ? sharedInv.getTotalSharedQuantity() : 0;
			
			// Delete from SharedInventory table (cascade will delete time slots)
			sharedInventoryRepository.deleteByProductIdAndInventoryType(productId, inventoryType);
			
			// Try to parse productId to Long for lookup in inventory tables
			Long numericProductId = null;
			try {
				numericProductId = Long.parseLong(productId);
			} catch (NumberFormatException e) {
				// productId is not numeric, that's okay
			}
			
			// Update the correct inventory table based on type
			if (inventoryType.equalsIgnoreCase("generalInventory")) {
				if (numericProductId != null) {
					Inventory inventory = inventoryRepository.findByProductId(numericProductId);
					if (inventory != null) {
						// Restore the shared quantity back to inventory
						Integer currentQuantity = inventory.getQuantity() != null ? inventory.getQuantity() : 0;
						inventory.setQuantity(currentQuantity + totalSharedQuantity);
						inventory.setShared(false);
						inventoryRepository.save(inventory);
					}
				}
			} else if (inventoryType.equalsIgnoreCase("fineChemicalInventory") || inventoryType.equalsIgnoreCase("fineChemicals")) {
				if (numericProductId != null) {
					FineChemicalInventory fineChemicalInventory = fineChemicalInventoryRepository.findByProductId(numericProductId);
					if (fineChemicalInventory != null) {
						// Restore the shared quantity back to inventory
						Integer currentQuantity = fineChemicalInventory.getQuantity() != null ? 
							Integer.parseInt(fineChemicalInventory.getQuantity()) : 0;
						fineChemicalInventory.setQuantity(String.valueOf(currentQuantity + totalSharedQuantity));
						fineChemicalInventory.setShared(false);
						fineChemicalInventoryRepository.save(fineChemicalInventory);
					}
				}
			}
		}
	}
	
	/**
	 * Calculate LocalDateTime from day name and time string
	 * @param day the day name (e.g., "Monday", "Wednesday", "Friday")
	 * @param time the time string in either 24-hour format (e.g., "12:58", "15:00") or 12-hour format (e.g., "10:00 AM", "02:00 PM")
	 * @return LocalDateTime representing the next occurrence of that day at the specified time
	 */
	private LocalDateTime calculateDateTimeFromDayAndTime(String day, String time) {
		try {
			// Get current date and find next occurrence of the specified day
			LocalDate today = LocalDate.now();
			DayOfWeek targetDay = DayOfWeek.valueOf(day.toUpperCase());
			
			// Calculate next occurrence of target day from today
			LocalDate targetDate = today.with(TemporalAdjusters.next(targetDay));
			
			// Parse the time string - try both 24-hour and 12-hour formats
			java.time.LocalTime parsedTime = null;
			String trimmedTime = time.trim();
			
			try {
				// Try 12-hour format with AM/PM first (e.g., "10:00 AM", "02:00 PM")
				if (trimmedTime.toUpperCase().contains("AM") || trimmedTime.toUpperCase().contains("PM")) {
					DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
					parsedTime = java.time.LocalTime.parse(trimmedTime, timeFormatter);
				} else {
					// Try 24-hour format (e.g., "12:58", "15:00")
					DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
					parsedTime = java.time.LocalTime.parse(trimmedTime, timeFormatter);
				}
			} catch (Exception ex) {
				// If both fail, try other common formats
				DateTimeFormatter[] formatters = {
					DateTimeFormatter.ofPattern("H:mm"),
					DateTimeFormatter.ofPattern("HH:mm:ss"),
					DateTimeFormatter.ofPattern("h:mm a"),
					DateTimeFormatter.ofPattern("hh:mm:ss a")
				};
				
				for (DateTimeFormatter formatter : formatters) {
					try {
						parsedTime = java.time.LocalTime.parse(trimmedTime, formatter);
						break;
					} catch (Exception e) {
						// Continue to next format
					}
				}
				
				if (parsedTime == null) {
					throw new DateTimeParseException("Unable to parse time: " + trimmedTime, trimmedTime, 0);
				}
			}
			
			// Combine date and time
			return LocalDateTime.of(targetDate, parsedTime);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid day or time format. Day should be a day name (e.g., Monday, TUESDAY), time can be 24-hour (12:58) or 12-hour (02:00 PM)", e);
		}
	}
	
	/**
	 * Get all time slots for a shared inventory
	 * @param sharedInventoryId the ID of the shared inventory
	 * @return list of time slots
	 */
	public List<TimeSlotDTO> getTimeSlots(Long sharedInventoryId) {
		List<InventoryTimeSlot> timeSlots = inventoryTimeSlotRepository.findBySharedInventory_SharedInventoryId(sharedInventoryId);
		List<TimeSlotDTO> timeSlotsDTO = new ArrayList<>();
		
		for (InventoryTimeSlot slot : timeSlots) {
			TimeSlotDTO slotDTO = new TimeSlotDTO(
				slot.getTimeSlotId(),
				slot.getSlotNumber(),
				slot.getDay(),
				slot.getFromTime(),
				slot.getToTime(),
				slot.getStartTime(),
				slot.getEndTime()
			);
			timeSlotsDTO.add(slotDTO);
		}
		
		return timeSlotsDTO;
	}
	
	/**
	 * Get all requests raised against items shared by the user's group
	 * @param page the page number (1-indexed)
	 * @param size the page size
	 * @param user the user whose group's shared items we want to check
	 * @param filters optional filters (status, etc.)
	 * @return response containing paginated requests with column definitions
	 */
	public SharedItemsRequestListResponse getIncomingRequestsForDonor(int page, int size, User user, Map<String, String> filters) {
		
		// Validate user and group
		if (user == null || user.getGroupName() == null || user.getGroupName().isEmpty()) {
			throw new IllegalArgumentException("User and group name are required");
		}
		
		String userGroupName = user.getGroupName();
		String statusFilter = (filters != null) ? filters.get("status") : null;
		
		// Fetch requests based on whether status filter is provided
		Page<GroupSharingRequest> requestsPage;
		if (statusFilter != null && !statusFilter.isEmpty()) {
			requestsPage = groupSharingRequestRepository.findRequestsForSharedItemsByGroupAndStatus(
				userGroupName, statusFilter, PageRequest.of(page - 1, size)
			);
		} else {
			requestsPage = groupSharingRequestRepository.findRequestsForSharedItemsByGroup(
				userGroupName, PageRequest.of(page - 1, size)
			);
		}
		
		// Build the response list
		List<SharedItemRequestDTO> requestDTOs = new ArrayList<>();
		
		for (GroupSharingRequest gsr : requestsPage.getContent()) {
			// Get product details
			String productName = "Unknown Product";
			Integer availableQuantity = 0;
			
			Long numericProductId = null;
			try {
				numericProductId = Long.parseLong(gsr.getProductId());
			} catch (NumberFormatException e) {
				// productId is not numeric
			}
			
			if (gsr.getInventoryType().equalsIgnoreCase("generalInventory")) {
				if (numericProductId != null) {
					Inventory inventory = inventoryRepository.findByProductId(numericProductId);
					if (inventory != null) {
						productName = inventory.getProductname();
						try {
							availableQuantity = Integer.parseInt(inventory.getQuantity().toString());
						} catch (Exception e) {
							availableQuantity = 0;
						}
					}
				}
			} else if (gsr.getInventoryType().equalsIgnoreCase("fineChemicalInventory")) {
				if (numericProductId != null) {
					FineChemicalInventory fineChemical = fineChemicalInventoryRepository.findByProductId(numericProductId);
					if (fineChemical != null) {
						productName = fineChemical.getProductname();
						try {
							availableQuantity = Integer.parseInt(fineChemical.getQuantity().toString());
						} catch (Exception e) {
							availableQuantity = 0;
						}
					}
				}
			}
			
			// Create selected slot DTO
			SelectedSlotDTO selectedSlot = new SelectedSlotDTO(gsr.getSlotDate(), gsr.getSlotTime());
			
			// Create address DTO
			AddressSimpleDTO address = new AddressSimpleDTO(gsr.getCity(), gsr.getCountry());
			
			// Create request DTO
			SharedItemRequestDTO requestDTO = new SharedItemRequestDTO(
				"req_" + gsr.getRequestId(),
				productName,
				gsr.getQuantity(),
				availableQuantity,
				gsr.getUserName(), // Group name or requester name
				gsr.getStatus(),
				selectedSlot,
				address,
				gsr.getCreatedAt()
			);
			
			// Set rejection reason if the request was rejected
			if (gsr.getRejectionReason() != null) {
				requestDTO.setRejectionReason(gsr.getRejectionReason());
			}
			
			requestDTOs.add(requestDTO);
		}
		
		// Build column definitions
		List<Map<String, String>> columns = new ArrayList<>();
		columns.add(Map.of("label", "Request ID", "key", "requestId"));
		columns.add(Map.of("label", "Product Name", "key", "productName"));
		columns.add(Map.of("label", "Requested Quantity", "key", "requestedQuantity"));
		columns.add(Map.of("label", "Available Quantity", "key", "availableQuantity"));
		columns.add(Map.of("label", "Requested By (Group)", "key", "requesterGroup"));
		columns.add(Map.of("label", "Status", "key", "status"));
		columns.add(Map.of("label", "Selected Slot", "key", "selectedSlot"));
		columns.add(Map.of("label", "Address", "key", "address"));
		columns.add(Map.of("label", "Rejection Reason", "key", "rejectionReason"));
		columns.add(Map.of("label", "Requested On", "key", "createdAt"));
		
		// Build pagination DTO
		PaginationDTO paginationDTO = new PaginationDTO(
			requestsPage.getTotalPages(),
			size,
			page,
			requestsPage.getTotalElements()
		);
		
		// Build data map
		Map<String, Object> data = new HashMap<>();
		data.put("pagination", paginationDTO);
		data.put("columns", columns);
		data.put("list", requestDTOs);
		
		// Build and return response
		SharedItemsRequestListResponse response = new SharedItemsRequestListResponse();
		response.setData(data);
		response.setMessage("Incoming requests fetched successfully");
		response.setStatus("success");
		
		return response;
	}
	
	/**
	 * Get all requests made by the receiver (current user)
	 * @param page the page number (1-indexed)
	 * @param size the page size
	 * @param user the receiver/requester
	 * @param filters optional filters (status, etc.)
	 * @return response containing paginated requests with column definitions
	 */
	public SharedItemsRequestListResponse getMyRequests(int page, int size, User user, Map<String, String> filters) {
		
		// Validate user
		if (user == null || user.getId() <= 0) {
			throw new IllegalArgumentException("User ID is required");
		}
		
		Long userId = user.getId();
		String statusFilter = (filters != null) ? filters.get("status") : null;
		
		// Fetch requests based on whether status filter is provided
		Page<GroupSharingRequest> requestsPage;
		if (statusFilter != null && !statusFilter.isEmpty()) {
			requestsPage = groupSharingRequestRepository.findMyRequestsByStatus(
				userId, statusFilter, PageRequest.of(page - 1, size)
			);
		} else {
			requestsPage = groupSharingRequestRepository.findMyRequests(
				userId, PageRequest.of(page - 1, size)
			);
		}
		
		// Build the response list
		List<ReceiverRequestDTO> requestDTOs = new ArrayList<>();
		
		for (GroupSharingRequest gsr : requestsPage.getContent()) {
			// Get product details
			String productName = "Unknown Product";
			
			Long numericProductId = null;
			try {
				numericProductId = Long.parseLong(gsr.getProductId());
			} catch (NumberFormatException e) {
				// productId is not numeric
			}
			
			if (gsr.getInventoryType().equalsIgnoreCase("generalInventory")) {
				if (numericProductId != null) {
					Inventory inventory = inventoryRepository.findByProductId(numericProductId);
					if (inventory != null) {
						productName = inventory.getProductname();
					}
				}
			} else if (gsr.getInventoryType().equalsIgnoreCase("fineChemicalInventory")) {
				if (numericProductId != null) {
					FineChemicalInventory fineChemical = fineChemicalInventoryRepository.findByProductId(numericProductId);
					if (fineChemical != null) {
						productName = fineChemical.getProductname();
					}
				}
			}
			
			// Get donor group from SharedInventory
			String donorGroup = "Unknown Donor";
			Optional<SharedInventory> sharedInventory = sharedInventoryRepository.findByProductId(gsr.getProductId());
			if (sharedInventory.isPresent()) {
				donorGroup = sharedInventory.get().getSharedByGroupName();
			}
			
			// Create selected slot DTO
			SelectedSlotDTO selectedSlot = new SelectedSlotDTO(gsr.getSlotDate(), gsr.getSlotTime());
			
			// Create address DTO
			AddressSimpleDTO address = new AddressSimpleDTO(gsr.getCity(), gsr.getCountry());
			
			// Create request DTO
			ReceiverRequestDTO requestDTO = new ReceiverRequestDTO(
				"req_" + gsr.getRequestId(),
				productName,
				gsr.getQuantity(),
				donorGroup,
				gsr.getStatus(),
				selectedSlot,
				address,
				gsr.getCreatedAt()
			);
			
			// Set rejection reason if the request was rejected
			if (gsr.getRejectionReason() != null) {
				requestDTO.setRejectionReason(gsr.getRejectionReason());
			}
			
			requestDTOs.add(requestDTO);
		}
		
		// Build column definitions
		List<Map<String, String>> columns = new ArrayList<>();
		columns.add(Map.of("label", "Request ID", "key", "requestId"));
		columns.add(Map.of("label", "Product Name", "key", "productName"));
		columns.add(Map.of("label", "Requested Quantity", "key", "requestedQuantity"));
		columns.add(Map.of("label", "Shared By (Group)", "key", "donorGroup"));
		columns.add(Map.of("label", "Status", "key", "status"));
		columns.add(Map.of("label", "Selected Slot", "key", "selectedSlot"));
		columns.add(Map.of("label", "Address", "key", "address"));
		columns.add(Map.of("label", "Rejection Reason", "key", "rejectionReason"));
		columns.add(Map.of("label", "Requested On", "key", "createdAt"));
		
		// Build pagination DTO
		PaginationDTO paginationDTO = new PaginationDTO(
			requestsPage.getTotalPages(),
			size,
			page,
			requestsPage.getTotalElements()
		);
		
		// Build data map
		Map<String, Object> data = new HashMap<>();
		data.put("pagination", paginationDTO);
		data.put("columns", columns);
		data.put("list", requestDTOs);
		
		// Build and return response
		SharedItemsRequestListResponse response = new SharedItemsRequestListResponse();
		response.setData(data);
		response.setMessage("My requests fetched successfully");
		response.setStatus("success");
		
		return response;
	}
	
	/**
	 * Approve a share request
	 * @param approveDTO the request ID and approver info
	 * @return response with approval details
	 */
	public Map<String, Object> approveShareRequest(ApproveShareRequestDTO approveDTO) {
		
		Map<String, Object> response = new HashMap<>();
		
		// Validate input
		if (approveDTO == null || approveDTO.getRequestId() == null || approveDTO.getRequestId().isEmpty()) {
			response.put("success", false);
			response.put("errorCode", "INVALID_REQUEST_ID");
			response.put("message", "Request ID is required");
			return response;
		}
		
		if (approveDTO.getApprovedBy() == null) {
			response.put("success", false);
			response.put("errorCode", "INVALID_INPUT");
			response.put("message", "Approver information is required");
			return response;
		}
		
		// Extract requestId (remove "req_" prefix if present)
		String requestId = approveDTO.getRequestId();
		if (requestId.startsWith("req_")) {
			requestId = requestId.substring(4);
		}
		
		Long requestIdLong;
		try {
			requestIdLong = Long.parseLong(requestId);
		} catch (NumberFormatException e) {
			response.put("success", false);
			response.put("errorCode", "INVALID_REQUEST_ID");
			response.put("message", "Invalid request ID format");
			return response;
		}
		
		// Find the request
		Optional<GroupSharingRequest> requestOpt = groupSharingRequestRepository.findByRequestId(requestIdLong);
		if (!requestOpt.isPresent()) {
			response.put("success", false);
			response.put("errorCode", "REQUEST_NOT_FOUND");
			response.put("message", "Request not found");
			return response;
		}
		
		GroupSharingRequest gsr = requestOpt.get();
		
		// Check if request status is PENDING
		if (!gsr.getStatus().equalsIgnoreCase("PENDING")) {
			response.put("success", false);
			response.put("errorCode", "INVALID_REQUEST_STATUS");
			response.put("message", "Only pending requests can be approved");
			return response;
		}
		
		// Check if approver's group is the donor (who shared the product)
		Optional<SharedInventory> sharedInventoryOpt = sharedInventoryRepository.findByProductId(gsr.getProductId());
		if (!sharedInventoryOpt.isPresent()) {
			response.put("success", false);
			response.put("errorCode", "PRODUCT_NOT_FOUND");
			response.put("message", "Shared product not found");
			return response;
		}
		
		SharedInventory sharedInventory = sharedInventoryOpt.get();
		String donorGroupName = sharedInventory.getSharedByGroupName();
		String approverGroupName = approveDTO.getApprovedBy().getGroupName();
		
		if (!donorGroupName.equals(approverGroupName)) {
			response.put("success", false);
			response.put("errorCode", "UNAUTHORIZED");
			response.put("message", "Only the donor can approve this request");
			return response;
		}
		
		// Calculate available quantity
		// available = totalSharedQuantity - sum(all APPROVED quantities for this productId)
		Integer totalShared = sharedInventory.getTotalSharedQuantity();
		Integer approvedQuantities = getTotalApprovedQuantityForProduct(gsr.getProductId());
		Integer available = totalShared - approvedQuantities;
		
		// Check if requested quantity is available
		if (gsr.getQuantity() > available) {
			response.put("success", false);
			response.put("errorCode", "INSUFFICIENT_AVAILABLE_QUANTITY");
			response.put("message", "Not enough quantity available to approve this request");
			return response;
		}
		
		// Update request status to APPROVED
		gsr.setStatus("APPROVED");
		groupSharingRequestRepository.save(gsr);
		
		// Update SharedInventory available quantity
		Integer remainingQuantity = available - gsr.getQuantity();
		sharedInventory.setAvailableQuantity(remainingQuantity);
		sharedInventoryRepository.save(sharedInventory);
		
		// Build success response
		ApproveShareResponseDataDTO dataDTO = new ApproveShareResponseDataDTO(
			"req_" + gsr.getRequestId(),
			"APPROVED",
			gsr.getQuantity(),
			remainingQuantity
		);
		
		response.put("success", true);
		response.put("message", "Request approved successfully");
		response.put("data", dataDTO);
		
		return response;
	}
	
	/**
	 * Helper method to get total approved quantity for a product
	 * @param productId the product ID
	 * @return total quantity of all approved requests for this product
	 */
	private Integer getTotalApprovedQuantityForProduct(String productId) {
		List<GroupSharingRequest> approvedRequests = groupSharingRequestRepository.findByProductIdAndStatus(productId, "APPROVED");
		if (approvedRequests == null || approvedRequests.isEmpty()) {
			return 0;
		}
		
		return approvedRequests.stream()
			.mapToInt(gsr -> gsr.getQuantity() != null ? gsr.getQuantity() : 0)
			.sum();
	}
	
	/**
	 * Reject a share request
	 * @param rejectDTO the request ID, rejector info, and reason
	 * @return response with rejection details
	 */
	public Map<String, Object> rejectShareRequest(RejectShareRequestDTO rejectDTO) {
		
		Map<String, Object> response = new HashMap<>();
		
		// Validate input
		if (rejectDTO == null || rejectDTO.getRequestId() == null || rejectDTO.getRequestId().isEmpty()) {
			response.put("success", false);
			response.put("errorCode", "INVALID_REQUEST_ID");
			response.put("message", "Request ID is required");
			return response;
		}
		
		if (rejectDTO.getRejectedBy() == null) {
			response.put("success", false);
			response.put("errorCode", "INVALID_INPUT");
			response.put("message", "Rejector information is required");
			return response;
		}
		
		// Extract requestId (remove "req_" prefix if present)
		String requestId = rejectDTO.getRequestId();
		if (requestId.startsWith("req_")) {
			requestId = requestId.substring(4);
		}
		
		Long requestIdLong;
		try {
			requestIdLong = Long.parseLong(requestId);
		} catch (NumberFormatException e) {
			response.put("success", false);
			response.put("errorCode", "INVALID_REQUEST_ID");
			response.put("message", "Invalid request ID format");
			return response;
		}
		
		// Find the request
		Optional<GroupSharingRequest> requestOpt = groupSharingRequestRepository.findByRequestId(requestIdLong);
		if (!requestOpt.isPresent()) {
			response.put("success", false);
			response.put("errorCode", "REQUEST_NOT_FOUND");
			response.put("message", "Request not found");
			return response;
		}
		
		GroupSharingRequest gsr = requestOpt.get();
		
		// Check if request status is PENDING
		if (!gsr.getStatus().equalsIgnoreCase("PENDING")) {
			response.put("success", false);
			response.put("errorCode", "INVALID_REQUEST_STATUS");
			response.put("message", "Only pending requests can be rejected");
			return response;
		}
		
		// Check if rejector's group is the donor (who shared the product)
		Optional<SharedInventory> sharedInventoryOpt = sharedInventoryRepository.findByProductId(gsr.getProductId());
		if (!sharedInventoryOpt.isPresent()) {
			response.put("success", false);
			response.put("errorCode", "PRODUCT_NOT_FOUND");
			response.put("message", "Shared product not found");
			return response;
		}
		
		SharedInventory sharedInventory = sharedInventoryOpt.get();
		String donorGroupName = sharedInventory.getSharedByGroupName();
		String rejectorGroupName = rejectDTO.getRejectedBy().getGroupName();
		
		if (!donorGroupName.equals(rejectorGroupName)) {
			response.put("success", false);
			response.put("errorCode", "UNAUTHORIZED");
			response.put("message", "Only the donor can reject this request");
			return response;
		}
		
		// Update request status to REJECTED
		gsr.setStatus("REJECTED");
		
		// Persist the rejection reason
		if (rejectDTO.getReason() != null && !rejectDTO.getReason().isEmpty()) {
			gsr.setRejectionReason(rejectDTO.getReason());
		}
		
		groupSharingRequestRepository.save(gsr);
		
		// Add the rejected quantity back to SharedInventory
		Integer rejectedQuantity = gsr.getQuantity();
		Integer currentAvailableQuantity = sharedInventory.getAvailableQuantity() != null ? 
			Integer.parseInt(sharedInventory.getAvailableQuantity().toString()) : 0;
		Integer newAvailableQuantity = currentAvailableQuantity + rejectedQuantity;
		
		sharedInventory.setAvailableQuantity(newAvailableQuantity);
		sharedInventoryRepository.save(sharedInventory);
		
		// Build success response
		RejectShareResponseDataDTO dataDTO = new RejectShareResponseDataDTO(
			"req_" + gsr.getRequestId(),
			"REJECTED"
		); 
		
		response.put("success", true);
		response.put("message", "Request rejected successfully");
		response.put("data", dataDTO);
		
		return response;
	}
	
	/**
	 * Mark a share request as received by the requester
	 * @param requestId the request ID
	 * @param markReceivedDTO the received details (receivedBy, receivedAt, notes)
	 * @return response with completion details
	 */
	public Map<String, Object> markAsReceived(String requestId, MarkAsReceivedRequest markReceivedDTO) {
		
		Map<String, Object> response = new HashMap<>();
		
		// Validate input
		if (requestId == null || requestId.isEmpty()) {
			response.put("success", false);
			response.put("errorCode", "INVALID_REQUEST_ID");
			response.put("message", "Request ID is required");
			return response;
		}
		
		if (markReceivedDTO == null) {
			response.put("success", false);
			response.put("errorCode", "INVALID_INPUT");
			response.put("message", "Mark as received details are required");
			return response;
		}
		
		if (markReceivedDTO.getReceivedBy() == null) {
			response.put("success", false);
			response.put("errorCode", "INVALID_INPUT");
			response.put("message", "Receiver information is required");
			return response;
		}
		
		if (markReceivedDTO.getReceivedAt() == null || markReceivedDTO.getReceivedAt().isEmpty()) {
			response.put("success", false);
			response.put("errorCode", "INVALID_INPUT");
			response.put("message", "Received timestamp is required");
			return response;
		}
		
		// Extract requestId (remove "req_" prefix if present)
		String parsedRequestId = requestId;
		if (parsedRequestId.startsWith("req_")) {
			parsedRequestId = parsedRequestId.substring(4);
		}
		
		Long requestIdLong;
		try {
			requestIdLong = Long.parseLong(parsedRequestId);
		} catch (NumberFormatException e) {
			response.put("success", false);
			response.put("errorCode", "INVALID_REQUEST_ID");
			response.put("message", "Invalid request ID format");
			return response;
		}
		
		// Find the request
		Optional<GroupSharingRequest> requestOpt = groupSharingRequestRepository.findByRequestId(requestIdLong);
		if (!requestOpt.isPresent()) {
			response.put("success", false);
			response.put("errorCode", "REQUEST_NOT_FOUND");
			response.put("message", "Request not found");
			return response;
		}
		
		GroupSharingRequest gsr = requestOpt.get();
		
		// Check if request status is APPROVED (only APPROVED → COMPLETED is allowed)
		if (!gsr.getStatus().equalsIgnoreCase("APPROVED")) {
			if (gsr.getStatus().equalsIgnoreCase("COMPLETED")) {
				response.put("success", false);
				response.put("errorCode", "ALREADY_COMPLETED");
				response.put("message", "This request is already completed");
			} else if (gsr.getStatus().equalsIgnoreCase("PENDING")) {
				response.put("success", false);
				response.put("errorCode", "INVALID_REQUEST_STATUS");
				response.put("message", "Only approved requests can be marked as received");
			} else if (gsr.getStatus().equalsIgnoreCase("REJECTED")) {
				response.put("success", false);
				response.put("errorCode", "INVALID_REQUEST_STATUS");
				response.put("message", "Rejected requests cannot be marked as received");
			} else {
				response.put("success", false);
				response.put("errorCode", "INVALID_REQUEST_STATUS");
				response.put("message", "Only approved requests can be marked as received");
			}
			return response;
		}
		
		// Authorization check: Only the requester (receiver) can mark as received
		User receivedBy = markReceivedDTO.getReceivedBy();
		Long receivedByUserId;
		try {
			receivedByUserId = Long.parseLong(String.valueOf(receivedBy.getId()));
		} catch (NumberFormatException e) {
			response.put("success", false);
			response.put("errorCode", "INVALID_USER_ID");
			response.put("message", "Invalid user ID format");
			return response;
		}
		
		if (!receivedByUserId.equals(gsr.getUserId())) {
			response.put("success", false);
			response.put("errorCode", "UNAUTHORIZED");
			response.put("message", "Only the requester can mark this as received");
			return response;
		}
		
		// Also verify the receiver belongs to the requester group (optional but recommended)
		Optional<SharedInventory> sharedInventoryOpt = sharedInventoryRepository.findByProductId(gsr.getProductId());
		if (sharedInventoryOpt.isPresent()) {
			SharedInventory sharedInventory = sharedInventoryOpt.get();
			String donorGroupName = sharedInventory.getSharedByGroupName();
			
			// Verify requester is NOT from the donor group
			if (receivedBy.getGroupName() != null && receivedBy.getGroupName().equals(donorGroupName)) {
				response.put("success", false);
				response.put("errorCode", "UNAUTHORIZED");
				response.put("message", "Only the requester can mark this as received");
				return response;
			}
		}
		
		// Parse receivedAt timestamp - handle ISO 8601 format
		LocalDateTime receivedAtTime;
		try {
			String timestampStr = markReceivedDTO.getReceivedAt().trim();
			// Handle ISO 8601 formats with timezone information
			if (timestampStr.endsWith("Z") || timestampStr.contains("+") || timestampStr.contains("-") && timestampStr.lastIndexOf("-") > 10) {
				// Parse as OffsetDateTime and convert to LocalDateTime
				java.time.OffsetDateTime offsetDateTime = java.time.OffsetDateTime.parse(timestampStr);
				receivedAtTime = offsetDateTime.toLocalDateTime();
			} else {
				// Parse as LocalDateTime directly
				receivedAtTime = LocalDateTime.parse(timestampStr);
			}
		} catch (Exception e) {
			response.put("success", false);
			response.put("errorCode", "INVALID_TIMESTAMP");
			response.put("message", "Invalid timestamp format. Expected ISO 8601 format (e.g., 2026-03-20T10:30:00Z or 2026-03-20T10:30:00+00:00)");
			return response;
		}
		
		// Optional: Validate receivedAt is not in the future
		LocalDateTime now = LocalDateTime.now();
		if (receivedAtTime.isAfter(now)) {
			response.put("success", false);
			response.put("errorCode", "INVALID_RECEIVED_TIME");
			response.put("message", "Received time cannot be in the future");
			return response;
		}
		
		// Update request status to COMPLETED
		gsr.setStatus("COMPLETED");
		gsr.setReceivedAt(receivedAtTime);
		gsr.setReceivedNotes(markReceivedDTO.getNotes());
		groupSharingRequestRepository.save(gsr);
		
		// Build success response
		Map<String, Object> data = new HashMap<>();
		data.put("requestId", "req_" + gsr.getRequestId());
		data.put("status", "COMPLETED");
		data.put("receivedAt", markReceivedDTO.getReceivedAt());
		
		response.put("success", true);
		response.put("message", "Item marked as received");
		response.put("data", data);
		
		return response;
	}
	
	/**
	 * Unshare an item and revert it back to inventory
	 * @param unshareDTO the unshare request details
	 * @return response with unshare details
	 */
	public Map<String, Object> unshareItem(UnshareItemRequest unshareDTO) {
		
		Map<String, Object> response = new HashMap<>();
		
		// Validate input
		if (unshareDTO == null) {
			response.put("success", false);
			response.put("errorCode", "INVALID_INPUT");
			response.put("message", "Unshare request details are required");
			return response;
		}
		
		if (unshareDTO.getProductId() == null || unshareDTO.getProductId() <= 0) {
			response.put("success", false);
			response.put("errorCode", "INVALID_PRODUCT_ID");
			response.put("message", "Valid product ID is required");
			return response;
		}
		
		if (unshareDTO.getInventoryType() == null || unshareDTO.getInventoryType().isEmpty()) {
			response.put("success", false);
			response.put("errorCode", "INVALID_INVENTORY_TYPE");
			response.put("message", "Inventory type is required");
			return response;
		}
		
		if (unshareDTO.getUser() == null) {
			response.put("success", false);
			response.put("errorCode", "INVALID_INPUT");
			response.put("message", "User information is required");
			return response;
		}
		
		if (unshareDTO.getReason() == null || unshareDTO.getReason().isEmpty()) {
			response.put("success", false);
			response.put("errorCode", "INVALID_REASON");
			response.put("message", "Reason for unsharing is required");
			return response;
		}
		
		// Check if item exists in SharedInventory
		String productIdStr = unshareDTO.getProductId().toString();
		Optional<SharedInventory> sharedInventoryOpt = sharedInventoryRepository.findByProductIdAndInventoryType(
			productIdStr, 
			unshareDTO.getInventoryType()
		);
		
		if (!sharedInventoryOpt.isPresent()) {
			response.put("success", false);
			response.put("errorCode", "PRODUCT_NOT_FOUND");
			response.put("message", "Product not found in shared inventory");
			return response;
		}
		
		SharedInventory sharedInventory = sharedInventoryOpt.get();
		
		// Ownership validation: Only the donor can unshare
		String donorGroupName = sharedInventory.getSharedByGroupName();
		String requesterGroupName = unshareDTO.getUser().getGroupName();
		
		if (!donorGroupName.equals(requesterGroupName)) {
			response.put("success", false);
			response.put("errorCode", "UNAUTHORIZED");
			response.put("message", "Only the owner can unshare this item");
			return response;
		}
		
		// Check for existing requests with APPROVED or COMPLETED status
		List<GroupSharingRequest> allRequests = groupSharingRequestRepository.findByProductId(productIdStr);
		List<GroupSharingRequest> activeRequests = new ArrayList<>();
		List<GroupSharingRequest> pendingRequests = new ArrayList<>();
		
		if (allRequests != null) {
			for (GroupSharingRequest gsr : allRequests) {
				if (gsr.getStatus().equalsIgnoreCase("APPROVED") || gsr.getStatus().equalsIgnoreCase("COMPLETED")) {
					activeRequests.add(gsr);
				} else if (gsr.getStatus().equalsIgnoreCase("PENDING")) {
					pendingRequests.add(gsr);
				}
			}
		}
		
		// Case C: Block if APPROVED or COMPLETED requests exist
		if (!activeRequests.isEmpty()) {
			response.put("success", false);
			response.put("errorCode", "ACTIVE_REQUESTS_EXIST");
			response.put("message", "Cannot unshare. Some requests are already approved or completed");
			return response;
		}
		
		// Case B: Auto-cancel PENDING requests
		int cancelledCount = 0;
		if (!pendingRequests.isEmpty()) {
			for (GroupSharingRequest pendingRequest : pendingRequests) {
				pendingRequest.setStatus("CANCELLED");
				groupSharingRequestRepository.save(pendingRequest);
				cancelledCount++;
			}
		}
		
		// Case A: Unshare the item - delete from SharedInventory
		// Update the inventory table (Inventory or FineChemicalInventory) to set shared = false
		if (unshareDTO.getInventoryType().equalsIgnoreCase("generalInventory")) {
			Inventory inventory = inventoryRepository.findByProductId(unshareDTO.getProductId());
			if (inventory != null) {
				inventory.setShared(false);
				inventory.setQuantity(sharedInventory.getAvailableQuantity()); // Add back the available quantity
				inventoryRepository.save(inventory);
			}
		} else if (unshareDTO.getInventoryType().equalsIgnoreCase("fineChemicalInventory")) {
			FineChemicalInventory fineChemicalInventory = fineChemicalInventoryRepository.findByProductId(unshareDTO.getProductId());
			if (fineChemicalInventory != null) {
				fineChemicalInventory.setShared(false);
				fineChemicalInventory.setQuantity(sharedInventory.getAvailableQuantity()+""); // Add back the available quantity
				fineChemicalInventoryRepository.save(fineChemicalInventory);
			}
		}
		
		// Delete the SharedInventory entry (cascade will handle time slots)
		sharedInventoryRepository.delete(sharedInventory);
		
		// Build response
		response.put("success", true);
		Map<String, Object> data = new HashMap<>();
		data.put("productId", unshareDTO.getProductId());
		data.put("status", "UNSHARED");
		
		if (cancelledCount > 0) {
			response.put("message", "Item unshared and pending requests cancelled");
			data.put("cancelledRequests", cancelledCount);
		} else {
			response.put("message", "Item moved back to inventory successfully");
		}
		
		response.put("data", data);
		
		return response;
	}
	
	/**
	 * Get borrowed inventory for a user/group
	 * Only includes COMPLETED requests where requesterGroup = user.groupName
	 * @param page the page number (1-indexed)
	 * @param size the page size
	 * @param borrowedDTO the request containing user and filters
	 * @return response containing paginated borrowed items with column definitions
	 */
	public SharedItemsRequestListResponse getBorrowedInventory(int page, int size, BorrowedInventoryRequest borrowedDTO) {
		
		// Validate input
		if (borrowedDTO == null || borrowedDTO.getUser() == null) {
			throw new IllegalArgumentException("User information is required");
		}
		
		User user = borrowedDTO.getUser();
		if (user.getGroupName() == null || user.getGroupName().isEmpty()) {
			throw new IllegalArgumentException("User group name is required");
		}
		
		// Fetch completed requests where requesterGroup = user.groupName
		Page<GroupSharingRequest> borrowedItemsPage = groupSharingRequestRepository.findBorrowedItemsByRequesterGroupAndStatus(
			user.getGroupName(),
			"COMPLETED",
			PageRequest.of(page - 1, size)
		);
		
		// Build the response list
		List<BorrowedItemDTO> borrowedItems = new ArrayList<>();
		
		for (GroupSharingRequest gsr : borrowedItemsPage.getContent()) {
			// Get product details
			String productName = "Unknown Product";
			Long productId = null;
			
			try {
				productId = Long.parseLong(gsr.getProductId());
			} catch (NumberFormatException e) {
				// productId is not numeric
			}
			
			if (gsr.getInventoryType().equalsIgnoreCase("generalInventory")) {
				if (productId != null) {
					Inventory inventory = inventoryRepository.findByProductId(productId);
					if (inventory != null) {
						productName = inventory.getProductname();
					}
				}
			} else if (gsr.getInventoryType().equalsIgnoreCase("fineChemicalInventory")) {
				if (productId != null) {
					FineChemicalInventory fineChemical = fineChemicalInventoryRepository.findByProductId(productId);
					if (fineChemical != null) {
						productName = fineChemical.getProductname();
					}
				}
			}
			
			// Get donor group from SharedInventory
			String donorGroup = "Unknown Donor";
			Optional<SharedInventory> sharedInventory = sharedInventoryRepository.findByProductId(gsr.getProductId());
			if (sharedInventory.isPresent()) {
				donorGroup = sharedInventory.get().getSharedByGroupName();
			}
			
			// Create selected slot DTO
			SelectedSlotDTO selectedSlot = new SelectedSlotDTO(gsr.getSlotDate(), gsr.getSlotTime());
			
			// Create address DTO
			AddressSimpleDTO address = new AddressSimpleDTO(gsr.getCity(), gsr.getCountry());
			
			// Format receivedAt timestamp
			String receivedAtStr = "N/A";
			if (gsr.getReceivedAt() != null) {
				receivedAtStr = gsr.getReceivedAt().toString() + "Z";
			}
			
			// Create borrowed item DTO
			BorrowedItemDTO borrowedItem = new BorrowedItemDTO(
				"req_" + gsr.getRequestId(),
				productId,
				productName,
				gsr.getQuantity(),
				donorGroup,
				gsr.getStatus(),
				receivedAtStr,
				selectedSlot,
				address
			);
			
			borrowedItems.add(borrowedItem);
		}
		
		// Apply optional filters if provided
		if (borrowedDTO.getFilters() != null && !borrowedDTO.getFilters().isEmpty()) {
			borrowedItems = applyBorrowedItemFilters(borrowedItems, borrowedDTO.getFilters());
		}
		
		// Apply pagination on filtered results
		int totalRecords = borrowedItems.size();
		int totalPages = (int) Math.ceil((double) totalRecords / size);
		int startIndex = (page - 1) * size;
		int endIndex = Math.min(startIndex + size, totalRecords);
		
		List<BorrowedItemDTO> paginatedList = new ArrayList<>();
		if (startIndex < totalRecords) {
			paginatedList = borrowedItems.subList(startIndex, endIndex);
		}
		
		// Build column definitions
		List<Map<String, String>> columns = new ArrayList<>();
		columns.add(Map.of("label", "Request ID", "key", "requestId"));
		columns.add(Map.of("label", "Product Name", "key", "productName"));
		columns.add(Map.of("label", "Borrowed Quantity", "key", "borrowedQuantity"));
		columns.add(Map.of("label", "Donor Group", "key", "donorGroup"));
		columns.add(Map.of("label", "Received Date", "key", "receivedAt"));
		columns.add(Map.of("label", "Exchange Address", "key", "address"));
		columns.add(Map.of("label", "Time Slot", "key", "selectedSlot"));
		columns.add(Map.of("label", "Status", "key", "status"));
		
		// Build pagination DTO
		PaginationDTO paginationDTO = new PaginationDTO(
			totalPages,
			size,
			page,
			totalRecords
		);
		
		// Build data map
		Map<String, Object> data = new HashMap<>();
		data.put("pagination", paginationDTO);
		data.put("columns", columns);
		data.put("list", paginatedList);
		
		// Build and return response
		SharedItemsRequestListResponse response = new SharedItemsRequestListResponse();
		response.setData(data);
		response.setMessage("Borrowed inventory fetched successfully");
		response.setStatus("success");
		
		return response;
	}
	
	/**
	 * Apply optional filters to borrowed items
	 * @param items the list of borrowed items
	 * @param filters map containing: search, donorGroup, fromDate, toDate, itemName
	 * @return filtered list
	 */
	private List<BorrowedItemDTO> applyBorrowedItemFilters(List<BorrowedItemDTO> items, Map<String, String> filters) {
		List<BorrowedItemDTO> filtered = new ArrayList<>(items);
		
		// Apply search filter (general keyword search)
		String search = filters.get("search");
		if (search != null && !search.isEmpty()) {
			String searchLower = search.toLowerCase();
			filtered.removeIf(item ->
				!item.getProductName().toLowerCase().contains(searchLower) &&
				!item.getRequestId().toLowerCase().contains(searchLower)
			);
		}
		
		// Apply donor group filter
		String donorGroup = filters.get("donorGroup");
		if (donorGroup != null && !donorGroup.isEmpty()) {
			filtered.removeIf(item -> !item.getDonorGroup().equalsIgnoreCase(donorGroup));
		}
		
		// Apply item name filter
		String itemName = filters.get("itemName");
		if (itemName != null && !itemName.isEmpty()) {
			filtered.removeIf(item -> !item.getProductName().toLowerCase().contains(itemName.toLowerCase()));
		}
		
		// Apply date range filters
		String fromDate = filters.get("fromDate");
		String toDate = filters.get("toDate");
		if ((fromDate != null && !fromDate.isEmpty()) || (toDate != null && !toDate.isEmpty())) {
			filtered.removeIf(item -> {
				String receivedAt = item.getReceivedAt();
				if (receivedAt.equals("N/A")) {
					return true;
				}
				
				if (fromDate != null && !fromDate.isEmpty() && receivedAt.compareTo(fromDate) < 0) {
					return true;
				}
				
				if (toDate != null && !toDate.isEmpty() && receivedAt.compareTo(toDate) > 0) {
					return true;
				}
				
				return false;
			});
		}
		
		return filtered;
	}
	
}

