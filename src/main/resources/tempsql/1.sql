/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 5.7.38-log : Database - db01
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`db01` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `db01`;

/*Table structure for table `comment` */

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `comment_id` int(5) NOT NULL AUTO_INCREMENT,
  `comment_post_id` int(5) NOT NULL,
  `comment_user_id` int(5) NOT NULL,
  `comment_time` bigint(15) DEFAULT NULL,
  `comment_content` varchar(255) NOT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `comment_post_id` (`comment_post_id`),
  KEY `comment_user_id` (`comment_user_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`comment_post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`comment_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `comment` */

insert  into `comment`(`comment_id`,`comment_post_id`,`comment_user_id`,`comment_time`,`comment_content`) values 
(1,1,1,1665808254018,'This is the first time to make comment'),
(2,1,1,1665808270057,'you guys love ?');

/*Table structure for table `friendlist` */

DROP TABLE IF EXISTS `friendlist`;

CREATE TABLE `friendlist` (
  `list_id` int(5) NOT NULL AUTO_INCREMENT,
  `user_id` int(5) NOT NULL,
  `friend_id` int(5) NOT NULL,
  PRIMARY KEY (`list_id`),
  KEY `user_id` (`user_id`),
  KEY `friend_id` (`friend_id`),
  CONSTRAINT `friendlist_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `friendlist_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `friendlist` */

/*Table structure for table `image` */

DROP TABLE IF EXISTS `image`;

CREATE TABLE `image` (
  `image_id` int(5) NOT NULL AUTO_INCREMENT,
  `image_post_id` int(5) NOT NULL,
  `image_url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `image_post_id` (`image_post_id`),
  CONSTRAINT `image_ibfk_1` FOREIGN KEY (`image_post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `image` */

insert  into `image`(`image_id`,`image_post_id`,`image_url`) values 
(1,1,'http://localhost:8080/images/741917776/360e2253-9b83-4079-8eeb-4b902161fb04.png'),
(2,1,'http://localhost:8080/images/741917776/03cbf755-6f20-46ae-8961-a959d6cd70f2.jpg'),
(3,2,'http://localhost:8080/images/741917776/54a7e114-0411-4177-8237-63b40fe2ed88.jpg'),
(4,2,'http://localhost:8080/images/741917776/61390444-1704-4524-bfd5-1966f4776053.jpg'),
(5,3,'http://localhost:8080/images/532540232/8f42a495-e1c9-4f6b-b1e2-8efaf49a8f13.jpg'),
(6,3,'http://localhost:8080/images/532540232/66e71bc9-2b16-49df-aae2-916e2f49897e.jpg');

/*Table structure for table `pet` */

DROP TABLE IF EXISTS `pet`;

CREATE TABLE `pet` (
  `pet_id` int(5) NOT NULL AUTO_INCREMENT,
  `pet_user_id` int(5) NOT NULL,
  `name` varchar(20) NOT NULL,
  `pet_image_address` varchar(100) DEFAULT NULL,
  `category` varchar(10) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `pet_description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`pet_id`),
  KEY `pet_user_id` (`pet_user_id`),
  CONSTRAINT `pet_ibfk_1` FOREIGN KEY (`pet_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

/*Data for the table `pet` */

insert  into `pet`(`pet_id`,`pet_user_id`,`name`,`pet_image_address`,`category`,`age`,`pet_description`) values 
(1,1,'DuDu','http://localhost:8080/images/741917776/d1db76dc-9618-4fab-9d2f-6260feea7ceb.jpg','cat',NULL,'He is my third cat, cool boy'),
(2,1,'koala','http://localhost:8080/images/741917776/d078c786-4ab9-40b5-8676-0d0b78aa0ffa.jpg','cat',NULL,'Just test for koala, she is not a cat!   QvQ'),
(3,1,'Miko','http://localhost:8080/images/741917776/0140ad2e-6116-4430-9713-610579b7c846.jpg','cat',NULL,'She is Miko, she love sleeping'),
(4,3,'XiXi','http://localhost:8080/images/532540232/2730dc81-2a0e-40fd-b642-47ec55f0a0f1.jpg','dog',NULL,'He is XiXi, my puppy');

/*Table structure for table `petimage` */

DROP TABLE IF EXISTS `petimage`;

CREATE TABLE `petimage` (
  `image_id` int(5) NOT NULL AUTO_INCREMENT,
  `image_pet_id` int(5) NOT NULL,
  `image_url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `image_pet_id` (`image_pet_id`),
  CONSTRAINT `petimage_ibfk_1` FOREIGN KEY (`image_pet_id`) REFERENCES `pet` (`pet_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

/*Data for the table `petimage` */

insert  into `petimage`(`image_id`,`image_pet_id`,`image_url`) values 
(1,1,'http://localhost:8080/images/741917776/278269c8-3421-4f84-87e5-cb6ae8123bdb.png'),
(2,1,'http://localhost:8080/images/741917776/a2bd5504-4dd0-4e47-8a63-2ae6f8ddaa4a.jpg'),
(3,2,'http://localhost:8080/images/741917776/cf5ea12f-b091-4eff-8d2b-4f9a72cc068f.jpg'),
(4,3,'http://localhost:8080/images/741917776/8eb2e972-5185-49c6-bfbd-8bbfd777ce41.png'),
(5,3,'http://localhost:8080/images/741917776/97dfe263-874c-422a-bfdd-75aa8d870b54.jpg'),
(6,4,'http://localhost:8080/images/532540232/09826501-5b87-4d13-ae22-35730b3dbec8.jpg'),
(7,4,'http://localhost:8080/images/532540232/e1262a77-d58b-4555-b755-42253ef41518.jpg');

/*Table structure for table `post` */

DROP TABLE IF EXISTS `post`;

CREATE TABLE `post` (
  `post_id` int(5) NOT NULL AUTO_INCREMENT,
  `post_user_id` int(5) NOT NULL,
  `post_content` varchar(255) NOT NULL,
  `post_time` bigint(15) DEFAULT NULL,
  `topic` varchar(100) DEFAULT NULL,
  `tag` varchar(100) DEFAULT NULL,
  `love` int(5) DEFAULT '0',
  PRIMARY KEY (`post_id`),
  KEY `post_user_id` (`post_user_id`),
  FULLTEXT KEY `idx_content` (`post_content`),
  FULLTEXT KEY `idx_topic` (`topic`),
  CONSTRAINT `post_ibfk_1` FOREIGN KEY (`post_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

/*Data for the table `post` */

insert  into `post`(`post_id`,`post_user_id`,`post_content`,`post_time`,`topic`,`tag`,`love`) values 
(1,1,'You guys love them? ',1665808029642,'This is the first post of project',NULL,0),
(2,1,'How beautiful and cute are them ',1665808141656,'I know you love them',NULL,0),
(3,3,'XiXi feel happy when hanging out',1665809020128,'XiXi is a lovely dog',NULL,0);

/*Table structure for table `requestlist` */

DROP TABLE IF EXISTS `requestlist`;

CREATE TABLE `requestlist` (
  `request_id` int(5) NOT NULL AUTO_INCREMENT,
  `user_id_from` int(5) NOT NULL,
  `user_id_to` int(5) NOT NULL,
  PRIMARY KEY (`request_id`),
  KEY `user_id_from` (`user_id_from`),
  KEY `user_id_to` (`user_id_to`),
  CONSTRAINT `requestlist_ibfk_1` FOREIGN KEY (`user_id_from`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `requestlist_ibfk_2` FOREIGN KEY (`user_id_to`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `requestlist` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `password` varchar(40) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `uuid` varchar(80) DEFAULT NULL,
  `user_image_address` varchar(100) DEFAULT 'http://localhost/images/default.jpg',
  `nickname` varchar(20) DEFAULT 'lazy to set name',
  `description` varchar(255) DEFAULT 'this guy is very lazy to write decription',
  `tag` varchar(20) DEFAULT 'Cat',
  `gender` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `USERNAME` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`username`,`password`,`email`,`uuid`,`user_image_address`,`nickname`,`description`,`tag`,`gender`) values 
(1,'741917776','UBL3wzfOlscd2jT0oxy9QA==','741917776@qq.com',NULL,'http://localhost:8080/images/default.jpg','lazy to set name','this guy is very lazy to write decription','Cat',1),
(2,'richard','PDLx+YKFRPT7WSobvjHdrQMb62ozycMd','67252808@qq.com','0d70eb80-603d-43b9-b7d6-8226687f0e5f','http://localhost:8080/images/default.jpg','lazy to set name','this guy is very lazy to write decription','Cat',1),
(3,'532540232','5hvbrRS/UhwVG9mf0JBMkQ==','741917776@qq.com','fed76004-fa61-4d02-bab6-2bdd0f5f5a8b','https://avatars.dicebear.com/api/adventurer/532540232.jpg','Isidro','this guy is very lazy to write decription','Cat',1);

/*Table structure for table `userlove` */

DROP TABLE IF EXISTS `userlove`;

CREATE TABLE `userlove` (
  `user_love_id` int(5) NOT NULL AUTO_INCREMENT,
  `post_id` int(5) NOT NULL,
  `user_id` int(5) NOT NULL,
  PRIMARY KEY (`user_love_id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `userlove_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `userlove_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `userlove` */

/*Table structure for table `video` */

DROP TABLE IF EXISTS `video`;

CREATE TABLE `video` (
  `video_id` int(5) NOT NULL AUTO_INCREMENT,
  `video_post_id` int(5) NOT NULL,
  `video_url` varchar(50) NOT NULL,
  PRIMARY KEY (`video_id`),
  KEY `video_post_id` (`video_post_id`),
  CONSTRAINT `video_ibfk_1` FOREIGN KEY (`video_post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `video` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
