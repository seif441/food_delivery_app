
        /**
         * Orders Page Logic
         * Handles rendering, tab switching, and interactions.
         * Expects a global `orders` array to be present.
         */

        const OrderManager = {
            currentTab: 'active',
            
            init() {
                // Initialize icons
                if (window.lucide) lucide.createIcons();
                
                // Check if data exists
                if (typeof window.orders === 'undefined' || !Array.isArray(window.orders)) {
                    console.warn("OrderManager: No 'orders' data found. Initializing with empty array.");
                    window.orders = [];
                }

                // Initial Render
                this.renderOrders(this.currentTab);
            },

            // --- Helpers for UI Styles ---

            getStatusStyles(status) {
                const styles = {
                    'preparing': 'bg-blue-50 text-blue-600 ring-blue-500/20',
                    'delivering': 'bg-orange-50 text-orange-600 ring-orange-500/20',
                    'delivered': 'bg-gray-100 text-gray-600 ring-gray-500/20',
                    'cancelled': 'bg-red-50 text-red-600 ring-red-500/20'
                };
                return styles[status] || 'bg-gray-100 text-gray-600';
            },

            getStatusLabel(status) {
                const labels = {
                    'preparing': 'Preparing',
                    'delivering': 'On the way',
                    'delivered': 'Delivered',
                    'cancelled': 'Cancelled'
                };
                return labels[status] || status;
            },

            getStatusIcon(status) {
                const icons = {
                    'preparing': 'chef-hat',
                    'delivering': 'bike',
                    'delivered': 'check-circle-2',
                    'cancelled': 'x-circle'
                };
                return icons[status] || 'info';
            },

            // --- Core Rendering ---

            renderOrders(tab) {
                const list = document.getElementById('orders-list');
                if (!list) return;

                list.innerHTML = ''; // Clear current

                // Filter logic
                const filteredOrders = window.orders.filter(o => {
                    if (tab === 'active') return ['preparing', 'delivering'].includes(o.status);
                    return ['delivered', 'cancelled'].includes(o.status);
                });

                // Empty State
                if (filteredOrders.length === 0) {
                    list.innerHTML = `
                        <div class="flex flex-col items-center justify-center py-12 text-center animate-fade-in">
                            <div class="bg-gray-100 p-6 rounded-full mb-4">
                                <i data-lucide="shopping-bag" class="w-10 h-10 text-gray-400"></i>
                            </div>
                            <h3 class="text-lg font-bold text-gray-900">No ${tab} orders</h3>
                            <p class="text-gray-500 text-sm mt-1 mb-6">Looks like you haven't ordered anything yet.</p>
                            <button onclick="window.location.href='index.html'" class="bg-orange-600 text-white px-6 py-2.5 rounded-xl font-bold hover:bg-orange-700 transition shadow-lg shadow-orange-200">
                                Start Craving
                            </button>
                        </div>
                    `;
                    if (window.lucide) lucide.createIcons();
                    return;
                }

                // Generate Cards
                filteredOrders.forEach((order, index) => {
                    // Staggered animation delay based on index
                    const delay = index * 75;
                    
                    const card = document.createElement('div');
                    card.className = `bg-white p-5 rounded-2xl border border-gray-100 shadow-sm animate-fade-in hover:shadow-md transition-shadow duration-300`;
                    card.style.animationDelay = `${delay}ms`;
                    
                    const itemString = order.items.join(', ');
                    const isReorderable = order.status === 'delivered';

                    card.innerHTML = `
                        <div class="flex justify-between items-start mb-4">
                            <div class="flex gap-4">
                                <img src="${order.logo}" class="w-12 h-12 rounded-xl object-cover border border-gray-100" onerror="this.src='https://placehold.co/100?text=Food'">
                                <div>
                                    <h3 class="font-bold text-gray-900">${order.restaurant}</h3>
                                    <p class="text-xs text-gray-500 mt-1">${order.date}</p>
                                </div>
                            </div>
                            <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold ring-1 ring-inset ${this.getStatusStyles(order.status)}">
                                <i data-lucide="${this.getStatusIcon(order.status)}" class="w-3 h-3"></i>
                                ${this.getStatusLabel(order.status)}
                            </span>
                        </div>
                        
                        <div class="border-t border-b border-gray-50 py-3 my-3">
                            <p class="text-sm text-gray-600 line-clamp-2 leading-relaxed">
                                <span class="font-medium text-gray-900">${order.itemCount} items:</span> ${itemString}
                            </p>
                        </div>

                        <div class="flex items-center justify-between mt-2">
                            <span class="font-extrabold text-gray-900">$${order.total.toFixed(2)}</span>
                            
                            <div class="flex gap-2">
                                 ${isReorderable ? `
                                    <button class="px-4 py-2 bg-orange-50 text-orange-700 text-sm font-bold rounded-lg hover:bg-orange-100 transition" onclick="OrderManager.reorder('${order.restaurant}')">
                                        Reorder
                                    </button>
                                ` : ''}
                                 ${['preparing', 'delivering'].includes(order.status) ? `
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

            // --- Interaction Functions ---

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

            reorder(restaurant) {
                // Mock reorder functionality
                alert(`Adding favorites from ${restaurant} to your cart!`);
                window.location.href = 'index.html';
            }
        };

        // Initialize when DOM is ready
        document.addEventListener('DOMContentLoaded', () => {
            OrderManager.init();
        });
