import React, { useEffect } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import NavBar from './NavBar';
import Footer from './Footer';

// Mocking authentication state
const isAuthenticated = true; // Replace with actual auth logic
const isAdmin = true;         // Replace with actual role logic

export default function AppLayout() {
  const location = useLocation();

  useEffect(() => {
    // Dynamically load custom styles and scripts based on route
    const stylePath = `/css/${location.pathname.slice(1) || 'main'}.css`;
    const scriptPath = `/js/${location.pathname.slice(1) || 'main'}.js`;

    const styleLink = document.createElement("link");
    styleLink.rel = "stylesheet";
    styleLink.href = stylePath;
    document.head.appendChild(styleLink);

    const script = document.createElement("script");
    script.type = "module";
    script.src = scriptPath;
    document.body.appendChild(script);

    return () => {
      // Cleanup added elements when the component unmounts
      document.head.removeChild(styleLink);
      document.body.removeChild(script);
    };
  }, [location]);

  return (
    <div>
      {/* External Links */}
      <link
        rel="stylesheet"
        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"
        integrity="sha512-iecdLmaskl7CVkqkXNQ/ZH/XLlvWZOJyj7Yy7tcenmpD1ypASozpmT/E0iPtmFIB46ZmdtAc9eNBvH0H/ZpiBw=="
        crossOrigin="anonymous"
        referrerPolicy="no-referrer"
      />
      <link
        href="https://fonts.googleapis.com/css2?family=Quicksand&display=swap"
        rel="stylesheet"
      />

      {/* Page Title */}
      <title>{location.pathname === '/' ? 'Blog' : `Blog - ${location.pathname.slice(1)}`}</title>

      {/* Main CSS - Load conditional admin CSS */}
      <link rel="stylesheet" href="/css/mainFanfare.css" />
      {isAuthenticated && isAdmin && <link rel="stylesheet" href="/css/mainAdmin.css" />}

      <NavBar />

      <main>
        {/* Flash Messages */}
        {location.state?.flashMessage && (
          <div className="flash-success">
            {location.state.flashMessage}
          </div>
        )}

        {/* Render Page Content */}
        <Outlet />
      </main>

      <Footer />
    </div>
  );
}
