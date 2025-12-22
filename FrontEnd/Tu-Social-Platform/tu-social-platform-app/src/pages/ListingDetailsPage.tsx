import React, { useEffect, useMemo, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Header from "../components/Header";
import type { ListingItem } from "../types";
import { listingService } from "../services/ListingService";
import { chatService } from "../services/ChatService";
import { ChatWindow } from "../components/ChatWindow";
import { useAuth } from "../services/AuthContext";

const ListingDetailsPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const listingId = Number(id);

  const [listing, setListing] = useState<ListingItem | null>(null);
  const [loading, setLoading] = useState(true);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const isPoster = useMemo(() => {
    if (!user || !listing) return false;
    return listing.posterId === user.id;
  }, [user, listing]);

  const [chatOpen, setChatOpen] = useState(false);
  const [chatState, setChatState] = useState<{
    chatId: number;
    title: string;
    otherUserId: number;
  } | null>(null);

  useEffect(() => {
    if (!Number.isFinite(listingId)) {
      setError("Invalid listing id.");
      setLoading(false);
      return;
    }

    const fetchListing = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await listingService.getListingById(listingId);
        setListing(data);
      } catch (err) {
        console.error(err);
        setError("Failed to load listing.");
      } finally {
        setLoading(false);
      }
    };

    fetchListing();
  }, [listingId]);

  const handleDelete = async () => {
    if (!user || !listing) return;

    const ok = window.confirm("Are you sure you want to delete this listing?");
    if (!ok) return;

    try {
      setDeleting(true);
      await listingService.deleteListing(listing.id, user.id);
      navigate("/listings");
    } catch (err) {
      console.error(err);
      alert("Failed to delete listing.");
    } finally {
      setDeleting(false);
    }
  };

  const handleContact = async () => {
    if (!user) {
      alert("Please log in to contact the poster.");
      return;
    }

    if (!listing) return;

    // Prevent messaging yourself
    if (listing.posterId === user.id) {
      alert("You cannot message yourself!");
      return;
    }

    try {
      const chat = await chatService.createDirectChat(listing.posterId);

      setChatState({
        chatId: Number(chat.chatId),
        title: `Chat: ${listing.title}`, 
        otherUserId: listing.posterId,
      });
      setChatOpen(true);
    } catch (err) {
      console.error("Failed to initiate chat", err);
      alert("Failed to open chat. Please try again.");
    }
  };

  if (loading) {
    return (
      <>
        <Header />
        <div
          style={{
            maxWidth: 1100,
            margin: "0 auto",
            padding: "2rem 1.5rem",
            paddingTop: "8%",
          }}
        >
          <p>Loading listing...</p>
        </div>
      </>
    );
  }

  if (error || !listing) {
    return (
      <>
        <Header />

        <div
          style={{
            maxWidth: 1100,
            margin: "0 auto",
            padding: "2rem 1.5rem",
            paddingTop: "8%",
          }}
        >
          <button
            onClick={() => navigate("/listings")}
            style={{
              border: "none",
              background: "none",
              color: "#4f46e5",
              cursor: "pointer",
              marginBottom: "1rem",
            }}
          >
            ← Back to listings
          </button>
          <p>{error ?? "Listing not found."}</p>
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
        {/* Back button */}
        <div style={{ gridColumn: "1 / -1" }}>
          <button
            onClick={() => navigate("/listings")}
            style={{
              border: "none",
              background: "none",
              color: "#4f46e5",
              cursor: "pointer",
              marginBottom: "1rem",
            }}
          >
            ← Back to listings
          </button>
        </div>

        {/* LEFT COLUMN */}
        <main
          style={{
            background: "white",
            borderRadius: 20,
            padding: "1.8rem 1.6rem",
            boxShadow: "0 15px 40px rgba(15,23,42,0.08)",
          }}
        >
          {(() => {
            const isExpired = new Date(listing.expiresAt) < new Date();
            const isLive = listing.isActive && !isExpired;

            let label = "Inactive";
            let bg = "#fee2e2";
            let color = "#dc2626";

            if (isLive) {
              label = "Active";
              bg = "#dcfce7"; 
              color = "#16a34a";
            } else if (listing.isActive && isExpired) {
              label = "Expired";
              bg = "#ffedd5"; 
              color = "#c2410c";
            }

            return (
              <span
                style={{
                  display: "inline-block",
                  padding: "0.15rem 0.8rem",
                  borderRadius: 999,
                  background: bg,
                  color: color,
                  fontSize: "0.8rem",
                  textTransform: "uppercase",
                  letterSpacing: "0.04em",
                  fontWeight: 600,
                }}
              >
                {label}
              </span>
            );
          })()}

          <h1 style={{ fontSize: "1.8rem", margin: "0.4rem 0 1rem" }}>
            {listing.title}
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
                Dorm
              </span>
              <span style={{ fontWeight: 500 }}>{listing.dormName}</span>
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
                Price
              </span>
              <span style={{ fontWeight: 500 }}>{listing.price} BGN/month</span>
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
                Posted
              </span>
              <span style={{ fontWeight: 500 }}>
                {new Date(listing.createdAt).toLocaleDateString()}
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
                Expires
              </span>
              <span style={{ fontWeight: 500 }}>
                {new Date(listing.expiresAt).toLocaleDateString()}
              </span>
            </div>
          </div>

          <h2 style={{ fontSize: "1.05rem", margin: "1rem 0 0.5rem" }}>
            Description
          </h2>
          <p style={{ lineHeight: 1.6, marginBottom: "1.5rem" }}>
            {listing.description}
          </p>

          {!isPoster && (
            <button
              onClick={handleContact}
              style={{
                border: "none",
                background:
                  "linear-gradient(135deg, #4f46e5 0%, #6366f1 50%, #8b5cf6 100%)",
                color: "white",
                padding: "0.6rem 1.4rem",
                borderRadius: 999,
                fontWeight: 600,
                cursor: "pointer",
                boxShadow: "0 10px 25px rgba(79,70,229,0.25)",
              }}
            >
              Contact
            </button>
          )}

          <ChatWindow
            isOpen={chatOpen}
            chatId={chatState?.chatId ?? null}
            chatTitle={chatState?.title ?? "Chat"}
            isGroup={false}
            isAdmin={false}
            otherUserId={chatState?.otherUserId ?? null}
            onClose={() => {
              setChatOpen(false);
              setChatState(null);
            }}
          />

          {isPoster && (
            <div
              style={{ display: "flex", gap: "0.75rem", marginTop: "0.75rem" }}
            >
              <button
                onClick={() => navigate(`/listings/${listing.id}/edit`)}
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

              <button
                onClick={handleDelete}
                disabled={deleting}
                style={{
                  border: "none",
                  background: "#ef4444",
                  color: "white",
                  padding: "0.6rem 1.4rem",
                  borderRadius: 999,
                  fontWeight: 600,
                  cursor: deleting ? "default" : "pointer",
                  opacity: deleting ? 0.75 : 1,
                }}
              >
                {deleting ? "Deleting..." : "Delete"}
              </button>
            </div>
          )}
        </main>

        {/* RIGHT COLUMN */}
        <aside
          style={{ display: "flex", flexDirection: "column", gap: "1.2rem" }}
        >
          {/* Listing Info Card */}
          <div
            style={{
              background: "white",
              borderRadius: 20,
              padding: "1.4rem 1.4rem 1.6rem",
              boxShadow: "0 14px 30px rgba(15,23,42,0.06)",
            }}
          >
            <h2 style={{ fontSize: "1.05rem", margin: "0 0 1rem" }}>
              Listing Information
            </h2>

            <div
              style={{
                display: "flex",
                flexDirection: "column",
                gap: "0.75rem",
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
                    marginBottom: "0.25rem",
                  }}
                >
                  Monthly Rent
                </span>
                <span
                  style={{
                    fontWeight: 700,
                    fontSize: "1.5rem",
                    color: "#2563eb",
                  }}
                >
                  {listing.price} BGN
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
                    marginBottom: "0.25rem",
                  }}
                >
                  Status
                </span>
                <span
                  style={{
                    fontWeight: 500,
                    fontSize: "0.95rem",
                    color: listing.isActive ? "#16a34a" : "#dc2626",
                  }}
                >
                  {listing.isActive ? "Available" : "No longer available"}
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
                    marginBottom: "0.25rem",
                  }}
                >
                  Posted on
                </span>
                <span style={{ fontWeight: 500, fontSize: "0.95rem" }}>
                  {new Date(listing.createdAt).toLocaleDateString("en-US", {
                    year: "numeric",
                    month: "long",
                    day: "numeric",
                  })}
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
                    marginBottom: "0.25rem",
                  }}
                >
                  Expires on
                </span>
                <span style={{ fontWeight: 500, fontSize: "0.95rem" }}>
                  {new Date(listing.expiresAt).toLocaleDateString("en-US", {
                    year: "numeric",
                    month: "long",
                    day: "numeric",
                  })}
                </span>
              </div>
            </div>
          </div>

          {/* Tips Card */}
          <div
            style={{
              background: "white",
              borderRadius: 20,
              padding: "1.4rem 1.4rem 1.6rem",
              boxShadow: "0 14px 30px rgba(15,23,42,0.06)",
            }}
          >
            <h2 style={{ fontSize: "1.05rem", margin: "0 0 0.75rem" }}>
              💡 Safety Tips
            </h2>
            <ul
              style={{
                listStyle: "none",
                padding: 0,
                margin: 0,
                fontSize: "0.85rem",
                lineHeight: 1.6,
                color: "#64748b",
              }}
            >
              <li style={{ marginBottom: "0.5rem" }}>
                • Always meet in public places first
              </li>
              <li style={{ marginBottom: "0.5rem" }}>
                • Verify the listing in person before committing
              </li>
              <li style={{ marginBottom: "0.5rem" }}>
                • Never share personal financial information
              </li>
              <li style={{ marginBottom: "0.5rem" }}>• Trust your instincts</li>
            </ul>
          </div>
        </aside>
      </div>
    </>
  );
};

export default ListingDetailsPage;
