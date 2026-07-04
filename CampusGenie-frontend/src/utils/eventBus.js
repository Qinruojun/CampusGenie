class EventBus {
  constructor() {
    this.events = {}
  }

  on(eventName, callback) {
    if (!this.events[eventName]) {
      this.events[eventName] = []
    }
    this.events[eventName].push(callback)
  }

  emit(eventName, ...args) {
    if (this.events[eventName]) {
      this.events[eventName].forEach(callback => {
        try {
          callback(...args)
        } catch (e) {
          console.error('Event callback error:', e)
        }
      })
    }
  }

  off(eventName, callback) {
    if (this.events[eventName]) {
      this.events[eventName] = this.events[eventName].filter(cb => cb !== callback)
    }
  }

  clear(eventName) {
    if (eventName) {
      this.events[eventName] = []
    } else {
      this.events = {}
    }
  }
}

export const eventBus = new EventBus()

export const EVENT_TYPES = {
  REFRESH_PENDING_COUNT: 'refresh_pending_count'
}