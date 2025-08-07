-- example of create view
USE ms3_itbms_db;

SELECT * FROM sale_items;

SELECT DISTINCT storage_Gb FROM sale_items;

CREATE VIEW saleitems_storage_size_view AS
SELECT DISTINCT storage_Gb FROM sale_items;

SELECT * FROM saleitems_storage_size_view;


-- between 101
/api/saleitems?upperPrice=1000&lowerPrice=5000&brands=apple,samsung&storageSize=32,254
SELECT * FROM sale_items WHERE price BETWEEN 1000 AND 5000