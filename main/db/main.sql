-- phpMyAdmin SQL Dump
-- version 3.4.5
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2013 年 06 月 19 日 08:58
-- 服务器版本: 5.5.16
-- PHP 版本: 5.3.8

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 数据库: `scada_db`
--

-- --------------------------------------------------------

--
-- 表的结构 `realtime_lists`
--

CREATE TABLE IF NOT EXISTS `realtime_lists` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `xid` varchar(50) NOT NULL,
  `userId` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `realtimeListsUn1` (`xid`),
  KEY `realtimeListsFk1` (`userId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- 表的结构 `realtime_list_points`
--

CREATE TABLE IF NOT EXISTS `realtime_list_points` (
  `realtimeListId` int(11) NOT NULL,
  `dataPointId` int(11) NOT NULL,
  `sortOrder` int(11) NOT NULL,
  KEY `realtimeListPointsFk1` (`realtimeListId`),
  KEY `realtimeListPointsFk2` (`dataPointId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `realtime_list_users`
--

CREATE TABLE IF NOT EXISTS `realtime_list_users` (
  `realtimeListId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `accessType` int(11) NOT NULL,
  PRIMARY KEY (`realtimeListId`,`userId`),
  KEY `realtimeListUsersFk2` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `selected_realtime_list`
--

CREATE TABLE IF NOT EXISTS `selected_realtime_list` (
  `userId` int(11) NOT NULL,
  `realtimeListId` int(11) NOT NULL,
  PRIMARY KEY (`userId`),
  KEY `selectedRealtimeListFk2` (`realtimeListId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `sewage_companies`
--

CREATE TABLE IF NOT EXISTS `sewage_companies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

-- --------------------------------------------------------

--
-- 表的结构 `sewage_records`
--

CREATE TABLE IF NOT EXISTS `sewage_records` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comp_id` int(11) DEFAULT NULL,
  `record` varchar(100) DEFAULT NULL,
  `time` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
