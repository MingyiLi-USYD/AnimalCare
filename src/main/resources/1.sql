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
CREATE DATABASE /*!32312 IF NOT EXISTS*/`db01` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `db01`;

/*Table structure for table `comment` */

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `comment_id` bigint(15) NOT NULL AUTO_INCREMENT,
  `comment_post_id` bigint(15) NOT NULL,
  `comment_user_id` bigint(15) NOT NULL,
  `comment_time` bigint(15) DEFAULT NULL,
  `comment_content` varchar(255) NOT NULL,
  `comment_love` bigint(15) DEFAULT '0',
  PRIMARY KEY (`comment_id`),
  KEY `comment_post_id` (`comment_post_id`),
  KEY `comment_user_id` (`comment_user_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`comment_post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`comment_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1677933883574657027 DEFAULT CHARSET=utf8;

/*Data for the table `comment` */

insert  into `comment`(`comment_id`,`comment_post_id`,`comment_user_id`,`comment_time`,`comment_content`,`comment_love`) values 
(999,1674831012545101828,1674780959554027522,1688505128580,'Nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit',0),
(1676338125301858305,1674831012545101828,1674658659785715714,1688505128581,'测试评论',0),
(1676382482922962946,1674781036062326786,1674658659785715714,1688515704264,'添加一个测试评论试试看',0),
(1676559709627207682,1674831012545101828,1674658659785715714,1688557958401,'再发个评论试试看',0),
(1677625760852299777,1674831012545101828,1674658659785715714,1688812124820,'再次发表评论',0),
(1677627351227518978,1674831012545101828,1674658659785715714,1688812503998,'测试评论啊',0),
(1677627813020389377,1674831012545101828,1674658659785715714,1688812614099,'发布',0),
(1677629076424404993,1674831012545101828,1674658659785715714,1688812915316,'再次评论',0),
(1677630350783983617,1674831012545101828,1674658659785715714,1688813219149,'无语了',0),
(1677630403955175426,1674831012545101828,1674658659785715714,1688813231826,'真的傻逼',0),
(1677922810469535745,1674781036062326786,1674658659785715714,1688882946969,'测试一下评论',0),
(1677932820192804866,1674831012545101825,1674658659785715714,1688885333474,'对post测试',0),
(1677932990213107714,1674831012545101825,1674658659785715714,1688885374010,'再次测试',0),
(1677933883574657026,1674831012545101825,1674658659785715714,1688885587004,'现在呢',0);

/*Table structure for table `image` */

DROP TABLE IF EXISTS `image`;

CREATE TABLE `image` (
  `image_id` bigint(15) NOT NULL AUTO_INCREMENT,
  `image_post_id` bigint(15) NOT NULL,
  `image_url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `image_post_id` (`image_post_id`),
  CONSTRAINT `image_ibfk_1` FOREIGN KEY (`image_post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `image` */

/*Table structure for table `pet` */

DROP TABLE IF EXISTS `pet`;

CREATE TABLE `pet` (
  `pet_id` bigint(15) NOT NULL AUTO_INCREMENT,
  `pet_user_id` bigint(15) NOT NULL,
  `name` varchar(20) NOT NULL,
  `pet_image_address` varchar(300) DEFAULT NULL,
  `category` varchar(10) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `pet_description` varchar(400) DEFAULT NULL,
  `pet_visible` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`pet_id`),
  KEY `pet_user_id` (`pet_user_id`),
  CONSTRAINT `pet_ibfk_1` FOREIGN KEY (`pet_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1682133292293410819 DEFAULT CHARSET=utf8;

/*Data for the table `pet` */

insert  into `pet`(`pet_id`,`pet_user_id`,`name`,`pet_image_address`,`category`,`age`,`pet_description`,`pet_visible`) values 
(1679450666651533313,1674658659785715714,'11','https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/kJyD9yk12fY5D3Sx9k2ofEg0Yw12%2F7759ae4b-5b43-49e4-9361-e66a3aff8359?alt=media&token=073005fd-f5a4-41d0-a8a1-87c23dcb4727','cat',NULL,'11',1),
(1679450729184411650,1674658659785715714,'12312','https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/kJyD9yk12fY5D3Sx9k2ofEg0Yw12%2F20b2e9de-939e-40e9-bee3-171f10eae085?alt=media&token=b1278e3c-3c8c-49a5-8ff5-3aa8ef32a27c','cat',NULL,'11',1),
(1682133292293410818,1674658659785715714,'DuDu','https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/kJyD9yk12fY5D3Sx9k2ofEg0Yw12%2Fc054dc0c-213a-4278-97db-fd6223c074c2?alt=media&token=ce190dd2-1758-4efc-8d7d-849944186a8f','cat',NULL,'DuDuAA',1);

/*Table structure for table `petimage` */

DROP TABLE IF EXISTS `petimage`;

CREATE TABLE `petimage` (
  `image_id` bigint(15) NOT NULL AUTO_INCREMENT,
  `image_pet_id` bigint(15) NOT NULL,
  `image_url` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`image_id`),
  KEY `image_pet_id` (`image_pet_id`),
  CONSTRAINT `petimage_ibfk_1` FOREIGN KEY (`image_pet_id`) REFERENCES `pet` (`pet_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1682974460548689923 DEFAULT CHARSET=utf8;

/*Data for the table `petimage` */

insert  into `petimage`(`image_id`,`image_pet_id`,`image_url`) values 
(1681551370060054530,1679450729184411650,'https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/kJyD9yk12fY5D3Sx9k2ofEg0Yw12petImages%2F1679450729184411650%2F148b3032-519e-4698-8434-7dda89f522d9?alt=media&token=64324a6a-be52-46ba-988a-9cb5b60c7f61'),
(1682974460548689922,1679450729184411650,'https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/kJyD9yk12fY5D3Sx9k2ofEg0Yw12petImages%2F1679450729184411650%2F6d80450c-2e84-453c-8628-88ea5d8b955c?alt=media&token=6ec7a236-06d2-4967-bc51-3dd73fbf4e93');

/*Table structure for table `post` */

DROP TABLE IF EXISTS `post`;

CREATE TABLE `post` (
  `post_id` bigint(15) NOT NULL AUTO_INCREMENT,
  `post_user_id` bigint(15) NOT NULL,
  `post_content` varchar(2000) NOT NULL,
  `post_time` bigint(15) DEFAULT NULL,
  `topic` varchar(100) DEFAULT NULL,
  `tag` varchar(100) DEFAULT NULL,
  `love` bigint(15) DEFAULT '0',
  `images` text NOT NULL,
  `visible` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`post_id`),
  KEY `post_user_id` (`post_user_id`),
  FULLTEXT KEY `idx_content` (`post_content`),
  FULLTEXT KEY `idx_topic` (`topic`),
  CONSTRAINT `post_ibfk_1` FOREIGN KEY (`post_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1683740783402758146 DEFAULT CHARSET=utf8;

/*Data for the table `post` */

insert  into `post`(`post_id`,`post_user_id`,`post_content`,`post_time`,`topic`,`tag`,`love`,`images`,`visible`) values 
(1674781036062326786,1674780959554027522,'1111',NULL,'Nisi ut aliquip ex ea commodo consequat',NULL,1,'[\"https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/0Mn3Y28B10YfE12nZoLv0gD2f2t1%2F661e1b06-d751-4942-9600-f1103c612124?alt=media&token=d93c64e3-3ce9-418f-a5bc-6d26d652b00a\"]',1),
(1674831012545101825,1674658659785715714,'111',NULL,'Nisi ut aliquip ex ea commodo consequat',NULL,1,'[\"https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/kJyD9yk12fY5D3Sx9k2ofEg0Yw12%2F8e6ca469-d72d-4842-a81f-b5e654e7c51f?alt=media&token=d87298aa-5559-4962-b18f-65dfb896a863\"]',1),
(1674831012545101828,1674780959554027522,'Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum Excepteur sint occaecat cupidatat non proident, sunt in Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum culpa qui officia deserunt mollit anim id est laborum',NULL,'Nisi ut aliquip ex ea commodo consequat',NULL,0,'[\"https://picsum.photos/200/300\",\"https://picsum.photos/200/301\",\"https://picsum.photos/200/302\"]',1),
(1674831012545101830,1674780959554027522,'Ut enim ad minim veniam',NULL,'Nisi ut aliquip ex ea commodo consequat',NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101831,1674780959554027522,'Quis nostrud exercitation ullamco laboris',NULL,'Nisi ut aliquip ex ea commodo consequat',NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101832,1674780959554027522,'Nisi ut aliquip ex ea commodo consequat',NULL,'Nisi ut aliquip ex ea commodo consequat',NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101833,1674780959554027522,'Duis aute irure dolor in reprehenderit',NULL,'Nisi ut aliquip ex ea commodo consequat',NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101834,1674780959554027522,'Excepteur sint occaecat cupidatat non proident',NULL,'Nisi ut aliquip ex ea commodo consequat',NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101835,1674780959554027522,'Sunt in culpa qui officia deserunt mollit anim id est laborum',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101836,1674780959554027522,'Lorem ipsum dolor sit amet, consectetur adipiscing elit',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101837,1674780959554027522,'Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101838,1674780959554027522,'Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101839,1674780959554027522,'Nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101840,1674780959554027522,'Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101841,1674780959554027522,'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101842,1674780959554027522,'Ut labore et dolore magna aliqua. Ut enim ad minim veniam',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101843,1674780959554027522,'Quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101844,1674780959554027522,'Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101845,1674780959554027522,'Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101875,1674780959554027522,'Ut enim ad minim veniam',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101876,1674780959554027522,'Duis aute irure dolor',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101877,1674780959554027522,'Excepteur sint occaecat cupidatat non proident',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101878,1674780959554027522,'Sunt in culpa qui officia deserunt mollit anim id est laborum',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1674831012545101925,1674780959554027522,'Lorem ipsum dolor sit amet, consectetur adipiscing elit',NULL,NULL,NULL,0,'[\"https://picsum.photos/200/300\"]',1),
(1682071993058504706,1674658659785715714,'Test2',NULL,'Test2','cat',0,'[\"https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/kJyD9yk12fY5D3Sx9k2ofEg0Yw12%2F8293678f-7d68-4fd1-b4a6-8e3fef8ab6a1?alt=media&token=76ddd7c9-1822-4933-a58c-fdc6e6aa6d38\",\"https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/kJyD9yk12fY5D3Sx9k2ofEg0Yw12%2F781c4a13-5fb0-460c-8cdb-1e9c51f105c1?alt=media&token=d7345c34-7229-4e43-8b20-6ee812a002b1\",\"https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/kJyD9yk12fY5D3Sx9k2ofEg0Yw12%2F189928a0-b314-4bc1-8b55-7a6e3b347158?alt=media&token=30eb8885-438a-4a83-b2e1-6c0323d12b31\"]',1),
(1682074585591361537,1674658659785715714,'测试3',NULL,'测试3','cat',0,'[\"https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/kJyD9yk12fY5D3Sx9k2ofEg0Yw12%2Feb5fd532-d300-4746-9048-5b343ece95d6?alt=media&token=3a86072e-9843-45a4-894e-0dd87066634f\",\"https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/kJyD9yk12fY5D3Sx9k2ofEg0Yw12%2Fb3e53c4f-65d2-4ef2-b634-856613e524be?alt=media&token=b8546190-ebe1-46ef-9c0a-e2afbf0660c8\"]',1),
(1683740783402758145,1674658659785715714,'11',NULL,'11','dog',0,'[\"https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/kJyD9yk12fY5D3Sx9k2ofEg0Yw12%2F9eb83fbe-e272-4758-9270-95d4494d80bc?alt=media&token=0a24275f-e47d-4f19-a3d8-df9c0fba1aa4\"]',1);

/*Table structure for table `request` */

DROP TABLE IF EXISTS `request`;

CREATE TABLE `request` (
  `request_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `request` longtext,
  PRIMARY KEY (`request_id`),
  KEY `user` (`user_id`),
  CONSTRAINT `user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1678256165547311106 DEFAULT CHARSET=utf8mb4;

/*Data for the table `request` */

insert  into `request`(`request_id`,`user_id`,`request`) values 
(1674781843671367682,1674780959554027522,'{ }'),
(1678256165547311105,1674658659785715714,'{ }');

/*Table structure for table `subcomment` */

DROP TABLE IF EXISTS `subcomment`;

CREATE TABLE `subcomment` (
  `subcomment_id` bigint(15) NOT NULL AUTO_INCREMENT,
  `subcomment_comment_id` bigint(15) NOT NULL,
  `subcomment_user_id` bigint(15) NOT NULL,
  `subcomment_time` bigint(15) DEFAULT NULL,
  `subcomment_content` varchar(255) NOT NULL,
  `subcomment_love` bigint(15) DEFAULT '0',
  `target_nickname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`subcomment_id`),
  KEY `subcomment_ibfk_1` (`subcomment_comment_id`),
  KEY `subcomment_ibfk_2` (`subcomment_user_id`),
  CONSTRAINT `subcomment_ibfk_1` FOREIGN KEY (`subcomment_comment_id`) REFERENCES `comment` (`comment_id`) ON DELETE CASCADE,
  CONSTRAINT `subcomment_ibfk_2` FOREIGN KEY (`subcomment_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1677934412417671171 DEFAULT CHARSET=utf8;

/*Data for the table `subcomment` */

insert  into `subcomment`(`subcomment_id`,`subcomment_comment_id`,`subcomment_user_id`,`subcomment_time`,`subcomment_content`,`subcomment_love`,`target_nickname`) values 
(1,999,1674658659785715714,1688505128581,'测试子评论啊啊',0,NULL),
(2,999,1674658659785715714,1688505128581,'测试子评论2',0,NULL),
(3,999,1674658659785715714,1688505128582,'测试子评论3',0,NULL),
(4,999,1674658659785715714,1688505128583,'测试子评论4',0,NULL),
(5,999,1674658659785715714,1688505128584,'测试子评论5',0,NULL),
(6,999,1674658659785715714,1688505128585,'测试子评论6',0,NULL),
(1677925698491105282,1677922810469535745,1674658659785715714,1688883635528,'111',0,NULL),
(1677925765226676225,1677922810469535745,1674658659785715714,1688883651440,'1111',0,NULL),
(1677925814631383042,1677922810469535745,1674658659785715714,1688883663220,'111111111',0,NULL),
(1677930568904323074,1677922810469535745,1674658659785715714,1688884796724,'再次测试',0,NULL),
(1677934412417671170,1677933883574657026,1674658659785715714,1688885713090,'效果不错',0,NULL);

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT,
  `username` varchar(40) NOT NULL,
  `password` varchar(40) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `uuid` varchar(80) DEFAULT NULL,
  `avatar` varchar(200) DEFAULT 'https://firebasestorage.googleapis.com/v0/b/petbook-react-springboot.appspot.com/o/.png?alt=media&token=845e414e-1435-4848-80d5-de3d7fed0ed7',
  `nickname` varchar(20) DEFAULT 'lazy to set name',
  `description` varchar(255) DEFAULT 'this guy is very lazy to write decription',
  `tag` varchar(20) DEFAULT 'Cat',
  `gender` tinyint(1) DEFAULT '1',
  `love_list` text,
  `friend_list` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `USERNAME` (`username`),
  UNIQUE KEY `username_2` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1680139671282003970 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`username`,`password`,`email`,`uuid`,`avatar`,`nickname`,`description`,`tag`,`gender`,`love_list`,`friend_list`) values 
(1674658659785715714,'kJyD9yk12fY5D3Sx9k2ofEg0Yw12',NULL,'lmy741917776@gmail.com','kJyD9yk12fY5D3Sx9k2ofEg0Yw12','https://lh3.googleusercontent.com/a/AAcHTteGdWop8z16I8LGZAb-nolwABlOk0oxIqj2tt1YRt1vbw=s96-c','Mingyi Li','this guy is very lazy to write decription','Cat',1,'[ \"1674781036062326786\", \"1674831012545101825\" ]','[ \"1674780959554027522\", \"1680139671282003969\" ]'),
(1674780959554027522,'0Mn3Y28B10YfE12nZoLv0gD2f2t1',NULL,'741917776@qq.com','0Mn3Y28B10YfE12nZoLv0gD2f2t1','https://randomuser.me/api/portraits/thumb/men/37.jpg','MINGYILI','this guy is very lazy to write decription','Cat',1,NULL,'[ \"1674658659785715714\", \"1680139671282003969\" ]'),
(1680139671282003969,'7QhCP0aohTeHy0cIiTs7tcYt39m2',NULL,'532540232@qq.com','7QhCP0aohTeHy0cIiTs7tcYt39m2','https://randomuser.me/api/portraits/thumb/men/38.jpg','HaHa','this guy is very lazy to write decription','Cat',1,NULL,'[ \"1674658659785715714\", \"1674780959554027522\" ]');

/*Table structure for table `video` */

DROP TABLE IF EXISTS `video`;

CREATE TABLE `video` (
  `video_id` bigint(15) NOT NULL AUTO_INCREMENT,
  `video_post_id` bigint(15) NOT NULL,
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
