import React, { useEffect, useState } from "react";
import "../styles/HomePage.css";
import Header from "../components/Header";
import { useNavigate } from "react-router-dom"; 
import image1 from "../assets/1.jpg";
import image2 from "../assets/2.jpg";
import image3 from "../assets/3.jpg";

type Slide = {
  id: number;
  src: string;
  alt: string;
};

const slides: Slide[] = [
  {
    id: 1,
    src: image1, 
    alt: "Students at the campus",
  },
  {
    id: 2,
    src: image2,
    alt: "Group of students studying together",
  },
  {
    id: 3,
    src: image3,
    alt: "Technical University building",
  },
];

const HomePage: React.FC = () => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const navigate = useNavigate();  

  const nextSlide = () => {
    setCurrentIndex((prevIndex) => (prevIndex + 1) % slides.length);
  };

  const prevSlide = () => {
    setCurrentIndex((prevIndex) =>
      prevIndex === 0 ? slides.length - 1 : prevIndex - 1
    );
  };

  const goToSlide = (index: number) => {
    setCurrentIndex(index);
  };

  useEffect(() => {
    const interval = setInterval(nextSlide, 5000);
    return () => clearInterval(interval);
  }, []);

  const handleClick = (path: string) => {
    navigate(path);  
  };

  return (
    <>
    <Header/>
    
    <main className="home">
      
      <section className="hero-simple">
        <div className="hero-inner">
          
          <div className="hero-carousel hero-carousel-large">
            <div className="carousel">
              <div
                className="carousel-track"
                style={{ transform: `translateX(-${currentIndex * 100}%)` }}
              >
                {slides.map((slide) => (
                  <div className="carousel-slide" key={slide.id}>
                    <img src={slide.src} alt={slide.alt} />
                  </div>
                ))}
              </div>

              <button
                className="carousel-control carousel-control-prev"
                onClick={prevSlide}
                aria-label="Previous slide"
              >
                ‹
              </button>
              <button
                className="carousel-control carousel-control-next"
                onClick={nextSlide}
                aria-label="Next slide"
              >
                ›
              </button>

              <div className="carousel-dots">
                {slides.map((_, index) => (
                  <button
                    key={index}
                    className={
                      "carousel-dot" +
                      (index === currentIndex ? " carousel-dot-active" : "")
                    }
                    onClick={() => goToSlide(index)}
                    aria-label={`Go to slide ${index + 1}`}
                  />
                ))}
              </div>
            </div>
          </div>

          
          <div className="hero-text-centered">
            <h1>Welcome to our social platform</h1>
            <h2>for students of the Technical University</h2>
            <p>
              Meet new people, find a roomie, discover events and stay informed
              about everything happening around campus.
            </p>
          </div>
        </div>
      </section>

      {/* BUTTON SECTION */}
       <section className="home-actions">
        <div className="home-actions-inner">
          <h3>Explore the campus community</h3>
          <p className="home-actions-subtitle">
            Jump straight into what you&apos;re looking for.
          </p>

          <div className="home-actions-buttons">
            <button
              className="hero-action-button"
              onClick={() => handleClick("/events")}
            >
              Events
            </button>
            <button
              className="hero-action-button"
              onClick={() => handleClick("/dorms")}
            >
              Dorms
            </button>
            <button
              className="hero-action-button"
              onClick={() => handleClick("/listings")}
            >
              Find a Roomie
            </button>
            <button
              className="hero-action-button"
              onClick={() => handleClick("/information")}
            >
              Information
            </button>
            <button
              className="hero-action-button"
              onClick={() => handleClick("/posts")}
            >
              Posts
            </button>
          </div>
        </div>
      </section>
    </main>
    </>
  );
};


export default HomePage;