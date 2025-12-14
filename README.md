# 🍔 Food Delivery System

A comprehensive, full-stack food delivery application built with **Spring Boot**. This system handles the complete flow of food ordering, from product browsing and cart management to order placement, delivery tracking, and role-based dashboards for Admins, Staff, and Delivery Drivers.

### 🌐 Live Demo
**Click here to view the deployed application:**
👉 **[https://food-delivery-system-production-a37e.up.railway.app/](https://food-delivery-system-production-a37e.up.railway.app/)**

---

## 📂 UML Diagrams
> **Note to Instructors/Reviewers:**
> All project design documents, including Use Case, Class, and Sequence diagrams, can be found in the **`UML Diagrams`** folder located in the root directory of this repository.

---

## 🏗️ Architecture
The system follows a microservice-ready architecture using **Spring Cloud**:

1.  **Main Application (Core Monolith):** Runs on port `5005` (Locally). Handles all business logic, database interactions, and serves the frontend static resources.
2.  **API Gateway:** Runs on port `8080`. Acts as the single entry point, handling routing and security interception before forwarding requests to the main application.

## 🚀 Features by Role

### 👤 Customer
* **Browse Menu:** View products by categories (Pizza, Burger, Sushi, etc.).
* **Cart Management:** Add/remove items and view total cost.
* **Order Placement:** Select delivery addresses and place orders.
* **Live Tracking:** Track order status (Pending, Preparing, On the Way, Delivered).

### 👨‍💼 Admin
* **User Management:** Manage customers, staff, and delivery personnel.
* **System Oversight:** View system activity logs and global order statistics.

### 🍳 Kitchen Staff
* **Order Management:** View incoming orders and update status (e.g., "Cooking", "Ready").
* **Menu Management:** Add/Update products and categories.

### 🛵 Delivery Staff
* **Delivery Dashboard:** View assigned orders.
* **Status Updates:** Mark orders as "Picked Up" or "Delivered".

---

## 🛠️ Tech Stack
* **Backend:** Java 21, Spring Boot 3.x (Web, Data JPA, Validation)
* **Gateway:** Spring Cloud Gateway (Netty)
* **Database:** PostgreSQL
* **Frontend:** HTML5, CSS3, JavaScript (Vanilla)
* **Logging:** Spring AOP (Aspect Oriented Programming) for tracking system activities.
* **Deployment:** Railway

---

## ⚙️ Local Setup & Installation

### 1. Database Setup
1.  Ensure **PostgreSQL** is running.
2.  Create a database named `food_delivery_db` (or as configured in your properties).
3.  (Optional) Run the script in `db_scripts/manual_backup.sql` to seed initial data.

### 2. Running the Main Application
1.  Navigate to the main project folder.
2.  Ensure `src/main/resources/application.properties` has `server.port=5005`.
3.  Run the application:
    ```bash
    ./mvnw spring-boot:run
    ```

### 3. Running the API Gateway
1.  Navigate to the `GatewayProject` folder.
2.  Run the Gateway application:
    ```bash
    ./mvnw spring-boot:run
    ```
    *The Gateway will start on port `8080`.*

---

## 🖥️ Usage

### Option 1: Live Production (Cloud)
Visit: **[https://food-delivery-system-production-a37e.up.railway.app/](https://food-delivery-system-production-a37e.up.railway.app/)**

### Option 2: Local Development (Gateway)
Visit: **http://localhost:8080/auth.html**

* **Login:** Users are redirected to their specific dashboards based on their role (Admin, Staff, Delivery, Customer) upon successful authentication.
* **Tracking:** System activity is automatically logged to `system_activity_logs.txt` in the root directory via AOP.

---

## 📂 Project Structure
```text
D:.
├── UML Diagrams/            <-- DESIGN DOCUMENTS HERE
├── db_scripts/              <-- Database backups/SQL
├── system_activity_logs.txt <-- Auto-generated audit logs
├── src/main/java/com/system/food_delivery_app
│   ├── controller/          <-- REST Controllers
│   ├── model/               <-- JPA Entities
│   ├── service/             <-- Business Logic
│   ├── repository/          <-- DB Access
│   ├── aspect/              <-- Logging Aspects
│   └── config/              <-- Security & Web Config
└── src/main/resources/static
    ├── css/                 <-- Stylesheets
    ├── js/                  <-- Frontend Logic
    ├── images/              <-- Product Images (Burger, Pizza, etc.)
    └── *.html               <-- View Pages
