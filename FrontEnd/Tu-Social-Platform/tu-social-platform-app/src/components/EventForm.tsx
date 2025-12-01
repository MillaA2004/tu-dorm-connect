import React, { useState } from "react";
import { Map, Marker } from "@vis.gl/react-google-maps";
import { DEFAULT_CENTER } from "../config";
import type { LatLng, NewEvent } from "../types";

const mapContainerStyle: React.CSSProperties = {
  width: "100%",
  height: "300px",
};

interface EventFormProps {
  onCreate: (event: NewEvent) => void;
}

const EventForm: React.FC<EventFormProps> = ({ onCreate }) => {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [address, setAddress] = useState("");
  const [eventType, setEventType] = useState("party");
  const [dateTime, setDateTime] = useState("");
  const [capacity, setCapacity] = useState<number | "">("");
  const [location, setLocation] = useState<LatLng | null>(null);

  const handleMapClick = (e: any) => {
    const latLng = e?.detail?.latLng ?? e?.latLng;
    if (!latLng) return;

    const lat = typeof latLng.lat === "function" ? latLng.lat() : latLng.lat;
    const lng = typeof latLng.lng === "function" ? latLng.lng() : latLng.lng;

    setLocation({ lat, lng });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!title.trim()) return alert("Add a title");
    if (!description.trim()) return alert("Add a description");
    if (!address.trim()) return alert("Add an address");
    if (!dateTime) return alert("Pick a date & time");
    if (capacity === "" || capacity <= 0)
      return alert("Set a positive capacity");
    if (!location) return alert("Pick a location on the map");

    onCreate({
      title: title.trim(),
      description: description.trim(),
      address: address.trim(),
      eventType,
      dateTime,
      capacity: Number(capacity),
      location
      
    });

    
    setTitle("");
    setDescription("");
    setAddress("");
    setEventType("party");
    setDateTime("");
    setCapacity("");
    setLocation(null);
  };

  return (
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
      <h2 style={{ margin: 0, fontSize: "1.5rem" }}>Create Event</h2>

      
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

      
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "1.2fr 0.8fr",
          gap: "1rem",
        }}
      >
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
            onChange={(e) =>
              setCapacity(e.target.value === "" ? "" : Number(e.target.value))
            }
            placeholder="e.g. 50"
          />
        </div>
      </div>

      
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

      
      <div>
        <p style={{ marginBottom: "0.5rem", fontWeight: 500 }}>
          Pick a location on the map (click anywhere):
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
            defaultCenter={DEFAULT_CENTER}
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

      <button
        type="submit"
        style={{
          marginTop: "0.5rem",
          padding: "0.6rem 1.3rem",
          borderRadius: 999,
          border: "none",
          background:
            "linear-gradient(135deg, rgb(37,99,235), rgb(56,189,248))",
          color: "white",
          cursor: "pointer",
          justifySelf: "flex-start",
          fontWeight: 600,
          fontSize: "0.95rem",
          boxShadow: "0 8px 20px rgba(37,99,235,0.35)",
        }}
      >
        Create Event
      </button>
    </form>
  );
};

export default EventForm;

