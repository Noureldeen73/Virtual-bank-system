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

// Format currency
function formatCurrency(amount) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(amount);
}

// Show/hide loading spinner
function showLoadingSpinner() {
  const loadingSpinner = document.getElementById("loadingSpinner");
  if (loadingSpinner) {
    loadingSpinner.classList.remove("d-none");
  }
}

function hideLoadingSpinner() {
  const loadingSpinner = document.getElementById("loadingSpinner");
  if (loadingSpinner) {
    loadingSpinner.classList.add("d-none");
  }
}

// Load user accounts into dropdown
async function loadUserAccounts() {
  try {
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

    // Fetch user's accounts from dashboard endpoint (we can reuse this)
    const response = await fetch(`/bff/dashboard/${userId}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const dashboardData = await response.json();
    const accounts = dashboardData.accounts || [];

    // Update navbar username
    const navbarUsername = document.getElementById("navbarUsername");
    if (navbarUsername && dashboardData.username) {
      navbarUsername.textContent = `| ${dashboardData.username}`;
    }

    // Populate the from account dropdown
    const fromAccountSelect = document.getElementById("fromAccountId");
    if (fromAccountSelect) {
      // Clear existing options except the first one
      fromAccountSelect.innerHTML =
        '<option value="">Select your account</option>';

      accounts.forEach((account) => {
        const option = document.createElement("option");
        option.value = account.accountId;
        option.textContent = `${
          account.accountType
        } - ••••${account.accountNumber.slice(-4)} (${formatCurrency(
          account.balance
        )})`;
        option.dataset.balance = account.balance;
        option.dataset.accountNumber = account.accountNumber;
        option.dataset.accountType = account.accountType;
        fromAccountSelect.appendChild(option);
      });
    }

    if (accounts.length === 0) {
      showFlash(
        "No accounts found. Please create an account first.",
        "warning"
      );
      setTimeout(() => {
        window.location.href = "/createAccount.html";
      }, 3000);
    }
  } catch (error) {
    console.error("Error loading user accounts:", error);
    showFlash("Failed to load your accounts. Please try again.", "danger");
  }
}

// Update transfer summary
function updateTransferSummary() {
  const fromAccount = document.getElementById("fromAccountId");
  const toAccountId = document.getElementById("toAccountId").value.trim();
  const amount = parseFloat(document.getElementById("amount").value) || 0;

  const transferSummary = document.getElementById("transferSummary");
  const summaryFromAccount = document.getElementById("summaryFromAccount");
  const summaryToAccount = document.getElementById("summaryToAccount");
  const summaryAmount = document.getElementById("summaryAmount");

  // Show summary if all required fields are filled
  if (fromAccount.value && toAccountId && amount > 0) {
    const selectedOption = fromAccount.options[fromAccount.selectedIndex];
    const fromAccountText = selectedOption.textContent;

    summaryFromAccount.textContent = fromAccountText;
    summaryToAccount.textContent = `••••${toAccountId.slice(-4)}`;
    summaryAmount.textContent = formatCurrency(amount);

    transferSummary.classList.remove("d-none");
  } else {
    transferSummary.classList.add("d-none");
  }
}

// Validate transfer form
function validateTransferForm() {
  const fromAccountId = document.getElementById("fromAccountId").value;
  const toAccountId = document.getElementById("toAccountId").value.trim();
  const amount = parseFloat(document.getElementById("amount").value) || 0;

  // Reset previous error states
  document.querySelectorAll(".form-control.error").forEach((el) => {
    el.classList.remove("error");
  });
  document.querySelectorAll(".error-message").forEach((el) => {
    el.remove();
  });

  let isValid = true;

  // Validate from account
  if (!fromAccountId) {
    showFieldError(
      "fromAccountId",
      "Please select an account to transfer from"
    );
    isValid = false;
  }

  // Validate to account ID
  if (!toAccountId) {
    showFieldError("toAccountId", "Please enter the recipient's account ID");
    isValid = false;
  } else if (toAccountId === fromAccountId) {
    showFieldError("toAccountId", "Cannot transfer to the same account");
    isValid = false;
  }

  // Validate amount
  if (!amount || amount <= 0) {
    showFieldError("amount", "Please enter a valid transfer amount");
    isValid = false;
  } else if (amount < 0.01) {
    showFieldError("amount", "Minimum transfer amount is $0.01");
    isValid = false;
  } else if (fromAccountId) {
    // Check if amount exceeds available balance
    const selectedOption =
      document.getElementById("fromAccountId").options[
        document.getElementById("fromAccountId").selectedIndex
      ];
    const availableBalance = parseFloat(selectedOption.dataset.balance) || 0;

    if (amount > availableBalance) {
      showFieldError(
        "amount",
        `Insufficient funds. Available balance: ${formatCurrency(
          availableBalance
        )}`
      );
      isValid = false;
    }
  }

  return isValid;
}

// Show field error
function showFieldError(fieldId, message) {
  const field = document.getElementById(fieldId);
  if (field) {
    field.classList.add("error");

    const errorDiv = document.createElement("div");
    errorDiv.className = "error-message";
    errorDiv.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;

    field.parentNode.parentNode.appendChild(errorDiv);
  }
}

// Process transfer
async function processTransfer(transferData) {
  try {
    showLoadingSpinner();

    const response = await fetch("/transfer", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(transferData),
    });

    const result = await response.json();

    hideLoadingSpinner();

    if (response.ok) {
      // Success - show success message and redirect
      showFlash(
        `Transfer successful! $${transferData.amount} sent to account ${transferData.toAccountId}`,
        "success"
      );

      // Clear form
      document.getElementById("transferForm").reset();
      document.getElementById("transferSummary").classList.add("d-none");

      // Redirect to dashboard after a delay
      setTimeout(() => {
        window.location.href = "/dashboard.html";
      }, 3000);
    } else {
      // Error - show error message
      showFlash(
        result.message || "Transfer failed. Please try again.",
        "danger"
      );
    }
  } catch (error) {
    hideLoadingSpinner();
    console.error("Transfer error:", error);
    showFlash(
      "Network error. Please check your connection and try again.",
      "danger"
    );
  }
}

// Show custom transfer confirmation modal
function showTransferConfirmation(transferData, fromAccountText) {
  // Create modal HTML
  const modalHTML = `
    <div class="modal fade" id="transferConfirmModal" tabindex="-1" aria-labelledby="transferConfirmModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" style="border: none; border-radius: 20px; box-shadow: 0 20px 40px rgba(0,0,0,0.1);">
          <div class="modal-header" style="border-bottom: 1px solid rgba(255,255,255,0.1); background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-radius: 20px 20px 0 0;">
            <h5 class="modal-title" id="transferConfirmModalLabel">
              <i class="fas fa-exchange-alt me-2"></i>Confirm Transfer
            </h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body" style="padding: 2rem; background: rgba(255,255,255,0.95);">
            <div class="text-center mb-4">
              <div class="transfer-amount" style="font-size: 2.5rem; font-weight: bold; color: #667eea; margin-bottom: 0.5rem;">
                ${formatCurrency(transferData.amount)}
              </div>
              <p class="text-muted">Are you sure you want to transfer this amount?</p>
            </div>
            
            <div class="transfer-details" style="background: rgba(102, 126, 234, 0.1); border-radius: 15px; padding: 1.5rem; margin-bottom: 1.5rem;">
              <div class="row g-3">
                <div class="col-12">
                  <div class="d-flex justify-content-between align-items-center">
                    <span class="fw-bold text-muted">From:</span>
                    <span class="text-end">${fromAccountText}</span>
                  </div>
                </div>
                <div class="col-12">
                  <hr style="margin: 0.5rem 0; opacity: 0.3;">
                </div>
                <div class="col-12">
                  <div class="d-flex justify-content-between align-items-center">
                    <span class="fw-bold text-muted">To:</span>
                    <span class="text-end">••••${transferData.toAccountId.slice(
                      -4
                    )}</span>
                  </div>
                </div>
                <div class="col-12">
                  <hr style="margin: 0.5rem 0; opacity: 0.3;">
                </div>
                <div class="col-12">
                  <div class="d-flex justify-content-between align-items-center">
                    <span class="fw-bold text-muted">Amount:</span>
                    <span class="text-end fw-bold" style="color: #667eea;">${formatCurrency(
                      transferData.amount
                    )}</span>
                  </div>
                </div>
                ${
                  transferData.description
                    ? `
                <div class="col-12">
                  <hr style="margin: 0.5rem 0; opacity: 0.3;">
                </div>
                <div class="col-12">
                  <div class="d-flex justify-content-between align-items-center">
                    <span class="fw-bold text-muted">Description:</span>
                    <span class="text-end">${transferData.description}</span>
                  </div>
                </div>
                `
                    : ""
                }
              </div>
            </div>
            
            <div class="alert alert-info" style="border: none; background: rgba(23, 162, 184, 0.1); color: #17a2b8; border-radius: 10px;">
              <i class="fas fa-info-circle me-2"></i>
              This action cannot be undone. Please review the details carefully.
            </div>
          </div>
          <div class="modal-footer" style="border-top: 1px solid rgba(255,255,255,0.1); background: rgba(255,255,255,0.95); border-radius: 0 0 20px 20px; padding: 1.5rem;">
            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal" style="border-radius: 25px; padding: 0.5rem 1.5rem;">
              <i class="fas fa-times me-2"></i>Cancel
            </button>
            <button type="button" class="btn btn-primary" id="confirmTransferBtn" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; border-radius: 25px; padding: 0.5rem 1.5rem;">
              <i class="fas fa-check me-2"></i>Confirm Transfer
            </button>
          </div>
        </div>
      </div>
    </div>
  `;

  // Remove existing modal if any
  const existingModal = document.getElementById("transferConfirmModal");
  if (existingModal) {
    existingModal.remove();
  }

  // Add modal to page
  document.body.insertAdjacentHTML("beforeend", modalHTML);

  // Show modal
  const modal = new bootstrap.Modal(
    document.getElementById("transferConfirmModal")
  );
  modal.show();

  // Handle confirm button click
  document
    .getElementById("confirmTransferBtn")
    .addEventListener("click", async function () {
      modal.hide();
      await processTransfer(transferData);
    });

  // Clean up modal after it's hidden
  document
    .getElementById("transferConfirmModal")
    .addEventListener("hidden.bs.modal", function () {
      this.remove();
    });
}

// Transfer form initialization
document.addEventListener("DOMContentLoaded", function () {
  // Display any stored flash messages first
  displayStoredFlashMessage();

  // Load user accounts
  loadUserAccounts();

  // Get form elements
  const transferForm = document.getElementById("transferForm");
  const fromAccountId = document.getElementById("fromAccountId");
  const toAccountId = document.getElementById("toAccountId");
  const amount = document.getElementById("amount");
  const description = document.getElementById("description");

  // Add event listeners for real-time summary updates
  [fromAccountId, toAccountId, amount].forEach((element) => {
    element.addEventListener("input", updateTransferSummary);
    element.addEventListener("change", updateTransferSummary);
  });

  // Transfer form submission
  transferForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    if (!validateTransferForm()) {
      return;
    }

    const transferData = {
      fromAccountId: fromAccountId.value,
      toAccountId: toAccountId.value.trim(),
      amount: parseFloat(amount.value),
      description: description.value.trim() || null,
    };

    // Show custom confirmation modal
    showTransferConfirmation(
      transferData,
      fromAccountId.options[fromAccountId.selectedIndex].textContent
    );
  });

  // Logout button functionality
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", function () {
      // Clear any stored tokens or session data
      localStorage.removeItem("userToken");
      localStorage.removeItem("userId");
      localStorage.removeItem("username");
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
  }
});
