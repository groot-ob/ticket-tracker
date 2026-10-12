package com.onchari.backend.service;

import com.onchari.backend.exceptions.TicketNotFoundException;
import com.onchari.backend.model.dto.TicketRequest;
import com.onchari.backend.model.dto.TicketResponse;
import com.onchari.backend.model.dto.UpdateTicketRequest;
import com.onchari.backend.model.entity.Ticket;
import com.onchari.backend.model.entity.enums.Status;
import com.onchari.backend.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketResponse createTicket(TicketRequest request) {
        var ticket = Ticket.builder()
                .id(UUID.randomUUID())
                .title(request.title())
                .description(request.description())
                .status(request.status())
                .priority(request.priority())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Ticket saved = ticketRepository.save(ticket);
        return mapToDto(saved);
    }

    public Page<TicketResponse> getTickets(Status status, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<Ticket> ticketPage;
        if (status != null && !status.name().isBlank()) {
            ticketPage = ticketRepository.findByStatus(status, pageable);
        } else {
            ticketPage = ticketRepository.findAll(pageable);
        }
        return ticketPage.map(this::mapToDto);
    }

    public TicketResponse updateTicket(UUID id, UpdateTicketRequest request) {
        log.info("updating ticket {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(String.format("Ticket with id %s not found",id)));

        Ticket updated = ticketRepository.save(
                ticket.toBuilder()
                        .title(request.title() != null ? request.title() : ticket.getTitle())
                        .description(request.description() != null ? request.description() : ticket.getDescription())
                        .status(request.status() != null ? request.status() : ticket.getStatus())
                        .priority(request.priority() != null ? request.priority() : ticket.getPriority())
                        .updatedAt(Instant.now())
                        .build()
        );
        return mapToDto(updated);
    }

    public void deleteTicket(UUID id) {
        log.info("deleting ticket {}", id);
        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException(String.format("Ticket with id %s not found",id));
        }
        ticketRepository.deleteById(id);
    }

    public Map<String, Long> getTicketStats() {
        List<Map<String, Object>> results = ticketRepository.countTicketsByStatus();
        Map<String, Long> stats = new HashMap<>();
        for (Map<String, Object> result : results) {
            Object statusObj = result.get("status");
            String status = statusObj != null ? statusObj.toString() : "UNKNOWN";
            Long count = (Long) result.get("count");
            stats.put(status, count);
        }
        return stats;
    }

    private TicketResponse mapToDto(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

}
