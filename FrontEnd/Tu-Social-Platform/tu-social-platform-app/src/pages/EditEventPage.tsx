import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Map, Marker } from "@vis.gl/react-google-maps";
import { DEFAULT_CENTER } from "../config";
import type { EventItem, LatLng } from "../types";
import { eventService, type EventRequestDTO } from "../services/eventService";
import { useAuth } from "../services/AuthContext";
import Header from "../components/Header";

const mapContainerStyle: React.CSSProperties = {
  width: "100%",
  height: "300px",
};

const EditEventPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const eventId = Number(id);

  const [event, setEvent] = useState<EventItem | null>(null);
  const [loading, setLoading] = useState(true);

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [address, setAddress] = useState("");
  const [eventType, setEventType] = useState("party");
  const [dateTime, setDateTime] = useState("");
  const [capacity, setCapacity] = useState<number | "">("");
  const [location, setLocation] = useState<LatLng | null>(null);

  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!Number.isFinite(eventId)) {
      setLoading(false);
      return;
    }

    const fetchEvent = async () => {
      try {
        setLoading(true);
        const data = await eventService.getEventById(eventId);
        setEvent(data);

        // prefill form fields
        setTitle(data.title);
        setDescription(data.description);
        setAddress(data.address);
        setEventType(data.eventType);
        // if your backend returns seconds, datetime-local needs "YYYY-MM-DDTHH:mm"
        setDateTime(data.dateTime?.slice(0, 16) ?? "");
        setCapacity(data.capacity ?? "");
        setLocation(data.location ?? null);
      } catch (err) {
        console.error(err);
        setEvent(null);
      } finally {
        setLoading(false);
      }
    };

    fetchEvent();
  }, [eventId]);

  // Only creator can edit
  const isCreator = !!user && !!event && event.creator.id === user.id;

  const handleMapClick = (e: any) => {
    const latLng = e?.detail?.latLng ?? e?.latLng;
    if (!latLng) return;

    const lat = typeof latLng.lat === "function" ? latLng.lat() : latLng.lat;
    const lng = typeof latLng.lng === "function" ? latLng.lng() : latLng.lng;

    setLocation({ lat, lng });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return alert("You must be logged in.");
    if (!event) return;

    if (!isCreator) {
      alert("You are not allowed to edit this event.");
      return;
    }

    if (!title.trim()) return alert("Add a title");
    if (!description.trim()) return alert("Add a description");
    if (!address.trim()) return alert("Add an address");
    if (!dateTime) return alert("Pick a date & time");
    if (capacity === "" || Number(capacity) <= 0) return alert("Set a positive capacity");
    if (!location) return alert("Pick a location on the map");

    const payload: EventRequestDTO = {
      title: title.trim(),
      description: description.trim(),
      address: address.trim(),
      eventType,
      dateTime: dateTime.length === 16 ? `${dateTime}:00` : dateTime,
      capacity: Number(capacity),
      latitude: location.lat,
      longitude: location.lng,
    };

    try {
      setSaving(true);
      await eventService.updateEvent(event.id, payload);
      navigate(`/events/${event.id}`);
    } catch (err) {
      console.error(err);
      alert("Failed to save changes.");
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    if (event) navigate(`/events/${event.id}`);
    else navigate("/events");
  };

  if (loading) {
    return (
      <div style={{ maxWidth: 900, margin: "0 auto", padding: "2rem 1.5rem 3rem" }}>
        <p>Loading...</p>
      </div>
    );
  }

  if (!event) {
    return (
      <div style={{ maxWidth: 900, margin: "0 auto", padding: "2rem 1.5rem 3rem" }}>
        <button
          onClick={() => navigate("/events")}
          style={{
            border: "none",
            background: "none",
            color: "#4f46e5",
            cursor: "pointer",
            marginBottom: "1rem",
          }}
        >
          ← Back to events
        </button>
        <p>Event not found.</p>
      </div>
    );
  }

  if (!isCreator) {
    return (
      <div style={{ maxWidth: 900, margin: "0 auto", padding: "2rem 1.5rem 3rem" }}>
        <button
          onClick={() => navigate(`/events/${event.id}`)}
          style={{
            border: "none",
            background: "none",
            color: "#4f46e5",
            cursor: "pointer",
            marginBottom: "1rem",
          }}
        >
          ← Back to event
        </button>
        <p>You are not allowed to edit this event.</p>
      </div>
    );
  }

  return (

    <>
    <Header/>
    
    <div style={{ maxWidth: 900, margin: "0 auto", padding: "2rem 1.5rem 3rem" }}>
      <button
        onClick={() => navigate(`/events/${event.id}`)}
        style={{
          border: "none",
          background: "none",
          color: "#4f46e5",
          cursor: "pointer",
          marginBottom: "1rem",
        }}
      >
        ← Back to event
      </button>

      <form
        onSubmit={handleSubmit}
        style={{
          display: "grid",
          gap: "1rem",
          borderRadius: 16,
          padding: "1.5rem",
          marginBottom: "2rem",
          background: "#ffffff",
          boxShadow: "0 10px 25px rgba(15, 23, 42, 0.08)",
        }}
      >
        <h2 style={{ margin: 0, fontSize: "1.5rem" }}>Edit Event</h2>

        {/* Title */}
        <div style={{ display: "grid", gap: "0.35rem" }}>
          <label style={{ fontWeight: 500 }}>Title</label>
          <input
            style={{
              width: "100%",
              padding: "0.6rem 0.75rem",
              borderRadius: 8,
              border: "1px solid #d4d4d8",
              fontSize: "0.95rem",
            }}
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="My cool event"
          />
        </div>

        {/* Description */}
        <div style={{ display: "grid", gap: "0.35rem" }}>
          <label style={{ fontWeight: 500 }}>Description</label>
          <textarea
            style={{
              width: "100%",
              padding: "0.6rem 0.75rem",
              borderRadius: 8,
              border: "1px solid #d4d4d8",
              fontSize: "0.95rem",
              minHeight: 80,
              resize: "vertical",
            }}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="What is this event about?"
          />
        </div>

        {/* Address */}
        <div style={{ display: "grid", gap: "0.35rem" }}>
          <label style={{ fontWeight: 500 }}>Address</label>
          <input
            style={{
              width: "100%",
              padding: "0.6rem 0.75rem",
              borderRadius: 8,
              border: "1px solid #d4d4d8",
              fontSize: "0.95rem",
            }}
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            placeholder="Street, city, etc."
          />
        </div>

        {/* Event type + Capacity */}
        <div style={{ display: "grid", gridTemplateColumns: "1.2fr 0.8fr", gap: "1rem" }}>
          <div style={{ display: "grid", gap: "0.35rem" }}>
            <label style={{ fontWeight: 500 }}>Event type</label>
            <select
              style={{
                padding: "0.6rem 0.75rem",
                borderRadius: 8,
                border: "1px solid #d4d4d8",
                fontSize: "0.95rem",
              }}
              value={eventType}
              onChange={(e) => setEventType(e.target.value)}
            >
              <option value="party">Party</option>
              <option value="meetup">Meetup</option>
              <option value="conference">Conference</option>
              <option value="workshop">Workshop</option>
            </select>
          </div>

          <div style={{ display: "grid", gap: "0.35rem" }}>
            <label style={{ fontWeight: 500 }}>Capacity</label>
            <input
              type="number"
              min={1}
              style={{
                padding: "0.6rem 0.75rem",
                borderRadius: 8,
                border: "1px solid #d4d4d8",
                fontSize: "0.95rem",
              }}
              value={capacity}
              onChange={(e) => setCapacity(e.target.value === "" ? "" : Number(e.target.value))}
              placeholder="e.g. 50"
            />
          </div>
        </div>

        {/* Date & time */}
        <div style={{ display: "grid", gap: "0.35rem" }}>
          <label style={{ fontWeight: 500 }}>Date & time</label>
          <input
            type="datetime-local"
            style={{
              padding: "0.6rem 0.75rem",
              borderRadius: 8,
              border: "1px solid #d4d4d8",
              fontSize: "0.95rem",
            }}
            value={dateTime}
            onChange={(e) => setDateTime(e.target.value)}
          />
        </div>

        {/* Map */}
        <div>
          <p style={{ marginBottom: "0.5rem", fontWeight: 500 }}>
            Update location on the map:
          </p>

          <div
            style={{
              ...mapContainerStyle,
              borderRadius: 12,
              overflow: "hidden",
              border: "1px solid #e4e4e7",
            }}
          >
            <Map
              style={{ width: "100%", height: "100%" }}
              defaultZoom={location ? 14 : 11}
              defaultCenter={location || DEFAULT_CENTER}
              center={location || DEFAULT_CENTER}
              onClick={handleMapClick}
            >
              {location && <Marker position={location} />}
            </Map>
          </div>
        </div>

        {location && (
          <p style={{ fontSize: "0.85rem", color: "#6b7280" }}>
            Selected: <strong>{location.lat.toFixed(5)}</strong>,{" "}
            <strong>{location.lng.toFixed(5)}</strong>
          </p>
        )}

        {/* Buttons */}
        <div style={{ marginTop: "0.5rem", display: "flex", gap: "0.75rem" }}>
          <button
            type="submit"
            disabled={saving}
            style={{
              padding: "0.6rem 1.3rem",
              borderRadius: 999,
              border: "none",
              background:
                "linear-gradient(135deg, rgb(37,99,235), rgb(56,189,248))",
              color: "white",
              cursor: "pointer",
              fontWeight: 600,
              fontSize: "0.95rem",
              boxShadow: "0 8px 20px rgba(37,99,235,0.35)",
              opacity: saving ? 0.75 : 1,
            }}
          >
            {saving ? "Saving..." : "Save changes"}
          </button>

          <button
            type="button"
            onClick={handleCancel}
            style={{
              padding: "0.6rem 1.3rem",
              borderRadius: 999,
              border: "1px solid #d4d4d8",
              background: "white",
              color: "#374151",
              cursor: "pointer",
              fontWeight: 500,
              fontSize: "0.95rem",
            }}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
    </>
  );
};

export default EditEventPage;
