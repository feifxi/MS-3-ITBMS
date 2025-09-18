-- liquibase formatted sql

-- changeset feifxi:2
-- This changeset inserts mock data into the brands and roles tables.
INSERT INTO brands (id, name, website_url, is_active,country_of_origin) VALUES
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

INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'Administrator with full access'),
('ROLE_USER', 'Regular user with limited access'),
('ROLE_MODERATOR', 'Moderator with intermediate access');