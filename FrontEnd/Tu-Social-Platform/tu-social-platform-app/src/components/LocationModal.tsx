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
  const handleOpenGoogleMaps = () => {
    const { lat, lng } = event.location;
    const url = `https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}`;
    window.open(url, "_blank", "noopener,noreferrer");
  };

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
        {/* Header */}
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginBottom: "0.75rem",
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

        {/* Map */}
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

        {/* Actions */}
        <div
          style={{
            display: "flex",
            justifyContent: "flex-end",
            marginTop: "0.75rem",
          }}
        >
          <button
            onClick={handleOpenGoogleMaps}
            style={{
              padding: "0.55rem 1.1rem",
              borderRadius: 999,
              border: "none",
              background:
                "linear-gradient(135deg, rgb(37,99,235), rgb(56,189,248))",
              color: "white",
              fontWeight: 600,
              cursor: "pointer",
              boxShadow: "0 6px 16px rgba(37,99,235,0.35)",
            }}
          >
            Get directions
          </button>
        </div>
      </div>
    </div>
  );
};

export default LocationModal;
