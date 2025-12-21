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
        <div
          style={{
            display: "flex",
            justifyContent: "flex-end",
            marginBottom: "1rem",
          }}
        >
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

        <ListingForm />
      </div>
    </>
  );
};

export default CreateListingPage;