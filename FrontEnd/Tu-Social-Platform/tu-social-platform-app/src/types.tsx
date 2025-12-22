export interface LatLng {
  lat: number;
  lng: number;
}


export interface UserSummary {
  id: number;
  firstName: string;
  lastName: string;
  profileImageUrl?: string | null;
}

export interface NewEvent {
  title: string;
  description: string;
  address: string;
  eventType: string;
  dateTime: string;
  capacity: number;
  location: LatLng;
  participants: UserSummary[];
}

export interface EventItem extends NewEvent {
  id: number;
  creator: UserSummary;
}


export interface Message {
  id: string;
  text: string;
  createdAt: string; 
  isRead: boolean;
}

export interface Conversation {
  id: string;
  name: string;
  avatarUrl?: string;
  lastMessage: string;
  lastMessageAt: string; 
  unreadCount: number;
}

export interface NotificationItem {
  id: string;
  title: string;
  description: string;
  createdAt: string; 
  isRead: boolean;
  type?: "like" | "comment" | "event" | "system";
}



export type Dorm = {
  id: string;
  name: string;
  address?: string;
  city?: string;
  priceFrom?: number;
  imageUrl?: string;
  location: { lat: number; lng: number };
};






