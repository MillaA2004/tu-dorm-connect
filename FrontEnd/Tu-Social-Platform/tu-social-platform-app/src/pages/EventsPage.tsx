import React, { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import EventList from "../components/EventList";
import LocationModal from "../components/LocationModal";
import { type EventItem } from "../types";
import { eventService } from "../services/eventService";
import { useAuth } from "../services/AuthContext";

type FilterType =
  | "all"
  | "party"
  | "workshop"
  | "conference"
  | "sport"
  | "meetup";

const EventsPage: React.FC = () => {
  const [events, setEvents] = useState<EventItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedEvent, setSelectedEvent] = useState<EventItem | null>(null);
  const [joiningEventId, setJoiningEventId] = useState<number | null>(null);

  const [searchTerm, setSearchTerm] = useState("");
  const [filterType, setFilterType] = useState<FilterType>("all");

  const { user } = useAuth();
  const navigate = useNavigate();

  const abortRef = useRef<AbortController | null>(null);

  const fetchAll = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await eventService.getAllEvents();
      setEvents(data);
    } catch (err) {
      console.error(err);
      setError("Failed to load events.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAll();
  }, []);

  
  const filteredEvents = useMemo(() => {
    if (filterType === "all") return events;
    return events.filter((e) => e.eventType === filterType);
  }, [events, filterType]);

  
  const handleSearchSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const q = searchTerm.trim();

   
    abortRef.current?.abort();
    const controller = new AbortController();
    abortRef.current = controller;

    try {
      setLoading(true);
      setError(null);

      
      if (!q) {
        await fetchAll();
        return;
      }

      
      const data = await eventService.searchEvents(q, filterType, controller.signal);
      if (!controller.signal.aborted) setEvents(data);
    } catch (err: any) {
      if (err?.name === "CanceledError" || err?.name === "AbortError") return;
      console.error(err);
      setError("Failed to search events.");
    } finally {
      if (!controller.signal.aborted) setLoading(false);
    }
  };

  const handleClearFilters = async () => {
    setSearchTerm("");
    setFilterType("all");
    await fetchAll();
  };

  const handleCheckLocation = (event: EventItem) => setSelectedEvent(event);
  const handleCloseLocation = () => setSelectedEvent(null);

  const handleJoin = async (event: EventItem) => {
    if (!user) {
      alert("Please log in to join events.");
      return;
    }

    try {
      setJoiningEventId(event.id);
      const updated = await eventService.joinEvent(event.id, user.id);
      setEvents((prev) => prev.map((e) => (e.id === event.id ? updated : e)));
    } catch (err) {
      console.error(err);
      alert("Failed to join event.");
    } finally {
      setJoiningEventId(null);
    }
  };

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
          <h1 style={{ margin: 0, fontSize: "1.7rem" }}>Events</h1>
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


        {/* ✅ NEW: buttons below "Events" and above search */}
        <div
          style={{
            display: "flex",
            gap: "0.6rem",
            marginBottom: "1.1rem",
            flexWrap: "wrap",
          }}
        >
          <button
            type="button"
            onClick={() => navigate("/events/mine")}
            disabled={!user}
            title={!user ? "Log in to view your events" : ""}
            style={{
              padding: "0.55rem 0.95rem",
              borderRadius: 999,
              border: "1px solid #ddd",
              background: "white",
              cursor: user ? "pointer" : "not-allowed",
              fontWeight: 700,
              opacity: user ? 1 : 0.6,
            }}
          >
            My events
          </button>

          <button
            type="button"
            onClick={() => navigate("/events/joined")}
            disabled={!user}
            title={!user ? "Log in to view joined events" : ""}
            style={{
              padding: "0.55rem 0.95rem",
              borderRadius: 999,
              border: "1px solid #ddd",
              background: "white",
              cursor: user ? "pointer" : "not-allowed",
              fontWeight: 700,
              opacity: user ? 1 : 0.6,
            }}
          >
            Joined events
          </button>
        </div>

        {/* ✅ Search + Filter */}
        <form
          onSubmit={handleSearchSubmit}
          style={{
            display: "flex",
            gap: "0.75rem",
            flexWrap: "wrap",
            alignItems: "center",
            marginBottom: "1.25rem",
          }}
        >
          <input
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search events by title or address"
            style={{
              flex: "1 1 320px",
              padding: "0.65rem 0.9rem",
              borderRadius: 12,
              border: "1px solid #ddd",
              outline: "none",
            }}
          />

          <button
            type="submit"
            style={{
              padding: "0.65rem 0.9rem",
              borderRadius: 12,
              border: "none",
              background: "rgb(37,99,235)",
              color: "white",
              fontWeight: 700,
              cursor: "pointer",
              whiteSpace: "nowrap",
            }}
          >
            Search
          </button>

          <select
            value={filterType}
            onChange={(e) => setFilterType(e.target.value as FilterType)}
            style={{
              padding: "0.65rem 0.9rem",
              borderRadius: 12,
              border: "1px solid #ddd",
              background: "white",
              cursor: "pointer",
              minWidth: 160,
            }}
          >
            <option value="all">All types</option>
            <option value="party">Party</option>
            <option value="meetup">Meetup</option>
            <option value="conference">Conference</option>
            <option value="workshop">Workshop</option>
            <option value="sport">Sport</option>
          </select>

          {(searchTerm.trim() !== "" || filterType !== "all") && (
            <button
              type="button"
              onClick={handleClearFilters}
              style={{
                padding: "0.65rem 0.9rem",
                borderRadius: 12,
                border: "1px solid #ddd",
                background: "white",
                fontWeight: 700,
                cursor: "pointer",
                whiteSpace: "nowrap",
              }}
            >
              Clear
            </button>
          )}
        </form>

        {loading && <p>Loading events...</p>}
        {error && <p style={{ color: "red" }}>{error}</p>}

        {!loading && !error && (
          <>
            {filteredEvents.length === 0 ? (
              <p style={{ opacity: 0.75 }}>No events match your search/filter.</p>
            ) : (
              <EventList
                events={filteredEvents}
                onCheckLocation={handleCheckLocation}
              />
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

export default EventsPage;


