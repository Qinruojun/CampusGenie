import { defineStore } from 'pinia'

const STORAGE_KEY = 'campusgenie:contribution-view-cache'

function readStorage() {
  const raw = sessionStorage.getItem(STORAGE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

function writeStorage(data) {
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(data))
}

export const useContributionViewStore = defineStore('contributionView', {
  state: () => ({
    contribution: readStorage()
  }),
  actions: {
    setContribution(item) {
      this.contribution = item
      writeStorage(item)
    },
    getContribution() {
      return this.contribution
    },
    clearContribution() {
      this.contribution = null
      sessionStorage.removeItem(STORAGE_KEY)
    }
  }
})
