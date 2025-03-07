// Open the login popup
function openLoginPopup() {
    document.getElementById("loginPopup").style.display = "flex";
}

// Close the login popup
function closeLoginPopup() {
    document.getElementById("loginPopup").style.display = "none";
}

// Handle login form submission
function handleLoginSubmit(event) {
    event.preventDefault();

    const email = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    fetch("http://localhost:8081/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    })
        .then(response => response.json())
        .then(data => {
            console.log("Login response:", data);
            if (data.token) {
                // Login successful
                localStorage.setItem('token', data.token); // Store token for session
                console.log("Token received and stored:", data.token);
                console.log(localStorage.getItem("token"));

                document.getElementById("message").innerText = "Login Successful!";
                setTimeout(() => {
                    closeLoginPopup();
                    updateLoginLogoutButton();
                }, 1000); // Close popup after 1 second
            } else {
                document.getElementById("message").innerText = "Invalid Credentials";
                console.log("Invalid credentials provided.");
            }
        })
        .catch(error => console.error("Error:", error));
}

// Update the Login/Logout button based on the user's login state
function updateLoginLogoutButton() {
    const token = localStorage.getItem('token'); // Check for token in local storage
    const loginLogoutButton = document.getElementById('loginLogoutButton');
    const userMapsLink = document.getElementById('userMapsLink');
    console.log("Current token in localStorage:", token);

    if (token) {
        // User is logged in, show "Logout"
        loginLogoutButton.innerText = 'Logout';
        loginLogoutButton.setAttribute('href', '#');
        loginLogoutButton.setAttribute('onclick', 'logout()');

        if (userMapsLink) userMapsLink.style.display = "inline";
        console.log("User is logged in. Token:", token);
    } else {
        // User is not logged in, show "Login"
        loginLogoutButton.innerText = 'Login';
        loginLogoutButton.setAttribute('href', '#');
        loginLogoutButton.setAttribute('onclick', 'openLoginPopup()');

        if (userMapsLink) userMapsLink.style.display = "none";
        console.log("User is not logged in.");
    }
}

// Handle logout functionality
function logout() {
    // Remove token and update UI
    localStorage.removeItem('token');
    console.log("User logged out. Token removed.");
    updateLoginLogoutButton();
    window.location.href = "/";
}

function toggleMenu() {
    document.getElementById("nav-menu").classList.toggle("show");
}

function toggleDescription(mapId) {
    let descriptionText = document.getElementById(`descriptionText-${mapId}`);
    let descriptionTextarea = document.getElementById(`descriptionTextarea-${mapId}`);

    console.log(`Current state - descriptionText:`, descriptionText, `descriptionTextarea:`, descriptionTextarea);

    // If the elements don't exist, create them dynamically
    if (!descriptionText || !descriptionTextarea) {
        console.warn(`Elements not found for mapId: ${mapId}, creating new ones.`);

        const container = document.getElementById(`mapContainer-${mapId}`); // Adjust if necessary
        if (!container) {
            console.error(`Container for mapId ${mapId} not found.`);
            return;
        }

        // Create description text (default visible)
        descriptionText = document.createElement("p");
        descriptionText.id = `descriptionText-${mapId}`;
        descriptionText.textContent = "Click to add a description...";
        descriptionText.style.display = "block";

        // Create textarea (default hidden)
        descriptionTextarea = document.createElement("textarea");
        descriptionTextarea.id = `descriptionTextarea-${mapId}`;
        descriptionTextarea.style.display = "none";
        descriptionTextarea.placeholder = "Enter a description...";

        // Append elements to the container
        container.appendChild(descriptionText);
        container.appendChild(descriptionTextarea);
    }

    // Toggle visibility
    if (descriptionText.style.display === "none") {
        descriptionText.style.display = "block";
        descriptionTextarea.style.display = "none";
    } else {
        descriptionText.style.display = "none";
        descriptionTextarea.style.display = "block";
    }
}

// Function to update the description
function updateDescription(mapId, newDescription) {
    const token = localStorage.getItem('token');

    if (!token) {
        alert("You must be logged in to update the description.");
        return;
    }

    fetch(`/api/secure/${mapId}/update-description`, {
        method: 'PUT',
        headers: {
            "Authorization": `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ description: newDescription })
    })
        .then(response => response.text())
        .then(data => {
            alert(data); // You can also handle success message or UI changes here
            console.log("Description updated:", data);
        })
        .catch(error => {
            console.error('Error updating description:', error);
        });
}

function navigateToMaps() {
    const token = localStorage.getItem('token');

    if (!token) {
        alert("You need to log in first!");
        window.location.href = "/"; // Redirect to home
        return;
    }

    fetch("/maps", {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (response.ok) {
                return response.text(); // Get HTML response
            } else {
                throw new Error("Unauthorized");
            }
        })
        .then(html => {
            document.body.innerHTML = html; // Replace body content with fetched page
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Access Denied. Please log in again.");
            localStorage.removeItem("token");
            window.location.href = "/"; // Redirect to login
        });
}



// Toggle map privacy
async function togglePrivacy(mapId) {
    const token = localStorage.getItem('token');
    console.log("Current token in localStorage:", token);
    if (!token) {
        alert("You must be logged in to change privacy settings.");
        return;
    }

    const response = await fetch(`/api/secure/${mapId}/toggle-privacy`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token // Send token with request
        }
    });

    if (response.ok) {
        location.reload();
    } else {
        alert("Failed to update privacy");
    }
}


// Call this function when the page loads to set the button state
window.onload = function() {
    console.log("Page loaded. Checking login state...");
    console.log("Script.js is geladen!");
    updateLoginLogoutButton(); // Update the button based on the current login state

    // Attach the login form submit event
    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.onsubmit = handleLoginSubmit;
    }

};
