import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import UserDetails, {
  type UserProfileData,
  type UserProfileUpdate,
} from "../components/UserDetails";
import {
  getUserById,
  getUserByEmail,
  updateUser,
  type UserDTO,
} from "../services/UserService";
import { useAuth } from "../services/AuthContext"; 
import Header from "../components/Header";

const UserProfilePage: React.FC = () => {
  const { userId: userIdParam } = useParams<{ userId?: string }>();
  const { user } = useAuth(); 

  const [profile, setProfile] = useState<UserProfileData | null>(null);
  const [userDto, setUserDto] = useState<UserDTO | null>(null);

  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const [error, setError] = useState<string | null>(null);

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

  
  if (!userIdParam && !user) {
    return (
      <main
        style={{
          maxWidth: "1000px",
          margin: "24px auto",
          padding: "0 16px",
        }}
      >
        <p>You must be logged in to view your profile.</p>
      </main>
    );
  }

  if (loading) {
    return (
      <main
        style={{
          maxWidth: "1000px",
          margin: "24px auto",
          padding: "0 16px",
        }}
      >
        <p>Loading profile...</p>
      </main>
    );
  }

  if (notFound) {
    return (
      <main
        style={{
          maxWidth: "1000px",
          margin: "24px auto",
          padding: "0 16px",
        }}
      >
        <p>User not found.</p>
      </main>
    );
  }

  if (!profile) {
    return (
      <main
        style={{
          maxWidth: "1000px",
          margin: "24px auto",
          padding: "0 16px",
        }}
      >
        {error ? (
          <p style={{ color: "crimson" }}>{error}</p>
        ) : (
          <p>Could not load profile.</p>
        )}
      </main>
    );
  }

  
  const isCurrentUser =
    !!user && !!userDto && userDto.email === user.email;

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

  return (
    <>
    <Header/>
    
    <main
      style={{
        maxWidth: "1000px",
        margin: "24px auto",
        padding: "0 16px",
      }}
    >
      {error && (
        <p style={{ color: "crimson", marginBottom: "12px" }}>{error}</p>
      )}

      <UserDetails
        {...profile}
        isCurrentUser={isCurrentUser}
        onSaveProfile={handleSaveProfile}
        onMessage={() => console.log("Message clicked")}
      />
    </main>
    </>
  );
};

export default UserProfilePage;
