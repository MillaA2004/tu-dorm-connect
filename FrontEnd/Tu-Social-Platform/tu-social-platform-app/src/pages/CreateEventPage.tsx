import React from "react";
import { useNavigate } from "react-router-dom";
import EventForm from "../components/EventForm";
import Header from "../components/Header";

const CreateEventPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <>
    <Header/>
   
    <div
      style={{
        maxWidth: 900,
        margin: "0 auto",
        padding: "2rem 1.5rem 3rem",
        paddingTop: "8%",
      }}
    >
      <button
        onClick={() => navigate("/events")}
        style={{
          border: "none",
          background: "none",
          color: "#4f46e5",
          cursor: "pointer",
          marginBottom: "1rem",
        }}
      >
        ← Back to events
      </button>

      <EventForm />
    </div>
     </>
  );
};

export default CreateEventPage;
