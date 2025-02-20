import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getProductById, getProductImg } from "../services/ApiService";
import { Container, Typography, Box, TextField, Button } from "@mui/material";
import { addProductToCart, getProductCountInCart } from "../services/ProductCartService";
import { isAuthenticated } from "../utils/jwtUtils"; // Assuming you have an auth context
import { MdAddShoppingCart } from "react-icons/md";

const ProductDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  // Directly use the result of isAuthenticated to check if the user is logged in
  const authenticated = isAuthenticated(); // should return a boolean directly
  
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [cartOptionsVisible, setCartOptionsVisible] = useState(false);
  const [productCount, setProductCount] = useState(0);

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const productData = await getProductById(id);
        const imageResponse = await getProductImg(id);

        const productWithImage = {
          ...productData,
          img: URL.createObjectURL(new Blob([imageResponse])),
        };

        setProduct(productWithImage);
        setLoading(false);
      } catch (err) {
        setError("Error fetching product details");
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  const updateProductCountInCart = () => {
    const count = getProductCountInCart(id);
    setProductCount(count);
  };

  const handleAddToCart = async (event) => {
    event.preventDefault();

    // Check if the user is authenticated
    if (!authenticated) {
      // Redirect to the login page if the user is not authenticated
      navigate("/login");
      return; // Exit the function early
    }

    // Add product to cart if the user is authenticated
    for (let i = 0; i < quantity; i++) {
      addProductToCart({ id, type: "product" });
    }

    updateProductCountInCart();
    setCartOptionsVisible(true);
  };

  useEffect(() => {
    if (product) {
      updateProductCountInCart();
    }
  }, [product]);

  if (loading) return <p>Loading product details...</p>;
  if (error) return <p>{error}</p>;

  return (
    <main>
      <Container maxWidth="md" sx={{ marginTop: 4 }}>
        <Button
          onClick={() => navigate("/products")}
          sx={{
            mb: 2,
            color: "white",
            backgroundColor: "#007bff",
            "&:hover": {
              backgroundColor: "#0056b3",
            },
          }}
        >
          &larr; Ga Terug
        </Button>

        <Box sx={{ display: "flex", justifyContent: "space-between", mb: 3 }}>
          <Typography variant="h4" gutterBottom>
            Product Details
          </Typography>
        </Box>

        {product.img && (
          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              mb: 3,
            }}
          >
            <img
              src={product.img}
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

        <Box sx={{ mb: 3 }}>
          <Typography variant="h6">Product Informatie</Typography>
          <Typography variant="body1">
            <strong>Naam:</strong> {product.name}
          </Typography>
          <Typography variant="body1">
            <strong>Prijs (€):</strong> {product.price}
          </Typography>
          <Typography variant="body1">
            <strong>Beschikbaar:</strong> {product.available ? "Yes" : "No"}
          </Typography>
          <Typography variant="body1">
            <strong>Categorie:</strong> {product.category}
          </Typography>
          <Typography variant="body1">
            <strong>In Kar:</strong> {productCount} keer
          </Typography>
        </Box>

        {product.available ? (
          <Box
            component="form"
            onSubmit={handleAddToCart}
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: 2,
              maxWidth: "400px",
              margin: "0 auto",
            }}
          >
            <TextField
              label="Aantal"
              type="number"
              value={quantity}
              onChange={(e) => setQuantity(Number(e.target.value))}
              inputProps={{ min: 1 }}
              required
              InputLabelProps={{
                style: { color: "white" },
              }}
              InputProps={{
                style: { color: "white", borderColor: "white" },
              }}
              sx={{
                "& .MuiOutlinedInput-root": {
                  "& fieldset": {
                    borderColor: "white",
                  },
                  "&:hover fieldset": {
                    borderColor: "white",
                  },
                  "&.Mui-focused fieldset": {
                    borderColor: "white",
                  },
                },
              }}
            />
            <Button
              variant="contained"
              type="submit"
              sx={{
                backgroundColor: "#28a745",
                color: "white",
                "&:hover": {
                  backgroundColor: "#218838",
                },
                padding: "10px 16px", // Adjust the size of the button
                display: "flex", // Make button size adjust to the content
                justifyContent: "center", // Center the icon inside the button
                alignItems: "center", // Ensure the icon is vertically centered
                minWidth: "60px", // Ensure the button has a reasonable minimum size
                borderRadius: "8px", // Optional, to add rounded corners to the button
              }}
            >
              <MdAddShoppingCart style={{ fontSize: "24px", marginRight: "8px" }} />
              Voeg aan kar toe
            </Button>
          </Box>
        ) : (
          <Typography
            variant="body1"
            color="error"
            sx={{
              textAlign: "center",
              marginTop: 2,
            }}
          >
            Product is momenteel niet beschikbaar en kan dus niet aan de kar toegevoegd worden
          </Typography>
        )}

        {cartOptionsVisible && (
          <Box
            sx={{
              marginTop: 4,
              display: "flex",
              flexDirection: "column",
              gap: 2,
              textAlign: "center",
            }}
          >
            <Typography variant="body1">
              Product successvol toegevoegd aan de kar!
            </Typography>
            <Box sx={{ display: "flex", justifyContent: "center", gap: 2 }}>
              <Button
                onClick={() => navigate("/products")}
                variant="outlined"
                sx={{
                  borderColor: "#007bff",
                  color: "#007bff",
                  "&:hover": {
                    borderColor: "#0056b3",
                    backgroundColor: "rgba(0, 123, 255, 0.1)",
                  },
                }}
              >
                Verder shoppen
              </Button>
              <Button
                onClick={() => navigate("/cart")}
                variant="contained"
                sx={{
                  backgroundColor: "#007bff",
                  color: "white",
                  "&:hover": {
                    backgroundColor: "#0056b3",
                  },
                }}
              >
                Bekijk je Kar
              </Button>
            </Box>
          </Box>
        )}
      </Container>
    </main>
  );
};

export default ProductDetails;
