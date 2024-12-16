-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: abc_cinema
-- ------------------------------------------------------
-- Server version	9.1.0

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
-- Table structure for table `Contact`
--

DROP TABLE IF EXISTS `Contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Contact` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Contact`
--

LOCK TABLES `Contact` WRITE;
/*!40000 ALTER TABLE `Contact` DISABLE KEYS */;
/*!40000 ALTER TABLE `Contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` varchar(50) NOT NULL,
  `password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `ticket_no` int NOT NULL AUTO_INCREMENT,
  `movie_id` int NOT NULL,
  `showtime_id` int NOT NULL,
  `user_id` int NOT NULL,
  `seat_number` varchar(5) NOT NULL,
  `status` varchar(20) DEFAULT NULL,
  `booking_date` date NOT NULL,
  `booking_time` time NOT NULL,
  PRIMARY KEY (`ticket_no`),
  KEY `movie_id` (`movie_id`),
  KEY `showtime_id` (`showtime_id`),
  KEY `user_id` (`user_id`),
  KEY `seat_number` (`seat_number`),
  CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`),
  CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`showtime_id`) REFERENCES `showtimes` (`id`),
  CONSTRAINT `bookings_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `bookings_ibfk_4` FOREIGN KEY (`seat_number`) REFERENCES `seats` (`seat_number`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movie_showtime`
--

DROP TABLE IF EXISTS `movie_showtime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie_showtime` (
  `movie_id` int NOT NULL,
  `showtime_id` int NOT NULL,
  `balcony_ticket_price` decimal(10,2) DEFAULT NULL,
  `normal_ticket_price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`movie_id`,`showtime_id`),
  KEY `showtime_id` (`showtime_id`),
  CONSTRAINT `movie_showtime_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `movie_showtime_ibfk_2` FOREIGN KEY (`showtime_id`) REFERENCES `showtimes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie_showtime`
--

LOCK TABLES `movie_showtime` WRITE;
/*!40000 ALTER TABLE `movie_showtime` DISABLE KEYS */;
/*!40000 ALTER TABLE `movie_showtime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movies`
--

DROP TABLE IF EXISTS `movies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `movie_title` varchar(255) DEFAULT NULL,
  `movie_poster` varchar(255) DEFAULT NULL,
  `movie_description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movies`
--

LOCK TABLES `movies` WRITE;
/*!40000 ALTER TABLE `movies` DISABLE KEYS */;
/*!40000 ALTER TABLE `movies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `order_id` varchar(50) NOT NULL,
  `payment_id` varchar(50) DEFAULT NULL,
  `ticket_nos` text NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seats`
--

DROP TABLE IF EXISTS `seats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seats` (
  `seat_number` varchar(5) NOT NULL,
  `seat_type` enum('normal','balcony') NOT NULL,
  `status` enum('available','booked') DEFAULT 'available',
  PRIMARY KEY (`seat_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seats`
--

LOCK TABLES `seats` WRITE;
/*!40000 ALTER TABLE `seats` DISABLE KEYS */;
INSERT INTO `seats` VALUES ('A01','normal','available'),('A02','normal','available'),('A03','normal','available'),('A04','normal','available'),('A05','normal','available'),('A06','normal','available'),('A07','normal','available'),('A08','normal','available'),('A09','normal','available'),('A10','normal','available'),('A11','normal','available'),('A12','normal','available'),('A13','normal','available'),('A14','normal','available'),('A15','normal','available'),('A16','normal','available'),('A17','normal','available'),('A18','normal','available'),('A19','normal','available'),('A20','normal','available'),('B01','normal','available'),('B02','normal','available'),('B03','normal','available'),('B04','normal','available'),('B05','normal','available'),('B06','normal','available'),('B07','normal','available'),('B08','normal','available'),('B09','normal','available'),('B10','normal','available'),('B11','normal','available'),('B12','normal','available'),('B13','normal','available'),('B14','normal','available'),('B15','normal','available'),('B16','normal','available'),('B17','normal','available'),('B18','normal','available'),('B19','normal','available'),('B20','normal','available'),('C01','normal','available'),('C02','normal','available'),('C03','normal','available'),('C04','normal','available'),('C05','normal','available'),('C06','normal','available'),('C07','normal','available'),('C08','normal','available'),('C09','normal','available'),('C10','normal','available'),('C11','normal','available'),('C12','normal','available'),('C13','normal','available'),('C14','normal','available'),('C15','normal','available'),('C16','normal','available'),('C17','normal','available'),('C18','normal','available'),('C19','normal','available'),('C20','normal','available'),('D01','normal','available'),('D02','normal','available'),('D03','normal','available'),('D04','normal','available'),('D05','normal','available'),('D06','normal','available'),('D07','normal','available'),('D08','normal','available'),('D09','normal','available'),('D10','normal','available'),('D11','normal','available'),('D12','normal','available'),('D13','normal','available'),('D14','normal','available'),('D15','normal','available'),('D16','normal','available'),('D17','normal','available'),('D18','normal','available'),('D19','normal','available'),('D20','normal','available'),('E01','normal','available'),('E02','normal','available'),('E03','normal','available'),('E04','normal','available'),('E05','normal','available'),('E06','normal','available'),('E07','normal','available'),('E08','normal','available'),('E09','normal','available'),('E10','normal','available'),('E11','normal','available'),('E12','normal','available'),('E13','normal','available'),('E14','normal','available'),('E15','normal','available'),('E16','normal','available'),('E17','normal','available'),('E18','normal','available'),('E19','normal','available'),('E20','normal','available'),('F01','normal','available'),('F02','normal','available'),('F03','normal','available'),('F04','normal','available'),('F05','normal','available'),('F06','normal','available'),('F07','normal','available'),('F08','normal','available'),('F09','normal','available'),('F10','normal','available'),('F11','normal','available'),('F12','normal','available'),('F13','normal','available'),('F14','normal','available'),('F15','normal','available'),('F16','normal','available'),('F17','normal','available'),('F18','normal','available'),('F19','normal','available'),('F20','normal','available'),('G01','normal','available'),('G02','normal','available'),('G03','normal','available'),('G04','normal','available'),('G05','normal','available'),('G06','normal','available'),('G07','normal','available'),('G08','normal','available'),('G09','normal','available'),('G10','normal','available'),('G11','normal','available'),('G12','normal','available'),('G13','normal','available'),('G14','normal','available'),('G15','normal','available'),('G16','normal','available'),('G17','normal','available'),('G18','normal','available'),('G19','normal','available'),('G20','normal','available'),('H01','normal','available'),('H02','normal','available'),('H03','normal','available'),('H04','normal','available'),('H05','normal','available'),('H06','normal','available'),('H07','normal','available'),('H08','normal','available'),('H09','normal','available'),('H10','normal','available'),('H11','normal','available'),('H12','normal','available'),('H13','normal','available'),('H14','normal','available'),('H15','normal','available'),('H16','normal','available'),('H17','normal','available'),('H18','normal','available'),('H19','normal','available'),('H20','normal','available'),('I01','normal','available'),('I02','normal','available'),('I03','normal','available'),('I04','normal','available'),('I05','normal','available'),('I06','normal','available'),('I07','normal','available'),('I08','normal','available'),('I09','normal','available'),('I10','normal','available'),('I11','normal','available'),('I12','normal','available'),('I13','normal','available'),('I14','normal','available'),('I15','normal','available'),('I16','normal','available'),('I17','normal','available'),('I18','normal','available'),('I19','normal','available'),('I20','normal','available'),('J01','normal','available'),('J02','normal','available'),('J03','normal','available'),('J04','normal','available'),('J05','normal','available'),('J06','normal','available'),('J07','normal','available'),('J08','normal','available'),('J09','normal','available'),('J10','normal','available'),('J11','normal','available'),('J12','normal','available'),('J13','normal','available'),('J14','normal','available'),('J15','normal','available'),('J16','normal','available'),('J17','normal','available'),('J18','normal','available'),('J19','normal','available'),('J20','normal','available'),('K01','normal','available'),('K02','normal','available'),('K03','normal','available'),('K04','normal','available'),('K05','normal','available'),('K06','normal','available'),('K07','normal','available'),('K08','normal','available'),('K09','normal','available'),('K10','normal','available'),('K11','normal','available'),('K12','normal','available'),('K13','normal','available'),('K14','normal','available'),('K15','normal','available'),('K16','normal','available'),('K17','normal','available'),('K18','normal','available'),('K19','normal','available'),('K20','normal','available'),('L01','normal','available'),('L02','normal','available'),('L03','normal','available'),('L04','normal','available'),('L05','normal','available'),('L06','normal','available'),('L07','normal','available'),('L08','normal','available'),('L09','normal','available'),('L10','normal','available'),('L11','normal','available'),('L12','normal','available'),('L13','normal','available'),('L14','normal','available'),('L15','normal','available'),('L16','normal','available'),('L17','normal','available'),('L18','normal','available'),('L19','normal','available'),('L20','normal','available'),('M01','normal','available'),('M02','normal','available'),('M03','normal','available'),('M04','normal','available'),('M05','normal','available'),('M06','normal','available'),('M07','normal','available'),('M08','normal','available'),('M09','normal','available'),('M10','normal','available'),('M11','normal','available'),('M12','normal','available'),('M13','normal','available'),('M14','normal','available'),('M15','normal','available'),('M16','normal','available'),('M17','normal','available'),('M18','normal','available'),('M19','normal','available'),('M20','normal','available'),('N01','normal','available'),('N02','normal','available'),('N03','normal','available'),('N04','normal','available'),('N05','normal','available'),('N06','normal','available'),('N07','normal','available'),('N08','normal','available'),('N09','normal','available'),('N10','normal','available'),('N11','normal','available'),('N12','normal','available'),('N13','normal','available'),('N14','normal','available'),('N15','normal','available'),('N16','normal','available'),('N17','normal','available'),('N18','normal','available'),('N19','normal','available'),('N20','normal','available'),('O01','balcony','available'),('O02','balcony','available'),('O03','balcony','available'),('O04','balcony','available'),('O05','balcony','available'),('O06','balcony','available'),('O07','balcony','available'),('O08','balcony','available'),('O09','balcony','available'),('O10','balcony','available'),('O11','balcony','available'),('O12','balcony','available'),('O13','balcony','available'),('O14','balcony','available'),('O15','balcony','available'),('O16','balcony','available'),('O17','balcony','available'),('O18','balcony','available'),('O19','balcony','available'),('O20','balcony','available'),('P01','balcony','available'),('P02','balcony','available'),('P03','balcony','available'),('P04','balcony','available'),('P05','balcony','available'),('P06','balcony','available'),('P07','balcony','available'),('P08','balcony','available'),('P09','balcony','available'),('P10','balcony','available'),('P11','balcony','available'),('P12','balcony','available'),('P13','balcony','available'),('P14','balcony','available'),('P15','balcony','available'),('P16','balcony','available'),('P17','balcony','available'),('P18','balcony','available'),('P19','balcony','available'),('P20','balcony','available'),('Q01','balcony','available'),('Q02','balcony','available'),('Q03','balcony','available'),('Q04','balcony','available'),('Q05','balcony','available'),('Q06','balcony','available'),('Q07','balcony','available'),('Q08','balcony','available'),('Q09','balcony','available'),('Q10','balcony','available'),('Q11','balcony','available'),('Q12','balcony','available'),('Q13','balcony','available'),('Q14','balcony','available'),('Q15','balcony','available'),('Q16','balcony','available'),('Q17','balcony','available'),('Q18','balcony','available'),('Q19','balcony','available'),('Q20','balcony','available'),('R01','balcony','available'),('R02','balcony','available'),('R03','balcony','available'),('R04','balcony','available'),('R05','balcony','available'),('R06','balcony','available'),('R07','balcony','available'),('R08','balcony','available'),('R09','balcony','available'),('R10','balcony','available'),('R11','balcony','available'),('R12','balcony','available'),('R13','balcony','available'),('R14','balcony','available'),('R15','balcony','available'),('R16','balcony','available'),('R17','balcony','available'),('R18','balcony','available'),('R19','balcony','available'),('R20','balcony','available'),('S01','balcony','available'),('S02','balcony','available'),('S03','balcony','available'),('S04','balcony','available'),('S05','balcony','available'),('S06','balcony','available'),('S07','balcony','available'),('S08','balcony','available'),('S09','balcony','available'),('S10','balcony','available'),('S11','balcony','available'),('S12','balcony','available'),('S13','balcony','available'),('S14','balcony','available'),('S15','balcony','available'),('S16','balcony','available'),('S17','balcony','available'),('S18','balcony','available'),('S19','balcony','available'),('S20','balcony','available'),('T01','balcony','available'),('T02','balcony','available'),('T03','balcony','available'),('T04','balcony','available'),('T05','balcony','available'),('T06','balcony','available'),('T07','balcony','available'),('T08','balcony','available'),('T09','balcony','available'),('T10','balcony','available'),('T11','balcony','available'),('T12','balcony','available'),('T13','balcony','available'),('T14','balcony','available'),('T15','balcony','available'),('T16','balcony','available'),('T17','balcony','available'),('T18','balcony','available'),('T19','balcony','available'),('T20','balcony','available'),('U01','balcony','available'),('U02','balcony','available'),('U03','balcony','available'),('U04','balcony','available'),('U05','balcony','available'),('U06','balcony','available'),('U07','balcony','available'),('U08','balcony','available'),('U09','balcony','available'),('U10','balcony','available'),('U11','balcony','available'),('U12','balcony','available'),('U13','balcony','available'),('U14','balcony','available'),('U15','balcony','available'),('U16','balcony','available'),('U17','balcony','available'),('U18','balcony','available'),('U19','balcony','available'),('U20','balcony','available');
/*!40000 ALTER TABLE `seats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `showtimes`
--

DROP TABLE IF EXISTS `showtimes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `showtimes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `display_time` varchar(20) DEFAULT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `showtimes`
--

LOCK TABLES `showtimes` WRITE;
/*!40000 ALTER TABLE `showtimes` DISABLE KEYS */;
/*!40000 ALTER TABLE `showtimes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone_number` varchar(15) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
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

-- Dump completed on 2024-12-15 14:31:06
