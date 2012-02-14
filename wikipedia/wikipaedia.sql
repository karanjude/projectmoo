
CREATE DATABASE wikipaedia;

CREATE TABLE `wikipaedia_content` (
  `id` smallint(6) NOT NULL AUTO_INCREMENT,
  `local_url` varchar(400) NOT NULL,
  `field_type` tinyint(4) DEFAULT NULL,
  `field_content` text,
  PRIMARY KEY (`id`),
  KEY `local_url_index` (`local_url`(333))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
