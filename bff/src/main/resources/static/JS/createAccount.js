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

const username = sessionStorage.getItem("username");
const userId = sessionStorage.getItem("userId");
if (!username || !userId) {
  window.location.href = "/index.html";
}

// Bank Account Creation JavaScript
document.addEventListener("DOMContentLoaded", function () {
  const createBankAccountForm = document.getElementById(
    "createBankAccountForm"
  );

  createBankAccountForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    // Get userId from session storage or localStorage
    let userId = sessionStorage.getItem("userId");
    if (!userId) {
      userId = localStorage.getItem("userId");
    }

    if (!userId) {
      showFlash("User session not found. Please login again.", "danger");
      setTimeout(() => {
        window.location.href = "/index.html";
      }, 2000);
      return;
    }

    const accountType = document.getElementById("accountType").value;
    console.log("Account Type:", accountType);
    const amount = parseFloat(document.getElementById("amount").value);
    console.log("Initial Amount:", amount);

    // Frontend validation
    if (!accountType) {
      showFlash("Please select an account type.", "warning");
      return;
    }

    if (!amount || amount < 25) {
      showFlash("Minimum initial deposit is $25.00", "warning");
      return;
    }

    const formData = {
      userId: userId,
      accountType: accountType,
      initialBalance: amount,
    };

    try {
      const res = await fetch("accounts/create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      const result = await res.json();

      if (res.ok) {
        showFlash(
          `Bank account created successfully! Account Number: ${
            result.accountNumber || "Generated"
          }`,
          "success"
        );
        setTimeout(() => {
          window.location.href = "/home.html";
        }, 2000);
      } else {
        showFlash(
          result.message || "Failed to create bank account. Please try again.",
          "danger"
        );
      }
    } catch (error) {
      showFlash("Network error. Please try again.", "danger");
    }
  });
});
