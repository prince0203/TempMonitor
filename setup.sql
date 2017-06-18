CREATE TABLE IF NOT EXISTS `records` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` smallint(4) NOT NULL,
  `month` tinyint(2) NOT NULL,
  `day` tinyint(2) NOT NULL,
  `hour` tinyint(2) NOT NULL,
  `minute` tinyint(2) NOT NULL,
  `temp` float NOT NULL,
  `hum` float NOT NULL,
  `pres` float NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `hours` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` smallint(4) NOT NULL,
  `month` tinyint(2) NOT NULL,
  `day` tinyint(2) NOT NULL,
  `hour` tinyint(2) NOT NULL,
  `temp` float NOT NULL,
  `hum` float NOT NULL,
  `pres` float NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `days` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` smallint(4) NOT NULL,
  `month` tinyint(2) NOT NULL,
  `day` tinyint(2) NOT NULL,
  `temp` float NOT NULL,
  `hum` float NOT NULL,
  `pres` float NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `months` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` smallint(4) NOT NULL,
  `month` tinyint(2) NOT NULL,
  `temp` float NOT NULL,
  `hum` float NOT NULL,
  `pres` float NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `years` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` smallint(4) NOT NULL,
  `temp` float NOT NULL,
  `hum` float NOT NULL,
  `pres` float NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;
