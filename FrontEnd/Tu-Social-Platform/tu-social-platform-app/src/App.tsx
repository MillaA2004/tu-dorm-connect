import React from "react";
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
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
import PostPage from "./pages/PostPage";
import PostDetailsPage from "./pages/PostDetailsPage";
import ListingPage from "./pages/ListingPage";
import CreateListingPage from "./pages/CreateListingPage";
import ListingDetailsPage from "./pages/ListingDetailsPage";
import EditListingPage from "./pages/EditListingPage";
import MyListingsPage from "./pages/MyListingPage";
import QuestionnairePage from "./pages/QuestionnairePage";
import MatchesPage from "./pages/MatchesPage";
import DormListPage from "./pages/DormListPage";
import AddDorm from "./pages/AddDorm";
import EditDorm from "./pages/EditDorm";
import DormDetailsPage from "./pages/DormDetailsPage";
import ReportsPage from "./pages/ReportsPage";
import ProtectedRoute from "./services/ProtectedRoute";


const App: React.FC = () => {
  return (
    <AuthProvider>
      <APIProvider apiKey={MAPS_API_KEY}>
        <BrowserRouter>
          <Routes>

            {/* 🔓 PUBLIC ROUTES */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            
            {/* 🔒 PROTECTED ROUTES */}
            <Route element={<ProtectedRoute />}>
              <Route path="/home" element={<HomePage />} />

              {/* Profile */}
              <Route path="/profile/:userId" element={<UserProfilePage />} />
              <Route path="/profile/me" element={<UserProfilePage />} />

              {/* Events */}
              <Route path="/events" element={<EventsPage />} />
              <Route path="/events/new" element={<CreateEventPage />} />
              <Route path="/events/:id" element={<EventDetailsPage />} />
              <Route path="/events/:id/edit" element={<EditEventPage />} />
              <Route path="/events/joined" element={<MyJoinedEventsPage />} />
              <Route path="/events/mine" element={<MyEventsPage />} />

              {/* Posts */}
              <Route path="/posts" element={<PostPage />} />
              <Route path="/posts/:postId" element={<PostDetailsPage />} />

              {/* Listings */}
              <Route path="/listings" element={<ListingPage />} />
              <Route path="/listings/new" element={<CreateListingPage />} />
              <Route path="/listings/:id" element={<ListingDetailsPage />} />
              <Route path="/listings/:id/edit" element={<EditListingPage />} />
              <Route path="/listings/mine" element={<MyListingsPage />} />

              {/* Matching */}
              <Route path="/questionnaire" element={<QuestionnairePage />} />
              <Route path="/matches/:viewerId" element={<MatchesPage />} />

              {/* Dorms */}
              <Route path="/dorms" element={<DormListPage />} />
              <Route path="/dorms/add" element={<AddDorm />} />
              <Route path="/dorms/:id" element={<DormDetailsPage />} />
              <Route path="/dorms/:id/edit" element={<EditDorm />} />

              {/* Admin */}
              <Route path="/reports" element={<ReportsPage />} />
            </Route>

            {/* fallback */}
            <Route path="*" element={<Link to="/login" replace />} />

          </Routes>
        </BrowserRouter>
      </APIProvider>
    </AuthProvider>
  );
};

export default App;
