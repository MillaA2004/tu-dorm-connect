import apiClient from "./apiClient";

export type Role = "User" | "Admin";

export const AdminService = {
  suspendUser(userId: number, minutes: number) {
    return apiClient.post(`/api/admin/users/${userId}/suspend`, {
      minutes,
    });
  },

  unsuspendUser(userId: number) {
    return apiClient.post(`/api/admin/users/${userId}/unsuspend`);
  },

  setRoleByUserId(userId: number, role: Role) {
    return apiClient.post(`/api/admin/users/${userId}/role`, {
      role,
    });
  },

  setRoleByEmail(email: string, role: Role) {
    return apiClient.post(`/api/admin/users/email/${email}/role`, {
      role,
    });
  },
};
