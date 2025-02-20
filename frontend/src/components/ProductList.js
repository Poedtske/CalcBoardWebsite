import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { getProducts, getProductImg, getCategories } from "../services/ApiService"; // API service calls
import { Grid, Box, Typography, Button } from "@mui/material";
import styles from "../css/productList.module.css"; // CSS module for styling

const WebshopPage = () => {
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchCategoriesAndProducts = async () => {
      try {
        const categoryData = await getCategories(); // Fetch categories for the filter
        const productData = await getProducts(); // Fetch all products

        // Fetch images for each product
        const productsWithImages = await Promise.all(
          productData.map(async (product) => {
            const imageResponse = await getProductImg(product.id);
            return {
              ...product,
              img: URL.createObjectURL(new Blob([imageResponse])), // Convert image response to blob
            };
          })
        );

        setCategories(categoryData);
        setProducts(productsWithImages);
        setFilteredProducts(productsWithImages);
        setLoading(false);
      } catch (err) {
        setError("Error fetching products or categories");
        setLoading(false);
      }
    };

    fetchCategoriesAndProducts();
  }, []);

  // Filter products based on selected category
  const handleCategoryChange = (category) => {
    setSelectedCategory(category);

    if (category === "") {
      setFilteredProducts(products); // Show all products if no category selected
    } else {
      setFilteredProducts(products.filter((product) => product.category === category));
    }
  };

  if (loading) return <p>Loading products...</p>;
  if (error) return <p>{error}</p>;

  return (
    <main>
      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "flex-start", gap: 4, padding: 4 }}>
        {/* Sidebar for Category Filter */}
        <Box
          sx={{
            width: "20%",
            minWidth: "200px",
            paddingRight: 2,
            borderRight: "1px solid #ddd",
          }}
        >
          <Typography variant="h6">Filter op Categorie</Typography>
          <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
            <Button
              variant={selectedCategory === "" ? "contained" : "outlined"}
              onClick={() => handleCategoryChange("")}
              fullWidth
            >
              Alle Producten
            </Button>
            {categories.map((category) => (
              <Button
                key={category}
                variant={selectedCategory === category ? "contained" : "outlined"}
                onClick={() => handleCategoryChange(category)}
                fullWidth
              >
                {category}
              </Button>
            ))}
          </Box>
        </Box>

        {/* Product Grid */}
        <Box
          sx={{
            width: "80%",
            maxWidth: "1200px", // Max width for the product grid
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <Grid container spacing={4} sx={{ justifyContent: "center" }}>
            {filteredProducts.map((product) => (
              <Grid item xs={12} sm={6} md={4} key={product.id}>
                <Box sx={{ position: "relative", textAlign: "center" }}>
                  <Link to={`/products/${product.id}`}>
                    <img
                      src={product.img}
                      alt={product.name}
                      style={{
                        width: "100%",
                        height: "auto",
                        borderRadius: "8px",
                        cursor: "pointer",
                        objectFit: "cover",
                      }}
                    />
                    {/* Overlay for Unavailable Products */}
                    {!product.available && (
                      <div
                        style={{
                          position: "absolute",
                          top: 0,
                          left: 0,
                          width: "100%",
                          height: "100%",
                          backgroundColor: "rgba(0, 0, 0, 0.6)",
                          display: "flex",
                          justifyContent: "center",
                          alignItems: "center",
                          borderRadius: "8px",
                          zIndex: 1,
                        }}
                      >
                        <Typography
                          variant="h5"
                          sx={{
                            color: "red",
                            fontWeight: "bold",
                            textTransform: "uppercase",
                            textAlign: "center",
                            position: "absolute",
                            top: "50%",
                            left: "50%",
                            transform: "translate(-50%, -50%) rotate(+45deg)", // Rotate the text and center it
                            whiteSpace: "nowrap", // Prevent text wrapping
                          }}
                        >
                          Niet Beschikbaar
                        </Typography>
                      </div>
                    )}

                  </Link>
                  <Typography variant="body1" sx={{ mt: 2 }}>
                    {product.name}
                  </Typography>
                  <Typography variant="body2" sx={{ color: "gray" }}>
                    {product.price} €
                  </Typography>
                </Box>
              </Grid>
            ))}
          </Grid>
        </Box>
      </Box>
    </main>
  );
};

export default WebshopPage;
