function showFlash(message, type = "info") {
  const flashContainer = document.getElementById("flashContainer");

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

function storeFlashMessage(message, type = "info") {
  localStorage.setItem("flashMessage", JSON.stringify({ message, type }));
}
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

document.addEventListener("DOMContentLoaded", displayStoredFlashMessage);

const loginTab = document.getElementById("loginTab");
const registerTab = document.getElementById("registerTab");
const loginForm = document.getElementById("loginForm");
const registerForm = document.getElementById("registerForm");

loginTab.onclick = function (e) {
  e.preventDefault();
  loginTab.classList.add("active");
  registerTab.classList.remove("active");
  loginForm.classList.remove("d-none");
  registerForm.classList.add("d-none");

  loginForm.style.animation = "slideIn 0.4s ease-out";
  document.getElementById("registerUsername").value = "";
  document.getElementById("registerEmail").value = "";
  document.getElementById("registerFirstName").value = "";
  document.getElementById("registerLastName").value = "";
  document.getElementById("registerPassword").value = "";
};

registerTab.onclick = function (e) {
  e.preventDefault();
  registerTab.classList.add("active");
  loginTab.classList.remove("active");
  registerForm.classList.remove("d-none");
  loginForm.classList.add("d-none");

  registerForm.style.animation = "slideIn 0.4s ease-out";
  document.getElementById("loginUsername").value = "";
  document.getElementById("loginPassword").value = "";
};

function isValidEmail(email) {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return emailRegex.test(email);
}

loginForm.addEventListener("submit", async function (e) {
  e.preventDefault();
  const username = document.getElementById("loginUsername").value.trim();
  const password = document.getElementById("loginPassword").value;

  if (!username || !password) {
    showFlash("Please fill in both fields.", "warning");
    return;
  }
  if (username.length < 4) {
    showFlash("Username must be at least 4 characters.", "warning");
    return;
  }
  if (password.length < 8) {
    showFlash("Password must be greater than 8 characters.", "warning");
    return;
  }

  const data = { username, password };
  try {
    const res = await fetch("/api/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    const result = await res.json();

    if (res.ok) {
      storeFlashMessage("Login successful!", "success");
      sessionStorage.setItem("username", result.username);
      sessionStorage.setItem("userId", result.userId);
      window.location.href = "/home.html ";
    } else {
      showFlash(result.message || "Login failed. Please try again.", "danger");
    }
  } catch (error) {
    showFlash("Invalid credentials", "danger");
  }
});

registerForm.addEventListener("submit", async function (e) {
  e.preventDefault();
  const username = document.getElementById("registerUsername").value.trim();
  const email = document.getElementById("registerEmail").value.trim();
  const firstName = document.getElementById("registerFirstName").value.trim();
  const lastName = document.getElementById("registerLastName").value.trim();
  const password = document.getElementById("registerPassword").value;

  if (!username || !email || !firstName || !lastName || !password) {
    showFlash("Please fill in all fields.", "warning");
    return;
  }
  if (username.length < 4) {
    showFlash("Username must be at least 4 characters.", "warning");
    return;
  }
  if (firstName.length < 4) {
    showFlash("First name must be at least 4 characters.", "warning");
    return;
  }
  if (lastName.length < 4) {
    showFlash("Last name must be at least 4 characters.", "warning");
    return;
  }
  if (password.length < 8) {
    showFlash("Password must be greater than 8 characters.", "warning");
    return;
  }
  if (!isValidEmail(email)) {
    showFlash("Invalid email address.", "warning");
    return;
  }

  const data = { username, email, firstName, lastName, password };
  try {
    const res = await fetch("/api/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
    const result = await res.json();
    if (res.ok) {
      showFlash("Registration successful! You can now login.", "success");
      loginTab.click();
    } else {
      showFlash(
        result.message || "Registration failed. Please try again.",
        "danger"
      );
    }
  } catch (error) {
    showFlash("Network error. Please try again.", "danger");
  }
});
