import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Header from "../components/Header";
import { listingService } from "../services/ListingService";
import { useAuth } from "../services/AuthContext";
import type { ListingResponseDTO, DormSummary } from "../types";

const EditListingPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const listingId = Number(id);

  const [listing, setListing] = useState<ListingResponseDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState<number | "">("");

  const [dormId, setDormId] = useState("");
  const [expiryDays, setExpiryDays] = useState<number | "">("");

  const [dorms, setDorms] = useState<DormSummary[]>([]);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!Number.isFinite(listingId)) {
      setLoading(false);
      return;
    }
    if (!user) {
      navigate("/listings");
      return;
    }

    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);

        const [listingData, dormsData] = await Promise.all([
          listingService.getListingById(listingId),
          listingService.getDormOptions(), 
        ]);

        if (Number(listingData.poster.id) !== Number(user.id)) {
          setError("You are not allowed to edit this listing.");
          setLoading(false);
          return;
        }

        setListing(listingData);
        if (Array.isArray(dormsData)) {
          setDorms(dormsData);
        }

        setTitle(listingData.title);
        setDescription(listingData.description);
        setPrice(listingData.price);
        setDormId(String(listingData.dorm.id));

        setExpiryDays(30);
      } catch (err) {
        console.error(err);
        setError("Failed to load listing data.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [listingId, user, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user || !listing) return;

    if (!title.trim()) return alert("Title is required");
    if (!description.trim()) return alert("Description is required");
    if (!dormId) return alert("Dorm is required");
    if (price === "" || Number(price) <= 0)
      return alert("Valid price is required");

    try {
      setSaving(true);

      const payload = {
        title: title.trim(),
        description: description.trim(),
        price: Number(price),
        dormId: Number(dormId), 
        expiryDays: expiryDays ? Number(expiryDays) : null,
      };

      await listingService.updateListing(listing.id, payload, user.id);

      alert("Listing updated successfully!");
      navigate(`/listings/${listing.id}`);
    } catch (err) {
      console.error(err);
      alert("Failed to update listing.");
    } finally {
      setSaving(false);
    }
  };

  const backButtonStyle: React.CSSProperties = {
    display: "inline-flex",
    alignItems: "center",
    gap: "0.4rem",
    padding: "0.5rem 1.2rem",
    backgroundColor: "white",
    border: "1px solid #d1d5db",
    borderRadius: "9999px",
    color: "#1f2937",
    fontSize: "0.9rem",
    fontWeight: 600,
    cursor: "pointer",
    transition: "all 0.2s ease",
  };

  if (loading) {
    return (
      <>
        <Header />
        <div
          style={{
            maxWidth: 900,
            margin: "0 auto",
            padding: "2rem 1.5rem",
          }}
        >
          <p>Loading...</p>
        </div>
      </>
    );
  }

  if (error) {
    return (
      <>
        <Header />
        <div
          style={{
            maxWidth: 900,
            margin: "0 auto",
            padding: "2rem 1.5rem",
            paddingTop: "8%",
          }}
        >
          <button
            onClick={() => navigate("/listings")}
            style={backButtonStyle}
            onMouseOver={(e) =>
              (e.currentTarget.style.backgroundColor = "#f9fafb")
            }
            onMouseOut={(e) =>
              (e.currentTarget.style.backgroundColor = "white")
            }
          >
            <span>←</span> Back to listings
          </button>
          <div style={{ marginTop: "2rem", color: "#dc2626", fontWeight: 500 }}>
            {error}
          </div>
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
        <div
          style={{
            display: "flex",
            justifyContent: "flex-end",
            marginBottom: "1rem",
          }}
        >
          <button
            onClick={() => navigate(`/listings/${listingId}`)}
            style={backButtonStyle}
            onMouseOver={(e) =>
              (e.currentTarget.style.backgroundColor = "#f9fafb")
            }
            onMouseOut={(e) =>
              (e.currentTarget.style.backgroundColor = "white")
            }
          >
            <span>←</span> Back to listing
          </button>
        </div>

        <form
          onSubmit={handleSubmit}
          style={{
            display: "grid",
            gap: "1rem",
            borderRadius: 16,
            padding: "1.5rem",
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
                minHeight: 100,
              }}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </div>

          <div style={{ display: "grid", gap: "0.35rem" }}>
            <label style={{ fontWeight: 500 }}>Dorm</label>
            <select
              style={{
                padding: "0.6rem 0.75rem",
                borderRadius: 8,
                border: "1px solid #d4d4d8",
                fontSize: "0.95rem",
              }}
              value={dormId}
              onChange={(e) => setDormId(e.target.value)}
            >
              <option value="">Select dorm</option>
              {dorms.map((dorm) => (
                <option key={dorm.id} value={dorm.id}>
                  {dorm.dormName}
                </option>
              ))}
            </select>
          </div>

          {/* Price + Expiry */}
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr",
              gap: "1rem",
            }}
          >
            <div style={{ display: "grid", gap: "0.35rem" }}>
              <label style={{ fontWeight: 500 }}>Price (BGN)</label>
              <input
                type="number"
                min={0}
                step="0.01"
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
              />
            </div>
            <div style={{ display: "grid", gap: "0.35rem" }}>
              <label style={{ fontWeight: 500 }}>Extend Expiry (Days)</label>
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
              onClick={() => navigate(`/listings/${listingId}`)}
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