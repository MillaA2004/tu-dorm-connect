import React from "react";
import ListingCard from "./ListingCard";
import type { ListingItem } from "../types";

interface Props {
  listings: ListingItem[];
  currentUserId?: number;
  onViewDetails?: (id: number) => void;
  onEdit?: (id: number) => void;
  onDelete?: (id: number) => void;
  onContact?: (id: number) => void;
}

const ListingList: React.FC<Props> = ({
  listings,
  currentUserId,
  onViewDetails,
  onEdit,
  onDelete,
  onContact,
}) => {
  return (
    <div
      style={{
        display: "grid",
        gap: "1.25rem",
      }}
    >
      {listings.map((listing) => (
        <ListingCard
          key={listing.id}
          listing={listing}
          isOwner={currentUserId === listing.posterId}
          onViewDetails={() => onViewDetails?.(listing.id)}
          onEdit={onEdit ? () => onEdit(listing.id) : undefined}
          onDelete={onDelete ? () => onDelete(listing.id) : undefined}
          onContact={onContact ? () => onContact(listing.id) : undefined}
        />
      ))}
    </div>
  );
};

export default ListingList;