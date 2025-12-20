import React, { useEffect, useState } from "react";
import {
  Search,
  Plus,
  X,
  Filter,
  DollarSign,
  Home,
  MapPin,
} from "lucide-react";
import { listingService } from "../services/ListingService";
import type { ListingItem } from "../types";
import "../styles/ListingPage.css";

// Import this if you have auth context
// import { useAuth } from "../services/AuthContext";

const ListingsPage: React.FC = () => {
  const [listings, setListings] = useState<ListingItem[]>([]);
  const [filteredListings, setFilteredListings] = useState<ListingItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [searchTerm, setSearchTerm] = useState("");
  const [selectedDorm, setSelectedDorm] = useState("all");
  const [maxPrice, setMaxPrice] = useState("");
  const [showFilters, setShowFilters] = useState(false);
  const [selectedListing, setSelectedListing] = useState<ListingItem | null>(
    null
  );

  // Mock current user - replace with actual auth
  // const { user } = useAuth();
  const currentUser = { id: 1 };

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

  const handleClearFilters = () => {
    setSearchTerm("");
    setSelectedDorm("all");
    setMaxPrice("");
  };

  const handleDeleteListing = async (id: number) => {
    if (!window.confirm("Are you sure you want to delete this listing?")) {
      return;
    }

    try {
      await listingService.deleteListing(id, currentUser.id);
      setListings((prev) => prev.filter((l) => l.id !== id));
      setSelectedListing(null);
    } catch (err) {
      console.error("Failed to delete listing", err);
      alert("Failed to delete listing. Please try again.");
    }
  };

  const hasActiveFilters =
    searchTerm.trim() !== "" || selectedDorm !== "all" || maxPrice !== "";

  return (
    <div className="listings-page">
      {/* Header */}
      <header className="listings-header">
        <div className="listings-header-container">
          <h1 className="listings-header-title">Find a Roomie</h1>

          <button
            className="btn-primary"
            onClick={() => {
              // Navigate to create listing page
              // navigate('/listings/new')
              alert("Navigate to create listing page");
            }}
          >
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
          <div className="listings-grid">
            {filteredListings.map((listing) => (
              <div
                key={listing.id}
                className="listing-card"
                onClick={() => setSelectedListing(listing)}
              >
                <div className="listing-card-header">
                  <h3 className="listing-card-title">{listing.title}</h3>
                  {listing.posterId === currentUser.id && (
                    <span className="listing-card-badge">Your Listing</span>
                  )}
                </div>

                <p className="listing-card-description">
                  {listing.description}
                </p>

                <div className="listing-card-location">
                  <MapPin size={16} />
                  <span>{listing.dorm}</span>
                </div>

                <div className="listing-card-footer">
                  <div>
                    <span className="listing-card-price">
                      {listing.price} BGN
                    </span>
                    <span className="listing-card-price-label">/month</span>
                  </div>
                  <span className="listing-card-date">
                    {new Date(listing.createdAt).toLocaleDateString()}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Listing Detail Modal */}
      {selectedListing && (
        <div className="modal-overlay" onClick={() => setSelectedListing(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">{selectedListing.title}</h2>
              <button
                className="modal-close-btn"
                onClick={() => setSelectedListing(null)}
              >
                <X size={24} style={{ color: "#64748b" }} />
              </button>
            </div>

            <div className="modal-body">
              {/* Price Banner */}
              <div className="modal-price-banner">
                <div className="modal-price">{selectedListing.price} BGN</div>
                <div className="modal-price-label">per month</div>
              </div>

              {/* Location */}
              <div className="modal-location">
                <MapPin size={20} style={{ color: "#2563eb" }} />
                <span className="modal-location-text">
                  {selectedListing.dorm}
                </span>
              </div>

              {/* Description */}
              <h3 className="modal-section-title">Description</h3>
              <p className="modal-description">{selectedListing.description}</p>

              {/* Additional Info */}
              <div className="modal-info-box">
                <div className="modal-info-row">
                  <strong>Posted:</strong>{" "}
                  {new Date(selectedListing.createdAt).toLocaleDateString()}
                </div>
                <div className="modal-info-row">
                  <strong>Expires:</strong>{" "}
                  {new Date(selectedListing.expiresAt).toLocaleDateString()}
                </div>
                <div className="modal-info-row">
                  <strong>Status:</strong>{" "}
                  {selectedListing.isActive ? "Active" : "Inactive"}
                </div>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="modal-actions">
              {selectedListing.posterId === currentUser.id ? (
                <>
                  <button
                    className="btn-edit"
                    onClick={() => {
                      // Navigate to edit page
                      alert("Edit listing functionality");
                    }}
                  >
                    Edit Listing
                  </button>
                  <button
                    className="btn-delete"
                    onClick={() => handleDeleteListing(selectedListing.id)}
                  >
                    Delete
                  </button>
                </>
              ) : (
                <button
                  className="btn-contact"
                  onClick={() => {
                    // Open contact/message modal
                    alert("Contact poster functionality");
                  }}
                >
                  Contact Poster
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ListingsPage;
