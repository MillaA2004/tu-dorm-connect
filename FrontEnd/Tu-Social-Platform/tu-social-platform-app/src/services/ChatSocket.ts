import SockJS from "sockjs-client";
import { Client, type IMessage } from "@stomp/stompjs";

export type OnMessage = (msg: any) => void;

let client: Client | null = null;

export const chatSocket = {
  connect: (token: string) => {
    if (client?.active) return client;

    client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws-chat"),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: () => {},
      reconnectDelay: 3000,
    });

    client.activate();
    return client;
  },

  subscribeToChat: (chatId: number, onMessage: OnMessage) => {
    if (!client) throw new Error("Socket not connected");

    return client.subscribe(`/topic/chats/${chatId}`, (message: IMessage) => {
      onMessage(JSON.parse(message.body));
    });
  },

  sendToChat: (chatId: number, content: string) => {
    if (!client) throw new Error("Socket not connected");

    client.publish({
      destination: "/app/chats.sendMessage",
      body: JSON.stringify({ chatId, content }),
    });
  },

  disconnect: () => {
    client?.deactivate();
    client = null;
  },
};
