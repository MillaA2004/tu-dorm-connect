import React from "react";
import { useNavigate } from "react-router-dom";
import ListingForm from "../components/ListingForm";
import Header from "../components/Header";
import BackButton from "../components/BackButton";

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
          <BackButton fallbackPath="/listings" />
        </div>

        <ListingForm />
      </div>
    </>
  );
};

export default CreateListingPage;