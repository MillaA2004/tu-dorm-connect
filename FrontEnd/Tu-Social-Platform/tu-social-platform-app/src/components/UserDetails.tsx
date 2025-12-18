import React, { useEffect, useState } from "react";
import "../styles/UserDetails.css";

export type UserProfileData = {
  firstName: string;
  lastName: string;
  major: string;
  academicYear: number;
  profileImageUrl: string;
};

export type UserProfileUpdate = UserProfileData & {
  newProfileImageFile?: File | null;
};

type UserDetailsProps = UserProfileData & {
  isCurrentUser: boolean;

  
  canDelete: boolean;

  onSaveProfile?: (updated: UserProfileUpdate) => void;
  onMessage?: () => Promise<void> | void;

  
  onDelete?: () => Promise<void> | void;
};

const UserDetails: React.FC<UserDetailsProps> = ({
  firstName,
  lastName,
  major,
  academicYear,
  profileImageUrl,
  isCurrentUser,
  canDelete,
  onSaveProfile,
  onMessage,
  onDelete,
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const [draft, setDraft] = useState<UserProfileData>({
    firstName,
    lastName,
    major,
    academicYear,
    profileImageUrl,
  });

  const [previewImageUrl, setPreviewImageUrl] = useState(profileImageUrl);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  useEffect(() => {
    setDraft({ firstName, lastName, major, academicYear, profileImageUrl });
    setPreviewImageUrl(profileImageUrl);
    setSelectedFile(null);
  }, [firstName, lastName, major, academicYear, profileImageUrl]);

  useEffect(() => {
    return () => {
      if (previewImageUrl && previewImageUrl.startsWith("blob:")) {
        URL.revokeObjectURL(previewImageUrl);
      }
    };
  }, [previewImageUrl]);

  const handleChange = (field: keyof UserProfileData, value: string) => {
    setDraft((prev) => ({ ...prev, [field]: value }));
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const objectUrl = URL.createObjectURL(file);

    setPreviewImageUrl((prev) => {
      if (prev && prev.startsWith("blob:")) {
        URL.revokeObjectURL(prev);
      }
      return objectUrl;
    });

    setSelectedFile(file);
  };

  const handleStartEdit = () => {
    setIsEditing(true);
  };

  const handleCancel = () => {
    setDraft({ firstName, lastName, major, academicYear, profileImageUrl });
    setPreviewImageUrl(profileImageUrl);
    setSelectedFile(null);
    setIsEditing(false);
  };

  const handleSave = () => {
    if (onSaveProfile) {
      onSaveProfile({
        ...draft,
        profileImageUrl: previewImageUrl,
        newProfileImageFile: selectedFile,
      });
    }
    setIsEditing(false);
  };

  const handleDelete = async () => {
    if (!onDelete) return;

    const ok = window.confirm(
      "Are you sure you want to delete this profile? This cannot be undone."
    );
    if (!ok) return;

    try {
      setIsDeleting(true);
      await onDelete();
    } finally {
      setIsDeleting(false);
    }
  };

  const display = { ...draft, profileImageUrl: previewImageUrl };

  return (
    <section className="user-details">
      <div className="user-details__main">
        <div className="user-details__avatar-wrapper">
          <img
            src={previewImageUrl}
            alt={`${display.firstName} ${display.lastName}`}
            className="user-details__avatar"
          />

          {isCurrentUser && isEditing && (
            <>
              <label
                htmlFor="profile-image-input"
                className="user-details__avatar-overlay"
              >
                Change photo
              </label>
              <input
                id="profile-image-input"
                type="file"
                accept="image/*"
                className="user-details__file-input"
                onChange={handleImageChange}
              />
            </>
          )}
        </div>

        <div className="user-details__info">
          {isEditing ? (
            <>
              <div className="user-details__name-row">
                <input
                  className="user-details__input user-details__input--name"
                  value={draft.firstName}
                  onChange={(e) => handleChange("firstName", e.target.value)}
                  placeholder="First name"
                />
                <input
                  className="user-details__input user-details__input--name"
                  value={draft.lastName}
                  onChange={(e) => handleChange("lastName", e.target.value)}
                  placeholder="Last name"
                />
              </div>

              <div className="user-details__meta-row">
                <input
                  className="user-details__input"
                  value={draft.major}
                  onChange={(e) => handleChange("major", e.target.value)}
                  placeholder="Major"
                />
                <span className="user-details__dot">•</span>
                <input
                  className="user-details__input user-details__input--year"
                  value={draft.academicYear}
                  onChange={(e) =>
                    handleChange("academicYear", e.target.value)
                  }
                  placeholder="Academic year"
                />
              </div>
            </>
          ) : (
            <>
              <h1 className="user-details__name">
                {display.firstName} {display.lastName}
              </h1>
              <p className="user-details__meta">
                <span className="user-details__major">
                  {" "}
                  Major: {display.major}
                </span>
                <span className="user-details__dot">•</span>
                <span className="user-details__year">
                  {" "}
                  Academic Year: {display.academicYear}
                </span>
              </p>
            </>
          )}
        </div>
      </div>

      <div className="user-details__actions">
        {isCurrentUser ? (
          isEditing ? (
            <>
              <button
                className="user-details__button user-details__button--secondary"
                onClick={handleCancel}
                disabled={isDeleting}
              >
                Cancel
              </button>
              <button
                className="user-details__button user-details__button--primary"
                onClick={handleSave}
                disabled={isDeleting}
              >
                Save
              </button>
            </>
          ) : (
            <button
              className="user-details__button user-details__button--primary"
              onClick={handleStartEdit}
              disabled={isDeleting}
            >
              Edit profile
            </button>
          )
        ) : (
          <button
            className="user-details__button user-details__button--secondary"
            onClick={onMessage}
            disabled={isDeleting}
          >
            Message
          </button>
        )}

        
        {canDelete && (
          <button
            className="user-details__button user-details__button--danger"
            onClick={handleDelete}
            disabled={isDeleting}
          >
            {isDeleting ? "Deleting..." : "Delete profile"}
          </button>
        )}
      </div>
    </section>
  );
};

export default UserDetails;

