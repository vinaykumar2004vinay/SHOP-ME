/**
 * utils.js — Shared utility functions.
 * Session management, toast notifications, formatting helpers.
 */

// ── Session / Auth ─────────────────────────────────────────────

function saveUser(user) { sessionStorage.setItem('user', JSON.stringify(user)); }

function getUser() {
  try { return JSON.parse(sessionStorage.getItem('user')); }
  catch { return null; }
}

function clearUser() { sessionStorage.removeItem('user'); }
function isLoggedIn() { return !!getUser(); }
function isAdmin()    { const u = getUser(); return u && u.role === 'ADMIN'; }

function requireAuth() {
  if (!isLoggedIn()) window.location.href = '/pages/auth.html';
}

function requireAdmin() {
  if (!isAdmin()) {
    showToast('Access denied. Admins only.', 'error');
    setTimeout(() => window.location.href = '/index.html', 1500);
  }
}

// ── Toast Notifications ────────────────────────────────────────

const TOAST_ICONS = {
  success: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>`,
  error:   `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>`,
  warning: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>`,
  info:    `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>`,
};

function showToast(message, type = 'info', duration = 3500) {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `${TOAST_ICONS[type] || TOAST_ICONS.info}<span>${message}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.animation = 'toastOut 0.25s ease forwards';
    setTimeout(() => toast.remove(), 250);
  }, duration);
}

// ── Formatting ─────────────────────────────────────────────────

function formatPrice(amount) {
  return '₹' + Number(amount).toLocaleString('en-IN', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  });
}

function formatDate(dateString) {
  if (!dateString) return '—';
  return new Date(dateString).toLocaleDateString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric'
  });
}

function formatDateTime(dateString) {
  if (!dateString) return '—';
  return new Date(dateString).toLocaleString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  });
}

function truncate(text, max = 60) {
  if (!text) return '';
  return text.length > max ? text.slice(0, max) + '...' : text;
}

/** Returns img URL or a clean grey placeholder */
function productImage(url, name = 'Product') {
  if (url && (url.startsWith('http') || url.startsWith('/'))) return url;
  return `https://placehold.co/400x300/e2e8f0/94a3b8?text=${encodeURIComponent(name.slice(0,12))}`;
}

/** Status badge HTML — no emojis, clean coloured chips */
function statusBadge(status) {
  const cls = {
    PENDING:    'status-pending',
    PROCESSING: 'status-processing',
    SHIPPED:    'status-shipped',
    DELIVERED:  'status-delivered',
    CANCELLED:  'status-cancelled'
  }[status] || 'badge-gray';
  return `<span class="status-chip ${cls}">${status}</span>`;
}

// ── Modal Helpers ──────────────────────────────────────────────

function openModal(id) {
  const el = document.getElementById(id);
  if (el) { el.style.display = 'flex'; document.body.style.overflow = 'hidden'; }
}

function closeModal(id) {
  const el = document.getElementById(id);
  if (el) { el.style.display = 'none'; document.body.style.overflow = ''; }
}

document.addEventListener('click', e => {
  if (e.target.classList.contains('modal-overlay')) {
    e.target.style.display = 'none';
    document.body.style.overflow = '';
  }
});

// ── Loading States ─────────────────────────────────────────────

function setLoading(btn, loading, text = 'Loading...') {
  if (!btn) return;
  if (loading) {
    btn.disabled = true;
    btn.dataset.orig = btn.innerHTML;
    btn.innerHTML = `<span class="spinner spinner-sm" style="width:14px;height:14px;border-width:2px"></span> ${text}`;
  } else {
    btn.disabled = false;
    btn.innerHTML = btn.dataset.orig || btn.innerHTML;
  }
}
