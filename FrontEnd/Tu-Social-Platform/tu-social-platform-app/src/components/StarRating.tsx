// type StarRatingProps = {
//   value: number;
//   onChange?: (v: number) => void;
//   size?: "sm" | "md";
//   readOnly?: boolean;
// };

// const StarRating: React.FC<StarRatingProps> = ({
//   value,
//   onChange,
//   size = "md",
//   readOnly = false,
// }) => {
//   const stars = [1, 2, 3, 4, 5];

//   return (
//     <div className={`stars stars--${size}`} role="radiogroup" aria-label="Rating">
//       {stars.map((s) => {
//         const active = s <= value;
//         return (
//           <button
//             key={s}
//             type="button"
//             className={`star ${active ? "is-active" : ""}`}
//             onClick={() => !readOnly && onChange?.(s)}
//             onMouseDown={(e) => e.preventDefault()}
//             disabled={readOnly}
//             aria-label={`${s} star`}
//             aria-checked={active}
//             role="radio"
//           >
//             ★
//           </button>
//         );
//       })}
//     </div>
//   );
// };
