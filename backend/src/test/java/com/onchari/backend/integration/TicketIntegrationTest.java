package com.onchari.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onchari.backend.model.dto.TicketRequest;
import com.onchari.backend.model.dto.UpdateTicketRequest;
import com.onchari.backend.model.entity.Ticket;
import com.onchari.backend.model.entity.enums.Priority;
import com.onchari.backend.model.entity.enums.Status;
import com.onchari.backend.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TicketIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketRepository ticketRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();

        Ticket t1 = Ticket.builder()
                .id(UUID.randomUUID())
                .title("Network Outage")
                .description("Office internet down")
                .status(Status.OPEN)
                .priority(Priority.HIGH)
                .build();

        Ticket t2 = Ticket.builder()
                .id(UUID.randomUUID())
                .title("Printer Jam")
                .description("Floor 2 printer stuck")
                .status(Status.CLOSED)
                .priority(Priority.LOW)
                .build();

        ticketRepository.save(t1);
        ticketRepository.save(t2);
    }

    @Test
    void shouldCreateTicketSuccessfully() throws Exception {
        TicketRequest request = TicketRequest.builder()
                .title("Database Connection Timeout")
                .description("App failing to connect to primary read replica under load")
                .status(Status.OPEN)
                .priority(Priority.HIGH)
                .build();

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is("Database Connection Timeout")))
                .andExpect(jsonPath("$.description", is("App failing to connect to primary read replica under load")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.priority", is("HIGH")));
    }

    @Test
    void shouldUpdateTicketSuccessfully() throws Exception {
        Ticket existingTicket = Ticket.builder()
                .id(UUID.randomUUID())
                .title("Original Title")
                .description("Original Description")
                .status(Status.OPEN)
                .priority(Priority.LOW)
                .build();

        Ticket savedTicket = ticketRepository.save(existingTicket);

        UpdateTicketRequest updateRequest = new UpdateTicketRequest(
                "Updated Ticket Title",
                "Updated description details",
                Status.IN_PROGRESS,
                Priority.HIGH
        );

        mockMvc.perform(patch("/api/v1/tickets/{id}", savedTicket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedTicket.getId().toString())))
                .andExpect(jsonPath("$.title", is("Updated Ticket Title")))
                .andExpect(jsonPath("$.description", is("Updated description details")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.priority", is("HIGH")));
    }

    @Test
    void shouldReturnPaginatedTicketsSuccessfully() throws Exception {

        mockMvc.perform(get("/api/v1/tickets")
                        .param("page", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.page.totalElements", is(2)))
                .andExpect(jsonPath("$.content[0].title", notNullValue()));
    }

    @Test
    void shouldFilterTicketsByStatusSuccessfully() throws Exception {

        mockMvc.perform(get("/api/v1/tickets")
                        .param("status", "OPEN")
                        .param("page", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status", is("OPEN")))
                .andExpect(jsonPath("$.content[0].title", is("Network Outage")));
    }

    @Test
    void shouldReturnTicketStatsSuccessfully() throws Exception {
        mockMvc.perform(get("/api/v1/tickets/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @Test
    void shouldDeleteTicketSuccessfully() throws Exception {
        Ticket existingTicket = Ticket.builder()
                .id(UUID.fromString("320230c7-6568-4780-a6ca-89c276485ee5"))
                .title("Ticket to be deleted")
                .description("This ticket will be removed")
                .status(Status.OPEN)
                .priority(Priority.LOW)
                .build();

        Ticket savedTicket = ticketRepository.save(existingTicket);

        mockMvc.perform(delete("/api/v1/tickets/{id}", savedTicket.getId()))
                .andExpect(status().isNoContent());

        assert(ticketRepository.findById(savedTicket.getId()).isEmpty());
    }

    @Test
    void shouldReturnExceptionWhenDeletingNonExistentTicket() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/tickets/{id}", randomId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail").exists());
    }
}
