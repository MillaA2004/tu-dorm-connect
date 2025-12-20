import React from "react";
import ListingCard from "./ListingCard";
import type { ListingItem } from "../types";

interface Props {
  listings: ListingItem[];
  onSelect?: (id: number) => void;
}

const ListingList: React.FC<Props> = ({ listings, onSelect }) => {
  return (
    <div
      style={{
        display: "grid",
        gap: "1.25rem",
        gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))",
      }}
    >
      {listings.map((l) => (
        <ListingCard key={l.id} listing={l} onClick={() => onSelect?.(l.id)} />
      ))}
    </div>
  );
};

export default ListingList;
