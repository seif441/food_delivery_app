/**
 * Staff Dashboard Controller
 * Dynamic Version with Mock Data Fallback
 */
const StaffDashboard = {
    staffId: null,
    currentFilter: 'all',
    
    // --- MOCK DATA (Fallback) ---
    mockData: [
        { 
            id: 101, 
            status: 'PENDING', 
            orderDate: new Date().toISOString(), 
            items: [{product: {name: 'Dbl Cheeseburger'}, quantity: 2}, {product: {name: 'Lg Fries'}, quantity: 1}], 
            note: 'No pickles' 
        },
        { 
            id: 102, 
            status: 'PREPARING', 
            orderDate: new Date(Date.now() - 1000 * 60 * 5).toISOString(), 
            items: [{product: {name: 'Pepperoni Pizza'}, quantity: 1}, {product: {name: 'Coke Zero'}, quantity: 2}], 
            note: '' 
        },
        { 
            id: 104, 
            status: 'PREPARED', 
            orderDate: new Date(Date.now() - 1000 * 60 * 15).toISOString(), 
            items: [{product: {name: 'Chicken Wings (12pc)'}, quantity: 1}], 
            note: 'Extra spicy' 
        }
    ],

    init() {
        // 1. GET USER FROM SESSION
        const user = api.getUser();
        
        if (!user) {
            console.warn("No user found in session. Redirecting to login.");
            window.location.href = 'auth.html';
            return;
        }

        // 2. SET ID DYNAMICALLY
        this.staffId = user.id; 

        // 3. SET NAME
        this.updateHeaderName(user.name || user.username || "Staff");

        // 4. FETCH FRESH PROFILE
        this.loadProfile();

        // 5. FETCH ORDERS (Real or Mock)
        this.fetchOrders();
        
        // 6. START POLLING
        setInterval(() => this.fetchOrders(), 10000);
        
        if(window.lucide) lucide.createIcons();
    },

    // --- PROFILE MANAGEMENT ---

    async loadProfile() {
        try {
            const staffProfile = await api.getStaffProfile(this.staffId);
            this.updateHeaderName(staffProfile.name || staffProfile.username);
        } catch (e) {
            console.error("Profile fetch failed, using session name.");
        }
    },

    updateHeaderName(name) {
        const nameDisplay = document.getElementById('staff-name-display');
        if(nameDisplay && name) {
            nameDisplay.innerText = name;
            nameDisplay.classList.remove('animate-pulse');
        }
    },

    // --- ORDER MANAGEMENT ---

    async fetchOrders() {
        try {
            // TRY REAL API
            const orders = await api.getStaffOrders(this.staffId);
            this.renderBoard(orders);
        } catch (error) {
            console.warn("API Failed. Using MOCK DATA.");
            // FALLBACK TO MOCK DATA
            this.renderBoard(this.mockData);
        }
    },

    async prepareOrder(orderId) {
        // UI Animation
        this.animateAction(orderId, 'ignite');

        // Check if it's a mock ID (usually > 100 in our mock data, or handled via catch)
        setTimeout(async () => {
            try {
                await api.prepareOrder(this.staffId, orderId);
                // Success: remove card and refresh
                this.animateAction(orderId, 'remove');
                setTimeout(() => this.fetchOrders(), 200);
            } catch(e) {
                // If API fails (e.g. using Mock Data), simulate success locally
                console.warn("API Error. Simulating local update on mock data.");
                const order = this.mockData.find(o => o.id === orderId);
                if(order) {
                     order.status = 'PREPARING';
                     this.animateAction(orderId, 'remove');
                     setTimeout(() => this.renderBoard(this.mockData), 200);
                } else {
                    alert("Failed to connect to backend.");
                }
            }
        }, 1000);
    },

    async markReady(orderId) {
        this.animateAction(orderId, 'remove');
        
        setTimeout(async () => {
            try {
                await api.markOrderReady(this.staffId, orderId);
                this.fetchOrders();
            } catch(e) {
                console.warn("API Error. Simulating local update on mock data.");
                const order = this.mockData.find(o => o.id === orderId);
                if(order) {
                    order.status = 'PREPARED';
                    this.renderBoard(this.mockData);
                }
            }
        }, 200);
    },

    logout() {
        if(confirm("Are you sure you want to log out?")) {
            api.clearUser();
            window.location.href = 'auth.html';
        }
    },

    // --- ANIMATIONS ---
    animateAction(orderId, type) {
        const card = document.getElementById(`ticket-${orderId}`);
        const btn = document.getElementById(`btn-cook-${orderId}`);

        if (type === 'ignite' && btn && card) {
             btn.innerHTML = `<i data-lucide="flame" class="w-4 h-4 animate-bounce"></i> Igniting...`;
             btn.className = "bg-orange-600 text-white py-2 rounded-lg font-bold text-sm transition w-full shadow-lg shadow-orange-500/50";
             if(window.lucide) lucide.createIcons();
             
             card.classList.add('animate-sizzle');
             this.spawnSteam(card);
        } else if (type === 'remove' && card) {
            card.classList.remove('animate-sizzle');
            card.classList.add('animate-scale-out');
        }
    },

    spawnSteam(card) {
        for (let i = 0; i < 6; i++) {
            setTimeout(() => {
                const steam = document.createElement('div');
                steam.className = 'steam-particle';
                const size = Math.random() * 10 + 10;
                steam.style.width = `${size}px`;
                steam.style.height = `${size}px`;
                steam.style.left = `${Math.random() * 80 + 10}%`;
                steam.style.top = '60%'; 
                card.appendChild(steam);
                setTimeout(() => steam.remove(), 1000);
            }, i * 150);
        }
    },

    filterView(filter) {
        this.currentFilter = filter;
        ['all', 'pending', 'preparing', 'completed'].forEach(id => {
            const btn = document.getElementById(`btn-${id}`);
            if(!btn) return;
            if (id === filter) btn.className = "px-4 py-2 rounded-lg text-sm font-bold bg-gray-900 text-white transition-colors shadow-md transform scale-105";
            else btn.className = "px-4 py-2 rounded-lg text-sm font-bold bg-white text-gray-600 hover:bg-gray-50 border border-gray-200 transition-colors";
        });
        
        // Re-run render logic. If we were using mock data, this uses mock data.
        // If we were using API, it triggers a fetch.
        this.fetchOrders();
    },

    renderBoard(orders) {
        const cols = {
            pending: document.getElementById('col-pending'),
            preparing: document.getElementById('col-preparing'),
            ready: document.getElementById('col-ready')
        };
        
        if(!cols.pending) return;
        Object.values(cols).forEach(el => el.innerHTML = '');

        let counts = { pending: 0, preparing: 0, ready: 0 };

        if(Array.isArray(orders)) {
            orders.forEach(order => {
                const status = (order.status || '').toLowerCase(); 
                let targetCol = null;
                
                if (status === 'pending') { targetCol = cols.pending; counts.pending++; }
                else if (status === 'preparing') { targetCol = cols.preparing; counts.preparing++; }
                else if (status === 'prepared' || status === 'delivered') { targetCol = cols.ready; counts.ready++; }

                let shouldShow = false;
                if (this.currentFilter === 'all') shouldShow = true;
                else if (this.currentFilter === 'pending' && status === 'pending') shouldShow = true;
                else if (this.currentFilter === 'preparing' && status === 'preparing') shouldShow = true;
                else if (this.currentFilter === 'completed' && (status === 'prepared' || status === 'delivered')) shouldShow = true;

                if (shouldShow && targetCol) {
                    targetCol.appendChild(this.createTicket(order));
                }
            });
        }

        const countPending = document.getElementById('count-pending');
        const countPreparing = document.getElementById('count-preparing');
        const countReady = document.getElementById('count-ready');

        if(countPending) countPending.innerText = counts.pending;
        if(countPreparing) countPreparing.innerText = counts.preparing;
        if(countReady) countReady.innerText = counts.ready;
        
        if(window.lucide) lucide.createIcons();
    },

    createTicket(order) {
        // TIME FORMATTING
        let timeDisplay = '--:--';
        if(order.orderDate) {
            if(Array.isArray(order.orderDate)) {
                 const hour = order.orderDate[3].toString().padStart(2, '0');
                 const minute = order.orderDate[4].toString().padStart(2, '0');
                 timeDisplay = `${hour}:${minute}`;
            } else {
                try {
                    const date = new Date(order.orderDate);
                    timeDisplay = date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
                } catch(e) { timeDisplay = 'Now'; }
            }
        }

        const div = document.createElement('div');
        const statusLower = (order.status || '').toLowerCase();
        
        let borderClass = statusLower === 'pending' ? 'status-border-pending' : 
                          statusLower === 'preparing' ? 'status-border-preparing' : 'status-border-ready';

        // ACTIONS
        let actionsHtml = '';
        if (statusLower === 'pending') {
            actionsHtml = `<div class="mt-4"><button id="btn-cook-${order.id}" onclick="StaffDashboard.prepareOrder(${order.id})" class="w-full bg-gray-900 text-white py-2 rounded-lg font-bold text-sm hover:bg-gray-800 transition shadow-lg shadow-gray-200">Start Cook</button></div>`;
        } else if (statusLower === 'preparing') {
            actionsHtml = `<div class="mt-4"><button onclick="StaffDashboard.markReady(${order.id})" class="w-full bg-blue-600 text-white py-2 rounded-lg font-bold text-sm hover:bg-blue-700 transition shadow-lg shadow-blue-200 flex justify-center items-center gap-2"><i data-lucide="check" class="w-4 h-4"></i> Ready for Driver</button></div>`;
        } else {
            actionsHtml = `<div class="mt-4 text-center"><span class="text-green-600 font-bold text-sm flex items-center justify-center gap-1"><i data-lucide="check-circle" class="w-4 h-4"></i> Assigned to Driver</span></div>`;
        }

        div.id = `ticket-${order.id}`;
        div.className = `bg-white p-4 rounded-xl shadow-sm border border-gray-100 animate-slide-up ${borderClass} mb-4 last:mb-0 relative overflow-hidden`;
        
        const itemsHtml = (order.items || []).map(item => `
            <div class="flex justify-between items-center">
                <span class="font-bold text-gray-800 text-lg">${item.quantity}x</span>
                <span class="text-gray-700 font-medium flex-1 ml-3 truncate">
                    ${item.product ? item.product.name : 'Unknown Item'}
                </span>
            </div>
        `).join('');

        div.innerHTML = `
            <div class="flex justify-between items-start mb-3 relative z-10">
                <div>
                    <span class="text-xs font-bold text-gray-400 uppercase tracking-wider">Order #${order.id}</span>
                    <div class="text-sm font-medium text-gray-500 flex items-center gap-1 mt-1">
                        <i data-lucide="clock" class="w-3 h-3"></i> ${timeDisplay}
                    </div>
                </div>
            </div>
            <div class="space-y-2 mb-4 border-t border-b border-gray-50 py-3 relative z-10">${itemsHtml}</div>
            ${order.note ? `<div class="bg-yellow-50 border border-yellow-100 p-3 rounded-lg text-sm text-yellow-800 mb-3 relative z-10"><span class="font-bold">Note:</span> ${order.note}</div>` : ''}
            <div class="relative z-10">${actionsHtml}</div>
        `;
        return div;
    }
};

document.addEventListener('DOMContentLoaded', () => StaffDashboard.init());