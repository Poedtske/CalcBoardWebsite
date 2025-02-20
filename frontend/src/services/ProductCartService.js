import axios from "axios";

//handles the productCart

const PRODUCT_CART_KEY = "product_cart"; // Key for the product cart

// Get the product cart from sessionStorage or return a default structure
export const getProductCart = () => {
  const savedCart = JSON.parse(sessionStorage.getItem(PRODUCT_CART_KEY)) || { products: [] };
  console.log("Cart fetched from sessionStorage:", savedCart);
  return savedCart;
};

// Save the product cart to sessionStorage
export const saveProductCart = (cart) => {
  sessionStorage.setItem(PRODUCT_CART_KEY, JSON.stringify(cart));
  console.log("Cart saved to sessionStorage:", cart);
};

// Add a product to the cart
export const addProductToCart = (product) => {
  const cart = getProductCart(); // Get the current cart
  cart.products.push(product); // Add the new product
  saveProductCart(cart); // Save the updated cart
};

// Remove a product from the cart (removes only a single instance)
export const removeProductFromCart = (productId) => {
  const cart = getProductCart(); // Get the current cart
  console.log("Removing product with ID:", productId);
  
  // Log the cart state before filtering
  console.log("Cart before removing product:", cart.products);
  
  cart.products = cart.products.filter((product) => parseInt(product.id, 10) !== productId); // Remove product by ID

  // Log the cart state after filtering
  console.log("Cart after removing product:", cart.products);

  saveProductCart(cart); // Save the updated cart
};

// Remove all instances of a product from the cart
export const removeAllProductsFromCart = (productId) => {
  const cart = getProductCart(); // Get the current cart
  console.log("Removing all instances of product with ID:", productId);

  // Log the cart state before filtering
  console.log("Cart before removing product:", cart.products);

  // Remove all instances of the product by filtering it out
  cart.products = cart.products.filter((product) => parseInt(product.id, 10) !== productId);

  // Log the cart state after filtering
  console.log("Cart after removing all instances of product:", cart.products);

  saveProductCart(cart); // Save the updated cart
  return cart.products; // Return the updated list of products
};
// Clear the entire product cart
export const clearProductCart = () => {
  sessionStorage.removeItem(PRODUCT_CART_KEY); // Remove the product cart from sessionStorage
  console.log("Product cart cleared from sessionStorage");
};

// Prepare and send the product cart to the backend
export const sendProductCartToBackend = async (backendUrl) => {
  const cart = getProductCart(); // Get the current cart
  const invoiceDto = {
    productList: cart.products, // Prepare product data for the backend
  };

  try {
    const response = await axios.post(backendUrl, invoiceDto); // Send data to the backend
    console.log("Invoice sent successfully:", response.data);
    clearProductCart(); // Clear the cart after sending the data
  } catch (error) {
    console.error("Error sending invoice:", error);
    throw error; // Re-throw the error after logging it
  }
};

// Get the count of a specific product in the cart
export const getProductCountInCart = (productId) => {
  const cart = getProductCart(); // Get the current cart
  const productInCart = cart.products.filter((product) => product.id === productId); // Find matching products
  return productInCart.length; // Return the count of that product in the cart
};
