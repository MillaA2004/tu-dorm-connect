import React, { useEffect, useMemo, useState } from "react";
import DormMap from "../components/DormMap";
import { dormService } from "../services/DormService";
import type { DormResponseDTO, DormMapItem } from "../services/DormService";
import Header from "../components/Header";

const DormListPage: React.FC = () => {
  const [dorms, setDorms] = useState<DormResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

   
  const [search, setSearch] = useState("");

  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await dormService.getAllDorms();
        setDorms(data);
      } catch (e: any) {
        setError(e?.message ?? "Failed to load dorms");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const q = search.trim().toLowerCase();

  
  const dormMapItems: DormMapItem[] = useMemo(() => {
    return dorms
      .filter(
        (d) =>
          typeof d.latitude === "number" &&
          typeof d.longitude === "number"
      )
      .filter((d) => {
        if (!q) return true;
        return d.name?.toLowerCase().includes(q);
      })
      .map((d) => ({
        id: d.id,
        name: d.name,
        address: d.address,
        price: d.price,
        imageUrl: d.imageUrlsList?.[0],
        lat: d.latitude!,
        lng: d.longitude!,
      }));
  }, [dorms, q]);

  return (

    <>
    <Header/>
    
    <div style={{ padding: 16 , marginTop: '5%',}}>
      <h2 style={{ margin: "0 0 12px", textAlign: "center" }}>
  Dorms
</h2>


      
<div
  style={{
    marginBottom: 12,
    display: "flex",
    justifyContent: "center",
  }}
>
  <input
    value={search}
    onChange={(e) => setSearch(e.target.value)}
    placeholder="Search dorm by name…"
    style={{
      width: "min(520px, 100%)",
      padding: "10px 12px",
      borderRadius: 8,
      border: "1px solid #ccc",
    }}
  />
</div>


      {loading && <div>Loading dorms…</div>}
      {error && <div style={{ color: "crimson" }}>{error}</div>}

      {!loading && !error && <DormMap dorms={dormMapItems} />}
    </div>
    </>
  );
};

export default DormListPage;



