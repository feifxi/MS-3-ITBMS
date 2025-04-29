DROP DATABASE ms3_itbms_db;
CREATE SCHEMA IF NOT EXISTS `ms3_itbms_db` DEFAULT CHARACTER SET utf8 ;
USE `ms3_itbms_db` ;

CREATE TABLE IF NOT EXISTS `ms3_itbms_db`.`brands` (
	id INT PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    website_url VARCHAR(40),
    is_active BOOLEAN,
    country_of_origin VARCHAR(80),
    created_on DATETIME DEFAULT CURRENT_TIMESTAMP,
	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE sale_items (
	id INT PRIMARY KEY,
    model VARCHAR(60) NOT NULL,
    brand_id INT NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    ram_Gb INT,
    screen_size_inch DECIMAL(3,1),
    storage_Gb INT,
    color TEXT,
    quantity INT NOT NULL DEFAULT 1,
    created_on DATETIME DEFAULT CURRENT_TIMESTAMP,
	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (brand_id) REFERENCES brands(id)
);


INSERT INTO `ms3_itbms_db`.`brands` (`id`, `name`, `country_of_origin`, `website_url`, `is_active`)
VALUES
(1, 'Samsung', 'South Korea', 'https://www.samsung.com', '1'),
(2, 'Apple', 'United States', 'https://www.apple.com', '1'),
(3, 'Xiaomi', 'China', 'https://www.mi.com', '1'),
(4, 'Huawei', 'China', 'https://www.huawei.com', '1'),
(5, 'OnePlus', 'China', 'https://www.oneplus.com', '1'),
(6, 'Sony', 'Japan', 'https://www.sony.com', '1'),
(7, 'LG', 'South Korea', 'https://www.lg.com', '1'),
(8, 'Nokia', 'Finland', 'https://www.nokia.com', '0'),
(9, 'Motorola', 'United States', 'https://www.motorola.com', '0'),
(10, 'OPPO', 'China', 'https://www.oppo.com', '1'),
(11, 'Vivo', 'China', 'https://www.vivo.com', '1'),
(12, 'ASUS', 'Taiwan', 'https://www.asus.com', '1'),
(13, 'Google', 'United States', 'https://store.google.com', '1'),
(14, 'Realme', 'China', 'https://www.realme.com', '1'),
(15, 'BlackBerry', 'Canada', 'https://www.blackberry.com', '1'),
(16, 'HTC', 'Taiwan', 'https://www.htc.com', '1'),
(17, 'ZTE', 'China', 'https://www.zte.com', '1'),
(18, 'Lenovo', 'China', 'https://www.lenovo.com', '1'),
(19, 'Honor', 'China', 'https://www.hihonor.com', '1'),
(20, 'Nothing', 'United Kingdom', 'https://nothing.tech', '1');

INSERT INTO `ms3_itbms_db`.`sale_items`
(`id`, `brand_id`, `model`, `description`, `quantity`, `price`, `screen_size_inch`, `ram_Gb`, `storage_Gb`, `color`)
VALUES
(1, 2, 'iPhone 14 Pro Max', 'ไอโฟนโปรรุ่นล่าสุด มาพร้อม Dynamic Island จอใหญ่สุดในตระกูล กล้องระดับโปร', 5, 42900.00, 6.7, 6, 512, 'Space Black'),
(2, 2, 'iPhone 14', 'ไอโฟนรุ่นใหม่ล่าสุด รองรับ 5G เร็วแรง ถ่ายภาพสวยทุกสภาพแสง', 8, 29700.00, 6.1, 6, 256, 'Midnight'),
(3, 2, 'iPhone 14 Pro', 'ไอโฟนโปร จอ ProMotion 120Hz กล้องระดับมืออาชีพ', 6, 33000.00, 6.1, 6, 256, 'Sierra Blue'),
(7, 2, 'iPhone SE 2022', 'Budget-Friendly model', 15, 14190.00, 4.7, 4, 64, 'Starlight'),
(8, 2, 'iPhone 14 Plus', 'iPhone 14 Plus 128GB สี Starlight รุ่นสุดคุ้มที่ขายในตลาด TH แบต 100% ตีกล่องครบ ประกันศูนย์ 1 ปี + ของแถม 6 ชิ้น ฟรี', 7, 29700.00, 6.7, 6, 256, 'Blue'),
(16, 1, 'Galaxy S23 Ultra', 'Samsung Galaxy S23 Ultra 512GB สีดำเงา สภาพนางฟ้า 99% ใช้งานน้อย ติดฟิล์มหน้าจอแบบเต็มจอ รองรับการทำงาน S-Pen', 1, 32900.00, 7.6, 6, 512, 'Black');
