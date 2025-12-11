// src/main/resources/static/js/main.js

// --- Global State ---
let state = {
    user: JSON.parse(localStorage.getItem('user')) || null,
    cart: [], // We manage cart locally for UI speed, sync with API on checkout/add
    products: [],
    categories: []
};

// --- Initialization ---
document.addEventListener('DOMContentLoaded', async () => {
    lucide.createIcons();
    updateHeaderUser();
    
    // Load Data
    await loadCategories();
    await loadProducts('all');
    
    // Setup Global Event Listeners
    setupEventListeners();
});

// --- 1. Header & Auth Logic ---

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
            <button onclick="openAuthModal()" class="flex items-center gap-2 text-gray-600 hover:text-orange-600 font-semibold transition">
                <i data-lucide="user" class="w-5 h-5"></i>
                <span>Login</span>
            </button>
        `;
    }
    lucide.createIcons();
}

function logout() {
    localStorage.removeItem('user');
    state.user = null;
    state.cart = [];
    updateHeaderUser();
    updateCartUI();
    window.location.reload();
}

// --- 2. Data Loading (Products/Categories) ---

async function loadCategories() {
    try {
        const categories = await api.getAllCategories();
        state.categories = categories;
        const rail = document.getElementById('category-rail');
        if(!rail) return;

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
    } catch(e) { console.error(e); }
}

async function loadProducts(catId) {
    const grid = document.getElementById('products-grid');
    grid.innerHTML = '<div class="col-span-full text-center py-10"><div class="animate-spin w-8 h-8 border-4 border-orange-500 border-t-transparent rounded-full mx-auto"></div></div>';
    
    try {
        const products = catId === 'all' ? await api.getAllProducts() : await api.getProductsByCategory(catId);
        state.products = products;
        
        if(products.length === 0) {
            grid.innerHTML = '<div class="col-span-full text-center text-gray-400">No items found.</div>';
            return;
        }

        grid.innerHTML = products.map(p => `
            <div onclick="openDetailModal(${p.id})" class="group bg-white rounded-3xl p-3 shadow-sm hover:shadow-xl transition-all border border-gray-100 cursor-pointer hover:-translate-y-1">
                <div class="relative h-48 rounded-2xl overflow-hidden bg-gray-100">
                    <img src="${p.imageUrl || 'https://placehold.co/400'}" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500">
                    ${!p.available ? '<div class="absolute inset-0 bg-black/60 flex items-center justify-center text-white font-bold">SOLD OUT</div>' : ''}
                </div>
                <div class="mt-4 px-2 pb-2">
                    <h3 class="font-bold text-lg text-gray-900 leading-tight">${p.name}</h3>
                    <p class="text-sm text-gray-500 mt-1 line-clamp-1">${p.description || ''}</p>
                    <div class="mt-4 flex items-center justify-between">
                        <span class="text-xl font-extrabold text-gray-900">$${p.price.toFixed(2)}</span>
                        <button onclick="event.stopPropagation(); addToCart(${p.id})" class="bg-gray-100 text-gray-900 p-3 rounded-xl hover:bg-orange-600 hover:text-white transition-all shadow-sm">
                            <i data-lucide="plus" class="w-5 h-5"></i>
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
        lucide.createIcons();
    } catch(e) { console.error(e); }
}

// --- 3. Cart Logic & Drawer ---

function addToCart(productId, qty = 1) {
    if (!state.user) return openAuthModal();

    const product = state.products.find(p => p.id === productId);
    const existing = state.cart.find(i => i.id === productId);

    if (existing) {
        existing.qty += qty;
    } else {
        state.cart.push({ ...product, qty });
    }
    
    // Sync with API (Optional: Implement api.addToCart here)
    updateCartUI();
    openCartDrawer();
}

function updateCartQty(productId, delta) {
    const item = state.cart.find(i => i.id === productId);
    if (item) {
        item.qty += delta;
        if (item.qty <= 0) state.cart = state.cart.filter(i => i.id !== productId);
        updateCartUI();
    }
}

function updateCartUI() {
    const total = state.cart.reduce((sum, i) => sum + (i.price * i.qty), 0);
    const count = state.cart.reduce((sum, i) => sum + i.qty, 0);

    // Update Badges
    document.querySelectorAll('.cart-badge').forEach(el => {
        el.textContent = count;
        el.classList.toggle('hidden', count === 0);
    });

    // Render Drawer Items
    const cartContainer = document.getElementById('cart-items-container');
    if (cartContainer) {
        if (state.cart.length === 0) {
            cartContainer.innerHTML = `
                <div class="h-full flex flex-col items-center justify-center text-center opacity-50">
                    <i data-lucide="shopping-bag" class="w-12 h-12 text-gray-400 mb-4"></i>
                    <p class="font-bold text-gray-900">Basket is empty</p>
                </div>`;
        } else {
            cartContainer.innerHTML = state.cart.map(item => `
                <div class="flex gap-4 mb-4">
                    <img src="${item.imageUrl || 'https://placehold.co/100'}" class="w-20 h-20 rounded-xl object-cover bg-gray-100">
                    <div class="flex-1">
                        <div class="flex justify-between items-start mb-1">
                            <h4 class="font-bold text-gray-900 line-clamp-1">${item.name}</h4>
                            <span class="font-bold text-gray-900">$${(item.price * item.qty).toFixed(2)}</span>
                        </div>
                        <div class="flex items-center gap-3 bg-gray-50 rounded-lg p-1 w-fit mt-2">
                            <button onclick="updateCartQty(${item.id}, -1)" class="w-7 h-7 flex items-center justify-center bg-white rounded shadow-sm"><i data-lucide="minus" class="w-3 h-3"></i></button>
                            <span class="text-sm font-bold w-4 text-center">${item.qty}</span>
                            <button onclick="updateCartQty(${item.id}, 1)" class="w-7 h-7 flex items-center justify-center bg-white rounded shadow-sm"><i data-lucide="plus" class="w-3 h-3"></i></button>
                        </div>
                    </div>
                </div>
            `).join('');
        }
    }

    // Update Totals
    const subtotalEl = document.getElementById('cart-subtotal');
    const totalEl = document.getElementById('cart-total');
    if (subtotalEl) subtotalEl.textContent = `$${total.toFixed(2)}`;
    if (totalEl) totalEl.textContent = `$${(total + 2.99).toFixed(2)}`; // $2.99 Delivery

    lucide.createIcons();
}

function openCartDrawer() {
    document.getElementById('cart-drawer').classList.remove('hidden');
}

function closeCartDrawer() {
    document.getElementById('cart-drawer').classList.add('hidden');
}

async function handleCheckout() {
    if (!state.user) return openAuthModal();
    
    // Show Success Overlay
    closeCartDrawer();
    const overlay = document.getElementById('success-overlay');
    overlay.classList.remove('hidden');
    
    // Simulate API call
    setTimeout(() => {
        state.cart = []; // Clear cart
        updateCartUI();
        overlay.classList.add('hidden');
    }, 3000);
}

// --- 4. Modals (Detail & Auth) ---

function openDetailModal(productId) {
    const product = state.products.find(p => p.id === productId);
    if (!product) return;

    document.getElementById('modal-img').src = product.imageUrl || 'https://placehold.co/600';
    document.getElementById('modal-title').textContent = product.name;
    document.getElementById('modal-desc').textContent = product.description || 'Tasty food.';
    document.getElementById('modal-price').textContent = `$${product.price}`;
    
    // Setup Add Button
    const btn = document.getElementById('modal-add-btn');
    btn.onclick = () => {
        addToCart(product.id, 1);
        closeDetailModal();
    };

    document.getElementById('detail-modal').classList.remove('hidden');
}

function closeDetailModal() {
    document.getElementById('detail-modal').classList.add('hidden');
}

// Auth Modal
function openAuthModal() {
    document.getElementById('auth-modal').classList.remove('hidden');
    switchAuthTab('login');
}

function closeAuthModal() {
    document.getElementById('auth-modal').classList.add('hidden');
}

function switchAuthTab(tab) {
    const loginForm = document.getElementById('auth-login-form');
    const registerForm = document.getElementById('auth-register-form');
    const tabLogin = document.getElementById('tab-login');
    const tabRegister = document.getElementById('tab-register');

    if (tab === 'login') {
        loginForm.classList.remove('hidden');
        registerForm.classList.add('hidden');
        tabLogin.classList.add('text-orange-600', 'border-b-2', 'border-orange-600');
        tabLogin.classList.remove('text-gray-400');
        tabRegister.classList.remove('text-orange-600', 'border-b-2', 'border-orange-600');
        tabRegister.classList.add('text-gray-400');
    } else {
        loginForm.classList.add('hidden');
        registerForm.classList.remove('hidden');
        tabRegister.classList.add('text-orange-600', 'border-b-2', 'border-orange-600');
        tabRegister.classList.remove('text-gray-400');
        tabLogin.classList.remove('text-orange-600', 'border-b-2', 'border-orange-600');
        tabLogin.classList.add('text-gray-400');
    }
}

function setupEventListeners() {
    // Auth Forms
    document.getElementById('form-login').onsubmit = async (e) => {
        e.preventDefault();
        const fd = new FormData(e.target);
        try {
            const user = await api.login(fd.get('email'), fd.get('password'));
            localStorage.setItem('user', JSON.stringify(user));
            state.user = user;
            closeAuthModal();
            updateHeaderUser();
            
            // Redirect based on role
            const role = user.role?.name || user.role;
            if (role === 'ADMIN') window.location.href = 'admin_dashboard.html';
            else if (role === 'STAFF') window.location.href = 'staff_dashboard.html';
            else if (role === 'DELIVERY') window.location.href = 'delivery_dashboard.html';
        } catch(err) { alert(err.message); }
    };

    document.getElementById('form-register').onsubmit = async (e) => {
        e.preventDefault();
        const fd = new FormData(e.target);
        try {
            await api.registerCustomer(Object.fromEntries(fd));
            alert('Account created! Please login.');
            switchAuthTab('login');
        } catch(err) { alert(err.message); }
    };
}