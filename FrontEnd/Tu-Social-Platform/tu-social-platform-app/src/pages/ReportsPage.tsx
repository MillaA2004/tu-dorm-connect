import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { reportService, type ReportResponse } from "../services/ReportService";
import Header from "../components/Header";
import { useAuth } from "../services/AuthContext";


type Filter = "all" | "viewed" | "unviewed";

function formatDate(iso: string) {
  const d = new Date(iso);
  return isNaN(d.getTime()) ? iso : d.toLocaleString();
}

function targetLink(r: ReportResponse) {
  switch (r.targetType) {
    case "POST":
      return `/posts/${r.targetId}`;
    case "EVENT":
      return `/events/${r.targetId}`;
    case "USER":
      return `/profile/${r.targetId}`;
    default:
      return "/";
  }
}

const ReportsPage: React.FC = () => {
  const navigate = useNavigate();

  const [reports, setReports] = useState<ReportResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);

  const [filter, setFilter] = useState<Filter>("all");
  const [query, setQuery] = useState("");

  const { user } = useAuth();
  const isAdmin =
  (user as any)?.role === "Admin" || (user as any)?.isAdmin === true;

  const fetchReports = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await reportService.getAllReports();
      
      data.sort((a, b) => (b.createdAt > a.createdAt ? 1 : -1));
      setReports(data);
    } catch (e) {
      console.error(e);
      setError("Failed to load reports.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void fetchReports();
  }, []);

  const filtered = useMemo(() => {
    let list = [...reports];

    if (filter === "viewed") list = list.filter((r) => r.isViewed);
    if (filter === "unviewed") list = list.filter((r) => !r.isViewed);

    const q = query.trim().toLowerCase();
    if (q) {
      list = list.filter((r) => {
        return (
          String(r.reportId).includes(q) ||
          String(r.targetId).includes(q) ||
          r.targetType.toLowerCase().includes(q) ||
          (r.reason ?? "").toLowerCase().includes(q) ||
          String(r.reporterId).includes(q)
        );
      });
    }

    return list;
  }, [reports, filter, query]);

  
  const markViewed = async (reportId: number) => {
    setBusyId(reportId);
    setError(null);
    try {
      await reportService.markAsViewed(reportId);
      
      setReports((prev) =>
        prev.map((r) => (r.reportId === reportId ? { ...r, isViewed: true } : r))
      );
    } catch (e) {
      console.error(e);
      setError("Failed to mark report as viewed.");
    } finally {
      setBusyId(null);
    }
  };

  const openTarget = (r: ReportResponse) => {
    navigate(targetLink(r));
  };


  if (!isAdmin) {
  return (
    <>
      <Header />
      <main style={{ maxWidth: 1100, margin: "0 auto", padding: "2rem 1.5rem", paddingTop: "8%" }}>
        <h1>Reports</h1>
        <p style={{ color: "crimson" }}>Access denied. Admins only.</p>
      </main>
    </>
  );
}
 
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
        <div
          style={{
            display: "flex",
            alignItems: "flex-end",
            justifyContent: "space-between",
            gap: "1rem",
            flexWrap: "wrap",
            marginBottom: "1rem",
          }}
        >
          <div>
            <h1 style={{ margin: 0 }}>Reports</h1>
            
          </div>

          <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
            <select
              value={filter}
              onChange={(e) => setFilter(e.target.value as Filter)}
              style={{
                padding: "0.55rem 0.8rem",
                borderRadius: 10,
                border: "1px solid #d1d5db",
                background: "white",
              }}
            >
              <option value="all">All</option>
              <option value="unviewed">Unviewed</option>
              <option value="viewed">Viewed</option>
            </select>

            <input
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Search (id, target, reason, reporter)…"
              style={{
                padding: "0.55rem 0.8rem",
                borderRadius: 10,
                border: "1px solid #d1d5db",
                minWidth: 260,
              }}
            />

            <button
              className="hero-action-button"
              type="button"
              onClick={() => void fetchReports()}
              disabled={loading}
            >
              Refresh
            </button>
          </div>
        </div>

        {error && (
          <div style={{ color: "crimson", marginBottom: "0.9rem" }}>{error}</div>
        )}

        {loading ? (
          <div>Loading reports…</div>
        ) : filtered.length === 0 ? (
          <div style={{ color: "#6b7280" }}>No reports found.</div>
        ) : (
          <div style={{ display: "grid", gap: "0.9rem" }}>
            {filtered.map((r) => {
              const isBusy = busyId === r.reportId;
              const isViewed = r.isViewed;

              return (
                <article
                  key={r.reportId}
                  role="button"
                  tabIndex={0}
                  onClick={() => openTarget(r)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ") {
                      e.preventDefault();
                      openTarget(r);
                    }
                  }}
                  style={{
                    background: "white",
                    borderRadius: 16,
                    padding: "1rem 1rem",
                    boxShadow: "0 10px 26px rgba(15,23,42,0.06)",
                    border: isViewed ? "1px solid #e5e7eb" : "1px solid #fecaca",
                    cursor: "pointer",
                  }}
                >
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      gap: "1rem",
                      flexWrap: "wrap",
                      alignItems: "center",
                    }}
                  >
                    <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                      <span
                        style={{
                          fontSize: "0.8rem",
                          fontWeight: 700,
                          padding: "0.2rem 0.6rem",
                          borderRadius: 999,
                          background: "#eff6ff",
                          color: "#1d4ed8",
                        }}
                      >
                        {r.targetType}
                      </span>

                      <span
                        style={{
                          fontSize: "0.8rem",
                          fontWeight: 700,
                          padding: "0.2rem 0.6rem",
                          borderRadius: 999,
                          background: isViewed ? "#ecfdf5" : "#fef2f2",
                          color: isViewed ? "#065f46" : "#991b1b",
                        }}
                      >
                        {isViewed ? "Viewed" : "Unviewed"}
                      </span>

                      <button
  type="button"
  className="linkBtn"
  onClick={(e) => {
    e.stopPropagation();
    navigate(`/profile/${r.reporterId}`);
  }}
  style={{ padding: 0 }}
>
  View Reporter
</button>

                    </div>

                    <div style={{ display: "flex", gap: 10, alignItems: "center" }}>
                      <span style={{ color: "#6b7280", fontSize: "0.9rem" }}>
                        {formatDate(r.createdAt)}
                      </span>

                      {!isViewed && (
                        <button
                          type="button"
                          className="hero-action-button"
                          disabled={isBusy}
                          onClick={(e) => {
                            e.stopPropagation();
                            void markViewed(r.reportId);
                          }}
                        >
                          {isBusy ? "Marking..." : "Mark as viewed"}
                        </button>
                      )}
                    </div>
                  </div>

                  <div
                    style={{
                      marginTop: 10,
                      paddingTop: 10,
                      borderTop: "1px solid #eef2f7",
                      color: "#111827",
                      lineHeight: 1.5,
                      whiteSpace: "pre-wrap",
                    }}
                  >
                    <div style={{ fontWeight: 700, marginBottom: 6 }}>Reason</div>
                    <div style={{ color: "#374151" }}>{r.reason}</div>

                    <div style={{ marginTop: 10, color: "#2563eb", fontWeight: 700 }}>
                      Open {r.targetType.toLowerCase()} →
                    </div>
                  </div>
                </article>
              );
            })}
          </div>
        )}
      </main>
    </>
  );
};

export default ReportsPage;
