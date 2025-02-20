import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getEventById, addTicketToCart } from "../../services/ApiService"; // Import API functions
import styles from "./Layout.module.css"; // Your CSS styles

const EventGrid = () => {
  const { id } = useParams(); // Get event ID from route params
  const navigate = useNavigate();
  const [event, setEvent] = useState(null); // State to hold event details
  const [loading, setLoading] = useState(true); // State to track loading
  const [error, setError] = useState(null); // State to track errors
  const [sidebarVisible, setSidebarVisible] = useState(false);
  const [selectedTable, setSelectedTable] = useState(null);
  const [formError, setFormError] = useState(null); // State to handle form errors
  const [successMessage, setSuccessMessage] = useState(null); // State to handle success messages

  // Fetch the event details when the component mounts
  useEffect(() => {
    const fetchEvent = async () => {
      try {
        const fetchedEvent = await getEventById(id); // Fetch event details from API
        setEvent(fetchedEvent); // Store the event in state
        setLoading(false); // Stop loading once data is fetched
      } catch (err) {
        console.error("Error fetching event:", err);
        setError("Failed to fetch event details. Please try again later.");
        setLoading(false); // Stop loading even if there's an error
      }
    };

    fetchEvent();
  }, [id]);

  // Open the update form sidebar when a table is clicked
  const openUpdateForm = (table, index) => {
    setSelectedTable({ ...table, index }); // Store the table data and its index
    setFormError(null); // Clear previous errors
    setSuccessMessage(null); // Clear previous success messages
    setSidebarVisible(true); // Show the sidebar
  };

  // Close the sidebar
  const closeSidebar = () => {
    setSidebarVisible(false);
    setSelectedTable(null); // Clear the selected table
  };

  // Handle ticket form submission
  const handleTicketSubmit = async (e) => {
    e.preventDefault();
    const amount = parseInt(e.target.amount.value, 10);
    const availableSeats =
      selectedTable.seats - (selectedTable.tickets ? selectedTable.tickets.length : 0);

    if (amount > availableSeats) {
      setFormError(`Cannot purchase more than ${availableSeats} seats.`);
      return;
    }

    const ticketData = {
      table: selectedTable.id, // Table ID
      price: e.target.price.value, // Price from the event
      event: e.target.event.value, // Event ID
      amount, // Number of tickets to purchase
    };

    try {
      // Call API to add ticket
      const response = await addTicketToCart(ticketData);
      console.log(response.status);
      if (response.status == 200) {
        const newTickets = await response.data; // Parse the JSON response
        // Update table's tickets after successful purchase
        const updatedTables = event.tables.map((table) =>
          table.id === selectedTable.id
            ? { ...table, tickets: [...(table.tickets || []), ...new Array(amount).fill(newTickets)] }
            : table
        );
        setEvent({ ...event, tables: updatedTables });
        setFormError(null); // Clear errors if any
        setSuccessMessage("Ticket created and added to your cart."); // Display success message
      }
       else {
        const errorData = await response.data; // Get error details
        setFormError(errorData.message || "Failed to purchase ticket."); // Display error message
      }
    } catch (err) {
      if(err.response.status==403){
        navigate("/login");
      }
      console.error("Error submitting ticket:", err);
      setFormError("Failed to purchase ticket. Please try again.");
    }
  };

  // If data is still loading, show loading message
  if (loading) return <p>Loading...</p>;

  // If there is an error, show an error message
  if (error) return <p>{error}</p>;

  // Render the event grid layout
  return (
    <main>
      <nav>
        <button onClick={() => setSidebarVisible(!sidebarVisible)}>
          Toggle Sidebar
        </button>
      </nav>

      <div>
        <h3>{event.title}</h3>
        <h4>{event.location}</h4>

        <div
          className={styles.event_grid}
          style={{
            gridTemplateColumns: `repeat(${event.kolommen}, 1fr)`,
            gridTemplateRows: `repeat(${event.rijen}, 1fr)`,
          }}
        >
          {/* Render tables */}
          {event.tables.map((table, index) => (
            <button
              key={table.id}
              className={styles.event_grid_item}
              style={{
                width: `${table.width}px`,
                height: `${table.height}px`,
                marginTop: table.margin?.[0] || 0,
                marginRight: table.margin?.[1] || 0,
                marginBottom: table.margin?.[2] || 0,
                marginLeft: table.margin?.[3] || 0,
                paddingBlock: `${table.width / 4}px`,
              }}
              onClick={() => openUpdateForm(table, index)} // Pass the table and its index
            >
              <span id="tableNr">Nr. {index + 1}</span> {/* Display table number */}
              <span
                id="availableSeats"
                style={{ border: "solid black 2px", paddingInline: "1em" }}
              >
                {table.seats - (table.tickets ? table.tickets.length : 0)} {/* Available seats */}
              </span>
              <p id="seats" style={{ display: "none" }}>{table.seats}</p>
            </button>
          ))}
        </div>
      </div>

      {/* Sidebar for selected table */}
      {sidebarVisible && (
        <div className={`${styles.sidebar} ${sidebarVisible ? styles.open : ""}`}>
          <button onClick={closeSidebar} className={styles.closeBtn}>
            Sluiten &times;
          </button>
          {selectedTable && (
            <div>
              {/* Display table details */}
              <p>
                Tafelnummer:{" "}
                {selectedTable.index !== undefined ? `Nr. ${selectedTable.index + 1}` : "N/A"}
              </p>
              <p>
                Beschikbare Plaatsen:{" "}
                {selectedTable.seats - (selectedTable.tickets ? selectedTable.tickets.length : 0)}
              </p>
              <p>Prijs: {"€" + event.ticketPrice || "N/A"}</p>
              {formError && <p className={styles.error}>{formError}</p>}
              {successMessage && (
                <div>
                  <p className={styles.success}>{successMessage}</p>
                  <button onClick={() => navigate("/cart")}>Go to Cart</button>
                </div>
              )}
              {!successMessage &&
                selectedTable.seats - (selectedTable.tickets ? selectedTable.tickets.length : 0) >
                  0 && (
                  <form onSubmit={handleTicketSubmit}>
                    <input
                      type="hidden"
                      name="table"
                      value={selectedTable.id} // Table ID
                    />
                    <input
                      type="hidden"
                      name="event"
                      value={event.id} // Event ID
                    />
                    <input
                      type="hidden"
                      name="price"
                      value={event.ticketPrice} // Price from the event
                    />
                    <label>
                      Hoeveelheid:
                      <input
                        type="number"
                        name="amount"
                        min="1"
                        max={
                          selectedTable.seats -
                          (selectedTable.tickets ? selectedTable.tickets.length : 0)
                        }
                        required
                      />
                    </label>
                    <button type="submit">Koop Ticket</button>
                  </form>
                )}
            </div>
          )}
        </div>
      )}
    </main>
  );
};

export default EventGrid;
