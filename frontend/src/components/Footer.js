import React from 'react';

export default function Footer() {
  const sponsor = {
    rank: 1,
    url: "https://sponsor-website.com",
    logo: "/path/to/sponsor/logo.png",
  };

  return (
    <footer>
      <div id="mail">k.f.demoedigevrienden@gmail.com</div>
      <nav>
        <a href="https://www.facebook.com/groups/284633908275549" target="_blank" rel="noopener noreferrer">
          <i className="fab fa-facebook"></i>
        </a>
        <a href="https://www.instagram.com/instaporkestdmv/" target="_blank" rel="noopener noreferrer">
          <i className="fab fa-instagram"></i>
        </a>
      </nav>
    </footer>
  );
}
