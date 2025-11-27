import React from "react";
import { Map, Marker } from "@vis.gl/react-google-maps";
import type { EventItem } from "../types";

const mapContainerStyle: React.CSSProperties = {
  width: "100%",
  height: "350px",
};

interface LocationModalProps {
  event: EventItem;
  onClose: () => void;
}

const LocationModal: React.FC<LocationModalProps> = ({ event, onClose }) => {
  return (
    <div
      style={{
        position: "fixed",
        inset: 0,
        background: "rgba(0,0,0,0.4)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        zIndex: 1000,
      }}
      onClick={onClose}
    >
      <div
        style={{
          background: "white",
          padding: "1rem",
          borderRadius: 8,
          width: "90%",
          maxWidth: 600,
          boxShadow: "0 10px 30px rgba(0,0,0,0.2)",
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginBottom: "0.5rem",
            alignItems: "center",
          }}
        >
          <h3 style={{ margin: 0 }}>{event.title} – Location</h3>
          <button
            onClick={onClose}
            style={{
              border: "none",
              background: "transparent",
              fontSize: "1.1rem",
              cursor: "pointer",
            }}
          >
            ✕
          </button>
        </div>

        <div style={mapContainerStyle}>
          <Map
            style={{ width: "100%", height: "100%" }}
            defaultZoom={15}
            defaultCenter={event.location}
            center={event.location}
          >
            <Marker position={event.location} />
          </Map>
        </div>
      </div>
    </div>
  );
};

export default LocationModal;
