# GoCart Backend - E-Commerce & Delivery Management System

A robust Spring Boot backend application for a full-featured e-commerce platform with integrated delivery partner management and real-time order tracking capabilities.

## 🚀 Features

### User Management
- **Authentication & Authorization**: JWT-based secure authentication system
- **User Profiles**: Complete profile management with address and location tracking
- **Role-based Access**: Separate access controls for customers and delivery partners

### Product Management
- **Product CRUD Operations**: Full create, read, update, and delete functionality
- **Warehouse Integration**: Multi-warehouse inventory management
- **Product Discovery**: Advanced search and filtering capabilities

### Shopping Experience
- **Shopping Cart**: Add, update, and remove items from cart
- **Wishlist**: Save favorite products for later
- **Order Management**: Complete order lifecycle management
- **Order History**: Track all past orders with detailed information

### Delivery Partner System
- **Partner Authentication**: Separate login system for delivery partners
- **Order Assignment**: Intelligent order assignment to available partners
- **Real-time Tracking**: Live location updates during delivery
- **Earnings Dashboard**: Track deliveries and earnings
- **Order History**: View completed and ongoing deliveries

### Real-time Features
- **Server-Sent Events (SSE)**: Real-time order notifications and status updates
- **Live Tracking**: Continuous location updates for active deliveries
- **Push Notifications**: Instant alerts for delivery partners on new orders

## 🛠️ Tech Stack

- **Framework**: Spring Boot 4.0.1
- **Language**: Java 21
- **Database**: MySQL (with PostgreSQL support available)
- **Security**: Spring Security with JWT (JJWT 0.11.5)
- **ORM**: Spring Data JPA with Hibernate
- **Real-time**: Server-Sent Events (SSE)
- **Build Tool**: Maven
- **Additional Libraries**: 
  - Lombok for boilerplate reduction
  - Spring Boot DevTools for development
  - Spring Boot Actuator for monitoring

## 📁 Project Structure

```
src/main/java/com/example/backendgocart/
├── config/              # Security and WebSocket configuration
├── controller/          # REST API endpoints
│   ├── CartController
│   ├── DeliveryPartnerController
│   ├── OrderController
│   ├── OrderSseController
│   ├── ProductController
│   ├── UserController
│   ├── UserProfileController
│   └── WishlistController
├── dto/                 # Data Transfer Objects
├── model/              # JPA Entity classes
│   ├── User
│   ├── Product
│   ├── CartItem
│   ├── Order
│   ├── OrderItem
│   ├── DeliveryPartner
│   ├── DeliveryAssignment
│   ├── Warehouse
│   └── ...
├── repository/         # JPA Repositories
├── service/           # Business logic layer
└── util/              # Utility classes
```

## 🔧 Installation & Setup

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- MySQL Server 8.0+
- Git

### Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd BackendGocart
   ```

2. **Configure Database**
   
   Create a MySQL database:
   ```sql
   CREATE DATABASE Myamazon;
   ```
   
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/Myamazon
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   jwt.secret=your_secure_jwt_secret
   ```

3. **Build the project**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The server will start on `http://localhost:8080`

## 📡 API Endpoints

### Authentication
- `POST /api/register` - Register new user
- `POST /api/login` - User login
- `POST /api/delivery/register` - Register delivery partner
- `POST /api/delivery/login` - Delivery partner login

### Products
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Add new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Cart & Wishlist
- `GET /api/cart` - Get user cart
- `POST /api/cart` - Add item to cart
- `DELETE /api/cart/{id}` - Remove cart item
- `GET /api/wishlist` - Get wishlist
- `POST /api/wishlist` - Add to wishlist

### Orders
- `POST /api/orders` - Place order
- `GET /api/orders` - Get user orders
- `GET /api/orders/{id}` - Get order details
- `GET /api/orders/track/{id}` - Track order

### Delivery Partner
- `GET /api/delivery/orders` - Get assigned orders
- `PUT /api/delivery/orders/{id}/status` - Update delivery status
- `GET /api/delivery/earnings` - Get earnings
- `GET /api/delivery/profile` - Get profile
- `GET /api/order/notifications` - SSE endpoint for real-time notifications

## 🔐 Security

- JWT-based authentication for secure API access
- Password encryption using BCrypt
- CORS configuration for frontend integration
- Role-based authorization (USER, DELIVERY_PARTNER, ADMIN)

## 🌐 Database Schema

Key entities:
- **User**: Customer account information
- **UserProfile**: Extended user details with location
- **Product**: Product catalog
- **CartItem**: Shopping cart entries
- **WishlistItem**: Saved items
- **Order**: Order information
- **OrderItem**: Individual items in an order
- **DeliveryPartner**: Delivery agent details
- **DeliveryAssignment**: Order-to-partner mapping
- **Warehouse**: Inventory locations
- **WarehouseInventory**: Stock levels

## 📊 Real-time Features

### Server-Sent Events (SSE)
The application uses SSE for pushing real-time updates from server to clients:

**For Delivery Partners:**
- New order assignment notifications
- Order status changes
- Real-time earnings updates

**For Customers:**
- Live order status updates
- Real-time delivery location tracking
- Estimated delivery time updates

**Implementation:**
- `OrderSseService`: Manages order-specific SSE connections
- `DeliveryNotificationSseService`: Handles delivery partner notifications
- Long-lived connections with automatic reconnection
- Per-order and per-user emitter management

## 🚦 Development

### Running Tests
```bash
./mvnw test
```

### Building for Production
```bash
./mvnw clean package -DskipTests
java -jar target/BackendGocart-0.0.1-SNAPSHOT.jar
```

## 📝 Environment Variables (Production)

For production deployment, use environment variables:
```properties
DB_URL=jdbc:mysql://your-db-host:3306/dbname
DB_USER=your_db_user
DB_PASS=your_db_password
JWT_SECRET=your_production_jwt_secret
PORT=8080
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Author

**Ravi Kiran Mothukuri**

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- All contributors and testers
- The open-source community

---

**Note**: This is a personal/portfolio project demonstrating full-stack development capabilities with modern technologies and best practices.
