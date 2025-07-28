// Flash message functions (shared across pages)
function showFlash(message, type = "info") {
  const flashContainer = document.getElementById("flashContainer");
  if (!flashContainer) return;

  const alertDiv = document.createElement("div");
  alertDiv.className = `alert alert-${type} alert-dismissible fade show shadow-sm`;
  alertDiv.style.minWidth = "300px";
  alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;

  flashContainer.appendChild(alertDiv);

  // Auto-dismiss after 5 seconds
  setTimeout(() => {
    if (alertDiv && alertDiv.parentNode) {
      alertDiv.classList.remove("show");
      setTimeout(() => {
        if (alertDiv.parentNode) {
          alertDiv.parentNode.removeChild(alertDiv);
        }
      }, 150);
    }
  }, 5000);
}

// Function to display stored flash messages
function displayStoredFlashMessage() {
  const flashContainer = document.getElementById("flashContainer");
  if (!flashContainer) return;

  const storedFlash = localStorage.getItem("flashMessage");
  if (storedFlash) {
    const { message, type } = JSON.parse(storedFlash);
    showFlash(message, type);
    localStorage.removeItem("flashMessage");
  }
}
const username = sessionStorage.getItem("username");
const userId = sessionStorage.getItem("userId");
if (!username || !userId) {
  window.location.href = "/index.html";
}

// Homepage JavaScript functionality
document.addEventListener("DOMContentLoaded", function () {
  // Display any stored flash messages first
  displayStoredFlashMessage();

  // Show username in navbar if available
  const navbarUsername = document.getElementById("navbarUsername");
  let username = sessionStorage.getItem("username");
  if (!username) {
    // fallback to localStorage if not in sessionStorage
    username = localStorage.getItem("username");
  }
  if (navbarUsername && username) {
    navbarUsername.textContent = `| ${username}`;
  }

  const dashboardBtn = document.getElementById("dashboardBtn");
  const createAccountBtn = document.getElementById("createAccountBtn");
  const transferBtn = document.getElementById("transferBtn");
  const logoutBtn = document.getElementById("logoutBtn");

  // Dashboard button click handler
  dashboardBtn.addEventListener("click", function () {
    window.location.href = "/dashboard.html";
  });

  // Transfer button click handler
  transferBtn.addEventListener("click", function () {
    // Check if user is logged in
    const isLoggedIn = checkUserSession();

    if (isLoggedIn) {
      window.location.href = "/transfer.html";
    } else {
      // Redirect to login page
      window.location.href = "/index.html";
    }
  });

  // Create bank account button click handler
  createAccountBtn.addEventListener("click", function () {
    // Redirect to bank account creation page
    window.location.href = "/createAccount.html";
  });

  // Logout button click handler
  logoutBtn.addEventListener("click", function () {
    // Clear any stored tokens or session data
    localStorage.removeItem("userToken");
    sessionStorage.clear();

    // Show logout message and redirect
    localStorage.setItem(
      "flashMessage",
      JSON.stringify({
        message: "You have been logged out successfully.",
        type: "info",
      })
    );

    window.location.href = "/index.html";
  });

  // Simple session check function (placeholder)
  function checkUserSession() {
    // You can implement proper session/token validation here
    // For now, check if there's a token in localStorage or sessionStorage
    return localStorage.getItem("userToken") !== null;
  }
});

// Handle URL hash to show register tab if coming from create account button
window.addEventListener("load", function () {
  if (
    window.location.hash === "#register" &&
    window.location.pathname.includes("index.html")
  ) {
    // If on index.html and hash is #register, show register tab
    const registerTab = document.getElementById("registerTab");
    if (registerTab) {
      registerTab.click();
    }
  }
});
