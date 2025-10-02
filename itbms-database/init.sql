-- SET GLOBAL time_zone = '+00:00';
-- SET time_zone = '+00:00';

DROP DATABASE IF EXISTS `ms3_itbms_db` ;
CREATE SCHEMA IF NOT EXISTS `ms3_itbms_db` DEFAULT CHARACTER SET utf8 ;

USE `ms3_itbms_db` ;

CREATE TABLE IF NOT EXISTS `ms3_itbms_db`.`users` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nickname VARCHAR(40) NOT NULL,
    full_name VARCHAR(40) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    shop_name VARCHAR(255),
    phone VARCHAR(20),
    bank_account_number VARCHAR(50),
    bank_name VARCHAR(50),
    id_card_number VARCHAR(20),
    id_card_image_front VARCHAR(255),
    id_card_image_back VARCHAR(255),
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    user_type ENUM('BUYER','SELLER') DEFAULT 'BUYER',
    status ENUM('ACTIVE','INACTIVE') DEFAULT 'INACTIVE',
    verification_token VARCHAR(255),
    verification_token_expiry DATETIME,
    created_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email)
);

CREATE TABLE IF NOT EXISTS `ms3_itbms_db`.`roles` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `ms3_itbms_db`.`user_roles` (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `ms3_itbms_db`.`addresses` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    address_line TEXT NOT NULL,
    city VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `ms3_itbms_db`.`refresh_tokens` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expiry_date DATETIME NOT NULL,
    created_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expiry_date (expiry_date)
);

CREATE TABLE IF NOT EXISTS `ms3_itbms_db`.`brands` (
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL UNIQUE,
    website_url VARCHAR(40),	
    is_active BOOLEAN,
    country_of_origin VARCHAR(80),
    created_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `ms3_itbms_db`.`sale_items` (
	id INT PRIMARY KEY AUTO_INCREMENT,
    model VARCHAR(60) NOT NULL,
    brand_id INT NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    ram_Gb INT,
    screen_size_inch DECIMAL(5,2),
    storage_Gb INT,
    color TEXT,
    quantity INT NOT NULL DEFAULT 1,
    seller_id INT NOT NULL,
    created_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE CASCADE,
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `ms3_itbms_db`.`sale_item_images` (
	id INT PRIMARY KEY AUTO_INCREMENT,
    image_name TEXT NOT NULL,
    original_image_name TEXT NOT NULL,
    sale_item_id INT NOT NULL,
    image_view_order INT NOT NULL,
    FOREIGN KEY (sale_item_id) REFERENCES sale_items(id) ON DELETE CASCADE
);

-- Orders Table
CREATE TABLE IF NOT EXISTS `ms3_itbms_db`.`orders` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    buyer_id INT NOT NULL,
    seller_id INT NOT NULL,
    address_id INT NOT NULL, -- delivery address
    total_amount DECIMAL(12,2) NOT NULL,
    status ENUM('PENDING','PAID','SHIPPED','DELIVERED','CANCELLED','RETURNED') DEFAULT 'PENDING',
    payment_status ENUM('PENDING','PAID','FAILED','REFUNDED') DEFAULT 'PENDING',
    order_note TEXT,
    shipping_address_note TEXT,
    created_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE CASCADE,
    INDEX idx_buyer (buyer_id),
    INDEX idx_seller (seller_id)
);

-- Order Items Table (Each product in an order)
CREATE TABLE IF NOT EXISTS `ms3_itbms_db`.`order_items` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    sale_item_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price_at_purchase DECIMAL(12,2) NOT NULL, -- store the price at time of order
    subtotal DECIMAL(12,2) GENERATED ALWAYS AS (quantity * price_at_purchase) STORED,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (sale_item_id) REFERENCES sale_items(id) ON DELETE CASCADE
);

-- (Optional) Payments / Transactions
CREATE TABLE IF NOT EXISTS `ms3_itbms_db`.`payments` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    payment_method ENUM('BANK_TRANSFER','CREDIT_CARD','COD','EWALLET') NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    status ENUM('PENDING','SUCCESS','FAILED','REFUNDED') DEFAULT 'PENDING',
    transaction_reference VARCHAR(255),
    created_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);



-- ======= Insert Mock Data =======
INSERT INTO `ms3_itbms_db`.`brands` (`id`, `name`, `website_url`, `is_active`,`country_of_origin`) VALUES
(1,"Samsung","https://www.samsung.com",1, "South Korea"),
(2,"Apple","https://www.apple.com",1, "United States"),
(3,"Xiaomi","https://www.mi.com",1, "China"),
(4,"Huawei","https://www.huawei.com",1, "China"),
(5,"OnePlus","https://www.oneplus.com",1, "China"),
(6,"Sony","https://www.sony.com",1, "Japan"),
(7,"LG","https://www.lg.com",1, "South Korea"),
(8,"Nokia","https://www.nokia.com",0, "Finland"),
(9,"Motorola","https://www.motorola.com",0, "United States"),
(10,"OPPO","https://www.oppo.com",1, "China"),
(11,"Vivo","https://www.vivo.com",1, "China"),
(12,"ASUS","https://www.asus.com",1, "Taiwan"),
(13,"Google","https://store.google.com",1, "United States"),
(14,"Realme","https://www.realme.com",1, "China"),
(15,"BlackBerry","https://www.blackberry.com",1, "Canada"),
(16,"HTC","https://www.htc.com",1, "Taiwan"),
(17,"ZTE","https://www.zte.com",1, "China"),
(18,"Lenovo","https://www.lenovo.com",1, "China"),
(19,"Honor","https://www.hihonor.com",1, "China"),
(20,"Nothing","https://nothing.tech",1, "United Kingdom");

INSERT INTO `ms3_itbms_db`.`roles` (`name`, `description`) VALUES 
('ROLE_ADMIN', 'Administrator with full access'),
('ROLE_USER', 'Regular user with limited access'),
('ROLE_MODERATOR', 'Moderator with intermediate access');