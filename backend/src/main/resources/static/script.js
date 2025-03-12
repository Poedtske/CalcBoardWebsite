let http = "http://10.2.160.151:8081";

// Open the login popup
function openLoginPopup() {
    console.log("login login")
    document.getElementById("loginPopup").style.display = "flex";
}

// Close the login popup
function closeLoginPopup() {
    document.getElementById("loginPopup").style.display = "none";
}

// Toggle between Login and Register forms
function toggleAuthForms() {
    const loginForm = document.getElementById("loginFormContainer");
    const registerForm = document.getElementById("registerFormContainer");

    if (loginForm.style.display === "none") {
        loginForm.style.display = "block";
        registerForm.style.display = "none";
    } else {
        loginForm.style.display = "none";
        registerForm.style.display = "block";
    }
}

// Handle Register Form Submission
async function handleRegisterSubmit(event) {
    event.preventDefault();

    const firstName = document.getElementById("regFirstName").value;
    const lastName = document.getElementById("regLastName").value;
    const email = document.getElementById("regUsername").value;
    const password = document.getElementById("regPassword").value;
    const confirmPassword = document.getElementById("regConfirmPassword").value;

    if (password !== confirmPassword) {
        document.getElementById("registerMessage").textContent = "Passwords do not match!";
        return;
    }

    const response = await fetch("/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ firstName, lastName, email, password })
    });

    const result = await response.json();
    document.getElementById("registerMessage").textContent = result.message || "Registration successful!";
}

// Handle login form submission
function handleLoginSubmit(event) {
    event.preventDefault();

    const email = document.getElementById("username").value;
    const password = document.getElementById("password").value;


    fetch( `${http}/login`, {
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

        // Hide the login popup if user is logged in
        closeLoginPopup();
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

function toggleDescription(buttonElement) {
    let container = buttonElement.closest('.map-container');  // Get the nearest container
    if (!container) {
        console.error("Container not found!");
        return;
    }

    let mapId = container.getAttribute('data-map-id');  // Get the map ID
    console.log("Editing description for Map ID:", mapId);

    let descriptionText = container.querySelector('.description-text');
    let descriptionTextarea = container.querySelector('.description-textarea');
    let saveButton = container.querySelector('.save-description-btn');

    if (!descriptionText || !descriptionTextarea || !saveButton) {
        console.warn("Description elements not found in container.");
        return;
    }

    if (descriptionText.style.display === "none") {
        descriptionText.style.display = "block";
        descriptionTextarea.style.display = "none";
        saveButton.style.display = "none";
    } else {
        descriptionText.style.display = "none";
        descriptionTextarea.style.display = "block";
        saveButton.style.display = "inline-block";
    }
}

function confirmDownload(button) {
    if (!localStorage.getItem("token")) {
        alert("You need to log in to download maps.");
        return;
    }

    let mapName = button.getAttribute("data-mapname");
    alert("Confirm download for: " + mapName);

    const userConfirmed = confirm(`Are you sure you want to download the map: ${mapName}?`);

    if (userConfirmed) {
        downloadMap(mapName);
    }
}

function downloadMap(mapName) {
    const token = localStorage.getItem("token");

    fetch(`${http}/maps/download/${mapName}`, {

        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Download failed. Please check your login status.");
            }
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = `${mapName}.zip`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            alert("Download started!");
        })
        .catch(error => {
            console.error("Error downloading the map:", error);
            alert("Download failed.");
        });
}


// Function to update the description
function saveDescription(buttonElement) {
    let container = buttonElement.closest('.map-container');
    if (!container) {
        console.error("Container not found!");
        return;
    }

    let mapId = container.getAttribute('data-map-id');  // Get the map ID
    let descriptionTextarea = container.querySelector('.description-textarea');
    let descriptionText = container.querySelector('.description-text');
    let newDescription = descriptionTextarea.value;

    console.log("Saving description for Map ID:", mapId, "New description:", newDescription);

    if (!mapId || !newDescription) {
        console.warn("Invalid map ID or description.");
        return;
    }

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
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error("Failed to update description");
            }
        })
        .then(data => {
            alert("Description updated successfully!");
            descriptionText.textContent = newDescription;
            toggleDescription(buttonElement);
        })
        .catch(error => {
            console.error('Error updating description:', error);
            alert("Failed to update description.");
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
    } else {
        alert("Failed to update privacy");
    }
}


// Attach event listener for Register Form submission
window.onload = function() {
    updateLoginLogoutButton();

    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.onsubmit = handleLoginSubmit;
    }

    const registerForm = document.getElementById("registerForm");
    if (registerForm) {
        registerForm.onsubmit = handleRegisterSubmit;
    }
};
