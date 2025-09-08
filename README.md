# 📱  Mobile Shop E-commerce Platform

A full-stack e-commerce web application for mobile devices and accessories, built with Vue.js frontend and Java Spring Boot backend, containerized with Docker for production deployment.

## 🌐 Live Demo

Visit the live application: [http://intproj24.sit.kmutt.ac.th/ms3/](http://intproj24.sit.kmutt.ac.th/ms3/)

## ✨ Features

- **Product Catalog** - Browse mobile phones and accessories
- **Search & Filter** - Find products by brand, price range, and specifications
- **Shopping Cart** - Add/remove items with real-time cart updates
- **User Account** - Registration, login, and profile management
- **Order Management** - Place orders and track order history
- **Responsive Design** - Optimized for all device sizes
- **Admin Panel (up comming)** - Manage products, categories, and orders
- **Secure Payments (up comming)** - Integrated payment processing

## 🛠️ Technology Stack

### Frontend
- **Vue.js 3** - Progressive JavaScript framework
- **Vue Router** - Client-side routing
- **Pinia** - State management
- **Tailwind CSS** - Styling and responsive design

### Backend
- **Java Spring Boot** - Enterprise-grade backend framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database abstraction layer
- **MySQL** - Relational database
- **REST API** - RESTful web services

### DevOps & Deployment
- **Docker** - Containerization platform
- **Docker Compose** - Multi-container orchestration
- **Production Server** - Deployed on university infrastructure (private VM)

## 🚀 Getting Started

### Prerequisites

- Node.js (v16+)
- Java JDK 11+
- Maven 3.6+
- Docker & Docker Compose
- MySQL

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/feifxi/MS-3-ITBMS.git
   cd MS-3-ITBMS
   ```

2. **Backend Setup**
   ```bash
   cd itbms-backend
   # Configure database connection in application.yml
   cp src/main/resources/application.example.yml src/main/resources/application.yml
   # Install dependencies and run
   mvn clean install
   mvn spring-boot:run
   ```

3. **Frontend Setup**
   ```bash
   cd itbms-frontend
   npm install
   npm run dev
   ```

4. **Database Setup via docker (run at root project)**
   ```docker
   docker run --name itbms-dev-db -e MYSQL_ROOT_PASSWORD=rootpass -v ./itbms-database/init.sql:/docker-entrypoint-initdb.d/init.sql -p 3306:3306 -d mysql:latest
   ```
   
5. **Access the application**
   - Frontend: `http://localhost:5173/ms3/`
   - Backend API: `http://localhost:8080/itb-mshop`

### Docker Deployment

**Build and run with Docker Compose**
   ```bash
   docker-compose up --build
   ```


## 📁 Project Architecture

```
MS-3-ITBMS/
├── itbms-frontend/          # Vue.js application
│   ├── src/
│   │   ├── components/      # Reusable Vue components
│   │   ├── views/           # Page components
│   │   ├── router/          # Route definitions
│   │   ├── store/           # Pinia store 
├── itbms-backend/           # Spring Boot application
│   ├── src/main/java/
│   │   ├── controllers/     # REST controllers
│   │   ├── services/        # Business logic layer
│   │   ├── repositories/    # Data access layer
│   │   ├── entities/        # Entity classes
│   │   ├── security/        # Security modules
│   │   └── configs/         # Configuration classes
├── itbms-database/          # SQL scripts for init database
├── docker-compose.yml       # Multi-container setup
└── README.md
```

## 🔧 Key Functionalities

### Customer Features
- Browse product catalog with categories
- Search and filter products
- View detailed product information
- Add items to shopping cart
- User registration and authentication
- Place and track orders
- Order history and receipts

### Admin Features (up comming)
- Product management (CRUD operations)
- Category management
- Order management and status updates
- User management
- Sales analytics and reporting

## 🐳 Docker Configuration

The application uses multi-stage Docker builds for optimization:

- **Frontend**: Nginx-served Vue.js build
- **Backend**: OpenJDK with Spring Boot JAR
- **Database**: MySQL container
- **Reverse Proxy**: Nginx for routing

## 🔒 Security Features

- JWT-based authentication
- Role-based access control (Customer/Admin)
- Input validation and sanitization
- HTTPS in production (up comming)
- CORS configuration
- SQL injection protection via JPA

---
⭐ This project was developed as part of INT222 Integrated Project to show the fullstack skill.
