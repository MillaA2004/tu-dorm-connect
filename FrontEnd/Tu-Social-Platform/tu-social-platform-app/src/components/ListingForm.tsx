import React, { useState, useEffect } from "react"; 
import { useNavigate } from "react-router-dom";
import { useAuth } from "../services/AuthContext";
import { listingService } from "../services/ListingService";
import type { ListingRequestDTO, DormSummary } from "../types";

const ListingForm: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  // Form State
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [dormId, setDormId] = useState(""); 
  const [price, setPrice] = useState<number | "">("");
  const [expiryDays, setExpiryDays] = useState<number | "">("");

  // Data State
  const [dorms, setDorms] = useState<DormSummary[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 1. Fetch Dorms on Load
  useEffect(() => {
    listingService
      .getDormOptions()
      .then((data) => {
        if (Array.isArray(data)) {
          setDorms(data);
        } else {
          console.error("Expected array of dorms but got:", data);
          setDorms([]);
        }
      })
      .catch((err) => console.error("Failed to load dorms", err));
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!user) return alert("You must be logged in to create a listing.");

    if (!title.trim()) return alert("Add a title");
    if (!description.trim()) return alert("Add a description");
    if (!dormId) return alert("Select a dorm");
    if (price === "" || price <= 0) return alert("Set a positive price");

    const payload: ListingRequestDTO = {
      title: title.trim(),
      description: description.trim(),
      price: Number(price),
      dormId: Number(dormId),
      expiryDays: expiryDays === "" ? null : Number(expiryDays),
    };

    try {
      setIsSubmitting(true);
      await listingService.createListing(user.id, payload);

      // Reset form
      setTitle("");
      setDescription("");
      setDormId("");
      setPrice("");
      setExpiryDays("");

      navigate("/listings");
    } catch (err: any) {
      console.error(err);

      if (err?.response?.data?.message?.includes("questionnaire")) {
        alert(
          "You must complete your questionnaire before creating a listing."
        );
        navigate("/questionnaire");
        return;
      }

      alert("Failed to create listing. Please try again.");
    } finally {
      setIsSubmitting(false);
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
      <h2 style={{ margin: 0, fontSize: "1.5rem" }}>Create Listing</h2>

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
          placeholder="Cozy room for rent"
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
          placeholder="Describe the room, roommates, rules, etc."
        />
      </div>

      {/* Dorm */}
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
          <label style={{ fontWeight: 500 }}>Price (BGN/month)</label>
          <input
            type="number"
            min={1}
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
            placeholder="e.g. 70.99"
          />
        </div>

        <div style={{ display: "grid", gap: "0.35rem" }}>
          <label style={{ fontWeight: 500 }}>Expires in (days)</label>
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
              setExpiryDays(e.target.value === "" ? "" : Number(e.target.value))
            }
            placeholder="Optional"
          />
        </div>
      </div>

      {/* Submit */}
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
          boxShadow: "0 8px 20px rgba(37,99,235,0.35)",
          opacity: isSubmitting ? 0.75 : 1,
        }}
      >
        {isSubmitting ? "Creating..." : "Create Listing"}
      </button>
    </form>
  );
};

export default ListingForm;
