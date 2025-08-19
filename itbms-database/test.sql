-- example of create view
USE ms3_itbms_db;

SELECT * FROM sale_items;

SELECT DISTINCT storage_Gb FROM sale_items;

CREATE VIEW saleitems_storage_size_view AS
SELECT DISTINCT storage_Gb FROM sale_items;

SELECT * FROM saleitems_storage_size_view;


-- between 101
-- /api/saleitems?upperPrice=1000&lowerPrice=5000&brands=apple,samsung&storageSize=32,254
SELECT * FROM sale_items WHERE price BETWEEN 1000 AND 5000;

-- User
USE ms3_itbms_db;

SELECT * FROM user_roles;
SELECT * FROM users;
SELECT * FROM roles;

-- Inspect user role
SELECT u.username, r.name AS 'role name' FROM users u
JOIN user_roles ur ON ur.user_id = u.id
JOIN roles r ON ur.role_id = r.id
WHERE u.id = 8;

-- update role
UPDATE user_roles SET role_id = 1 WHERE user_id = 1;
-- add role
INSERT INTO user_roles VALUES (8,1);