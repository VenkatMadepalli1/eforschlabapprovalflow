package com.eforsch.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eforsch.dto.NotificationVO;
import com.eforsch.entity.FineChemicalInventory;
import com.eforsch.entity.Inventory;
import com.eforsch.entity.NotificationEntity;
import com.eforsch.entity.Order;
import com.eforsch.repository.FineChemicalInventoryRepository;
import com.eforsch.repository.InventoryRepository;
import com.eforsch.repository.NotificationRepository;
import com.eforsch.repository.OrderRepository;
import com.eforsch.util.OrderConverter;
import com.eforsch.util.OrderVO;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private FineChemicalInventoryRepository fineChemicalInventoryRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private NotificationRepository notificationRepository;


    public Map<String, Object> getOrdersList(int page, int size) {
        Page<Order> paginatedResult = orderRepository.findAll(PageRequest.of(page - 1, size));
        
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : paginatedResult.getContent()) {
            OrderVO orderVO = OrderConverter.fromEntityToVO(order);
            orderVOList.add(orderVO);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("list", orderVOList);
        response.put("pagination", Map.of(
                "currentPage", paginatedResult.getNumber() + 1,
                "pageSize", paginatedResult.getSize(),
                "totalRecords", paginatedResult.getTotalElements(),
                "totalPages", paginatedResult.getTotalPages()
        ));

        return response;
    }
    
    public Map<String, Object> getOrdersListByGroupName(int page, int size, String groupName, String role) {
    	
    	Page<Order> paginatedResult = null;
		if (role.equals("podept")) {
			// Both approvals must be done before PO dept acts
			paginatedResult = orderRepository.findByAdminApproved(true, PageRequest.of(page - 1, size));
		}else if(role.equalsIgnoreCase("labMgmt")) {
			// Lab sees ALL orders: pending ones to approve, approved/rejected ones for status,
			// and ordered ones so the Delivered button is available
			paginatedResult = orderRepository.findAll(PageRequest.of(page - 1, size));
		}else if(role.equalsIgnoreCase("admin")) {
			paginatedResult = orderRepository.findAll(PageRequest.of(page - 1, size));
		}else if(groupName != null && !groupName.isEmpty()) {
			// Scientists and group leaders get all of their group's orders;
			// the frontend hides not-yet-lab-approved orders from the group leader
			paginatedResult = orderRepository.findByGroupName(groupName, PageRequest.of(page - 1, size));
		}else {
			paginatedResult = orderRepository.findAll(PageRequest.of(page - 1, size));
		}
		
		List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : paginatedResult.getContent()) {
            OrderVO orderVO = OrderConverter.fromEntityToVO(order);
            orderVOList.add(orderVO);
        }
  
		Map<String, Object> response = new HashMap<>();
        response.put("list", orderVOList);
        response.put("pagination", Map.of(
                "currentPage", paginatedResult.getNumber() + 1,
                "pageSize", paginatedResult.getSize(),
                "totalRecords", paginatedResult.getTotalElements(),
                "totalPages", paginatedResult.getTotalPages()
        ));

        return response;
    }
    
    // write a method same as getOrdersListByStatus for status is ordered
	public Map<String, Object> getOrdersListByStatus(int page, int size, String status) {
		Page<Order> paginatedResult = orderRepository.findByStatus(status, PageRequest.of(page - 1, size));

		 List<OrderVO> orderVOList = new ArrayList<>();
	        for (Order order : paginatedResult.getContent()) {
	            OrderVO orderVO = OrderConverter.fromEntityToVO(order);
	            orderVOList.add(orderVO);
	        }
		
		
		Map<String, Object> response = new HashMap<>();
		response.put("list", orderVOList);
		response.put("pagination",
				Map.of("currentPage", paginatedResult.getNumber() + 1, "pageSize", paginatedResult.getSize(),
						"totalRecords", paginatedResult.getTotalElements(), "totalPages",
						paginatedResult.getTotalPages()));

		return response;
	}
    
    
    public Map<String, Object> getOrderList(int page, int size) {
        Page<Order> paginatedResult = orderRepository.findAll(PageRequest.of(page - 1, size));

        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : paginatedResult.getContent()) {
            OrderVO orderVO = OrderConverter.fromEntityToVO(order);
            orderVOList.add(orderVO);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("list", orderVOList);
        response.put("pagination", Map.of(
                "currentPage", paginatedResult.getNumber() + 1,
                "pageSize", paginatedResult.getSize(),
                "totalRecords", paginatedResult.getTotalElements(),
                "totalPages", paginatedResult.getTotalPages()
        ));

        return response;
    }
    
 // Get order by ID
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    // Orders created outside the normal add-order flow (e.g. re-ordered from existing
    // inventory) may have no notification row; create one instead of failing the approval
    private NotificationEntity getOrCreateNotification(Long orderId, Order order) {
        return notificationRepository.findByEntityId(orderId).orElseGet(() -> {
            NotificationEntity n = new NotificationEntity();
            n.setEntityId(orderId);
            n.setEntityType("Order");
            n.setGroupName(order.getGroupName());
            n.setCreatedAt(System.currentTimeMillis());
            return n;
        });
    }

    public OrderVO addOrder(OrderVO orderVO) {
        Order newOrder = OrderConverter.fromVOToEntity(orderVO, new Order());
        newOrder = orderRepository.save(newOrder);
        
     // Create a notification after successfully creating the order
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle("New Order - Pending Lab Approval");
        notificationVO.setMessage("Order #"+newOrder.getOrderId() + " requires your approval.");
        notificationVO.setType("approval_pending");
        notificationVO.setEntityId(newOrder.getOrderId());
        notificationVO.setEntityType("Order");
        notificationVO.setRole("labMgmt"); // First approver in the workflow
        notificationVO.setGroupName(orderVO.getGroupName()); // Set the appropriate group name
        notificationVO.setCreatedAt(System.currentTimeMillis());
        notificationVO.setRead(false);
        notificationService.createNotification(notificationVO);
        
        return OrderConverter.fromEntityToVO(newOrder);
    }
    
    public OrderVO approveAdmin(Long orderId) {
    	Optional<Order> orderOptional = orderRepository.findById(orderId);

		if (orderOptional.isPresent()) {
			Order existingOrder = orderOptional.get();
			existingOrder.setAdminApproved(true);
			existingOrder.setAdminApprovalStatusDate(new java.util.Date());
			existingOrder = orderRepository.save(existingOrder);

			NotificationEntity notificationEntity = getOrCreateNotification(orderId, existingOrder);
			notificationEntity.setMessage("Order #" + orderId + " has been approved by Group Leader. Ready for purchase processing.");
			notificationEntity.setTitle("Order Approved - Ready for PO");
			notificationEntity.setType("approval_pending");
			notificationEntity.setRole("podept");
			notificationEntity.setRead(false);
			notificationEntity.setUpdatedAt(new java.util.Date());
			notificationRepository.save(notificationEntity);
			return OrderConverter.fromEntityToVO(existingOrder);
		} else {
			throw new RuntimeException("Order not found");
		}
    }
    
    public OrderVO rejectAdmin(Long orderId) {
    	Optional<Order> orderOptional = orderRepository.findById(orderId);

		if (orderOptional.isPresent()) {
			Order existingOrder = orderOptional.get();
			existingOrder.setAdminApproved(false);
			existingOrder.setStatus("rejected");
			existingOrder.setAdminApprovalStatusDate(new java.util.Date());
			existingOrder = orderRepository.save(existingOrder);

			NotificationEntity notificationEntity = getOrCreateNotification(orderId, existingOrder);
			notificationEntity.setMessage("Order #" + orderId + " has been rejected by Group Leader.");
			notificationEntity.setTitle("Order Rejected by Group Leader");
			notificationEntity.setType("rejected");
			notificationEntity.setRole("labMgmt");
			notificationEntity.setRead(false);
			notificationEntity.setUpdatedAt(new java.util.Date());
			notificationRepository.save(notificationEntity);
			return OrderConverter.fromEntityToVO(existingOrder);
		} else {
			throw new RuntimeException("Order not found");
		}
    }
    
    public OrderVO labApprove(Long orderId) {
    	Optional<Order> orderOptional = orderRepository.findById(orderId);

		if (orderOptional.isPresent()) {
			Order existingOrder = orderOptional.get();
			existingOrder.setLabApproved(true);
			existingOrder.setLabApprovalStatusDate(new java.util.Date());
			existingOrder = orderRepository.save(existingOrder);

			NotificationEntity notificationEntity = getOrCreateNotification(orderId, existingOrder);
			notificationEntity.setMessage("Order #" + orderId + " has been approved by Lab Management. Now requires Group Leader approval.");
			notificationEntity.setTitle("Pending Group Leader Approval");
			notificationEntity.setType("approved");
			notificationEntity.setRole("groupleader");
			notificationEntity.setRead(false);
			notificationEntity.setUpdatedAt(new java.util.Date());
			notificationRepository.save(notificationEntity);
			return OrderConverter.fromEntityToVO(existingOrder);
		} else {
			throw new RuntimeException("Order not found");
		}
    }
    
    public OrderVO labReject(Long orderId, String rejectReason) {
    	Optional<Order> orderOptional = orderRepository.findById(orderId);

		if (orderOptional.isPresent()) {
			Order existingOrder = orderOptional.get();
			existingOrder.setLabApproved(false);
			existingOrder.setStatus("rejected");
			existingOrder.setLabApprovalStatusDate(new java.util.Date());
			if (rejectReason != null && !rejectReason.isEmpty()) {
				existingOrder.setRejectReason(rejectReason);
			}
			existingOrder = orderRepository.save(existingOrder);

			NotificationEntity notificationEntity = getOrCreateNotification(orderId, existingOrder);
			notificationEntity.setMessage("Order #" + orderId + " has been rejected by Lab Management.");
			notificationEntity.setTitle("Lab Rejected");
			notificationEntity.setType("rejected");
			notificationEntity.setRole("groupleader");
			notificationEntity.setRead(false);
			notificationEntity.setUpdatedAt(new java.util.Date());
			notificationRepository.save(notificationEntity);
			return OrderConverter.fromEntityToVO(existingOrder);
		} else {
			throw new RuntimeException("Order not found");
		}
    }
    
    

    public OrderVO modifyOrder(OrderVO updatedOrderVO) {
        Optional<Order> orderOptional = orderRepository.findById(updatedOrderVO.getOrderId());

        if (orderOptional.isPresent()) {
            Order existingOrder = orderOptional.get();
            
            Order updatedOrder = OrderConverter.fromVOToEntity(updatedOrderVO, new Order());
            
            // Retain createdAt from the existing order
            updatedOrder.setCreatedAt(existingOrder.getCreatedAt());

            // Save updated entity
            updatedOrder = orderRepository.save(updatedOrder);
            
            return OrderConverter.fromEntityToVO(updatedOrder);
        } else {
            throw new RuntimeException("Order not found");
        }
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    // Called when an order is marked as delivered.
    // Creates a general inventory or fine chemical inventory item from the order data.
    // Skips creation if an item already exists for this order (duplicate prevention).
    public void createInventoryFromOrder(Order order) {
        Long orderId = order.getOrderId();
        String inventoryType = order.getInventoryType();

        if ("fineChemicalInventory".equalsIgnoreCase(inventoryType)) {
            FineChemicalInventory item = new FineChemicalInventory();
            item.setSourceOrderId(orderId);
            item.setProductname(order.getProductName());
            item.setCatalogue(order.getCatalogue());
            item.setCompanyname(order.getCompanyName());
            item.setQuantity(order.getQuantity() != null ? order.getQuantity().toString() : null);
            item.setExpiryDate(order.getExpiryDate());
            item.setCompanyInternalNo(order.getCompanyInternalNo());
            item.setSapMaterialNo(order.getSapMaterialNo());
            item.setWvsubqty(order.getWeightVolSubQty());
            item.setBudgetno(order.getBudgetno());
            item.setOrderdate(order.getOrderDate());
            item.setOrderedby(order.getOrderedBy());
            item.setConcentration(order.getConcentration());
            item.setPrice(order.getPrice() != null ? order.getPrice() : 0.0);
            item.setRemarks(order.getRemarks());
            item.setCasnumber(order.getCasNumber());
            item.setHazardousSubstance(order.getHazardousSubstance() != null ? Boolean.parseBoolean(order.getHazardousSubstance()) : null);
            item.setCmrSubstance(order.getCmrSubstance() != null ? Boolean.parseBoolean(order.getCmrSubstance()) : null);
            item.setSkinResorptive(order.getSkinResorptive() != null ? Boolean.parseBoolean(order.getSkinResorptive()) : null);
            try {
                ObjectMapper om = new ObjectMapper();
                item.setGhsSymbols(order.getGhsSymbols() != null ? om.writeValueAsString(order.getGhsSymbols()) : null);
                item.setGhsSignalWord(order.getGhsSignalWord() != null ? om.writeValueAsString(order.getGhsSignalWord()) : null);
            } catch (Exception e) {
                item.setGhsSymbols(null);
                item.setGhsSignalWord(null);
            }
            item.sethPhrases(order.gethPhrases());
            item.setpPhrases(order.getpPhrases());
            item.setSubstitutionCheck(order.getSubstitutionCheck());
            item.setSubstitutionOption(order.getSubstitutionOption());
            item.setStorageLocation(order.getStorageLocation());
            item.setApplicationOfHazardousSubstance(order.getApplicationOfHazardousSubstance());
            item.setConcentrationWorkingVolume(order.getConcentrationWorkingVolume());
            item.setLabNoWorkingWithChemical(order.getLabNoWorkingWithChemical());
            item.setNumberOfEmployees(order.getNumberOfEmployees());
            item.setHandlingDurationGreater15Min(order.getHandlingDurationGreater15Min());
            item.setHazardousDueToSkinContact(order.getHazardousDueToSkinContact());
            item.setGroupName(order.getGroupName());
            item.setStorageLocation(order.getStorageLocation());
            item.setOrderType(order.getOrderType());
            item.setBarcodeInfo(order.getBarcodeInfo());
            item.setCreatedAt(new Date());
            if (order.getSafetydatasheet() != null) {
                item.setFileName(order.getSafetydatasheet());
                item.setFileContent(order.getFileContent());
            }
            fineChemicalInventoryRepository.save(item);

        } else {
            // Default: general inventory
            Inventory item = new Inventory();
            item.setSourceOrderId(orderId);
            item.setProductname(order.getProductName());
            item.setCatalogue(order.getCatalogue());
            item.setCompanyname(order.getCompanyName());
            item.setQuantity(order.getQuantity());
            item.setCompanyinternalno(order.getCompanyInternalNo());
            item.setSapmaterialno(order.getSapMaterialNo());
            item.setWeightvolsubqty(order.getWeightVolSubQty());
            item.setBudgetno(order.getBudgetno());
            item.setOrderdate(order.getOrderDate());
            item.setExpirydate(order.getExpiryDate());
            item.setConcentration(order.getConcentration());
            item.setPrice(order.getPrice() != null ? order.getPrice() : 0.0);
            item.setRemarks(order.getRemarks());
            item.setAddedby(order.getOrderedBy());
            item.setGroupName(order.getGroupName());
            item.setShared(false);
            if (order.getSafetydatasheet() != null) {
                item.setFileName(order.getSafetydatasheet());
                item.setFileContent(order.getFileContent());
            }
            inventoryRepository.save(item);
        }
    }
}

