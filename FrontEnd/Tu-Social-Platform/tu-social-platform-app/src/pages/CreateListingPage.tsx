import React from "react";
import { useNavigate } from "react-router-dom";
import ListingForm from "../components/ListingForm";
import Header from "../components/Header";

const CreateListingPage: React.FC = () => {
  const navigate = useNavigate();

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
        <button
          onClick={() => navigate("/listings")}
          style={{
            border: "none",
            background: "none",
            color: "#4f46e5",
            cursor: "pointer",
            marginBottom: "1rem",
          }}
        >
          ← Back to listings
        </button>

        <ListingForm />
      </div>
    </>
  );
};

export default CreateListingPage;