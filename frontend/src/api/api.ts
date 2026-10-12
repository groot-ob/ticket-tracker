import axios from 'axios'
import type { Ticket } from "../types/Ticket.ts"

const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
})

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