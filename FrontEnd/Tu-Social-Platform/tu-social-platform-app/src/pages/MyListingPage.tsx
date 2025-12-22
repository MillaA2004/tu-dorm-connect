import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import ListingList from "../components/ListingList";
import { type ListingItem } from "../types";
import { listingService } from "../services/ListingService";
import { useAuth } from "../services/AuthContext";

const MyListingsPage: React.FC = () => {
  const [listings, setListings] = useState<ListingItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      navigate("/listings");
      return;
    }

    const fetchMyListings = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await listingService.getListingsByUserId(user.id);
        setListings(data);
      } catch (err) {
        console.error(err);
        setError("Failed to load your listings.");
      } finally {
        setLoading(false);
      }
    };

    fetchMyListings();
  }, [user, navigate]);

  const handleViewDetails = (id: number) => {
    navigate(`/listings/${id}`);
  };

  const handleEdit = (id: number) => {
    navigate(`/listings/${id}/edit`);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm("Are you sure you want to delete this listing?")) {
      return;
    }

    try {
      if (!user || !user.id) {
        alert("You must be logged in to delete a listing.");
        return;
      }
      await listingService.deleteListing(id, user.id);
      setListings((prev) => prev.filter((l) => l.id !== id));
    } catch (err) {
      console.error("Failed to delete listing", err);
      alert("Failed to delete listing. Please try again.");
    }
  };

  const handleContact = (id: number) => {
    alert(`This is your own listing.`);
  };

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
        {/* HEADER SECTION: TITLE + BUTTON INLINE */}
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginBottom: "2rem",
          }}
        >
          <h1 style={{ margin: 0, fontSize: "2rem" }}>My Listing</h1>

          <button
            onClick={() => navigate("/listings")}
            style={{
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
            }}
            onMouseOver={(e) =>
              (e.currentTarget.style.backgroundColor = "#f9fafb")
            }
            onMouseOut={(e) =>
              (e.currentTarget.style.backgroundColor = "white")
            }
          >
            <span>←</span> Back to listings
          </button>
        </div>

        {/* CONTENT SECTION */}
        {loading && <p>Loading your listing...</p>}
        {error && <p style={{ color: "red" }}>{error}</p>}

        {!loading && !error && (
          <>
            {listings.length === 0 ? (
              <div
                style={{
                  textAlign: "center",
                  padding: "3rem 1rem",
                  background: "white",
                  borderRadius: 16,
                  boxShadow: "0 4px 16px rgba(0,0,0,0.08)",
                }}
              >
                <p style={{ fontSize: "1.1rem", marginBottom: "0.5rem" }}>
                  You haven't created a listing yet
                </p>
                <p style={{ color: "#6b7280", marginBottom: "1.5rem" }}>
                  Start by creating your own listing to find a roomie!
                </p>
                <button
                  onClick={() => navigate("/listings/new")}
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
                  }}
                >
                  Create your first listing
                </button>
              </div>
            ) : (
              <>
                <p style={{ color: "#6b7280", marginBottom: "1.5rem" }}>
                  You have <strong>{listings.length}</strong>{" "}
                  {listings.length === 1 ? "listing" : "listings"}
                </p>
                <ListingList
                  listings={listings}
                  currentUserId={user?.id}
                  onViewDetails={handleViewDetails}
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                  onContact={handleContact}
                />
              </>
            )}
          </>
        )}
      </div>
    </>
  );
};

export default MyListingsPage;