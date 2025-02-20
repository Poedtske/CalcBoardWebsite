import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getEventById, deleteEventById, getEventImg } from "../../../services/ApiService"; // Import API functions
import styles from "../../../css/entity.module.css";
import {
  Container,
  Typography,
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
} from "@mui/material";

const ShowEvent = () => {
  const { id } = useParams(); // Get event ID from route params
  const navigate = useNavigate();

  const [event, setEvent] = useState(null); // State to hold event details
  const [loading, setLoading] = useState(true); // State to track loading
  const [error, setError] = useState(null); // State to track errors

  useEffect(() => {
    const fetchEvent = async () => {
      try {
        const fetchedEvent = await getEventById(id); // Fetch event details from API
        const imageResponse = await getEventImg(id);
        if(imageResponse!=null){
          const eventWithImage= {
            ...fetchedEvent,
            img: URL.createObjectURL(new Blob([imageResponse])), // Convert image response to blob
          };
  
          
          setEvent(eventWithImage); // Store the event in state
        }else{
          const eventWithImage= {
            ...fetchedEvent,
            img: null, // Convert image response to blob
          };
          setEvent(eventWithImage);
        }
        setLoading(false); // Stop loading once data is fetched
      } catch (err) {
        console.error("Error fetching event:", err);
        setError("Failed to fetch event details. Please try again later.");
        setLoading(false); // Stop loading even if there's an error
      }
    };

    fetchEvent();
  }, [id]);

  const deleteEvent = async () => {
    try {
      await deleteEventById(id); // Call API to delete the event
      navigate("/admin/events"); // Navigate back after successful deletion
    } catch (err) {
      console.error("Error deleting event:", err);
      setError("Failed to delete the event. Please try again.");
    }
  };

  const formatTime = (date) => {
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    return `${hours}:${minutes}`;
  };

  const formatDate = (date) => {
    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
  };

  const renderDatesList = (dates) => {
    return (
      <ul>
        {dates.map((entry, index) => {
          const startDateTime = new Date(`${entry.date}T${entry.startTime}`);
          const endDateTime = new Date(`${entry.date}T${entry.endTime}`);
          return (
            <p>
              {formatDate(startDateTime)} - {formatTime(startDateTime)} to {formatTime(endDateTime)}
            </p>
          );
        })}
      </ul>
    );
  };

  if (loading) {
    return <p>Loading event details...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <main>
      <div className={styles.post_item}>
        <div className={styles.post_content}>
          <h1>{event.title}</h1>

          {/* event Image */}
      {event.img && (
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            mb: 3,
          }}
        >
          <img
            src={event.img} // Use the image URL created from the Blob
            alt={event.name}
            style={{
              width: "300px",
              height: "300px",
              objectFit: "cover",
              borderRadius: "10px",
            }}
          />
        </Box>
      )}

          {event.description && (
            <>
              <h2>Omschrijving</h2>
              <p>{event.description}</p>
            </>
          )}

          <h2>Locatie</h2>
          <p>{event.location}</p>
          <h2>Datum & Tijd</h2> 
          {event.startTime && event.endTime ? (
            <>
              <p>Datum: {formatDate(new Date(event.startTime))}</p> {/* Added date */}                          
              <p>Begin: {formatTime(new Date(event.startTime))}</p>
              <p>Einde: {formatTime(new Date(event.endTime))}</p>
            </>
            
          ) : event.dates && event.dates.length > 0 ? (
            renderDatesList(event.dates)
          ) : (
            <p>Geen tijd & datum beschikbaar</p>
          )}

          {event.spondId && (
            <button style={{ border: "none", backgroundColor: "transparent" }}>
              <a
                href={`https://spond.com/client/sponds/${event.spondId}`}
                target="_blank"
                rel="noopener noreferrer"
              >
                <img
                  className="Spond"
                  src="/images/spond.png"
                  alt="Spond-logo"
                />
              </a>
            </button>
          )}

          <div className="row">
            <button
              className={styles.updateBtn}
              onClick={() => navigate(`/admin/events/update/${id}`)}
            >
              Aanpassen
            </button>
            <div className="col-6 text-end">
              <button
                onClick={() => {
                  if (window.confirm("Ben je zeker dat je dit evenement wilt verwijderen?")) {
                    deleteEvent();
                  }
                }}
                className={styles.deleteBtn}
              >
                Verwijderen
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
};

export default ShowEvent;
