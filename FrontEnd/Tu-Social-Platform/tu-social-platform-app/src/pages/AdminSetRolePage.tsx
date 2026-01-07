import React, { useState } from "react";
import Header from "../components/Header";
import { AdminService, type Role } from "../services/AdminService";
import { useAuth } from "../services/AuthContext";

const AdminSetRolePage: React.FC = () => {
  const { user } = useAuth();
  const isAdmin = (user as any)?.role === "Admin";

  const [email, setEmail] = useState("");
  const [role, setRole] = useState<Role>("User");

  if (!isAdmin) {
    return (
      <>
        <Header />
        <main
          style={{
            maxWidth: 1100,
            margin: "0 auto",
            padding: "2rem 1.5rem",
            paddingTop: "8%",
          }}
        >
          <h1>Set User Role</h1>
          <p style={{ color: "crimson" }}>Access denied. Admins only.</p>
        </main>
      </>
    );
  }

  return (
    <>
      <Header />
      <main style={{ maxWidth: 600, margin: "0 auto", padding: 20 }}>
        <h1>Set user role</h1>

        <input
          placeholder="User email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <select value={role} onChange={(e) => setRole(e.target.value as Role)}>
          <option value="User">User</option>
          <option value="Admin">Admin</option>
        </select>

        <button
          onClick={async () => {
            await AdminService.setRoleByEmail(email, role);
            alert("Role updated");
          }}
        >
          Update role
        </button>
      </main>
    </>
  );
};

export default AdminSetRolePage;
