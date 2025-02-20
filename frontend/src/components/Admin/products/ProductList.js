import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { getProducts, getProductImg, deleteProductById, manageAvailabilityProduct } from "../../../services/ApiService"; // Replace with correct API calls
import styles from "./ProductList.module.css"; // Import CSS module

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const productData = await getProducts(); // Fetch the products list

        // Map through products to fetch their images
        const productsWithImages = await Promise.all(
          productData.map(async (product) => {
            const imageResponse = await getProductImg(product.id); // Fetch product image
            return {
              ...product,
              img: URL.createObjectURL(new Blob([imageResponse])), // Convert image response to blob
            };
          })
        );

        setProducts(productsWithImages);
        setLoading(false);
      } catch (err) {
        setError("Error fetching products");
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  const handleDelete = async (id) => {
    if (window.confirm("Ben je zeker dat je dit product wilt verwijderen?")) {
      try {
        await deleteProductById(id);
        setProducts(products.filter((product) => product.id !== id));
        alert("Product successvol verwijderd");
      } catch (err) {
        alert("Kon product niet verwijderen");
        setError(err.message)
      }
    }
  };

  const handleAvailabilityProduct = async (id) => {
    try {
      await manageAvailabilityProduct(id);

      // Update the product's availability in the state
      setProducts((prevProducts) =>
        prevProducts.map((product) =>
          product.id === id ? { ...product, available: !product.available } : product
        )
      );
    } catch (err) {
      alert("Failed to change product availability");
    }
  };

  if (loading) return <p>Loading products...</p>;
  if (error) return <p>{error}</p>;

  return (
    <main>
      <h2 className={styles.title}>Product Lijst</h2>

      {/* Create Product Button */}
      <div className={styles.actions}>
        <button
          className={styles.createButton}
          onClick={() => navigate("/admin/products/create")}
        >
          Maak Nieuw Product
        </button>
      </div>

      <table className={styles.table}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Foto</th>
            <th>Naam</th>
            <th>Prijs (€)</th>
            <th>Beschikbaar</th> {/* New column */}
            <th>Acties</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr key={product.id}>
              <td>{product.id}</td>
              <td className={styles.imageCell}>
                <img
                  src={product.img}
                  alt={product.name}
                  className={styles.productImage}
                />
              </td>
              <td>{product.name}</td>
              <td>{product.price}</td>
              <td>
                {/* Availability button */}
                <button
                  className={`${styles.actionButton} ${
                    product.available ? styles.available : styles.unavailable
                  }`}
                  onClick={() => handleAvailabilityProduct(product.id)}
                >
                  {product.available ? "Beschikbaar" : "Niet Beschikbaar"}
                </button>
              </td>
              <td>
                <button
                  className={`${styles.actionButton}`}
                  onClick={() => navigate(`/admin/products/${product.id}`)}
                >
                  Bekijken
                </button>
                <button
                  className={`${styles.actionButton} ${styles.updateButton}`}
                  onClick={() => navigate(`/admin/products/update/${product.id}`)}
                >
                  Aanpassen
                </button>
                <button
                  className={`${styles.actionButton} ${styles.deleteButton}`}
                  onClick={() => handleDelete(product.id)}
                >
                  Verwijderen
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </main>
  );
};

export default ProductList;
