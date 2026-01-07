import React, { useMemo, useState } from "react";
import { Map, Marker } from "@vis.gl/react-google-maps";
import { useNavigate } from "react-router-dom";
import { dormService } from "../services/DormService";
import type { DormRequestDTO } from "../services/DormService";
import "../styles/AddDorm.css";
import { useAuth } from "../services/AuthContext";
import Header from "../components/Header";

type LatLng = { lat: number; lng: number };


const AddDorm: React.FC = () => {
  const navigate = useNavigate();
  
  const user = useAuth();
  const isAdmin = user.user?.role === "Admin";

 const defaultCenter = useMemo<LatLng>(() => ({ lat: 42.6977, lng: 23.3219 }), []);

  const [name, setName] = useState("");
  const [address, setAddress] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState<number>(200);

  const [picked, setPicked] = useState<LatLng | null>(null);
  const [files, setFiles] = useState<File[]>([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isAdmin) {
  return (
    <>
      <Header />
      <main style={{ maxWidth: 1100, margin: "0 auto", padding: "2rem 1.5rem", paddingTop: "8%" }}>
        <h2>Access denied</h2>
        <p style={{ color: "crimson" }}>Access denied. Admins only.</p>
        <button className="btn" onClick={() => navigate("/home")} type="button">
            Go home
          </button>
      </main>
    </>
  );
}
 
  

  const onMapClick = (e: any) => {
    
    const latLng = e?.detail?.latLng;
    if (!latLng) return;

    const lat = typeof latLng.lat === "function" ? latLng.lat() : latLng.lat;
    const lng = typeof latLng.lng === "function" ? latLng.lng() : latLng.lng;

    setPicked({ lat, lng });
  };

  const onMarkerDragEnd = (e: any) => {
    const latLng = e?.latLng;
    if (!latLng) return;

    const lat = typeof latLng.lat === "function" ? latLng.lat() : latLng.lat;
    const lng = typeof latLng.lng === "function" ? latLng.lng() : latLng.lng;

    setPicked({ lat, lng });
  };

  const validate = () => {
    if (!name.trim()) return "Name is required.";
    if (!address.trim()) return "Address is required.";
    if (!description.trim()) return "Description is required.";
    if (!Number.isFinite(price) || price <= 0) return "Price must be a positive number.";
    if (!picked) return "Please pick dorm coordinates on the map.";
    return null;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const msg = validate();
    if (msg) {
      setError(msg);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const dto: DormRequestDTO = {
        name: name.trim(),
        address: address.trim(),
        description: description.trim(),
        price: Number(price),
        imageUrlsList: [], 
        latitude: picked!.lat,
        longitude: picked!.lng,
      };

      const created = await dormService.createDorm(dto, files.length ? files : undefined);

      
      navigate(`/dorms/${created.id}`);
    } catch (err: any) {
      setError(err?.response?.data?.message ?? err?.message ?? "Failed to create dorm.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
    <Header/>
   
    <div className="addDormPage">
      <div className="addDormLayout">
        <div className="addDormCard">
          <h2>Add dorm</h2>
          <p className="muted">Fill the info and pick the location on the map.</p>

          {error && <div className="errorBox">{error}</div>}

          <form onSubmit={handleSubmit} className="form">
            <label className="field">
              <span>Name</span>
              <input value={name} onChange={(e) => setName(e.target.value)} />
            </label>

            <label className="field">
              <span>Address</span>
              <input value={address} onChange={(e) => setAddress(e.target.value)} />
            </label>

            <label className="field">
              <span>Description</span>
              <textarea
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                rows={4}
              />
            </label>

            <label className="field">
              <span>Price (€)</span>
              <input
                type="number"
                value={price}
                onChange={(e) => setPrice(Number(e.target.value))}
                min={1}
                step={1}
              />
            </label>

            <label className="field">
              <span>Images (optional)</span>
              <input
                type="file"
                multiple
                accept="image/*"
                onChange={(e) => setFiles(Array.from(e.target.files ?? []))}
              />
              {files.length > 0 && (
                <div className="muted small">{files.length} file(s) selected</div>
              )}
            </label>

            <div className="coordsRow">
              <div className="coordBox">
                <div className="muted small">Latitude</div>
                <div className="coordValue">{picked ? picked.lat.toFixed(6) : "—"}</div>
              </div>
              <div className="coordBox">
                <div className="muted small">Longitude</div>
                <div className="coordValue">{picked ? picked.lng.toFixed(6) : "—"}</div>
              </div>
            </div>

            <div className="actions">
              <button className="btn btnGhost" type="button" onClick={() => navigate(-1)}>
                Cancel
              </button>
              <button className="btn btnPrimary" type="submit" disabled={loading}>
                {loading ? "Creating..." : "Create dorm"}
              </button>
            </div>
          </form>
        </div>

        <div className="mapCard">
          <div className="mapHeader">
            <div>
              <div className="mapTitle">Pick location</div>
              <div className="muted small">
                Click on the map to place the marker. Drag to adjust.
              </div>
            </div>
            {picked && (
              <button className="btn btnGhost" type="button" onClick={() => setPicked(null)}>
                Clear
              </button>
            )}
          </div>

          <div className="mapWrap">
            <Map
              style={{ width: "100%", height: "100%" }}
              defaultZoom={13}
              defaultCenter={defaultCenter}
              onClick={onMapClick}
              gestureHandling="greedy"
            >
              {picked && (
                <Marker
                  position={picked}
                  draggable
                  onDragEnd={onMarkerDragEnd}
                />
              )}
            </Map>
          </div>
        </div>
      </div>
    </div>
     </>
  );
};

export default AddDorm;
