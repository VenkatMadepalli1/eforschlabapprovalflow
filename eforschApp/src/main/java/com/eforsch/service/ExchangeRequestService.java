package com.eforsch.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.eforsch.dto.GroupShareItemRequest;
import com.eforsch.dto.GroupShareItemResponse;

@Service
public class ExchangeRequestService {

    private static final int BLOCK_DURATION_DAYS = 3;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * Creates a new group share item request.
     * - Generates a requestId
     * - Blocks the item for 3 days (TEMP_BLOCKED)
     * - Notifies PD via Email + In-App
     */
    public GroupShareItemResponse createRequest(GroupShareItemRequest request) {

        // 1. Generate request ID
        String requestId = generateRequestId();

        // 2. Calculate blockedUntil (now + 3 days)
        LocalDateTime blockedUntil = LocalDateTime.now(ZoneOffset.UTC).plusDays(BLOCK_DURATION_DAYS);
        String blockedUntilStr = blockedUntil.format(FORMATTER);

        // 3. TODO: Save request to DB via repository

        // 4. TODO: Update item status to TEMP_BLOCKED in item table

        // 5. TODO: Send Email + In-App notification to PD

        // 6. Build and return response
        GroupShareItemResponse response = new GroupShareItemResponse();
        response.setRequestId(requestId);
        response.setItemId(request.getItemId());
        response.setRequestedQuantity(request.getRequestedQuantity());
        response.setStatus("PENDING_PD_RESPONSE");
        response.setBlockedUntil(blockedUntilStr);

        return response;
    }

    /**
     * Generates a unique request ID.
     * TODO: Replace with DB sequence or UUID-based generation as per your existing pattern.
     */
    private String generateRequestId() {
        long timestamp = System.currentTimeMillis();
        return "REQ-" + timestamp;
    }
}