import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  getCategories,
  getProductById,
  getProductImg,
  updateProductByIdWithoutImg,
  updateProductByIdWithImg,
} from "../../../services/ApiService"; // Replace with correct paths
import {
  Container,
  Typography,
  Box,
  TextField,
  FormControlLabel,
  Checkbox,
  Select,
  MenuItem,
  Button,
} from "@mui/material";

const UpdateProduct = () => {
  const { id } = useParams(); // Product ID from the route
  const navigate = useNavigate();

  const [imageFile, setImageFile] = useState(null);
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [categories, setCategories] = useState([]);
  const [formData, setFormData] = useState({
    name: "",
    price: "",
    available: true,
    category: "",
  });

  const [errors, setErrors] = useState({});
  const [submitStatus, setSubmitStatus] = useState("");
  const [apiError, setApiError] = useState("");

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const productData = await getProductById(id); // Fetch product details
        const imageResponse = await getProductImg(id); // Fetch product image
        const categoriesData = await getCategories();

        const productWithImage = {
          ...productData,
          img: URL.createObjectURL(new Blob([imageResponse])), // Convert image response to Blob
        };

        setProduct(productWithImage);

        // Populate formData with fetched product data
        setFormData({
          name: productData.name || "",
          price: productData.price || "",
          available: productData.available || false,
          category: productData.category || "",
        });

        setCategories(categoriesData);
        setLoading(false);
      } catch (err) {
        setError(err.message);
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  if (loading) return <p>Loading product details...</p>;
  if (error) return <p>{error}</p>;

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
    if (!formData.category) validationErrors.category = "Category is required.";

    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) return;

    // Prepare FormData
    const data = new FormData();
    data.append(
      "productDto",
      new Blob([JSON.stringify(formData)], { type: "application/json" })
    );
    if (imageFile) {
      data.append("imageFile", imageFile);
    }

    try {
      // API call to update product
      if(imageFile){
        await updateProductByIdWithImg(id, data);
      }else{
        await updateProductByIdWithoutImg(id, data);
      }
      
      setSubmitStatus("Product updated successfully!");
      setErrors({});
      setApiError("");
      navigate("/admin/products"); // Redirect to product list
    } catch (error) {
      console.error("Error updating product:", error);
      const errorMessage =
        error.response?.data?.message || "Failed to update product. Please try again.";
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
          Aanpassing Product
        </Typography>

        {/* Status Messages */}
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

        {/* Product Image */}
        {product?.img && (
          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              mb: 3,
            }}
          >
            <img
              src={product.img} // Use the image URL created from the Blob
              alt={product.name}
              style={{
                width: "300px",
                height: "300px",
                objectFit: "cover",
                borderRadius: "10px",
              }}
            />
          </Box>
        )}

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
          Pas Product Aan
        </Button>
      </Box>
    </Container>
    </main>
  );
};

export default UpdateProduct;
