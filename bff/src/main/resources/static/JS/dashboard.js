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

// Format date
function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric",
  });
}

// Format date and time
function formatDateTime(dateString) {
  const date = new Date(dateString);
  return date.toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

// Create user welcome section
function createUserWelcomeSection(user) {
  return `
        <div class="welcome-card">
            <div class="user-info">
                <div class="user-avatar">
                    <i class="fas fa-user"></i>
                </div>
                <div class="user-details">
                    <h2>Welcome back, ${user.firstName} ${user.lastName}!</h2>
                    <p><i class="fas fa-user-circle"></i> ${user.username}</p>
                    <p><i class="fas fa-envelope"></i> ${user.email}</p>
                    <p><i class="fas fa-calendar"></i> Member since ${formatDate(
                      user.createdAt || new Date()
                    )}</p>
                </div>
            </div>
        </div>
    `;
}

// Create account summary section
function createAccountSummarySection(accounts) {
  const totalBalance = accounts.reduce(
    (sum, account) => sum + (account.balance || 0),
    0
  );
  const totalTransactions = accounts.reduce(
    (sum, account) => sum + (account.transactions?.length || 0),
    0
  );

  return `
        <div class="section-header">
            <h3 class="section-title">
                <i class="fas fa-chart-bar me-2"></i>Account Overview
            </h3>
        </div>
        <div class="summary-grid">
            <div class="summary-card total-balance">
                <div class="summary-header">
                    <div class="summary-icon">
                        <i class="fas fa-dollar-sign"></i>
                    </div>
                    <div>
                        <p class="summary-title">Total Balance</p>
                        <h3 class="summary-value">${formatCurrency(
                          totalBalance
                        )}</h3>
                        <p class="summary-subtitle">Across all accounts</p>
                    </div>
                </div>
            </div>
            <div class="summary-card total-accounts">
                <div class="summary-header">
                    <div class="summary-icon">
                        <i class="fas fa-bank"></i>
                    </div>
                    <div>
                        <p class="summary-title">Active Accounts</p>
                        <h3 class="summary-value">${accounts.length}</h3>
                        <p class="summary-subtitle">Bank accounts</p>
                    </div>
                </div>
            </div>
            <div class="summary-card recent-transactions">
                <div class="summary-header">
                    <div class="summary-icon">
                        <i class="fas fa-exchange-alt"></i>
                    </div>
                    <div>
                        <p class="summary-title">Total Transactions</p>
                        <h3 class="summary-value">${totalTransactions}</h3>
                        <p class="summary-subtitle">All time</p>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Get transaction icon based on type
function getTransactionIcon(type) {
  switch (type?.toLowerCase()) {
    case "deposit":
      return "fas fa-plus";
    case "withdrawal":
      return "fas fa-minus";
    case "transfer":
      return "fas fa-exchange-alt";
    default:
      return "fas fa-money-bill";
  }
}

// Get transaction class based on type
function getTransactionClass(type) {
  switch (type?.toLowerCase()) {
    case "deposit":
      return "deposit";
    case "withdrawal":
      return "withdrawal";
    case "transfer":
      return "transfer";
    default:
      return "deposit";
  }
}

// Create transaction item
function createTransactionItem(transaction, currentAccountId) {
  // Parse the amount (remove + sign if present)
  let amount = parseFloat(transaction.amount.toString().replace("+", ""));

  // Determine if this is incoming or outgoing based on account IDs
  const isIncoming = transaction.toAccountId === currentAccountId;
  const isOutgoing = transaction.fromAccountId === currentAccountId;

  let transactionType, icon, amountClass, amountSign;

  if (isIncoming) {
    // Money coming to this account
    transactionType = "Received";
    icon = "fas fa-arrow-down";
    amountClass = "positive";
    amountSign = "+";
  } else if (isOutgoing) {
    // Money going from this account
    transactionType = "Sent";
    icon = "fas fa-arrow-up";
    amountClass = "negative";
    amountSign = "-";
  } else {
    // Fallback
    transactionType = "Transaction";
    icon = "fas fa-exchange-alt";
    amountClass = amount >= 0 ? "positive" : "negative";
    amountSign = amount >= 0 ? "+" : "-";
  }

  return `
        <li class="transaction-item">
            <div class="transaction-details">
                <div class="transaction-icon ${amountClass}">
                    <i class="${icon}"></i>
                </div>
                <div class="transaction-info">
                    <h6>${transactionType}</h6>
                    <p>${formatDateTime(transaction.createdAt)}</p>
                    <small class="text-muted">Status: ${
                      transaction.status
                    }</small>
                </div>
            </div>
            <div class="transaction-amount">
                <p class="amount ${amountClass}">${amountSign}${formatCurrency(
    Math.abs(amount)
  )}</p>
                <p class="date">${formatDate(transaction.createdAt)}</p>
            </div>
        </li>
    `;
}

// Create account card
function createAccountCard(account) {
  const accountTypeClass = account.accountType?.toLowerCase() || "checking";
  const accountIcon =
    accountTypeClass === "savings" ? "fas fa-piggy-bank" : "fas fa-credit-card";
  const transactions = account.transactions || [];
  const recentTransactions = transactions.slice(0, 5); // Show last 5 transactions

  return `
        <div class="account-card">
            <div class="account-header ${accountTypeClass}">
                <div class="account-info">
                    <div class="account-type">
                        <i class="${accountIcon}"></i>
                        ${account.accountType || "Checking"} Account
                    </div>
                    <div class="account-number">
                        •••• •••• •••• ${
                          account.accountNumber?.slice(-4) || "0000"
                        }
                    </div>
                    <h3 class="account-balance" style="font-size: 2rem; font-weight: bold; color: #fff; text-shadow: 0 2px 4px rgba(0,0,0,0.3); margin-top: 1rem;">${formatCurrency(
                      account.balance || 0
                    )}</h3>
                </div>
            </div>
            <div class="account-body">
                <div class="transactions-header">
                    <h4 class="transactions-title">
                        <i class="fas fa-history me-2"></i>Recent Transactions
                    </h4>
                    ${
                      transactions.length > 5
                        ? '<a href="#" class="view-all-btn">View All</a>'
                        : ""
                    }
                </div>
                ${
                  recentTransactions.length > 0
                    ? `
                    <ul class="transactions-list">
                        ${recentTransactions
                          .map((transaction) =>
                            createTransactionItem(
                              transaction,
                              account.accountId
                            )
                          )
                          .join("")}
                    </ul>
                `
                    : `
                    <div class="no-transactions">
                        <i class="fas fa-receipt mb-2"></i>
                        <p>No transactions yet</p>
                    </div>
                `
                }
            </div>
        </div>
    `;
}

// Create accounts grid section
function createAccountsGridSection(accounts) {
  if (accounts.length === 0) {
    return `
            <div class="section-header">
                <h3 class="section-title">
                    <i class="fas fa-bank me-2"></i>Your Accounts
                </h3>
            </div>
            <div class="no-accounts">
                <i class="fas fa-bank"></i>
                <h4>No accounts found</h4>
                <p>Create your first bank account to get started</p>
                <a href="createAccount.html" class="btn btn-success mt-3">
                    <i class="fas fa-plus me-2"></i>Create Account
                </a>
            </div>
        `;
  }

  return `
        <div class="section-header">
            <h3 class="section-title">
                <i class="fas fa-bank me-2"></i>Your Accounts
            </h3>
        </div>
        <div class="accounts-grid">
            ${accounts.map((account) => createAccountCard(account)).join("")}
        </div>
    `;
}

// Hide loading spinner
function hideLoadingSpinner() {
  const loadingSpinner = document.getElementById("loadingSpinner");
  if (loadingSpinner) {
    loadingSpinner.style.display = "none";
  }
}

// Load dashboard data
async function loadDashboardData() {
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

    // Make API call to get dashboard data
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

    // Update navbar username
    const navbarUsername = document.getElementById("navbarUsername");
    if (navbarUsername && dashboardData.username) {
      navbarUsername.textContent = `| ${dashboardData.username}`;
    }

    // Populate dashboard sections
    const userWelcome = document.getElementById("userWelcome");
    const accountSummary = document.getElementById("accountSummary");
    const accountsGrid = document.getElementById("accountsGrid");

    if (userWelcome) {
      userWelcome.innerHTML = createUserWelcomeSection(dashboardData);
    }

    if (accountSummary) {
      accountSummary.innerHTML = createAccountSummarySection(
        dashboardData.accounts || []
      );
    }

    if (accountsGrid) {
      accountsGrid.innerHTML = createAccountsGridSection(
        dashboardData.accounts || []
      );
    }

    hideLoadingSpinner();
  } catch (error) {
    console.error("Error loading dashboard data:", error);
    hideLoadingSpinner();
    showFlash("Failed to load dashboard data. Please try again.", "danger");
  }
}

// Dashboard initialization
document.addEventListener("DOMContentLoaded", function () {
  // Display any stored flash messages first
  displayStoredFlashMessage();

  // Load dashboard data
  loadDashboardData();

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
