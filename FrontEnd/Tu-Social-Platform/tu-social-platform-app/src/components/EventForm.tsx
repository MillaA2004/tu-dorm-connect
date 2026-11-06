import React from "react";
import { useNavigate } from "react-router-dom";
import { Map, Marker } from "@vis.gl/react-google-maps";
import { DEFAULT_CENTER } from "../config";
import { useAuth } from "../services/AuthContext";

import { useEventForm } from "../hooks/useEventForm";
import { useEventLocation } from "../hooks/useEventLocation";
import { useEventSubmit } from "../hooks/useEventSubmit";

const mapContainerStyle: React.CSSProperties = {
  width: "100%",
  height: "300px",
};

const EventForm: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  
  const {
    form,
    updateField,
    resetForm,
    validate,
    buildPayload,
  } = useEventForm();

  
  const {
    location,
    setLocation,
    handleMapClick,
  } = useEventLocation();

  
  const {
    submitEvent,
    isSubmitting,
  } = useEventSubmit();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!user) {
      alert("You must be logged in to create an event.");
      return;
    }

   
    const validationError = validate(location);

    if (validationError) {
      alert(validationError);
      return;
    }

    if (!location) return;

    
    const payload = buildPayload(location);

    
    const success = await submitEvent(payload, "create");

    if (success) {
      resetForm();
      setLocation(null);
      navigate("/events");
    } else {
      alert("Failed to create event. Please try again.");
    }
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
      <h2 style={{ margin: 0, fontSize: "1.5rem" }}>
        Create Event
      </h2>

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
          value={form.title}
          onChange={(e) =>
            updateField("title", e.target.value)
          }
          placeholder="My cool event"
        />
      </div>

      {/* Description */}
      <div style={{ display: "grid", gap: "0.35rem" }}>
        <label style={{ fontWeight: 500 }}>
          Description
        </label>
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
          value={form.description}
          onChange={(e) =>
            updateField("description", e.target.value)
          }
          placeholder="What is this event about?"
        />
      </div>

      {/* Address */}
      <div style={{ display: "grid", gap: "0.35rem" }}>
        <label style={{ fontWeight: 500 }}>
          Address
        </label>
        <input
          style={{
            width: "100%",
            padding: "0.6rem 0.75rem",
            borderRadius: 8,
            border: "1px solid #d4d4d8",
            fontSize: "0.95rem",
          }}
          value={form.address}
          onChange={(e) =>
            updateField("address", e.target.value)
          }
          placeholder="Street, city, etc."
        />
      </div>

      {/* Event type + Capacity */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "1.2fr 0.8fr",
          gap: "1rem",
        }}
      >
        {/* Event type */}
        <div
          style={{
            display: "grid",
            gap: "0.35rem",
          }}
        >
          <label style={{ fontWeight: 500 }}>
            Event type
          </label>
          <select
            style={{
              padding: "0.6rem 0.75rem",
              borderRadius: 8,
              border: "1px solid #d4d4d8",
              fontSize: "0.95rem",
            }}
            value={form.eventType}
            onChange={(e) =>
              updateField("eventType", e.target.value)
            }
          >
            <option value="party">Party</option>
            <option value="meetup">Meetup</option>
            <option value="conference">Conference</option>
            <option value="workshop">Workshop</option>
            <option value="sport">Sport</option>
          </select>
        </div>

        {/* Capacity */}
        <div
          style={{
            display: "grid",
            gap: "0.35rem",
          }}
        >
          <label style={{ fontWeight: 500 }}>
            Capacity
          </label>
          <input
            type="number"
            min={1}
            style={{
              padding: "0.6rem 0.75rem",
              borderRadius: 8,
              border: "1px solid #d4d4d8",
              fontSize: "0.95rem",
            }}
            value={form.capacity}
            onChange={(e) =>
              updateField(
                "capacity",
                e.target.value === ""
                  ? ""
                  : Number(e.target.value)
              )
            }
            placeholder="e.g. 50"
          />
        </div>
      </div>

      {/* Date & time */}
      <div style={{ display: "grid", gap: "0.35rem" }}>
        <label style={{ fontWeight: 500 }}>
          Date & time
        </label>
        <input
          type="datetime-local"
          style={{
            padding: "0.6rem 0.75rem",
            borderRadius: 8,
            border: "1px solid #d4d4d8",
            fontSize: "0.95rem",
          }}
          value={form.dateTime}
          onChange={(e) =>
            updateField("dateTime", e.target.value)
          }
        />
      </div>

      {/* Google Maps */}
      <div>
        <p
          style={{
            marginBottom: "0.5rem",
            fontWeight: 500,
          }}
        >
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
            style={{
              width: "100%",
              height: "100%",
            }}
            defaultZoom={location ? 14 : 11}
            defaultCenter={DEFAULT_CENTER}
            center={location || DEFAULT_CENTER}
            onClick={handleMapClick}
          >
            {location && (
              <Marker position={location} />
            )}
          </Map>
        </div>
      </div>

      {/* Selected location */}
      {location && (
        <p
          style={{
            fontSize: "0.85rem",
            color: "#6b7280",
          }}
        >
          Selected:{" "}
          <strong>
            {location.lat.toFixed(5)}
          </strong>
          ,{" "}
          <strong>
            {location.lng.toFixed(5)}
          </strong>
        </p>
      )}

      {/* Submit button */}
      <button
        type="submit"
        disabled={isSubmitting}
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
          boxShadow:
            "0 8px 20px rgba(37,99,235,0.35)",
          opacity: isSubmitting ? 0.75 : 1,
        }}
      >
        {isSubmitting
          ? "Creating..."
          : "Create Event"}
      </button>
    </form>
  );
};

export default EventForm;
