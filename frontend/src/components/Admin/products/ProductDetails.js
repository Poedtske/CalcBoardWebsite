import React, { useState, useEffect } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import {
  adminGetProductById,
  getProductImg,
  deleteProductById,
} from "../../../services/ApiService"; // Replace with correct paths
import styles from "./ProductDetails.module.css"; // CSS module
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

const ProductDetails = () => {
  const { id } = useParams(); // Product ID from the route
  const navigate = useNavigate();

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Function to calculate the quantity of a specific product in the invoices
  const calculateQuantity = (invoices, invoiceId) => {
    let quantity = 0;
    invoices.forEach((invoice) => {
      if (invoice.id === invoiceId) {
        quantity++;
      }
    });
    return quantity;
  };

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const productData = await adminGetProductById(id); // Fetch the product details
        const imageResponse = await getProductImg(id); // Fetch product image

        // Calculate the quantity based on the number of times this product appears in the invoices
        const totalQuantity = productData.invoices
          ? productData.invoices.reduce((sum, invoice) => sum + 1, 0) // Count how many times the product appears in the invoices
          : 0;

        // Calculate the sum of all invoice amounts (if needed)
        const totalAmount = productData.invoices
          ? productData.invoices.reduce((sum, invoice) => sum + invoice.amount, 0)
          : 0;

        const productWithImageAndQuantity = {
          ...productData,
          img: URL.createObjectURL(new Blob([imageResponse])), // Convert image response to blob
          quantity: totalQuantity, // Set quantity as the sum of the number of appearances in invoices
          totalAmount: totalAmount, // Set total amount from all invoices
        };

        setProduct(productWithImageAndQuantity);
        setLoading(false);
      } catch (err) {
        setError("Error fetching product details");
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  const handleDelete = async () => {
    try {
      await deleteProductById(id);
      alert("Product deleted successfully");
      navigate("/admin/products"); // Redirect to product list
    } catch (err) {
      console.error("Failed to delete product", err);
    }
  };

  // Function to filter unique invoices based on invoiceId
  const getUniqueInvoices = (invoices) => {
    const seenInvoiceIds = new Set();
    const uniqueInvoices = [];

    invoices.forEach((invoice) => {
      if (!seenInvoiceIds.has(invoice.id)) {
        seenInvoiceIds.add(invoice.id);
        uniqueInvoices.push(invoice);
      }
    });

    return uniqueInvoices;
  };

  if (loading) return <p>Loading product details...</p>;
  if (error) return <p>{error}</p>;

  return (
    <main>
      <Container maxWidth="md" className={styles.main}>
      <Box sx={{ display: "flex", justifyContent: "space-between", mb: 3 }}>
        <Typography variant="h4" gutterBottom>
          Product Details
        </Typography>
      </Box>

      {/* Product Image */}
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

      {/* Product Information */}
      <Box sx={{ mb: 3 }}>
        <Typography variant="h6">Product Informatie</Typography>
        <Typography variant="body1"><strong>Naam:</strong> {product.name}</Typography>
        <Typography variant="body1"><strong>Prijs (€):</strong> {product.price}</Typography>
        <Typography variant="body1"><strong>Beschikbaar:</strong> {product.available ? "Ja" : "Nee"}</Typography>
        <Typography variant="body1"><strong>Aantal in Manden:</strong> {product.quantity}</Typography>
        <Typography variant="body1"><strong>Categorie:</strong> {product.category}</Typography>
      </Box>

      {/* Actions */}
      <Box sx={{ display: "flex", justifyContent: "center", gap: 2, mb: 4 }}>
        {/* Navigate to edit page */}
        <Button
          variant="contained"
          component={Link}
          to={`/admin/products/update/${id}`} // Link to the edit page
          sx={{
            backgroundColor: "#fa6908", // Background color for the Edit button
            color: "white", // Text color
            "&:hover": {
              backgroundColor: "#e15d05", // Hover background color
            },
          }}
        >
          Aanpassen
        </Button>

        {/* Delete button */}
        <Button
          variant="contained" // Use 'contained' for solid background
          onClick={handleDelete}
          sx={{
            backgroundColor: "#dc3545", // Initial background color for the Delete button
            color: "white", // Text color for the Delete button
            "&:hover": {
              backgroundColor: "#c82333", // Darker red background color on hover
              borderColor: "#c82333", // Darker red border color on hover
            },
            borderColor: "#dc3545", // Initial border color for the Delete button
          }}
        >
          Verwijderen
        </Button>
      </Box>

      {/* Invoice Table */}
      <Typography variant="h6" gutterBottom>
        Rekeningen die dit product bevatten.
      </Typography>
      <TableContainer component={Paper} sx={{ backgroundColor: 'black', borderRadius: 2 }}>
        <Table sx={{ minWidth: 650 }}>
          <TableHead>
            <TableRow sx={{ backgroundColor: '#333' }}>
              <TableCell sx={{ color: 'white', borderColor: 'white' }}>Rekening ID</TableCell>
              <TableCell sx={{ color: 'white', borderColor: 'white' }}>Gebruiker</TableCell>
              <TableCell sx={{ color: 'white', borderColor: 'white' }}>Aantal</TableCell>
              <TableCell sx={{ color: 'white', borderColor: 'white' }}>Betaald</TableCell>
              <TableCell sx={{ color: 'white', borderColor: 'white' }}>Bevestigd</TableCell>
              <TableCell sx={{ color: 'white', borderColor: 'white' }}>Afgesloten</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {product.invoices && product.invoices.length > 0 ? (
              getUniqueInvoices(product.invoices).map((invoice) => (
                <TableRow key={invoice.id} sx={{ '&:nth-of-type(even)': { backgroundColor: '#444' } }}>
                  <TableCell sx={{ color: 'white', borderColor: 'white' }}>{invoice.id}</TableCell>
                  <TableCell sx={{ color: 'white', borderColor: 'white' }}>
                    {invoice.user.firstName + ' ' + invoice.user.lastName || "N/A"} <br />
                    {invoice.user?.email || "N/A"}
                  </TableCell>
                  <TableCell sx={{ color: 'white', borderColor: 'white' }}>
                    {calculateQuantity(product.invoices, invoice.id) || 'error'}
                  </TableCell>
                  <TableCell sx={{ color: 'white', borderColor: 'white' }}>
                    {invoice.paid ? "Ja" : "Nee"}
                  </TableCell>
                  <TableCell sx={{ color: 'white', borderColor: 'white' }}>
                    {invoice.confirmed ? "Ja" : "Nee"}
                  </TableCell>
                  <TableCell sx={{ color: 'white', borderColor: 'white' }}>
                    {invoice.closed ? "Ja" : "Nee"}
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={5} align="center" sx={{ color: 'white' }}>
                  Geen rekeningen bevatten dit product
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

    </Container>
    </main>
  );
};

export default ProductDetails;
