import apiClient from '../api/api.ts'
import type { Ticket } from "../types/Ticket.ts"

export default {
    getTickets(params?: { page?: number; size?: number; status?: string }) {
        return apiClient.get('tickets', { params })
    },
    createTicket(ticketData: Ticket) {
        return apiClient.post<Ticket>('tickets', ticketData)
    },
    updateTicket(id: number, ticketData: Ticket) {
        return apiClient.patch<Ticket>(`tickets/${id}`, ticketData)
    },
    deleteTicket(id: number) {
        return apiClient.delete(`tickets/${id}`)
    },
    getTicketsStats(){
        return apiClient.get('tickets/stats')
    }
}