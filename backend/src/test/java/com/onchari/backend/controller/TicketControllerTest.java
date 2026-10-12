package com.onchari.backend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.onchari.backend.model.dto.TicketRequest;
import com.onchari.backend.model.dto.TicketResponse;
import com.onchari.backend.model.dto.UpdateTicketRequest;
import com.onchari.backend.model.entity.enums.Priority;
import com.onchari.backend.model.entity.enums.Status;
import com.onchari.backend.service.TicketService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TicketService ticketService;

    @Test
    void createTicket() throws Exception {
        TicketRequest request = new TicketRequest("Fix Bug", "Description of bug", Status.OPEN, Priority.HIGH);

        TicketResponse response = TicketResponse.builder()
                .id(UUID.randomUUID())
                .title("Fix Bug")
                .description("bug description")
                .status(Status.OPEN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        given(ticketService.createTicket(ArgumentMatchers.any(TicketRequest.class))).willReturn(response);

        mockMvc.perform(post("/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Fix Bug"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void getTickets() throws Exception {

        TicketResponse response = TicketResponse.builder()
                .id(UUID.randomUUID())
                .title("Fix Bug")
                .description("bug description")
                .status(Status.OPEN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        Page<TicketResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        given(ticketService.getTickets(eq(null), eq(0), eq(10))).willReturn(page);

        mockMvc.perform(get("/v1/tickets")
                        .param("page", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Fix Bug"));
    }

    @Test
    void updateTicket() throws Exception {

        UUID id = UUID.randomUUID();

        UpdateTicketRequest updateRequest = UpdateTicketRequest.
                builder()
                .title("New Title")
                .description("Description")
                .status(Status.IN_PROGRESS)
                .priority(Priority.HIGH)
                .build();

        TicketResponse response = TicketResponse.builder()
                .id(UUID.randomUUID())
                . title("New Title")
                .description("Description")
                .status(Status.IN_PROGRESS)
                .priority(Priority.HIGH)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        given(ticketService.updateTicket(eq(id), ArgumentMatchers.any(UpdateTicketRequest.class))).willReturn(response);

        mockMvc.perform(patch("/v1/tickets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void deleteTicket() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(ticketService).deleteTicket(id);

        mockMvc.perform(delete("/v1/tickets/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTicketStats() throws Exception{
        Map<String, Long> stats = Map.of("OPEN", 5L, "IN_PROGRESS", 2L, "CLOSED", 8L);

        given(ticketService.getTicketStats()).willReturn(stats);

        mockMvc.perform(get("/v1/tickets/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.OPEN").value(5))
                .andExpect(jsonPath("$.IN_PROGRESS").value(2))
                .andExpect(jsonPath("$.CLOSED").value(8));
    }
}