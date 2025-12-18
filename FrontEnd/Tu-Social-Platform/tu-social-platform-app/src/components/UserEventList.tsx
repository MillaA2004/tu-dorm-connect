import React, { useCallback, useEffect, useState } from "react";
import type { EventItem } from "../types";
import EventList from "./EventList";
import { eventService } from "../services/eventService";

type Props = {
  userId: number;
  refreshKey?: number;
  onCheckLocation: (event: EventItem) => void;
};

const MyEventList: React.FC<Props> = ({ userId, refreshKey, onCheckLocation }) => {
  const [events, setEvents] = useState<EventItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadEvents = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await eventService.getEventsCreatedByUser(userId);
      setEvents(data);
    } catch (e) {
      console.error("Failed to load user events:", e);
      setError("Could not load events. Please try again.");
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    void loadEvents();
  }, [loadEvents, refreshKey]);

  if (loading) return <p>Loading events…</p>;

  if (error) {
    return (
      <div style={{ display: "grid", gap: 8 }}>
        <div style={{ color: "crimson" }}>{error}</div>
        <button onClick={() => void loadEvents()}>Retry</button>
      </div>
    );
  }

  return <EventList events={events} onCheckLocation={onCheckLocation} />;
};

export default MyEventList;
