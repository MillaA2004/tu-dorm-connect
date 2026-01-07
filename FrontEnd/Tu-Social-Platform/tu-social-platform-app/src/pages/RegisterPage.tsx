import React, { useState, useEffect } from "react";
import "../styles/register.css";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../services/AuthContext";
import {
 type AcademicYear,
 type Gender,
  type RegisterFormValues,
} from "../services/authTypes";

interface RegisterFormState {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  gender: Gender | "";
  major: string;
  academicYear: AcademicYear | "";
  imageFile: File | null;
}


const RegisterForm: React.FC = () => {
  const [formData, setFormData] = useState<RegisterFormState>({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    gender: "",
    major: "",
    academicYear: "",
    imageFile: null,
  });

  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [errors, setErrors] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const navigate = useNavigate();
  const { register } = useAuth();

  useEffect(() => {
    return () => {
      if (imagePreview) {
        URL.revokeObjectURL(imagePreview);
      }
    };
  }, [imagePreview]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith("image/")) {
      setErrors("Please upload a valid image file.");
      return;
    }

    setErrors(null);
    setFormData((prev) => ({
      ...prev,
      imageFile: file,
    }));

    const previewUrl = URL.createObjectURL(file);
    setImagePreview(previewUrl);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (
      !formData.firstName ||
      !formData.lastName ||
      !formData.email ||
      !formData.password ||
      !formData.gender ||
      !formData.major ||
      !formData.academicYear
    ) {
      setErrors("Please fill in all required fields.");
      return;
    }

    setErrors(null);
    setSubmitting(true);

    const payload: RegisterFormValues = {
      firstName: formData.firstName,
      lastName: formData.lastName,
      email: formData.email,
      password: formData.password,
      gender: formData.gender as Gender,
      major: formData.major,
      academicYear: formData.academicYear as AcademicYear,
      imageFile: formData.imageFile,
    };

    try {
      await register(payload);
      
      navigate("/home");
    } catch (err: any) {
      console.error(err);
      const message =
        err?.response?.data?.message ||
        err?.message ||
        "Registration failed. Please try again.";
      setErrors(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="register-page">
      <form className="register-form" onSubmit={handleSubmit}>
        <h2>Create an Account</h2>

        {errors && <div className="error-message">{errors}</div>}

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="firstName">First Name*</label>
            <input
              id="firstName"
              name="firstName"
              type="text"
              value={formData.firstName}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="lastName">Last Name*</label>
            <input
              id="lastName"
              name="lastName"
              type="text"
              value={formData.lastName}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="email">Email*</label>
          <input
            id="email"
            name="email"
            type="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">Password*</label>
          <input
            id="password"
            name="password"
            type="password"
            value={formData.password}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="gender">Gender*</label>
            <select
              id="gender"
              name="gender"
              value={formData.gender}
              onChange={handleChange}
              required
            >
              <option value="">Select gender</option>
              <option value="male">Male</option>
              <option value="female">Female</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="academicYear">Academic Year*</label>
            <select
              id="academicYear"
              name="academicYear"
              value={formData.academicYear}
              onChange={handleChange}
              required
            >
              <option value="">Select year</option>
              <option value="freshman">1st Year</option>
              <option value="sophomore">2nd Year</option>
              <option value="junior">3rd Year</option>
              <option value="senior">4th Year</option>
              <option value = "master">Master</option>
            </select>
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="major">Major*</label>
          <input
            id="major"
            name="major"
            type="text"
            value={formData.major}
            onChange={handleChange}
            placeholder="e.g. Computer Science"
            required
          />
        </div>

        <div className="form-group image-group">
          <label htmlFor="image">Profile Image</label>
          <input
            id="image"
            name="image"
            type="file"
            accept="image/*"
            onChange={handleImageChange}
          />

          {imagePreview && (
            <div className="image-preview">
              <p>Preview:</p>
              <img src={imagePreview} alt="Profile preview" />
            </div>
          )}
        </div>

        <button type="submit" className="submit-btn" disabled={submitting}>
          {submitting ? "Registering..." : "Register"}
        </button>

        <div style={{ marginTop: "12px", textAlign: "center" }}>
          <span style={{ fontSize: "14px", color: "#6b7280" }}>
            Already have an account?
          </span>
          <button
            type="button"
            onClick={() => navigate("/login")}
            style={{
              background: "none",
              border: "none",
              color: "#2563eb",
              marginLeft: "6px",
              cursor: "pointer",
              fontSize: "14px",
              textDecoration: "underline",
            }}
          >
            Go to login
          </button>
        </div>
      </form>
    </div>
  );
};

export default RegisterForm;

