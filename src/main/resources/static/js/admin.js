document.addEventListener('DOMContentLoaded', () => {
    // Load data when page opens
    loadMenuByCategory();
    loadStaff();
    // NEW: Load categories for the "Add Product" dropdown
    loadCategoriesForSelect(); 
});

// ==========================================
// 1. MENU MANAGEMENT (Categories & Products)
// ==========================================

async function loadMenuByCategory() {
    const container = document.getElementById('categoriesContainer');
    if (!container) return;
    
    container.innerHTML = '<p>Loading menu...</p>';

    try {
        // Fetch Categories and Products using API wrapper
        const [categories, products] = await Promise.all([
            api.getAllCategories(),
            api.getAllProducts()
        ]);

        container.innerHTML = '';

        if (!categories || categories.length === 0) {
            container.innerHTML = '<p>No categories found. Create one above.</p>';
            return;
        }

        // Render Categories
        categories.forEach(cat => {
            // Filter products for this category
            const catProducts = products.filter(p => p.category && p.category.id === cat.id);

            const card = document.createElement('div');
            card.className = 'category-card';
            card.innerHTML = `
                <div class="category-header">
                    <h3>${cat.description || cat.name || 'Category #' + cat.id}</h3>
                    <div>
                        <button onclick="openProductModal(${cat.id}, '${cat.description || 'Category'}')" style="cursor:pointer;">+ Add Product</button>
                        <button onclick="deleteCategory(${cat.id})" style="color:red; margin-left:10px; cursor:pointer; border:none; background:none;">Delete</button>
                    </div>
                </div>
                <div class="category-body">
                    <div class="product-grid">
                        ${catProducts.length > 0 ? catProducts.map(p => `
                            <div class="product-item">
                                <img src="${p.imageUrl || 'https://via.placeholder.com/100'}" alt="Food" onerror="this.src='https://via.placeholder.com/100'">
                                <div><strong>${p.name}</strong></div>
                                <div style="color:green">$${p.price}</div>
                                <button onclick="deleteProduct(${p.id})" style="color:red; font-size:0.8rem; border:none; background:none; margin-top:5px; cursor:pointer;">Remove</button>
                            </div>
                        `).join('') : '<p style="color:#888; font-style:italic;">No products in this category.</p>'}
                    </div>
                </div>
            `;
            container.appendChild(card);
        });

    } catch (error) {
        console.error("Error loading menu:", error);
        container.innerHTML = '<p style="color:red">Failed to load menu.</p>';
    }
}

// --- Add Category ---
async function handleCategorySubmit(e) {
    e.preventDefault(); 
    const description = document.getElementById('catDesc').value;

    try {
        // MANUAL FIX: Send 'name' AND 'description' to satisfy the database
        const response = await fetch('/api/categories/add', {
            method: 'POST',
            headers: api.getHeaders(),
            body: JSON.stringify({ description: description, name: description }) 
        });

        if (!response.ok) throw new Error("Failed to add category");
        
        closeModal('categoryModal');
        document.getElementById('catDesc').value = ''; 
        loadMenuByCategory(); 
        alert("Category Added!");
    } catch (error) {
        console.error(error);
        alert("Error creating category: " + error.message);
    }
}

async function deleteCategory(id) {
    if(!confirm("Delete this category?")) return;
    try {
        // UPDATE: Use api.js wrapper
        await api.deleteCategory(id);
        loadMenuByCategory();
    } catch(e) { alert("Error deleting category"); }
}

// --- Add Product ---
function openProductModal(catId, catName) {
    // Set hidden ID
    const catInput = document.getElementById('pCategoryId');
    if(catInput) catInput.value = catId;
    
    // Set Title
    const title = document.getElementById('targetCategoryName');
    if(title) title.innerText = catName;

    document.getElementById('productModal').style.display = 'flex';
}

async function handleProductSubmit(e) {
    e.preventDefault();

    const productData = {
        name: document.getElementById('pName').value,
        price: parseFloat(document.getElementById('pPrice').value),
        imageUrl: document.getElementById('pImg').value,
        categoryId: parseInt(document.getElementById('pCategoryId').value),
        available: true
    };

    try {
        // UPDATE: Use api.js wrapper
        await api.addProduct(productData);
        
        closeModal('productModal');
        // Clear form
        document.getElementById('pName').value = ''; 
        document.getElementById('pPrice').value = '';
        document.getElementById('pImg').value = '';
        loadMenuByCategory();
        alert("Product Added!");
    } catch (error) {
        console.error(error);
        alert("Error adding product: " + error.message);
    }
}

async function deleteProduct(id) {
    if(!confirm("Remove this product?")) return;
    try {
        // MANUAL FIX: Updated URL to match ProductController
        await fetch(`/api/products/delete/${id}`, { method: 'DELETE', headers: api.getHeaders() });
        loadMenuByCategory();
    } catch(e) { alert("Error deleting product"); }
}

// NEW HELPER: Load categories into the invisible dropdown (if needed for reference)
async function loadCategoriesForSelect() {
    const select = document.getElementById('pCategory'); 
    if(!select) return;
    
    const categories = await api.getAllCategories();
    select.innerHTML = '';
    categories.forEach(c => {
        const option = document.createElement('option');
        option.value = c.id;
        option.text = c.description || c.name;
        select.appendChild(option);
    });
}


// ==========================================
// 2. STAFF & USER MANAGEMENT (Grouped)
// ==========================================

async function loadStaff() {
    const tbody = document.getElementById('staffTableBody');
    if (!tbody) return;

    try {
        // UPDATE: Use api.js wrapper
        const users = await api.getAllUsers();
        
        tbody.innerHTML = ''; 

        // Group users by Role Name
        const groupedUsers = {};
        users.forEach(user => {
            let rName = 'No Role';
            if (user.role) {
                rName = user.role.roleName || user.role.name || 'No Role';
            }
            const key = rName.toUpperCase(); 
            
            if (!groupedUsers[key]) {
                groupedUsers[key] = [];
            }
            groupedUsers[key].push({ ...user, displayRole: rName });
        });

        // Define order
        const roleOrder = ['ADMIN', 'STAFF', 'DELIVERY_STAFF', 'CUSTOMER', 'NO ROLE'];

        // Render
        roleOrder.forEach(roleKey => {
            const group = groupedUsers[roleKey] || [];
            
            if (group.length > 0) {
                // Header Row
                tbody.innerHTML += `
                    <tr style="background-color: #e5e7eb; font-weight: bold;">
                        <td colspan="5" style="text-align: center; color: #374151;">
                            ${roleKey} (${group.length})
                        </td>
                    </tr>
                `;

                // User Rows
                tbody.innerHTML += group.map(user => {
                    const isAdmin = roleKey === 'ADMIN';
                    const deleteBtn = isAdmin 
                        ? `<span style="color:#9ca3af; font-size:0.9em;">(Protected)</span>` 
                        : `<button onclick="deleteStaff(${user.id})" style="color:red; border:none; background:none; cursor:pointer;">Remove</button>`;
                    
                    return `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.name}</td>
                        <td>${user.email}</td>
                        <td><span style="background:#f3f4f6; padding:2px 8px; border-radius:4px; font-size:0.9em;">${user.displayRole}</span></td>
                        <td>${deleteBtn}</td>
                    </tr>
                    `;
                }).join('');
            }
        });

    } catch (error) {
        console.error("Error loading users:", error);
    }
}

async function handleStaffSubmit(e) {
    e.preventDefault(); 
    
    const roleName = document.getElementById('sRole').value;
    const userData = {
        name: document.getElementById('sName').value,
        email: document.getElementById('sEmail').value,
        password: document.getElementById('sPassword').value
    };

    try {
        // UPDATE: Use api.js wrapper (Handles ?roleName= logic)
        await api.addStaffMember(userData, roleName);
        
        alert("Staff added!");
        closeModal('staffModal');
        // Clear form
        document.getElementById('sName').value = '';
        document.getElementById('sEmail').value = '';
        document.getElementById('sPassword').value = '';
        loadStaff();
    } catch (error) {
        alert("Error: " + error.message);
    }
}

async function deleteStaff(id) {
    if(!confirm("Delete User?")) return;
    try {
        // UPDATE: Use api.js wrapper
        await api.deleteUser(id);
        loadStaff();
    } catch(e) { alert(e.message); }
}

// ==========================================
// 3. UTILITIES & NAVIGATION
// ==========================================

function showSection(id) {
    document.querySelectorAll('.section').forEach(d => d.classList.remove('active'));
    document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
    
    const target = document.getElementById(id);
    if(target) target.classList.add('active');
    
    const btnMap = { 'orders': 0, 'menu': 1, 'staff': 2 };
    const btns = document.querySelectorAll('.nav-btn');
    if(btns[btnMap[id]]) btns[btnMap[id]].classList.add('active');
}

function openCategoryModal() { document.getElementById('categoryModal').style.display = 'flex'; }
function openStaffModal() { document.getElementById('staffModal').style.display = 'flex'; }
function closeModal(id) { document.getElementById(id).style.display = 'none'; }