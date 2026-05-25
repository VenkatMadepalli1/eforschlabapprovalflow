package com.eforsch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eforsch.dto.NotificationVO;
import com.eforsch.entity.NotificationEntity;
import com.eforsch.entity.Order;
import com.eforsch.repository.NotificationRepository;
import com.eforsch.repository.OrderRepository;
import com.eforsch.util.OrderConverter;
import com.eforsch.util.OrderVO;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
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
			// Lab is the first approver — show all orders not yet lab-approved
			paginatedResult = orderRepository.findByLabApproved(false, PageRequest.of(page - 1, size));
		}else if(role.equalsIgnoreCase("admin")) {
			paginatedResult = orderRepository.findAll(PageRequest.of(page - 1, size));
		}else if(groupName != null && !groupName.isEmpty()) {
			// Group leader is the second approver — show only lab-approved orders for their group
			paginatedResult = orderRepository.findByGroupNameAndLabApproved(groupName, true, PageRequest.of(page - 1, size));
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
			
			Optional<NotificationEntity> notificationOptional = notificationRepository.findByEntityId(orderId);

			if (notificationOptional.isPresent()) {
			    NotificationEntity notificationEntity = notificationOptional.get();
			    notificationEntity.setMessage("Order #" + orderId + " has been approved by Group Leader. Ready for purchase processing.");
			    notificationEntity.setTitle("Order Approved - Ready for PO");
			    notificationEntity.setType("approval_pending");
			    notificationEntity.setRole("podept"); // Group leader is last approver, notify PO dept
			    notificationEntity.setRead(false); // Example of updating the read status
			    notificationEntity.setUpdatedAt(new java.util.Date()); // Assuming you have an updatedAt field
			    notificationRepository.save(notificationEntity);
			} else {
			    throw new RuntimeException("Notification not found for Order ID: " + orderId);
			}
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
			existingOrder = orderRepository.save(existingOrder);
			
			Optional<NotificationEntity> notificationOptional = notificationRepository.findByEntityId(orderId);

			if (notificationOptional.isPresent()) {
			    NotificationEntity notificationEntity = notificationOptional.get();
			    notificationEntity.setMessage("Order #" + orderId + " has been rejected by Group Leader.");
			    notificationEntity.setTitle("Order Rejected by Group Leader");
			    notificationEntity.setType("rejected");
			    notificationEntity.setRole("labMgmt"); // Notify scientist's group that the order was rejected by lab
			    notificationEntity.setRead(false); // Example of updating the read status
			    notificationEntity.setUpdatedAt(new java.util.Date()); // Assuming you have an updatedAt field
			    notificationRepository.save(notificationEntity);
			} else {
			    throw new RuntimeException("Notification not found for Order ID: " + orderId);
			}
			
			
			
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
			
			Optional<NotificationEntity> notificationOptional = notificationRepository.findByEntityId(orderId);

			if (notificationOptional.isPresent()) {
			    NotificationEntity notificationEntity = notificationOptional.get();
			    notificationEntity.setMessage("Order #" + orderId + " has been approved by Lab Management. Now requires Group Leader approval.");
			    notificationEntity.setTitle("Pending Group Leader Approval");
			    notificationEntity.setType("approved");
			    notificationEntity.setRole("groupleader"); // Lab is first approver, notify group leader next
			    notificationEntity.setRead(false); // Example of updating the read status
			    notificationEntity.setUpdatedAt(new java.util.Date()); // Assuming you have an updatedAt field
			    notificationRepository.save(notificationEntity);
			} else {
			    throw new RuntimeException("Notification not found for Order ID: " + orderId);
			}
			return OrderConverter.fromEntityToVO(existingOrder);
		} else {
			throw new RuntimeException("Order not found");
		}
    }
    
    public OrderVO labReject(Long orderId) {
    	Optional<Order> orderOptional = orderRepository.findById(orderId);
    	
		if (orderOptional.isPresent()) {
			Order existingOrder = orderOptional.get();
			existingOrder.setLabApproved(false);
			existingOrder = orderRepository.save(existingOrder);
			
			
			Optional<NotificationEntity> notificationOptional = notificationRepository.findByEntityId(orderId);

			if (notificationOptional.isPresent()) {
			    NotificationEntity notificationEntity = notificationOptional.get();
			    notificationEntity.setMessage("Order #" + orderId + " has been rejected by Lab Management.");
			    notificationEntity.setTitle("Lab Rejected");
			    notificationEntity.setType("rejected");
			    notificationEntity.setRole("groupleader"); // Notify scientist's group that the order was rejected by lab
			    notificationEntity.setRead(false); // Example of updating the read status
			    notificationEntity.setUpdatedAt(new java.util.Date()); // Assuming you have an updatedAt field
			    notificationRepository.save(notificationEntity);
			} else {
			    throw new RuntimeException("Notification not found for Order ID: " + orderId);
			}
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
}

