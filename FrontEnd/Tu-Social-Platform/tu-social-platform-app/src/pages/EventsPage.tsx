import React, { useState } from "react";
import type { EventItem, NewEvent } from "../types";
import EventForm from "../components/EventForm";
import EventList from "../components/EventList";
import LocationModal from "../components/LocationModal";
import Header from "../components/Header";
import "../styles/EventsPage.css";

const CURRENT_USER_NAME = "Martin Petrov";

const MOCK_EVENTS: EventItem[] = [
  {
    id: 1,
    creatorName: "Alex Ivanov",
    title: "Friday Night Party",
    description: "Chill house music, drinks and games. Bring a friend!",
    address: "Dorm 3, Student City, Sofia",
    eventType: "party",
    dateTime: "2025-11-21T21:00",
    capacity: 40,
    location: { lat: 42.655, lng: 23.35 },
  },
  {
    id: 2,
    creatorName: "Elena Georgieva",
    title: "JavaScript Study Group",
    description: "Weekly meetup to work on projects and interview tasks.",
    address: "TU Library, Room 204",
    eventType: "workshop",
    dateTime: "2025-11-22T18:00",
    capacity: 15,
    location: { lat: 42.6565, lng: 23.347 },
  },
  {
    id: 3,
    creatorName: "Student Council",
    title: "Football Tournament",
    description: "5v5 games, mixed teams. Register your team or join solo.",
    address: "Dorm 8 Football Field",
    eventType: "sport",
    dateTime: "2025-11-25T16:00",
    capacity: 60,
    location: { lat: 42.654, lng: 23.34 },
  },
];

type FilterType =
  | "all"
  | "party"
  | "workshop"
  | "conference"
  | "sport"
  | "meetup";

const EventsPage: React.FC = () => {
  const [events, setEvents] = useState<EventItem[]>([]);
  const [selectedEvent, setSelectedEvent] = useState<EventItem | null>(null);
  const [showForm, setShowForm] = useState<boolean>(false);
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [filterType, setFilterType] = useState<FilterType>("all");

  const addEvent = (newEvent: NewEvent) => {
    const eventWithCreator: EventItem = {
      id: Date.now(),
      creatorName: CURRENT_USER_NAME,
      ...newEvent,
    };
    setEvents((prev) => [...prev, eventWithCreator]);
  };

  const allEvents = [...MOCK_EVENTS, ...events];
  const normalizedSearch = searchTerm.trim().toLowerCase();

  const filteredEvents = allEvents.filter((event) => {
    if (filterType !== "all" && event.eventType !== filterType) return false;
    if (!normalizedSearch) return true;

    const haystack = `${event.title} ${event.address}`.toLowerCase();
    return haystack.includes(normalizedSearch);
  });

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
  };

  return (
    <>
    <Header/>
    <div className="events-page">
      <div className="events-page__content">
        <h1 className="events-title">Events</h1>

        
        <div className="events-actions">
          <button
            type="button"
            className={
              "events-actions__btn events-actions__btn--primary" +
              (showForm ? " events-actions__btn--active" : "")
            }
            onClick={() => setShowForm((prev) => !prev)}
          >
            Add event +
          </button>

          <button
            type="button"
            className="events-actions__btn events-actions__btn--secondary"
            onClick={() => {
              console.log("My events – TODO: navigate");
            }}
          >
            My events
          </button>

          <button
            type="button"
            className="events-actions__btn events-actions__btn--secondary"
            onClick={() => {
              console.log("Joined events – TODO: navigate");
            }}
          >
            Joined events
          </button>
        </div>

        
        {showForm && (
          <div className="events-form-wrapper">
            <EventForm onCreate={addEvent} />
          </div>
        )}

        
        <form className="events-search" onSubmit={handleSearchSubmit}>
          <div className="events-search__left">
            <input
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search events by title or address"
              className="events-search__input"
            />
            <button type="submit" className="events-search__button">
              Search
            </button>
          </div>

          <select
            value={filterType}
            onChange={(e) => setFilterType(e.target.value as FilterType)}
            className="events-search__select"
          >
            <option value="all">All types</option>
            <option value="party">Party</option>
            <option value="meetup">Meetup</option>
            <option value="conference">Conference</option>
            <option value="workshop">Workshop</option>
            <option value="sport">Sport</option>
          </select>
        </form>

        
        <EventList
          events={filteredEvents}
          onCheckLocation={(event) => setSelectedEvent(event)}
        />
      </div>

      {selectedEvent && (
        <LocationModal
          event={selectedEvent}
          onClose={() => setSelectedEvent(null)}
        />
      )}
    </div>
    </>
  );
};

export default EventsPage;
