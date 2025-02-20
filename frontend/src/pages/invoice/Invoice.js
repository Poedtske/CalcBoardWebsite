import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getCart, getProductById, pay } from "../../services/ApiService"; // Import the cart fetching function and pay function
import { clearProductCart, getProductCart } from "../../services/ProductCartService"; // Import the service to get products from session
import styles from "./Invoice.module.css"; // Import CSS module for styling

const Invoice = () => {
  const navigate = useNavigate();

  const [cart, setCart] = useState({
    products: [],
    tickets: [],
  });
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchCartData = async () => {
      try {
        // Get the products saved in sessionStorage
        const productList = getProductCart();
        const groupedProducts = groupProductsById(productList.products);

        // Fetch product details for each unique product
        const productDetails = await Promise.all(
          Object.keys(groupedProducts).map(async (productId) => {
            const product = await getProductById(productId);
            return {
              ...product,
              quantity: groupedProducts[productId], // Include quantity from session
            };
          })
        );

        // Fetch ticket data from API
        const cartData = await getCart();
        setCart({
          products: productDetails,
          tickets: cartData.tickets,
        });
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
      acc[product.id] = acc[product.id] ? acc[product.id] + 1 : 1;
      return acc;
    }, {});
  };

  // Function to calculate total price of cart items
  const calculateTotalPrice = () => {
    let total = 0;

    // Add prices of products
    if (cart.products != null && cart.products.length !== 0) {
      for (let product of cart.products) {
        total += product.price * product.quantity; // Multiply price by quantity
      }
    }

    // Add prices of tickets
    if (cart.tickets != null && cart.tickets.length !== 0) {
      for (let ticket of cart.tickets) {
        total += ticket.price;
      }
    }

    return total;
  };

  const handlePay = async () => {
    try {
        // Retrieve products saved in the session storage
        const productList = getProductCart();
        const groupedProducts = groupProductsById(productList.products);
        
        // Prepare data to be sent with the payment request
        const paymentData = Object.keys(groupedProducts).map((productId) => ({
            productId: parseInt(productId, 10), // Ensure the ID is parsed as an integer
            quantity: groupedProducts[productId],
        }));

        // Pass the payment data to the `pay` function
        await pay(paymentData);
        console.log(paymentData);
        alert("Betaling geslaagd!"); // Optional: Show confirmation to the user
        clearProductCart();
        navigate("/"); // Navigate back to home after successful payment
    } catch (err) {
        console.error("Payment failed:", err);
        alert("Betaling gefaald.");
    }
};

  const totalPrice = calculateTotalPrice();

  if (loading) {
    return <p>Loading your invoice...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <main className={styles.invoice_page}>
      <div className={styles.invoice_content}>
        <h1>Factuur</h1>

        {/* Display products in the invoice */}
        {cart.products.length > 0 && (
          <div className={styles.products_list}>
            <h3>Producten</h3>
            <ul>
              {cart.products.map((product) => (
                <li key={product.id} className={styles.product_item}>
                  <span>{product.name}</span> - €{(product.price * product.quantity).toFixed(2)} (x{product.quantity})
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* Display tickets in the invoice */}
        {cart.tickets.length > 0 && (
          <div className={styles.tickets_list}>
            <h3>Tickets</h3>
            <ul>
              {cart.tickets.map((ticket) => (
                <li key={ticket.id} className={styles.ticket_item}>
                  <span>{ticket.name}</span> - €{ticket.price.toFixed(2)}
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* Display Total Amount */}
        <p className={styles.total_amount}>
          <strong>Totaal Bedrag: €{totalPrice.toFixed(2)}</strong>
        </p>

        {/* Big Image */}
        <div className={styles.image_container}>
          <img src="/images/payconnicQrcode.png" alt="Invoice" className={styles.invoice_image} />
        </div>

        {/* Invoice Text */}
        <div className={styles.invoice_text}>
          <p>
            Rekening nummer: <b>12544 454 751244 51044</b>
          </p>
          <p>
            Gelieve u email in de beschrijving van u transactie te zetten. Druk op de betaal knop wanneer u betaald heeft, hierna zullen wij u een mail versturen om de betaling te bevestigen.
          </p>
          <p>
            Indien er vragen zijn omtrent u order, contacteer ons via <b>k.f.demoedigevrienden@gmail.com</b>.
          </p>
        </div>

        {/* Button to Handle Payment */}
        <div className={styles.button_container}>
          <button className={styles.pay_button} onClick={handlePay}>
            Ik heb Betaald
          </button>
        </div>
      </div>
    </main>
  );
};

export default Invoice;
