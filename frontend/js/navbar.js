/**
 * navbar.js — Renders the site header with search, cart, and user menu.
 * Uses inline SVG icons (Heroicons) — no emoji, no image dependencies.
 */

// SVG icon library — reused across navbar and other components
const ICONS = {
  search: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>`,
  cart:   `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 01-8 0"/></svg>`,
  user:   `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`,
  orders: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/><path d="M9 12h6M9 16h4"/></svg>`,
  profile:`<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`,
  admin:  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>`,
  logout: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>`,
  close:  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>`,
};

document.addEventListener('DOMContentLoaded', () => {
  renderNavbar();
  updateCartBadge();
});

function renderNavbar() {
  const wrap = document.getElementById('navbar');
  if (!wrap) return;

  const user       = getUser();
  const admin      = user && user.role === 'ADMIN';
  const loggedIn   = !!user;

  wrap.innerHTML = `
    <header class="navbar">
      <div class="navbar-top">

        <!-- Logo -->
        <a href="/index.html" class="navbar-logo">Shop<span>Ease</span></a>

        <!-- Search -->
        <div class="navbar-search">
          <input
            type="text"
            id="navSearchInput"
            placeholder="Search for products, brands and more..."
            onkeydown="if(event.key==='Enter') doNavSearch()"
          />
          <button class="navbar-search-btn" onclick="doNavSearch()" aria-label="Search">
            ${ICONS.search}
          </button>
        </div>

        <!-- Actions -->
        <div class="navbar-actions">

          ${admin ? `
            <a href="/pages/admin.html" class="btn btn-outline btn-sm">
              ${ICONS.admin} Admin Panel
            </a>
          ` : ''}

          ${loggedIn && !admin ? `
            <!-- Cart -->
            <button class="cart-btn" onclick="toggleCart()" aria-label="Cart">
              ${ICONS.cart}
              <span class="cart-badge" id="cartBadge" style="display:none">0</span>
              <span>Cart</span>
            </button>
          ` : ''}

          ${loggedIn ? `
            <!-- User dropdown -->
            <div class="user-menu">
              <button class="user-btn" onclick="toggleUserDD()" aria-label="Account">
                ${ICONS.user}
                <span>${user.firstName}</span>
              </button>
              <div class="user-dropdown" id="userDropdown">
                <div class="user-dropdown-name">Hi, ${user.firstName} ${user.lastName}</div>
                <a href="/pages/profile.html">
                  ${ICONS.profile} My Profile
                </a>
                ${!admin ? `
                  <a href="/pages/orders.html">
                    ${ICONS.orders} My Orders
                  </a>
                ` : ''}
                <div class="dd-divider"></div>
                <a href="#" class="dd-logout" onclick="doLogout(event)">
                  ${ICONS.logout} Sign Out
                </a>
              </div>
            </div>
          ` : `
            <a href="/pages/auth.html" class="btn btn-outline btn-sm">Sign In</a>
            <a href="/pages/auth.html?tab=register" class="btn btn-primary btn-sm">Register</a>
          `}
        </div>
      </div>
    </header>
  `;
}

function doNavSearch() {
  const input = document.getElementById('navSearchInput');
  if (!input) return;
  const q = input.value.trim();
  if (q) window.location.href = `/index.html?search=${encodeURIComponent(q)}`;
}

function toggleUserDD() {
  const dd = document.getElementById('userDropdown');
  if (dd) dd.classList.toggle('open');
}

// Close dropdown when clicking outside
document.addEventListener('click', e => {
  if (!e.target.closest('.user-menu')) {
    const dd = document.getElementById('userDropdown');
    if (dd) dd.classList.remove('open');
  }
});

function doLogout(e) {
  e.preventDefault();
  clearUser();
  localStorage.removeItem('cartCount');
  showToast('Signed out successfully.', 'success');
  setTimeout(() => window.location.href = '/index.html', 1000);
}

function updateCartBadge() {
  const badge = document.getElementById('cartBadge');
  if (!badge) return;
  const n = parseInt(localStorage.getItem('cartCount') || '0');
  badge.textContent = n;
  badge.style.display = n > 0 ? 'flex' : 'none';
}

function setCartBadge(n) {
  localStorage.setItem('cartCount', n);
  updateCartBadge();
}
