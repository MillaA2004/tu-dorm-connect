import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { APIProvider } from "@vis.gl/react-google-maps";

import EventsPage from "./pages/EventsPage";
import { MAPS_API_KEY } from "./config";
import HomePage from "./pages/Home";

const App: React.FC = () => {
  return (
    <APIProvider apiKey={MAPS_API_KEY}>
      <BrowserRouter>
        <Routes>
          <Route path="/events" element={<EventsPage />} />
          <Route path="/home" element={<HomePage />} />
        </Routes>
      </BrowserRouter>
    </APIProvider>
  );
};

export default App;
