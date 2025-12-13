// import React, { useState } from "react";
// import { useNavigate } from "react-router-dom";
// import "../styles/register.css";
// import { loginUser } from "../services/AuthService"; 

// const LoginForm: React.FC = () => {
//   const navigate = useNavigate();

//   const [email, setEmail] = useState("");
//   const [password, setPassword] = useState("");
//   const [error, setError] = useState<string | null>(null);
//   const [loading, setLoading] = useState(false);

//   const handleSubmit = async (e: React.FormEvent) => {
//     e.preventDefault();

//     if (!email || !password) {
//       setError("Please enter email and password.");
//       return;
//     }

//     setError(null);
//     setLoading(true);

//     try {
//       const jwt = await loginUser({ email, password });
//       console.log("JWT token:", jwt.token);

     
//       navigate("/home");
//     } catch (err: any) {
//       console.error(err);
//       setError(err.message || "Login failed");
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="register-page">
//       <form className="register-form" onSubmit={handleSubmit}>
//         <h2>Login</h2>

//         {error && <div className="error-message">{error}</div>}

//         <div className="form-group">
//           <label>Email*</label>
//           <input
//             type="email"
//             value={email}
//             onChange={(e) => setEmail(e.target.value)}
//             required
//           />
//         </div>

//         <div className="form-group">
//           <label>Password*</label>
//           <input
//             type="password"
//             value={password}
//             onChange={(e) => setPassword(e.target.value)}
//             required
//           />
//         </div>

//         <button type="submit" className="submit-btn" disabled={loading}>
//           {loading ? "Logging in..." : "Login"}
//         </button>

//         <div style={{ marginTop: "12px", textAlign: "center" }}>
//           <span style={{ fontSize: "14px", color: "#6b7280" }}>
//             Don’t have an account?
//           </span>
//           <button
//             type="button"
//             onClick={() => navigate("/register")}
//             style={{
//               background: "none",
//               border: "none",
//               color: "#2563eb",
//               marginLeft: "6px",
//               cursor: "pointer",
//               fontSize: "14px",
//               textDecoration: "underline",
//             }}
//           >
//             Register
//           </button>
//         </div>
//       </form>
//     </div>
//   );
// };

// export default LoginForm;



import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/register.css";
import { useAuth } from "../services/AuthContext"; // or "../context/AuthContext" depending on your path

const LoginForm: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth(); // 👈 use AuthContext instead of calling AuthService directly

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!email || !password) {
      setError("Please enter email and password.");
      return;
    }

    setError(null);
    setLoading(true);

    try {
      // 👇 this will:
      // - call AuthService.loginUser
      // - store token
      // - decode user
      // - update AuthContext state
      await login(email, password);

      navigate("/home");
    } catch (err: any) {
      console.error(err);
      const message =
        err?.response?.data?.message ||
        err?.message ||
        "Login failed";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-page">
      <form className="register-form" onSubmit={handleSubmit}>
        <h2>Login</h2>

        {error && <div className="error-message">{error}</div>}

        <div className="form-group">
          <label>Email*</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label>Password*</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <button type="submit" className="submit-btn" disabled={loading}>
          {loading ? "Logging in..." : "Login"}
        </button>

        <div style={{ marginTop: "12px", textAlign: "center" }}>
          <span style={{ fontSize: "14px", color: "#6b7280" }}>
            Don’t have an account?
          </span>
          <button
            type="button"
            onClick={() => navigate("/register")}
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
            Register
          </button>
        </div>
      </form>
    </div>
  );
};

export default LoginForm;
