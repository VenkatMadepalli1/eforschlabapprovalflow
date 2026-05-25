package com.eforsch.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eforsch.dto.DeleteInventoryRequest;
import com.eforsch.dto.User;
import com.eforsch.service.ArchiveInventoryService;
import com.eforsch.util.ArchiveInventoryVO;

@RestController
@RequestMapping("/api/archive")
public class ArchiveInventoryController {

	@Autowired
	private ArchiveInventoryService archiveInventoryService;

	 @DeleteMapping("/deleteInventory")
	    public ResponseEntity<?> softDeleteInventory(
	            @RequestBody DeleteInventoryRequest request) {

	        try {
	            Map<String, Object> result = archiveInventoryService.softDeleteItem(request);
	            return ResponseEntity.ok(result);
	        } catch (Exception ex) {
	            return ResponseEntity.status(401).body(Map.of("status", "error", "message", ex.getMessage(), "code", 401));
	        }
	    }
	 
	 @DeleteMapping("/archives/soft")
	 public ResponseEntity<?> softDeleteArchiveItem(
	         @RequestBody DeleteInventoryRequest request) {

	     try {
	         Map<String, Object> response = archiveInventoryService.softDeleteFromArchive(request);
	         return ResponseEntity.ok(response);
	     } catch (RuntimeException ex) {
	         return ResponseEntity.status(401).body(Map.of(
	             "status", "error",
	             "message", ex.getMessage(),
	             "code", 401
	         ));
	     }
	 }
	 
	 @DeleteMapping("/archives/permanent")
	 public ResponseEntity<?> permanentlyDeleteArchiveItemByProductId(
	         @RequestBody DeleteInventoryRequest request) {

	     try {
	         Map<String, Object> result = archiveInventoryService.permanentDeleteFromArchiveByProductId(request);
	         return ResponseEntity.ok(result);
	     } catch (RuntimeException ex) {
	         return ResponseEntity.status(401).body(Map.of(
	             "status", "error",
	             "message", ex.getMessage(),
	             "code", 401
	         ));
	     }
	 }

	 @GetMapping("/archives")
	 public ResponseEntity<?> getArchivedItems(@RequestParam(required = true) String groupName) {

	     try {
	         List<ArchiveInventoryVO> archives = archiveInventoryService.getArchivedItemsByYear();
	         
	         // 1) list: map VO -> JSON shape (archiveId -> orderId)
	         List<Map<String, Object>> list = archives.stream().map(vo -> {
	             Map<String, Object> row = new LinkedHashMap<>();
	             row.put("orderId", vo.getArchiveId());            // <-- rename
	             row.put("productId", vo.getProductId());
	             row.put("productName", vo.getProductName());
	             row.put("catalogue", vo.getCatalogue());
	             row.put("companyName", vo.getCompanyName());
	             row.put("quantity", vo.getQuantity());
	             row.put("budgetno", vo.getBudgetno());
	             row.put("price", vo.getPrice());
	             row.put("role", vo.getRole());
	             row.put("safetydatasheet", vo.getSafetydatasheet());
	             row.put("expiryDate", vo.getExpiryDate());
	             row.put("companyinternalno", vo.getCompanyinternalno());
	             row.put("sapmaterialno", vo.getSapmaterialno());
	             row.put("weightvolsubqty", vo.getWeightvolsubqty());
	             row.put("budgetNo", vo.getBudgetNo());            // keep both variants as in sample
	             row.put("orderdate", vo.getOrderdate());
	             row.put("orderedby", vo.getOrderedby());
	             row.put("concentration", vo.getConcentration());
	             row.put("remarks", vo.getRemarks());
	             row.put("casNumber", vo.getCasNumber());
	             row.put("hazardousSubstance", vo.getHazardousSubstance());
	             row.put("cmrSubstance", vo.getCmrSubstance());
	             row.put("skinResorptive", vo.getSkinResorptive());
	             row.put("inventoryType", vo.getInventoryType());
	             row.put("ghsSymbols", vo.getGhsSymbols());
	             row.put("ghsCheckbox", vo.getGhsCheckbox());
	             row.put("hPhrases", vo.gethPhrases());
	             row.put("pPhrases", vo.getpPhrases());
	             row.put("substitutionCheck", vo.getSubstitutionCheck());
	             row.put("storageLocation", vo.getStorageLocation());
	             row.put("adminApproved", Boolean.TRUE.equals(vo.getAdminApproved()));
	             row.put("labApproved", Boolean.TRUE.equals(vo.getLabApproved()));
	             row.put("adminApprovalStatusDate", vo.getAdminApprovalStatusDate());
	             row.put("labApprovalStatusDate", vo.getLabApprovalStatusDate());
	             row.put("adminName", vo.getAdminName());
	             row.put("userName", vo.getUserName());
	             row.put("status", vo.getStatus());
	             row.put("createdAt", vo.getCreatedAt());
	             row.put("updatedAt", vo.getUpdatedAt());
	             row.put("createdBy", vo.getCreatedBy());
	             row.put("updatedBy", vo.getUpdatedBy());
	             row.put("groupName", vo.getGroupName());
	             return row;
	         }).collect(Collectors.toList());

	         // 2) pagination (adjust if you have real paging)
	         Map<String, Object> pagination = new LinkedHashMap<>();
	         pagination.put("currentPage", 1);
	         pagination.put("totalRecords", list.size());
	         pagination.put("totalPages", 1);
	         pagination.put("pageSize", 100);

	         // 3) columns (exactly like your sample; avoid duplicates)
	         List<Map<String, Object>> columns = new ArrayList<>();
	         columns.add(Map.of("key", "orderId", "sortable", true, "label", "ID"));
	         columns.add(Map.of("key", "productName", "sortable", true, "label", "Product Name", "filterable", true));
	         columns.add(Map.of("key", "inventoryType", "sortable", true, "label", "Inventory Type", "filterable", true));
	         columns.add(Map.of("key", "expiryDate", "sortable", true, "label", "Expiry Date", "filterable", true));
	         columns.add(Map.of("key", "companyInternalNo", "label", "Company Internal No", "filterable", true));
	         columns.add(Map.of("key", "sapMaterialNo", "label", "SAP Material No", "filterable", true));
	         columns.add(Map.of("key", "weightVolSubQty", "label", "Weight/Vol Sub Qty", "filterable", true));
	         columns.add(Map.of("key", "orderDate", "sortable", true, "label", "Order Date", "filterable", true));
	         columns.add(Map.of("key", "orderedBy", "label", "Ordered By", "filterable", true));
	         columns.add(Map.of("key", "createdBy", "label", "Added By", "filterable", true));
	         columns.add(Map.of("key", "concentration", "label", "Concentration", "filterable", true));
	         columns.add(Map.of("key", "adminApproved", "label", "GL Approved", "filterable", true));
	         columns.add(Map.of("key", "labApproved", "label", "LM Approved", "filterable", true));
	         columns.add(Map.of("key", "catalogue", "label", "Catalogue", "filterable", true));
	         columns.add(Map.of("key", "companyName", "label", "Company Name", "filterable", true));
	         columns.add(Map.of("key", "quantity", "sortable", true, "label", "Quantity"));
	         columns.add(Map.of("key", "budgetno", "label", "Budget", "filterable", true));
	         columns.add(Map.of("key", "price", "label", "Price", "filterable", true));
	         columns.add(Map.of("key", "approvalStatusDate", "label", "Date approved", "filterable", true));
	         columns.add(Map.of("key", "adminName", "label", "Admin Name", "filterable", true));
	         columns.add(Map.of("key", "userName", "label", "User Name", "filterable", true));
	         columns.add(Map.of("key", "status", "label", "Status", "filterable", true));
	         columns.add(Map.of("key", "fileName", "label", "File Name", "filterable", true));
	         columns.add(Map.of("key", "createdAt", "label", "Created At", "filterable", true));
	         columns.add(Map.of("key", "updatedAt", "label", "Updated At", "filterable", true));
	         columns.add(Map.of("key", "createdBy", "label", "Created By", "filterable", true));
	         columns.add(Map.of("key", "updatedBy", "label", "Updated By", "filterable", true));
	         columns.add(Map.of("key", "groupName", "label", "Group Name", "filterable", true));
	         columns.add(Map.of("key", "remarks", "label", "Remark", "filterable", true));

	         // 4) data
	         Map<String, Object> data = new LinkedHashMap<>();
	         data.put("list", list);
	         data.put("pagination", pagination);
	         data.put("columns", columns);

	         // 5) final body
	         Map<String, Object> body = new LinkedHashMap<>();
	         body.put("data", data);
	         body.put("message", "Data fetched successfully");
	         body.put("status", "success");
	         
	         return ResponseEntity.ok(body);
	         
	       
	     } catch (RuntimeException ex) {
	         return ResponseEntity.status(401).body(Map.of(
	             "status", "error",
	             "message", ex.getMessage(),
	             "code", 401
	         ));
	     }
	 }

	
}
