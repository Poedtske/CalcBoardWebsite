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
  Checkbox,
  FormControlLabel,
} from "@mui/material";
import { createProduct, getCategories } from "../../../services/ApiService";

const CreateProduct = () => {
  const [categories, setCategories] = useState([]);
  const [formData, setFormData] = useState({
    name: "",
    price: "",
    available: true,
    category: "",
  });
  const [imageFile, setImageFile] = useState(null);
  const [errors, setErrors] = useState({});
  const [submitStatus, setSubmitStatus] = useState("");
  const [apiError, setApiError] = useState("");
  const navigate = useNavigate();

  // Fetch categories
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const categoriesData = await getCategories();
        setCategories(categoriesData);
      } catch (error) {
        console.error("Error fetching categories:", error);
      }
    };
    fetchCategories();
  }, []);

  // Input change handler
  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === "checkbox" ? checked : value,
    });
  };

  // Image file change handler
  const handleFileChange = (e) => {
    const file = e.target.files[0];

    // Validate file type
    if (!file.type.startsWith("image/")) {
      setErrors((prevErrors) => ({
        ...prevErrors,
        img: "Only image files are allowed.",
      }));
      setImageFile(null);
      return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      setErrors((prevErrors) => ({
        ...prevErrors,
        img: "File size must not exceed 5MB.",
      }));
      setImageFile(null);
      return;
    }

    setErrors((prevErrors) => ({ ...prevErrors, img: "" })); // Clear errors
    setImageFile(file);
  };

  // Submit handler
  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validate form fields
    const validationErrors = {};
    if (!formData.name) validationErrors.name = "Name is required.";
    if (!formData.price || formData.price <= 0)
      validationErrors.price = "Price must be greater than 0.";
    if (!imageFile) validationErrors.img = "Image file is required.";
    if (!formData.category) validationErrors.category = "Category is required.";

    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) return;

    // Prepare FormData
    const data = new FormData();
    data.append("productDto", new Blob([JSON.stringify(formData)],{ type: "application/json" }));
    data.append("imageFile", imageFile);

    try {
      // API call to create product
      await createProduct(data);
      setSubmitStatus("Product created successfully!");
      setErrors({});
      setApiError("");
      navigate("/admin/products"); // Redirect to product list
    } catch (error) {
      console.error("Error creating product:", error);
      const errorMessage =
        error.response?.data?.message ||
        "Failed to create product. Please try again.";
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
            Maak Product Aan
          </Typography>

          {/* Status Messages */}
          {submitStatus && (
            <Typography
              variant="body2"
              align="center"
              sx={{ color: "green" }}
            >
              {submitStatus}
            </Typography>
          )}
          {apiError && (
            <Typography
              variant="body2"
              align="center"
              sx={{ color: "red" }}
            >
              {apiError}
            </Typography>
          )}

          {Object.values(errors).length > 0 && (
            <Typography variant="body2" color="error" align="center">
              {Object.values(errors).join(" \n")}
            </Typography>
          )}

          {/* Product Name */}
          <TextField
            label="Product Naam"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            fullWidth
            required
            InputLabelProps={{ style: { color: "white" } }}
            InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
          />

          {/* Price */}
          <TextField
            label="Prijs (€)"
            name="price"
            type="number"
            value={formData.price}
            onChange={handleInputChange}
            fullWidth
            required
            InputLabelProps={{ style: { color: "white" } }}
            InputProps={{ style: { color: "white", backgroundColor: "#333" } }}
          />

          {/* Image File */}
          <Box>
            <label
              htmlFor="product-image"
              style={{
                color: "white",
                display: "block",
                marginBottom: "8px",
                fontSize: "16px",
              }}
            >
              Product Foto
            </label>
            <input
              id="product-image"
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

          {/* Availability */}
          <FormControlLabel
            control={
              <Checkbox
                checked={formData.available}
                onChange={handleInputChange}
                name="available"
                sx={{
                  color: "white",
                  "&.Mui-checked": { color: "green" },
                }}
              />
            }
            label="Beschikbaar"
            sx={{ color: "white" }}
          />

          {/* Category */}
          <Select
            name="category"
            value={formData.category}
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
              Selecteer Categorie
            </MenuItem>
            {categories.map((category) => (
              <MenuItem
                key={category}
                value={category}
                sx={{
                  backgroundColor: "#333",
                  color: "white",
                  "&:hover": {
                    backgroundColor: "#555",
                  },
                }}
              >
                {category}
              </MenuItem>
            ))}
          </Select>

          {/* Submit Button */}
          <Button
            type="submit"
            variant="contained"
            fullWidth
            sx={{
              backgroundColor: "white",
              color: "black",
              "&:hover": {
                backgroundColor: "gray",
                color: "white",
              },
            }}
          >
            Maak Product
          </Button>
        </Box>
      </Container>
    </main>
  );
};

export default CreateProduct;
