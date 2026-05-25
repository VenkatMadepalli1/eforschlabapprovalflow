package com.eforsch.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eforsch.dto.ApproveShareRequestDTO;
import com.eforsch.dto.GetMyRequestsPayloadDTO;
import com.eforsch.dto.GroupSharingRequestDTO;
import com.eforsch.dto.MarkAsReceivedRequest;
import com.eforsch.dto.RejectShareRequestDTO;
import com.eforsch.dto.UnshareItemRequest;
import com.eforsch.dto.ShareProductWithTimeSlotsDTO;
import com.eforsch.dto.SharedItemsRequestListResponse;
import com.eforsch.dto.TimeSlotDTO;
import com.eforsch.dto.User;
import com.eforsch.service.SharedInventoryService;
import com.eforsch.util.ErrorResponse;
import com.eforsch.util.ShareInventoryVO;
import com.eforsch.util.SuccessResponse;

@RestController
@RequestMapping("/api/share")
public class GroupSharingController {

    @Autowired
    private SharedInventoryService sharedInventoryService;

    @PostMapping("/request")
    public ResponseEntity<?> createGroupSharingRequest(@RequestBody GroupSharingRequestDTO requestDTO) {
        try {
            Map<String, Object> result = sharedInventoryService.createGroupSharingRequest(requestDTO);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorCode", "SERVER_ERROR");
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    
    @PostMapping("/getSharedProductList")
    public ResponseEntity<?> getSharedProductList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody User user) {

        try {
            Map<String, Object> productData = sharedInventoryService.getProductList(page, size, true, user);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Data fetched successfully");
            response.put("data", Map.of(
                    "columns", List.of(
                            Map.of("key", "productId", "label", "ID", "sortable", true),
                            Map.of("key", "productName", "label", "Product Name", "sortable", true, "filterable", true),
                            Map.of("key", "catalogue", "label", "Catalogue No", "filterable", true),
                            Map.of("key", "companyName", "label", "Company Name", "filterable", true),
                            Map.of("key", "quantity", "label", "Quantity", "sortable", true),
                            Map.of("key", "expiryDate", "label", "Expiry Date", "filterable", true),  
                            Map.of("key", "remark", "label", "Remark", "filterable", true),
                            Map.of("key", "addedBy", "label", "Shared By", "sortable", true)
                            
                    ),
                    "list", productData.get("list"),
                    "pagination", productData.get("pagination")
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ErrorResponse("error", "Unauthorized. Invalid or missing token.", 401));
        }
    }
    
    @PostMapping("/getAllSharedProductList")
    public ResponseEntity<?> getAllSharedProductList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Map<String, Object> productData = sharedInventoryService.getAllProductList(page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Data fetched successfully");
            response.put("data", Map.of(
                    "columns", List.of(
                            Map.of("key", "productId", "label", "ID", "sortable", true),
                            Map.of("key", "productName", "label", "Product Name", "sortable", true, "filterable", true),
                            Map.of("key", "catalogue", "label", "Catalogue No", "filterable", true),
                            Map.of("key", "companyName", "label", "Company Name", "filterable", true),
                            Map.of("key", "quantity", "label", "Quantity", "sortable", true),
                            Map.of("key", "expiryDate", "label", "Expiry Date", "filterable", true),  
                            Map.of("key", "remark", "label", "Remark", "filterable", true),
                            Map.of("key", "addedBy", "label", "Shared By", "sortable", true)
                            
                    ),
                    "list", productData.get("list"),
                    "pagination", productData.get("pagination")
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("error", "An error occurred: " + e.getMessage(), 500));
        }
    }
    
   
    
    @PostMapping("/shareProduct")
    public ResponseEntity<?> shareProduct(@RequestBody ShareProductWithTimeSlotsDTO requestDTO) {
        ShareInventoryVO createdProduct = sharedInventoryService.shareProduct(requestDTO);
        return ResponseEntity.ok(new SuccessResponse("success", "Product shared successfully", createdProduct));
    }
    
    @DeleteMapping("/revokeShare/{productId}")
    public ResponseEntity<?> revokeShareProduct(@PathVariable String productId, @RequestParam String inventoryType) {
        try {
            sharedInventoryService.revokeShareProduct(productId, inventoryType);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product share revoked successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorCode", "SERVER_ERROR");
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/getTimeSlots/{sharedInventoryId}")
    public ResponseEntity<?> getTimeSlots(@PathVariable Long sharedInventoryId) {
        try {
            List<?> timeSlots = sharedInventoryService.getTimeSlots(sharedInventoryId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Time slots retrieved successfully");
            response.put("data", timeSlots);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorCode", "SERVER_ERROR");
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/requests/donor")
    public ResponseEntity<?> getIncomingRequestsForDonor(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody GetMyRequestsPayloadDTO payload) {
        try {
            User user = payload.getUser();
            
            // Convert filter DTO to Map for service compatibility
            Map<String, String> filters = new HashMap<>();
            if (payload.getFilters() != null && payload.getFilters().getStatus() != null) {
                filters.put("status", payload.getFilters().getStatus());
            }
            
            // Call service
            SharedItemsRequestListResponse response = sharedInventoryService.getIncomingRequestsForDonor(page, size, user, filters);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorCode", "INVALID_INPUT");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorCode", "SERVER_ERROR");
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/requests/receiver")
    public ResponseEntity<?> getMyRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody GetMyRequestsPayloadDTO payload) {
        try {
            User user = payload.getUser();
            
            // Convert filter DTO to Map for service compatibility
            Map<String, String> filters = new HashMap<>();
            if (payload.getFilters() != null && payload.getFilters().getStatus() != null) {
                filters.put("status", payload.getFilters().getStatus());
            }
            
            // Call service
            SharedItemsRequestListResponse response = sharedInventoryService.getMyRequests(page, size, user, filters);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorCode", "INVALID_INPUT");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorCode", "SERVER_ERROR");
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/request/approve")
    public ResponseEntity<?> approveShareRequest(@RequestBody ApproveShareRequestDTO approveDTO) {
        try {
            Map<String, Object> response = sharedInventoryService.approveShareRequest(approveDTO);
            
            if ((Boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorCode", "SERVER_ERROR");
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/request/reject")
    public ResponseEntity<?> rejectShareRequest(@RequestBody RejectShareRequestDTO rejectDTO) {
        try {
            Map<String, Object> response = sharedInventoryService.rejectShareRequest(rejectDTO);
            
            if ((Boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorCode", "SERVER_ERROR");
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/request/{requestId}/received")
    public ResponseEntity<?> markAsReceived(
            @PathVariable String requestId,
            @RequestBody MarkAsReceivedRequest markReceivedDTO) {
        try {
            Map<String, Object> response = sharedInventoryService.markAsReceived(requestId, markReceivedDTO);
            
            if ((Boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorCode", "SERVER_ERROR");
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/unshare")
    public ResponseEntity<?> unshareItem(@RequestBody UnshareItemRequest unshareDTO) {
        try {
            Map<String, Object> response = sharedInventoryService.unshareItem(unshareDTO);
            
            if ((Boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("errorCode", "SERVER_ERROR");
            errorResponse.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
}

