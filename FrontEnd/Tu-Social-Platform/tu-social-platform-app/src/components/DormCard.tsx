import React from "react";
import type { DormUI } from "../services/DormService";
import "../styles/DormCard.css";

type Props = {
  dorm: DormUI;
  onClick?: () => void;
};

const DormCard: React.FC<Props> = ({ dorm, onClick }) => {
  return (
    <button className="dormCard" onClick={onClick} type="button">
      <div className="dormCard__media">
        {dorm.imageUrl ? (
          <img
            className="dormCard__img"
            src={dorm.imageUrl}
            alt={dorm.name}
            loading="lazy"
          />
        ) : (
          <div className="dormCard__img dormCard__img--placeholder" />
        )}
      </div>

      <div className="dormCard__body">
        <div className="dormCard__titleRow">
          <h4 className="dormCard__title">{dorm.name}</h4>
          
        </div>

        <div className="dormCard__meta">{dorm.address}</div>

        <div className="dormCard__cta">View dorm →</div>
      </div>
    </button>
  );
};

export default DormCard;
