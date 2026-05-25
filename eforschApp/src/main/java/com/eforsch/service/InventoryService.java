package com.eforsch.service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eforsch.dto.FileDownload;
import com.eforsch.dto.User;
import com.eforsch.entity.FineChemicalInventory;
import com.eforsch.entity.Inventory;
import com.eforsch.repository.FineChemicalInventoryRepository;
import com.eforsch.repository.InventoryRepository;
import com.eforsch.util.InventoryMapper;
import com.eforsch.util.InventoryVO;

@Service
public class InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Autowired
	private com.eforsch.repository.SharedInventoryRepository sharedInventoryRepository;
	
	public InventoryVO addProduct(InventoryVO inventoryVO) {
		Inventory inventory = inventoryRepository.save(InventoryMapper.fromVOToEntity(inventoryVO));
		return InventoryMapper.fromEntityToVO(inventory, false);
	}
	
	public void saveInventory(MultipartFile file) throws Exception {
		List<Inventory> inventoryList = new ArrayList<>();
		try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream)) {
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();
			while (rows.hasNext()) {
				Row row = rows.next();
				if (row.getRowNum() == 0) {
					continue; // Skip header row
				}
			}
		}
		inventoryRepository.saveAll(inventoryList);
	}

	public Map<String, Object> getProductList(int page, int size, boolean shared, User user) {
		Page<Inventory> paginatedResult = null;
		
		// Validate user and group
		if (user == null || user.getGroupName() == null || user.getGroupName().isEmpty()) {
			throw new IllegalArgumentException("User and group name are required");
		}
		
		String userGroupName = user.getGroupName();

		if (shared) {
			paginatedResult = inventoryRepository.findByGroupNameContainingIgnoreCaseAndShared(userGroupName, true, PageRequest.of(page - 1, size));
		} else {
			paginatedResult = inventoryRepository.findByGroupNameContainingIgnoreCaseAndShared(userGroupName, false, PageRequest.of(page - 1, size));
		}

		List<InventoryVO> inventoryVOList = new ArrayList<>();
		for (Inventory inventory : paginatedResult.getContent()) {
			InventoryVO inventoryVO = InventoryMapper.fromEntityToVO(inventory, false);
			
			// If product is shared, override quantity with available quantity from SharedInventory
			if (inventory.isShared()) {
				try {
					Optional<com.eforsch.entity.SharedInventory> sharedInvOpt = sharedInventoryRepository.findByProductId(Long.toString(inventory.getProductId()));
					if (sharedInvOpt.isPresent()) {
						com.eforsch.entity.SharedInventory sharedInv = sharedInvOpt.get();
						if (sharedInv.getAvailableQuantity() != null) {
							inventoryVO.setQuantity(sharedInv.getAvailableQuantity());
						}
					}
				} catch (Exception e) {
					// If error getting shared inventory, keep original quantity
				}
			}
			
			inventoryVOList.add(inventoryVO);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("list", inventoryVOList);
		response.put("pagination",
				Map.of("currentPage", paginatedResult.getNumber() + 1, "pageSize", paginatedResult.getSize(),
						"totalRecords", paginatedResult.getTotalElements(), "totalPages",
						paginatedResult.getTotalPages()));

		return response;
	}

	public InventoryVO updateProduct(Long productId, InventoryVO updatedInventoryVO) {
		// Retrieve the inventory entity by ID
		Optional<Inventory> inventoryOptional = inventoryRepository.findById(productId.longValue());

		// If inventory exists, update its fields
		if (inventoryOptional.isPresent()) {
			Inventory existingInventory = inventoryOptional.get();

			// Update fields from InventoryVO to Inventory entity
			existingInventory.setProductname(updatedInventoryVO.getProductname());
			existingInventory.setCatalogue(updatedInventoryVO.getCatalogue());
			existingInventory.setPrice(updatedInventoryVO.getPrice());
			existingInventory.setCompanyname(updatedInventoryVO.getCompanyname());
			existingInventory.setQuantity(updatedInventoryVO.getQuantity());
			existingInventory.setGroupName(updatedInventoryVO.getGroupName());
			existingInventory.setAddedby(updatedInventoryVO.getAddedby());
			existingInventory.setShared(updatedInventoryVO.isShared());
			existingInventory.setCompanyinternalno(updatedInventoryVO.getCompanyinternalno());
			existingInventory.setSapmaterialno(updatedInventoryVO.getSapmaterialno());
			existingInventory.setWeightvolsubqty(updatedInventoryVO.getWeightvolsubqty());
			existingInventory.setBudgetno(updatedInventoryVO.getBudgetno());
			existingInventory.setConcentration(updatedInventoryVO.getConcentration());
			existingInventory.setRemarks(updatedInventoryVO.getRemarks());
			existingInventory.setExpirydate(updatedInventoryVO.getExpirydate());
			existingInventory.setOrderdate(updatedInventoryVO.getOrderdate());

			// Save the updated entity
			Inventory savedInventory = inventoryRepository.save(existingInventory);

			// Return the updated VO
			return InventoryMapper.fromEntityToVO(savedInventory, false);
		} else {
			throw new RuntimeException("Product with ID " + productId + " not found");
		}
	}

	public void deleteProduct(Long productId) {
		if (!inventoryRepository.existsById(productId)) {
			throw new RuntimeException("Product not found");
		}
		inventoryRepository.deleteById(productId);
	}

	public InventoryVO getProduct(Long productId) {

		Optional<Inventory> inventory = inventoryRepository.findById(productId);

		if (inventory == null) {
			throw new RuntimeException("Product not found");
		}

		if (inventory.isPresent()) {
			Inventory inventoryEntity = inventory.get();
			return InventoryMapper.fromEntityToVO(inventoryEntity, false);
		}
		return null;
	}

	public void processExcelFile(MultipartFile file, String groupName) {
		try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {

			Sheet sheet = workbook.getSheetAt(0); // Read the first sheet
			if (sheet == null) {
				throw new RuntimeException("The uploaded Excel file is empty.");
			}

			// Validate headers
			validateHeaders(sheet);

			List<Inventory> inventoryList = new ArrayList<>();
			for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
				Row row = sheet.getRow(i);
				if (row != null) {
					Inventory inventory = parseRowToInventory(row);
					// inventory.setGroupName(groupName); // Set the group name for the inventory
					// entry
					inventoryList.add(inventory);
				}
			}

			// Save all valid entries to the database
			inventoryRepository.saveAll(inventoryList);

		} catch (Exception e) {
			throw new RuntimeException("Error processing Excel file: " + e.getMessage());
		}
	}

	private void validateHeaders(Sheet sheet) {
		Row headerRow = sheet.getRow(0);
		if (headerRow == null) {
			throw new RuntimeException("The Excel file has no header row.");
		}

		List<String> expectedHeaders = Arrays.asList("Product Name", "Catalogue Number", "Company Name", "Quantity",
				"Group Name", "Company Internal number", "SAP Material number", "Weight Volume Subquantity",
				"Budget No", "Concentration", "Remarks", "Added by", "Expiry Date");

		for (int j = 0; j < expectedHeaders.size(); j++) {
			Cell cell = headerRow.getCell(j);
			if (cell == null || !expectedHeaders.get(j).equals(cell.getStringCellValue().trim())) {
				throw new RuntimeException("Invalid header in Excel file at column " + (j + 1));
			}
		}
	}

	private Inventory parseRowToInventory(Row row) {
		Inventory inventory = new Inventory();

		// Assuming row index 0 = productId (skip, as it’s auto-generated)
		inventory.setProductname(row.getCell(0).getStringCellValue());
		inventory.setCatalogue(row.getCell(1).getStringCellValue());
		inventory.setCompanyname(row.getCell(2).getStringCellValue());
		inventory.setQuantity((int) row.getCell(3).getNumericCellValue());
		inventory.setGroupName(row.getCell(4).getStringCellValue());
		inventory.setCompanyinternalno(row.getCell(5).getStringCellValue());
		inventory.setSapmaterialno(row.getCell(6).getStringCellValue());
		inventory.setWeightvolsubqty(row.getCell(7).getStringCellValue());
		inventory.setBudgetno(row.getCell(8).getStringCellValue());
		inventory.setConcentration(row.getCell(9).getStringCellValue());

		inventory.setRemarks(row.getCell(10).getStringCellValue());
		inventory.setAddedby(row.getCell(11).getStringCellValue());

		inventory.setExpirydate(getCellDate(row.getCell(12)));
		inventory.setOrderdate(getCurrentDateOnly());

		return inventory;
	}

	private Date getCurrentDateOnly() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			return sdf.parse(sdf.format(new Date())); // trims time
		} catch (Exception e) {
			return new Date(); // fallback with time if parsing fails
		}
	}

	private Date getCellDate(Cell cell) {
		if (cell == null)
			return null;

		if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
			// Excel serial date (actual date cell)
			return cell.getDateCellValue();
		} else if (cell.getCellType() == CellType.STRING) {
			// User typed the date as text → dd-MM-yyyy
			String value = cell.getStringCellValue().trim();
			if (value.isEmpty())
				return null;

			try {
				// Adjust pattern if your Excel uses "/" instead of "-"
				return new java.text.SimpleDateFormat("dd-MM-yyyy").parse(value);
			} catch (Exception e) {
				System.err.println("Invalid date format: " + value);
				return null;
			}
		}
		return null;
	}

	public InventoryVO updateProductFile(Long productId, MultipartFile file, boolean returnFileContent)
			throws Exception {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("No file uploaded");
		}
		validateFile(file);

		Inventory inv = inventoryRepository.findByProductId(productId);
		if (inv == null) {
			throw new IllegalArgumentException("Inventory not found: " + productId);
		}

		inv.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
		inv.setFileType(file.getContentType());
		inv.setFileContent(file.getBytes());

		Inventory saved = inventoryRepository.save(inv);
		return InventoryMapper.fromEntityToVO(saved, false);
	}

	public FileDownload getProductFile(Long productId) {
		Inventory inv = inventoryRepository.findByProductId(productId);

		if (inv == null) {
			throw new IllegalArgumentException("Inventory not found: " + productId);
		}

		if (inv.getFileContent() == null)
			return null;

		return new FileDownload(inv.getFileName(), inv.getFileType(), inv.getFileContent());
	}

	public void deleteProductFile(Long productId) {
		Inventory inv = inventoryRepository.findByProductId(productId);
		if (inv == null) {
			throw new IllegalArgumentException("Inventory not found: " + productId);
		}

		inv.setFileName(null);
		inv.setFileType(null);
		inv.setFileContent(null);
		inventoryRepository.save(inv);
	}

	/** Basic whitelist/guardrails — tweak to your needs */
	private void validateFile(MultipartFile file) {
		// Size guardrail (also configure in application.yml)
		long max = 50L * 1024 * 1024; // 50MB
		if (file.getSize() > max) {
			throw new IllegalArgumentException("File too large. Max 50MB");
		}

		// (Optional) content-type whitelist
		String ct = file.getContentType();
		if (ct != null) {
			boolean ok = ct.equals("application/pdf") || ct.equals("image/png") || ct.equals("image/jpeg")
					|| ct.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			if (!ok) {
				throw new IllegalArgumentException("Unsupported file type: " + ct);
			}
		}
	}

}
