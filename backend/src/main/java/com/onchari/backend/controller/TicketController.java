package com.onchari.backend.controller;

import com.onchari.backend.model.dto.TicketRequest;
import com.onchari.backend.model.dto.TicketResponse;
import com.onchari.backend.model.dto.UpdateTicketRequest;
import com.onchari.backend.model.entity.enums.Status;
import com.onchari.backend.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;


    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest ticketRequest){
        TicketResponse ticketResponse = ticketService.createTicket(ticketRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketResponse);
    }

    @GetMapping
    public ResponseEntity<Page<TicketResponse>> listTickets(
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<TicketResponse> tickets = ticketService.getTickets(status, page, limit);
        return ResponseEntity.ok(tickets);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable UUID id,
            @RequestBody UpdateTicketRequest updateRequest) {
        TicketResponse updated = ticketService.updateTicket(id, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id){
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getTicketStats() {
        Map<String, Long> stats = ticketService.getTicketStats();
        return ResponseEntity.ok(stats);
    }
}
