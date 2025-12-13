// src/main/resources/static/js/main.js

// --- Global Application State ---
let state = {
    user: JSON.parse(localStorage.getItem('user')) || null,
    cartId: null,      // Stores the ID of the cart from the database
    cartItems: [],     // Stores the array of items in the cart
    products: [],      // Stores loaded products
    categories: [],    // Stores loaded categories
    addresses: [],     // Store user addresses
    pendingAction: null // To store 'checkout' intent when forced to add address
};

// --- Initialization ---
document.addEventListener('DOMContentLoaded', async () => {
    lucide.createIcons();
    updateHeaderUser();
    
    // 1. Load Initial Data
    await loadCategories();
    await loadProducts('all');
    
    // 2. Initialize Cart & Addresses (if logged in)
    if (state.user) {
        await refreshCart();
        await loadUserAddresses();
    }
    
    // 3. Setup Global Event Listeners
    setupEventListeners();
});

// ==========================================
// 1. ADDRESS MANAGEMENT LOGIC
// ==========================================

async function loadUserAddresses() {
    if (!state.user) return;
    state.addresses = await api.getAddresses(state.user.id);
    updateLocationHeader();
}

function updateLocationHeader() {
    const btn = document.getElementById('header-address-text');
    if (!btn) return;

    if (state.addresses && state.addresses.length > 0) {
        // Show the first address
        const addr = state.addresses[0];
        btn.textContent = `${addr.streetAddress}, ${addr.city}`;
        btn.classList.remove('text-gray-400');
        btn.classList.add('text-gray-700');
    } else {
        btn.textContent = "Set Location";
        btn.classList.add('text-gray-400');
    }
}

function openAddressModal() {
    if (!state.user) return window.location.href = 'auth.html';
    document.getElementById('address-modal').classList.remove('hidden');
}

function closeAddressModal() {
    document.getElementById('address-modal').classList.add('hidden');
}

async function handleAddressSubmit(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);
    
    // Construct payload for Java Entity
    // Note: We wrap "user" object because of @ManyToOne relation
    const payload = {
        streetAddress: formData.get('streetAddress'),
        city: formData.get('city'),
        postalCode: formData.get('postalCode'),
        additionalInfo: formData.get('additionalInfo'),
        user: { id: state.user.id } 
    };

    const btn = form.querySelector('button[type="submit"]');
    const originalText = btn.innerText;
    btn.innerText = "Saving...";
    btn.disabled = true;

    try {
        await api.createAddress(payload);
        await loadUserAddresses(); // Refresh local state
        closeAddressModal();
        form.reset();
        
        // If we were trying to checkout, resume now
        if (state.pendingAction === 'checkout') {
            state.pendingAction = null;
            openCartDrawer(); // Re-open drawer
            handleCheckout(); // Retry checkout
        }

    } catch (err) {
        alert("Error saving address: " + err.message);
    } finally {
        btn.innerText = originalText;
        btn.disabled = false;
    }
}

// ==========================================
// 2. HEADER & AUTHENTICATION UI
// ==========================================

function updateHeaderUser() {
    const authSection = document.getElementById('nav-auth-section');
    if (!authSection) return;

    if (state.user) {
        authSection.innerHTML = `
            <div class="flex items-center gap-4">
                <span class="text-sm font-bold text-gray-700 hidden md:block">Hi, ${state.user.name}</span>
                <button id="btn-logout" class="flex items-center gap-2 text-gray-500 hover:text-red-500 font-semibold transition">
                    <i data-lucide="log-out" class="w-5 h-5"></i>
                    <span class="hidden md:inline">Logout</span>
                </button>
            </div>
        `;
        document.getElementById('btn-logout').addEventListener('click', logout);
    } else {
        authSection.innerHTML = `
            <a href="auth.html" class="flex items-center gap-2 text-gray-600 hover:text-orange-600 font-semibold transition">
                <i data-lucide="user" class="w-5 h-5"></i>
                <span>Login</span>
            </a>
        `;
    }
    lucide.createIcons();
}

function logout() {
    localStorage.removeItem('user');
    state.user = null;
    state.cartId = null;
    state.cartItems = [];
    state.addresses = [];
    
    // Reset UI
    updateHeaderUser();
    updateLocationHeader();
    updateCartUI();
    
    // Optional: Redirect to home
    window.location.href = 'index.html';
}

// ==========================================
// 3. DATA LOADING (MENU)
// ==========================================

async function loadCategories() {
    try {
        const categories = await api.getAllCategories();
        state.categories = categories;
        const rail = document.getElementById('category-rail');
        if(!rail) return;

        // Render Category Buttons
        rail.innerHTML = `
            <button onclick="loadProducts('all')" class="flex flex-col items-center gap-3 min-w-[80px] group">
                <div class="w-16 h-16 rounded-2xl flex items-center justify-center text-2xl shadow-sm bg-orange-500 text-white shadow-orange-200">🍽️</div>
                <span class="text-xs font-bold text-orange-600">All</span>
            </button>
            ${categories.map(cat => `
                <button onclick="loadProducts(${cat.id})" class="flex flex-col items-center gap-3 min-w-[80px] group">
                    <div class="w-16 h-16 rounded-2xl flex items-center justify-center text-2xl shadow-sm bg-white border border-gray-100 group-hover:bg-orange-50">
                        ${cat.icon || '🥘'}
                    </div>
                    <span class="text-xs font-bold text-gray-600 group-hover:text-orange-600">${cat.name}</span>
                </button>
            `).join('')}
        `;
    } catch(e) { console.error("Error loading categories:", e); }
}

async function loadProducts(catId) {
    const grid = document.getElementById('products-grid');
    if (!grid) return;
    
    // Show Loading Spinner
    grid.innerHTML = '<div class="col-span-full text-center py-10"><div class="animate-spin w-8 h-8 border-4 border-orange-500 border-t-transparent rounded-full mx-auto"></div></div>';
    
    try {
        // Fetch Data
        const products = catId === 'all' ? await api.getAllProducts() : await api.getProductsByCategory(catId);
        state.products = products;
        
        // Handle Empty State
        if(products.length === 0) {
            grid.innerHTML = '<div class="col-span-full text-center text-gray-400">No items found.</div>';
            return;
        }

        // Render Product Cards
        grid.innerHTML = products.map(p => `
            <div onclick="openDetailModal(${p.id})" class="group bg-white rounded-3xl p-3 shadow-sm hover:shadow-xl transition-all border border-gray-100 cursor-pointer hover:-translate-y-1">
                <div class="relative h-48 rounded-2xl overflow-hidden bg-gray-100">
                    <img src="${p.imageUrl || 'https://placehold.co/400'}" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" onerror="this.src='https://placehold.co/400'">
                    ${!p.available ? '<div class="absolute inset-0 bg-black/60 flex items-center justify-center text-white font-bold">SOLD OUT</div>' : ''}
                </div>
                <div class="mt-4 px-2 pb-2">
                    <h3 class="font-bold text-lg text-gray-900 leading-tight">${p.name}</h3>
                    <p class="text-sm text-gray-500 mt-1 line-clamp-1">${p.description || ''}</p>
                    <div class="mt-4 flex items-center justify-between">
                        <span class="text-xl font-extrabold text-gray-900">$${p.price.toFixed(2)}</span>
                        <button onclick="event.stopPropagation(); addToCart(${p.id})" class="bg-gray-100 text-gray-900 p-3 rounded-xl hover:bg-orange-600 hover:text-white transition-all shadow-sm group-hover:shadow-md">
                            <i data-lucide="plus" class="w-5 h-5"></i>
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
        lucide.createIcons();
    } catch(e) { console.error("Error loading products:", e); }
}

// ==========================================
// 4. CART LOGIC (BACKEND INTEGRATED)
// ==========================================

async function refreshCart() {
    if (!state.user) return;
    try {
        // Fetch the Cart from Backend using User ID
        const cart = await api.getCartByUser(state.user.id);
        
        // Update Local State
        state.cartId = cart.id;
        state.cartItems = cart.items || [];
        
        updateCartUI();
    } catch (e) {
        console.error("Cart sync error:", e);
    }
}

async function addToCart(productId, qty = 1) {
    // 1. Enforce Login
    if (!state.user) return window.location.href = 'auth.html';

    try {
        // 2. Ensure we have a Cart ID
        if (!state.cartId) await refreshCart();

        // 3. Send Request to Backend
        // The backend returns the updated Cart object
        const updatedCart = await api.addToCart(state.cartId, productId, qty);
        
        // 4. Update State & UI
        state.cartItems = updatedCart.items;
        updateCartUI();
        
    } catch (e) {
        alert('Failed to add item. Please try again.');
        console.error(e);
    }
}

async function updateCartQty(productId, delta) {
    if (!state.cartId) return;

    // Find the item to get current quantity
    const item = state.cartItems.find(i => i.product.id === productId);
    if (!item) return;

    const newQty = item.quantity + delta;
    
    try {
        let updatedCart;
        if (newQty <= 0) {
            // Case: Remove Item
            updatedCart = await api.removeItemFromCart(state.cartId, productId);
        } else {
            // Case: Update Quantity
            updatedCart = await api.updateCartItem(state.cartId, productId, newQty);
        }
        
        state.cartItems = updatedCart.items;
        updateCartUI();
    } catch (e) {
        console.error("Update qty error", e);
    }
}

function updateCartUI() {
    // Calculate Totals based on current state
    const total = state.cartItems.reduce((sum, item) => sum + (item.price), 0);
    const count = state.cartItems.reduce((sum, item) => sum + item.quantity, 0);

    // 1. Update Cart Badges (Desktop & Mobile)
    document.querySelectorAll('.cart-badge').forEach(el => {
        el.textContent = count;
        el.classList.toggle('hidden', count === 0);
    });

    // 2. Render Items in Drawer
    const cartContainer = document.getElementById('cart-items-container');
    if (cartContainer) {
        if (state.cartItems.length === 0) {
            cartContainer.innerHTML = `
                <div class="h-full flex flex-col items-center justify-center text-center opacity-50">
                    <i data-lucide="shopping-bag" class="w-12 h-12 text-gray-400 mb-4"></i>
                    <p class="font-bold text-gray-900">Basket is empty</p>
                </div>`;
        } else {
            cartContainer.innerHTML = state.cartItems.map(item => `
                <div class="flex gap-4 mb-4 animate-fade-in">
                    <img src="${item.product.imageUrl || 'https://placehold.co/100'}" class="w-20 h-20 rounded-xl object-cover bg-gray-100" onerror="this.src='https://placehold.co/100'">
                    <div class="flex-1">
                        <div class="flex justify-between items-start mb-1">
                            <h4 class="font-bold text-gray-900 line-clamp-1">${item.product.name}</h4>
                            <span class="font-bold text-gray-900">$${item.price.toFixed(2)}</span>
                        </div>
                        <div class="flex items-center gap-3 bg-gray-50 rounded-lg p-1 w-fit mt-2">
                            <button onclick="updateCartQty(${item.product.id}, -1)" class="w-7 h-7 flex items-center justify-center bg-white rounded shadow-sm hover:text-red-500 transition-colors"><i data-lucide="minus" class="w-3 h-3"></i></button>
                            <span class="text-sm font-bold w-4 text-center">${item.quantity}</span>
                            <button onclick="updateCartQty(${item.product.id}, 1)" class="w-7 h-7 flex items-center justify-center bg-white rounded shadow-sm hover:text-green-500 transition-colors"><i data-lucide="plus" class="w-3 h-3"></i></button>
                        </div>
                    </div>
                </div>
            `).join('');
        }
    }

    // 3. Update Totals Section
    const subtotalEl = document.getElementById('cart-subtotal');
    const totalEl = document.getElementById('cart-total');
    if (subtotalEl) subtotalEl.textContent = `$${total.toFixed(2)}`;
    if (totalEl) totalEl.textContent = `$${(total + 2.99).toFixed(2)}`; // Adding $2.99 Delivery Fee

    lucide.createIcons();
}

function openCartDrawer() {
    document.getElementById('cart-drawer').classList.remove('hidden');
}

function closeCartDrawer() {
    document.getElementById('cart-drawer').classList.add('hidden');
}

async function handleCheckout() {
    // 1. Validation
    if (!state.user) return window.location.href = 'auth.html';
    if (state.cartItems.length === 0) return alert("Your basket is empty!");

    // 2. CHECK IF USER HAS ADDRESS (UPDATED LOGIC)
    if (!state.addresses || state.addresses.length === 0) {
        state.pendingAction = 'checkout';
        closeCartDrawer();
        openAddressModal();
        return;
    }

    const checkoutBtn = document.getElementById('checkout-btn');
    if(checkoutBtn) {
        checkoutBtn.disabled = true;
        checkoutBtn.textContent = "Processing...";
    }

    try {
        // 3. Prepare Data for Backend
        const subtotal = state.cartItems.reduce((sum, item) => sum + item.price, 0);
        const deliveryFee = 2.99;
        
        const orderPayload = {
            customer: { id: state.user.id }, // Java needs User object with ID
            totalPrice: subtotal + deliveryFee,
            status: "PENDING",
            paymentMethod: "CASH_ON_DELIVERY",
            // Map cart items to the structure Order expects
            items: state.cartItems.map(item => ({
                product: { id: item.product.id, price: item.product.price }, 
                quantity: item.quantity,
                price: item.price // This is total price for this line item
            }))
        };

        // 4. Send to Backend
        console.log("Sending Order:", orderPayload); // Debugging
        const createdOrder = await api.placeOrder(orderPayload);
        console.log("Order Created:", createdOrder);

        // 5. Clear Cart (Now it's safe to delete cart items)
        if (state.cartId) {
            await api.clearCart(state.cartId);
        }

        // 6. Success UI
        closeCartDrawer();
        const overlay = document.getElementById('success-overlay');
        if(overlay) {
            overlay.classList.remove('hidden');
        } else {
            alert("Order Placed Successfully!");
        }

        // 7. Reset State and Redirect
        state.cartItems = [];
        updateCartUI();

        setTimeout(() => {
            window.location.href = 'orders.html';
        }, 1500);

    } catch (e) {
        console.error("Checkout Failed:", e);
        alert("Failed to place order: " + e.message);
        if(checkoutBtn) {
            checkoutBtn.disabled = false;
            checkoutBtn.textContent = "Checkout";
        }
    }
}

// ==========================================
// 5. MODALS (DETAIL, AUTH & ADDRESS)
// ==========================================

function openDetailModal(productId) {
    const product = state.products.find(p => p.id === productId);
    if (!product) return;

    // Populate Modal Data
    const imgEl = document.getElementById('modal-img');
    imgEl.src = product.imageUrl || 'https://placehold.co/600';
    imgEl.onerror = () => imgEl.src = 'https://placehold.co/600';

    document.getElementById('modal-title').textContent = product.name;
    document.getElementById('modal-desc').textContent = product.description || 'Delicious food ready to be delivered.';
    document.getElementById('modal-price').textContent = `$${product.price.toFixed(2)}`;
    
    // Recreate Add Button to clear old listeners
    const btn = document.getElementById('modal-add-btn');
    const newBtn = btn.cloneNode(true);
    btn.parentNode.replaceChild(newBtn, btn);
    
    newBtn.onclick = () => {
        addToCart(product.id, 1);
        closeDetailModal();
    };

    document.getElementById('detail-modal').classList.remove('hidden');
}

function closeDetailModal() {
    document.getElementById('detail-modal').classList.add('hidden');
}

function switchAuthTab(tab) {
    const loginForm = document.getElementById('auth-login-form');
    const registerForm = document.getElementById('auth-register-form');
    const tabLogin = document.getElementById('tab-login');
    const tabRegister = document.getElementById('tab-register');

    if (tab === 'login') {
        loginForm?.classList.remove('hidden');
        registerForm?.classList.add('hidden');
        tabLogin?.classList.add('text-orange-600', 'border-b-2', 'border-orange-600');
        tabLogin?.classList.remove('text-gray-400');
        tabRegister?.classList.remove('text-orange-600', 'border-b-2', 'border-orange-600');
        tabRegister?.classList.add('text-gray-400');
    } else {
        loginForm?.classList.add('hidden');
        registerForm?.classList.remove('hidden');
        tabRegister?.classList.add('text-orange-600', 'border-b-2', 'border-orange-600');
        tabRegister?.classList.remove('text-gray-400');
        tabLogin?.classList.remove('text-orange-600', 'border-b-2', 'border-orange-600');
        tabLogin?.classList.add('text-gray-400');
    }
}

function setupEventListeners() {
    // Auth Forms (checked if present on page)
    const loginForm = document.getElementById('form-login');
    if (loginForm) {
        loginForm.onsubmit = async (e) => {
            e.preventDefault();
            const fd = new FormData(e.target);
            try {
                const user = await api.login(fd.get('email'), fd.get('password'));
                localStorage.setItem('user', JSON.stringify(user));
                state.user = user;
                
                // Role Redirects
                const role = user.role?.roleName || user.role; 
                if (role === 'ADMIN') window.location.href = 'admin_dashboard.html';
                else if (role === 'STAFF') window.location.href = 'staff_dashboard.html';
                else if (role === 'DELIVERY_STAFF') window.location.href = 'delivery_dashboard.html';
                else window.location.href = 'index.html'; 
                
            } catch(err) { 
                alert(err.message); 
            }
        };
    }

    const registerForm = document.getElementById('form-register');
    if (registerForm) {
        registerForm.onsubmit = async (e) => {
            e.preventDefault();
            const fd = new FormData(e.target);
            const data = Object.fromEntries(fd);
            
            if(data.password.length < 8) return alert("Password must be at least 8 chars");

            try {
                await api.registerCustomer(data);
                alert('Account created! Please login.');
                switchAuthTab('login');
            } catch(err) { 
                alert(err.message); 
            }
        };
    }

    // NEW: Address Form Listener
    const addressForm = document.getElementById('address-form');
    if(addressForm) {
        addressForm.addEventListener('submit', handleAddressSubmit);
    }
}