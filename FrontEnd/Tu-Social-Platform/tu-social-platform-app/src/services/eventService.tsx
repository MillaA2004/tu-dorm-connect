import type { EventItem, UserSummary } from "../types";
import apiClient from "./apiClient";

export interface EventRequestDTO {
  title: string;
  description: string;
  address: string;
  dateTime: string;
  capacity: number | null;
  eventType: string;
  latitude: number | null;
  longitude: number | null;
}

export interface EventResponseDTO {
  eventId: number;
  title: string;
  description: string;
  address: string;
  dateTime: string;
  capacity: number | null;
  createdAt: string;
  eventType: string;
  latitude: number | null;
  longitude: number | null;

  creator: UserSummary;
  participants: UserSummary[];
}

const EVENTS_BASE = "/api/events";

const mapDtoToEventItem = (dto: EventResponseDTO): EventItem => ({
  id: dto.eventId,
  title: dto.title,
  description: dto.description,
  address: dto.address,
  eventType: dto.eventType,
  dateTime: dto.dateTime,
  capacity: dto.capacity ?? 0,
  location: {
    lat: dto.latitude ?? 0,
    lng: dto.longitude ?? 0,
  },
  creator: dto.creator,
  participants: dto.participants ?? [],
});

export const eventService = {
  
  getAllEvents: async (): Promise<EventItem[]> => {
    const res = await apiClient.get<EventResponseDTO[]>(EVENTS_BASE);
    return res.data.map(mapDtoToEventItem);
  },

  getEventById: async (eventId: number): Promise<EventItem> => {
    const res = await apiClient.get<EventResponseDTO>(`${EVENTS_BASE}/${eventId}`);
    return mapDtoToEventItem(res.data);
  },

 
  getMyCreatedEvents: async (): Promise<EventItem[]> => {
    const res = await apiClient.get<EventResponseDTO[]>(`${EVENTS_BASE}/me/created`);
    return res.data.map(mapDtoToEventItem);
  },

  getMyParticipatingEvents: async (): Promise<EventItem[]> => {
    const res = await apiClient.get<EventResponseDTO[]>(`${EVENTS_BASE}/me/participating`);
    return res.data.map(mapDtoToEventItem);
  },

  getEventsCreatedByUser: async (userId: number): Promise<EventItem[]> => {
    const res = await apiClient.get<EventResponseDTO[]>(
      `${EVENTS_BASE}/creator/${userId}`
    );
    return res.data.map(mapDtoToEventItem);
  },

  
  createEvent: async (payload: EventRequestDTO): Promise<EventItem> => {
    const res = await apiClient.post<EventResponseDTO>(EVENTS_BASE, payload);
    return mapDtoToEventItem(res.data);
  },

  updateEvent: async (eventId: number, payload: EventRequestDTO): Promise<EventItem> => {
    const res = await apiClient.put<EventResponseDTO>(`${EVENTS_BASE}/${eventId}`, payload);
    return mapDtoToEventItem(res.data);
  },

  deleteEvent: async (eventId: number): Promise<void> => {
    await apiClient.delete(`${EVENTS_BASE}/${eventId}`);
  },

  joinEvent: async (eventId: number): Promise<EventItem> => {
    const res = await apiClient.post<EventResponseDTO>(`${EVENTS_BASE}/${eventId}/join`);
    return mapDtoToEventItem(res.data);
  },

  leaveEvent: async (eventId: number): Promise<EventItem> => {
    const res = await apiClient.post<EventResponseDTO>(`${EVENTS_BASE}/${eventId}/leave`);
    return mapDtoToEventItem(res.data);
  },

  removeParticipant: async (eventId: number, participantId: number): Promise<EventItem> => {
    const res = await apiClient.delete<EventResponseDTO>(
      `${EVENTS_BASE}/${eventId}/participants/${participantId}`
    );
    return mapDtoToEventItem(res.data);
  },

  
  searchEvents: async (
    q: string,
    eventType?: string,
    signal?: AbortSignal
  ): Promise<EventItem[]> => {
    const trimmed = q.trim();

    if (!trimmed) {
      const res = await apiClient.get<EventResponseDTO[]>(EVENTS_BASE, { signal });
      let items = res.data.map(mapDtoToEventItem);
      if (eventType && eventType !== "all") {
        items = items.filter((e) => e.eventType === eventType);
      }
      return items;
    }

    const res = await apiClient.get<EventResponseDTO[]>(`${EVENTS_BASE}/search`, {
      params: { q: trimmed },
      signal,
    });

    let items = res.data.map(mapDtoToEventItem);

    if (eventType && eventType !== "all") {
      items = items.filter((e) => e.eventType === eventType);
    }

    return items;
  },
};
