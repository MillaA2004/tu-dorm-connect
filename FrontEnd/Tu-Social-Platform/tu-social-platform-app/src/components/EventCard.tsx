import React from "react";
import { useNavigate } from "react-router-dom";
import type { EventItem } from "../types";

interface EventCardProps {
  event: EventItem;
  onCheckLocation: () => void;
}

const EventCard: React.FC<EventCardProps> = ({ event, onCheckLocation }) => {
  const navigate = useNavigate();


  const goToProfile = (userId: number) => {
  navigate(`/profile/${userId}`);
};


  const date = new Date(event.dateTime);
  const formattedDate = isNaN(date.getTime())
    ? event.dateTime
    : date.toLocaleString();

  const creatorName = `${event.creator.firstName} ${event.creator.lastName}`.trim();

const initials = creatorName
  .split(" ")
  .filter(Boolean)
  .slice(0, 2)
  .map((p) => p[0]?.toUpperCase())
  .join("");


  return (
    <div
      style={{
        borderRadius: 16,
        padding: "1.1rem 1.3rem",
        background: "#ffffff",
        boxShadow: "0 6px 18px rgba(15,23,42,0.08)",
        display: "flex",
        flexDirection: "column",
        gap: "0.75rem",
      }}
    >
      
<div
  style={{
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  }}
>
  <div
  onClick={() => goToProfile(event.creator.id)}
  title="View profile"
  style={{
    display: "flex",
    alignItems: "center",
    gap: "0.6rem",
    cursor: "pointer",
    userSelect: "none",
  }}
>

    
    {event.creator.profileImageUrl ? (
      <img
        src={event.creator.profileImageUrl}
        alt={`${event.creator.firstName} ${event.creator.lastName}`}
        style={{
          width: 36,
          height: 36,
          borderRadius: 999,
          objectFit: "cover",
        }}
      />
    ) : (
      <div
        style={{
          width: 36,
          height: 36,
          borderRadius: "999px",
          background:
            "linear-gradient(135deg, rgb(37,99,235), rgb(56,189,248))",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          color: "white",
          fontWeight: 600,
        }}
      >
        {initials}
      </div>
    )}

    <div>
      <div style={{ fontWeight: 600 }}>
        {event.creator.firstName} {event.creator.lastName}
      </div>
      <div style={{ fontSize: "0.8rem", color: "#6b7280" }}>
        {event.eventType}
      </div>
    </div>
  </div>

  <div style={{ fontSize: "0.8rem", color: "#4b5563" }}>{formattedDate}</div>
</div>


      
      <div>
        <h3 style={{ margin: 0 }}>{event.title}</h3>
       
      </div>

      
      <div style={{ fontSize: "0.85rem", color: "#4b5563" }}>
        <p style={{ margin: "0.2rem 0" }}>
          <strong>Address:</strong> {event.address}
        </p>
        
      </div>

      
      <div
        style={{
          marginTop: "0.25rem",
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        
        <button
          onClick={() => navigate(`/events/${event.id}`)}
          style={{
            padding: "0.45rem 0.95rem",
            borderRadius: 999,
            border: "1px solid #16a34a",
            background: "white",
            color: "#16a34a",
            cursor: "pointer",
            fontSize: "0.85rem",
            fontWeight: 500,
          }}
        >
          View details
        </button>

        
        <button
          onClick={onCheckLocation}
          style={{
            padding: "0.45rem 0.95rem",
            borderRadius: 999,
            border: "none",
            background:
              "linear-gradient(135deg, rgb(37,99,235), rgb(56,189,248))",
            color: "white",
            cursor: "pointer",
            fontSize: "0.85rem",
            fontWeight: 500,
          }}
        >
          Check location
        </button>
      </div>
    </div>
  );
};

export default EventCard;
