import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  getEventTypes,
  getEventById,
  getEventImg,
  updateEventByIdWithImg,
  updateEventByIdWithoutImg,
} from "../../../services/ApiService"; // Replace with correct paths
import {
  Container,
  Typography,
  Box,
  TextField,
  Select,
  MenuItem,
  Button,
  Input,
} from "@mui/material";
import { format } from "date-fns";

const UpdateEvent = () => {
  const { id } = useParams(); // Event ID from the route
  const navigate = useNavigate();

  const [imageFile, setImageFile] = useState(null);
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [eventTypes, setEventTypes] = useState(null);
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

  const [errors, setErrors] = useState({});
  const [submitStatus, setSubmitStatus] = useState("");
  const [apiError, setApiError] = useState("");

  useEffect(() => {
    const fetchEvent = async () => {
      try {
        const eventData = await getEventById(id); // Fetch event details
        const imageResponse = await getEventImg(id); // Fetch event image
        const eventTypesData= await getEventTypes(id)

        const eventWithImage = {
          ...eventData,
          img: URL.createObjectURL(new Blob([imageResponse])),
        };

        setEvent(eventWithImage);

        setFormData({
          title: eventData.title || "",
          location: eventData.location || "",
          type: eventData.type || "",
          description: eventData.description || "",
          ticketPrice: eventData.ticketPrice || "",
          seatsPerTable: eventData.seatsPerTable || "",
          layout: eventData.layout || "",
          startTime: eventData.startTime || "",
          endTime: eventData.endTime || "",
        });
        
        setEventTypes(eventTypesData);
        setLoading(false);
      } catch (err) {
        setError("Error fetching event details");
        setLoading(false);
      }
    };

    fetchEvent();
  }, [id]);

  if (loading) return <p>Loading event details...</p>;
  if (error) return <p>{error}</p>;

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];

    if (!file.type.startsWith("image/")) {
      setErrors((prevErrors) => ({ ...prevErrors, img: "Only image files are allowed." }));
      setImageFile(null);
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      setErrors((prevErrors) => ({ ...prevErrors, img: "File size must not exceed 5MB." }));
      setImageFile(null);
      return;
    }

    setErrors((prevErrors) => ({ ...prevErrors, img: "" }));
    setImageFile(file);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const validationErrors = {};
    if (!formData.title) validationErrors.title = "Title is required.";
    if (!formData.location) validationErrors.location = "Location is required.";
    if (!formData.type) validationErrors.type = "Type is required.";
    if (!formData.ticketPrice || formData.ticketPrice <= 0) validationErrors.ticketPrice = "Price must be greater than 0.";

    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) return;

    const data = new FormData();
    const eventDto = {
          ...formData,
          startTime: format(new Date(formData.startTime), "yyyy-MM-dd HH:mm"),
          endTime: format(new Date(formData.endTime), "yyyy-MM-dd HH:mm"),
        };
    data.append("eventDto", new Blob([JSON.stringify(eventDto)], { type: "application/json" }));
    if (imageFile) {
      data.append("imageFile", imageFile);
    }

    try {
      if (imageFile) {
        await updateEventByIdWithImg(id, data);
      } else {
        await updateEventByIdWithoutImg(id, data);
      }

      setSubmitStatus("Event updated successfully!");
      setErrors({});
      setApiError("");
      navigate("/admin/events"); // Redirect
    } catch (error) {
      console.error("Error updating event:", error);
      const errorMessage = error.response?.data?.message || "Failed to update event. Please try again.";
      setSubmitStatus("");
      setApiError(errorMessage);
    }
  };

  return (
    <main>
        <Container maxWidth="sm" sx={{ backgroundColor: "#000", color: "white", borderRadius: 2, padding: 3, boxShadow: 3 }}>
            <Box component="form" onSubmit={handleSubmit} sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
                <Typography variant="h5" align="center" sx={{ color: "white" }}>Update Event</Typography>
                {submitStatus && <Typography variant="body2" align="center" sx={{ color: "green" }}>{submitStatus}</Typography>}
                {apiError && <Typography variant="body2" align="center" sx={{ color: "red" }}>{apiError}</Typography>}
                {Object.values(errors).length > 0 && <Typography variant="body2" color="error" align="center">{Object.values(errors).join(" \n")}</Typography>}

                <TextField 
                label="Title" 
                name="title" 
                value={formData.title} 
                onChange={handleInputChange} 
                fullWidth 
                required 
                InputLabelProps={{ style: { color: "white" } }}
                InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
                />

                <TextField 
                label="Location" 
                name="location" 
                value={formData.location} 
                onChange={handleInputChange} 
                fullWidth 
                required 
                InputLabelProps={{ style: { color: "white" } }}
                InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
                />

                {/* Image File */}
                <Box>
                <label
                    htmlFor="event-image"
                    style={{
                    color: "white",
                    display: "block",
                    marginBottom: "8px",
                    fontSize: "16px",
                    }}
                >
                    Event Image
                </label>
                <input
                    id="event-image"
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                    style={{
                    display: "block",
                    color: "white",
                    backgroundColor: "#333",
                    border: "1px solid #555",
                    borderRadius: "4px",
                    padding: "8px",
                    width: "100%",
                    cursor: "pointer",
                    }}
                />
                {errors.img && (
                    <Typography variant="body2" color="error" sx={{ marginTop: "8px" }}>
                    {errors.img}
                    </Typography>
                )}
                </Box>

                {/* EventTypes */}
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
                    <MenuItem value="" disabled sx={{ color: "#aaa" }}>
                    Select Event Type
                    </MenuItem>
                    {eventTypes.map((type) => (
                    <MenuItem
                        key={type}
                        value={type}
                        sx={{
                        backgroundColor: "#333",
                        color: "white",
                        "&:hover": {
                            backgroundColor: "#555",
                        },
                        }}
                    >
                        {type}
                    </MenuItem>
                    ))}
                </Select>
                <TextField 
                label="Ticket Price" 
                name="ticketPrice" 
                type="number" 
                value={formData.ticketPrice} 
                onChange={handleInputChange} 
                fullWidth 
                required 
                InputLabelProps={{ style: { color: "white" } }}
                InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
                />
                <TextField 
                    label="Seats Per Table" 
                    name="seatsPerTable" // Added field for seatsPerTable
                    type="number" 
                    value={formData.seatsPerTable} 
                    onChange={handleInputChange} 
                    fullWidth 
                    required
                    InputLabelProps={{ style: { color: "white" } }}
                InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
                />
                <TextField 
                    label="layout for example 5x5" 
                    name="layout" // Added field for seatsPerTable
                    type="text" 
                    value={formData.layout} 
                    onChange={handleInputChange} 
                    fullWidth 
                    required
                    InputLabelProps={{ style: { color: "white" } }}
                InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
                />
                <TextField 
                label="Start Time" 
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
               label="End Time" 
                name="endTime" 
                type="datetime-local" 
                value={formData.endTime} 
                onChange={handleInputChange} 
                fullWidth 
                required 
                InputLabelProps={{ style: { color: "white" } }}
                InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
                />
                <Input type="file" onChange={handleFileChange} accept="image/*" fullWidth />
                {errors.img && <Typography variant="body2" color="error">{errors.img}</Typography>}
                <Button type="submit" variant="contained" fullWidth>Update Event</Button>
            </Box>
        </Container>
    </main>
  );
};

export default UpdateEvent;
