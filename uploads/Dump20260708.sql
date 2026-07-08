-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: foodsndrinks
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `slug` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('FOOD','DRINK','ALL') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ALL',
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `slug` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Cà phê pha máy','ca-phe-pha-may','DRINK','Cà phê Espresso, Americano, Latte, Cappuccino chuẩn vị Ý.','2026-07-07 02:17:53','2026-07-07 02:17:53'),(2,'Trà & Trà sữa','tra-tra-sua','DRINK','Các dòng trà trái cây giải nhiệt và trà sữa trân châu kem béo.','2026-07-07 02:17:53','2026-07-07 02:17:53'),(3,'Nước ép & Sinh tố','nuoc-ep-sinh-to','DRINK','Trái cây tươi xay và ép nguyên chất, tốt cho sức khỏe.','2026-07-07 02:17:53','2026-07-07 02:17:53'),(4,'Đồ ăn nhanh','do-an-nhanh','FOOD','Khoai tây chiên, gà rán giòn rụm, xúc xích nướng.','2026-07-07 02:17:53','2026-07-07 02:17:53'),(5,'Burger & Pizza','burger-pizza','FOOD','Bánh mì kẹp thịt nướng và Pizza phô mai kéo sợi.','2026-07-07 02:17:53','2026-07-07 02:17:53'),(6,'Salad & Healthy','salad-healthy','FOOD','Đồ ăn nhẹ, eat-clean, ít calo dành cho người ăn kiêng.','2026-07-07 02:17:53','2026-07-07 02:17:53'),(7,'Món tráng miệng','mon-trang-mieng','FOOD','Bánh ngọt, bánh flan, tiramisu và kem tươi.','2026-07-07 02:17:53','2026-07-07 02:17:53'),(8,'Combo Tiết Kiệm','combo-tiet-kiem','ALL','Các set kết hợp đồ ăn và nước uống với giá ưu đãi.','2026-07-07 02:17:53','2026-07-07 02:17:53');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'20260624150700','create table users','SQL','V20260624150700__create_table_users.sql',2093688258,'root','2026-06-25 09:55:00',73,1),(2,'20260624150710','create table categories','SQL','V20260624150710__create_table_categories.sql',-1665861599,'root','2026-06-25 09:55:00',70,1),(3,'20260624150720','create table products','SQL','V20260624150720__create_table_products.sql',-698112336,'root','2026-06-25 09:55:00',68,1),(4,'20260624150730','create table product images','SQL','V20260624150730__create_table_product_images.sql',90969296,'root','2026-06-25 09:55:00',64,1),(5,'20260624150740','create table orders','SQL','V20260624150740__create_table_orders.sql',1929675151,'root','2026-06-25 09:55:00',58,1),(6,'20260624150750','create table order details','SQL','V20260624150750__create_table_order_details.sql',65638503,'root','2026-06-25 09:55:00',72,1),(7,'20260624150760','create table ratings','SQL','V20260624150760__create_table_ratings.sql',-1303163675,'root','2026-06-25 09:55:00',103,1),(8,'20260624150770','create table suggestions','SQL','V20260624150770__create_table_suggestions.sql',2062559121,'root','2026-06-25 09:55:01',152,1),(9,'20260624150780','create indexes','SQL','V20260624150780__create_indexes.sql',-368010086,'root','2026-06-25 09:55:01',748,1),(10,'20260702000001','alter users phone nullable','SQL','V20260702000001__alter_users_phone_nullable.sql',1590921306,'root','2026-07-07 02:08:15',321,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `unit_price` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order_details_order_id` (`order_id`),
  KEY `idx_order_details_product_id` (`product_id`),
  CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_details`
--

LOCK TABLES `order_details` WRITE;
/*!40000 ALTER TABLE `order_details` DISABLE KEYS */;
INSERT INTO `order_details` VALUES (1,1,2,1,35.00,35.00),(2,2,9,1,75000.00,75000.00),(3,2,8,1,35000.00,35000.00),(4,2,12,1,95000.00,95000.00),(5,2,6,1,50000.00,50000.00),(6,2,3,1,45000.00,45000.00),(7,2,4,1,55000.00,55000.00),(8,3,2,1,35000.00,35000.00),(9,4,13,1,25000.00,25000.00),(10,5,1,3,25000.00,75000.00),(11,6,13,1,25000.00,25000.00),(12,7,2,1,35000.00,35000.00),(13,8,13,1,25000.00,25000.00),(14,8,9,1,75000.00,75000.00);
/*!40000 ALTER TABLE `order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `total_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `status` enum('CART','PENDING','PROCESSING','COMPLETED','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CART',
  `shipping_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  `ordered_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_orders_user_id` (`user_id`),
  KEY `idx_orders_status` (`status`),
  KEY `idx_orders_ordered_at` (`ordered_at`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,1,35.00,'COMPLETED','test đặt hàng địa chỉ','','2026-07-07 03:06:09','2026-07-07 03:05:56','2026-07-07 03:20:23'),(2,2,355000.00,'COMPLETED','Tòa Keangnam, Phạm Hùng, Hà Nội','Giao càng nhanh càng tốt','2026-07-07 03:21:19','2026-07-07 03:20:41','2026-07-07 03:27:36'),(3,2,35000.00,'CANCELLED','aaaaaa','','2026-07-07 03:27:22','2026-07-07 03:27:17','2026-07-07 03:27:41'),(4,2,25000.00,'PENDING','a','a','2026-07-07 03:29:14','2026-07-07 03:29:10','2026-07-07 03:29:14'),(5,12,75000.00,'PENDING','fadsfsd','fasdf','2026-07-07 03:32:57','2026-07-07 03:32:48','2026-07-07 03:32:57'),(6,12,25000.00,'COMPLETED','a','a','2026-07-07 03:42:12','2026-07-07 03:42:07','2026-07-07 05:22:01'),(7,2,35000.00,'PENDING','fdsaf','','2026-07-08 06:08:19','2026-07-08 06:08:15','2026-07-08 06:08:19'),(8,12,100000.00,'PENDING','fadsfsd','','2026-07-08 06:09:07','2026-07-08 06:09:03','2026-07-08 06:09:07');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_images`
--

DROP TABLE IF EXISTS `product_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_primary` tinyint(1) NOT NULL DEFAULT '0',
  `sort_order` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_product_images_product_id` (`product_id`),
  CONSTRAINT `product_images_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_images`
--

LOCK TABLES `product_images` WRITE;
/*!40000 ALTER TABLE `product_images` DISABLE KEYS */;
INSERT INTO `product_images` VALUES (41,1,'/uploads/products/f1a24ef7-741f-47fa-b375-0ff127c1b3e9.jpg',1,0),(42,1,'/uploads/products/d78dbb52-cb32-4ec6-96bd-2d6b3b936da1.jpg',0,1),(43,1,'/uploads/products/e228abc0-2083-43b2-91eb-f3b6ded260be.jpg',0,2),(44,2,'/uploads/products/2387417e-e472-4938-bb54-2f9e358793a5.jpg',1,0),(45,2,'/uploads/products/c07fff40-109d-48e6-9301-a0d0614c592c.jpg',0,1),(46,2,'/uploads/products/0b06b16b-e0be-45ab-9cc9-09cdf53191a4.jpg',0,2),(47,3,'/uploads/products/316db713-ad3e-434a-8623-79e851fed0af.jpg',1,0),(48,3,'/uploads/products/85233245-6547-4982-b071-755b13afe9a7.jpg',0,1),(49,3,'/uploads/products/20e84891-b2b9-4807-b60e-8f4026758c48.jpg',0,2),(50,4,'/uploads/products/1a650aa5-41c0-4314-bfce-364acd448b09.jpg',1,0),(51,4,'/uploads/products/995b3c08-4fd2-4c0c-a0a5-a8e13612e915.jpg',0,1),(52,4,'/uploads/products/ef8eaa5e-d889-485a-b746-619d12013b9a.jpg',0,2),(53,5,'/uploads/products/acf12f17-70fe-44b5-ad91-f99058834f9c.jpg',1,0),(54,5,'/uploads/products/0a685ea3-5145-4d45-9a8b-a9d49ea8c779.jpg',0,1),(55,5,'/uploads/products/9ece914b-8a85-4809-b98e-f12448fabb9e.jpg',0,2),(56,14,'/uploads/products/4dbfa018-43f9-4b9e-b813-4f616127aafd.jpg',1,0),(57,14,'/uploads/products/0b8e661f-b298-4c36-a028-42f16fd2d466.jpg',0,1),(58,14,'/uploads/products/d29463fd-df6e-47f4-9d2b-6e32a70ab540.jpg',0,2),(59,13,'/uploads/products/1b85edc9-b6eb-4df8-b0cd-1a2a6d0c3c17.jpg',1,0),(60,13,'/uploads/products/00fe3301-66a5-4186-9b2d-1bbe78b26a61.webp',0,1),(61,13,'/uploads/products/6c83ea23-e830-4e32-8c31-aadd212e0d7d.webp',0,2),(62,12,'/uploads/products/a43e2445-9ba9-4d68-a0d3-86437133469e.jpg',1,0),(63,12,'/uploads/products/45f450e7-9c98-4ccc-80b7-8312c5c7d650.jpg',0,1),(64,12,'/uploads/products/a814a829-bbd1-48fe-b7ed-20011a89cfc3.jpg',0,2),(65,11,'/uploads/products/96a76f10-1e25-4cda-afb0-eebb53c6e00b.jpg',1,0),(66,11,'/uploads/products/41e2fb10-c049-42ce-9caf-6989438cdc0d.jpg',0,1),(67,10,'/uploads/products/f652ff96-046c-4442-b092-5363f61769aa.jpg',1,0),(68,10,'/uploads/products/a4738eb3-7009-4d8d-b083-8f1d2a045fbc.jpg',0,1),(69,10,'/uploads/products/b7b7e387-3e84-4fa3-aa76-3649b1aff8b1.jpg',0,2),(70,9,'/uploads/products/40e2ca94-a72b-44a5-9567-39f448a850df.jpg',1,0),(71,9,'/uploads/products/22099e30-cb04-4dfe-afb5-19bb8ed29658.jpg',0,1),(72,9,'/uploads/products/e4c89ece-07b6-4802-af0c-f89fa870e5d5.jpg',0,2),(73,8,'/uploads/products/9546ef7d-82ac-472a-9637-f8c4183bb5cb.webp',1,0),(74,8,'/uploads/products/006e253d-41d4-4726-8df5-d01fb5d2e213.png',0,1),(75,8,'/uploads/products/bd46e8b6-5fef-400d-b277-3e2cad21d724.jpg',0,2),(76,7,'/uploads/products/728aa301-8e2e-4c97-a1b2-ae22f251d253.jpg',1,0),(77,7,'/uploads/products/6eab5e92-b40d-4e73-930a-935de609c338.jpg',0,1),(78,6,'/uploads/products/dfd8c538-19a9-4cd1-b0ef-cda09fa53158.jpg',1,0),(79,6,'/uploads/products/ee691847-c159-4a2b-b63e-834a05dd5180.jpg',0,1),(80,15,'/uploads/products/40f1a336-7d8a-4e41-af49-ebd22562e09a.jpg',1,0),(81,15,'/uploads/products/284f7779-fb27-4444-b4f4-96acf6101478.jpg',0,1),(82,15,'/uploads/products/5dc1de24-e96f-4100-8f59-8acbf68b6768.jpg',0,2);
/*!40000 ALTER TABLE `product_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `slug` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('FOOD','DRINK') COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `rating_avg` decimal(3,2) NOT NULL DEFAULT '0.00',
  `rating_count` int NOT NULL DEFAULT '0',
  `is_available` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`),
  KEY `idx_products_name` (`name`),
  KEY `idx_products_type` (`type`),
  KEY `idx_products_price` (`price`),
  KEY `idx_products_rating_avg` (`rating_avg`),
  KEY `idx_products_category_id` (`category_id`),
  KEY `idx_products_is_available` (`is_available`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,1,'Cà Phê Đen Đá','ca-phe-den-da','DRINK',25000.00,'Cà phê rang xay nguyên chất pha phin truyền thống, đậm vị đắng.',4.80,150,1,'2026-07-07 02:34:11','2026-07-07 03:10:05'),(2,1,'Bạc Xỉu Sài Gòn','bac-xiu-sai-gon','DRINK',35000.00,'Sự kết hợp hoàn hảo giữa sữa đặc sữa tươi và một chút cà phê.',4.90,210,1,'2026-07-07 02:34:11','2026-07-07 03:11:09'),(3,2,'Trà Đào Cam Sả','tra-dao-cam-sa','DRINK',45000.00,'Trà đen thanh mát kết hợp cùng đào ngâm ngọt lịm và hương sả thơm lừng.',4.70,320,1,'2026-07-07 02:34:11','2026-07-07 03:14:19'),(4,2,'Trà Sữa Trân Châu Đường Đen','tra-sua-tran-chau-duong-den','DRINK',55000.00,'Sữa tươi nguyên kem kết hợp trân châu nấu đường đen dẻo thơm.',5.00,500,1,'2026-07-07 02:34:11','2026-07-07 03:15:01'),(5,3,'Nước Ép Dưa Hấu','nuoc-ep-dua-hau','DRINK',40000.00,'Nước ép dưa hấu tươi 100%, không thêm đường, giải nhiệt cực đã.',4.50,85,1,'2026-07-07 02:34:11','2026-07-07 02:37:01'),(6,3,'Sinh Tố Bơ','sinh-to-bo','DRINK',50000.00,'Bơ sáp Đà Lạt xay cùng sữa đặc và đá bào béo ngậy.',4.60,112,1,'2026-07-07 02:34:11','2026-07-07 02:39:17'),(7,4,'Gà Rán Phủ Phô Mai','ga-ran-phu-pho-mai','FOOD',85000.00,'Gà rán giòn rụm tẩm sốt phô mai thơm lừng chuẩn vị Hàn Quốc.',4.80,250,1,'2026-07-07 02:34:11','2026-07-07 02:40:41'),(8,4,'Khoai Tây Chiên Cỡ Lớn','khoai-tay-chien-co-lon','FOOD',35000.00,'Khoai tây chiên vàng giòn rắc chút muối tinh.',4.30,95,1,'2026-07-07 02:34:11','2026-07-07 02:42:29'),(9,5,'Burger Bò Phô Mai Chảy','burger-bo-pho-mai-chay','FOOD',75000.00,'Vỏ bánh mềm, nhân thịt bò nướng tảng ép kèm phô mai Cheddar.',4.90,310,1,'2026-07-07 02:34:11','2026-07-07 02:43:30'),(10,5,'Pizza Hải Sản Nhiệt Đới','pizza-hai-san-nhiet-doi','FOOD',159000.00,'Pizza đế mỏng giòn với tôm, mực, dứa và phô mai Mozzarella kéo sợi.',4.70,180,1,'2026-07-07 02:34:11','2026-07-07 02:44:56'),(11,6,'Salad Ức Gà Xốt Mè Rang','salad-uc-ga-xot-me-rang','FOOD',65000.00,'Rau xà lách tươi, cà chua bi, ngô ngọt và ức gà nướng trộn xốt mè rang.',4.60,140,1,'2026-07-07 02:34:11','2026-07-07 02:47:27'),(12,6,'Salad Cá Hồi','salad-ca-hoi','FOOD',95000.00,'Cá hồi Na Uy áp chảo ăn kèm salad hỗn hợp và xốt chanh dây.',4.80,75,1,'2026-07-07 02:34:11','2026-07-07 02:48:09'),(13,7,'Bánh Flan Caramel','banh-flan-caramel','FOOD',25000.00,'Bánh flan mềm mịn, béo ngậy vị trứng sữa hòa quyện cùng caramel đắng nhẹ.',4.90,420,1,'2026-07-07 02:34:11','2026-07-07 02:50:05'),(14,7,'Bánh Tiramisu','banh-tiramisu','FOOD',55000.00,'Bánh ngọt chuẩn vị Ý với cốt bánh ladyfinger nhúng cà phê và lớp kem mascarpone.',4.70,165,1,'2026-07-07 02:34:11','2026-07-08 06:21:09'),(15,1,'Cà phên chồn','ca-phen-chon','DRINK',70000.00,'Cà phê chồn (hay Kopi Luwak) là một trong những loại cà phê đắt giá và độc đáo nhất thế giới. Sản phẩm này được tạo ra nhờ loài cầy vòi hương ăn quả cà phê, sau đó hạt được thải qua phân, thu gom, làm sạch và rang xay. Quá trình lên men tự nhiên trong dạ dày chồn tạo nên hương vị béo ngậy, ít đắng, mượt mà và vô cùng thơm ngon.',0.00,0,1,'2026-07-08 06:25:14','2026-07-08 06:25:14');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ratings`
--

DROP TABLE IF EXISTS `ratings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ratings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `stars` tinyint NOT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_rating_user_product` (`user_id`,`product_id`),
  KEY `idx_ratings_product_id` (`product_id`),
  CONSTRAINT `ratings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ratings_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ratings_chk_1` CHECK (((`stars` >= 1) and (`stars` <= 5)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ratings`
--

LOCK TABLES `ratings` WRITE;
/*!40000 ALTER TABLE `ratings` DISABLE KEYS */;
/*!40000 ALTER TABLE `ratings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `suggestions`
--

DROP TABLE IF EXISTS `suggestions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `suggestions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('PENDING','REVIEWED','APPROVED','REJECTED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `admin_note` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_suggestions_user_id` (`user_id`),
  KEY `idx_suggestions_status` (`status`),
  CONSTRAINT `suggestions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `suggestions`
--

LOCK TABLES `suggestions` WRITE;
/*!40000 ALTER TABLE `suggestions` DISABLE KEYS */;
INSERT INTO `suggestions` VALUES (1,2,'Tôi thích bạn thêm món \"Cơm cháy Ninh Bình\" vào menu.\r\nRất phù hợp với việc ăn vặt','PENDING',NULL,'2026-07-07 03:17:53','2026-07-07 03:17:53'),(2,2,'Tôi muốn thêm gỏi cá Kim Sơn vào món ăn nhé\r\nđề nghị Ad xem xét cho vào thực đơn','PENDING',NULL,'2026-07-07 03:27:14','2026-07-07 03:27:14'),(3,2,'test','PENDING',NULL,'2026-07-07 04:19:19','2026-07-07 04:19:19'),(4,2,'test2','PENDING',NULL,'2026-07-07 04:19:22','2026-07-07 04:19:22'),(5,2,'test 3','PENDING',NULL,'2026-07-07 04:19:25','2026-07-07 04:19:25'),(6,2,'test 4','PENDING',NULL,'2026-07-07 04:19:28','2026-07-07 04:19:28'),(7,2,'test 5','PENDING',NULL,'2026-07-07 04:19:31','2026-07-07 04:19:31'),(8,2,'test 6','PENDING',NULL,'2026-07-07 04:19:37','2026-07-07 04:19:37'),(9,2,'test 7','PENDING',NULL,'2026-07-07 04:19:42','2026-07-07 04:19:42'),(10,2,'test 8','PENDING',NULL,'2026-07-07 04:19:45','2026-07-07 04:19:45'),(11,2,'test 9','PENDING',NULL,'2026-07-07 04:19:51','2026-07-07 04:19:51'),(12,2,'Tôi thích bạn thêm món \"Cơm cháy Ninh Bình\" vào menu. Rất phù hợp với việc ăn vặt','PENDING',NULL,'2026-07-07 04:20:01','2026-07-07 04:20:01'),(13,2,'Tôi muốn thêm gỏi cá Kim Sơn vào món ăn nhé đề nghị Ad xem xét cho vào thực đơn','PENDING',NULL,'2026-07-07 04:20:09','2026-07-07 04:20:09');
/*!40000 ALTER TABLE `suggestions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('ROLE_USER','ROLE_ADMIN') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ROLE_USER',
  `auth_provider` enum('LOCAL','GOOGLE','FACEBOOK','TWITTER') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'LOCAL',
  `provider_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin@gmail.com','$2a$10$TwjQxvyzkHuobVmionTW0eoZY2mwXMWHaf7xzCbpPQA4dE1WrAKNO','huu hoang','0314567980',NULL,'ROLE_ADMIN','LOCAL',NULL,1,'2026-06-25 06:55:46','2026-06-25 06:56:17'),(2,'huuhv','huuhv@gmail.com','$2a$10$3lAgYIT9MxEarT5grGHlmOeMQIq8AkhoAxAIIf5a0xy4qmEJNZ9Ta','huuhv','0321456790',NULL,'ROLE_USER','LOCAL',NULL,1,'2026-06-25 06:56:51','2026-06-25 06:56:51'),(7,'huuhv1','a@a.vn','$2a$10$32c0YTUIvlRqMPmZq/IPreBhoquoTeZIKqCBoHs/G6O2W4gMZm6Ra','aaaaa','0987654321',NULL,'ROLE_USER','LOCAL',NULL,1,'2026-06-25 07:42:50','2026-06-25 07:42:50'),(8,'huuhv2','aa@a.vn','$2a$10$curMtPVNMVQRO.Ui3qO0ieAwnpTV.UE58FLTa0KVkMTQJyTUR4tH.','fdafadfads','0987654320',NULL,'ROLE_USER','LOCAL',NULL,1,'2026-06-25 07:45:21','2026-06-25 07:45:21'),(9,'huuhv3','huuhv3@gmail.com','$2a$10$kmJVMqX/m8LzsKiruhkKneyhAtnFwsduAq3YahZpUNFJfej5fUb4W','afsdfasdfs','0987654322',NULL,'ROLE_USER','LOCAL',NULL,1,'2026-06-25 07:46:38','2026-06-25 07:46:38'),(10,'huuhv4','aaa@a.vn','$2a$10$H6YC9lqeY6d6Fx9cJjIz2eOJmYzZxW1QUOUXLGOG3RypGHYpFGrCG','fadfsdfs','0965643213',NULL,'ROLE_USER','LOCAL',NULL,1,'2026-06-25 07:49:10','2026-06-25 07:49:10'),(11,'huuhv5','aaaa@g.vn','$2a$10$SboSC3ADHo1cpAwy/8lv5OGv0DrTEF5fzWgpsR4VFIhZWCnOXS09q','dafdasfsd','0321456789',NULL,'ROLE_USER','LOCAL',NULL,1,'2026-06-25 09:32:44','2026-06-25 09:32:44'),(12,'huu-hoang-van','hoang.van.huu@sun-asterisk.com',NULL,'Huu Hoang Van',NULL,'https://lh3.googleusercontent.com/a/ACg8ocIFdcf9sQyFENb9BY6Q5ypoHS4qaCI-SERq2mW9Guor-sNmRQM=s96-c','ROLE_USER','GOOGLE','117982618985446058529',1,'2026-07-07 03:31:49','2026-07-07 03:31:49');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-08 14:20:11
