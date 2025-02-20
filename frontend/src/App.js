import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import AppLayout from './components/AppLayout';
import NavBar from './components/NavBar';
import AdminNavBar from './components/Admin/AdminNavBar';
import Footer from './components/Footer';
import Home from './pages/home/Home';
import Bestuur from './pages/fanfare/bestuur/Bestuur';
import Dirigent from './pages/fanfare/dirigent/Dirigent';
import Geschiedenis from './pages/fanfare/geschiedenis/Geschiedenis';
import Instrumenten from './pages/fanfare/instrumenten/Instrumenten';
import Jeugd from './pages/jeugd/Jeugd';
import Documenten from './pages/info/documenten/Documenten';
import Privacy from './pages/info/privacy/Privacy';
import Kalender from './pages/kalender/Kalender';
import Sponsors from './pages/sponsors/Sponsors';
import ShowEvent from './components/ShowEvent';
import Test from './pages/Test';
import AdminProductList from "./components/Admin/products/ProductList";
import { ProductListProvider } from "./context/ProductContext";
import AdminProductDetail from "./components/Admin/products/ProductDetails";
import './App.css';
import { EventProvider } from './context/EventContext';
import Registration from './components/Registration';
import RegistrationSuccess from './components/RegistrationSuccess';
import Login from './components/Login';
import LoginSuccessful from './components/LoginSuccessful';
import ProtectedRoute from './components/ProtectedRoute';
import { AuthProvider, useAuth } from './context/AuthContext';
import EventsList from './components/Admin/events/EventsList';
import CreateEvent from './components/Admin/events/CreateEvent';
import EventDetails from './components/Admin/events/EventDetails';
import { getUserRole, getAuthToken } from './utils/jwtUtils';
import EventLayout from './pages/eventLayout/EventLayout';
import Cart from './pages/cart/Cart';
import Invoice from './pages/invoice/Invoice';
import CreateProduct from './components/Admin/products/CreateProduct';
import ProductList from './components/ProductList';
import ProductDetails from './components/ProductDetail';
import AdminUpdateProduct from './components/Admin/products/UpdateProduct';
import UpdateEvent from './components/Admin/events/UpdateEvent';

function App() {
  const ADMIN_ROLE = "ADMIN";
  const USER_ROLE = "USER";
  const token = getAuthToken();
  const userRole = token ? getUserRole() : null;  // Only call getUserRole if token exists

  // Routes for authenticated users with role "USER"
  const userRoutes = (
    <>
      <Route path="/cart" element={<ProtectedRoute requiredRole={USER_ROLE}><Cart /></ProtectedRoute>} />
      <Route path="/invoice" element={<ProtectedRoute requiredRole={USER_ROLE}><Invoice /></ProtectedRoute>} />
    </>
  );

  // Routes for admin users with role "ADMIN"
  const adminRoutes = (
    <>
      <Route path="/admin/events" element={<ProtectedRoute requiredRole={ADMIN_ROLE}><EventsList /></ProtectedRoute>} />
      <Route path="/admin/events/create" element={<ProtectedRoute requiredRole={ADMIN_ROLE}><CreateEvent /></ProtectedRoute>} />
      <Route path="/admin/events/:id" element={<ProtectedRoute requiredRole={ADMIN_ROLE}><EventDetails /></ProtectedRoute>} />
      <Route path="/admin/events/update/:id" element={<ProtectedRoute requiredRole={ADMIN_ROLE}><UpdateEvent /></ProtectedRoute>} />
      <Route path="/admin/products" element={<ProtectedRoute requiredRole={ADMIN_ROLE}><AdminProductList /></ProtectedRoute>} />
      <Route path="/admin/products/create" element={<ProtectedRoute requiredRole={ADMIN_ROLE}><CreateProduct /></ProtectedRoute>} />
      <Route path="/admin/products/:id" element={<ProtectedRoute requiredRole={ADMIN_ROLE}><AdminProductDetail /></ProtectedRoute>} />
      <Route path="/admin/products/update/:id" element={<ProtectedRoute requiredRole={ADMIN_ROLE}><AdminUpdateProduct /></ProtectedRoute>} />
    </>
  );

  // Routes accessible by all users (public routes)
  const publicRoutes = (
    <>
      <Route path="/" element={<Home />} />
      <Route path="/events/:id/layout" element={<EventLayout />} />
      <Route path="/events/:id" element={<ShowEvent />} />
      <Route path="/fanfare/bestuur" element={<Bestuur />} />
      <Route path="/fanfare/dirigent" element={<Dirigent />} />
      <Route path="/fanfare/geschiedenis" element={<Geschiedenis />} />
      <Route path="/fanfare/instrumenten" element={<Instrumenten />} />
      <Route path="/jeugd" element={<Jeugd />} />
      <Route path="/info/documenten" element={<Documenten />} />
      <Route path="/info/privacy" element={<Privacy />} />
      <Route path="/kalender" element={<Kalender />} />
      <Route path="/sponsors" element={<Sponsors />} />
      <Route path="/register" element={<Registration />} />
      <Route path="/registrationSuccessful" element={<RegistrationSuccess />} />
      <Route path="/login" element={<Login />} />
      <Route path="/loginSuccessful" element={<LoginSuccessful />} />
      <Route path="/products" element={<ProductList />} />
      <Route path="/products/:id" element={<ProductDetails />} />
    </>
  );

  return (
    <>
      <AuthProvider>
        <ProductListProvider>
        <EventProvider>
          {token ? (
            userRole === ADMIN_ROLE ? (
              <>
                <AdminNavBar />
                <Routes>
                  {adminRoutes}
                  {publicRoutes}
                </Routes>
              </>
            ) : (
              <>
                <NavBar />
                <Routes>
                  {userRoutes}
                  {publicRoutes}
                </Routes>
              </>
            )
          ) : (
            <>
              <NavBar />
              <Routes>{publicRoutes}</Routes>
            </>
          )}
        </EventProvider>
        </ProductListProvider>
      </AuthProvider>
      <Footer />
    </>
  );
}

export default App;
