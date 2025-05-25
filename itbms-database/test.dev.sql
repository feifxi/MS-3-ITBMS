SELECT * FROM ms3_itbms_db.sale_items;
SELECT * FROM ms3_itbms_db.brands;

DROP DATABASE `ms3_itbms_db`;

SELECT @@global.time_zone, @@session.time_zone;

-- set timezone
SET GLOBAL time_zone = '+00:00';
SET time_zone = '+00:00';

-- restore
SET GLOBAL time_zone = 'SYSTEM';
SET time_zone = 'SYSTEM';