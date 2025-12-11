const API_BASE = '/api';
const api = {
    // --- Auth Headers ---
    getHeaders: () => {
        const user = JSON.parse(localStorage.getItem('user'));
        const headers = { 'Content-Type': 'application/json' };
        if (user && user.token) {
            headers['Authorization'] = `Bearer ${user.token}`;
        }
        return headers;
    },

    // --- Authentication ---
    login: async (email, password) => {
        const response = await fetch(`${API_BASE}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        
        if (!response.ok) {
            // Read error text from backend
            const errorMsg = await response.text(); 
            throw new Error(errorMsg || 'Login failed');
        }
        return await response.json(); // Returns the User Object
    },

    // Register Customer
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

    // --- Categories (CategoryService) ---
getAllCategories: async () => {
        // FIXED: Added "/all" to match Java Controller
        const response = await fetch(`${API_BASE}/categories/all`); 
        if (!response.ok) return [];
        return await response.json();
    },

    // --- Products (ProductService) ---
getAllProducts: async () => {
        // FIXED: Added "/all" to match Java Controller
        const response = await fetch(`${API_BASE}/products/all`);
        if (!response.ok) return [];
        return await response.json();
    },

    getProductsByCategory: async (categoryId) => {
        const response = await fetch(`${API_BASE}/products/category/${categoryId}`);
        if (!response.ok) return [];
        return await response.json();
    },

    // --- Cart (CartService) ---
    // Maps to CartService.addItemToCart(CartId, ProductId, quantity)
    addToCart: async (cartId, productId, quantity) => {
        const response = await fetch(`${API_BASE}/cart/${cartId}/add?productId=${productId}&quantity=${quantity}`, {
            method: 'POST',
            headers: api.getHeaders()
        });
        if (!response.ok) throw new Error('Failed to add to cart');
        return await response.json();
    },

    // --- Admin/Staff Services ---
    addStaffMember: async (userData, roleName) => {
        const response = await fetch(`${API_BASE}/admin/add-staff?role=${roleName}`, {
            method: 'POST',
            headers: api.getHeaders(),
            body: JSON.stringify(userData)
        });
        if (!response.ok) throw new Error('Failed to create staff');
        return await response.json();
    },

    addProduct: async (productDTO) => {
        const response = await fetch(`${API_BASE}/admin/products`, {
            method: 'POST',
            headers: api.getHeaders(),
            body: JSON.stringify(productDTO)
        });
        if (!response.ok) throw new Error('Failed to add product');
        return await response.json();
    },

    viewAllOrders: async () => {
        const response = await fetch(`${API_BASE}/staff/orders`, { headers: api.getHeaders() });
        return response.ok ? await response.json() : [];
    },

    updateOrderStatus: async (orderId, status) => {
        const response = await fetch(`${API_BASE}/staff/orders/${orderId}/status`, {
            method: 'PUT',
            headers: api.getHeaders(),
            body: JSON.stringify({ status })
        });
        return await response.json();
    }
};