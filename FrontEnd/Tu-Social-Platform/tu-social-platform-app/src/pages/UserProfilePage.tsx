import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import UserDetails, {
  type UserProfileData,
  type UserProfileUpdate,
} from "../components/UserDetails";
import {
  getUserById,
  getUserByEmail,
  updateUser,
  deleteUser,
  type UserDTO,
} from "../services/UserService";
import { useAuth } from "../services/AuthContext";
import Header from "../components/Header";
import UserPostList from "../components/UserPostList";
import UserEventList from "../components/UserEventList";
import UserListingList from "../components/UserListingList";
import type { EventItem } from "../types";
import LocationModal from "../components/LocationModal";
import { chatService } from "../services/ChatService";
import { ChatWindow } from "../components/ChatWindow";

type Tab = "posts" | "events" | "listings";

const UserProfilePage: React.FC = () => {
  const { userId: userIdParam } = useParams<{ userId?: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [profile, setProfile] = useState<UserProfileData | null>(null);
  const [userDto, setUserDto] = useState<UserDTO | null>(null);

  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [activeTab, setActiveTab] = useState<Tab>("listings");

  const [chatOpen, setChatOpen] = useState(false);
  const [chatState, setChatState] = useState<null | {
    chatId: number;
    title: string;
  }>(null);

  const [selectedEvent, setSelectedEvent] = useState<EventItem | null>(null);

  useEffect(() => {
    let isCancelled = false;

    async function loadUser() {
      setError(null);
      setNotFound(false);

      if (!userIdParam && !user) {
        setLoading(false);
        return;
      }

      setLoading(true);

      try {
        let result: { dto: UserDTO; profile: UserProfileData };

        if (userIdParam) {
          const id = Number(userIdParam);
          if (Number.isNaN(id)) {
            setNotFound(true);
            return;
          }
          result = await getUserById(id);
        } else {
          if (!user?.email) {
            setNotFound(true);
            return;
          }
          result = await getUserByEmail(user.email);
        }

        if (isCancelled) return;

        setUserDto(result.dto);
        setProfile(result.profile);
      } catch (err: any) {
        if (isCancelled) return;

        if (
          err?.message === "USER_NOT_FOUND" ||
          (err as any)?.code === "USER_NOT_FOUND"
        ) {
          setNotFound(true);
        } else {
          console.error("Failed to load user:", err?.response ?? err);
          setError("Something went wrong while loading the user.");
        }
      } finally {
        if (!isCancelled) setLoading(false);
      }
    }

    loadUser();

    return () => {
      isCancelled = true;
    };
  }, [userIdParam, user?.email]);

  useEffect(() => {
    setActiveTab("listings");
  }, [userIdParam]);

  if (!userIdParam && !user) {
    return (
      <main
        style={{ maxWidth: "1000px", margin: "24px auto", padding: "0 16px" }}
      >
        <p>You must be logged in to view your profile.</p>
      </main>
    );
  }

  if (loading) {
    return (
      <main
        style={{ maxWidth: "1000px", margin: "24px auto", padding: "0 16px" }}
      >
        <p>Loading profile...</p>
      </main>
    );
  }

  if (notFound) {
    return (
      <main
        style={{ maxWidth: "1000px", margin: "24px auto", padding: "0 16px" }}
      >
        <p>User not found.</p>
      </main>
    );
  }

  if (!profile) {
    return (
      <main
        style={{ maxWidth: "1000px", margin: "24px auto", padding: "0 16px" }}
      >
        {error ? (
          <p style={{ color: "crimson" }}>{error}</p>
        ) : (
          <p>Could not load profile.</p>
        )}
      </main>
    );
  }

  const isCurrentUser = !!user && !!userDto && userDto.email === user.email;
  const isAdmin =
    (user as any)?.role === "Admin" || (user as any)?.isAdmin === true;
  const canDelete = (isCurrentUser || isAdmin) && !!userDto?.userId;

  const handleSaveProfile = async (updated: UserProfileUpdate) => {
    if (!userDto) return;

    try {
      setError(null);
      const userId = userDto.userId;
      const updatedDto: UserDTO = {
        ...userDto,
        firstName: updated.firstName,
        lastName: updated.lastName,
        major: updated.major,
        year: updated.academicYear,
        profileImageUrl: userDto.profileImageUrl,
      };

      const { dto, profile } = await updateUser(userId, {
        user: updatedDto,
        file: updated.newProfileImageFile ?? undefined,
      });

      setUserDto(dto);
      setProfile(profile);
    } catch (err) {
      console.error(err);
      setError("Failed to save profile changes.");
    }
  };

  const handleDeleteProfile = async () => {
    if (!userDto?.userId) return;

    try {
      setError(null);
      await deleteUser(userDto.userId);
      navigate("/register", { replace: true });
    } catch (err) {
      console.error(err);
      setError("Failed to delete profile.");
    }
  };

  const handleMessage = async () => {
    if (!userDto?.userId) return;

    try {
      setError(null);
      const chat = await chatService.createDirectChat(userDto.userId);
      const title =
        `${profile.firstName} ${profile.lastName}`.trim() ||
        chat.name ||
        "Direct message";

      setChatState({ chatId: Number(chat.chatId), title });
      setChatOpen(true);
    } catch (e) {
      console.error("Failed to open chat:", e);
      setError("Failed to open chat.");
    }
  };

  const handleCheckLocation = (event: EventItem) => setSelectedEvent(event);
  const handleCloseLocation = () => setSelectedEvent(null);
  const shownUserId = userDto?.userId;

  return (
    <>
      <Header />

      <main
        style={{ maxWidth: "1000px", margin: "24px auto", padding: "0 16px" }}
      >
        {error && (
          <p style={{ color: "crimson", marginBottom: "12px" }}>{error}</p>
        )}

        <UserDetails
          {...profile}
          isCurrentUser={isCurrentUser}
          canDelete={canDelete}
          onSaveProfile={handleSaveProfile}
          onMessage={handleMessage}
          onDelete={handleDeleteProfile}
        />

        <div
          style={{
            display: "flex",
            gap: 10,
            justifyContent: "center",
            marginTop: 24,
            marginBottom: 12,
          }}
        >
          <button
            className={`hero-action-button ${
              activeTab === "posts" ? "active" : ""
            }`}
            onClick={() => setActiveTab("posts")}
          >
            Posts
          </button>

          <button
            className={`hero-action-button ${
              activeTab === "events" ? "active" : ""
            }`}
            onClick={() => setActiveTab("events")}
          >
            Events
          </button>

          <button
            className={`hero-action-button ${
              activeTab === "listings" ? "active" : ""
            }`}
            onClick={() => setActiveTab("listings")}
          >
            Listings
          </button>
        </div>

        {shownUserId && activeTab === "posts" && (
          <section style={{ marginTop: 12 }}>
            <UserPostList userId={shownUserId} />
          </section>
        )}

        {selectedEvent && (
          <LocationModal event={selectedEvent} onClose={handleCloseLocation} />
        )}

        {shownUserId && activeTab === "events" && (
          <section style={{ marginTop: 12 }}>
            <UserEventList
              userId={shownUserId}
              onCheckLocation={handleCheckLocation}
            />
          </section>
        )}

        {shownUserId && activeTab === "listings" && (
          <section style={{ marginTop: 12 }}>
            <UserListingList userId={shownUserId} />
          </section>
        )}
      </main>

      <ChatWindow
        isOpen={chatOpen}
        chatId={chatState?.chatId ?? null}
        chatTitle={chatState?.title ?? "Chat"}
        isGroup={false}
        isAdmin={false}
        otherAvatarUrl={userDto?.profileImageUrl ?? ""}
        otherUserId={isCurrentUser ? null : userDto?.userId ?? null} //ako neshto se scbupi gledai tuk!
        onClose={() => {
          setChatOpen(false);
          setChatState(null);
        }}
      />
    </>
  );
};

export default UserProfilePage;
