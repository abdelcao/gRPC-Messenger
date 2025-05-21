import { defineStore } from "pinia";
import { ref } from "vue";

export const useNotifStore = defineStore("notification", () => {

  const notifications = ref<Notification[]>([]);
  function pushNotif (n: Notification) {
    notifications.value.push(n)
  }

  return {
    notifications,
    pushNotif,
  }
})
