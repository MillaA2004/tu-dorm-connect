import React, { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import ListingList from "../components/ListingList";
import { type ListingItem } from "../types";
import { listingService } from "../services/ListingService";
import { useAuth } from "../services/AuthContext";

const ListingsPage: React.FC = () => {
  const [listings, setListings] = useState<ListingItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [searchTerm, setSearchTerm] = useState("");
  const [selectedDorm, setSelectedDorm] = useState("all");
  const [maxPrice, setMaxPrice] = useState("");

  const { user } = useAuth();
  const navigate = useNavigate();

  const abortRef = useRef<AbortController | null>(null);

  const fetchAll = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await listingService.getAllListings();
      setListings(data);
    } catch (err) {
      console.error(err);
      setError("Failed to load listings.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAll();
  }, []);

  // Get unique dorm names
  const dormNames = useMemo(() => {
    return ["all", ...new Set(listings.map((l) => l.dorm))];
  }, [listings]);

  // Apply filters
  const filteredListings = useMemo(() => {
    let filtered = [...listings];

    // Dorm filter
    if (selectedDorm !== "all") {
      filtered = filtered.filter((l) => l.dorm === selectedDorm);
    }

    // Price filter
    if (maxPrice) {
      const maxPriceNum = parseFloat(maxPrice);
      if (!isNaN(maxPriceNum)) {
        filtered = filtered.filter((l) => l.price <= maxPriceNum);
      }
    }

    return filtered;
  }, [listings, selectedDorm, maxPrice]);

  const handleSearchSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const q = searchTerm.trim();

    // Cancel previous search
    abortRef.current?.abort();
    const controller = new AbortController();
    abortRef.current = controller;

    try {
      setLoading(true);
      setError(null);

      // If no search term, fetch all
      if (!q) {
        await fetchAll();
        return;
      }

      // Search with keyword
      const data = await listingService.searchListings(
        q,
        selectedDorm,
        controller.signal
      );
      if (!controller.signal.aborted) setListings(data);
    } catch (err: any) {
      if (err?.name === "CanceledError" || err?.name === "AbortError") return;
      console.error(err);
      setError("Failed to search listings.");
    } finally {
      if (!controller.signal.aborted) setLoading(false);
    }
  };

  const handleClearFilters = async () => {
    setSearchTerm("");
    setSelectedDorm("all");
    setMaxPrice("");
    await fetchAll();
  };

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
    // Navigate to messaging or open contact modal
    alert(`Contact poster for listing ${id}`);
    // navigate(`/messages/new?listingId=${id}`);
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
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginBottom: "1.25rem",
            alignItems: "center",
            gap: "1rem",
            flexWrap: "wrap",
          }}
        >
          <h1 style={{ margin: 0, fontSize: "1.7rem" }}>Listings</h1>
          <button
            onClick={() => navigate("/listings/new")}
            style={{
              padding: "0.5rem 1.1rem",
              borderRadius: 999,
              border: "none",
              background:
                "linear-gradient(135deg, rgb(37,99,235), rgb(56,189,248))",
              color: "white",
              cursor: "pointer",
              fontWeight: 600,
              fontSize: "0.9rem",
              boxShadow: "0 6px 16px rgba(37,99,235,0.35)",
              whiteSpace: "nowrap",
            }}
          >
            + Create listing
          </button>
        </div>

        {/* Navigation buttons */}
        <div
          style={{
            display: "flex",
            gap: "0.6rem",
            marginBottom: "1.1rem",
            flexWrap: "wrap",
          }}
        >
          <button
            type="button"
            onClick={() => navigate("/listings/mine")}
            disabled={!user}
            title={!user ? "Log in to view your listings" : ""}
            style={{
              padding: "0.55rem 0.95rem",
              borderRadius: 999,
              border: "1px solid #ddd",
              background: "white",
              cursor: user ? "pointer" : "not-allowed",
              fontWeight: 700,
              opacity: user ? 1 : 0.6,
            }}
          >
            My listings
          </button>
        </div>

        {/* Search + Filters */}
        <form
          onSubmit={handleSearchSubmit}
          style={{
            display: "flex",
            gap: "0.75rem",
            flexWrap: "wrap",
            alignItems: "center",
            marginBottom: "1.25rem",
          }}
        >
          <input
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search listings by title or location"
            style={{
              flex: "1 1 320px",
              padding: "0.65rem 0.9rem",
              borderRadius: 12,
              border: "1px solid #ddd",
              outline: "none",
            }}
          />

          <button
            type="submit"
            style={{
              padding: "0.65rem 0.9rem",
              borderRadius: 12,
              border: "none",
              background: "rgb(37,99,235)",
              color: "white",
              fontWeight: 700,
              cursor: "pointer",
              whiteSpace: "nowrap",
            }}
          >
            Search
          </button>

          {/* Dorm filter */}
          <select
            value={selectedDorm}
            onChange={(e) => setSelectedDorm(e.target.value)}
            style={{
              padding: "0.65rem 0.9rem",
              borderRadius: 12,
              border: "1px solid #ddd",
              background: "white",
              cursor: "pointer",
              minWidth: 140,
            }}
          >
            <option value="all">All dorms</option>
            {dormNames.slice(1).map((dorm) => (
              <option key={dorm} value={dorm}>
                {dorm}
              </option>
            ))}
          </select>

          {/* Max price filter */}
          <input
            type="number"
            value={maxPrice}
            onChange={(e) => setMaxPrice(e.target.value)}
            placeholder="Max price"
            min="0"
            style={{
              width: 120,
              padding: "0.65rem 0.9rem",
              borderRadius: 12,
              border: "1px solid #ddd",
              outline: "none",
            }}
          />

          {(searchTerm.trim() !== "" ||
            selectedDorm !== "all" ||
            maxPrice !== "") && (
            <button
              type="button"
              onClick={handleClearFilters}
              style={{
                padding: "0.65rem 0.9rem",
                borderRadius: 12,
                border: "1px solid #ddd",
                background: "white",
                fontWeight: 700,
                cursor: "pointer",
                whiteSpace: "nowrap",
              }}
            >
              Clear
            </button>
          )}
        </form>

        {loading && <p>Loading listings...</p>}
        {error && <p style={{ color: "red" }}>{error}</p>}

        {!loading && !error && (
          <>
            {filteredListings.length === 0 ? (
              <p style={{ opacity: 0.75 }}>
                No listings match your search/filter.
              </p>
            ) : (
              <ListingList
                listings={filteredListings}
                currentUserId={user?.id}
                onViewDetails={handleViewDetails}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onContact={handleContact}
              />
            )}
          </>
        )}
      </div>
    </>
  );
};

export default ListingsPage;
