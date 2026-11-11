# 🛒 ShopEase — Full-Stack E-Commerce Application

A complete e-commerce platform built with **Spring Boot** (backend) and **Vanilla HTML/CSS/JS** (frontend), running on separate ports.

---

## 📁 Project Structure

```
ECommerce/
├── backend/                    ← Spring Boot (port 8080)
│   ├── pom.xml
│   └── src/main/java/com/ecommerce/
│       ├── app/
│       │   ├── ECommerceApplication.java   ← Main entry + ASCII logo
│       │   ├── CorsConfig.java             ← CORS for cross-origin frontend
│       │   └── DataSeeder.java             ← Seeds admin + categories + products
│       ├── model/              ← JPA Entities (Hibernate auto-creates tables)
│       ├── repository/         ← Spring Data JPA repos
│       ├── service/            ← Business logic
│       ├── controller/         ← REST API endpoints
│       ├── dto/                ← Request/Response objects
│       └── exception/          ← Global error handling
│
└── frontend/                   ← Vanilla JS (port 3000)
    ├── index.html              ← Home / Product listing
    ├── server.js               ← Node.js static file server
    ├── css/style.css           ← Global styles (purple/blue theme)
    ├── js/
    │   ├── api.js              ← All API calls (centralized)
    │   ├── utils.js            ← Shared helpers (auth, toast, format)
    │   ├── navbar.js           ← Dynamic navbar rendering
    │   └── cart.js             ← Cart sidebar logic
    └── pages/
        ├── auth.html           ← Login / Register
        ├── profile.html        ← User profile & edit
        ├── checkout.html       ← 3-step checkout
        ├── orders.html         ← Order history & tracking
        └── admin.html          ← Admin panel (dashboard, products, orders, users)
```

---

## 🚀 Setup & Run

### Prerequisites
- Java 17+
- Maven
- MySQL 8+
- Node.js (for frontend server)

---

### Step 1 — Create Database

```sql
CREATE DATABASE ecommerce_db;
```
> Hibernate will auto-create all tables on first startup (`ddl-auto=update`)

---

### Step 2 — Configure Backend

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Optionally set email credentials for order confirmation emails:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

---

### Step 3 — Run Backend

```bash
cd backend
mvn spring-boot:run
```

Backend starts on **http://localhost:8080**

On startup you'll see:
- The **ShopEase ASCII logo** in console
- Hibernate auto-creating all tables
- Default **admin account** seeded: `admin@shopease.com` / `admin123`
- 6 categories + 12 sample products seeded

---

### Step 4 — Run Frontend

```bash
cd frontend
node server.js
```

Frontend starts on **http://localhost:3000**

> **Alternative:** Open `frontend/index.html` with VS Code **Live Server** extension (runs on port 5500)

---

## 👤 Default Accounts

| Role  | Email                  | Password  |
|-------|------------------------|-----------|
| Admin | admin@shopease.com     | admin123  |
| User  | Register a new account | —         |

---

## 🔌 API Endpoints

| Method | Endpoint                        | Description                   |
|--------|---------------------------------|-------------------------------|
| POST   | /api/users/register             | Register new user             |
| POST   | /api/users/login                | Login                         |
| GET    | /api/users/{id}                 | Get user profile              |
| PUT    | /api/users/{id}                 | Update profile                |
| GET    | /api/products                   | All active products           |
| GET    | /api/products/search?keyword=   | Search products               |
| GET    | /api/products/category/{id}     | Filter by category            |
| POST   | /api/products                   | Admin: add product            |
| PUT    | /api/products/{id}              | Admin: edit product           |
| DELETE | /api/products/{id}              | Admin: delete product         |
| GET    | /api/categories                 | All categories                |
| GET    | /api/cart/{userId}              | Get user's cart               |
| POST   | /api/cart/add                   | Add item to cart              |
| PUT    | /api/cart/item/{itemId}         | Update cart item qty          |
| DELETE | /api/cart/item/{itemId}         | Remove cart item              |
| POST   | /api/orders/checkout            | Place order from cart         |
| GET    | /api/orders/user/{userId}       | User's order history          |
| PATCH  | /api/orders/{id}/status         | Admin: update order status    |
| POST   | /api/orders/{id}/cancel         | Cancel order                  |

---

## ✨ Features Implemented

- ✅ ASCII logo on startup
- ✅ User registration & login
- ✅ Role-based: CUSTOMER / ADMIN
- ✅ Product catalog with categories
- ✅ Product search (name + category)
- ✅ Persistent shopping cart (DB-backed)
- ✅ Cart sidebar with qty controls
- ✅ 3-step checkout (address → delivery → payment)
- ✅ Order confirmation email
- ✅ Order history & status timeline
- ✅ Order cancellation with reason + stock restore
- ✅ Admin dashboard with stats
- ✅ Admin product management (CRUD)
- ✅ Admin order status updates
- ✅ Admin user management
- ✅ Hibernate auto DDL (no manual SQL)
- ✅ Java 8 LocalDateTime throughout
- ✅ Global exception handler (user-friendly errors)
- ✅ CORS configured for cross-port access
- ✅ Responsive design (mobile friendly)

---

## 🛠️ Tech Stack

**Backend**
- Java 17 + Spring Boot 3.2
- Spring Data JPA + Hibernate (auto DDL)
- MySQL 8
- Spring Mail (JavaMail)
- Lombok + Bean Validation

**Frontend**
- Vanilla HTML5 / CSS3 / JavaScript (ES6+)
- CSS custom properties (design tokens)
- Fetch API for all backend calls
- Google Fonts (Inter)

---

*Built as a learning project — focus on Spring Boot + Hibernate concepts*
