let state = {
    activeTab: 'menu',
    isSidebarOpen: false,
    modal: { isOpen: false, type: null, item: null, catId: null },
    categories: [], 
    staff: [],      
    orders: [       
        { id: '#ORD-7782', customer: 'Alice Freeman', total: 45.20, status: 'Cooking', items: 3, time: '12 mins ago' },
        { id: '#ORD-7781', customer: 'Bob Vance', total: 22.50, status: 'Ready', items: 1, time: '25 mins ago' },
        { id: '#ORD-7780', customer: 'Phyllis L.', total: 112.00, status: 'Delivered', items: 8, time: '1 hour ago' },
    ], 
    stats: [
       { label: 'Total Revenue', value: '$12,450', change: '+12%', icon: 'dollar-sign', color: 'bg-emerald-100 text-emerald-600' },
       { label: 'Active Orders', value: '18', change: '-2%', icon: 'clock', color: 'bg-orange-100 text-orange-600' },
       { label: 'Pending Delivery', value: '5', change: '0%', icon: 'shopping-bag', color: 'bg-blue-100 text-blue-600' },
       { label: 'Total Staff', value: '0', change: '0%', icon: 'users', color: 'bg-purple-100 text-purple-600' },
    ]
};

document.addEventListener('DOMContentLoaded', () => {
    loadAllData();
});

async function loadAllData() {
    try {
        const [cats, prods] = await Promise.all([
            api.getAllCategories(),
            api.getAllProducts()
        ]);

        state.categories = cats.map(c => {
            return {
                id: c.id,
                name: c.description || c.name || `Category ${c.id}`,
                icon: c.icon || '🍽️', 
                products: prods.filter(p => p.category && p.category.id === c.id).map(p => ({
                    id: p.id,
                    name: p.name,
                    price: p.price,
                    description: `Product ID: ${p.id}`, 
                    available: p.available,
                    image: p.imageUrl || 'https://via.placeholder.com/300?text=No+Image'
                }))
            };
        });
    } catch (e) { console.error("Menu Load Error", e); }

    try {
        const users = await api.getAllUsers();
        state.staff = users.map(u => ({
            id: u.id,
            name: u.name,
            email: u.email,
            role: u.role ? (u.role.roleName || u.role.name) : 'No Role',
            avatar: u.name ? u.name.substring(0,2).toUpperCase() : 'UR'
        }));
        state.stats[3].value = state.staff.length.toString();
    } catch (e) { console.error("Staff Load Error", e); }

    render();
}

function render() {
    renderSidebar();
    renderHeader();
    renderMainContent();
    lucide.createIcons();
}

function renderSidebar() {
    document.querySelectorAll('.nav-item').forEach(btn => {
        btn.className = 'nav-item w-full flex items-center space-x-3 px-4 py-3 rounded-lg transition-all duration-200 text-slate-400 hover:bg-slate-800 hover:text-white';
    });
    const activeBtn = document.getElementById('nav-' + state.activeTab);
    if(activeBtn) {
        activeBtn.className = 'nav-item w-full flex items-center space-x-3 px-4 py-3 rounded-lg transition-all duration-200 bg-gradient-to-r from-orange-600 to-orange-500 text-white shadow-lg shadow-orange-500/30';
    }
    
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('mobile-overlay');
    if (state.isSidebarOpen) {
        sidebar.classList.remove('-translate-x-full');
        overlay.classList.remove('hidden');
    } else {
        sidebar.classList.add('-translate-x-full');
        overlay.classList.add('hidden');
    }
}

function renderHeader() {
    const titles = { dashboard: 'Dashboard Overview', menu: 'Menu Management', orders: 'Live Orders', staff: 'Team Members' };
    document.getElementById('header-title').innerHTML = `<h2 class="text-xl md:text-2xl font-bold text-gray-900">${titles[state.activeTab]}</h2>`;

    const actionsDiv = document.getElementById('header-actions');
    let actionHtml = '';

    if (state.activeTab === 'menu') {
        actionHtml = `<button onclick="openModal('addCategory')" class="bg-slate-900 hover:bg-slate-800 text-white px-4 py-2 rounded-xl flex items-center space-x-2 shadow-lg shadow-slate-900/20 transition-all text-sm md:text-base"><i data-lucide="folder-plus" class="w-4 h-4 md:w-5 md:h-5"></i><span class="font-medium hidden md:inline">New Category</span></button>`;
    } else if (state.activeTab === 'staff') {
        actionHtml = `<button onclick="openModal('addStaff')" class="bg-orange-600 hover:bg-orange-700 text-white px-4 py-2 rounded-xl flex items-center space-x-2 shadow-lg shadow-orange-600/20 transition-all text-sm md:text-base"><i data-lucide="user-plus" class="w-4 h-4 md:w-5 md:h-5"></i><span class="font-medium hidden md:inline">Add Employee</span></button>`;
    }
    actionHtml += `<div class="h-8 w-8 rounded-full bg-orange-100 text-orange-700 flex items-center justify-center font-bold text-sm border-2 border-orange-200">AD</div>`;
    actionsDiv.innerHTML = actionHtml;
}

function renderMainContent() {
    const container = document.getElementById('main-content');
    if (state.activeTab === 'dashboard') container.innerHTML = getDashboardHtml();
    else if (state.activeTab === 'menu') container.innerHTML = getMenuHtml();
    else if (state.activeTab === 'orders') container.innerHTML = getOrdersHtml();
    else if (state.activeTab === 'staff') container.innerHTML = getStaffHtml();
}

function getDashboardHtml() {
    const statsHtml = state.stats.map(stat => `
        <div class="bg-white rounded-xl border border-gray-100 shadow-sm p-6 flex items-center justify-between hover:shadow-md transition-shadow">
            <div><p class="text-sm font-medium text-gray-500 mb-1">${stat.label}</p><h2 class="text-2xl font-bold text-gray-800">${stat.value}</h2><p class="text-xs font-medium mt-2 flex items-center ${stat.change.startsWith('+') ? 'text-emerald-600' : 'text-red-500'}">${stat.change}</p></div>
            <div class="p-3 rounded-full ${stat.color} bg-opacity-20"><i data-lucide="${stat.icon}" class="w-6 h-6"></i></div>
        </div>`).join('');
    
    return `<div class="space-y-6 animate-fade-in-up"><div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">${statsHtml}</div>
        <div class="bg-white rounded-xl border border-gray-100 shadow-sm p-8 text-center"><h3 class="text-lg font-medium text-gray-500">System Ready. Data will populate as activity occurs.</h3></div></div>`;
}

function getMenuHtml() {
    if (state.categories.length === 0) {
        return `<div class="text-center py-20 bg-white rounded-2xl border-2 border-dashed border-gray-200 animate-fade-in-up">
                <div class="bg-gray-50 p-4 rounded-full inline-block mb-4"><i data-lucide="layout-grid" class="w-8 h-8 text-gray-300"></i></div>
                <h3 class="text-lg font-medium text-gray-900">No categories found</h3>
                <p class="text-gray-500 mb-6">Create a category to get started.</p>
                <button onclick="openModal('addCategory')" class="text-orange-600 font-medium hover:underline">Create Category</button>
            </div>`;
    }

    return state.categories.map(cat => {
        const productsHtml = cat.products.length === 0 
            ? `<div onclick="openModal('addProduct', null, '${cat.id}')" class="col-span-full py-12 text-center bg-gray-50/50 rounded-xl border-2 border-dashed border-gray-200 group hover:border-orange-200 transition-colors cursor-pointer">
                <i data-lucide="plus-circle" class="w-8 h-8 text-gray-300 mx-auto mb-2 group-hover:text-orange-400"></i>
                <p class="text-gray-400 text-sm group-hover:text-orange-600">Add first product to ${cat.name}</p></div>`
            : cat.products.map(p => {
                return `<div class="group bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-xl hover:border-orange-100 transition-all duration-300 flex flex-col ${!p.available ? 'opacity-75 grayscale' : ''}">
                    <div class="h-48 w-full bg-gray-100 relative overflow-hidden">
                        <img src="${p.image}" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110" onerror="this.src='https://via.placeholder.com/300?text=No+Image'">
                        <div class="absolute top-3 right-3"><span class="px-2.5 py-1 rounded-md text-xs font-bold shadow-sm backdrop-blur-md ${p.available ? 'bg-white/90 text-green-700' : 'bg-gray-800/90 text-white'}">${p.available ? 'Active' : 'Hidden'}</span></div>
                    </div>
                    <div class="p-5 flex-1 flex flex-col">
                        <div class="flex justify-between items-start mb-2">
                            <h3 class="font-bold text-lg text-gray-800 leading-tight">${p.name}</h3>
                            <span class="font-bold text-orange-600 bg-orange-50 px-2 py-1 rounded-lg text-sm">$${p.price.toFixed(2)}</span>
                        </div>
                        <div class="flex items-center justify-between pt-4 border-t border-gray-50 mt-auto">
                            <span class="text-xs text-gray-400">ID: ${p.id}</span>
                            <button onclick="handleDeleteProduct('${p.id}')" class="p-2 text-red-500 hover:bg-red-50 rounded-lg"><i data-lucide="trash-2" class="w-4 h-4"></i></button>
                        </div>
                    </div>
                </div>`;
            }).join('');
        
        return `<div class="mb-10 animate-fade-in-up">
            <div class="flex items-center justify-between mb-6">
                <div class="flex items-center gap-3">
                    <span class="text-3xl">${cat.icon}</span>
                    <h3 class="text-xl font-bold text-gray-800 flex items-center gap-2">${cat.name} <span class="text-xs font-semibold bg-gray-200 text-gray-600 px-2.5 py-0.5 rounded-full">${cat.products.length}</span></h3>
                </div>
                <div class="flex items-center bg-white rounded-lg border border-gray-200 p-1 shadow-sm">
                    <button onclick="openModal('addProduct', null, '${cat.id}')" class="text-xs font-medium px-3 py-1.5 rounded-md hover:bg-gray-100 text-gray-600 flex items-center gap-1.5"><i data-lucide="plus" class="w-3.5 h-3.5"></i> Add Item</button>
                    <div class="w-px h-4 bg-gray-200 mx-1"></div>
                    <button onclick="handleDeleteCategory('${cat.id}')" class="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-md"><i data-lucide="trash" class="w-4 h-4"></i></button>
                </div>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">${productsHtml}</div>
        </div>`;
    }).join('');
}

function getStaffHtml() {
    const rows = state.staff.map(emp => {
        return `<tr class="group hover:bg-orange-50/30 transition-colors border-b border-gray-100 last:border-0">
            <td class="p-4 pl-6">
                <div class="flex items-center space-x-4">
                    <div class="w-10 h-10 rounded-full bg-gradient-to-br from-slate-700 to-slate-900 flex items-center justify-center text-white text-sm font-bold shadow-md ring-2 ring-white">${emp.avatar}</div>
                    <div><p class="font-semibold text-gray-900">${emp.name}</p><p class="text-xs text-gray-500">${emp.email}</p></div>
                </div>
            </td>
            <td class="p-4"><span style="background:#f3f4f6; padding:2px 8px; border-radius:4px; font-size:0.9em; font-weight:bold;">${emp.role}</span></td>
            <td class="p-4 pr-6 text-right">
                ${emp.role === 'ADMIN' ? '<span class="text-gray-400 text-xs">Protected</span>' : 
                `<button onclick="handleDeleteStaff('${emp.id}')" class="text-gray-400 hover:text-red-600 p-2"><i data-lucide="trash-2" class="w-4 h-4"></i></button>`}
            </td>
        </tr>`;
    }).join('');
    return `<div class="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden animate-fade-in-up">
        <table class="w-full text-left border-collapse">
            <thead class="bg-gray-50 border-b border-gray-200"><tr><th class="p-4 pl-6 text-xs font-semibold text-gray-500 uppercase">Employee</th><th class="p-4 text-xs font-semibold text-gray-500 uppercase">Role</th><th class="p-4 pr-6 text-right text-xs font-semibold text-gray-500 uppercase">Actions</th></tr></thead>
            <tbody class="divide-y divide-gray-100">${rows.length ? rows : '<tr><td colspan="3" class="p-10 text-center text-gray-400">No staff found via API.</td></tr>'}</tbody>
        </table>
    </div>`;
}

function getOrdersHtml() {
    if(state.orders.length === 0) {
        return `<div class="bg-white rounded-xl border border-gray-100 shadow-sm p-8 text-center"><h3 class="text-lg font-medium text-gray-500">No active orders found.</h3></div>`;
    }
    return `<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 animate-fade-in-up">${state.orders.map(o => `
        <div class="bg-white rounded-xl border border-gray-100 shadow-sm p-5 border-l-4 border-l-orange-500"><div class="flex justify-between items-start mb-4"><div><span class="text-xs font-bold text-gray-400 uppercase">${o.id}</span><h3 class="font-bold text-lg text-gray-800">${o.customer}</h3></div><span class="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-orange-100 text-orange-700">${o.status}</span></div><div class="flex justify-between items-center pt-4 border-t border-gray-100"><span class="font-bold text-xl text-gray-800">$${o.total.toFixed(2)}</span></div></div>`).join('')}</div>`;
}

async function handleFormSubmit(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const { type, catId } = state.modal;

    try {
        if (type === 'addStaff') {
            await api.addStaffMember({
                name: formData.get('name'),
                email: formData.get('email'),
                password: formData.get('password')
            }, formData.get('role'));
            alert("Staff Added");
        } 
        else if (type === 'addCategory') {
            const name = formData.get('catName');
            const icon = formData.get('icon'); // Get icon from hidden input
            await api.addCategory(name, icon);
            alert("Category Added");
        } 
        else if (type === 'addProduct') {
            await api.addProduct({
                name: formData.get('name'),
                price: parseFloat(formData.get('price')),
                imageUrl: formData.get('image'),
                categoryId: parseInt(catId),
                available: true
            });
            alert("Product Added");
        }
        closeModal();
        loadAllData(); 
    } catch (err) {
        alert("Operation failed: " + err.message);
    }
}

async function handleDeleteCategory(id) {
    if(!confirm("Delete Category?")) return;
    try { await api.deleteCategory(id); loadAllData(); } catch(e) { alert("Error deleting category"); }
}
async function handleDeleteProduct(id) {
    if(!confirm("Remove Product?")) return;
    try { await api.deleteProduct(id); loadAllData(); } catch(e) { alert("Error deleting product"); }
}
async function handleDeleteStaff(id) {
    if(!confirm("Remove User?")) return;
    try { await api.deleteUser(id); loadAllData(); } catch(e) { alert("Error deleting user"); }
}

function switchTab(tab) { state.activeTab = tab; state.isSidebarOpen = false; render(); }
function toggleSidebar() { state.isSidebarOpen = !state.isSidebarOpen; render(); }

// --- ICON SELECTION HELPER ---
function selectIcon(icon, btn) {
    document.getElementById('selectedIconInput').value = icon;
    document.querySelectorAll('.icon-btn').forEach(b => {
        b.classList.remove('bg-orange-100', 'border-orange-500', 'ring-2', 'ring-orange-200');
        b.classList.add('border-gray-200');
    });
    btn.classList.remove('border-gray-200');
    btn.classList.add('bg-orange-100', 'border-orange-500', 'ring-2', 'ring-orange-200');
}

function openModal(type, item = null, catId = null) {
    state.modal = { isOpen: true, type, item, catId };
    const container = document.getElementById('modal-container');
    const title = document.getElementById('modal-title');
    const body = document.getElementById('modal-body');
    
    container.classList.remove('hidden');
    setTimeout(() => { 
        container.classList.remove('opacity-0'); 
        document.getElementById('modal-panel').classList.remove('scale-95');
        document.getElementById('modal-panel').classList.add('scale-100');
    }, 10);

    if(type === 'addStaff') {
        title.innerText = 'Add Employee';
        body.innerHTML = `<form onsubmit="handleFormSubmit(event)" class="space-y-4">
            <div><label class="block text-xs font-bold text-gray-500 uppercase mb-1">Name</label><input name="name" required class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 outline-none"></div>
            <div><label class="block text-xs font-bold text-gray-500 uppercase mb-1">Email</label><input name="email" type="email" required class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 outline-none"></div>
            <div><label class="block text-xs font-bold text-gray-500 uppercase mb-1">Password</label><input name="password" type="password" required class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 outline-none"></div>
            <div><label class="block text-xs font-bold text-gray-500 uppercase mb-1">Role</label><select name="role" class="w-full px-3 py-2 border rounded-lg bg-white"><option value="STAFF">Kitchen Staff</option><option value="DELIVERY_STAFF">Delivery Driver</option></select></div>
            <button type="submit" class="w-full bg-slate-900 text-white py-3 rounded-lg font-bold hover:bg-slate-800 mt-2">Create Account</button>
        </form>`;
    } 
    else if (type === 'addCategory') {
        title.innerText = 'New Category';
        // Define available icons
        const icons = ['🍕', '🍔', '🌮', '🍣', '🥗', '🍩', '🥤', '☕', '🍗', '🍜', '🥪', '🥩'];
        const iconsHtml = icons.map(icon => 
            `<button type="button" onclick="selectIcon('${icon}', this)" class="icon-btn w-10 h-10 text-xl border border-gray-200 rounded-lg hover:bg-gray-50 transition-all flex items-center justify-center">${icon}</button>`
        ).join('');

        body.innerHTML = `<form onsubmit="handleFormSubmit(event)" class="space-y-4">
            <div>
                <label class="block text-xs font-bold text-gray-500 uppercase mb-1">Category Name</label>
                <input name="catName" required placeholder="e.g. Italian, Drinks" class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 outline-none">
            </div>
            <div>
                <label class="block text-xs font-bold text-gray-500 uppercase mb-2">Choose Icon</label>
                <div class="grid grid-cols-6 gap-2">
                    ${iconsHtml}
                </div>
                <input type="hidden" name="icon" id="selectedIconInput" value="🍽️">
            </div>
            <button type="submit" class="w-full bg-orange-600 text-white py-3 rounded-lg font-bold hover:bg-orange-700 mt-2">Create Category</button>
        </form>`;
    } 
    else if (type === 'addProduct') {
        title.innerText = 'Add Product';
        body.innerHTML = `<form onsubmit="handleFormSubmit(event)" class="space-y-4">
            <div><label class="block text-xs font-bold text-gray-500 uppercase mb-1">Name</label><input name="name" required class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 outline-none"></div>
            <div><label class="block text-xs font-bold text-gray-500 uppercase mb-1">Price</label><input name="price" type="number" step="0.01" required class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 outline-none"></div>
            <div><label class="block text-xs font-bold text-gray-500 uppercase mb-1">Image URL</label><input name="image" required class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 outline-none"></div>
            <button type="submit" class="w-full bg-orange-600 text-white py-3 rounded-lg font-bold hover:bg-orange-700 mt-2">Add Product</button>
        </form>`;
    }
}

function closeModal() {
    const container = document.getElementById('modal-container');
    container.classList.add('opacity-0');
    setTimeout(() => container.classList.add('hidden'), 300);
}