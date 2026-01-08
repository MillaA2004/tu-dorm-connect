import React, { useEffect, useMemo, useState } from "react";
import {Link, useNavigate, useParams } from "react-router-dom";
import { dormService } from "../services/DormService";
import type { DormResponseDTO } from "../services/DormService";
import { reviewService } from "../services/ReviewService";
import type { ReviewResponseDTO } from "../services/ReviewService";
import { useAuth } from "../services/AuthContext";
import "../styles/DormDetailsPage.css";
import Header from "../components/Header";

const clamp = (n: number, min: number, max: number) => Math.max(min, Math.min(max, n));

const Stars: React.FC<{ value: number; outOf?: number; className?: string }> = ({
  value,
  outOf = 5,
  className,
}) => {
  const full = clamp(Math.round(value), 0, outOf);
  const empty = outOf - full;
  return (
    <span className={className ?? ""} aria-label={`${value} out of ${outOf} stars`}>
      {"★".repeat(full)}
      <span className="starsEmpty">{"★".repeat(empty)}</span>
    </span>
  );
};



const fullName = (a?: { firstName?: string | null; lastName?: string | null; username?: string | null }) => {
  const first = (a?.firstName ?? "").trim();
  const last = (a?.lastName ?? "").trim();
  const joined = [first, last].filter(Boolean).join(" ");
  return joined || a?.username?.trim() || "User";
};


const DormDetailsPage: React.FC = () => {
  const { id } = useParams();
  const dormId = Number(id);
  const navigate = useNavigate();

  const { user } = useAuth();
  const isAdmin = user?.role === "Admin";

  const [dorm, setDorm] = useState<DormResponseDTO | null>(null);
  const [reviews, setReviews] = useState<ReviewResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

 
  const images = dorm?.imageUrlsList ?? [];
  const [activeImg, setActiveImg] = useState(0);

 
  const [rating, setRating] = useState<number>(5);
  const [comment, setComment] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const canReview = !!user;
  const myUserId = user?.id;

  useEffect(() => {
    if (!Number.isFinite(dormId) || dormId <= 0) {
      setError("Invalid dorm id.");
      setLoading(false);
      return;
    }

    (async () => {
      try {
        setLoading(true);
        setError(null);

        const [dormData, reviewData] = await Promise.all([
          dormService.getDormById(dormId),
          reviewService.getByDorm(dormId),
        ]);

        setDorm(dormData);
        setReviews(reviewData);
        setActiveImg(0);
      } catch (e: any) {
        setError(e?.response?.data?.message ?? e?.message ?? "Failed to load dorm.");
      } finally {
        setLoading(false);
      }
    })();
  }, [dormId]);

  const avgRating = useMemo(() => {
    if (!reviews.length) return null;
    const sum = reviews.reduce((s, r) => s + (r.rating ?? 0), 0);
    return Math.round((sum / reviews.length) * 10) / 10;
  }, [reviews]);

  const handlePrev = () => {
    if (!images.length) return;
    setActiveImg((i) => (i - 1 + images.length) % images.length);
  };

  const handleNext = () => {
    if (!images.length) return;
    setActiveImg((i) => (i + 1) % images.length);
  };

  const handleSubmitReview = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!canReview) return;

    const trimmed = comment.trim();
    if (!trimmed) return;

    setSubmitting(true);
    try {
      const created = await reviewService.create({
        rating,
        comment: trimmed,
        dormId,
      });

      setReviews((prev) => [created, ...prev]);
      setComment("");
      setRating(5);
    } catch (e: any) {
      alert(e?.response?.data?.message ?? e?.message ?? "Failed to post review.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteReview = async (reviewId: number) => {
    const ok = window.confirm("Delete this review?");
    if (!ok) return;

    try {
      await reviewService.remove(reviewId);
      setReviews((prev) => prev.filter((r) => r.id !== reviewId));
    } catch (e: any) {
      alert(e?.response?.data?.message ?? e?.message ?? "Failed to delete review.");
    }
  };

  const canDeleteReview = (r: ReviewResponseDTO) => {
    return isAdmin || (myUserId != null && r.author?.id === myUserId);
  };

  
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (!images.length) return;
      if (e.key === "ArrowLeft") handlePrev();
      if (e.key === "ArrowRight") handleNext();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
   
  }, [images.length]);

  if (loading) return <div className="ddpPage ddpCentered">Loading dorm…</div>;
  if (error) return <div className="ddpPage ddpCentered ddpError">{error}</div>;
  if (!dorm) return <div className="ddpPage ddpCentered">Dorm not found.</div>;

  const priceText =
    dorm.price == null ? "—" : new Intl.NumberFormat(undefined, { style: "currency", currency: "BGN" }).format(dorm.price);

  return (
    <>
    <Header/>
    
    <div className="ddpPage">
      
      <div className="ddpGrid">
        
        <div className="ddpLeft">
          
          <section className="ddpCard">
            <div className="ddpCardHeader">
              <h2 className="ddpH2">Photos</h2>
              <div className="ddpMuted">
                {images.length ? `${activeImg + 1} / ${images.length}` : "No images"}
              </div>
            </div>

            {images.length ? (
              <div className="ddpCarousel" role="region" aria-label="Dorm images carousel">
                <button className="ddpCarBtn ddpCarBtnLeft" onClick={handlePrev} aria-label="Previous image">
                  ‹
                </button>

                <div
  className="ddpCarFrame"
  role="img"
  aria-label={`Dorm image ${activeImg + 1}`}
  style={{ backgroundImage: `url("${images[activeImg]}")` }}
/>


                <button className="ddpCarBtn ddpCarBtnRight" onClick={handleNext} aria-label="Next image">
                  ›
                </button>

                <div className="ddpThumbs" aria-label="Image thumbnails">
                  {images.map((url, idx) => (
                    <button
                      key={`${url}-${idx}`}
                      className={`ddpThumb ${idx === activeImg ? "ddpThumbActive" : ""}`}
                      onClick={() => setActiveImg(idx)}
                      aria-label={`Show image ${idx + 1}`}
                    >
                      <img src={url} alt="" className="ddpThumbImg" loading="lazy" />
                    </button>
                  ))}
                </div>
              </div>
            ) : (
              <div className="ddpEmpty">
                <div className="ddpEmptyIcon">🖼️</div>
                <div>
                  <div className="ddpEmptyTitle">No photos yet</div>
                  <div className="ddpMuted">This dorm doesn’t have images uploaded.</div>
                </div>
              </div>
            )}
          </section>

          
          <section className="ddpCard">
            <div className="ddpCardHeader">
              <h2 className="ddpH2">Reviews</h2>
              <div className="ddpMuted">{reviews.length ? `${reviews.length} total` : "Be the first!"}</div>
            </div>

            
            <div className="ddpReviewComposer">
              {!canReview ? (
                <div className="ddpNotice">
                  <div className="ddpNoticeTitle">Want to leave a review?</div>
                  <div className="ddpMuted">Please log in to rate and comment.</div>
                </div>
              ) : (
                <form className="ddpForm" onSubmit={handleSubmitReview}>
                  <div className="ddpFormRow">
                    <label className="ddpLabel" htmlFor="rating">
                      Rating
                    </label>

                    <div className="ddpRatingRow">
                      <input
                        id="rating"
                        type="range"
                        min={1}
                        max={5}
                        step={1}
                        value={rating}
                        onChange={(e) => setRating(Number(e.target.value))}
                      />
                      <div className="ddpRatingValue">
                        <Stars value={rating} className="stars" /> <span className="ddpMuted">{rating} / 5</span>
                      </div>
                    </div>
                  </div>

                  <div className="ddpFormRow">
                    <label className="ddpLabel" htmlFor="comment">
                      Comment
                    </label>
                    <textarea
                      id="comment"
                      className="ddpTextarea"
                      value={comment}
                      onChange={(e) => setComment(e.target.value)}
                      placeholder="Share what you liked (or didn’t)…"
                      maxLength={2000}
                      rows={4}
                    />
                    <div className="ddpCharHint">{comment.length} / 2000</div>
                  </div>

                  <div className="ddpFormActions">
                    <button className="ddpBtn ddpBtnPrimary" type="submit" disabled={submitting || !comment.trim()}>
                      {submitting ? "Posting…" : "Post review"}
                    </button>
                  </div>
                </form>
              )}
            </div>

         
            <div className="ddpReviewList">
              {reviews.length ? (
                reviews.map((r) => (
                  <article key={r.id} className="ddpReviewItem">
                    <div className="ddpReviewTop">
                      <div className="ddpReviewAuthor">
                        {/* <div className="ddpAvatar" aria-hidden="true">
                          {(r.author?.firstName?.[0] ?? "U").toUpperCase()}
                        </div> */} 
                        <div className="ddpAvatar">
  {r.author?.profileImageUrl ? (
    <img
      src={r.author.profileImageUrl}
      alt={fullName(r.author)}
      className="ddpAvatarImg"
    />
  ) : (
    <span className="ddpAvatarFallback">
      {(r.author?.firstName?.[0] ?? "U").toUpperCase()}
    </span>
  )}
</div>
                        <div>
                         <div className="ddpReviewName">
  {r.author?.id ? (
    <Link
      to={`/profile/${r.author.id}`}
      className="ddpAuthorLink"
      title="View profile"
    >
      {fullName(r.author)}
    </Link>
  ) : (
    <span>{fullName(r.author)}</span>
  )}

  {r.author?.id === myUserId ? <span className="ddpBadge">You</span> : null}
</div>


                          <div className="ddpReviewMeta">
                            <Stars value={r.rating ?? 0} className="stars" />
                          </div>
                        </div>
                      </div>

                      {canDeleteReview(r) ? (
                        <button className="ddpBtn ddpBtnDangerOutline" onClick={() => handleDeleteReview(r.id)}>
                          Delete
                        </button>
                      ) : null}
                    </div>

                    <p className="ddpReviewComment">{r.comment}</p>
                  </article>
                ))
              ) : (
                <div className="ddpEmpty ddpEmptySmall">
                  <div className="ddpEmptyIcon">💬</div>
                  <div>
                    <div className="ddpEmptyTitle">No reviews yet</div>
                    <div className="ddpMuted">Be the first to share your experience.</div>
                  </div>
                </div>
              )}
            </div>
          </section>
        </div>

       
        <aside className="ddpRight">
          <section className="ddpCard ddpSticky">
            <div className="ddpCardHeader">
              <h2 className="ddpH2">Details</h2>
              
            </div>

            <div className="ddpDetails">
              <div className="ddpDetailRow">


               <div className="ddpDetailLabel">Rating</div>
  <div className="ddpDetailValue">
    {avgRating == null ? (
      <span className="ddpMuted">No ratings yet</span>
    ) : (
      <>
        <Stars value={avgRating} className="stars" />{" "}
        <span className="ddpMuted">
          {avgRating} / 5 ({reviews.length})
        </span>
      </>
    )}
  </div>

                <div className="ddpDetailLabel">Name</div>
                <div className="ddpDetailValue">{dorm.name}</div>
              </div>

              <div className="ddpDetailRow">
                <div className="ddpDetailLabel">Address</div>
                <div className="ddpDetailValue">{dorm.address}</div>
              </div>

              <div className="ddpDetailRow">
                <div className="ddpDetailLabel">Price</div>
                <div className="ddpDetailValue">{priceText} / month</div>
              </div>

              <div className="ddpDetailRow">
                <div className="ddpDetailLabel">Location</div>
                <div className="ddpDetailValue">
                  {dorm.latitude != null && dorm.longitude != null ? (
                    <>
                       <a
                        className="ddpLink"
                        href={`https://www.google.com/maps?q=${dorm.latitude},${dorm.longitude}`}
                        target="_blank"
                        rel="noreferrer"
                      >
                        Open in Google Maps
                      </a>
                    </>
                  ) : (
                    <span className="ddpMuted">No coordinates provided</span>
                  )}
                </div>
              </div>

              <div className="ddpDivider" />

              <div className="ddpDetailBlock">
                <div className="ddpDetailLabel">Description</div>
                <div className="ddpDetailValue ddpDescription">
                  {dorm.description?.trim() ? dorm.description : <span className="ddpMuted">No description</span>}
                </div>
              </div>
            </div>

           {isAdmin ? (
  <div className="ddpAdminActions">
    <button
      className="ddpBtn ddpBtnOutline"
      type="button"
      onClick={() => navigate(`/dorms/${dormId}/edit`)}
    >
      Edit
    </button>

    <button
      className="ddpBtn ddpBtnDanger"
      type="button"
      onClick={async () => {
        const ok = window.confirm("Delete this dorm? This cannot be undone.");
        if (!ok) return;

        try {
          await dormService.deleteDorm(dormId);
          navigate("/dorms");
        } catch (e: any) {
          alert(e?.response?.data?.message ?? e?.message ?? "Failed to delete dorm.");
        }
      }}
    >
      Delete
    </button>
  </div>
) : null}

          </section>
        </aside>
      </div>
    </div>
    </>
  );
};

export default DormDetailsPage;
