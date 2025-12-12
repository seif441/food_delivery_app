// FIXED: Point to your running Spring Boot backend on port 5005
const API_BASE = 'http://localhost:5005/api';

const api = {
    // 1. Session Persistence: Load user immediately
    currentUser: JSON.parse(localStorage.getItem('user')) || null,

    // --- Helper: Get Auth Headers ---
    getHeaders: () => {
        const headers = { 'Content-Type': 'application/json' };
        if (api.currentUser && api.currentUser.token) {
            headers['Authorization'] = `Bearer ${api.currentUser.token}`;
        }
        return headers;
    },

    // --- User Session Management ---
    setUser: (user) => {
        api.currentUser = user;
        localStorage.setItem('user', JSON.stringify(user));
    },

    getUser: () => {
        return api.currentUser;
    },

    clearUser: () => {
        api.currentUser = null;
        localStorage.removeItem('user');
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
        
        const user = await response.json();
        api.setUser(user); 
        return user;
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

    // NEW FUNCTION
    addCategory: async (description) => {
        // We send 'description' as BOTH description and name to satisfy database rules
        const response = await fetch(`${API_BASE}/categories/add`, {
            method: 'POST',
            headers: api.getHeaders(),
            body: JSON.stringify({ description: description, name: description }) 
        });
        if (!response.ok) throw new Error("Failed to add category");
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
    // 3. SHOPPING CART
    // ============================
    placeOrder: async (orderData) => {
        try {
            const response = await fetch(`${API_BASE}/orders/place`, {
                method: 'POST',
                headers: api.getHeaders(),
                body: JSON.stringify(orderData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Failed to place order');
            }
            return await response.json();
        } catch (error) {
            console.error('Place order error:', error);
            throw error;
        }
    },

    getCartByUser: async (userId) => {
        try {
            const response = await fetch(`${API_BASE}/carts/user/${userId}`);
            if (!response.ok) throw new Error('Failed to load cart');
            return await response.json();
        } catch (error) {
            console.error('Cart fetch error:', error);
            throw error;
        }
    },

    addToCart: async (cartId, productId, quantity) => {
        try {
            const url = `${API_BASE}/carts/${cartId}/items?productId=${productId}&quantity=${quantity}`;
            const response = await fetch(url, {
                method: 'POST',
                headers: api.getHeaders()
            });
            if (!response.ok) throw new Error('Failed to add item');
            return await response.json();
        } catch (error) {
            console.error('Add to cart error:', error);
            throw error;
        }
    },

    updateCartItem: async (cartId, productId, quantity) => {
        try {
            const url = `${API_BASE}/carts/${cartId}/items?productId=${productId}&quantity=${quantity}`;
            const response = await fetch(url, {
                method: 'PUT',
                headers: api.getHeaders()
            });
            if (!response.ok) throw new Error('Failed to update item');
            return await response.json();
        } catch (error) {
            console.error('Update cart error:', error);
            throw error;
        }
    },

    removeItemFromCart: async (cartId, productId) => {
        try {
            const url = `${API_BASE}/carts/${cartId}/items?productId=${productId}`;
            const response = await fetch(url, {
                method: 'DELETE',
                headers: api.getHeaders()
            });
            if (!response.ok) throw new Error('Failed to remove item');
            return await response.json();
        } catch (error) {
            console.error('Remove item error:', error);
            throw error;
        }
    },

    clearCart: async (cartId) => {
        try {
            const response = await fetch(`${API_BASE}/carts/${cartId}`, {
                method: 'DELETE',
                headers: api.getHeaders()
            });
            if (!response.ok) throw new Error('Failed to clear cart');
            return true;
        } catch (error) {
            console.error('Clear cart error:', error);
            throw error;
        }
    },

    // ============================
    // 4. ADMIN & STAFF DASHBOARDS
    // ============================

    // NEW FUNCTION
    getAllUsers: async () => {
        const response = await fetch(`${API_BASE}/admins/users`, { headers: api.getHeaders() });
        return response.ok ? await response.json() : [];
    },

    // NEW FUNCTION
    deleteUser: async (id) => {
        const response = await fetch(`${API_BASE}/admins/users/${id}`, { 
            method: 'DELETE', 
            headers: api.getHeaders() 
        });
        if (!response.ok) throw new Error("Failed to delete user");
    },


    // Admin: Add new Staff
    addStaffMember: async (userData, roleName) => {
        const response = await fetch(`${API_BASE}/admins/staff?roleName=${roleName}`, {
            method: 'POST',
            headers: api.getHeaders(),
            body: JSON.stringify(userData)
        });
        if (!response.ok) throw new Error('Failed to create staff');
        return await response.json();
    },

    addProduct: async (productDTO) => {
    const response = await fetch(`${API_BASE}/products/add`, {
            method: 'POST',
            headers: api.getHeaders(),
            body: JSON.stringify(productDTO)
        });
        if (!response.ok) throw new Error('Failed to add product');
        return await response.json();
    },


    getDriverProfile: async (id) => {
        const response = await fetch(`${API_BASE}/delivery/${id}`, { 
            headers: api.getHeaders() 
        });
        if (!response.ok) throw new Error('Failed to load profile');
        return await response.json();
    },

    
    getStaffProfile: async (staffId) => {
        const response = await fetch(`${API_BASE}/staff/${staffId}`, {
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error('Failed to fetch profile');
        return await response.json();
    },

    getStaffOrders: async (staffId) => {
        const response = await fetch(`${API_BASE}/staff/${staffId}/orders`, { 
            headers: api.getHeaders() 
        });
        if (!response.ok) throw new Error('Failed to fetch orders');
        return await response.json();
    },

    prepareOrder: async (staffId, orderId) => {
        const response = await fetch(`${API_BASE}/staff/${staffId}/orders/${orderId}/prepare`, {
            method: 'PUT',
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error('Failed to update status');
        return await response.json();
    },

    markOrderReady: async (staffId, orderId) => {
        const response = await fetch(`${API_BASE}/staff/${staffId}/orders/${orderId}/out-for-delivery`, {
            method: 'PUT',
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error('Failed to update status');
        return await response.json();
    },

    deleteCategory: async (id) => {
        const response = await fetch(`${API_BASE}/categories/delete/${id}`, {
            method: 'DELETE',
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error("Failed to delete category");
    },

    deleteProduct: async (id) => {
        const response = await fetch(`${API_BASE}/products/delete/${id}`, {
            method: 'DELETE',
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error("Failed to delete product");
    },
};