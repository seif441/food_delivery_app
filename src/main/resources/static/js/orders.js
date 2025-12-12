const OrderManager = {
    currentTab: 'active',
    orders: [],
    API_BASE_URL: '/api/orders', 

    init() {
        if (window.lucide) lucide.createIcons();
        this.start();
    },

    async start() {
        const userId = await this.getUserId();
        
        if (userId) {
            this.fetchOrders(userId);
        } else {
            this.showLoginPrompt();
        }
    },

    // --- SMART ID FETCHING ---
    async getUserId() {
        // 1. Check URL parameter (e.g. orders.html?id=2)
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has('id')) {
            console.log("Using ID from URL:", urlParams.get('id'));
            return urlParams.get('id');
        }

        // 2. Check LocalStorage (Standard Login)
        const storedId = localStorage.getItem('userId');
        if (storedId) {
            console.log("Using ID from LocalStorage:", storedId);
            return storedId;
        }

        // 3. AUTO-DETECT FROM DATABASE (Requested Feature)
        // This fetches all orders, picks the first customer found, and uses their ID.
        console.log("No ID found. Auto-detecting a user from database...");
        try {
            const response = await fetch(`${this.API_BASE_URL}/all`); // Admin endpoint
            if (response.ok) {
                const allOrders = await response.json();
                if (allOrders.length > 0 && allOrders[0].customer) {
                    const foundId = allOrders[0].customer.id;
                    console.log(`Auto-detected Customer ID: ${foundId}`);
                    // Save it so we don't have to search next time
                    localStorage.setItem('userId', foundId); 
                    return foundId;
                }
            }
        } catch (e) {
            console.warn("Could not auto-detect user:", e);
        }

        return null; // Could not find anyone
    },

    async fetchOrders(userId) {
        const list = document.getElementById('orders-list');
        const loading = document.getElementById('loading-state');
        const errorState = document.getElementById('error-state');

        if (list) list.innerHTML = '';
        if (loading) loading.classList.remove('hidden');
        if (errorState) errorState.classList.add('hidden');

        try {
            console.log(`Fetching orders for User ${userId}...`);
            const response = await fetch(`${this.API_BASE_URL}/customer/${userId}`);
            
            if (!response.ok) {
                throw new Error(`Server Error (${response.status})`);
            }
            
            this.orders = await response.json();
            
            // Sort: Newest first
            this.orders.sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate));

            this.renderOrders(this.currentTab);

        } catch (error) {
            console.error("Error loading orders:", error);
            if (loading) loading.classList.add('hidden');
            if (errorState) {
                errorState.classList.remove('hidden');
                document.getElementById('error-message').innerHTML = `
                    <strong class="text-red-600">Connection Error</strong><br>
                    ${error.message}<br>
                    <span class="text-xs text-gray-500">Check your backend console.</span>
                `;
            }
        } finally {
            if (loading) loading.classList.add('hidden');
        }
    },

    showLoginPrompt() {
        const list = document.getElementById('orders-list');
        const loading = document.getElementById('loading-state');
        if (loading) loading.classList.add('hidden');
        
        if (list) {
            list.innerHTML = `
                <div class="flex flex-col items-center justify-center py-12 text-center animate-fade-in">
                    <div class="bg-gray-100 p-6 rounded-full mb-4">
                        <i data-lucide="user-x" class="w-10 h-10 text-gray-400"></i>
                    </div>
                    <h3 class="text-lg font-bold text-gray-900">No Orders Found</h3>
                    <p class="text-gray-500 text-sm mt-1 mb-6">We couldn't find any users in the database.</p>
                    <div class="bg-yellow-50 p-4 rounded-xl text-xs text-left text-yellow-800 max-w-xs mx-auto mb-4 border border-yellow-100">
                        <strong>Tip:</strong> Run the SQL script to create orders. <br>
                        Once orders exist, this page will automatically find the user ID.
                    </div>
                </div>
            `;
            if (window.lucide) lucide.createIcons();
        }
    },

    // --- HELPERS ---

    renderOrders(tab) {
        const list = document.getElementById('orders-list');
        if (!list) return;

        list.innerHTML = ''; 

        // Active = Pending, Prepared, Out For Delivery
        const activeStatuses = ['PENDING', 'PREPARED', 'OUT_FOR_DELIVERY'];
        
        const filteredOrders = this.orders.filter(o => {
            if (tab === 'active') return activeStatuses.includes(o.status);
            return !activeStatuses.includes(o.status); // Past = Delivered, Cancelled
        });

        if (filteredOrders.length === 0) {
            list.innerHTML = `
                <div class="flex flex-col items-center justify-center py-12 text-center animate-fade-in">
                    <div class="bg-gray-100 p-6 rounded-full mb-4">
                        <i data-lucide="shopping-bag" class="w-10 h-10 text-gray-400"></i>
                    </div>
                    <h3 class="text-lg font-bold text-gray-900">No ${tab} orders</h3>
                    <p class="text-gray-500 text-sm mt-1 mb-6">No orders in this category.</p>
                    <button onclick="window.location.href='index.html'" class="bg-orange-600 text-white px-6 py-2.5 rounded-xl font-bold hover:bg-orange-700 transition shadow-lg shadow-orange-200">
                        Browse Menu
                    </button>
                </div>
            `;
            if (window.lucide) lucide.createIcons();
            return;
        }

        filteredOrders.forEach((order, index) => {
            const delay = index * 50; 
            const items = order.items || [];
            
            // Format Items text
            const itemNames = items.map(i => {
                const name = i.product ? i.product.name : 'Unknown Item';
                return `${name} (x${i.quantity})`;
            }).join(', ');

            const itemCount = items.reduce((sum, item) => sum + item.quantity, 0);

            // Image handling
            const firstImage = (items[0] && items[0].product && items[0].product.imageUrl) 
                ? items[0].product.imageUrl 
                : 'https://placehold.co/100?text=Food'; 

            const canCancel = order.status === 'PENDING';
            const canTrack = ['PREPARED', 'OUT_FOR_DELIVERY'].includes(order.status);

            const card = document.createElement('div');
            card.className = `bg-white p-5 rounded-2xl border border-gray-100 shadow-sm animate-fade-in hover:shadow-md transition-shadow duration-300`;
            card.style.animationDelay = `${delay}ms`;

            card.innerHTML = `
                <div class="flex justify-between items-start mb-4">
                    <div class="flex gap-4">
                        <img src="${firstImage}" class="w-12 h-12 rounded-xl object-cover border border-gray-100" onerror="this.src='https://placehold.co/100?text=Food'">
                        <div>
                            <h3 class="font-bold text-gray-900">Order #${order.id}</h3>
                            <p class="text-xs text-gray-500 mt-1">${this.formatDate(order.orderDate)}</p>
                        </div>
                    </div>
                    <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold ring-1 ring-inset ${this.getStatusStyles(order.status)}">
                        <i data-lucide="${this.getStatusIcon(order.status)}" class="w-3 h-3"></i>
                        ${this.getStatusLabel(order.status)}
                    </span>
                </div>
                
                <div class="border-t border-b border-gray-50 py-3 my-3">
                    <p class="text-sm text-gray-600 line-clamp-2 leading-relaxed">
                        <span class="font-medium text-gray-900">${itemCount} items:</span> ${itemNames}
                    </p>
                </div>

                <div class="flex items-center justify-between mt-2">
                    <span class="font-extrabold text-gray-900">$${order.totalPrice.toFixed(2)}</span>
                    <div class="flex gap-2">
                         ${canCancel ? `
                            <button onclick="OrderManager.cancelOrder(${order.id})" class="px-4 py-2 bg-red-50 text-red-600 text-sm font-bold rounded-lg hover:bg-red-100 transition">
                                Cancel
                            </button>
                        ` : ''}
                         ${canTrack ? `
                            <button class="px-4 py-2 bg-orange-600 text-white text-sm font-bold rounded-lg hover:bg-orange-700 transition flex items-center gap-2 shadow-lg shadow-orange-100">
                                Track Order
                            </button>
                        ` : ''}
                    </div>
                </div>
            `;
            list.appendChild(card);
        });
        
        if (window.lucide) lucide.createIcons();
    },

    getStatusStyles(status) {
        const styles = {
            'PENDING': 'bg-blue-50 text-blue-600 ring-blue-500/20',
            'PREPARED': 'bg-yellow-50 text-yellow-600 ring-yellow-500/20',
            'OUT_FOR_DELIVERY': 'bg-orange-50 text-orange-600 ring-orange-500/20',
            'DELIVERED': 'bg-gray-100 text-gray-600 ring-gray-500/20',
            'CANCELLED': 'bg-red-50 text-red-600 ring-red-500/20'
        };
        return styles[status] || 'bg-gray-100 text-gray-600';
    },

    getStatusLabel(status) {
        const labels = { 'PENDING': 'Order Placed', 'PREPARED': 'Preparing', 'OUT_FOR_DELIVERY': 'On the way', 'DELIVERED': 'Delivered', 'CANCELLED': 'Cancelled' };
        return labels[status] || status;
    },

    getStatusIcon(status) {
        const icons = { 'PENDING': 'clock', 'PREPARED': 'chef-hat', 'OUT_FOR_DELIVERY': 'bike', 'DELIVERED': 'check-circle-2', 'CANCELLED': 'x-circle' };
        return icons[status] || 'info';
    },

    formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return new Intl.DateTimeFormat('en-US', { month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' }).format(date);
    },
    
    // --- ACTIONS ---
    
    switchTab(tab) {
        this.currentTab = tab;
        const activeBtn = document.getElementById('tab-active');
        const pastBtn = document.getElementById('tab-past');
        const indicator = document.getElementById('tab-indicator');
        
        if (tab === 'active') {
            indicator.style.transform = 'translateX(0)';
            activeBtn.classList.replace('text-gray-500', 'text-gray-900');
            pastBtn.classList.replace('text-gray-900', 'text-gray-500');
        } else {
            indicator.style.transform = 'translateX(100%) translateX(8px)';
            pastBtn.classList.replace('text-gray-500', 'text-gray-900');
            activeBtn.classList.replace('text-gray-900', 'text-gray-500');
        }
        this.renderOrders(tab);
    },

    async cancelOrder(orderId) {
        if(!confirm("Cancel this order?")) return;
        try {
            const response = await fetch(`${this.API_BASE_URL}/${orderId}`, { method: 'DELETE' });
            if (response.ok) this.start();
            else alert("Could not cancel order.");
        } catch (e) {
            console.error(e);
            alert("Error connecting to server");
        }
    }
};

document.addEventListener('DOMContentLoaded', () => OrderManager.init());