import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { removeAllProductsFromCart, getProductCart, clearProductCart } from "../../services/ProductCartService"; // Import the ProductCart service for product-specific functionality
import { removeTicketFromCart, clearCart, getCart, getProductById } from "../../services/ApiService"; // Import the API functions
import styles from "./Cart.module.css";
import { MdOutlineRemoveShoppingCart } from "react-icons/md";


const Cart = () => {
  const navigate = useNavigate();
  const [cart, setCart] = useState({
    products: [], // List of products in the cart
    tickets: [],  // List of tickets in the cart
  });
  const [loading, setLoading] = useState(true); // State for loading
  const [error, setError] = useState(null); // State for error handling

  useEffect(() => {
    const fetchCartData = async () => {
      try {
        const productList = getProductCart(); // Get products from sessionStorage using ProductCart service
        const groupedProducts = groupProductsById(productList.products); // Group products by ID and sum quantities

        // Fetch product details for each unique product
        const productDetails = await Promise.all(
          Object.keys(groupedProducts).map(async (productId) => {
            const product = await getProductById(productId); // Fetch product details from API
            return {
              ...product,
              quantity: groupedProducts[productId], // Add quantity information
            };
          })
        );

        const cartData = await getCart();  // Fetch tickets via the API service
        setCart({ products: productDetails, tickets: cartData.tickets }); // Update cart state
        setLoading(false);
      } catch (err) {
        console.error("Error fetching cart data:", err);
        setError("Failed to load cart data. Please try again later.");
        setLoading(false);
      }
    };

    fetchCartData();
  }, []);

  // Helper function to group products by ID and sum the quantities
  const groupProductsById = (products) => {
    return products.reduce((acc, product) => {
      acc[product.id] = acc[product.id] ? acc[product.id] + 1 : 1; // Sum quantities
      return acc;
    }, {});
  };

  const handleRemoveProduct = async (productId) => {
    try {
      removeAllProductsFromCart(productId); // Call the ProductCart service to remove the product
      setCart((prevCart) => ({
        ...prevCart,
        products: prevCart.products.filter((product) => product.id !== productId), // Remove locally from state
      }));
    } catch (err) {
      console.error("Error removing product:", err);
      setError("Failed to remove product. Please try again.");
    }
  };

  const handleRemoveTicket = async (ticketId) => {
    try {
      await removeTicketFromCart(ticketId); // Call the API to remove the ticket
      setCart((prevCart) => ({
        ...prevCart,
        tickets: prevCart.tickets.filter((ticket) => ticket.id !== ticketId), // Remove locally from state
      }));
    } catch (err) {
      console.error("Error removing ticket:", err);
      setError("Failed to remove ticket. Please try again.");
    }
  };

  const handleClearCart = async () => {
    try {
      await clearCart(); // Clear the entire cart via API (products and tickets)
      setCart({ products: [], tickets: [] }); // Clear cart state locally
      clearProductCart();
    } catch (err) {
      console.error("Error clearing cart:", err);
      setError("Failed to clear cart. Please try again.");
    }
  };

  const handleNavigateToInvoice = () => {
    navigate("/invoice"); // Navigate to the invoice page
  };

  // Calculate the total price of all products and tickets in the cart
  const calculateTotalPrice = () => {
    let total = 0;

    if (cart.products != null && cart.products.length !== 0) {
      for (let product of cart.products) {
        if (product.price != null && !isNaN(product.price)) {
          total += product.price * product.quantity; // Multiply price by quantity
        }
      }
    }

    if (cart.tickets != null && cart.tickets.length !== 0) {
      for (let ticket of cart.tickets) {
        if (ticket.price != null && !isNaN(ticket.price)) {
          total += ticket.price; // Add price of ticket
        }
      }
    }

    return total;
  };

  const totalPrice = calculateTotalPrice();

  const isCartEmpty =
    (!cart.products || cart.products.length === 0) &&
    (!cart.tickets || cart.tickets.length === 0);

  if (loading) {
    return <p>Loading your cart...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <main>
      <div className={styles.cart_page}>
        {/* Conditionally display buttons only if the cart is not empty */}
        {!isCartEmpty && (
          <div className={styles.cart_buttons}>
            <button className={styles.pay_button} onClick={handleNavigateToInvoice}>
              Betalen
            </button>
            <button className={styles.clear_button} onClick={handleClearCart}>
              Kar leegmaken
            </button>
          </div>
        )}

        <h2>Uw Kar</h2>

        {/* Display Products in the Cart */}
        {cart.products && cart.products.length > 0 && (
          <div className={styles.cart_section}>
            <h3>Producten</h3>
            <ul>
              {cart.products.map((product) => (
                <li key={product.id} className={styles.cart_item}>
                  <span>{product.name}</span> - €{(product.price && !isNaN(product.price) ? product.price.toFixed(2) : "0.00")} x {product.quantity}
                  <button
                    className={styles.remove_button}
                    onClick={() => handleRemoveProduct(product.id)}
                  >
                    <MdOutlineRemoveShoppingCart style={{ fontSize: "24px"}}/>
                  </button>
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* Display Tickets in the Cart */}
        {cart.tickets && cart.tickets.length > 0 && (
          <div className={styles.cart_section}>
            <h3>Tickets</h3>
            <ul>
              {cart.tickets.map((ticket) => (
                <li key={ticket.id} className={styles.cart_item}>
                  <span>{ticket.name}</span> - €{(ticket.price && !isNaN(ticket.price) ? ticket.price.toFixed(2) : "0.00")}
                  <button
                    className={styles.remove_button}
                    onClick={() => handleRemoveTicket(ticket.id)}
                  >
                    Remove
                  </button>
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* If cart is empty */}
        {isCartEmpty && <p>Your cart is empty</p>}

        {/* Display the total price */}
        {!isCartEmpty && (
          <div className={styles.total_price}>
            <h3>Total: €{totalPrice.toFixed(2)}</h3>
          </div>
        )}
      </div>
    </main>
  );
};

export default Cart;
