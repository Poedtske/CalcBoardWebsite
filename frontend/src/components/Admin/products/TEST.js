import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import styles from '../home/home.module.css';
import { getEvents } from '../../services/ApiService';

const Home = () => {
  const [events, setEvents] = useState([]); // Store events data
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [intervalId, setIntervalId] = useState(null);

  useEffect(() => {
    let fetchData = async () => {
      try {
        const events = await getEvents(); // Assuming getEvents fetches events from your backend
        if (Array.isArray(events)) {
          setEvents(events); // Only update state if events is an array
        } else {
          console.error('Fetched data is not an array:', events);
        }
      } catch (error) {
        console.error('Error fetching events:', error);
      }
    };
    fetchData();
  }, []);

  // Helper function to format date to 'DD/MM/YYYY'
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are zero-indexed
    const year = date.getFullYear();

    return `${day}/${month}/${year}`;
  };

  // Helper function to format time for spond events to 'HH:mm'
  const formatTimeSpond = (dateString) => {
    const date = new Date(dateString);
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${hours}:${minutes}`;
  };

  // Helper function to format time for regular events to 'HH:mm'
  const formatTime = (timeString) => {
    const [hours, minutes] = timeString.split(':');
    return `${hours}:${minutes}`;
  };

  // Function to get event details
  const getEventDetails = (event) => {
    if (event.spondId) {
      // For spondEvents (events with spondId), use startTime directly
      return (
        <>
          <p className={styles.p} id="naam">{event.title}</p>
          <p className={styles.p} id="datum">{formatDate(event.startTime)}</p>
          <p className={styles.p} id="uur">{formatTimeSpond(event.startTime)}</p>
          <p className={styles.p} id="locatie">{event.location}</p>
        </>
      );
    } else if (event.dates && event.dates.length > 0) {
      // For regular events with multiple dates
      const firstDate = event.dates[0]; // You can adjust this logic to pick a specific date
      return (
        <>
          <p className={styles.p} id="naam">{event.title}</p>
          <p className={styles.p} id="datum">{formatDate(firstDate.date)}</p>
          <p className={styles.p} id="uur">{formatTime(firstDate.startTime)}</p>
          <p className={styles.p} id="locatie">{event.location}</p>
        </>
      );
    }
    return null; // Return null if no valid event data is found
  };

  // Function to go to the next activity/event
  const nextActivity = () => {
    setCurrentImageIndex((prevIndex) => (prevIndex === events.length - 1 ? 0 : prevIndex + 1));
  };

  // Function to go to the previous activity/event
  const prevActivity = () => {
    setCurrentImageIndex((prevIndex) => (prevIndex === 0 ? events.length - 1 : prevIndex - 1));
  };

  // Start the auto sliding when events are available
  useEffect(() => {
    if (events.length > 0) {
      const id = setInterval(nextActivity, 5000); // Slide every 5 seconds
      setIntervalId(id);
    }
    return () => {
      clearInterval(intervalId); // Clear the interval when the component is unmounted
    };
  }, [events]);

  const handleNext = () => {
    nextActivity();
    clearInterval(intervalId); // Stop auto slide on interaction
  };

  const handlePrev = () => {
    prevActivity();
    clearInterval(intervalId); // Stop auto slide on interaction
  };

  const firstEvent = events.length > 0 ? events[currentImageIndex] : null;

  return (
    <main className={styles.main}>
      {/* Banner Image */}
      <img src="/images/banner.jpeg" alt="banner" id="banner" />

      {/* Display first event poster if available */}
      {firstEvent && firstEvent.poster && (
        <section className={styles.section} style={{ backgroundColor: 'black' }}>
          <Link to={`/events/${firstEvent.id}`} style={{ marginInline: 'auto', width: '30%' }}>
            <img
              width="100%"
              id="poster"
              src={firstEvent.poster}
              alt={`${firstEvent.title}_poster`}
            />
          </Link>
        </section>
      )}

      {/* Event Slider */}
      <section className={styles.activity} style={{ backgroundColor: 'white', color: 'black', textAlign: 'center' }}>
        {firstEvent && getEventDetails(firstEvent)}

        <div>
          <button style={{ width: '10em' }}>
            <Link to="/kalender">Kalender</Link>
          </button>
        </div>

        {/* Slider Controls */}
        <div>
          <button
            style={{ width: '2em', marginRight: '10px' }} // Add margin-right for spacing
            className="prev"
            onClick={handlePrev}
          >
            &#10094;
          </button>

          <button style={{ width: '2em' }} className="next" onClick={handleNext}>
            &#10095;
          </button>
        </div>
      </section>
    </main>
  );
};

export default Home;
