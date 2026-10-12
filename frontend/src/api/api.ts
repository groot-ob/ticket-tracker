import axios from 'axios'
import type { Ticket } from "../types/Ticket.ts"

const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
})

export default apiClient;