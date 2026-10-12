package com.onchari.backend.model.dto;

import com.onchari.backend.model.entity.enums.Priority;
import com.onchari.backend.model.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;

public record TicketRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        Status status,

        Priority priority
) {
}
