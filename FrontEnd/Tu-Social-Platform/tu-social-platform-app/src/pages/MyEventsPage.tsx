import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import EventList from "../components/EventList";
import LocationModal from "../components/LocationModal";
import { type EventItem } from "../types";
import { eventService } from "../services/eventService";
import { useAuth } from "../services/AuthContext";

type TimeFilter = "upcoming" | "past";

const MyEventsPage: React.FC = () => {
  const [events, setEvents] = useState<EventItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedEvent, setSelectedEvent] = useState<EventItem | null>(null);

  const [timeFilter, setTimeFilter] = useState<TimeFilter>("upcoming");

  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchMyEvents = async () => {
      if (!user) {
        setLoading(false);
        setEvents([]);
        return;
      }

      try {
        setLoading(true);
        setError(null);
        const data = await eventService.getMyCreatedEvents();
        setEvents(data);
      } catch (err) {
        console.error(err);
        setError("Failed to load your events.");
      } finally {
        setLoading(false);
      }
    };

    fetchMyEvents();
  }, [user]);

  const filteredEvents = useMemo(() => {
    const now = new Date();

    return events
      .filter((e) => {
        const eventDate = new Date(e.dateTime);
        return timeFilter === "upcoming" ? eventDate >= now : eventDate < now;
      })
      .sort((a, b) => {
        const aTime = new Date(a.dateTime).getTime();
        const bTime = new Date(b.dateTime).getTime();
        
        return timeFilter === "upcoming" ? aTime - bTime : bTime - aTime;
      });
  }, [events, timeFilter]);

  const handleCheckLocation = (event: EventItem) => setSelectedEvent(event);
  const handleCloseLocation = () => setSelectedEvent(null);

  return (
    <>
      <Header />
      <div
        style={{
          maxWidth: 900,
          margin: "0 auto",
          padding: "2rem 1.5rem 3rem",
          paddingTop: "8%",
        }}
      >
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginBottom: "1.25rem",
            alignItems: "center",
            gap: "1rem",
            flexWrap: "wrap",
          }}
        >
          <h1 style={{ margin: 0, fontSize: "1.7rem" }}>My events</h1>

          <div style={{ display: "flex", gap: "0.75rem", flexWrap: "wrap" }}>
            <button
              onClick={() => navigate("/events")}
              style={{
                padding: "0.5rem 1.1rem",
                borderRadius: 999,
                border: "1px solid #ddd",
                background: "white",
                cursor: "pointer",
                fontWeight: 600,
                fontSize: "0.9rem",
              }}
            >
              ← All events
            </button>

            <button
              onClick={() => navigate("/events/new")}
              style={{
                padding: "0.5rem 1.1rem",
                borderRadius: 999,
                border: "none",
                background:
                  "linear-gradient(135deg, rgb(37,99,235), rgb(56,189,248))",
                color: "white",
                cursor: "pointer",
                fontWeight: 600,
                fontSize: "0.9rem",
                boxShadow: "0 6px 16px rgba(37,99,235,0.35)",
                whiteSpace: "nowrap",
              }}
            >
              + Create event
            </button>
          </div>
        </div>

        
        <div style={{ display: "flex", gap: "0.5rem", marginBottom: "1.25rem" }}>
          <button
            type="button"
            onClick={() => setTimeFilter("upcoming")}
            style={{
              padding: "0.5rem 0.9rem",
              borderRadius: 999,
              border: "1px solid #ddd",
              cursor: "pointer",
              fontWeight: 700,
              background:
                timeFilter === "upcoming" ? "rgb(37,99,235)" : "white",
              color: timeFilter === "upcoming" ? "white" : "black",
            }}
          >
            Upcoming
          </button>

          <button
            type="button"
            onClick={() => setTimeFilter("past")}
            style={{
              padding: "0.5rem 0.9rem",
              borderRadius: 999,
              border: "1px solid #ddd",
              cursor: "pointer",
              fontWeight: 700,
              background: timeFilter === "past" ? "rgb(37,99,235)" : "white",
              color: timeFilter === "past" ? "white" : "black",
            }}
          >
            Past
          </button>
        </div>

        {!user && (
          <p style={{ opacity: 0.8 }}>
            Please log in to view your created events.
          </p>
        )}

        {loading && <p>Loading your events...</p>}
        {error && <p style={{ color: "red" }}>{error}</p>}

        {!loading && !error && user && (
          <>
            {filteredEvents.length === 0 ? (
              <p style={{ opacity: 0.75 }}>
                {timeFilter === "upcoming"
                  ? "You don't have any upcoming events."
                  : "You don't have any past events."}
              </p>
            ) : (
              <EventList events={filteredEvents} onCheckLocation={handleCheckLocation} />
            )}
          </>
        )}
      </div>

      {selectedEvent && (
        <LocationModal event={selectedEvent} onClose={handleCloseLocation} />
      )}
    </>
  );
};

export default MyEventsPage;
