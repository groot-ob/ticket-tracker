package com.onchari.backend.model.dto;

import com.onchari.backend.model.entity.enums.Priority;
import com.onchari.backend.model.entity.enums.Status;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;
@Builder
public record TicketResponse(
        UUID id,
        String title,
        String description,
        Status status,
        Priority priority,
        Instant createdAt,
        Instant updatedAt
) {
}
