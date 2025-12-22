import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import { questionnaireService } from "../services/QuestionnaireService";
import { useAuth } from "../services/AuthContext";

interface QuestionnaireData {
  smokes: boolean | null;
  drinks: boolean | null;
  partyHome: boolean | null;
  stayAtHome: boolean | null;
  cleanliness: number | null;
  sharesCleaning: boolean | null;
  mbti: string;
  age: number | null;
  specialty: string;
  earlyRiser: boolean | null;
  bedtime: number | null;
  studiesInRoom: boolean | null;
  needsQuiet: number | null;
  guestFrequency: number | null;
  prefersSocialRoommate: boolean | null;
  cooksInDorm: boolean | null;
  foodSharing: number | null;
  entertainmentFrequency: number | null;
  usesHeadphones: boolean | null;
  personalSpaceImportance: number | null;
  sharesItems: boolean | null;
}

const QuestionnairePage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [loading, setLoading] = useState(false);
  const [existingData, setExistingData] = useState<QuestionnaireData | null>(
    null
  );

  const [formData, setFormData] = useState<QuestionnaireData>({
    smokes: null,
    drinks: null,
    partyHome: null,
    stayAtHome: null,
    cleanliness: null,
    sharesCleaning: null,
    mbti: "",
    age: null,
    specialty: "",
    earlyRiser: null,
    bedtime: null,
    studiesInRoom: null,
    needsQuiet: null,
    guestFrequency: null,
    prefersSocialRoommate: null,
    cooksInDorm: null,
    foodSharing: null,
    entertainmentFrequency: null,
    usesHeadphones: null,
    personalSpaceImportance: null,
    sharesItems: null,
  });

  useEffect(() => {
    if (!user) {
      navigate("/listings");
      return;
    }

    const fetchExisting = async () => {
      try {
        const data = await questionnaireService.getByUser(user.id);
        if (data) {
          setExistingData(data);
          setFormData(data);
        }
      } catch (err) {
        console.log("No existing questionnaire found");
      }
    };

    fetchExisting();
  }, [user, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;

    // Validation
    if (formData.age === null || formData.age < 18) {
      return alert("Please enter a valid age (18+)");
    }
    if (!formData.mbti) return alert("Please select your MBTI type");
    if (!formData.specialty) return alert("Please enter your specialty/major");

    try {
      setLoading(true);
      await questionnaireService.saveForUser(user.id, formData);
      alert("Questionnaire saved successfully!");
      navigate("/listings/new");
    } catch (err) {
      console.error(err);
      alert("Failed to save questionnaire. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleBooleanChange = (
    field: keyof QuestionnaireData,
    value: boolean
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleNumberChange = (
    field: keyof QuestionnaireData,
    value: number
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleStringChange = (
    field: keyof QuestionnaireData,
    value: string
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  return (
    <>
      <Header />
      <div
        style={{
          maxWidth: 800,
          margin: "0 auto",
          padding: "2rem 1.5rem 3rem",
          paddingTop: "8%",
        }}
      >
        <button
          onClick={() => navigate("/listings")}
          style={{
            border: "none",
            background: "none",
            color: "#4f46e5",
            cursor: "pointer",
            marginBottom: "1rem",
          }}
        >
          ← Back to listings
        </button>

        <div
          style={{
            background: "white",
            borderRadius: 16,
            padding: "2rem",
            boxShadow: "0 10px 25px rgba(15, 23, 42, 0.08)",
          }}
        >
          <h1 style={{ margin: "0 0 0.5rem", fontSize: "1.8rem" }}>
            Roommate Compatibility Questionnaire
          </h1>
          <p style={{ color: "#6b7280", marginBottom: "2rem" }}>
            Help us find your perfect roommate match! Answer these questions
            honestly.
            {existingData && " You can update your answers anytime."}
          </p>

          <form onSubmit={handleSubmit}>
            {/* Basic Info */}
            <section style={{ marginBottom: "2rem" }}>
              <h2
                style={{
                  fontSize: "1.2rem",
                  marginBottom: "1rem",
                  color: "#1e293b",
                }}
              >
                Basic Information
              </h2>

              <div style={{ display: "grid", gap: "1rem" }}>
                <div>
                  <label
                    style={{
                      display: "block",
                      fontWeight: 500,
                      marginBottom: "0.5rem",
                    }}
                  >
                    Age *
                  </label>
                  <input
                    type="number"
                    min={18}
                    max={100}
                    value={formData.age || ""}
                    onChange={(e) =>
                      handleNumberChange("age", Number(e.target.value))
                    }
                    style={{
                      width: "100%",
                      padding: "0.6rem 0.75rem",
                      borderRadius: 8,
                      border: "1px solid #d4d4d8",
                    }}
                    required
                  />
                </div>

                <div>
                  <label
                    style={{
                      display: "block",
                      fontWeight: 500,
                      marginBottom: "0.5rem",
                    }}
                  >
                    MBTI Type *
                  </label>
                  <select
                    value={formData.mbti}
                    onChange={(e) => handleStringChange("mbti", e.target.value)}
                    style={{
                      width: "100%",
                      padding: "0.6rem 0.75rem",
                      borderRadius: 8,
                      border: "1px solid #d4d4d8",
                    }}
                    required
                  >
                    <option value="">Select MBTI</option>
                    {[
                      "INTJ",
                      "INTP",
                      "ENTJ",
                      "ENTP",
                      "INFJ",
                      "INFP",
                      "ENFJ",
                      "ENFP",
                      "ISTJ",
                      "ISFJ",
                      "ESTJ",
                      "ESFJ",
                      "ISTP",
                      "ISFP",
                      "ESTP",
                      "ESFP",
                    ].map((type) => (
                      <option key={type} value={type}>
                        {type}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label
                    style={{
                      display: "block",
                      fontWeight: 500,
                      marginBottom: "0.5rem",
                    }}
                  >
                    Specialty/Major *
                  </label>
                  <input
                    type="text"
                    value={formData.specialty}
                    onChange={(e) =>
                      handleStringChange("specialty", e.target.value)
                    }
                    placeholder="e.g., Computer Science, Business, Arts"
                    style={{
                      width: "100%",
                      padding: "0.6rem 0.75rem",
                      borderRadius: 8,
                      border: "1px solid #d4d4d8",
                    }}
                    required
                  />
                </div>
              </div>
            </section>

            {/* Lifestyle & Habits */}
            <section style={{ marginBottom: "2rem" }}>
              <h2
                style={{
                  fontSize: "1.2rem",
                  marginBottom: "1rem",
                  color: "#1e293b",
                }}
              >
                Lifestyle & Habits
              </h2>

              <BooleanQuestion
                label="Do you smoke?"
                value={formData.smokes}
                onChange={(v) => handleBooleanChange("smokes", v)}
              />
              <BooleanQuestion
                label="Do you drink alcohol?"
                value={formData.drinks}
                onChange={(v) => handleBooleanChange("drinks", v)}
              />
              <BooleanQuestion
                label="Do you host parties at home?"
                value={formData.partyHome}
                onChange={(v) => handleBooleanChange("partyHome", v)}
              />
              <BooleanQuestion
                label="Do you prefer staying at home most of the time?"
                value={formData.stayAtHome}
                onChange={(v) => handleBooleanChange("stayAtHome", v)}
              />
            </section>

            {/* Cleanliness */}
            <section style={{ marginBottom: "2rem" }}>
              <h2
                style={{
                  fontSize: "1.2rem",
                  marginBottom: "1rem",
                  color: "#1e293b",
                }}
              >
                Cleanliness
              </h2>

              <ScaleQuestion
                label="How clean do you keep your space?"
                value={formData.cleanliness}
                onChange={(v) => handleNumberChange("cleanliness", v)}
                min="Very messy"
                max="Extremely clean"
              />
              <BooleanQuestion
                label="Are you willing to share cleaning duties?"
                value={formData.sharesCleaning}
                onChange={(v) => handleBooleanChange("sharesCleaning", v)}
              />
            </section>

            {/* Daily Routine */}
            <section style={{ marginBottom: "2rem" }}>
              <h2
                style={{
                  fontSize: "1.2rem",
                  marginBottom: "1rem",
                  color: "#1e293b",
                }}
              >
                Daily Routine
              </h2>

              <BooleanQuestion
                label="Are you an early riser?"
                value={formData.earlyRiser}
                onChange={(v) => handleBooleanChange("earlyRiser", v)}
              />
              <ScaleQuestion
                label="What time do you usually go to bed?"
                value={formData.bedtime}
                onChange={(v) => handleNumberChange("bedtime", v)}
                min="Very early (9-10 PM)"
                max="Very late (2+ AM)"
              />
            </section>

            {/* Study Habits */}
            <section style={{ marginBottom: "2rem" }}>
              <h2
                style={{
                  fontSize: "1.2rem",
                  marginBottom: "1rem",
                  color: "#1e293b",
                }}
              >
                Study Habits
              </h2>

              <BooleanQuestion
                label="Do you study in your room?"
                value={formData.studiesInRoom}
                onChange={(v) => handleBooleanChange("studiesInRoom", v)}
              />
              <ScaleQuestion
                label="How much do you need quiet when studying?"
                value={formData.needsQuiet}
                onChange={(v) => handleNumberChange("needsQuiet", v)}
                min="Not important"
                max="Absolutely essential"
              />
            </section>

            {/* Social Preferences */}
            <section style={{ marginBottom: "2rem" }}>
              <h2
                style={{
                  fontSize: "1.2rem",
                  marginBottom: "1rem",
                  color: "#1e293b",
                }}
              >
                Social Preferences
              </h2>

              <ScaleQuestion
                label="How often do you have guests over?"
                value={formData.guestFrequency}
                onChange={(v) => handleNumberChange("guestFrequency", v)}
                min="Never"
                max="Very often"
              />
              <BooleanQuestion
                label="Do you prefer a social, outgoing roommate?"
                value={formData.prefersSocialRoommate}
                onChange={(v) =>
                  handleBooleanChange("prefersSocialRoommate", v)
                }
              />
            </section>

            {/* Food & Eating */}
            <section style={{ marginBottom: "2rem" }}>
              <h2
                style={{
                  fontSize: "1.2rem",
                  marginBottom: "1rem",
                  color: "#1e293b",
                }}
              >
                Food & Eating
              </h2>

              <BooleanQuestion
                label="Do you cook in the dorm?"
                value={formData.cooksInDorm}
                onChange={(v) => handleBooleanChange("cooksInDorm", v)}
              />
              <ScaleQuestion
                label="How comfortable are you with sharing food?"
                value={formData.foodSharing}
                onChange={(v) => handleNumberChange("foodSharing", v)}
                min="Never share"
                max="Always share"
              />
            </section>

            {/* Noise & Entertainment */}
            <section style={{ marginBottom: "2rem" }}>
              <h2
                style={{
                  fontSize: "1.2rem",
                  marginBottom: "1rem",
                  color: "#1e293b",
                }}
              >
                Noise & Entertainment
              </h2>

              <ScaleQuestion
                label="How often do you play music/games/watch videos?"
                value={formData.entertainmentFrequency}
                onChange={(v) =>
                  handleNumberChange("entertainmentFrequency", v)
                }
                min="Rarely"
                max="Very often"
              />
              <BooleanQuestion
                label="Do you use headphones for entertainment?"
                value={formData.usesHeadphones}
                onChange={(v) => handleBooleanChange("usesHeadphones", v)}
              />
            </section>

            {/* Boundaries */}
            <section style={{ marginBottom: "2rem" }}>
              <h2
                style={{
                  fontSize: "1.2rem",
                  marginBottom: "1rem",
                  color: "#1e293b",
                }}
              >
                Boundaries & Sharing
              </h2>

              <ScaleQuestion
                label="How important is personal space to you?"
                value={formData.personalSpaceImportance}
                onChange={(v) =>
                  handleNumberChange("personalSpaceImportance", v)
                }
                min="Not important"
                max="Extremely important"
              />
              <BooleanQuestion
                label="Are you comfortable sharing personal items?"
                value={formData.sharesItems}
                onChange={(v) => handleBooleanChange("sharesItems", v)}
              />
            </section>

            {/* Submit Buttons */}
            <div style={{ display: "flex", gap: "1rem", marginTop: "2rem" }}>
              <button
                type="submit"
                disabled={loading}
                style={{
                  flex: 1,
                  padding: "0.75rem 1.5rem",
                  borderRadius: 999,
                  border: "none",
                  background:
                    "linear-gradient(135deg, rgb(37,99,235), rgb(56,189,248))",
                  color: "white",
                  cursor: loading ? "default" : "pointer",
                  fontWeight: 600,
                  fontSize: "1rem",
                  boxShadow: "0 8px 20px rgba(37,99,235,0.35)",
                  opacity: loading ? 0.75 : 1,
                }}
              >
                {loading
                  ? "Saving..."
                  : existingData
                  ? "Update & Continue"
                  : "Save & Continue"}
              </button>

              <button
                type="button"
                onClick={() => navigate("/listings")}
                style={{
                  padding: "0.75rem 1.5rem",
                  borderRadius: 999,
                  border: "1px solid #d4d4d8",
                  background: "white",
                  color: "#374151",
                  cursor: "pointer",
                  fontWeight: 500,
                  fontSize: "1rem",
                }}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
};

// Helper component for boolean questions
const BooleanQuestion: React.FC<{
  label: string;
  value: boolean | null;
  onChange: (value: boolean) => void;
}> = ({ label, value, onChange }) => (
  <div style={{ marginBottom: "1.5rem" }}>
    <label
      style={{ display: "block", fontWeight: 500, marginBottom: "0.75rem" }}
    >
      {label}
    </label>
    <div style={{ display: "flex", gap: "1rem" }}>
      <button
        type="button"
        onClick={() => onChange(true)}
        style={{
          flex: 1,
          padding: "0.6rem",
          borderRadius: 8,
          border: value === true ? "2px solid #2563eb" : "1px solid #d4d4d8",
          background: value === true ? "#eff6ff" : "white",
          color: value === true ? "#2563eb" : "#374151",
          fontWeight: value === true ? 600 : 400,
          cursor: "pointer",
        }}
      >
        Yes
      </button>
      <button
        type="button"
        onClick={() => onChange(false)}
        style={{
          flex: 1,
          padding: "0.6rem",
          borderRadius: 8,
          border: value === false ? "2px solid #2563eb" : "1px solid #d4d4d8",
          background: value === false ? "#eff6ff" : "white",
          color: value === false ? "#2563eb" : "#374151",
          fontWeight: value === false ? 600 : 400,
          cursor: "pointer",
        }}
      >
        No
      </button>
    </div>
  </div>
);

// Helper component for scale questions (1-5)
const ScaleQuestion: React.FC<{
  label: string;
  value: number | null;
  onChange: (value: number) => void;
  min: string;
  max: string;
}> = ({ label, value, onChange, min, max }) => (
  <div style={{ marginBottom: "1.5rem" }}>
    <label
      style={{ display: "block", fontWeight: 500, marginBottom: "0.75rem" }}
    >
      {label}
    </label>
    <div style={{ display: "flex", gap: "0.5rem", marginBottom: "0.5rem" }}>
      {[1, 2, 3, 4, 5].map((num) => (
        <button
          key={num}
          type="button"
          onClick={() => onChange(num)}
          style={{
            flex: 1,
            padding: "0.6rem",
            borderRadius: 8,
            border: value === num ? "2px solid #2563eb" : "1px solid #d4d4d8",
            background: value === num ? "#eff6ff" : "white",
            color: value === num ? "#2563eb" : "#374151",
            fontWeight: value === num ? 600 : 400,
            cursor: "pointer",
          }}
        >
          {num}
        </button>
      ))}
    </div>
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        fontSize: "0.8rem",
        color: "#6b7280",
      }}
    >
      <span>{min}</span>
      <span>{max}</span>
    </div>
  </div>
);

export default QuestionnairePage;