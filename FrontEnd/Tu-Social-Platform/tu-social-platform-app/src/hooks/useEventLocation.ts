import { useState } from "react";
import type { MapMouseEvent } from "@vis.gl/react-google-maps";
import type { LatLng } from "../types";

export const useEventLocation = () => {
  const [location, setLocation] = useState<LatLng | null>(
    null
  );

  const handleMapClick = (event: MapMouseEvent) => {
    const latLng = event.detail.latLng;

    if (!latLng) return;

    setLocation({
      lat: latLng.lat,
      lng: latLng.lng,
    });
  };

  return {
    location,
    setLocation,
    handleMapClick,
  };
};
