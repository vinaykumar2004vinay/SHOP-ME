/**
 * cart.js — Shopping cart sidebar.
 * SVG icons only, clean minimal layout.
 */

const TRASH_ICON = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14H6L5 6"/><path d="M10 11v6M14 11v6"/><path d="M9 6V4h6v2"/></svg>`;
const CLOSE_ICON = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>`;
const CART_IMG   = `https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=220&q=60`;

let cartData = null;

async function toggleCart() {
  const sidebar = document.getElementById('cartSidebar');
  const overlay = document.getElementById('cartOverlay');
  if (!sidebar) return;

  if (sidebar.classList.contains('open')) {
    closeCart();
  } else {
    sidebar.classList.add('open');
    overlay.classList.add('open');
    document.body.style.overflow = 'hidden';
    const user = getUser();
    if (user) await loadCart(user.id);
    else renderCartEmpty();
  }
}

function closeCart() {
  const sidebar = document.getElementById('cartSidebar');
  const overlay = document.getElementById('cartOverlay');
  if (sidebar) sidebar.classList.remove('open');
  if (overlay) overlay.classList.remove('open');
  document.body.style.overflow = '';
}

async function loadCart(userId) {
  const body = document.getElementById('cartBody');
  if (body) body.innerHTML = `<div class="loading-center"><div class="spinner"></div></div>`;
  try {
    const res = await CartAPI.getCart(userId);
    cartData = res.data;
    renderCart(cartData);
    const n = cartData.items ? cartData.items.length : 0;
    setCartBadge(n);
  } catch (err) {
    if (body) body.innerHTML = `<div class="loading-center"><p>Could not load cart</p></div>`;
  }
}

function renderCart(cart) {
  const body   = document.getElementById('cartBody');
  const footer = document.getElementById('cartFooter');
  if (!body) return;

  if (!cart || !cart.items || cart.items.length === 0) {
    renderCartEmpty();
    if (footer) footer.style.display = 'none';
    return;
  }

  let subtotal = 0;
  body.innerHTML = cart.items.map(item => {
    const sub = item.product.price * item.quantity;
    subtotal += sub;
    return `
      <div class="cart-item">
        <img class="cart-item-img"
          src="${productImage(item.product.imageUrl, item.product.name)}"
          alt="${item.product.name}"
          onerror="this.src='https://placehold.co/68x68/e2e8f0/94a3b8?text=IMG'"
        />
        <div class="cart-item-info">
          <div class="cart-item-name">${item.product.name}</div>
          <div class="cart-item-price">${formatPrice(item.product.price)}</div>
          <div class="qty-control">
            <button class="qty-btn" onclick="changeQty(${item.id}, ${item.quantity - 1})">&#8722;</button>
            <span class="qty-value">${item.quantity}</span>
            <button class="qty-btn" onclick="changeQty(${item.id}, ${item.quantity + 1})">+</button>
          </div>
        </div>
        <button class="cart-remove-btn" onclick="removeCartItem(${item.id})" title="Remove">
          ${TRASH_ICON}
        </button>
      </div>`;
  }).join('');

  if (footer) {
    footer.style.display = 'block';
    footer.innerHTML = `
      <div class="cart-summary-row"><span>Subtotal</span><span>${formatPrice(subtotal)}</span></div>
      <div class="cart-summary-row"><span>Delivery</span><span style="color:var(--success)">Calculated at checkout</span></div>
      <div class="cart-summary-row total"><span>Estimated Total</span><span>${formatPrice(subtotal)}</span></div>
      <a href="/pages/checkout.html" class="btn btn-primary btn-full btn-lg" style="margin-top:1rem" onclick="closeCart()">
        Proceed to Checkout
      </a>
      <button class="btn btn-ghost btn-full" style="margin-top:0.5rem" onclick="closeCart()">Continue Shopping</button>
    `;
  }
}

function renderCartEmpty() {
  const body = document.getElementById('cartBody');
  if (body) body.innerHTML = `
    <div class="cart-empty">
      <img src="${CART_IMG}" alt="Empty cart" />
      <h4>Your cart is empty</h4>
      <p>Add items to get started</p>
    </div>`;
}

async function changeQty(itemId, newQty) {
  try {
    if (newQty <= 0) { await removeCartItem(itemId); return; }
    await CartAPI.updateItem(itemId, newQty);
    const user = getUser();
    if (user) await loadCart(user.id);
  } catch (err) { showToast(err.message, 'error'); }
}

async function removeCartItem(itemId) {
  try {
    await CartAPI.removeItem(itemId);
    showToast('Item removed', 'success');
    const user = getUser();
    if (user) await loadCart(user.id);
  } catch (err) { showToast(err.message, 'error'); }
}

async function addToCart(productId, qty = 1) {
  const user = getUser();
  if (!user) {
    showToast('Please sign in to add items to cart', 'warning');
    setTimeout(() => window.location.href = '/pages/auth.html', 1000);
    return;
  }
  try {
    await CartAPI.addItem({ userId: user.id, productId, quantity: qty });
    showToast('Added to cart', 'success');
    const prev = parseInt(localStorage.getItem('cartCount') || '0');
    setCartBadge(prev + 1);
    const sidebar = document.getElementById('cartSidebar');
    if (sidebar && sidebar.classList.contains('open')) await loadCart(user.id);
  } catch (err) { showToast(err.message || 'Could not add to cart', 'error'); }
}

function getCurrentCart() { return cartData; }
