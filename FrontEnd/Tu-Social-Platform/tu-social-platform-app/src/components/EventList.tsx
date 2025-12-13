import React from "react";
import type { EventItem } from "../types";
import EventCard from "./EventCard";

interface EventListProps {
  events: EventItem[];
  onCheckLocation: (event: EventItem) => void;
}

const EventList: React.FC<EventListProps> = ({ events, onCheckLocation }) => {
  if (events.length === 0) {
    return <p style={{ color: "#6b7280" }}>No events yet.</p>;
  }

  return (
    <div style={{ display: "grid", gap: "1rem" }}>
      {events.map((event) => (
        <EventCard
          key={event.id}
          event={event}
          onCheckLocation={() => onCheckLocation(event)}
        />
      ))}
    </div>
  );
};

export default EventList;

