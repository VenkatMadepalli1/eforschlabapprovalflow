package com.eforsch.controller;

import java.io.IOException;

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
import com.eforsch.dto.FineChemicalInventoryVO;
import com.eforsch.dto.User;

import com.eforsch.service.FineChemicalService;
import com.eforsch.util.InventoryVO;
import com.eforsch.util.SuccessResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/finechemical")
public class FineChemicalController {

	@Autowired
	private FineChemicalService fineChemicalService;

	@PostMapping(path = "/getFineChemicalInventory", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch Fine Chemical Inventory List")
	public ResponseEntity<?> getFineChemicalInventory(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String order,
			 @RequestBody User user) {

		return ResponseEntity.ok(fineChemicalService.getFineChemicalInventory(page, limit, sortBy, order, user));
	}
	
	@PostMapping(path = "/getFineChemicalInventoryByGroupName", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch Fine Chemical Inventory List")
	public ResponseEntity<?> getFineChemicalInventoryByGroupName(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String order,
			 @RequestBody User user) {

		return ResponseEntity.ok(fineChemicalService.getFineChemicalInventoryByGroupName(page, limit, sortBy, order, user));
	}
	
	

	@GetMapping(path = "/getFineChemicalById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch Fine Chemical Inventory by ID")
	public ResponseEntity<?> getFineChemicalById(@PathVariable Long id) {

		return ResponseEntity.ok(fineChemicalService.getFineChemicalById(id));
	}

	 @PostMapping(value ="/addFineChemical", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	    public ResponseEntity<?> addFineChemical(@RequestPart("finechemical") String metadataJson,
	            @RequestPart(value = "file", required = false) MultipartFile file) {
	        
		 ObjectMapper objectMapper = JsonMapper.builder()
	    		    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
	    		    .build();
	    	FineChemicalInventoryVO inventoryVO = null;
			try {
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				inventoryVO = objectMapper.readValue(metadataJson, FineChemicalInventoryVO.class);
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
	    	FineChemicalInventoryVO createdProduct = fineChemicalService.addFineChemical(inventoryVO);
	        return ResponseEntity.ok(new SuccessResponse("success", "Product Added successfully", createdProduct));
	    }
	
	@PutMapping("/updateFineChemical/{id}")
	@Operation(summary = "Update Fine Chemical Inventory by ID")
	public ResponseEntity<?> updateFineChemical(@PathVariable Long id, @RequestBody FineChemicalInventoryVO input) {

		return ResponseEntity.ok(fineChemicalService.updateFineChemical(id, input));
	}

	
	 @PostMapping(value = "/uploadFineChemicals", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	    public ResponseEntity<String> uploadFineChemicals(@RequestParam MultipartFile file, @RequestParam String groupName) {
	        try {
	        	fineChemicalService.processExcelFile(file, groupName);
	            return ResponseEntity.ok("Excel file uploaded and data saved successfully!");
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body("Failed to upload Excel file: " + e.getMessage());
	        }
	    }
	

	@DeleteMapping("/deletefinneChemical")
	@Operation(summary = "Delete Fine Chemical Inventory by Product ID")
	public ResponseEntity<?> deleteFineChemical(@RequestParam Long productId) {

		return ResponseEntity.ok(fineChemicalService.deleteFineChemical(productId));
	}
	
	
	  /**
     * Upload or replace the file for a specific product.
     * PUT /api/inventory/{id}/file
     */
    @PutMapping(value = "/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FineChemicalInventoryVO> uploadOrReplaceFile(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) throws Exception {

        FineChemicalInventoryVO vo = fineChemicalService.updateProductFile(id, file, /*returnFileContent*/ false);
        return ResponseEntity.ok(vo);
    }

    /**
     * (Optional) Download the file
     * GET /api/inventory/{id}/file
     */
    @GetMapping("/{id}/downloadFile")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        FileDownload fd = fineChemicalService.getProductFile(id);
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
    	fineChemicalService.deleteProductFile(id);
        return ResponseEntity.noContent().build();
    }
	
	
	
}
