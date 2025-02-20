import React, { createContext, useState, useEffect } from 'react';
import { getEvents as fetchEvents, getEventById as fetchEventById, deleteEventById as removeEventFromApi } from '../services/ApiService';

export const EventContext = createContext();

export const EventProvider = ({ children }) => {
  const [events, setEvents] = useState([]);  // List of all events
  const [event, setEvent] = useState({});    // Single event details

  // Fetch and update all events when the component mounts
  useEffect(() => {
    async function fetchData() {
      try {
        const fetchedEvents = await fetchEvents(); // Call the API to get events
        setEvents(fetchedEvents); // Store events in the state
      } catch (error) {
        console.error("Error fetching events:", error);
      }
    }

    fetchData();
  }, []);

  // Update events list in state
  const updateEvents = (newEvents) => {
    setEvents(newEvents);
  };

  // Add a new event to the list
  const addEvent = (newEvent) => {
    setEvents((prevEvents) => [...prevEvents, newEvent]);
  };

  // Set the event details when a single event is fetched
  const updateEvent = (event) => {
    setEvent(event);
  };

  // Remove event by ID from the events list and update state
  const removeEventById = async (id) => {
    try {
      await removeEventFromApi(id);  // Call API to delete the event
      setEvents((prevEvents) => prevEvents.filter((event) => event.id !== id)); // Remove it from state
    } catch (error) {
      console.error(`Error deleting event with ID ${id}:`, error);
    }
  };

  // Fetch a specific event by ID
  const getEventById = async (id) => {
    try {
      const fetchedEvent = await fetchEventById(id); // Fetch event by ID
      setEvent(fetchedEvent); // Update state with the fetched event
    } catch (error) {
      console.error(`Error fetching event with ID ${id}:`, error);
    }
  };

  return (
    <EventContext.Provider value={{
      events, event, updateEvents, updateEvent, removeEventById, addEvent, getEventById
    }}>
      {children}
    </EventContext.Provider>
  );
};
