import apiClient from "./apiClient";

export interface MessageDTO {
  messageId: number;
  chatId: number;
  userId: number; 
  senderName?: string | null;
  senderImageUrl?: string | null;
  content: string;
  sentAt: string;
}

export interface SendMessageRequest {
  content: string;
}

export interface EditMessageRequest {
  content: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;     
  size: number;
  last: boolean;
  first: boolean;
}

const BASE = "/api/chats";

export const messageService = {
  getMessages: async (chatId: number, page = 0, size = 50): Promise<PageResponse<MessageDTO>> => {
    const res = await apiClient.get<PageResponse<MessageDTO>>(
      `${BASE}/${chatId}/messages`,
      { params: { page, size } }
    );
    return res.data;
  },

  sendMessage: async (chatId: number, content: string): Promise<MessageDTO> => {
    const res = await apiClient.post<MessageDTO>(
      `${BASE}/${chatId}/messages`,
      { content } satisfies SendMessageRequest
    );
    return res.data;
  },

  editMessage: async (
    chatId: number,
    messageId: number,
    content: string
  ): Promise<MessageDTO> => {
    const res = await apiClient.put<MessageDTO>(
      `${BASE}/${chatId}/messages/${messageId}`,
      { content } satisfies EditMessageRequest
    );
    return res.data;
  },
};
