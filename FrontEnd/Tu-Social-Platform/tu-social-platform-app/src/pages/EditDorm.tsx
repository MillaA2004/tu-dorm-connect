import React, { useEffect, useMemo, useState } from "react";
import { Map, Marker } from "@vis.gl/react-google-maps";
import { useNavigate, useParams } from "react-router-dom";
import { dormService } from "../services/DormService";
import type { DormResponseDTO, DormUpdateRequestDTO } from "../services/DormService";
import "../styles/AddDorm.css"; 
import { useAuth } from "../services/AuthContext";
import Header from "../components/Header";

type LatLng = { lat: number; lng: number };

const EditDorm: React.FC = () => {
  const navigate = useNavigate();
  const params = useParams();
  const dormId = Number(params.id);

  const auth = useAuth();
  const isAdmin = auth.user?.role === "Admin";

  const [loadingDorm, setLoadingDorm] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [dorm, setDorm] = useState<DormResponseDTO | null>(null);

 
  const [name, setName] = useState("");
  const [address, setAddress] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState<number>(0);
  const [picked, setPicked] = useState<LatLng | null>(null);

  const [files, setFiles] = useState<File[]>([]);
  const [replaceImages, setReplaceImages] = useState<boolean>(false);

  
  const defaultCenter = useMemo<LatLng>(() => ({ lat: 42.6977, lng: 23.3219 }), []);

  useEffect(() => {
    if (!Number.isFinite(dormId) || dormId <= 0) {
      setError("Invalid dorm id.");
      setLoadingDorm(false);
      return;
    }

    (async () => {
      try {
        setLoadingDorm(true);
        setError(null);

        const data = await dormService.getDormById(dormId);
        setDorm(data);

        
        setName(data.name ?? "");
        setAddress(data.address ?? "");
        setDescription(data.description ?? "");
        setPrice(typeof data.price === "number" ? data.price : 0);

        if (typeof data.latitude === "number" && typeof data.longitude === "number") {
          setPicked({ lat: data.latitude, lng: data.longitude });
        } else {
          setPicked(null);
        }
      } catch (e: any) {
        setError(e?.response?.data?.message ?? e?.message ?? "Failed to load dorm.");
      } finally {
        setLoadingDorm(false);
      }
    })();
  }, [dormId]);

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

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    const msg = validate();
    if (msg) {
      setError(msg);
      return;
    }
    if (!dorm) return;

    setSaving(true);
    setError(null);

    try {
      const dto: DormUpdateRequestDTO = {
        name: name.trim(),
        address: address.trim(),
        description: description.trim(),
        price: Number(price),
        latitude: picked!.lat,
        longitude: picked!.lng,
        replaceImages,
      };

      const updated = await dormService.updateDorm(dorm.id, dto, files.length ? files : undefined);

      
      navigate(`/dorms/${updated.id}`);
    } catch (e: any) {
      setError(e?.response?.data?.message ?? e?.message ?? "Failed to update dorm.");
    } finally {
      setSaving(false);
    }
  };

  
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

  if (loadingDorm) {
    return <div style={{ padding: 16 }}>Loading dorm…</div>;
  }

  if (error && !dorm) {
    return (
      <div style={{ padding: 16 }}>
        <div style={{ color: "crimson" }}>{error}</div>
        <button className="btn btnGhost" type="button" onClick={() => navigate(-1)}>
          Back
        </button>
      </div>
    );
  }

  return (
    <>
    <Header/>
    
    <div className="addDormPage">
      <div className="addDormLayout">
        <div className="addDormCard">
          <h2>Edit dorm</h2>
          <p className="muted">Update details and location, then save.</p>

          {error && <div className="errorBox">{error}</div>}

          <form onSubmit={handleSave} className="form">
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
              <span>Upload new images (optional)</span>
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

            <label className="field" style={{ display: "flex", gap: 10, alignItems: "center" }}>
              <input
                type="checkbox"
                checked={replaceImages}
                onChange={(e) => setReplaceImages(e.target.checked)}
              />
              <span style={{ margin: 0, color: "#111827", fontSize: 13 }}>
                Replace existing images with the uploaded ones
              </span>
            </label>

           {!!dorm?.imageUrlsList?.length && (
  <div className="muted small">
    Current images: {dorm.imageUrlsList.length}
  </div>
)}


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
              <button className="btn btnPrimary" type="submit" disabled={saving}>
                
                {saving ? "Saving..." : "Save changes"}
              </button>
            </div>
          </form>
        </div>

        <div className="mapCard">
          <div className="mapHeader">
            <div>
              <div className="mapTitle">Edit location</div>
              <div className="muted small">
                Click the map to move the marker. Drag to fine-tune.
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
              defaultCenter={picked ?? defaultCenter}
              center={picked ?? defaultCenter}
              onClick={onMapClick}
              gestureHandling="greedy"
            >
              {picked && <Marker position={picked} draggable onDragEnd={onMarkerDragEnd} />}
            </Map>
          </div>
        </div>
      </div>
    </div>
    </>
  );
};

export default EditDorm;
