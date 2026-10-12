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

// Watch for changes in ticketToEdit to populate the form for editing
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
    errorMessage.value = 'Failed to save ticket. Please check your backend.'
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div v-if="isOpen" class="fixed inset-0 overflow-hidden z-50">
    <div class="absolute inset-0 bg-black/30 transition-opacity" @click="emit('close')"></div>

    <div class="absolute inset-y-0 right-0 pl-10 max-w-full flex">
      <div class="w-screen max-w-md bg-white shadow-xl flex flex-col">
        <div class="px-6 py-4 bg-gray-50 border-b border-gray-200 flex items-center justify-between">
          <h3 class="text-xl font-bold text-gray-800">
            {{ form.id ? 'Edit Ticket' : 'Create New Ticket' }}
          </h3>
          <button @click="emit('close')" class="text-gray-400 hover:text-gray-600 font-semibold text-lg">&times;</button>
        </div>

        <div class="flex-1 overflow-y-auto p-6">
          <div v-if="errorMessage" class="mb-4 p-3 bg-red-50 text-red-700 text-sm rounded-md border border-red-200">
            {{ errorMessage }}
          </div>

          <form @submit.prevent="handleSubmit" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">Title</label>
              <input
                  v-model="form.title"
                  type="text"
                  required
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2"
                  placeholder="Enter ticket title..."
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700">Description</label>
              <textarea
                  v-model="form.description"
                  rows="3"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2"
                  placeholder="Describe the issue..."
              ></textarea>
            </div>

            <div class="grid grid-cols-1 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700">Status</label>
                <select
                    v-model="form.status"
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2 bg-white"
                >
                  <option value="OPEN">Open</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="RESOLVED">Resolved</option>
                  <option value="CLOSED">Closed</option>
                </select>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700">Priority</label>
                <select
                    v-model="form.priority"
                    class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm border p-2 bg-white"
                >
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HIGH">High</option>
                  <option value="URGENT">Urgent</option>
                </select>
              </div>
            </div>

            <div class="pt-4 flex justify-end space-x-3">
              <button
                  type="button"
                  @click="emit('close')"
                  class="rounded-md border border-gray-300 bg-white py-2 px-4 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                  type="submit"
                  :disabled="loading"
                  class="inline-flex justify-center rounded-md border border-transparent bg-indigo-600 py-2 px-4 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 disabled:opacity-50"
              >
                {{ loading ? 'Saving...' : (form.id ? 'Update Ticket' : 'Create Ticket') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>
