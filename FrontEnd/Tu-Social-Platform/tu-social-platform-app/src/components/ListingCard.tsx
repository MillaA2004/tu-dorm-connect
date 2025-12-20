import React from "react";
import { useNavigate } from "react-router-dom";
import type { ListingItem } from "../types";

interface ListingCardProps {
  listing: ListingItem;
  currentUserId?: number;
  onDelete?: (id: number) => void;
}

const ListingCard: React.FC<ListingCardProps> = ({
  listing,
  currentUserId,
  onDelete,
}) => {
  const navigate = useNavigate();

  const date = new Date(listing.createdAt);
  const formattedDate = isNaN(date.getTime())
    ? listing.createdAt
    : date.toLocaleDateString();

  const expiryDate = new Date(listing.expiresAt);
  const formattedExpiryDate = isNaN(expiryDate.getTime())
    ? listing.expiresAt
    : expiryDate.toLocaleDateString();

  const isOwner = currentUserId && listing.posterId === currentUserId;

  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (window.confirm("Are you sure you want to delete this listing?")) {
      onDelete?.(listing.id);
    }
  };

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
        transition: "transform 0.2s, box-shadow 0.2s",
        cursor: "pointer",
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = "translateY(-2px)";
        e.currentTarget.style.boxShadow = "0 8px 24px rgba(15,23,42,0.12)";
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = "translateY(0)";
        e.currentTarget.style.boxShadow = "0 6px 18px rgba(15,23,42,0.08)";
      }}
      onClick={() => navigate(`/listings/${listing.id}`)}
    >
      {/* Header with status and date */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <div
          style={{
            display: "flex",
            alignItems: "center",
            gap: "0.6rem",
          }}
        >
          {/* Listing type indicator */}
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
              fontSize: "1.1rem",
            }}
          >
            🏠
          </div>

          <div>
            <div style={{ fontWeight: 600, fontSize: "0.9rem" }}>
              {listing.dorm}
            </div>
            <div style={{ fontSize: "0.8rem", color: "#6b7280" }}>
              {isOwner ? "Your Listing" : "Available"}
            </div>
          </div>
        </div>

        <div style={{ fontSize: "0.8rem", color: "#4b5563" }}>
          {formattedDate}
        </div>
      </div>

      {/* Title */}
      <div>
        <h3 style={{ margin: 0, fontSize: "1.1rem" }}>{listing.title}</h3>
      </div>

      {/* Description */}
      <div style={{ fontSize: "0.85rem", color: "#4b5563" }}>
        <p style={{ margin: "0.2rem 0", lineHeight: 1.5 }}>
          {listing.description.length > 100
            ? `${listing.description.slice(0, 100)}...`
            : listing.description}
        </p>
      </div>

      {/* Price and details */}
      <div style={{ fontSize: "0.85rem", color: "#4b5563" }}>
        <p style={{ margin: "0.2rem 0" }}>
          <strong>Price:</strong> {listing.price} BGN/month
        </p>
        <p style={{ margin: "0.2rem 0", fontSize: "0.8rem", color: "#9ca3af" }}>
          Expires: {formattedExpiryDate}
        </p>
      </div>

      {/* Action buttons */}
      <div
        style={{
          marginTop: "0.25rem",
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          gap: "0.5rem",
        }}
      >
        {/* View details button */}
        <button
          onClick={(e) => {
            e.stopPropagation();
            navigate(`/listings/${listing.id}`);
          }}
          style={{
            padding: "0.45rem 0.95rem",
            borderRadius: 999,
            border: "1px solid #16a34a",
            background: "white",
            color: "#16a34a",
            cursor: "pointer",
            fontSize: "0.85rem",
            fontWeight: 500,
            transition: "all 0.2s",
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.background = "#16a34a";
            e.currentTarget.style.color = "white";
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.background = "white";
            e.currentTarget.style.color = "#16a34a";
          }}
        >
          View details
        </button>

        {/* Owner actions or contact button */}
        {isOwner ? (
          <div style={{ display: "flex", gap: "0.5rem" }}>
            <button
              onClick={(e) => {
                e.stopPropagation();
                navigate(`/listings/${listing.id}/edit`);
              }}
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
              Edit
            </button>
            <button
              onClick={handleDelete}
              style={{
                padding: "0.45rem 0.95rem",
                borderRadius: 999,
                border: "1px solid #ef4444",
                background: "white",
                color: "#ef4444",
                cursor: "pointer",
                fontSize: "0.85rem",
                fontWeight: 500,
                transition: "all 0.2s",
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.background = "#ef4444";
                e.currentTarget.style.color = "white";
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.background = "white";
                e.currentTarget.style.color = "#ef4444";
              }}
            >
              Delete
            </button>
          </div>
        ) : (
          <button
            onClick={(e) => {
              e.stopPropagation();
              // Navigate to contact or message functionality
              alert("Contact poster functionality");
            }}
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
            Contact
          </button>
        )}
      </div>

      {/* Status indicator */}
      {!listing.isActive && (
        <div
          style={{
            padding: "0.4rem 0.8rem",
            borderRadius: 8,
            background: "#fef2f2",
            color: "#ef4444",
            fontSize: "0.8rem",
            fontWeight: 600,
            textAlign: "center",
          }}
        >
          Inactive
        </div>
      )}
    </div>
  );
};

export default ListingCard;
