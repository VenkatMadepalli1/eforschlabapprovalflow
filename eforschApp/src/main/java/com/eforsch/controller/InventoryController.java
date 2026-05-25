package com.eforsch.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eforsch.dto.FileDownload;
import com.eforsch.dto.User;
import com.eforsch.service.InventoryService;
import com.eforsch.service.SharedInventoryService;
import com.eforsch.dto.BorrowedInventoryRequest;
import com.eforsch.util.ErrorResponse;
import com.eforsch.util.InventoryVO;
import com.eforsch.util.SuccessResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private SharedInventoryService sharedInventoryService;
    
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadExcel(@RequestParam MultipartFile file, @RequestParam String groupName) {
        try {
            inventoryService.processExcelFile(file, groupName);
            return ResponseEntity.ok("Excel file uploaded and data saved successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload Excel file: " + e.getMessage());
        }
    }
    
    
    @PostMapping("/getInventoryListOld")
    public ResponseEntity<?> getProductList1(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody User user) {

        try {
            Map<String, Object> productData = inventoryService.getProductList(page, size, false, user);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Data fetched successfully");
            response.put("data", Map.of(
                    "columns", List.of(
                            Map.of("key", "productid", "label", "ID", "sortable", true),
                            Map.of("key", "productname", "label", "Product Name", "sortable", true, "filterable", true),
                            Map.of("key", "catalogue", "label", "Catalogue No", "filterable", true),
                            Map.of("key", "companyname", "label", "Company Name", "filterable", true),
                            Map.of("key", "quantity", "label", "Quantity", "sortable", true),
                            Map.of("key", "expiryDate", "label", "Expiry Date", "filterable", true),         
                            Map.of("key", "companyInternalno", "label", "Company Internal No", "filterable", true),
                            Map.of("key", "sapmaterialno", "label", "SAP Material No", "filterable", true),
                            Map.of("key", "weightvolsubqty", "label", "Weight/Vol Sub Qty", "filterable", true),
                            Map.of("key", "budgetno", "label", "Budget No", "filterable", true),
                            Map.of("key", "orderdate", "label", "Order Date", "filterable", true),
                            Map.of("key", "concentration", "label", "Concentration", "filterable", true),
                            Map.of("key", "price", "label", "Total Price", "sortable", true, "filterable", true),
                            Map.of("key", "remarks", "label", "Remarks", "filterable", true)
                    ),
                    "list", productData.get("list"),
                    "pagination", productData.get("pagination")
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ErrorResponse("error", "Unauthorized. Invalid or missing token.", 401));
        }
    }
    
    @PostMapping("/getInventoryList")
    public ResponseEntity<?> getProductList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody User user) {

        try {
            Map<String, Object> productData = inventoryService.getProductList(page, size, false, user);

            // Build the "columns" array expected by the new (second) format.
            // NOTE: keys must match the fields present in the "list" objects.
            List<Map<String, Object>> columns = new java.util.ArrayList<>();
            columns.add(col("productId",       "Product ID",          true,  true,  null,  null));
            columns.add(col("productname",     "Product Name",        true,  true,  null,  null));
            columns.add(col("catalogue",       "Catalogue",           null,  true,  null,  null)); // keep "catalogue" (your list uses this)
            columns.add(col("companyname",     "Company Name",        null,  true,  null,  null));
            columns.add(col("quantity",        "Quantity",            true,  null,  null,  null));
            columns.add(col("groupName",       "Group Name",          null,  true,  null,  null));
            columns.add(col("companyinternalno","Company Internal No",null,  true,  null,  null));
            columns.add(col("sapmaterialno",   "SAP Material No",     null,  true,  null,  null));
            columns.add(col("weightvolsubqty", "W/V/Sub Qty",         null,  true,  null,  null));
            columns.add(col("budgetno",        "Budget No",           null,  true,  null,  null));
            columns.add(col("orderdate",       "Order Date",          null,  true,  true,  null));
            columns.add(col("expirydate",      "Expiry Date",         null,  true,  true,  null));
            columns.add(col("concentration",   "Concentration",       null,  true,  null,  null));
            columns.add(col("price",           "Price",               true,  true,  null,  null));
            columns.add(col("remarks",         "Remarks",             null,  true,  null,  null));
            columns.add(col("addedby",         "Added By",            null,  true,  null,  null));
            //columns.add(col("shared",          "Shared",              null,  true,  null,  null));
            columns.add(col("fileName",        "Attachment",          null,  true,  null,  null));
            columns.add(col("fileType",        "File Type",           null,  true,  null,  null));
            //columns.add(col("fileContent",     "File Content",        null,  null,  null,  true)); // hidden

            // Assemble the new top-level payload (no "status" or "message"; no "data" wrapper)
            Map<String, Object> payload = new java.util.LinkedHashMap<>();
            payload.put("list",       productData.get("list"));
            payload.put("pagination", productData.get("pagination"));
            payload.put("columns",    columns);

            return ResponseEntity.ok(payload);

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("error", "Unauthorized. Invalid or missing token.", 401));
        }
    }
    
    /**
     * Small helper to build a column definition.
     *
     * @param key        data key to bind
     * @param label      column label
     * @param sortable   true/false or null (omit)
     * @param filterable true/false or null (omit)
     * @param isDate     true/false or null (omit)
     * @param hidden     true/false or null (omit)
     */
    private Map<String, Object> col(String key, String label,
                                    Boolean sortable, Boolean filterable,
                                    Boolean isDate, Boolean hidden) {
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("key", key);
        m.put("label", label);
        if (Boolean.TRUE.equals(sortable))   m.put("sortable", true);
        if (Boolean.TRUE.equals(filterable)) m.put("filterable", true);
        if (Boolean.TRUE.equals(isDate))     m.put("isDate", true);
        if (Boolean.TRUE.equals(hidden))     m.put("hidden", true);
        return m;
    }
    
  
    
    @PostMapping(value ="/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(@RequestPart("inventory") String metadataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        
    	ObjectMapper objectMapper = JsonMapper.builder()
    		    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    		    .build();
    	InventoryVO inventoryVO = null;
		try {
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			inventoryVO = objectMapper.readValue(metadataJson, InventoryVO.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	
    	if (file != null && !file.isEmpty()) {
    		try {
    			inventoryVO.setFileName(file.getOriginalFilename());
    			inventoryVO.setFileType(file.getContentType());
    			inventoryVO.setFileContent(file.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    	InventoryVO createdProduct = inventoryService.addProduct(inventoryVO);
        return ResponseEntity.ok(new SuccessResponse("success", "Product Added successfully", createdProduct));
    }
    
  
    @PutMapping("/updateProduct")
    public ResponseEntity<?> updateProduct(@RequestBody InventoryVO inventoryVO) {
        try {
            InventoryVO updatedProduct = inventoryService.updateProduct(inventoryVO.getProductId(), inventoryVO);
            return ResponseEntity.ok(new SuccessResponse("success", "Product Updated successfully", updatedProduct));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("error", e.getMessage(), 400));
        }
    }

    @DeleteMapping("/deleteProduct")
    public ResponseEntity<?> deleteProduct(@RequestParam Long productId) {
        try {
            inventoryService.deleteProduct(productId);
            return ResponseEntity.ok(new SuccessResponse("success", "Product Deleted successfully", ""));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("error", e.getMessage(), 400));
        }
    }

    @GetMapping("/getProduct/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Long productId) {
        try {
            InventoryVO product = inventoryService.getProduct(productId);
            return ResponseEntity.ok(new SuccessResponse("success", "Product Retrieved successfully", product));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("error", e.getMessage(), 404));
        }
    }
    
    /**
     * Upload or replace the file for a specific product.
     * PUT /api/inventory/{id}/file
     */
    @PutMapping(value = "/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InventoryVO> uploadOrReplaceFile(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) throws Exception {

        InventoryVO vo = inventoryService.updateProductFile(id, file, /*returnFileContent*/ false);
        return ResponseEntity.ok(vo);
    }

    /**
     * (Optional) Download the file
     * GET /api/inventory/{id}/file
     */
    @GetMapping("/{id}/downloadFile")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        FileDownload fd = inventoryService.getProductFile(id);
        if (fd == null || fd.getData() == null) return ResponseEntity.notFound().build();

        ContentDisposition cd = ContentDisposition.attachment()
                .filename(fd.getFileName() == null ? "attachment" : fd.getFileName())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
                .contentType(MediaType.parseMediaType(
                        fd.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : fd.getContentType()))
                .body(fd.getData());
    }

    /**
     * (Optional) Remove file only
     * DELETE /api/inventory/{id}/file
     */
    @DeleteMapping("/{id}/file")
    public ResponseEntity<Void> removeFile(@PathVariable Long id) {
        inventoryService.deleteProductFile(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/borrowed")
    public ResponseEntity<?> getBorrowedInventory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody BorrowedInventoryRequest borrowedDTO) {
        try {
            com.eforsch.dto.SharedItemsRequestListResponse response = sharedInventoryService.getBorrowedInventory(page, size, borrowedDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorCode", "SERVER_ERROR");
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
}

