package com.eforsch.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eforsch.dto.FileDownload;
import com.eforsch.dto.FineChemicalInventoryVO;
import com.eforsch.dto.User;
import com.eforsch.entity.FineChemicalInventory;
import com.eforsch.repository.FineChemicalInventoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FineChemicalService {

	@Autowired
	private FineChemicalInventoryRepository fineChemicalInventoryRepository;
	
	@Autowired
	private com.eforsch.repository.SharedInventoryRepository sharedInventoryRepository;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public Map<String, Object> getFineChemicalInventoryByGroupName(int page, int limit, String sortBy, String order,
			User user) {
		try {
			Page<FineChemicalInventory> inventoryPage;

			String safeSortBy = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;
			String safeOrder = (order == null || order.isBlank()) ? "desc" : order;

			Sort.Direction direction = safeOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
			Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, safeSortBy));

			if (user.getGroupName() != null && !user.getGroupName().isBlank()) {
				inventoryPage = fineChemicalInventoryRepository.findBySharedAndGroupNameContainingIgnoreCase(false, user.getGroupName(),
						pageable);
			} else {
				inventoryPage = fineChemicalInventoryRepository.findByShared(false, pageable);
			}

			List<Map<String, Object>> listData = inventoryPage.getContent().stream().map(item -> {
				Map<String, Object> map = new HashMap<>();
				map.put("productId", item.getProductId());
				map.put("productname", item.getProductname());
				map.put("catalogue", item.getCatalogue());
				map.put("companyname", item.getCompanyname());
				map.put("quantity", item.getQuantity());
				map.put("expirydate", item.getExpiryDate());
				map.put("companyinternalno", item.getCompanyInternalNo());
				map.put("sapmaterialno", item.getSapMaterialNo());
				map.put("wvsubqty", item.getWvsubqty());
				map.put("budgetno", item.getBudgetno());
				map.put("orderdate", item.getOrderdate());
				map.put("orderedby", item.getOrderedby());
				map.put("concentration", item.getConcentration());
				map.put("remarks", item.getRemarks());
				map.put("casnumber", item.getCasnumber());
				map.put("hazardoussubstance", booleanToYesNo(item.getHazardousSubstance()));
				map.put("cmrsubstance", booleanToYesNo(item.getCmrSubstance()));
				map.put("skinresorptive", booleanToYesNo(item.getSkinResorptive()));
				map.put("ghssymbols", item.getGhsSymbols());
				map.put("ghssignalword", item.getGhsSignalWord());
				map.put("hphrases", item.gethPhrases());
				map.put("pphrases", item.getpPhrases());
				map.put("substitutioncheck", item.getSubstitutionCheck());
				map.put("storagelocation", item.getStorageLocation());
				map.put("substitutionOption", item.getSubstitutionOption());
				map.put("groupName", item.getGroupName());
				map.put("createdAt", item.getCreatedAt());
				map.put("price", item.getPrice());
				map.put("filename", item.getFileName());
				map.put("filetype", item.getFileType());
				// Convert to a map for each item
				return map;
			}).collect(Collectors.toList());

			Map<String, Object> pagination = Map.of("currentPage", inventoryPage.getNumber() + 1, "totalRecords",
					inventoryPage.getTotalElements(), "totalPages", inventoryPage.getTotalPages(), "pageSize",
					inventoryPage.getSize());

			List<Map<String, Object>> columns = List.of(Map.of("key", "srno", "label", "Sr No", "sortable", true),
					Map.of("key", "productname", "label", "Product", "filterable", true),
					Map.of("key", "catalogue", "label", "Catalogue", "filterable", true),
					Map.of("key", "companyname", "label", "Company", "filterable", true),
					Map.of("key", "quantity", "label", "Quantity"), Map.of("key", "expirydate", "label", "Expiry Date"),
					Map.of("key", "companyinternalno", "label", "Company Internal No"),
					Map.of("key", "sapmaterialno", "label", "SAP Material No"),
					Map.of("key", "wvsubqty", "label", "W/V/Sub Qty"),
					Map.of("key", "budgetno", "label", "KST/Budget No"),
					Map.of("key", "orderdate", "label", "Order Date"),
					Map.of("key", "orderedby", "label", "Ordered By"),
					Map.of("key", "concentration", "label", "Concentration"), Map.of("key", "price", "label", "Price"),
					Map.of("key", "remarks", "label", "Remarks"), Map.of("key", "casnumber", "label", "CAS Number"),
					Map.of("key", "hazardoussubstance", "label", "Hazardous Substance"),
					Map.of("key", "cmrsubstance", "label", "CMR Substance"),
					Map.of("key", "skinresorptive", "label", "Skin-resorptive"),
					Map.of("key", "ghssymbols", "label", "GHS Symbols"),
					Map.of("key", "ghssignalword", "label", "GHS Signal Word"),
					Map.of("key", "hphrases", "label", "H-Phrases"), Map.of("key", "pphrases", "label", "P-Phrases"),
					Map.of("key", "substitutioncheck", "label", "Substitution Check"),
					Map.of("key", "storagelocation", "label", "Storage Location"),
					Map.of("key", "substitutionOption", "label", "Substitution Option"),
					Map.of("key", "price", "label", "Price"),
					Map.of("key", "filename", "label", "File Name"), Map.of("key", "filetype", "label", "File Type"));

			Map<String, Object> data = Map.of("list", listData, "pagination", pagination, "columns", columns);

			return Map.of("data", data, "message", "Data fetched successfully", "status", "success");

		} catch (SecurityException se) {
			return Map.of("status", "error", "message", se.getMessage(), "code", 401);
		} catch (Exception e) {
			return Map.of("status", "error", "message", "An unexpected error occurred.", "code", 500);
		}
	}

	public Map<String, Object> getFineChemicalInventory(int page, int limit, String sortBy, String order, User user) {
		try {
			// Validate user and group
			if (user == null || user.getGroupName() == null || user.getGroupName().isEmpty()) {
				throw new IllegalArgumentException("User and group name are required");
			}
			
			Page<FineChemicalInventory> inventoryPage;

			String safeSortBy = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;
			String safeOrder = (order == null || order.isBlank()) ? "desc" : order;

			Sort.Direction direction = safeOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
			Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, safeSortBy));

			String userGroupName = user.getGroupName();
			inventoryPage = fineChemicalInventoryRepository.findBySharedAndGroupNameContainingIgnoreCase(false, userGroupName, pageable);

			List<Map<String, Object>> listData = inventoryPage.getContent().stream().map(item -> {
				Map<String, Object> map = new HashMap<>();
				map.put("productid", item.getProductId());
				map.put("productname", item.getProductname());
				map.put("catalogue", item.getCatalogue());
				map.put("companyname", item.getCompanyname());
				
				// If product is shared, get available quantity from SharedInventory
				String quantity = item.getQuantity();
				if (item.isShared()) {
					try {
						Optional<com.eforsch.entity.SharedInventory> sharedInvOpt = sharedInventoryRepository.findByProductId(Long.toString(item.getProductId()));
						if (sharedInvOpt.isPresent()) {
							com.eforsch.entity.SharedInventory sharedInv = sharedInvOpt.get();
							if (sharedInv.getAvailableQuantity() != null) {
								quantity = String.valueOf(sharedInv.getAvailableQuantity());
							}
						}
					} catch (Exception e) {
						// If error, keep original quantity
					}
				}
				
				map.put("quantity", quantity);
				map.put("expirydate", item.getExpiryDate());
				map.put("companyinternalno", item.getCompanyInternalNo());
				map.put("sapmaterialno", item.getSapMaterialNo());
				map.put("wvsubqty", item.getWvsubqty());
				map.put("budgetno", item.getBudgetno());
				map.put("orderdate", item.getOrderdate());
				map.put("orderedby", item.getOrderedby());
				map.put("concentration", item.getConcentration());
				map.put("remarks", item.getRemarks());
				map.put("casnumber", item.getCasnumber());
				map.put("hazardoussubstance", booleanToYesNo(item.getHazardousSubstance()));
				map.put("cmrsubstance", booleanToYesNo(item.getCmrSubstance()));
				map.put("skinresorptive", booleanToYesNo(item.getSkinResorptive()));
				map.put("ghssymbols", item.getGhsSymbols());
				map.put("ghssignalword", item.getGhsSignalWord());
				map.put("hphrases", item.gethPhrases());
				map.put("pphrases", item.getpPhrases());
				map.put("substitutioncheck", item.getSubstitutionCheck());
				map.put("substitutionOption", item.getSubstitutionOption());
				map.put("storagelocation", item.getStorageLocation());
				map.put("groupName", item.getGroupName());
				map.put("createdAt", item.getCreatedAt());
				map.put("price", item.getPrice());
				map.put("filename", item.getFileName());
				map.put("filetype", item.getFileType());
				return map;
			}).collect(Collectors.toList());

			Map<String, Object> pagination = Map.of("currentPage", inventoryPage.getNumber() + 1, "totalRecords",
					inventoryPage.getTotalElements(), "totalPages", inventoryPage.getTotalPages(), "pageSize",
					inventoryPage.getSize());

			List<Map<String, Object>> columns = List.of(Map.of("key", "srno", "label", "Sr No", "sortable", true),
					Map.of("key", "productname", "label", "Product", "filterable", true),
					Map.of("key", "catalogue", "label", "Catalogue", "filterable", true),
					Map.of("key", "companyname", "label", "Company", "filterable", true),
					Map.of("key", "quantity", "label", "Quantity"), Map.of("key", "expirydate", "label", "Expiry Date"),
					Map.of("key", "companyinternalno", "label", "Company Internal No"),
					Map.of("key", "sapmaterialno", "label", "SAP Material No"),
					Map.of("key", "wvsubqty", "label", "W/V/Sub Qty"),
					Map.of("key", "budgetno", "label", "KST/Budget No"),
					Map.of("key", "orderdate", "label", "Order Date"),
					Map.of("key", "orderedby", "label", "Ordered By"),
					Map.of("key", "concentration", "label", "Concentration"), Map.of("key", "price", "label", "Price"),
					Map.of("key", "remarks", "label", "Remarks"), Map.of("key", "casnumber", "label", "CAS Number"),
					Map.of("key", "hazardoussubstance", "label", "Hazardous Substance"),
					Map.of("key", "cmrsubstance", "label", "CMR Substance"),
					Map.of("key", "skinresorptive", "label", "Skin-resorptive"),
					Map.of("key", "ghssymbols", "label", "GHS Symbols"),
					Map.of("key", "ghssignalword", "label", "GHS Signal Word"),
					Map.of("key", "hphrases", "label", "H-Phrases"), Map.of("key", "pphrases", "label", "P-Phrases"),
					Map.of("key", "substitutioncheck", "label", "Substitution Check"),
					Map.of("key", "price", "label", "Price"),
					Map.of("key", "storagelocation", "label", "Storage Location"),
					Map.of("key", "substitutionOption", "label", "Substitution Option"),
					Map.of("key", "filename", "label", "File Name"), Map.of("key", "filetype", "label", "File Type"));

			Map<String, Object> data = Map.of("list", listData, "pagination", pagination, "columns", columns);

			return Map.of("data", data, "message", "Data fetched successfully", "status", "success");

		} catch (SecurityException se) {
			return Map.of("status", "error", "message", se.getMessage(), "code", 401);
		} catch (Exception e) {
			return Map.of("status", "error", "message", "An unexpected error occurred.", "code", 500);
		}
	}

	private String booleanToYesNo(Boolean value) {
		return value != null && value ? "Yes" : "No";
	}

	public Map<String, Object> getFineChemicalById(Long id) {
		try {
			Optional<FineChemicalInventory> optional = fineChemicalInventoryRepository.findById(id);
			if (optional.isEmpty()) {
				return Map.of("status", "error", "message", "Product not found", "code", 404);
			}

			FineChemicalInventory item = optional.get();

			Map<String, Object> itemMap = new HashMap<>();
			itemMap.put("productid", item.getProductId());
			itemMap.put("productname", item.getProductname());
			itemMap.put("catalogue", item.getCatalogue());
			itemMap.put("companyname", item.getCompanyname());
			itemMap.put("quantity", item.getQuantity());
			itemMap.put("expirydate", item.getExpiryDate());
			itemMap.put("companyinternalno", item.getCompanyInternalNo());
			itemMap.put("sapmaterialno", item.getSapMaterialNo());
			itemMap.put("wvsubqty", item.getWvsubqty());
			itemMap.put("budgetno", item.getBudgetno());
			itemMap.put("orderdate", item.getOrderdate());
			itemMap.put("orderedby", item.getOrderedby());
			itemMap.put("concentration", item.getConcentration());
			itemMap.put("remarks", item.getRemarks());
			itemMap.put("casnumber", item.getCasnumber());
			itemMap.put("hazardoussubstance", booleanToYesNo(item.getHazardousSubstance()));
			itemMap.put("cmrsubstance", booleanToYesNo(item.getCmrSubstance()));
			itemMap.put("skinresorptive", booleanToYesNo(item.getSkinResorptive()));
			itemMap.put("ghssymbols", item.getGhsSymbols());
			itemMap.put("ghssignalword", item.getGhsSignalWord());
			itemMap.put("hphrases", item.gethPhrases());
			itemMap.put("pphrases", item.getpPhrases());
			itemMap.put("substitutioncheck", item.getSubstitutionCheck());
			itemMap.put("storagelocation", item.getStorageLocation());
			itemMap.put("substitutionOption", item.getSubstitutionOption());
			itemMap.put("groupname", item.getGroupName());
			itemMap.put("createdat", item.getCreatedAt());
			itemMap.put("price", item.getPrice());
			itemMap.put("filename", item.getFileName());
			itemMap.put("filetype", item.getFileType());

			Map<String, Object> pagination = Map.of("currentPage", 1, "totalRecords", 1, "totalPages", 1, "pageSize",
					10);

			List<Map<String, Object>> columns = List.of(Map.of("key", "srno", "label", "Sr No", "sortable", true),
					Map.of("key", "productname", "label", "Product", "filterable", true),
					Map.of("key", "catalogue", "label", "Catalogue", "filterable", true),
					Map.of("key", "companyname", "label", "Company", "filterable", true),
					Map.of("key", "quantity", "label", "Quantity"), Map.of("key", "expirydate", "label", "Expiry Date"),
					Map.of("key", "companyinternalno", "label", "Company Internal No"),
					Map.of("key", "sapmaterialno", "label", "SAP Material No"),
					Map.of("key", "wvsubqty", "label", "W/V/Sub Qty"),
					Map.of("key", "budgetno", "label", "KST/Budget No"),
					Map.of("key", "orderdate", "label", "Order Date"),
					Map.of("key", "orderedby", "label", "Ordered By"),
					Map.of("key", "concentration", "label", "Concentration"), Map.of("key", "price", "label", "Price"),
					Map.of("key", "remarks", "label", "Remarks"), Map.of("key", "casnumber", "label", "CAS Number"),
					Map.of("key", "hazardoussubstance", "label", "Hazardous Substance"),
					Map.of("key", "cmrsubstance", "label", "CMR Substance"),
					Map.of("key", "skinresorptive", "label", "Skin-resorptive"),
					Map.of("key", "ghssymbols", "label", "GHS Symbols"),
					Map.of("key", "ghssignalword", "label", "GHS Signal Word"),
					Map.of("key", "hphrases", "label", "H-Phrases"), Map.of("key", "pphrases", "label", "P-Phrases"),
					Map.of("key", "substitutioncheck", "label", "Substitution Check"),
					Map.of("key", "price", "label", "Price"),
					Map.of("key", "storagelocation", "label", "Storage Location"),
					Map.of("key", "filename", "label", "File Name"), Map.of("key", "filetype", "label", "File Type"));

			Map<String, Object> data = Map.of("list", List.of(itemMap), "pagination", pagination, "columns", columns,
					"user", Map.of("id", 12345, "email", "user@example.com", "name", "John Doe", "role", "user",
							"groupName", ""));

			return Map.of("data", data, "message", "Data fetched successfully", "status", "success");

		} catch (SecurityException se) {
			return Map.of("status", "error", "message", se.getMessage(), "code", 401);
		} catch (Exception e) {
			return Map.of("status", "error", "message", "An unexpected error occurred.", "code", 500);
		}
	}

	public FineChemicalInventoryVO addFineChemical(FineChemicalInventoryVO input) {
		try {

			// convert VO to entity
			FineChemicalInventory entity = voToEntity(input);

			entity.setFileContent(input.getFileContent());
			entity.setFileName(input.getFileName());
			entity.setFileType(input.getFileType());

			FineChemicalInventory saved = fineChemicalInventoryRepository.save(entity);
			FineChemicalInventoryVO fineChemicalInventoryVO = entityToVO(saved);
			return fineChemicalInventoryVO;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private Map<String, Object> buildResponseData(FineChemicalInventory item) {
		Map<String, Object> map = new HashMap<>();
		map.put("productid", item.getProductId());
		map.put("productname", item.getProductname());
		map.put("catalogue", item.getCatalogue());
		map.put("companyname", item.getCompanyname());
		map.put("quantity", item.getQuantity());
		map.put("expirydate", item.getExpiryDate());
		map.put("companyinternalno", item.getCompanyInternalNo());
		map.put("sapmaterialno", item.getSapMaterialNo());
		map.put("wvsubqty", item.getWvsubqty());
		map.put("budgetno", item.getBudgetno());
		map.put("orderdate", item.getOrderdate());
		map.put("orderedby", item.getOrderedby());
		map.put("concentration", item.getConcentration());
		map.put("remarks", item.getRemarks());
		map.put("casnumber", item.getCasnumber());
		map.put("hazardoussubstance", booleanToYesNo(item.getHazardousSubstance()));
		map.put("cmrsubstance", booleanToYesNo(item.getCmrSubstance()));
		map.put("skinresorptive", booleanToYesNo(item.getSkinResorptive()));
		map.put("ghssymbols", item.getGhsSymbols());
		map.put("ghssignalword", item.getGhsSignalWord());
		map.put("hphrases", item.gethPhrases());
		map.put("pphrases", item.getpPhrases());
		map.put("substitutioncheck", item.getSubstitutionCheck()); // Return as object directly
		map.put("storagelocation", item.getStorageLocation());
		map.put("substitutionOption", item.getSubstitutionOption());
		map.put("groupName", item.getGroupName());
		map.put("price", item.getPrice());
		map.put("createdAt", item.getCreatedAt());
		map.put("filename", item.getFileName());
		map.put("filetype", item.getFileType());
		return map;
	}

	public Map<String, Object> updateFineChemical(Long id, FineChemicalInventoryVO input) {
		try {
			// validateAndParseToken(token);

			Optional<FineChemicalInventory> optional = fineChemicalInventoryRepository.findById(id);
			if (optional.isEmpty()) {
				return Map.of("status", "error", "message", "Product not found", "code", 404);
			}

			FineChemicalInventory existing = optional.get();

			// Update fields (new entity field names)
			existing.setProductname(input.getProductname());
			existing.setCatalogue(input.getCatalogue());
			existing.setCompanyname(input.getCompanyname());
			existing.setQuantity(input.getQuantity());
			existing.setExpiryDate(input.getExpiryDate());
			existing.setCompanyInternalNo(input.getCompanyInternalNo());
			existing.setSapMaterialNo(input.getSapMaterialNo());
			existing.setWvsubqty(input.getWvsubqty());
			existing.setBudgetno(input.getBudgetno());
			existing.setOrderdate(input.getOrderdate());
			existing.setOrderedby(input.getOrderedby());
			existing.setConcentration(input.getConcentration());
			existing.setRemarks(input.getRemarks());
			existing.setCasnumber(input.getCasnumber());
			existing.setHazardousSubstance(input.getHazardousSubstance());
			existing.setSubstitutionOption(input.getSubstitutionOption());
			existing.setCmrSubstance(input.getCmrSubstance());
			existing.setSkinResorptive(input.getSkinResorptive());
			existing.setPrice(input.getPrice());

			if (input.getGhsSymbols() != null) {
				existing.setGhsSymbols(objectMapper.writeValueAsString(input.getGhsSymbols()));
			}
			if (input.getGhsSignalWord() != null) {
				existing.setGhsSignalWord(objectMapper.writeValueAsString(input.getGhsSignalWord()));
			}

			existing.sethPhrases(input.gethPhrases());
			existing.setpPhrases(input.getpPhrases());
			existing.setSubstitutionCheck(input.getSubstitutionCheck());
			existing.setStorageLocation(input.getStorageLocation());
			existing.setGroupName(input.getGroupName());

			// Do not update createdAt in update

			FineChemicalInventory saved = fineChemicalInventoryRepository.save(existing);

			Map<String, Object> data = buildResponseData(saved);

			return Map.of("status", "success", "message", "Product updated successfully", "data", data);

		} catch (SecurityException se) {
			return Map.of("status", "error", "message", se.getMessage(), "code", 401);
		} catch (Exception e) {
			return Map.of("status", "error", "message", "An unexpected error occurred.", "code", 500);
		}
	}

	private FineChemicalInventory parseRowToInventory(Row row, Workbook workbook) {

		FineChemicalInventory inv = new FineChemicalInventory();

		inv.setProductname(row.getCell(0).getStringCellValue()); // Product Name
		inv.setCatalogue(row.getCell(1).getStringCellValue()); // Catalogue Number
		inv.setCompanyname(row.getCell(2).getStringCellValue()); // Company Name
		inv.setQuantity(row.getCell(3).getStringCellValue()); // Quantity
		inv.setExpiryDate(parseDate(row.getCell(4).getStringCellValue())); // Expiry Date
		inv.setCompanyInternalNo(row.getCell(5).getStringCellValue()); // Company Internal Number
		inv.setSapMaterialNo(row.getCell(6).getStringCellValue()); // SAP Material Number
		inv.setWvsubqty(row.getCell(7).getStringCellValue()); // Weight Volume Subquantity
		inv.setBudgetno(row.getCell(8).getStringCellValue()); // Budget No
		inv.setOrderedby(row.getCell(9).getStringCellValue()); // Ordered By
		inv.setConcentration(row.getCell(10).getStringCellValue()); // Concentration

		// Amount (numeric → BigDecimal)
		if (row.getCell(11) != null) {
			inv.setAmount(new java.math.BigDecimal(row.getCell(11).getNumericCellValue()));
		}

		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		// ... inside parseRowToInventory(Row row):
		BigDecimal amount = getCellBigDecimal(row.getCell(11), evaluator); // column 11 = "Amount"
		if (amount != null) {
			// Optional: enforce 2 decimal places
			amount = amount.setScale(2, RoundingMode.HALF_UP);
		}
		inv.setAmount(amount);

		inv.setRemarks(row.getCell(12).getStringCellValue()); // Remarks
		inv.setCasnumber(row.getCell(13).getStringCellValue()); // CAS Number

		// Boolean fields (Y/N or Yes/No)
		inv.setHazardousSubstance(parseYesNoBoolean(row.getCell(14).getStringCellValue())); // Hazardous Substance
		inv.setCmrSubstance(parseYesNoBoolean(row.getCell(15).getStringCellValue())); // CMR Substance
		inv.setSkinResorptive(parseYesNoBoolean(row.getCell(16).getStringCellValue())); // Skin Resorptive

		// JSON/Text fields
		// inv.setGhsSymbols(row.getCell(17).getStringCellValue()); // GHS Symbols
		// inv.setGhsSignalWord(row.getCell(18).getStringCellValue()); // GHS Signal
		// Word

		String symbols = getCellString(row.getCell(17));
		if (symbols != null && symbols.trim().startsWith("[")) {
			// assume user already provided JSON array, optionally validate
			inv.setGhsSymbols(symbols.trim());
		} else {
			inv.setGhsSymbols(csvToJsonArray(symbols));
		}

		String signal = getCellString(row.getCell(18)); // your index
		inv.setGhsSignalWord(asJsonString(signal));

		inv.sethPhrases(row.getCell(19).getStringCellValue()); // H Phrases
		inv.setpPhrases(row.getCell(20).getStringCellValue()); // P Phrases
		inv.setSubstitutionCheck(row.getCell(21).getStringCellValue()); // Substitution Check
		inv.setSubstitutionOption(row.getCell(22).getStringCellValue()); // Substitution Option
		inv.setStorageLocation(row.getCell(23).getStringCellValue()); // Storage Location
		inv.setGroupName(row.getCell(24).getStringCellValue()); // Group Name
		inv.setQtypriceordered(row.getCell(25).getStringCellValue()); // Quantity Price Ordered
		inv.setPriority(row.getCell(26).getStringCellValue()); // Priority
		inv.setReceived(row.getCell(27).getStringCellValue()); // Received
		inv.setCatalogue(row.getCell(28).getStringCellValue()); // Catalogue
		inv.setCreatedAt(parseDate(row.getCell(29).getStringCellValue())); // Created At
		inv.setOrderdate(parseDate(row.getCell(30).getStringCellValue())); // Order Date

		return inv;
	}

	public static FineChemicalInventory voToEntity(FineChemicalInventoryVO vo) {
		FineChemicalInventory entity = new FineChemicalInventory();
		entity.setProductId(vo.getProductId());
		entity.setProductname(vo.getProductname());
		entity.setCatalogue(vo.getCatalogue());
		entity.setCompanyname(vo.getCompanyname());
		entity.setQuantity(vo.getQuantity());
		entity.setExpiryDate(vo.getExpiryDate());
		entity.setCompanyInternalNo(vo.getCompanyInternalNo());
		entity.setSapMaterialNo(vo.getSapMaterialNo());
		entity.setWvsubqty(vo.getWvsubqty());
		entity.setBudgetno(vo.getBudgetno());
		entity.setOrderdate(vo.getOrderdate());
		entity.setOrderedby(vo.getOrderedby());
		entity.setConcentration(vo.getConcentration());
		entity.setAmount(vo.getAmount());
		entity.setRemarks(vo.getRemarks());
		entity.setCasnumber(vo.getCasnumber());
		entity.setHazardousSubstance(vo.getHazardousSubstance());
		entity.setCmrSubstance(vo.getCmrSubstance());
		entity.setSkinResorptive(vo.getSkinResorptive());
		entity.setPrice(vo.getPrice());
		// entity.setghssymbols(vo.getghssymbols());
		// entity.setghssignalword(vo.getghssignalword());

		if (vo.getGhsSignalWord() != null) {
			try {
				entity.setGhsSymbols(objectMapper.writeValueAsString(vo.getGhsSymbols()));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (vo.getGhsSignalWord() != null) {
			try {
				entity.setGhsSignalWord(objectMapper.writeValueAsString(vo.getGhsSignalWord()));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		entity.sethPhrases(vo.gethPhrases());
		entity.setpPhrases(vo.getpPhrases());
		entity.setSubstitutionCheck(vo.getSubstitutionCheck());
		entity.setSubstitutionOption(vo.getSubstitutionOption());
		entity.setStorageLocation(vo.getStorageLocation());
		entity.setGroupName(vo.getGroupName());
		entity.setQtypriceordered(vo.getQtypriceordered());
		entity.setPriority(vo.getPriority());
		entity.setReceived(vo.getReceived());
		entity.setCatalogue(vo.getCatalogue());
		entity.setCreatedAt(vo.getCreatedAt());
		return entity;
	}

	public static FineChemicalInventoryVO entityToVO(FineChemicalInventory entity) {
		FineChemicalInventoryVO vo = new FineChemicalInventoryVO();
		vo.setProductId(entity.getProductId());
		vo.setProductname(entity.getProductname());
		vo.setCatalogue(entity.getCatalogue());
		vo.setCompanyname(entity.getCompanyname());
		vo.setQuantity(entity.getQuantity());
		vo.setExpiryDate(entity.getExpiryDate());
		vo.setCompanyInternalNo(entity.getCompanyInternalNo());
		vo.setSapMaterialNo(entity.getSapMaterialNo());
		vo.setWvsubqty(entity.getWvsubqty());
		vo.setBudgetno(entity.getBudgetno());
		vo.setOrderdate(entity.getOrderdate());
		vo.setOrderedby(entity.getOrderedby());
		vo.setConcentration(entity.getConcentration());
		vo.setAmount(entity.getAmount());
		vo.setRemarks(entity.getRemarks());
		vo.setCasnumber(entity.getCasnumber());
		vo.setHazardousSubstance(entity.getHazardousSubstance());
		vo.setCmrSubstance(entity.getCmrSubstance());
		vo.setSkinResorptive(entity.getSkinResorptive());
		vo.setPrice(entity.getPrice());

		// vo.setghssymbols(entity.getghssymbols());
		// vo.setghssignalword(entity.getghssignalword());

		if (entity.getGhsSymbols() != null && !entity.getGhsSymbols().isEmpty()) {
			try {
				vo.setGhsSymbols(objectMapper.readValue(entity.getGhsSignalWord(), String[].class));
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (entity.getGhsSignalWord() != null && !entity.getGhsSignalWord().isEmpty()) {
			try {
				vo.setGhsSignalWord(objectMapper.readValue(entity.getGhsSignalWord(), String[].class));
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		vo.sethPhrases(entity.gethPhrases());
		vo.setpPhrases(entity.getpPhrases());
		vo.setSubstitutionCheck(entity.getSubstitutionCheck());
		vo.setSubstitutionOption(entity.getSubstitutionOption());
		vo.setStorageLocation(entity.getStorageLocation());
		vo.setGroupName(entity.getGroupName());
		vo.setQtypriceordered(entity.getQtypriceordered());
		vo.setPriority(entity.getPriority());
		vo.setReceived(entity.getReceived());
		vo.setCatalogue(entity.getCatalogue());
		vo.setCreatedAt(entity.getCreatedAt());
		vo.setFileName(entity.getFileName());
		vo.setFileType(entity.getFileType());

		return vo;
	}

	private String getCellValue(Row row, Integer colIndex) {
		if (colIndex == null)
			return null;
		Cell cell = row.getCell(colIndex);
		if (cell == null)
			return null;
		return switch (cell.getCellType()) {
		case STRING -> cell.getStringCellValue().trim();
		case NUMERIC -> DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue().toString()
				: String.valueOf(cell.getNumericCellValue());
		case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
		default -> null;
		};
	}

	private java.math.BigDecimal parseBigDecimal(String value) {
		try {
			return value != null ? new java.math.BigDecimal(value) : null;
		} catch (Exception e) {
			return null;
		}
	}

	private Boolean parseYesNoBoolean(String value) {
		if (value == null)
			return null;
		return value.equalsIgnoreCase("yes");
	}

	private Date parseDate(String value) {
		try {
			return java.sql.Date.valueOf(value);
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, Object> deleteFineChemical(Long productId) {
		try {
			// validateAndParseToken(token);

			Optional<FineChemicalInventory> optional = fineChemicalInventoryRepository.findById(productId);
			if (optional.isEmpty()) {
				return Map.of("status", "error", "message", "Product not found", "code", 404);
			}

			fineChemicalInventoryRepository.deleteById(productId);

			return Map.of("code", 200, "message", "Data Deleted successfully", "status", "success");

		} catch (SecurityException se) {
			return Map.of("status", "error", "message", se.getMessage(), "code", 401);
		} catch (Exception e) {
			return Map.of("status", "error", "message", "An unexpected error occurred.", "code", 500);
		}
	}

	public void processExcelFile(MultipartFile file, String groupName) {
		try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {

			Sheet sheet = workbook.getSheetAt(0); // Read the first sheet
			if (sheet == null) {
				throw new RuntimeException("The uploaded Excel file is empty.");
			}

			// Validate headers
			validateHeaders(sheet);

			List<FineChemicalInventory> inventoryList = new ArrayList<>();
			for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
				Row row = sheet.getRow(i);
				if (row != null) {
					FineChemicalInventory inventory = parseRowToInventory(row, workbook);
					// inventory.setGroupName(groupName); // Set the group name for the inventory
					// entry
					inventoryList.add(inventory);
				}
			}

			// Save all valid entries to the database
			fineChemicalInventoryRepository.saveAll(inventoryList);

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
				"Expiry Date", "Company Internal Number", "SAP Material Number", "Weight Volume Subquantity",
				"Budget No", "Ordered By", "Concentration", "Amount", "Remarks", "CAS Number", "Hazardous Substance",
				"CMR Substance", "Skin Resorptive", "GHS Symbols", "GHS Signal Word", "H Phrases", "P Phrases",
				"Substitution Check", "Substitution Option", "Storage Location", "Group Name", "Quantity Price Ordered",
				"Priority", "Received", "Catalogue", "Created At", "Order Date");

		for (int j = 0; j < expectedHeaders.size(); j++) {
			Cell cell = headerRow.getCell(j);
			if (cell == null || !expectedHeaders.get(j).equals(cell.getStringCellValue().trim())) {
				throw new RuntimeException("Invalid header in Excel file at column " + (j + 1));
			}
		}
	}

	public FineChemicalInventoryVO updateProductFile(Long productId, MultipartFile file, boolean returnFileContent)
			throws Exception {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("No file uploaded");
		}
		validateFile(file);

		FineChemicalInventory inv = fineChemicalInventoryRepository.findByProductId(productId);
		if (inv == null) {
			throw new IllegalArgumentException("Inventory not found: " + productId);
		}

		inv.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
		inv.setFileType(file.getContentType());
		inv.setFileContent(file.getBytes());

		FineChemicalInventory saved = fineChemicalInventoryRepository.save(inv);
		return entityToVO(saved);
	}

	public FileDownload getProductFile(Long productId) {
		FineChemicalInventory inv = fineChemicalInventoryRepository.findByProductId(productId);

		if (inv == null) {
			throw new IllegalArgumentException("Inventory not found: " + productId);
		}

		if (inv.getFileContent() == null)
			return null;

		return new FileDownload(inv.getFileName(), inv.getFileType(), inv.getFileContent());
	}

	public void deleteProductFile(Long productId) {
		FineChemicalInventory inv = fineChemicalInventoryRepository.findByProductId(productId);
		if (inv == null) {
			throw new IllegalArgumentException("Inventory not found: " + productId);
		}

		inv.setFileName(null);
		inv.setFileType(null);
		inv.setFileContent(null);
		fineChemicalInventoryRepository.save(inv);
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

	private BigDecimal getCellBigDecimal(Cell cell, FormulaEvaluator evaluator) {
		if (cell == null)
			return null;

		CellType type = cell.getCellType();
		if (type == CellType.FORMULA) {
			// Evaluate the formula first
			CellValue cv = evaluator.evaluate(cell);
			if (cv == null)
				return null;
			switch (cv.getCellType()) {
			case NUMERIC:
				// Convert numeric to text to avoid double precision issues
				String precise = NumberToTextConverter.toText(cv.getNumberValue());
				return toBigDecimal(precise);
			case STRING:
				return toBigDecimal(cv.getStringValue());
			default:
				return null;
			}
		}

		switch (type) {
		case NUMERIC:
			// Use NumberToTextConverter to keep exact value (no double rounding)
			String precise = NumberToTextConverter.toText(cell.getNumericCellValue());
			return toBigDecimal(precise);
		case STRING:
			return toBigDecimal(cell.getStringCellValue());
		case BLANK:
		case BOOLEAN:
		case ERROR:
		default:
			return null;
		}
	}

	/**
	 * Convert a user-facing string like "1,234.50", "$99.99", "10%" into
	 * BigDecimal.
	 */
	private BigDecimal toBigDecimal(String raw) {
		if (raw == null)
			return null;
		String s = raw.trim();
		if (s.isEmpty())
			return null;

		boolean isPercent = s.endsWith("%");
		// Remove percent sign early; we’ll divide by 100 later if needed
		if (isPercent)
			s = s.substring(0, s.length() - 1);

		// Handle negatives with parentheses: (1,234.56)
		boolean parenNegative = s.startsWith("(") && s.endsWith(")");
		if (parenNegative)
			s = s.substring(1, s.length() - 1);

		// Strip common currency symbols and spaces
		s = s.replaceAll("[,$€£₹\\s]", "");

		if (s.isEmpty())
			return null;

		BigDecimal bd = new BigDecimal(s);
		if (parenNegative)
			bd = bd.negate();
		if (isPercent)
			bd = bd.divide(BigDecimal.valueOf(100), Math.max(2, bd.scale() + 2), RoundingMode.HALF_UP);

		return bd;
	}

	private static final ObjectMapper MAPPER = new ObjectMapper();

	// Write a single string as JSON (=> "Warning")
	private String asJsonString(String v) {
		if (v == null || v.isBlank())
			return null;
		try {
			return MAPPER.writeValueAsString(v.trim());
		} catch (Exception e) {
			throw new IllegalArgumentException("Bad ghsSignalWord", e);
		}
	}

	// Turn "GHS02,GHS07" or "GHS02; GHS07" into ["GHS02","GHS07"]
	private String csvToJsonArray(String csv) {
		if (csv == null || csv.isBlank())
			return null;
		var parts = java.util.Arrays.stream(csv.split("[,;]")).map(String::trim).filter(s -> !s.isEmpty()).toList();
		try {
			return MAPPER.writeValueAsString(parts);
		} catch (Exception e) {
			throw new IllegalArgumentException("Bad ghsSymbols", e);
		}
	}

	private String getCellString(Cell cell) {
		if (cell == null)
			return null;
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
			} else {
				return String.valueOf((long) cell.getNumericCellValue());
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		default:
			return null;
		}
	}

	private Date getCellDate(Cell cell) {
		if (cell == null)
			return null;
		if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
			return cell.getDateCellValue();
		}
		if (cell.getCellType() == CellType.STRING) {
			try {
				return new SimpleDateFormat("yyyy-MM-dd").parse(cell.getStringCellValue());
			} catch (Exception e) {
				return null; // fallback if invalid date format
			}
		}
		return null;
	}
}
