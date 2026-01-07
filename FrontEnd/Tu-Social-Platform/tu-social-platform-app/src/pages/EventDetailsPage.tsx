import React, { useEffect, useMemo, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Map, Marker } from "@vis.gl/react-google-maps";
import Header from "../components/Header";
import type { EventItem } from "../types";
import { eventService } from "../services/eventService";
import { useAuth } from "../services/AuthContext";
import ReportForm from "../components/ReportForm";


const mapContainerStyle: React.CSSProperties = {
  width: "100%",
  height: "320px",
  borderRadius: "16px",
  overflow: "hidden",
};

const EventDetailsPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const eventId = Number(id);

  const [event, setEvent] = useState<EventItem | null>(null);
  const [loading, setLoading] = useState(true);
  const [joining, setJoining] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [isUpdatingParticipation, setIsUpdatingParticipation] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [removingParticipantId, setRemovingParticipantId] = useState<number | null>(null);
  const [reportOpen, setReportOpen] = useState(false);
 const isAdmin =
  (user as any)?.role === "Admin" || (user as any)?.isAdmin === true;

 const handleDelete = async () => {
  if (!user || !event) return;

  const ok = window.confirm("Are you sure you want to delete this event?");
  if (!ok) return;

  try {
    setDeleting(true);
    await eventService.deleteEvent(event.id);
    navigate("/events");
  } catch (err) {
    console.error(err);
    alert("Failed to delete event.");
  } finally {
    setDeleting(false);
  }
};



  const isCreator = useMemo(() => {
  if (!user || !event) return false;
  return event.creator.id === user.id;
}, [user, event]);




const goToProfile = (userId: number) => navigate(`/profile/${userId}`);

const getInitials = (firstName?: string, lastName?: string) =>
  `${firstName ?? ""} ${lastName ?? ""}`
    .trim()
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((x) => x[0]?.toUpperCase())
    .join("");




  useEffect(() => {
    if (!Number.isFinite(eventId)) {
      setError("Invalid event id.");
      setLoading(false);
      return;
    }

    const fetchEvent = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await eventService.getEventById(eventId);
        setEvent(data);
      } catch (err) {
        console.error(err);
        setError("Failed to load event.");
      } finally {
        setLoading(false);
      }
    };

    fetchEvent();
  }, [eventId]);

  const takenSpots = useMemo(() => event?.participants?.length ?? 0, [event]);
  const capacity = event?.capacity ?? 0;
  const remaining = Math.max(capacity - takenSpots, 0);

  const isParticipant = useMemo(() => {
    if (!user || !event) return false;
    return event.participants?.some((p) => p.id === user.id) ?? false;
  }, [user, event]);

  const handleOpenGoogleMaps = () => {
  if (!event) return;
  const { lat, lng } = event.location;
  const url = `https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}`;
  window.open(url, "_blank", "noopener,noreferrer");
};


  const handleJoin = async () => {
  if (!user) return alert("Please log in to join the event.");
  if (!event) return;

  try {
    setIsUpdatingParticipation(true);
    const updated = await eventService.joinEvent(event.id);
    setEvent(updated);
  } catch (err: any) {
    console.error(err);

    // Optional: show backend error message if present
    const msg =
      err?.response?.data?.message ??
      err?.response?.data ??
      "Failed to join event.";
    alert(msg);
  } finally {
    setIsUpdatingParticipation(false);
  }
};

const handleLeave = async () => {
  if (!user) return alert("Please log in.");
  if (!event) return;

  try {
    setIsUpdatingParticipation(true);
    const updated = await eventService.leaveEvent(event.id);
    setEvent(updated);
  } catch (err: any) {
    console.error(err);
    const msg =
      err?.response?.data?.message ??
      err?.response?.data ??
      "Failed to leave event.";
    alert(msg);
  } finally {
    setIsUpdatingParticipation(false);
  }
};


const handleRemoveParticipant = async (participantId: number) => {
  if (!user || !event) return;

  const ok = window.confirm("Remove this participant from the event?");
  if (!ok) return;

  try {
    setRemovingParticipantId(participantId);
    const updated = await eventService.removeParticipant(event.id, participantId);
    setEvent(updated);
  } catch (err: any) {
    console.error(err);
    const msg =
      err?.response?.data?.message ??
      err?.response?.data ??
      "Failed to remove participant.";
    alert(msg);
  } finally {
    setRemovingParticipantId(null);
  }
};



  if (loading) {
    return (
      <>
        <Header />
        <div style={{ maxWidth: 1100, margin: "0 auto", padding: "2rem 1.5rem", paddingTop: "8%" }}>
          <p>Loading event...</p>
        </div>
      </>
    );
  }

  if (error || !event) {
    return (
      <>
        <Header />
        <div style={{ maxWidth: 1100, margin: "0 auto", padding: "2rem 1.5rem", paddingTop: "8%" }}>
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
          <p>{error ?? "Event not found."}</p>
        </div>
      </>
    );
  }

  return (
    <>
      <Header />

      <div
        style={{
          maxWidth: 1100,
          margin: "0 auto",
          padding: "2rem 1.5rem 3rem",
          display: "grid",
          paddingTop: "8%",
          gridTemplateColumns: "minmax(0, 2fr) minmax(0, 1.3fr)",
          gap: "1.75rem",
        }}
      >
        {/* LEFT COLUMN */}
        <div style={{ gridColumn: "1 / -1" }}>
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
        </div>

        <main
          style={{
            background: "white",
            borderRadius: 20,
            padding: "1.8rem 1.6rem",
            boxShadow: "0 15px 40px rgba(15,23,42,0.08)",
          }}
        >
          <span
            style={{
              display: "inline-block",
              padding: "0.15rem 0.8rem",
              borderRadius: 999,
              background: "#eef2ff",
              color: "#4f46e5",
              fontSize: "0.8rem",
              textTransform: "uppercase",
              letterSpacing: "0.04em",
              fontWeight: 600,
            }}
          >
            {event.eventType}
          </span>

          <h1 style={{ fontSize: "1.8rem", margin: "0.4rem 0 1rem" }}>
            {event.title}
          </h1>

          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
              gap: "0.75rem",
              marginBottom: "1.3rem",
            }}
          >
            <div>
              <span
                style={{
                  display: "block",
                  fontSize: "0.75rem",
                  textTransform: "uppercase",
                  letterSpacing: "0.06em",
                  color: "#6b7280",
                }}
              >
                Host
              </span>
              
              <div
  onClick={() => goToProfile(event.creator.id)}
  title="View profile"
  style={{
    display: "inline-flex",
    alignItems: "center",
    gap: "0.55rem",
    cursor: "pointer",
    userSelect: "none",
    fontWeight: 500,
  }}
>
  {event.creator.profileImageUrl ? (
    <img
      src={event.creator.profileImageUrl}
      alt={`${event.creator.firstName} ${event.creator.lastName}`}
      style={{ width: 28, height: 28, borderRadius: 999, objectFit: "cover" }}
    />
  ) : (
    <div
      style={{
        width: 28,
        height: 28,
        borderRadius: 999,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        fontSize: "0.75rem",
        color: "white",
        fontWeight: 700,
        background: "#64748b",
      }}
    >
      {getInitials(event.creator.firstName, event.creator.lastName)}
    </div>
  )}

  <span>
    {event.creator.firstName} {event.creator.lastName}
  </span>
</div>

               
            </div>

            <div>
              <span
                style={{
                  display: "block",
                  fontSize: "0.75rem",
                  textTransform: "uppercase",
                  letterSpacing: "0.06em",
                  color: "#6b7280",
                }}
              >
                Date & time
              </span>
              <span style={{ fontWeight: 500 }}>
                {new Date(event.dateTime).toLocaleString()}
              </span>
            </div>

            <div>
              <span
                style={{
                  display: "block",
                  fontSize: "0.75rem",
                  textTransform: "uppercase",
                  letterSpacing: "0.06em",
                  color: "#6b7280",
                }}
              >
                Address
              </span>
              <span style={{ fontWeight: 500 }}>{event.address}</span>
            </div>

            <div>
              <span
                style={{
                  display: "block",
                  fontSize: "0.75rem",
                  textTransform: "uppercase",
                  letterSpacing: "0.06em",
                  color: "#6b7280",
                }}
              >
                Capacity
              </span>
              <span style={{ fontWeight: 500 }}>
                {takenSpots}/{capacity} attending
              </span>
            </div>
          </div>

          <h2 style={{ fontSize: "1.05rem", margin: "1rem 0 0.5rem" }}>
            About this event
          </h2>
          <p style={{ lineHeight: 1.6, marginBottom: "1.5rem" }}>
            {event.description}
          </p>

          {!isCreator && (
            <div style={{ display: "flex", gap: "0.75rem", flexWrap: "wrap" }}>
  <button
    onClick={isParticipant ? handleLeave : handleJoin}
    disabled={isUpdatingParticipation}
    style={{
      border: "none",
      background:
        "linear-gradient(135deg, #4f46e5 0%, #6366f1 50%, #8b5cf6 100%)",
      color: "white",
      padding: "0.6rem 1.4rem",
      borderRadius: 999,
      fontWeight: 600,
      cursor: isUpdatingParticipation ? "default" : "pointer",
      boxShadow: "0 10px 25px rgba(79,70,229,0.25)",
      opacity: isUpdatingParticipation ? 0.75 : 1,
    }}
  >
    {isUpdatingParticipation
      ? isParticipant
        ? "Leaving..."
        : "Joining..."
      : isParticipant
      ? "Leave event"
      : "Join event"}
  </button>

  
 <button
      type="button"
      onClick={() => setReportOpen(true)}
      style={{
        border: "1px solid #ef4444",
        background: "white",
        color: "#ef4444",
        padding: "0.6rem 1.4rem",
        borderRadius: 999,
        fontWeight: 700,
        cursor: "pointer",
      }}
    >
      Report
    </button>

  </div>
)}

<ReportForm
  isOpen={reportOpen}
  onClose={() => setReportOpen(false)}
  targetId={event.id}
  targetType="EVENT"
  title="Report this event"
/>



{(isCreator || isAdmin) && (
  <div style={{ display: "flex", gap: "0.75rem", marginTop: "0.75rem" }}>
    {isCreator && (
      <button
        onClick={() => navigate(`/events/${event.id}/edit`)}
        style={{
          border: "1px solid #d4d4d8",
          background: "white",
          color: "#374151",
          padding: "0.6rem 1.4rem",
          borderRadius: 999,
          fontWeight: 600,
          cursor: "pointer",
        }}
      >
        Edit
      </button>
    )}

    {(isCreator || isAdmin) && (
      <button
        onClick={handleDelete}
        style={{
          border: "none",
          background: "#ef4444",
          color: "white",
          padding: "0.6rem 1.4rem",
          borderRadius: 999,
          fontWeight: 600,
          cursor: "pointer",
        }}
      >
        Delete
      </button>
    )}
  </div>
)}



        </main>

        {/* RIGHT COLUMN */}
        <aside style={{ display: "flex", flexDirection: "column", gap: "1.2rem" }}>
          <div
            style={{
              background: "white",
              borderRadius: 20,
              padding: "1.4rem 1.4rem 1.6rem",
              boxShadow: "0 14px 30px rgba(15,23,42,0.06)",
            }}
          >
            <h2 style={{ fontSize: "1.05rem", margin: "0 0 0.4rem" }}>
              Location
            </h2>
            <p style={{ fontSize: "0.85rem", color: "#6b7280", marginTop: 0 }}>
              {event.address}
            </p>
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

           <div style={{ display: "flex", justifyContent: "flex-end", marginTop: "0.75rem" }}>
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
      whiteSpace: "nowrap",
    }}
  >
    Get directions
  </button>
</div>


          </div>

          <div
            style={{
              background: "white",
              borderRadius: 20,
              padding: "1.4rem 1.4rem 1.6rem",
              boxShadow: "0 14px 30px rgba(15,23,42,0.06)",
            }}
          >
            <h2 style={{ fontSize: "1.05rem", margin: "0 0 0.4rem" }}>
              Participants{" "}
              <span
                style={{
                  fontSize: "0.8rem",
                  background: "#eff6ff",
                  color: "#1d4ed8",
                  borderRadius: 999,
                  padding: "0.15rem 0.6rem",
                  marginLeft: "0.4rem",
                }}
              >
                {takenSpots}
              </span>
            </h2>

            <ul style={{ listStyle: "none", padding: 0, margin: "0.3rem 0 0.9rem" }}>
  {event.participants.map((p) => {
    const name = `${p.firstName} ${p.lastName}`.trim();
    const initials = name
      .split(" ")
      .filter(Boolean)
      .slice(0, 2)
      .map((x) => x[0]?.toUpperCase())
      .join("");

    return (
      <li
        key={p.id}
        onClick={() => goToProfile(p.id)}
        style={{
          display: "flex",
          alignItems: "center",
          gap: "0.6rem",
          padding: "0.35rem 0",
          cursor: "pointer",
          userSelect: "none"
        }}
      >
        {p.profileImageUrl ? (
          <img
            src={p.profileImageUrl}
            alt={name}
            style={{ width: 30, height: 30, borderRadius: 999, objectFit: "cover" }}
          />
        ) : (
          <div
            style={{
              width: 30,
              height: 30,
              borderRadius: 999,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              fontSize: "0.8rem",
              color: "white",
              fontWeight: 600,
              background: "#64748b",
            }}
          >
            {initials}
          </div>
        )}

        <span>{name}</span>
        {isCreator && p.id !== event.creator.id && (
  <button
    onClick={(e) => {
      e.stopPropagation(); // don't navigate to profile
      handleRemoveParticipant(p.id);
    }}
    disabled={removingParticipantId === p.id}
    title="Remove participant"
    style={{
      marginLeft: "auto",
      width: 28,
      height: 28,
      borderRadius: 999,
      border: "none",
      background: "#ef4444",
      color: "white",
      fontWeight: 800,
      cursor: removingParticipantId === p.id ? "default" : "pointer",
      opacity: removingParticipantId === p.id ? 0.7 : 1,
      display: "inline-flex",
      alignItems: "center",
      justifyContent: "center",
      lineHeight: 1,
    }}
  >
    ×
  </button>
)}


      </li>
    );
  })}
</ul>


            <p style={{ fontSize: "0.85rem", color: "#6b7280" }}>
              Spots left:{" "}
              <span
                style={{
                  color: remaining > 0 ? "#16a34a" : "#b91c1c",
                  fontWeight: 500,
                }}
              >
                {remaining}
              </span>
            </p>
          </div>
        </aside>
      </div>
    </>
  );
};

export default EventDetailsPage;
