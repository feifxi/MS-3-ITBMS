-- example of create view
USE ms3_itbms_db;

-- Sale item
SELECT * FROM sale_items s JOIN users u ON u.id = s.seller_id;
SELECT * FROM sale_item_images;

-- User
SELECT * FROM roles;
SELECT * FROM users;
SELECT * FROM seller_profiles;
SELECT * FROM buyer_profiles;
SELECT * FROM user_roles;
-- Auth
SELECT * FROM refresh_tokens;
-- Email Verified Exipires user
SELECT * FROM users u WHERE u.verification_token_expiry < NOW();

SET time_zone = '+07:00';
SELECT @@global.time_zone, @@session.time_zone;
SELECT NOW(), UTC_TIMESTAMP();

-- Inspect user role
SELECT u.username, r.name AS 'role name' FROM users u
JOIN user_roles ur ON ur.user_id = u.id
JOIN roles r ON ur.role_id = r.id
WHERE u.id = 8;

-- update role
UPDATE user_roles SET role_id = 1 WHERE user_id = 1;
-- add role
INSERT INTO user_roles VALUES (8,1);

DELETE FROM refresh_tokens;

SET SQL_SAFE_UPDATES = 0;

-- Migration
SELECT * FROM DATABASECHANGELOG;
SELECT * FROM DATABASECHANGELOGLOCK;

DESCRIBE users;