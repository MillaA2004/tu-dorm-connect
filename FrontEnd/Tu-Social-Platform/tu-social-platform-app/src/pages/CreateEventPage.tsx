import React from "react";
import EventForm from "../components/EventForm";
import Header from "../components/Header";
import BackButton from "../components/BackButton";

const CreateEventPage: React.FC = () => {
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
        <BackButton fallbackPath="/events/mine" label="Back to event" />
        <EventForm />
      </div>
    </>
  );
};

export default CreateEventPage;
