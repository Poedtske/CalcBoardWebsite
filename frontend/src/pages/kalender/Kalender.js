import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import styles from './kalender.module.css'; // Import CSS module
import { getEvents } from '../../services/ApiService';

const Kalender = () => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const formattedEvents = await getEvents(); // Fetch events (already formatted)
        console.log("Fetched Events:", formattedEvents);
        setEvents(formattedEvents);
        setLoading(false);
      } catch (error) {
        setError("Error fetching events");
        setLoading(false);
        console.error("Error fetching events:", error);
      }
    };
    fetchData();
  }, []);

  if (loading) {
    return <p>Loading events...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <main className={styles.main}>
      <div className={styles.eventList}>
        <div className={styles.eventContainer}>
          {events.map((event) => {
            const mainDate = event.mainDate || { day: "--", month: "N/A" }; // Fallback for mainDate
            const extraDates = event.extraDates || []; // Ensure extraDates is an array

            return (
              <Link
                to={{
                  pathname: `/events/${event.id}`,
                  state: { event }, // Pass event data as state
                }}
                key={event.id}
                className={styles.event}
              >
                <div className={styles.dateContainer}>
                  <div className={styles.dateDay}>{mainDate.day}</div>
                  <div className={styles.dateMonth}>{mainDate.month}</div>
                </div>
                <div className={styles.eventDetails}>
                  <div className={styles.eventTitle}>{event.title}</div>
                  <div className={styles.eventLocation}>{event.location}</div>
                  {extraDates.length > 0 && (
                    <div className={styles.extraDates}>
                      {extraDates.map((date, index) => (
                        <div key={index} className={styles.extraDate}>
                          {date.day} {date.month}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </Link>
            );
          })}
        </div>
      </div>
    </main>
  );
};

export default Kalender;
