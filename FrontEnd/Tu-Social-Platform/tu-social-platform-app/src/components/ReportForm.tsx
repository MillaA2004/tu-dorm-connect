import React, { useEffect, useState } from "react";
import { createPortal } from "react-dom";
import { reportService, type ReportTargetType } from "../services/ReportService";
import "../styles/ReportForm.css";

type Props = {
  isOpen: boolean;
  onClose: () => void;
  targetId: number;
  targetType: ReportTargetType;
  title?: string;
};

const ReportForm: React.FC<Props> = ({
  isOpen,
  onClose,
  targetId,
  targetType,
  title = "Report",
}) => {
  const [reason, setReason] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!isOpen) return;
    setReason("");
    setError(null);
    setBusy(false);
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;
    const esc = (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose();
    };
    window.addEventListener("keydown", esc);
    return () => window.removeEventListener("keydown", esc);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const submit = async () => {
    const r = reason.trim();
    if (!r) return;

    setBusy(true);
    setError(null);

    try {
      await reportService.submitReport({
        targetId,
        targetType,
        reason: r,
      });
      onClose();
    } catch (e: any) {
  const status = e?.response?.status;

  if (status === 409) {
    setError("You already reported this item.");
  } else {
    setError("Failed to submit report.");
  }


    } finally {
      setBusy(false);
    }
  };

  return createPortal(
    <div
      className="reportModalOverlay"
      onMouseDown={onClose}
      role="dialog"
      aria-modal="true"
    >
      <div
        className="reportModalCard"
        onMouseDown={(e) => e.stopPropagation()}
      >
        <div className="reportModalHeader">
          <div className="reportModalTitle">{title}</div>

          <button
            type="button"
            className="reportModalCloseBtn"
            onClick={onClose}
            disabled={busy}
            aria-label="Close"
          >
            ✕
          </button>
        </div>

        <div className="reportModalBody">
          

          <textarea
            className="reportModalTextarea"
            placeholder="Describe the reason for reporting…"
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            rows={5}
            disabled={busy}
            autoFocus
          />

          {error && <div className="errorText">{error}</div>}
        </div>

        <div className="reportModalFooter">
          <button
            type="button"
            className="hero-action-button"
            onClick={onClose}
            disabled={busy}
          >
            Cancel
          </button>

          <button
            type="button"
            className="hero-action-button danger"
            onClick={() => void submit()}
            disabled={busy || !reason.trim()}
          >
            {busy ? "Submitting..." : "Submit report"}
          </button>
        </div>
      </div>
    </div>,
    document.body
  );
};

export default ReportForm;
