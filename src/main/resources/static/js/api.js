const API_BASE = '/api';

const api = {
    // --- Helper: Get Auth Headers ---
    getHeaders: () => {
        const user = JSON.parse(localStorage.getItem('user'));
        const headers = { 'Content-Type': 'application/json' };
        // If you implement JWT later, uncomment the line below:
        // if (user && user.token) headers['Authorization'] = `Bearer ${user.token}`;
        return headers;
    },

    // ============================
    // 1. AUTHENTICATION
    // ============================

    login: async (email, password) => {
        const response = await fetch(`${API_BASE}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        
        if (!response.ok) {
            const errorMsg = await response.text();
            throw new Error(errorMsg || 'Login failed');
        }
        return await response.json();
    },

    registerCustomer: async (userData) => {
        const response = await fetch(`${API_BASE}/users/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            const errorMsg = await response.text();
            throw new Error(errorMsg || 'Registration failed');
        }
        return await response.json();
    },

    // ============================
    // 2. PUBLIC DATA (Menu)
    // ============================

    getAllCategories: async () => {
        const response = await fetch(`${API_BASE}/categories/all`); 
        if (!response.ok) return [];
        return await response.json();
    },

    getAllProducts: async () => {
        const response = await fetch(`${API_BASE}/products/all`);
        if (!response.ok) return [];
        return await response.json();
    },

    getProductsByCategory: async (categoryId) => {
        const response = await fetch(`${API_BASE}/products/category/${categoryId}`);
        if (!response.ok) return [];
        return await response.json();
    },

    // ============================
    // 3. SHOPPING CART (New)
    // ============================

    // Get (or create) cart for a logged-in user
    getCartByUser: async (userId) => {
        const response = await fetch(`${API_BASE}/carts/user/${userId}`);
        if (!response.ok) throw new Error('Failed to load cart');
        return await response.json();
    },

    // Add Item to Cart
    addToCart: async (cartId, productId, quantity) => {
        const response = await fetch(`${API_BASE}/carts/${cartId}/items?productId=${productId}&quantity=${quantity}`, {
            method: 'POST',
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error('Failed to add to cart');
        return await response.json();
    },

    // Update Item Quantity
    updateCartItem: async (cartId, productId, quantity) => {
        const response = await fetch(`${API_BASE}/carts/${cartId}/items?productId=${productId}&quantity=${quantity}`, {
            method: 'PUT',
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error('Failed to update cart');
        return await response.json();
    },

    // Remove Item Completely
    removeItemFromCart: async (cartId, productId) => {
        const response = await fetch(`${API_BASE}/carts/${cartId}/items?productId=${productId}`, {
            method: 'DELETE',
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error('Failed to remove item');
        return await response.json();
    },

    // Clear entire cart
    clearCart: async (cartId) => {
        const response = await fetch(`${API_BASE}/carts/${cartId}`, {
            method: 'DELETE',
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error('Failed to clear cart');
        return true;
    },

    // ============================
    // 4. ADMIN & STAFF DASHBOARDS
    // ============================

    // Admin: Add new Staff
    addStaffMember: async (userData, roleName) => {
        const response = await fetch(`${API_BASE}/admins/staff?role=${roleName}`, {
            method: 'POST',
            headers: api.getHeaders(),
            body: JSON.stringify(userData)
        });
        if (!response.ok) throw new Error('Failed to create staff');
        return await response.json();
    },

    // Admin: Add new Product
    addProduct: async (productDTO) => {
        const response = await fetch(`${API_BASE}/admins/menu`, {
            method: 'POST',
            headers: api.getHeaders(),
            body: JSON.stringify(productDTO)
        });
        if (!response.ok) throw new Error('Failed to add product');
        return await response.json();
    },

    // Staff: View All Orders
    viewAllOrders: async () => {
        const response = await fetch(`${API_BASE}/staff/orders`, { 
            headers: api.getHeaders() 
        });
        return response.ok ? await response.json() : [];
    },

    // Staff: Update Order Status (Cooking, Delivered, etc.)
    updateOrderStatus: async (orderId, status) => {
        const response = await fetch(`${API_BASE}/staff/orders/${orderId}/status?newStatus=${status}`, {
            method: 'PUT',
            headers: api.getHeaders()
        });
        return await response.json();
    }
};