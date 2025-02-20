import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Box,
  Typography,
  TextField,
  Select,
  MenuItem,
  Button,
} from "@mui/material";
import { createEvent, getEventTypes } from "../../../services/ApiService";
import { format } from "date-fns";

const CreateEvent = () => {
  const [eventTypes, setEventTypes] = useState([]);
  const [formData, setFormData] = useState({
    title: "",
    location: "",
    type: "",
    description: "",
    ticketPrice: "",
    seatsPerTable: "",
    layout: "",
    startTime: "",
    endTime: "",
  });

  const [imageFile, setImageFile] = useState(null);
  const [errors, setErrors] = useState({});
  const [submitStatus, setSubmitStatus] = useState("");
  const [apiError, setApiError] = useState("");
  const navigate = useNavigate();

  // Fetch event types
  useEffect(() => {
    const fetchEventTypes = async () => {
      try {
        const types = await getEventTypes();
        setEventTypes(types.filter((type) => type !== "UNKNOWN"));
      } catch (error) {
        console.error("Error fetching event types:", error);
      }
    };
    fetchEventTypes();
  }, []);

  // Input change handler
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  // Image file change handler
  const handleFileChange = (e) => {
    const file = e.target.files[0];

    // Validate image file
    if (!file.type.startsWith("image/")) {
      setErrors({ ...errors, img: "Only image files are allowed." });
      setImageFile(null);
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      setErrors({ ...errors, img: "File size must not exceed 5MB." });
      setImageFile(null);
      return;
    }
    setErrors({ ...errors, img: "" });
    setImageFile(file);
  };

  // Submit handler
  const handleSubmit = async (e) => {
    e.preventDefault();

    const validationErrors = {};
    if (!formData.title) validationErrors.title = "Title is required.";
    if (!formData.location) validationErrors.location = "Location is required.";
    if (!formData.type) validationErrors.type = "Event type is required.";
    if (!formData.description)
      validationErrors.description = "Description is required.";
    if (!formData.ticketPrice || formData.ticketPrice <= 0)
      validationErrors.ticketPrice = "Ticket price must be greater than 0.";
    if (!formData.seatsPerTable || formData.seatsPerTable <= 0)
      validationErrors.seatsPerTable = "Seats per table must be greater than 0.";
    if (!formData.layout) validationErrors.layout = "Layout is required.";
    if (!formData.startTime) validationErrors.startTime = "Start time is required.";
    if (!formData.endTime) validationErrors.endTime = "End time is required.";
    if (!imageFile) validationErrors.img = "Image file is required.";

    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) return;

    // Prepare form data for API
    const data = new FormData();
    const eventDto = {
      ...formData,
      startTime: format(new Date(formData.startTime), "yyyy-MM-dd HH:mm"),
      endTime: format(new Date(formData.endTime), "yyyy-MM-dd HH:mm"),
    };

    data.append("eventDto", new Blob([JSON.stringify(eventDto)], { type: "application/json" }));
    data.append("imageFile", imageFile);

    try {
      await createEvent(data);
      setSubmitStatus("Event created successfully!");
      setErrors({});
      setApiError("");
      navigate("/admin/events");
    } catch (error) {
      console.error("Error creating event:", error);
      const errorMessage =
        error.response?.data?.message ||
        "Failed to create event. Please try again.";
      setSubmitStatus("");
      setApiError(errorMessage);
    }
  };

  return (
    <main>
      <Container
        maxWidth="sm"
        sx={{
          backgroundColor: "#000",
          color: "white",
          borderRadius: 2,
          padding: 3,
          boxShadow: 3,
        }}
      >
        <Box
          component="form"
          onSubmit={handleSubmit}
          sx={{
            display: "flex",
            flexDirection: "column",
            gap: 2,
          }}
        >
          <Typography variant="h5" align="center" sx={{ color: "white" }}>
            Maak Evenement
          </Typography>

          {submitStatus && (
            <Typography variant="body2" align="center" sx={{ color: "green" }}>
              {submitStatus}
            </Typography>
          )}
          {apiError && (
            <Typography variant="body2" align="center" sx={{ color: "red" }}>
              {apiError}
            </Typography>
          )}

          {/* Title */}
          <TextField
            label="Titel"
            name="title"
            value={formData.title}
            onChange={handleInputChange}
            fullWidth
            required
            InputLabelProps={{ style: { color: "white" } }}
            InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
          />

          {/* Location */}
          <TextField
            label="Locatie"
            name="location"
            value={formData.location}
            onChange={handleInputChange}
            fullWidth
            required
            InputLabelProps={{ style: { color: "white" } }}
            InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
          />

          {/* Type */}
          <Select
            name="type"
            value={formData.type}
            onChange={handleInputChange}
            displayEmpty
            fullWidth
            sx={{
              color: "white",
              backgroundColor: "#333",
              "& .MuiSelect-icon": { color: "white" },
              "& .MuiOutlinedInput-notchedOutline": { borderColor: "#555" },
              "&:hover .MuiOutlinedInput-notchedOutline": { borderColor: "white" },
            }}
            MenuProps={{
              PaperProps: {
                sx: {
                  backgroundColor: "#333",
                  color: "white",
                  "& .MuiMenuItem-root": {
                    "&:hover": {
                      backgroundColor: "#555",
                    },
                  },
                },
              },
            }}
          >
            <MenuItem value="" disabled>
              Selecteer Type
            </MenuItem>
            {eventTypes.map((type) => (
              <MenuItem key={type} value={type}>
                {type}
              </MenuItem>
            ))}
          </Select>

          {/* Description */}
          <TextField
            label="Omschrijving"
            name="description"
            value={formData.description}
            onChange={handleInputChange}
            fullWidth
            multiline
            rows={3}
            required
            InputLabelProps={{ style: { color: "white" } }}
            InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
          />

          {/* Ticket Price */}
          <TextField
            label="Ticket Prijs (€)"
            name="ticketPrice"
            type="number"
            value={formData.ticketPrice}
            onChange={handleInputChange}
            fullWidth
            required
            InputLabelProps={{ style: { color: "white" } }}
            InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
          />

          {/* Seats per Table */}
          <TextField
            label="Stoelen per Tafel"
            name="seatsPerTable"
            type="number"
            value={formData.seatsPerTable}
            onChange={handleInputChange}
            fullWidth
            required
            InputLabelProps={{ style: { color: "white" } }}
            InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
          />

          {/* Layout */}
          <TextField
            label="Layout (e.g., 5x5)"
            name="layout"
            value={formData.layout}
            onChange={handleInputChange}
            fullWidth
            required
            InputLabelProps={{ style: { color: "white" } }}
            InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
          />

          {/* Start and End Times */}
          <TextField
            label="Start Tijd"
            name="startTime"
            type="datetime-local"
            value={formData.startTime}
            onChange={handleInputChange}
            fullWidth
            required
            InputLabelProps={{ style: { color: "white" } }}
            InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
          />
          <TextField
            label="Eind Tijd"
            name="endTime"
            type="datetime-local"
            value={formData.endTime}
            onChange={handleInputChange}
            fullWidth
            required
            InputLabelProps={{ style: { color: "white" } }}
            InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
          />

          {/* Image Upload */}
          <input type="file" accept="image/*" onChange={handleFileChange} required style={{
                display: "block",
                color: "white",
                backgroundColor: "#333",
                border: "1px solid #555",
                borderRadius: "4px",
                padding: "8px",
                width: "100%",
                cursor: "pointer",
              }}/>

          {/* Submit Button */}
          <Button type="submit" variant="contained" fullWidth>
            Maak aan
          </Button>
        </Box>
      </Container>
    </main>
  );
};

export default CreateEvent;
