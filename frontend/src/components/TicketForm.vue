<script setup lang="ts">
import { ref, watch } from 'vue'
import type { Ticket } from '../types/Ticket.ts'
import api from '../api/api.ts'

const props = defineProps<{
  isOpen: boolean
  ticketToEdit?: Ticket | null
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'saved'): void
}>()

const form = ref<Ticket>({
  title: '',
  description: '',
  status: 'OPEN',
  priority: 'MEDIUM',
})

const loading = ref<boolean>(false)
const errorMessage = ref<string | null>(null)

// Watch for changes to populate the form for editing
watch(() => props.ticketToEdit, (newVal) => {
  if (newVal) {
    form.value = { ...newVal }
  } else {
    form.value = { title: '', description: '', status: 'OPEN', priority: 'MEDIUM' }
  }
}, { immediate: true })

const handleSubmit = async () => {
  if (!form.value.title.trim()) {
    errorMessage.value = 'Title is required.'
    return
  }

  try {
    loading.value = true
    errorMessage.value = null

    if (form.value.id) {
      await api.updateTicket(form.value.id, form.value)
    } else {
      await api.createTicket(form.value)
    }

    emit('saved')
    emit('close')
  } catch (err) {
    errorMessage.value = 'Failed to save ticket. Please check your backend connection.'
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div v-if="isOpen" class="fixed inset-0 overflow-hidden z-50">
    <!-- Backdrop with smooth fade -->
    <div
        class="absolute inset-0 bg-slate-900/40 backdrop-blur-xs transition-opacity duration-300"
        @click="emit('close')"
    ></div>

    <div class="fixed inset-y-0 right-0 pl-10 max-w-full flex">
      <div class="w-screen max-w-md bg-white shadow-2xl flex flex-col transform transition-transform duration-300 ease-in-out">

        <!-- Header -->
        <div class="px-6 py-5 bg-slate-50 border-b border-slate-100 flex items-center justify-between">
          <div class="flex items-center space-x-3">
            <span class="p-2 bg-indigo-50 text-indigo-600 rounded-lg">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z"/>
              </svg>
            </span>
            <div>
              <h3 class="text-lg font-semibold text-slate-900">
                {{ form.id ? 'Edit Ticket' : 'Create New Ticket' }}
              </h3>
              <p class="text-xs text-slate-500">
                {{ form.id ? 'Update existing ticket details' : 'Fill out the details to create a new ticket' }}
              </p>
            </div>
          </div>
          <button
              @click="emit('close')"
              class="w-8 h-8 flex items-center justify-center rounded-full text-slate-400 hover:text-slate-600 hover:bg-slate-100 transition-colors"
          >
            &times;
          </button>
        </div>

        <!-- Body Form Container -->
        <div class="flex-1 overflow-y-auto p-6 space-y-6">

          <!-- Error Alert -->
          <div v-if="errorMessage" class="p-4 bg-red-50 text-red-700 text-sm rounded-xl border border-red-100 flex items-start space-x-3">
            <svg class="w-5 h-5 text-red-500 shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
            </svg>
            <div>{{ errorMessage }}</div>
          </div>

          <form @submit.prevent="handleSubmit" class="space-y-5">
            <!-- Title Field -->
            <div>
              <label class="block text-xs font-semibold uppercase tracking-wider text-slate-600 mb-1.5">Title <span class="text-red-500">*</span></label>
              <input
                  v-model="form.title"
                  type="text"
                  required
                  class="block w-full rounded-xl border border-slate-200 bg-slate-50/50 px-4 py-2.5 text-slate-800 placeholder-slate-400 shadow-xs focus:bg-white focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 focus:outline-none transition-all sm:text-sm"
                  placeholder="e.g. Fix authentication timeout issue"
              />
            </div>

            <!-- Description Field -->
            <div>
              <label class="block text-xs font-semibold uppercase tracking-wider text-slate-600 mb-1.5">Description</label>
              <textarea
                  v-model="form.description"
                  rows="4"
                  class="block w-full rounded-xl border border-slate-200 bg-slate-50/50 px-4 py-2.5 text-slate-800 placeholder-slate-400 shadow-xs focus:bg-white focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 focus:outline-none transition-all sm:text-sm"
                  placeholder="Provide a detailed description of the task or bug..."
              ></textarea>
            </div>

            <!-- Status and Priority Grid -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-xs font-semibold uppercase tracking-wider text-slate-600 mb-1.5">Status</label>
                <select
                    v-model="form.status"
                    class="block w-full rounded-xl border border-slate-200 bg-slate-50/50 px-3 py-2.5 text-slate-800 shadow-xs focus:bg-white focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 focus:outline-none transition-all sm:text-sm"
                >
                  <option value="OPEN">Open</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="CLOSED">Closed</option>
                </select>
              </div>

              <div>
                <label class="block text-xs font-semibold uppercase tracking-wider text-slate-600 mb-1.5">Priority</label>
                <select
                    v-model="form.priority"
                    class="block w-full rounded-xl border border-slate-200 bg-slate-50/50 px-3 py-2.5 text-slate-800 shadow-xs focus:bg-white focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20 focus:outline-none transition-all sm:text-sm"
                >
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HIGH">High</option>
                </select>
              </div>
            </div>

            <!-- Action Buttons Footer -->
            <div class="pt-6 border-t border-slate-100 flex items-center justify-end space-x-3">
              <button
                  type="button"
                  @click="emit('close')"
                  class="rounded-xl border border-slate-200 bg-white py-2.5 px-5 text-sm font-semibold text-slate-700 shadow-xs hover:bg-slate-50 transition-colors"
              >
                Cancel
              </button>
              <button
                  type="submit"
                  :disabled="loading"
                  class="inline-flex items-center justify-center rounded-xl bg-indigo-600 py-2.5 px-6 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600 disabled:opacity-50 transition-all"
              >
                <svg v-if="loading" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                {{ loading ? 'Saving...' : (form.id ? 'Save Changes' : 'Create Ticket') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>