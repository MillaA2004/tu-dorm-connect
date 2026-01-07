import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../services/apiClient";
import { useAuth } from "../services/AuthContext";
import Header from "../components/Header";

type User = {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  suspendedUntil: string | null; // може да е null
};

const AdminSuspendedUsersPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [users, setUsers] = useState<User[]>([]);
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const isAdmin = (user as any)?.role === "Admin";

  useEffect(() => {
    if (!isAdmin) return;

    const fetchUsers = async () => {
      try {
        setLoading(true);
        const res = await apiClient.get<User[]>("/api/users");
        // Взимаме само потребителите, които са suspended
        setUsers(res.data.filter((u) => u.suspendedUntil));
      } catch (err) {
        console.error(err);
        setError("Failed to load suspended users.");
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, [isAdmin]);

  const filtered = useMemo(() => {
    const q = query.toLowerCase();
    return users.filter(
      (u) =>
        u.firstName.toLowerCase().includes(q) ||
        u.lastName.toLowerCase().includes(q) ||
        u.email.toLowerCase().includes(q)
    );
  }, [users, query]);

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
          <h1>Suspended users</h1>
          <p style={{ color: "crimson" }}>Access denied. Admins only.</p>
        </main>
      </>
    );
  }

  return (
    <>
      <Header />
      <main style={{ maxWidth: 800, margin: "0 auto", padding: 20 }}>
        <h1>Suspended Users</h1>

        {error && <p style={{ color: "crimson" }}>{error}</p>}

        <input
          placeholder="Search by name or email"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          style={{
            width: "100%",
            padding: "8px 12px",
            marginBottom: 20,
            fontSize: 16,
          }}
        />

        {loading ? (
          <p>Loading...</p>
        ) : filtered.length === 0 ? (
          <p>No suspended users found.</p>
        ) : (
          <ul style={{ listStyle: "none", padding: 0 }}>
            {filtered.map((u) => (
              <li
                key={u.userId}
                style={{
                  marginBottom: 12,
                  padding: 12,
                  border: "1px solid #ddd",
                  borderRadius: 6,
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <div>
                  <strong>
                    {u.firstName} {u.lastName}
                  </strong>{" "}
                  ({u.email}) <br />
                  <small>
                    Suspended until:{" "}
                    {u.suspendedUntil
                      ? new Date(u.suspendedUntil).toLocaleString()
                      : "N/A"}
                  </small>
                </div>
                <button
                  onClick={() => navigate(`/profile/${u.userId}`)}
                  style={{
                    padding: "6px 12px",
                    borderRadius: 4,
                    border: "none",
                    backgroundColor: "#007bff",
                    color: "#fff",
                    cursor: "pointer",
                  }}
                >
                  View Profile
                </button>
              </li>
            ))}
          </ul>
        )}
      </main>
    </>
  );
};

export default AdminSuspendedUsersPage;
