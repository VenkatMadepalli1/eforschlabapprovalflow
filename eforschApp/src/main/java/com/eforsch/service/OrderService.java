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
}

