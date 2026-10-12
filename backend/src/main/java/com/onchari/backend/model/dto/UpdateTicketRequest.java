package com.onchari.backend.model.dto;


import com.onchari.backend.model.entity.enums.Priority;
import com.onchari.backend.model.entity.enums.Status;

public record UpdateTicketRequest(
        String title,

        String description,

        Status status,

        Priority priority
) {
}
