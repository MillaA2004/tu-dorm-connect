import React, { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import ListingList from "../components/ListingList";
import type { ListingResponseDTO, DormSummary } from "../types";
import { questionnaireService } from "../services/QuestionnaireService";
import { listingService } from "../services/ListingService";
import { useAuth } from "../services/AuthContext";

const ListingsPage: React.FC = () => {
  const [listings, setListings] = useState<ListingResponseDTO[]>([]);
  const [dorms, setDorms] = useState<DormSummary[]>([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [searchTerm, setSearchTerm] = useState("");
  const [selectedDormId, setSelectedDormId] = useState<string>("all");
  const [maxPrice, setMaxPrice] = useState("");
  const [hasQuestionnaire, setHasQuestionnaire] = useState<boolean>(false);

  const { user } = useAuth();
  const navigate = useNavigate();

  const abortRef = useRef<AbortController | null>(null);

  const fetchAll = async () => {
    try {
      setLoading(true);
      setError(null);
      const [listingsData, dormsData] = await Promise.all([
        listingService.getAllListings(user?.id),
        listingService.getDormOptions(),
      ]);
      setListings(listingsData);
      setDorms(dormsData);
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

  // Check if user has completed questionnaire
  useEffect(() => {
    const checkQuestionnaire = async () => {
      if (user) {
        const completed = await questionnaireService.hasCompleted(user.id);
        setHasQuestionnaire(completed);
      }
    };
    checkQuestionnaire();
  }, [user]);

  const dormNames = useMemo(() => {
    return ["all", ...new Set(listings.map((l) => l.dorm.dormName))];
  }, [listings]);

  const filteredListings = useMemo(() => {
    let filtered = [...listings];

    //by Dorm ID
    if (selectedDormId !== "all") {
      const idToMatch = Number(selectedDormId);
      filtered = filtered.filter((l) => l.dorm.id === idToMatch);
    }
    //by Max Price
    if (maxPrice) {
      const maxPriceNum = parseFloat(maxPrice);
      if (!isNaN(maxPriceNum)) {
        filtered = filtered.filter((l) => l.price <= maxPriceNum);
      }
    }

    return filtered;
  }, [listings, selectedDormId, maxPrice]);

  const handleSearchSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const q = searchTerm.trim();

    abortRef.current?.abort();
    const controller = new AbortController();
    abortRef.current = controller;

    try {
      setLoading(true);
      setError(null);

      if (!q) {
        // If search is cleared, reload all
        const data = await listingService.getAllListings(user?.id);
        setListings(data);
      } else {
        const data = await listingService.searchListings(
          q,
          user?.id,
          controller.signal
        );
        if (!controller.signal.aborted) setListings(data);
      }
    } catch (err: any) {
      if (err?.name === "CanceledError") return;
      console.error(err);
      setError("Failed to search listings.");
    } finally {
      if (!controller.signal.aborted) setLoading(false);
    }
  };

  const handleCreateListing = async () => {
    if (!user) {
      alert("Please log in to create a listing.");
      return;
    }
    try {
      const userListings = await listingService.getListingsByUserId(user.id);

      if (userListings.length > 0) {
        alert(
          "You already have an active listing. You must delete it or wait for it to expire before creating a new one."
        );
        return;
      }
      const completed = await questionnaireService.hasCompleted(user.id);
      if (completed) {
        navigate("/listings/new");
      } else {
        const proceed = window.confirm(
          "To create a listing, you must complete the compatibility questionnaire. This helps find the best matches for your room. Proceed to questionnaire?"
        );
        if (proceed) {
          navigate("/questionnaire");
        }
      }
    } catch (err) {
      console.error("Failed to check eligibility", err);
      alert("Something went wrong. Please try again.");
    }
  };

  const handleClearFilters = async () => {
    setSearchTerm("");
    setSelectedDormId("all");
    setMaxPrice("");
    const data = await listingService.getAllListings(user?.id);
    setListings(data);
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
          <h1 style={{ margin: 0, fontSize: "1.7rem" }}>Find a Roomie</h1>
          <button
            onClick={handleCreateListing}
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

        {user && !hasQuestionnaire && (
          <div
            style={{
              background: "linear-gradient(135deg, #eff6ff, #dbeafe)",
              border: "2px solid #60a5fa",
              borderRadius: 12,
              padding: "1rem 1.25rem",
              marginBottom: "1.25rem",
              display: "flex",
              alignItems: "center",
              gap: "1rem",
              justifyContent: "space-between",
              flexWrap: "wrap",
            }}
          >
            <div style={{ flex: 1, minWidth: 200 }}>
              <div
                style={{
                  fontWeight: 600,
                  marginBottom: "0.25rem",
                  color: "#1e40af",
                }}
              >
                🎯 Find Your Perfect Roommate Match!
              </div>
              <p style={{ margin: 0, fontSize: "0.9rem", color: "#3b82f6" }}>
                Complete the compatibility questionnaire to see personalized
                matches
              </p>
            </div>
            <button
              onClick={() => navigate("/questionnaire")}
              style={{
                padding: "0.6rem 1.2rem",
                borderRadius: 999,
                border: "none",
                background: "#2563eb",
                color: "white",
                cursor: "pointer",
                fontWeight: 600,
                fontSize: "0.9rem",
                whiteSpace: "nowrap",
              }}
            >
              Take Questionnaire
            </button>
          </div>
        )}

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
            My listing
          </button>

          {user && hasQuestionnaire && (
            <button
              type="button"
              onClick={() => navigate(`/matches/${user.id}`)}
              style={{
                padding: "0.55rem 0.95rem",
                borderRadius: 999,
                border: "1px solid #2563eb",
                background: "#eff6ff",
                color: "#2563eb",
                cursor: "pointer",
                fontWeight: 700,
              }}
            >
              🎯 View My Matches
            </button>
          )}
        </div>

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
            placeholder="Search listings by keywords..."
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

          {/* Dynamic Dorm filter */}
          <select
            value={selectedDormId}
            onChange={(e) => setSelectedDormId(e.target.value)}
            style={{
              padding: "0.65rem",
              borderRadius: 12,
              border: "1px solid #ddd",
              cursor: "pointer",
              minWidth: 140,
            }}
          >
            <option value="all">All dorms</option>
            {dorms.map((dorm) => (
              <option key={dorm.id} value={dorm.id}>
                {dorm.dormName}
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
            selectedDormId !== "all" ||
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
                //onContact={handleContact}
              />
            )}
          </>
        )}
      </div>
    </>
  );
};

export default ListingsPage;