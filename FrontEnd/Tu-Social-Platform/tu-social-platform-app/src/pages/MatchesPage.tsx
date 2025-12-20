import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import { matchService } from "../services/MatchService";
import { type UserMatchDTO } from "../types";
import { useAuth } from "../services/AuthContext";

const MatchesPage: React.FC = () => {
  const [matches, setMatches] = useState<UserMatchDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [minScore, setMinScore] = useState<number>(50);

  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      navigate("/listings");
      return;
    }

    const fetchMatches = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await matchService.getMatchesForViewer(user.id, minScore);
        // Sort by score descending
        setMatches(data.sort((a, b) => b.score - a.score));
      } catch (err) {
        console.error(err);
        setError("Failed to load matches.");
      } finally {
        setLoading(false);
      }
    };

    fetchMatches();
  }, [user, navigate, minScore]);

  const getScoreColor = (score: number) => {
    if (score >= 80) return "#16a34a"; // Green
    if (score >= 60) return "#2563eb"; // Blue
    if (score >= 40) return "#f59e0b"; // Orange
    return "#6b7280"; // Gray
  };

  const getScoreLabel = (score: number) => {
    if (score >= 80) return "Excellent Match";
    if (score >= 60) return "Good Match";
    if (score >= 40) return "Moderate Match";
    return "Low Match";
  };

  const getInitials = (name: string) => {
    return name
      .split(" ")
      .map((n) => n[0])
      .join("")
      .toUpperCase()
      .slice(0, 2);
  };

  return (
    <>
      <Header />
      <div
        style={{
          maxWidth: 1000,
          margin: "0 auto",
          padding: "2rem 1.5rem 3rem",
          paddingTop: "8%",
        }}
      >
        {/* Header */}
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginBottom: "1.5rem",
            gap: "1rem",
            flexWrap: "wrap",
          }}
        >
          <div>
            <h1 style={{ margin: "0 0 0.25rem", fontSize: "1.8rem" }}>
              Your Roommate Matches
            </h1>
            <p style={{ color: "#6b7280", margin: 0 }}>
              Based on your compatibility questionnaire
            </p>
          </div>

          <button
            onClick={() => navigate("/questionnaire")}
            style={{
              padding: "0.6rem 1.2rem",
              borderRadius: 999,
              border: "1px solid #d4d4d8",
              background: "white",
              color: "#374151",
              cursor: "pointer",
              fontWeight: 600,
              fontSize: "0.9rem",
            }}
          >
            Update Questionnaire
          </button>
        </div>

        {/* Back button */}
        <button
          onClick={() => navigate("/listings")}
          style={{
            border: "none",
            background: "none",
            color: "#4f46e5",
            cursor: "pointer",
            marginBottom: "1.5rem",
            padding: 0,
          }}
        >
          ← Back to listings
        </button>

        {/* Filter */}
        <div
          style={{
            background: "white",
            borderRadius: 12,
            padding: "1rem 1.5rem",
            marginBottom: "1.5rem",
            boxShadow: "0 4px 12px rgba(0,0,0,0.05)",
            display: "flex",
            alignItems: "center",
            gap: "1rem",
            flexWrap: "wrap",
          }}
        >
          <label style={{ fontWeight: 500 }}>Minimum Match Score:</label>
          <select
            value={minScore}
            onChange={(e) => setMinScore(Number(e.target.value))}
            style={{
              padding: "0.5rem 0.75rem",
              borderRadius: 8,
              border: "1px solid #d4d4d8",
              cursor: "pointer",
            }}
          >
            <option value={0}>All (0%+)</option>
            <option value={40}>Low (40%+)</option>
            <option value={60}>Good (60%+)</option>
            <option value={80}>Excellent (80%+)</option>
          </select>
          <span style={{ color: "#6b7280", fontSize: "0.9rem" }}>
            Showing {matches.length}{" "}
            {matches.length === 1 ? "match" : "matches"}
          </span>
        </div>

        {/* Loading/Error States */}
        {loading && <p>Loading matches...</p>}
        {error && <p style={{ color: "red" }}>{error}</p>}

        {/* Matches List */}
        {!loading && !error && (
          <>
            {matches.length === 0 ? (
              <div
                style={{
                  textAlign: "center",
                  padding: "3rem 1rem",
                  background: "white",
                  borderRadius: 16,
                  boxShadow: "0 4px 16px rgba(0,0,0,0.08)",
                }}
              >
                <p style={{ fontSize: "1.1rem", marginBottom: "0.5rem" }}>
                  No matches found
                </p>
                <p style={{ color: "#6b7280", marginBottom: "1.5rem" }}>
                  Try lowering the minimum match score or check back later for
                  new listings.
                </p>
                <button
                  onClick={() => navigate("/listings")}
                  style={{
                    padding: "0.6rem 1.3rem",
                    borderRadius: 999,
                    border: "none",
                    background:
                      "linear-gradient(135deg, rgb(37,99,235), rgb(56,189,248))",
                    color: "white",
                    cursor: "pointer",
                    fontWeight: 600,
                    fontSize: "0.95rem",
                    boxShadow: "0 8px 20px rgba(37,99,235,0.35)",
                  }}
                >
                  Browse All Listings
                </button>
              </div>
            ) : (
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  gap: "1rem",
                }}
              >
                {matches.map((match) => (
                  <div
                    key={match.id}
                    style={{
                      background: "white",
                      borderRadius: 16,
                      padding: "1.5rem",
                      boxShadow: "0 6px 18px rgba(15,23,42,0.08)",
                      display: "flex",
                      alignItems: "center",
                      gap: "1.5rem",
                      cursor: "pointer",
                      transition: "transform 0.2s, box-shadow 0.2s",
                    }}
                    onClick={() => {
                      if (match.listingId) {
                        navigate(`/listings/${match.listingId}`);
                      } else {
                        navigate(`/profile/${match.posterId}`);
                      }
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.transform = "translateY(-2px)";
                      e.currentTarget.style.boxShadow =
                        "0 8px 24px rgba(15,23,42,0.12)";
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.transform = "translateY(0)";
                      e.currentTarget.style.boxShadow =
                        "0 6px 18px rgba(15,23,42,0.08)";
                    }}
                  >
                    {/* Avatar */}
                    <div style={{ flexShrink: 0 }}>
                      {match.posterProfileImage ? (
                        <img
                          src={match.posterProfileImage}
                          alt={match.posterName}
                          style={{
                            width: 60,
                            height: 60,
                            borderRadius: 999,
                            objectFit: "cover",
                          }}
                        />
                      ) : (
                        <div
                          style={{
                            width: 60,
                            height: 60,
                            borderRadius: 999,
                            background:
                              "linear-gradient(135deg, rgb(37,99,235), rgb(56,189,248))",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            color: "white",
                            fontWeight: 700,
                            fontSize: "1.2rem",
                          }}
                        >
                          {getInitials(match.posterName)}
                        </div>
                      )}
                    </div>

                    {/* Info */}
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <h3 style={{ margin: "0 0 0.25rem", fontSize: "1.1rem" }}>
                        {match.posterName}
                      </h3>
                      {match.listingTitle && (
                        <p
                          style={{
                            margin: "0 0 0.5rem",
                            color: "#6b7280",
                            fontSize: "0.9rem",
                          }}
                        >
                          {match.listingTitle}
                        </p>
                      )}
                      <div
                        style={{
                          display: "inline-flex",
                          alignItems: "center",
                          gap: "0.5rem",
                          padding: "0.25rem 0.75rem",
                          borderRadius: 999,
                          background: `${getScoreColor(match.score)}15`,
                          color: getScoreColor(match.score),
                          fontSize: "0.85rem",
                          fontWeight: 600,
                        }}
                      >
                        <span style={{ fontSize: "1.2rem" }}>
                          {Math.round(match.score)}%
                        </span>
                        <span>{getScoreLabel(match.score)}</span>
                      </div>
                    </div>

                    {/* Score Circle */}
                    <div
                      style={{
                        flexShrink: 0,
                        width: 80,
                        height: 80,
                        borderRadius: 999,
                        border: `4px solid ${getScoreColor(match.score)}`,
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "center",
                        justifyContent: "center",
                        background: "white",
                      }}
                    >
                      <div
                        style={{
                          fontSize: "1.5rem",
                          fontWeight: 700,
                          color: getScoreColor(match.score),
                        }}
                      >
                        {Math.round(match.score)}
                      </div>
                      <div
                        style={{
                          fontSize: "0.7rem",
                          color: "#6b7280",
                          textTransform: "uppercase",
                        }}
                      >
                        Match
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </>
  );
};

export default MatchesPage;