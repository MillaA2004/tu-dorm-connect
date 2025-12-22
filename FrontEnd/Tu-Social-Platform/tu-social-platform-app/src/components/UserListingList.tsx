import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import ListingList from "./ListingList";
import { listingService } from "../services/ListingService";
import { useAuth } from "../services/AuthContext";
import type { ListingItem } from "../types";

interface Props {
  userId: number;
  onContact?: () => void;
}

const UserListingList: React.FC<Props> = ({ userId, onContact }) => {
  const [listings, setListings] = useState<ListingItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    let isMounted = true;

    const fetchListings = async () => {
      try {
        setLoading(true);
        // Fetch listings specifically for this user profile
        const data = await listingService.getListingsByUserId(userId);

        if (isMounted) {
          setListings(data);
          setError(null);
        }
      } catch (err) {
        console.error("Failed to fetch user listings", err);
        if (isMounted) setError("Failed to load listings.");
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    if (userId) {
      fetchListings();
    }

    return () => {
      isMounted = false;
    };
  }, [userId]);

  const handleDelete = async (listingId: number) => {
    if (!user) return;

    const confirmDelete = window.confirm(
      "Are you sure you want to delete this listing?"
    );
    if (!confirmDelete) return;

    try {
      await listingService.deleteListing(listingId, user.id);
      // Optimistically remove from UI
      setListings((prev) => prev.filter((l) => l.id !== listingId));
    } catch (err) {
      console.error("Failed to delete listing", err);
      alert("Failed to delete listing. Please try again.");
    }
  };

  if (loading) {
    return (
      <p style={{ color: "#6b7280", textAlign: "center" }}>
        Loading listings...
      </p>
    );
  }

  if (error) {
    return <p style={{ color: "crimson", textAlign: "center" }}>{error}</p>;
  }

  if (listings.length === 0) {
    return (
      <div style={{ textAlign: "center", padding: "2rem", color: "#6b7280" }}>
        <p>No active listings found for this user.</p>
      </div>
    );
  }

  return (
    <ListingList
      listings={listings}
      currentUserId={user?.id}
      onViewDetails={(id) => navigate(`/listings/${id}`)}
      onEdit={(id) => navigate(`/listings/${id}/edit`)}
      onDelete={handleDelete}
      // Pass the onContact prop (which is handleMessage from ProfilePage)
      // ListingList automatically hides this button if currentUserId matches the poster
      onContact={onContact ? () => onContact() : undefined}
    />
  );
};

export default UserListingList;