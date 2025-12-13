import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { APIProvider } from "@vis.gl/react-google-maps";

import EventsPage from "./pages/EventsPage";
import { MAPS_API_KEY } from "./config";
import HomePage from "./pages/Home";
import EventDetailsPage from "./pages/EventDetailsPage";
import CreateEventPage from "./pages/CreateEventPage";
import EditEventPage from "./pages/EditEventPage";
import RegisterPage from "./pages/RegisterPage";
import LoginPage from "./pages/LoginPage";
import UserProfilePage from "./pages/UserProfilePage";

import { AuthProvider } from "./services/AuthContext";
import MyEventsPage from "./pages/MyEventsPage";
import MyJoinedEventsPage from "./pages/MyJoinedEventsPage";

const App: React.FC = () => {
  return (
    <AuthProvider>
    <APIProvider apiKey={MAPS_API_KEY}>
      <BrowserRouter>
        <Routes>
          <Route path="/events" element={<EventsPage />} />
          <Route path="/home" element={<HomePage />} />
          <Route path="/events/:id" element={<EventDetailsPage />} />
          <Route
            path="/events/new"
            element={<CreateEventPage/>}
          />
          <Route path="/events/:id/edit" element={<EditEventPage />} />
          <Route path = "/register" element = {<RegisterPage/>} />
          <Route path = "/login" element = {<LoginPage/>} />
          <Route path="/profile/:userId" element={<UserProfilePage />} />
          <Route path="/profile/me" element={<UserProfilePage />} />
          <Route path = "/events/joined" element= {<MyJoinedEventsPage/>} />
          <Route path="/events/mine" element={<MyEventsPage />} />
          

        </Routes>
      </BrowserRouter>
    </APIProvider>
    </AuthProvider>
  );
};

export default App;
