import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import type { ListingItem } from "../types";
import { listingService } from "../services/ListingService";
import { useAuth } from "../services/AuthContext";
import Header from "../components/Header";

const EditListingPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const listingId = Number(id);

  const [listing, setListing] = useState<ListingItem | null>(null);
  const [loading, setLoading] = useState(true);

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState<number | "">("");
  const [dorm, setDorm] = useState("");
  const [expiryDays, setExpiryDays] = useState<number | "">("");

  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!Number.isFinite(listingId)) {
      setLoading(false);
      return;
    }

    const fetchListing = async () => {
      try {
        setLoading(true);
        const data = await listingService.getListingById(listingId);
        setListing(data);

        // Prefill form fields
        setTitle(data.title);
        setDescription(data.description);
        setPrice(data.price);
        setDorm(data.dorm);
        setExpiryDays(data.expiryDays ?? "");
      } catch (err) {
        console.error(err);
        setListing(null);
      } finally {
        setLoading(false);
      }
    };

    fetchListing();
  }, [listingId]);

  // Only poster can edit
  const isPoster = !!user && !!listing && listing.posterId === user.id;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return alert("You must be logged in.");
    if (!listing) return;

    if (!isPoster) {
      alert("You are not allowed to edit this listing.");
      return;
    }

    if (!title.trim()) return alert("Add a title");
    if (!description.trim()) return alert("Add a description");
    if (!dorm.trim()) return alert("Add a dorm name");
    if (price === "" || Number(price) <= 0)
      return alert("Set a positive price");
    if (expiryDays === "" || Number(expiryDays) <= 0)
      return alert("Set positive expiry days");

    const payload = {
      title: title.trim(),
      description: description.trim(),
      price: Number(price),
      dorm: dorm.trim(),
      posterId: user.id,
      expiryDays: Number(expiryDays),
    };

    try {
      setSaving(true);
      await listingService.updateListing(listing.id, payload, user.id);
      navigate(`/listings/${listing.id}`);
    } catch (err) {
      console.error(err);
      alert("Failed to save changes.");
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    if (listing) navigate(`/listings/${listing.id}`);
    else navigate("/listings");
  };

  if (loading) {
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
          <p>Loading...</p>
        </div>
      </>
    );
  }

  if (!listing) {
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
          <p>Listing not found.</p>
        </div>
      </>
    );
  }

  if (!isPoster) {
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
          <button
            onClick={() => navigate(`/listings/${listing.id}`)}
            style={{
              border: "none",
              background: "none",
              color: "#4f46e5",
              cursor: "pointer",
              marginBottom: "1rem",
            }}
          >
            ← Back to listing
          </button>
          <p>You are not allowed to edit this listing.</p>
        </div>
      </>
    );
  }

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
        <button
          onClick={() => navigate(`/listings/${listing.id}`)}
          style={{
            border: "none",
            background: "none",
            color: "#4f46e5",
            cursor: "pointer",
            marginBottom: "1rem",
          }}
        >
          ← Back to listing
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
          <h2 style={{ margin: 0, fontSize: "1.5rem" }}>Edit Listing</h2>

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
              placeholder="Spacious room near campus"
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
              placeholder="Describe the room, amenities, location..."
            />
          </div>

          {/* Dorm */}
          <div style={{ display: "grid", gap: "0.35rem" }}>
            <label style={{ fontWeight: 500 }}>Dorm</label>
            <input
              style={{
                width: "100%",
                padding: "0.6rem 0.75rem",
                borderRadius: 8,
                border: "1px solid #d4d4d8",
                fontSize: "0.95rem",
              }}
              value={dorm}
              onChange={(e) => setDorm(e.target.value)}
              placeholder="Johnson Hall, Smith Residence, etc."
            />
          </div>

          {/* Price + Expiry Days */}
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "1.2fr 0.8fr",
              gap: "1rem",
            }}
          >
            <div style={{ display: "grid", gap: "0.35rem" }}>
              <label style={{ fontWeight: 500 }}>Price (BGN/month)</label>
              <input
                type="number"
                min={0}
                step={0.01}
                style={{
                  padding: "0.6rem 0.75rem",
                  borderRadius: 8,
                  border: "1px solid #d4d4d8",
                  fontSize: "0.95rem",
                }}
                value={price}
                onChange={(e) =>
                  setPrice(e.target.value === "" ? "" : Number(e.target.value))
                }
                placeholder="e.g. 450"
              />
            </div>

            <div style={{ display: "grid", gap: "0.35rem" }}>
              <label style={{ fontWeight: 500 }}>Expiry (days)</label>
              <input
                type="number"
                min={1}
                style={{
                  padding: "0.6rem 0.75rem",
                  borderRadius: 8,
                  border: "1px solid #d4d4d8",
                  fontSize: "0.95rem",
                }}
                value={expiryDays}
                onChange={(e) =>
                  setExpiryDays(
                    e.target.value === "" ? "" : Number(e.target.value)
                  )
                }
                placeholder="30"
              />
            </div>
          </div>

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

export default EditListingPage;