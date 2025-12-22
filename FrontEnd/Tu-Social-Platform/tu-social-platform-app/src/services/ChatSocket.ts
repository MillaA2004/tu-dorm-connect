// import SockJS from "sockjs-client/dist/sockjs";

// import { Client, type IMessage } from "@stomp/stompjs";

// export type OnMessage = (msg: any) => void;

// let client: Client | null = null;

// export const chatSocket = {
//   connect: (token: string) => {
//     if (client?.active) return client;

//     client = new Client({
//       webSocketFactory: () => new SockJS("http://localhost:8080/ws-chat"),
//       connectHeaders: {
//         Authorization: `Bearer ${token}`,
//       },
//       debug: () => {},
//       reconnectDelay: 3000,
//     });

//     client.activate();
//     return client;
//   },

//   subscribeToChat: (chatId: number, onMessage: OnMessage) => {
//     if (!client) throw new Error("Socket not connected");

//     return client.subscribe(`/topic/chats/${chatId}`, (message: IMessage) => {
//       onMessage(JSON.parse(message.body));
//     });
//   },

//   sendToChat: (chatId: number, content: string) => {
//     if (!client) throw new Error("Socket not connected");

//     client.publish({
//       destination: "/app/chats.sendMessage",
//       body: JSON.stringify({ chatId, content }),
//     });
//   },

//   disconnect: () => {
//     client?.deactivate();
//     client = null;
//   },
// };


// ChatSocket.ts
import SockJS from "sockjs-client/dist/sockjs";
import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs";

export type OnMessage = (msg: any) => void;

let client: Client | null = null;
let connectPromise: Promise<Client> | null = null;

const WS_BASE = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
const WS_ENDPOINT = `${WS_BASE}/ws-chat`;

function resetConnectionState() {
  connectPromise = null;
  // keep client instance for reconnects unless you want to null it too
}

function ensureConnected(token: string): Promise<Client> {
  if (client?.connected) return Promise.resolve(client);

  if (connectPromise) return connectPromise;

  client = new Client({
    webSocketFactory: () => new SockJS(WS_ENDPOINT),
    connectHeaders: { Authorization: `Bearer ${token}` },

    // ✅ TURN THIS ON while debugging
    debug: (msg) => console.log("[stomp]", msg),

    reconnectDelay: 3000,
  });

  connectPromise = new Promise<Client>((resolve, reject) => {
    const c = client!;

    c.onConnect = () => {
      console.log("[stomp] CONNECTED");
      resolve(c);
    };

    c.onStompError = (frame) => {
      console.error("[stomp] STOMP ERROR:", frame.headers["message"], frame.body);
      resetConnectionState();
      reject(new Error(frame.headers["message"] || "STOMP error"));
    };

    c.onWebSocketError = (evt) => {
      console.error("[stomp] WS ERROR:", evt);
      // ✅ allow future attempts if connect fails early
      resetConnectionState();
      // don't reject here because it may recover; but clearing the promise prevents deadlock
    };

    c.onWebSocketClose = (evt) => {
      console.warn("[stomp] WS CLOSED:", evt);
      resetConnectionState();
    };

    c.onDisconnect = () => {
      console.log("[stomp] DISCONNECTED");
      resetConnectionState();
    };
  });

  client.activate();
  return connectPromise;
}

export const chatSocket = {
  connect: (token: string) => ensureConnected(token),

  subscribeToChat: async (
    token: string,
    chatId: number,
    onMessage: OnMessage
  ): Promise<StompSubscription> => {
    const c = await ensureConnected(token);

    console.log("[stomp] subscribing to", `/topic/chats/${chatId}`);
    return c.subscribe(`/topic/chats/${chatId}`, (message: IMessage) => {
      try {
        const parsed = JSON.parse(message.body);
        console.log("[stomp] MESSAGE", parsed);
        onMessage(parsed);
      } catch (e) {
        console.error("Failed to parse WS message body:", message.body, e);
      }
    });
  },

  sendToChat: async (token: string, chatId: number, content: string) => {
    const c = await ensureConnected(token);

    console.log("[stomp] sending", { chatId, content });
    c.publish({
      destination: "/app/chats.sendMessage",
      body: JSON.stringify({ chatId, content }),
    });
  },

  disconnect: async () => {
    if (!client) return;
    await client.deactivate();
    client = null;
    connectPromise = null;
  },
};
