/**
 * api.js — Centralized API client
 * All backend calls go through this file.
 * Base URL is defined once here — change it in one place if needed.
 */

const API_BASE = 'http://localhost:8080/api';

/**
 * Core fetch wrapper — handles JSON, errors, and console logging.
 * Re-used by every API function to keep code DRY.
 *
 * @param {string} endpoint - API path (e.g., '/users/login')
 * @param {Object} options  - fetch options (method, body, etc.)
 * @returns {Promise<Object>} - parsed response body
 */
async function apiFetch(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`;

  const config = {
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  };

  if (options.body && typeof options.body === 'object') {
    config.body = JSON.stringify(options.body);
  }

  console.log(`[API] ${config.method || 'GET'} ${url}`);

  try {
    const response = await fetch(url, config);
    const data = await response.json();

    if (!response.ok) {
      // Use backend error message if available
      throw new Error(data.message || `HTTP ${response.status}`);
    }

    return data;
  } catch (err) {
    console.error(`[API ERROR] ${endpoint}:`, err.message);
    throw err;
  }
}

// ── User Auth & Profile ──────────────────────────────────────────

const UserAPI = {
  register: (data) => apiFetch('/users/register', { method: 'POST', body: data }),
  login: (data) => apiFetch('/users/login', { method: 'POST', body: data }),
  getById: (id) => apiFetch(`/users/${id}`),
  update: (id, data) => apiFetch(`/users/${id}`, { method: 'PUT', body: data }),
  getAll: () => apiFetch('/users'),
  toggleStatus: (id) => apiFetch(`/users/${id}/toggle-status`, { method: 'PATCH' }),
};

// ── Products ─────────────────────────────────────────────────────

const ProductAPI = {
  getAll: () => apiFetch('/products'),
  getAllAdmin: () => apiFetch('/products/admin'),
  getById: (id) => apiFetch(`/products/${id}`),
  search: (keyword) => apiFetch(`/products/search?keyword=${encodeURIComponent(keyword)}`),
  getByCategory: (catId) => apiFetch(`/products/category/${catId}`),
  add: (data) => apiFetch('/products', { method: 'POST', body: data }),
  update: (id, data) => apiFetch(`/products/${id}`, { method: 'PUT', body: data }),
  delete: (id) => apiFetch(`/products/${id}`, { method: 'DELETE' }),
};

// ── Categories ───────────────────────────────────────────────────

const CategoryAPI = {
  getAll: () => apiFetch('/categories'),
  add: (data) => apiFetch('/categories', { method: 'POST', body: data }),
  update: (id, data) => apiFetch(`/categories/${id}`, { method: 'PUT', body: data }),
  delete: (id) => apiFetch(`/categories/${id}`, { method: 'DELETE' }),
};

// ── Cart ─────────────────────────────────────────────────────────

const CartAPI = {
  getCart: (userId) => apiFetch(`/cart/${userId}`),
  addItem: (data) => apiFetch('/cart/add', { method: 'POST', body: data }),
  updateItem: (itemId, quantity) => apiFetch(`/cart/item/${itemId}`, { method: 'PUT', body: { quantity } }),
  removeItem: (itemId) => apiFetch(`/cart/item/${itemId}`, { method: 'DELETE' }),
  clear: (userId) => apiFetch(`/cart/${userId}/clear`, { method: 'DELETE' }),
};

// ── Orders ───────────────────────────────────────────────────────

const OrderAPI = {
  checkout: (data) => apiFetch('/orders/checkout', { method: 'POST', body: data }),
  getUserOrders: (userId) => apiFetch(`/orders/user/${userId}`),
  getById: (id) => apiFetch(`/orders/${id}`),
  getAll: () => apiFetch('/orders'),
  getStats: () => apiFetch('/orders/stats'),
  updateStatus: (id, status) => apiFetch(`/orders/${id}/status`, { method: 'PATCH', body: { status } }),
  cancel: (id, reason, isAdmin = false) => apiFetch(`/orders/${id}/cancel`, {
    method: 'POST',
    body: { reason, isAdmin: String(isAdmin) }
  }),
};
