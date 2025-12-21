import React, { useMemo, useState } from "react";
import { Map, Marker, InfoWindow } from "@vis.gl/react-google-maps";
import { useNavigate } from "react-router-dom";
import DormCard from "../components/DormCard";
import "../styles/DormMap.css";
import type { DormMapItem } from "../services/DormService";
 

type Props = {
  dorms: DormMapItem[];
  defaultCenter?: { lat: number; lng: number };
  defaultZoom?: number;
};

const DormMap: React.FC<Props> = ({
  dorms,
  defaultCenter = { lat: 42.6977, lng: 23.3219 },
  defaultZoom = 13,
}) => {
  const navigate = useNavigate();
  const [activeDormId, setActiveDormId] = useState<number | null>(null);

  const activeDorm = useMemo(
    () => dorms.find((d) => d.id === activeDormId) ?? null,
    [dorms, activeDormId]
  );

  return (
    <div className="dormMap">
      <Map
        style={{ width: "100%", height: "100%" }}
        defaultCenter={defaultCenter}
        defaultZoom={defaultZoom}
        gestureHandling="greedy"
      >
        {dorms.map((d) => (
          <Marker
            key={d.id}
            position={{ lat: d.lat, lng: d.lng }}
            onMouseOver={() => setActiveDormId(d.id)}
            onMouseOut={() =>
              setActiveDormId((prev) => (prev === d.id ? null : prev))
            }
            onClick={() => setActiveDormId(d.id)}
          />
        ))}

        {activeDorm && (
          <InfoWindow
            position={{ lat: activeDorm.lat, lng: activeDorm.lng }}
            onCloseClick={() => setActiveDormId(null)}
            pixelOffset={[0, -8]}
          >
            <div
              onMouseEnter={() => setActiveDormId(activeDorm.id)}
              onMouseLeave={() => setActiveDormId(null)}
            >
              <DormCard
                dorm={{
                  id: activeDorm.id,
                  name: activeDorm.name,
                  address: activeDorm.address,
                  price: activeDorm.price,
                  imageUrl: activeDorm.imageUrl,
                }}
                onClick={() => navigate(`/dorms/${activeDorm.id}`)}
              />
            </div>
          </InfoWindow>
        )}
      </Map>
    </div>
  );
};

export default DormMap;


