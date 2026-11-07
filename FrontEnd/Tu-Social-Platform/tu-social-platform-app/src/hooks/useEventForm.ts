import { useState } from "react";
import type { LatLng } from "../types";
import type { EventRequestDTO } from "../services/eventService";

export interface EventFormData {
  title: string;
  description: string;
  address: string;
  eventType: string;
  dateTime: string;
  capacity: number | "";
}

const initialForm: EventFormData = {
  title: "",
  description: "",
  address: "",
  eventType: "party",
  dateTime: "",
  capacity: "",
};

export const useEventForm = () => {
  const [form, setForm] = useState<EventFormData>({
    ...initialForm,
  });

  const updateField = <K extends keyof EventFormData>(
    field: K,
    value: EventFormData[K]
  ) => {
    setForm(prev => ({
      ...prev,
      [field]: value,
    }));
  };

  const resetForm = () => {
    setForm({ ...initialForm });
  };

  const validate = (
    location: LatLng | null
  ): string | null => {
    if (!form.title.trim()) return "Add a title";
    if (!form.description.trim()) return "Add a description";
    if (!form.address.trim()) return "Add an address";
    if (!form.dateTime) return "Pick a date & time";

    if (
      form.capacity === "" ||
      !Number.isFinite(form.capacity) ||
      form.capacity <= 0
    ) {
      return "Set a positive capacity";
    }

    if (!location) return "Pick a location on the map";

    return null;
  };

  const buildPayload = (
    location: LatLng
  ): EventRequestDTO => ({
    title: form.title.trim(),
    description: form.description.trim(),
    address: form.address.trim(),
    eventType: form.eventType,
    dateTime:
      form.dateTime.length === 16
        ? `${form.dateTime}:00`
        : form.dateTime,
    capacity: Number(form.capacity),
    latitude: location.lat,
    longitude: location.lng,
  });

  return {
    form,
    setForm,
    updateField,
    resetForm,
    validate,
    buildPayload,
  };
};
