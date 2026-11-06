import { useRef, useState } from "react";
import {
  eventService,
  type EventRequestDTO,
} from "../services/eventService";

type SubmitMode = "create" | "update";

export const useEventSubmit = () => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const submittingRef = useRef(false);

  const submitEvent = async (
    payload: EventRequestDTO,
    mode: SubmitMode,
    eventId?: number
  ): Promise<boolean> => {
    if (submittingRef.current) return false;

    submittingRef.current = true;
    setIsSubmitting(true);
    setError(null);

    try {
      if (mode === "create") {
        await eventService.createEvent(payload);
      } else {
        if (eventId === undefined) {
          throw new Error("Event ID is required");
        }

        await eventService.updateEvent(eventId, payload);
      }

      return true;
    } catch (err) {
      console.error(err);
      setError("Failed to save event.");
      return false;
    } finally {
      submittingRef.current = false;
      setIsSubmitting(false);
    }
  };

  return {
    submitEvent,
    isSubmitting,
    error,
  };
};
