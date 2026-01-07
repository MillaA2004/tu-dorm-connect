import SockJS from "sockjs-client/dist/sockjs";
import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs";

export type OnMessage = (msg: any) => void;

let client: Client | null = null;
let connectPromise: Promise<Client> | null = null;
let latestToken: string | null = null;

function resetConnectionState() {
  connectPromise = null;
  client = null;
}

export const chatSocket = {
  connect: (token: string) => {
    latestToken = token;

    
    if (client?.active) return Promise.resolve(client);

    
    if (connectPromise) return connectPromise;

    connectPromise = new Promise((resolve, reject) => {
      client = new Client({
        webSocketFactory: () => new SockJS("http://localhost:8080/ws-chat"),
        reconnectDelay: 3000,
        debug: () => {},

        beforeConnect: () => {
          
          if (latestToken) {
            client!.connectHeaders = {
              Authorization: `Bearer ${latestToken}`,
            };
          }
        },

        onConnect: () => resolve(client!),

        onStompError: (frame) => {
          const msg = frame.headers["message"] || "STOMP error";
          resetConnectionState();
          reject(new Error(msg));
        },

        onWebSocketError: () => {
          resetConnectionState();
          reject(new Error("WebSocket error"));
        },

        onWebSocketClose: () => {
          
          resetConnectionState();
        },
      });

      client.activate();
    });

    return connectPromise;
  },

  subscribeToChat: async (
    chatId: number,
    onMessage: OnMessage
  ): Promise<StompSubscription> => {
    if (!client || !connectPromise) {
      throw new Error("Socket not connected. Call connect() first.");
    }

    await connectPromise;

    return client.subscribe(`/topic/chats/${chatId}`, (message: IMessage) => {
      onMessage(JSON.parse(message.body));
    });
  },

  sendToChat: async (chatId: number, content: string) => {
    if (!client || !connectPromise) {
      throw new Error("Socket not connected. Call connect() first.");
    }

    await connectPromise;

    client.publish({
      destination: "/app/chats.sendMessage",
      body: JSON.stringify({ chatId, content }),
    });
  },

  disconnect: async () => {
    latestToken = null;
    if (client) await client.deactivate();
    resetConnectionState();
  },
  //latest addition!!!
  subscribe: async (destination: string, onMessage: (body: any) => void) => {
  if (!client || !connectPromise) throw new Error("Socket not connected.");
  await connectPromise;

  return client.subscribe(destination, (message) => {
    onMessage(JSON.parse(message.body));
  });
},

};

