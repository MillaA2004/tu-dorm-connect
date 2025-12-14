import apiClient from "./apiClient";
import type { MessageDTO } from "./MessageService";

export interface ChatMemberDTO {
  userId: number;
  firstName?: string;
  lastName?: string;
  chatRole?: string;
  imageUrl?: string;
}

export interface ChatDTO {
  chatId: number;
  name: string | null;
  groupChat: boolean;
  members: ChatMemberDTO[];
  lastMessage: MessageDTO | null;
}

export interface CreateDirectChatRequest {
  otherUserId: number;
}

export interface CreateGroupChatRequest {
  name: string;
  memberIds: number[];
}

export interface AddMemberRequest {
  userId: number;
  chatRole: string;
}

const CHATS_BASE = "/api/chats";

export const chatService = {
  getMyChats: async (): Promise<ChatDTO[]> => {
    const res = await apiClient.get<ChatDTO[]>(CHATS_BASE);
    return res.data;
  },

  getChatById: async (chatId: number): Promise<ChatDTO> => {
    const res = await apiClient.get<ChatDTO>(`${CHATS_BASE}/${chatId}`);
    return res.data;
  },

  createDirectChat: async (otherUserId: number): Promise<ChatDTO> => {
    const res = await apiClient.post<ChatDTO>(
      `${CHATS_BASE}/direct`,
      { otherUserId } satisfies CreateDirectChatRequest
    );
    return res.data;
  },

  createGroupChat: async (payload: CreateGroupChatRequest): Promise<ChatDTO> => {
    const res = await apiClient.post<ChatDTO>(`${CHATS_BASE}/group`, payload);
    return res.data;
  },

  addMember: async (chatId: number, payload: AddMemberRequest): Promise<void> => {
    await apiClient.post(`${CHATS_BASE}/${chatId}/members`, payload);
  },

  removeMember: async (chatId: number, memberId: number): Promise<void> => {
    await apiClient.delete(`${CHATS_BASE}/${chatId}/members/${memberId}`);
  },
};
