import React, { useState } from "react";
import { useBackButton } from "../hooks/useBackButton"; // Adjust path as needed

interface BackButtonProps {
  fallbackPath?: string;
  label?: string;
}

const BackButton: React.FC<BackButtonProps> = ({
  fallbackPath = "/",
  label = "Back",
}) => {
  const [isHovered, setIsHovered] = useState(false);
  const handleBack = useBackButton(fallbackPath);

  const pillButtonStyle: React.CSSProperties = {
    display: "inline-flex",
    alignItems: "center",
    gap: "0.4rem",
    padding: "0.5rem 1.2rem",
    backgroundColor: isHovered ? "#f9fafb" : "white",
    border: "1px solid #d1d5db",
    borderRadius: "9999px",
    color: "#1f2937",
    fontSize: "0.9rem",
    fontWeight: 600,
    cursor: "pointer",
    transition: "all 0.2s ease",
  };

  return (
    <button
      onClick={handleBack}
      style={pillButtonStyle}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <span>←</span> {label}
    </button>
  );
};

export default BackButton;
