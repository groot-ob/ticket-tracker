<script setup lang="ts">
import TicketList from './components/TicketList.vue';
</script>

<template>
  <main class="min-h-screen bg-slate-50 py-10 px-4 sm:px-6 lg:px-8">
    <header class="max-w-4xl mx-auto mb-6 text-center">
      <h1 class="text-3xl font-bold text-gray-900">Ticket Manager</h1>
      <p class="text-sm text-gray-500 mt-1">Manage, filter, and track support or task tickets.</p>
    </header>

    <TicketList />
  </main>
</template>
