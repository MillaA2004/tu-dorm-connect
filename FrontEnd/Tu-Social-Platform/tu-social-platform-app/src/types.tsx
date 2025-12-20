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

export interface NewListing {
  title: string;
  description: string;
  price: number;
  dorm: string;
  posterId: number;
  expiryDays: number;
}
export interface ListingItem extends NewListing {
  id: number;
  createdAt: string;
  expiresAt: string;
  isActive: boolean;
}

export interface ListingRequestDTO {
  title: string;
  description: string;
  dorm: string;
  price: number;
  expiryDays: number | null;
}

export interface ListingResponseDTO {
  id: number;
  title: string;
  description: string;
  dorm: string;
  price: number;
  createdAt: string;
  expiresAt: string | null;
  isActive: boolean;
  posterId: number;
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

