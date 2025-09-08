# 📱 Mobile Shop E-commerce Platform

A full-stack e-commerce web application for mobile devices and accessories, built with Vue.js frontend and Java Spring Boot backend, containerized with Docker for production deployment.

## 🌐 Live Demo

Visit the live application: [http://intproj24.sit.kmutt.ac.th/ms3/sale-items](http://intproj24.sit.kmutt.ac.th/ms3/)

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
- **Production Server** - Deployed on university infrastructure

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
   # Configure database connection in application.properties
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

4. **Database Setup**
   ```sql
   CREATE DATABASE mobile_shop;
   -- Import initial data if available
   ```

### Docker Deployment

1. **Build and run with Docker Compose**
   ```bash
   docker-compose up --build
   ```

2. **Access the application**
   - Frontend: `http://localhost:8080`
   - Backend API: `http://localhost:8081`

## 📁 Project Architecture

```
MS-3-ITBMS/
├── frontend/                 # Vue.js application
│   ├── src/
│   │   ├── components/      # Reusable Vue components
│   │   ├── views/           # Page components
│   │   ├── router/          # Route definitions
│   │   ├── store/           # Vuex store modules
│   │   └── services/        # API service layers
├── backend/                 # Spring Boot application
│   ├── src/main/java/
│   │   ├── controller/      # REST controllers
│   │   ├── service/         # Business logic layer
│   │   ├── repository/      # Data access layer
│   │   ├── model/           # Entity classes
│   │   └── config/          # Configuration classes
├── docker-compose.yml       # Multi-container setup
├── Dockerfile.frontend      # Frontend container config
├── Dockerfile.backend       # Backend container config
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

### Admin Features
- Product management (CRUD operations)
- Category management
- Order management and status updates
- User management
- Sales analytics and reporting

## 🐳 Docker Configuration

The application uses multi-stage Docker builds for optimization:

- **Frontend**: Nginx-served Vue.js build
- **Backend**: OpenJDK with Spring Boot JAR
- **Database**: MySQL/PostgreSQL container
- **Reverse Proxy**: Nginx for routing

## 🔒 Security Features

- JWT-based authentication
- Role-based access control (Customer/Admin)
- Input validation and sanitization
- HTTPS in production
- CORS configuration
- SQL injection protection via JPA

## 📊 API Documentation

The backend provides RESTful APIs for:

- **Authentication**: `/api/auth/*`
- **Products**: `/api/products/*`
- **Categories**: `/api/categories/*`
- **Orders**: `/api/orders/*`
- **Users**: `/api/users/*`

## 🚀 Production Deployment

This application is deployed using Docker containers on university infrastructure with:
- Automated CI/CD pipeline
- Load balancing and scaling
- Database backups and monitoring
- SSL certificates for secure connections

## 🤝 Contributing

This is an academic project (MS-3-ITBMS course). Contributions and feedback are welcome!

## 📚 Course Information

This project was developed as part of:
- **Course**: MS-3 ITBMS (IT Business Management System)
- **Institution**: King Mongkut's University of Technology Thonburi (KMUTT)
- **Academic Year**: 2024

## 📝 License

This project is developed for educational purposes.

## 🙋‍♂️ Contact

For questions about this project or collaboration opportunities, feel free to reach out!

---
⭐ Don't forget to star this repository if you find it useful!
