package com.eforsch.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

import com.eforsch.dto.User;
import com.eforsch.entity.Order;
import com.eforsch.service.OrderService;
import com.eforsch.util.ErrorResponse;
import com.eforsch.util.OrderConverter;
import com.eforsch.util.OrderVO;
import com.eforsch.util.SuccessResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/getOrdersList")
    public ResponseEntity<?> getOrdersList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "10") int id
          ) {

        try {
            Map<String, Object> ordersData = orderService.getOrdersList(page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Data fetched successfully");
            response.put("data", Map.of(
                            	    "columns", List.of(
                            	        Map.of("key", "orderId", "label", "ID", "sortable", true),
                            	        Map.of("key", "productName", "label", "Product Name", "sortable", true, "filterable", true),
                            	        Map.of("key", "inventoryType", "label", "Inventory Type", "sortable", true, "filterable", true),
                            	        Map.of("key", "catalogue", "label", "Catalogue No", "filterable", true),
                            	        Map.of("key", "expiryDate",       "label", "Expiry Date",        "sortable", true,  "filterable", true),
                            	        Map.of("key", "companyInternalNo","label", "Company Internal No", "filterable", true),
                            	        Map.of("key", "sapMaterialNo",    "label", "SAP Material No",     "filterable", true),
                            	        Map.of("key", "weightVolSubQty",  "label", "Weight/Vol Sub Qty",  "filterable", true),
                            	        Map.of("key", "orderDate",        "label", "Order Date",          "sortable", true,  "filterable", true),
                            	        Map.of("key", "orderedBy",        "label", "Ordered By",          "filterable", true),
                            	        Map.of("key", "createdBy",        "label", "Added By",            "filterable", true),
                            	        Map.of("key", "concentration",    "label", "Concentration",       "filterable", true),
                            	        Map.of("key", "adminApproved",         "label", "GL Approved",  "filterable", true),
                            	        Map.of("key", "labApproved",         "label", "LM Approved",  "filterable", true),
                            	        Map.of("key", "companyName", "label", "Company Name", "filterable", true),
                            	        Map.of("key", "quantity", "label", "Quantity", "sortable", true),
                            	        Map.of("key", "budgetno", "label", "Budget", "filterable", true),
                            	        Map.of("key", "price", "label", "Price", "filterable", true),
                            	        Map.of("key", "approvalStatusDate", "label", "Date approved", "filterable", true),
                            	        Map.of("key", "adminName", "label", "Admin Name", "filterable", true),
                            	        Map.of("key", "userName", "label", "User Name", "filterable", true),
                            	        Map.of("key", "status", "label", "Status", "filterable", true),
                            	        Map.of("key", "fileName", "label", "File Name", "filterable", true),
                            	        Map.of("key", "createdAt", "label", "Created At", "filterable", true),
                            	        Map.of("key", "updatedAt", "label", "Updated At", "filterable", true),
                            	        Map.of("key", "updatedBy", "label", "Updated By", "filterable", true),
                            	        Map.of("key", "groupName", "label", "Group Name", "filterable", true),
                            	        Map.of("key", "remarks", "label", "Remark", "filterable", true),
                            	        Map.of("key", "productId",         "label", "Product ID",          "filterable", true),
                            	        Map.of("key", "casNumber",         "label", "CAS Number",          "filterable", true),
                            	        Map.of("key", "hazardousSubstance","label", "Hazardous Substance", "filterable", true),
                            	        Map.of("key", "cmrSubstance",      "label", "CMR Substance",       "filterable", true),
                            	        Map.of("key", "skinResorptive",    "label", "Skin Resorptive",     "filterable", true),
                            	        Map.of("key", "ghsSymbols",        "label", "GHS Symbols",         "filterable", true),
                            	        Map.of("key", "ghsCheckbox",       "label", "GHS Checkbox",        "filterable", true),
                            	        Map.of("key", "hPhrases",          "label", "H-Phrases",           "filterable", true),
                            	        Map.of("key", "pPhrases",          "label", "P-Phrases",           "filterable", true),
                            	        Map.of("key", "substitutionCheck", "label", "Substitution Check",  "filterable", true),
                            	        Map.of("key", "storageLocation",   "label", "Storage Location",    "filterable", true),
                            	        Map.of("key", "attachment",       "label", "Attachment",          "filterable", false)
                            	        
                    ),
                    "list", ordersData.get("list"),
                    "pagination", ordersData.get("pagination")
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) { 
            return ResponseEntity.status(401).body(new ErrorResponse("error", "Unauthorized. Invalid or missing token.", 401));
        }
    }
    
    @GetMapping("/getOrdersListByGroupName")
    public ResponseEntity<?> getOrdersListByGroupName(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "10") int id,
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String role,
            @RequestParam String groupName) {

        try {
            Map<String, Object> ordersData = orderService.getOrdersListByGroupName(page, size, groupName, role);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Data fetched successfully");
            response.put("data", Map.of(
            	    "columns", List.of(
            	    		 Map.of("key", "orderId", "label", "ID", "sortable", true),
                 	        Map.of("key", "productName", "label", "Product Name", "sortable", true, "filterable", true),
                 	        Map.of("key", "inventoryType", "label", "Inventory Type", "sortable", true, "filterable", true),
                 	       Map.of("key", "catalogue", "label", "Catalogue No", "filterable", true),
                 	        Map.of("key", "expiryDate",       "label", "Expiry Date",        "sortable", true,  "filterable", true),
                 	        Map.of("key", "companyInternalNo","label", "Company Internal No", "filterable", true),
                 	        Map.of("key", "sapMaterialNo",    "label", "SAP Material No",     "filterable", true),
                 	        Map.of("key", "weightVolSubQty",  "label", "Weight/Vol Sub Qty",  "filterable", true),
                 	        Map.of("key", "orderDate",        "label", "Order Date",          "sortable", true,  "filterable", true),
                 	        Map.of("key", "orderedBy",        "label", "Ordered By",          "filterable", true),
                 	        Map.of("key", "createdBy",        "label", "Added By",            "filterable", true),
                 	        Map.of("key", "concentration",    "label", "Concentration",       "filterable", true),
                 	        Map.of("key", "companyName", "label", "Company Name", "filterable", true),
                 	        Map.of("key", "quantity", "label", "Quantity", "sortable", true),
                 	        Map.of("key", "budgetno", "label", "Budget", "filterable", true),
                 	        Map.of("key", "price", "label", "Price", "filterable", true),
                 	        Map.of("key", "adminApproved",         "label", "GL Approved",  "filterable", true),
                 	        Map.of("key", "labApproved",         "label", "LM Approved",  "filterable", true),
                 	        Map.of("key", "approvalStatusDate", "label", "Date approved", "filterable", true),
                 	        Map.of("key", "adminName", "label", "Admin Name", "filterable", true),
                 	        Map.of("key", "userName", "label", "User Name", "filterable", true),
                 	        Map.of("key", "status", "label", "Status", "filterable", true),
                 	        Map.of("key", "fileName", "label", "File Name", "filterable", true),
                 	        Map.of("key", "createdAt", "label", "Created At", "filterable", true),
                 	        Map.of("key", "updatedAt", "label", "Updated At", "filterable", true),
                 	        Map.of("key", "updatedBy", "label", "Updated By", "filterable", true),
                 	        Map.of("key", "groupName", "label", "Group Name", "filterable", true),
                 	        Map.of("key", "remarks", "label", "Remark", "filterable", true),
                 	        Map.of("key", "productId",         "label", "Product ID",          "filterable", true),
                 	       Map.of("key", "casNumber",         "label", "CAS Number",          "filterable", true),
                 	       Map.of("key", "hazardousSubstance","label", "Hazardous Substance", "filterable", true),
                 	       Map.of("key", "cmrSubstance",      "label", "CMR Substance",       "filterable", true),
                 	       Map.of("key", "skinResorptive",    "label", "Skin Resorptive",     "filterable", true),
                 	       Map.of("key", "ghsSymbols",        "label", "GHS Symbols",         "filterable", true),
                 	       Map.of("key", "ghsCheckbox",       "label", "GHS Checkbox",        "filterable", true),
                 	       Map.of("key", "hPhrases",          "label", "H-Phrases",           "filterable", true),
                 	       Map.of("key", "pPhrases",          "label", "P-Phrases",           "filterable", true),
                 	       Map.of("key", "substitutionCheck", "label", "Substitution Check",  "filterable", true),
                 	       Map.of("key", "storageLocation",   "label", "Storage Location",    "filterable", true),
                 	      Map.of("key", "attachment",       "label", "Attachment",          "filterable", false)
            	    		),
                    "list", ordersData.get("list"),
                    "pagination", ordersData.get("pagination")
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ErrorResponse("error", "Unauthorized. Invalid or missing token.", 401));
        }
    }
    
    
    @GetMapping("/ordered/{orderId}")
    public ResponseEntity<?> getOrdersListByOrdered(@PathVariable Long orderId, 
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "10") int id,
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String role,
            @RequestParam(required = false) String groupName) {

        try {
        	
        	Order modifyOrder = orderService.getOrderById(orderId);
        	modifyOrder.setStatus("ordered");
        	OrderVO modifyOrderVO = OrderConverter.fromEntityToVO(modifyOrder);
        	orderService.modifyOrder(modifyOrderVO);
        	
        	
            Map<String, Object> ordersData = orderService.getOrdersListByStatus(page, size, "ordered");
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Data fetched successfully");
            response.put("data", Map.of(
            	    "columns", List.of(
            	    		 Map.of("key", "orderId", "label", "ID", "sortable", true),
                 	        Map.of("key", "productName", "label", "Product Name", "sortable", true, "filterable", true),
                 	        Map.of("key", "inventoryType", "label", "Inventory Type", "sortable", true, "filterable", true),
                 	       Map.of("key", "catalogue", "label", "Catalogue No", "filterable", true),
                 	        Map.of("key", "expiryDate",       "label", "Expiry Date",        "sortable", true,  "filterable", true),
                 	        Map.of("key", "companyInternalNo","label", "Company Internal No", "filterable", true),
                 	        Map.of("key", "sapMaterialNo",    "label", "SAP Material No",     "filterable", true),
                 	        Map.of("key", "weightVolSubQty",  "label", "Weight/Vol Sub Qty",  "filterable", true),
                 	        Map.of("key", "orderDate",        "label", "Order Date",          "sortable", true,  "filterable", true),
                 	        Map.of("key", "orderedBy",        "label", "Ordered By",          "filterable", true),
                 	        Map.of("key", "createdBy",        "label", "Added By",            "filterable", true),
                 	        Map.of("key", "concentration",    "label", "Concentration",       "filterable", true),
                 	        Map.of("key", "adminApproved",         "label", "GL Approved",  "filterable", true),
                 	        Map.of("key", "labApproved",         "label", "LM Approved",  "filterable", true),
                 	        Map.of("key", "companyName", "label", "Company Name", "filterable", true),
                 	        Map.of("key", "quantity", "label", "Quantity", "sortable", true),
                 	        Map.of("key", "budgetno", "label", "Budget", "filterable", true),
                 	        Map.of("key", "price", "label", "Price", "filterable", true),
                 	        Map.of("key", "approvalStatusDate", "label", "Date approved", "filterable", true),
                 	        Map.of("key", "adminName", "label", "Admin Name", "filterable", true),
                 	        Map.of("key", "userName", "label", "User Name", "filterable", true),
                 	        Map.of("key", "status", "label", "Status", "filterable", true),
                 	        Map.of("key", "fileName", "label", "File Name", "filterable", true),
                 	        Map.of("key", "createdAt", "label", "Created At", "filterable", true),
                 	        Map.of("key", "updatedAt", "label", "Updated At", "filterable", true),
                 	        Map.of("key", "updatedBy", "label", "Updated By", "filterable", true),
                 	        Map.of("key", "groupName", "label", "Group Name", "filterable", true),
                 	        Map.of("key", "remarks", "label", "Remark", "filterable", true),
                 	       Map.of("key", "productId",         "label", "Product ID",          "filterable", true),
                 	      Map.of("key", "casNumber",         "label", "CAS Number",          "filterable", true),
                 	      Map.of("key", "hazardousSubstance","label", "Hazardous Substance", "filterable", true),
                 	      Map.of("key", "cmrSubstance",      "label", "CMR Substance",       "filterable", true),
                 	      Map.of("key", "skinResorptive",    "label", "Skin Resorptive",     "filterable", true),
                 	      Map.of("key", "ghsSymbols",        "label", "GHS Symbols",         "filterable", true),
                 	      Map.of("key", "ghsCheckbox",       "label", "GHS Checkbox",        "filterable", true),
                 	      Map.of("key", "hPhrases",          "label", "H-Phrases",           "filterable", true),
                 	      Map.of("key", "pPhrases",          "label", "P-Phrases",           "filterable", true),
                 	      Map.of("key", "substitutionCheck", "label", "Substitution Check",  "filterable", true),
                 	      Map.of("key", "storageLocation",   "label", "Storage Location",    "filterable", true),
                 	     Map.of("key", "attachment",       "label", "Attachment",          "filterable", false)
            	    		),
                    "list", ordersData.get("list"),
                    "pagination", ordersData.get("pagination")
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ErrorResponse("error", "Unauthorized. Invalid or missing token.", 401));
        }
    }
    
    @GetMapping("/delivered/{orderId}")
    public ResponseEntity<?> getOrdersListByDelivered(@PathVariable Long orderId, 
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "10") int id,
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String role,
            @RequestParam(required = false) String groupName) {

        try {
        	
        	Order modifyOrder = orderService.getOrderById(orderId);
        	modifyOrder.setStatus("delivered");
        	OrderVO modifyOrderVO = OrderConverter.fromEntityToVO(modifyOrder);
        	orderService.modifyOrder(modifyOrderVO);
        	
        	
            Map<String, Object> ordersData = orderService.getOrdersListByStatus(page, size, "delivered");
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Data fetched successfully");
            response.put("data", Map.of(
            	    "columns", List.of(
            	    		 Map.of("key", "orderId", "label", "ID", "sortable", true),
                 	        Map.of("key", "productName", "label", "Product Name", "sortable", true, "filterable", true),
                 	        Map.of("key", "inventoryType", "label", "Inventory Type", "sortable", true, "filterable", true),
                 	       Map.of("key", "catalogue", "label", "Catalogue No", "filterable", true),
                 	        Map.of("key", "expiryDate",       "label", "Expiry Date",        "sortable", true,  "filterable", true),
                 	        Map.of("key", "companyInternalNo","label", "Company Internal No", "filterable", true),
                 	        Map.of("key", "sapMaterialNo",    "label", "SAP Material No",     "filterable", true),
                 	        Map.of("key", "weightVolSubQty",  "label", "Weight/Vol Sub Qty",  "filterable", true),
                 	        Map.of("key", "orderDate",        "label", "Order Date",          "sortable", true,  "filterable", true),
                 	        Map.of("key", "orderedBy",        "label", "Ordered By",          "filterable", true),
                 	        Map.of("key", "createdBy",        "label", "Added By",            "filterable", true),
                 	        Map.of("key", "concentration",    "label", "Concentration",       "filterable", true),
                 	         Map.of("key", "adminApproved",         "label", "GL Approved",  "filterable", true),
                 	        Map.of("key", "labApproved",         "label", "LM Approved",  "filterable", true),
                 	        Map.of("key", "companyName", "label", "Company Name", "filterable", true),
                 	        Map.of("key", "quantity", "label", "Quantity", "sortable", true),
                 	        Map.of("key", "budgetno", "label", "Budget", "filterable", true),
                 	        Map.of("key", "price", "label", "Price", "filterable", true),
                 	        Map.of("key", "approvalStatusDate", "label", "Date approved", "filterable", true),
                 	        Map.of("key", "adminName", "label", "Admin Name", "filterable", true),
                 	        Map.of("key", "userName", "label", "User Name", "filterable", true),
                 	        Map.of("key", "status", "label", "Status", "filterable", true),
                 	        Map.of("key", "fileName", "label", "File Name", "filterable", true),
                 	        Map.of("key", "createdAt", "label", "Created At", "filterable", true),
                 	        Map.of("key", "updatedAt", "label", "Updated At", "filterable", true),
                 	        Map.of("key", "updatedBy", "label", "Updated By", "filterable", true),
                 	        Map.of("key", "groupName", "label", "Group Name", "filterable", true),
                 	        Map.of("key", "remarks", "label", "Remark", "filterable", true),
                 	       Map.of("key", "productId",         "label", "Product ID",          "filterable", true),
                 	      Map.of("key", "casNumber",         "label", "CAS Number",          "filterable", true),
                 	      Map.of("key", "hazardousSubstance","label", "Hazardous Substance", "filterable", true),
                 	      Map.of("key", "cmrSubstance",      "label", "CMR Substance",       "filterable", true),
                 	      Map.of("key", "skinResorptive",    "label", "Skin Resorptive",     "filterable", true),
                 	      Map.of("key", "ghsSymbols",        "label", "GHS Symbols",         "filterable", true),
                 	      Map.of("key", "ghsCheckbox",       "label", "GHS Checkbox",        "filterable", true),
                 	      Map.of("key", "hPhrases",          "label", "H-Phrases",           "filterable", true),
                 	      Map.of("key", "pPhrases",          "label", "P-Phrases",           "filterable", true),
                 	      Map.of("key", "substitutionCheck", "label", "Substitution Check",  "filterable", true),
                 	      Map.of("key", "storageLocation",   "label", "Storage Location",    "filterable", true),
                 	     Map.of("key", "attachment",       "label", "Attachment",          "filterable", false)
            	    		),
                    "list", ordersData.get("list"),
                    "pagination", ordersData.get("pagination")
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ErrorResponse("error", "Unauthorized. Invalid or missing token.", 401));
        }
    }
    

    @GetMapping("/approveGroupLeader/approve/{orderId}")
    public ResponseEntity<?> approveAdmin(@PathVariable Long orderId) {
        OrderVO createdOrder = orderService.approveAdmin(orderId);
        return ResponseEntity.ok(new SuccessResponse("success", "Order is approved", createdOrder));
    }
    
    @GetMapping("/rejectGroupLeader/{orderId}")
    public ResponseEntity<?> rejectAdmin(@PathVariable Long orderId) {
        OrderVO createdOrder = orderService.rejectAdmin(orderId);
        return ResponseEntity.ok(new SuccessResponse("success", "Order is rejected", createdOrder));
    }
    
    @GetMapping("/labApprove/approve/{orderId}")
    public ResponseEntity<?> labApprove(@PathVariable Long orderId) {
        OrderVO createdOrder = orderService.labApprove(orderId);
        return ResponseEntity.ok(new SuccessResponse("success", "Order is approved", createdOrder));
    }
    
    @GetMapping("/labReject/{orderId}")
    public ResponseEntity<?> labReject(@PathVariable Long orderId) {
        OrderVO createdOrder = orderService.labReject(orderId);
        return ResponseEntity.ok(new SuccessResponse("success", "Order is rejected", createdOrder));
    }
    
    // Endpoint to download attachment
    @GetMapping("/downloadAttachment/{orderId}")
    public ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long orderId) throws IOException {
        Order order = orderService.getOrderById(orderId);
        
        // Check if attachment is null or empty
        if (order.getFileContent() == null || order.getFileContent().length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Set the PDF file in the response
        ByteArrayResource resource = new ByteArrayResource(order.getFileContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=attachment.pdf") // Provide filename
                .contentType(MediaType.APPLICATION_PDF)  // Specify media type as PDF
                .contentLength(order.getFileContent().length)
                .body(resource);
    }
    
    
    @PostMapping(value ="/addOrder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addOrder(@RequestPart("order") String metadataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        
    	ObjectMapper objectMapper = JsonMapper.builder()
    		    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    		    .build();
    	OrderVO orderVo = null;
		try {
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			orderVo = objectMapper.readValue(metadataJson, OrderVO.class);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	if (file != null && !file.isEmpty()) {
    		try {
    			orderVo.setSafetydatasheet(file.getOriginalFilename());
    			orderVo.setFileContent(file.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    	orderVo.setInventoryType("generalInventory");
    	OrderVO createdOrder = orderService.addOrder(orderVo);
        
        return ResponseEntity.ok(new SuccessResponse("success", "Order added successfully", createdOrder));
    }
    
    
    
    @PostMapping(value ="/addOrderFineChemical", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addOrderFineChemical(@RequestPart("order") String metadataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        
    	//ObjectMapper objectMapper = new ObjectMapper();
    	
    	ObjectMapper objectMapper = JsonMapper.builder()
    		    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    		    .build();
    	
    	OrderVO orderVo = null;
		try {
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			orderVo = objectMapper.readValue(metadataJson, OrderVO.class);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	if (file != null && !file.isEmpty()) {
    		try {
    			orderVo.setSafetydatasheet(file.getOriginalFilename());
    			orderVo.setFileContent(file.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    	orderVo.setInventoryType("fineChemicalInventory");
    	OrderVO createdOrder = orderService.addOrder(orderVo);
        
        return ResponseEntity.ok(new SuccessResponse("success", "Order added successfully", createdOrder));
    }
    
    

    @PutMapping("/modifyOrder")
    public ResponseEntity<?> modifyOrder(@RequestBody OrderVO updatedOrder) {
    	OrderVO modifiedOrder = orderService.modifyOrder(updatedOrder);
        return ResponseEntity.ok(new SuccessResponse("success", "Order modified successfully", modifiedOrder));
    }

    @DeleteMapping("/deleteOrder/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(new SuccessResponse("success", "Order deleted successfully", null));
    }
}
