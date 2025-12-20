import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Search, Plus, X, Filter, DollarSign, Home } from "lucide-react";
import { listingService } from "../services/ListingService";
import ListingList from "../components/ListingList";
import type { ListingItem } from "../types";
import "../styles/ListingPage.css";

import { useAuth } from "../services/AuthContext";

const ListingsPage: React.FC = () => {
  const navigate = useNavigate();

  const [listings, setListings] = useState<ListingItem[]>([]);
  const [filteredListings, setFilteredListings] = useState<ListingItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [searchTerm, setSearchTerm] = useState("");
  const [selectedDorm, setSelectedDorm] = useState("all");
  const [maxPrice, setMaxPrice] = useState("");
  const [showFilters, setShowFilters] = useState(false);

  const { user } = useAuth();

  // Load listings from backend
  useEffect(() => {
    const fetchListings = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await listingService.getAllListings();
        setListings(data);
        setFilteredListings(data);
      } catch (err) {
        console.error("Failed to load listings", err);
        setError("Failed to load listings. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchListings();
  }, []);

  // Get unique dorm names
  const dormNames = ["all", ...new Set(listings.map((l) => l.dorm))];

  // Apply filters
  useEffect(() => {
    let filtered = [...listings];

    // Search filter
    if (searchTerm.trim()) {
      const searchLower = searchTerm.toLowerCase();
      filtered = filtered.filter(
        (l) =>
          l.title.toLowerCase().includes(searchLower) ||
          l.description.toLowerCase().includes(searchLower) ||
          l.dorm.toLowerCase().includes(searchLower)
      );
    }

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

    setFilteredListings(filtered);
  }, [searchTerm, selectedDorm, maxPrice, listings]);

  // Handler functions
  const handleClearFilters = () => {
    setSearchTerm("");
    setSelectedDorm("all");
    setMaxPrice("");
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

  const handleCreateListing = () => {
    navigate("/listings/new");
  };

  const hasActiveFilters =
    searchTerm.trim() !== "" || selectedDorm !== "all" || maxPrice !== "";

  return (
    <div className="listings-page">
      {/* Header */}
      <header className="listings-header">
        <div className="listings-header-container">
          <h1 className="listings-header-title">🏠 Find a Roomie</h1>

          <button className="btn-primary" onClick={handleCreateListing}>
            <Plus size={18} />
            Create Listing
          </button>
        </div>
      </header>

      {/* Main Content */}
      <div className="listings-container">
        {/* Search and Filter Section */}
        <div className="search-filter-container">
          <div className="search-row">
            {/* Search Bar */}
            <div className="search-input-wrapper">
              <Search size={20} className="search-icon" />
              <input
                className="search-input"
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Search by title, description, or dorm..."
              />
            </div>

            {/* Filters Toggle */}
            <button
              className={`btn-secondary ${showFilters ? "active" : ""}`}
              onClick={() => setShowFilters(!showFilters)}
            >
              <Filter size={18} />
              Filters
            </button>

            {/* Clear Filters */}
            {hasActiveFilters && (
              <button className="btn-clear" onClick={handleClearFilters}>
                <X size={18} />
                Clear
              </button>
            )}
          </div>

          {/* Advanced Filters */}
          {showFilters && (
            <div className="filters-section">
              {/* Dorm Filter */}
              <div className="filter-group">
                <label className="filter-label">
                  <Home size={16} />
                  Dorm
                </label>
                <select
                  className="filter-select"
                  value={selectedDorm}
                  onChange={(e) => setSelectedDorm(e.target.value)}
                >
                  {dormNames.map((dorm) => (
                    <option key={dorm} value={dorm}>
                      {dorm === "all" ? "All Dorms" : dorm}
                    </option>
                  ))}
                </select>
              </div>

              {/* Max Price Filter */}
              <div className="filter-group">
                <label className="filter-label">
                  <DollarSign size={16} />
                  Max Price (BGN)
                </label>
                <input
                  className="filter-input"
                  type="number"
                  value={maxPrice}
                  onChange={(e) => setMaxPrice(e.target.value)}
                  placeholder="Enter max price"
                  min="0"
                />
              </div>
            </div>
          )}
        </div>

        {/* Results Count */}
        <p className="results-count">
          Showing <strong>{filteredListings.length}</strong>{" "}
          {filteredListings.length === 1 ? "listing" : "listings"}
        </p>

        {/* Listings Grid */}
        {loading ? (
          <div className="loading-state">
            <p>Loading listings...</p>
          </div>
        ) : error ? (
          <div className="error-state">
            <p>{error}</p>
          </div>
        ) : filteredListings.length === 0 ? (
          <div className="empty-state">
            <p className="empty-state-title">No listings found</p>
            <p className="empty-state-subtitle">
              Try adjusting your filters or search terms
            </p>
          </div>
        ) : (
          <ListingList
            listings={filteredListings}
            currentUserId={user ? user.id : undefined}
            onViewDetails={handleViewDetails}
            onEdit={handleEdit}
            onDelete={handleDelete}
            onContact={handleContact}
          />
        )}
      </div>
    </div>
  );
};

export default ListingsPage;
