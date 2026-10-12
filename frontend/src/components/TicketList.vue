<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import api from '../api/api.ts'
import type { Ticket } from '../types/Ticket.ts'
import TicketForm from './TicketForm.vue'

const tickets = ref<Ticket[]>([])
const stats = ref<Record<string, number>>({})
const loading = ref<boolean>(true)
const error = ref<string | null>(null)
const isDrawerOpen = ref<boolean>(false)
const selectedTicketForEdit = ref<Ticket | null>(null)

// Pagination & Filtering state
const currentPage = ref<number>(0)
const pageSize = ref<number>(10)
const totalPages = ref<number>(0)
const selectedStatus = ref<string>('')

const fetchTickets = async () => {
  try {
    loading.value = true
    error.value = null

    const response = await api.getTickets({
      page: currentPage.value,
      size: pageSize.value,
      status: selectedStatus.value || undefined
    })
    const data = response.data as any

    if (Array.isArray(data)) {
      let filtered = data
      if (selectedStatus.value) {
        filtered = data.filter((t: Ticket) => t.status === selectedStatus.value)
      }
      tickets.value = filtered
      totalPages.value = 1
    } else {
      tickets.value = data.content || []
      totalPages.value = data.totalPages || 1
    }
  } catch (err) {
    error.value = 'Failed to load tickets from the backend.'
    console.error(err)
  } finally {
    loading.value = false
  }
}

const fetchStats = async () => {
  try {
    const response = await api.getTicketsStats()
    stats.value = response.data
  } catch (err) {
    console.error('Failed to fetch ticket stats', err)
  }
}

const refreshData = () => {
  fetchTickets()
  fetchStats()
}

const openCreateDrawer = () => {
  selectedTicketForEdit.value = null
  isDrawerOpen.value = true
}

const openEditDrawer = (ticket: Ticket) => {
  selectedTicketForEdit.value = ticket
  isDrawerOpen.value = true
}

const handleDelete = async (id?: number) => {
  if (!id) return
  if (!window.confirm('Are you sure you want to delete this ticket?')) return

  try {
    await api.deleteTicket(id)
    refreshData()
  } catch (err) {
    console.error('Failed to delete ticket', err)
    alert('Failed to delete ticket from the backend.')
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
  }
}

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
  }
}

watch(selectedStatus, () => {
  currentPage.value = 0
  fetchTickets()
})

watch(currentPage, () => {
  fetchTickets()
})

onMounted(() => {
  fetchTickets()
  fetchStats()
})
</script>

<template>
  <div class="max-w-4xl mx-auto p-6 bg-white shadow-md rounded-lg mt-8">

    <!-- Embedded Statistics Cards -->
    <div class="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-8">
      <div class="bg-slate-50 border border-slate-200 p-4 rounded-lg shadow-xs">
        <p class="text-xs font-medium text-slate-500 uppercase tracking-wider">Open</p>
        <p class="text-2xl font-bold text-blue-600 mt-1">{{ stats['OPEN'] || 0 }}</p>
      </div>
      <div class="bg-slate-50 border border-slate-200 p-4 rounded-lg shadow-xs">
        <p class="text-xs font-medium text-slate-500 uppercase tracking-wider">In Progress</p>
        <p class="text-2xl font-bold text-amber-600 mt-1">{{ stats['IN_PROGRESS'] || 0 }}</p>
      </div>

      <div class="bg-slate-50 border border-slate-200 p-4 rounded-lg shadow-xs">
        <p class="text-xs font-medium text-slate-500 uppercase tracking-wider">Closed</p>
        <p class="text-2xl font-bold text-gray-600 mt-1">{{ stats['CLOSED'] || 0 }}</p>
      </div>
    </div>

    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6 gap-4">
      <h2 class="text-2xl font-bold text-gray-800">Support Tickets</h2>

      <div class="flex items-center space-x-3 w-full sm:w-auto">
        <select
            v-model="selectedStatus"
            class="rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2 bg-white"
        >
          <option value="">All </option>
          <option value="OPEN">Open</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="CLOSED">Closed</option>
        </select>

        <button
            @click="openCreateDrawer"
            class="inline-flex items-center justify-center rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 whitespace-nowrap"
        >
          + Create Ticket
        </button>
      </div>
    </div>

    <div v-if="loading" class="text-gray-500 py-4 text-center">Loading tickets...</div>
    <div v-if="error" class="text-red-500 py-4 text-center">{{ error }}</div>

    <div v-if="!loading && !error" class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Priority</th>
          <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
        </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
        <tr v-if="tickets.length === 0">
          <td colspan="4" class="px-6 py-4 text-center text-sm text-gray-500">No tickets found.</td>
        </tr>
        <tr v-for="ticket in tickets" :key="ticket.id">
          <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ ticket.title }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                {{ ticket.status }}
              </span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-amber-100 text-amber-800">
                {{ ticket.priority }}
              </span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-3">
            <!-- Edit Pencil Icon -->
            <button
                @click="openEditDrawer(ticket)"
                class="text-indigo-600 hover:text-indigo-900 transition-colors p-1 rounded-md hover:bg-indigo-50 inline-flex items-center"
                title="Edit Ticket"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
            </button>

            <!-- Delete Trash Icon -->
            <button
                @click="handleDelete(ticket.id)"
                class="text-red-600 hover:text-red-900 transition-colors p-1 rounded-md hover:bg-red-50 inline-flex items-center"
                title="Delete Ticket"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- Pagination Controls -->
      <div class="flex items-center justify-between border-t border-gray-200 px-4 py-3 sm:px-6 mt-4">
        <div class="text-sm text-gray-700">
          Page <span class="font-medium">{{ currentPage + 1 }}</span> of <span class="font-medium">{{ totalPages || 1 }}</span>
        </div>
        <div class="flex space-x-2">
          <button
              @click="prevPage"
              :disabled="currentPage === 0"
              class="rounded-md border border-gray-300 bg-white px-3 py-1 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 disabled:opacity-50"
          >
            Previous
          </button>
          <button
              @click="nextPage"
              :disabled="currentPage >= totalPages - 1"
              class="rounded-md border border-gray-300 bg-white px-3 py-1 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 disabled:opacity-50"
          >
            Next
          </button>
        </div>
      </div>
    </div>

    <!-- Slide-over OffCanvas Form -->
    <TicketForm
        :is-open="isDrawerOpen"
        :ticket-to-edit="selectedTicketForEdit"
        @close="isDrawerOpen = false"
        @saved="refreshData"
    />
  </div>
</template>
