/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50726
Source Host           : localhost:3306
Source Database       : aheadshop

Target Server Type    : MYSQL
Target Server Version : 50726
File Encoding         : 65001

Date: 2026-03-26 11:28:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `distributors`
-- ----------------------------
DROP TABLE IF EXISTS `distributors`;
CREATE TABLE `distributors` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `level_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '分销等级ID',
  `invite_code` varchar(20) NOT NULL,
  `total_commission` decimal(10,2) unsigned NOT NULL DEFAULT '0.00',
  `withdrawable_amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00',
  `frozen_amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00',
  `total_withdrawn` decimal(10,2) unsigned NOT NULL DEFAULT '0.00',
  `status` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '状态(0:禁用,1:正常)	',
  `approved` int(10) unsigned NOT NULL DEFAULT '0',
  `created_at` int(10) unsigned NOT NULL DEFAULT '0',
  `updated_at` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of distributors
-- ----------------------------

-- ----------------------------
-- Table structure for `distributor_levels`
-- ----------------------------
DROP TABLE IF EXISTS `distributor_levels`;
CREATE TABLE `distributor_levels` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(48) NOT NULL,
  `min_sales_amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00',
  `commission_rate` decimal(5,2) unsigned DEFAULT '0.00',
  `description` varchar(255) DEFAULT NULL,
  `status` tinyint(1) unsigned DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `created_at` int(10) unsigned NOT NULL DEFAULT '0',
  `updated_at` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of distributor_levels
-- ----------------------------

-- ----------------------------
-- Table structure for `orders`
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `order_no` varchar(24) NOT NULL,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `distributor_id` int(10) unsigned DEFAULT '0',
  `total_amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00',
  `pay_amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00',
  `freight_amount` decimal(10,2) unsigned DEFAULT '0.00',
  `discount_amount` decimal(10,2) unsigned DEFAULT '0.00',
  `commission_amount` decimal(10,2) unsigned DEFAULT '0.00',
  `contact_name` varchar(50) NOT NULL,
  `contact_phone` varchar(11) NOT NULL,
  `province` varchar(50) NOT NULL,
  `city` varchar(50) NOT NULL,
  `district` varchar(50) NOT NULL,
  `address` varchar(255) NOT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `status` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '订单状态(0:待支付,1:待发货,2:待收货,3:已完成,4:已取消)',
  `pay_status` tinyint(1) unsigned DEFAULT '0' COMMENT '支付状态(0:未支付,1:已支付)',
  `pay_type` varchar(12) DEFAULT NULL,
  `paid_at` int(10) unsigned DEFAULT '0',
  `shiped_at` int(10) unsigned DEFAULT '0',
  `delivery_sn` varchar(32) DEFAULT NULL,
  `delivery_company` varchar(50) DEFAULT NULL,
  `received_at` int(10) unsigned DEFAULT '0',
  `confirmed_ed` int(10) unsigned DEFAULT '0',
  `canceled_at` int(10) unsigned DEFAULT '0',
  `cancel_reason` varchar(255) DEFAULT NULL,
  `created_at` int(10) unsigned NOT NULL DEFAULT '0',
  `updated_at` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for `order_logs`
-- ----------------------------
DROP TABLE IF EXISTS `order_logs`;
CREATE TABLE `order_logs` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `order_id` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `order_status` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `action` varchar(32) NOT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `created_at` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of order_logs
-- ----------------------------

-- ----------------------------
-- Table structure for `order_products`
-- ----------------------------
DROP TABLE IF EXISTS `order_products`;
CREATE TABLE `order_products` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `order_id` int(10) unsigned NOT NULL DEFAULT '0',
  `product_id` int(10) unsigned NOT NULL DEFAULT '0',
  `product_sku_id` int(10) unsigned NOT NULL DEFAULT '0',
  `product_name` varchar(200) NOT NULL,
  `product_cover` varchar(100) DEFAULT NULL,
  `product_price` decimal(10,2) NOT NULL,
  `quantity` int(10) unsigned NOT NULL DEFAULT '0',
  `total_price` decimal(10,2) unsigned NOT NULL DEFAULT '0.00',
  `commission_amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '分销佣金比例/固定金额',
  `commission_type` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '佣金类型(1:按比例,2:按固定金额)',
  `specs` text,
  `created_at` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of order_products
-- ----------------------------

-- ----------------------------
-- Table structure for `products`
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `category_id` int(10) NOT NULL,
  `name` varchar(200) NOT NULL,
  `description` text,
  `detail` longtext,
  `cover` varchar(100) DEFAULT NULL,
  `price` decimal(10,2) unsigned NOT NULL DEFAULT '0.00',
  `market_price` decimal(10,2) unsigned DEFAULT '0.00',
  `cost_price` decimal(10,2) unsigned DEFAULT '0.00',
  `stock` int(10) unsigned DEFAULT '0',
  `sales_count` int(10) unsigned DEFAULT '0',
  `commission_amount` decimal(10,2) unsigned DEFAULT '0.00',
  `commission_type` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '佣金类型(1:按比例,2:按固定金额)',
  `status` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '状态(0:下架,1:上架)',
  `sort_order` int(10) unsigned NOT NULL DEFAULT '0',
  `created_at` int(10) unsigned NOT NULL DEFAULT '0',
  `updated_at` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of products
-- ----------------------------

-- ----------------------------
-- Table structure for `product_categories`
-- ----------------------------
DROP TABLE IF EXISTS `product_categories`;
CREATE TABLE `product_categories` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `parent_id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(48) NOT NULL,
  `sort_order` int(10) unsigned NOT NULL DEFAULT '0',
  `icon` varchar(100) DEFAULT NULL,
  `status` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '状态(0:禁用,1:启用) ',
  `created_at` int(10) unsigned NOT NULL DEFAULT '0',
  `updated_at` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of product_categories
-- ----------------------------

-- ----------------------------
-- Table structure for `product_images`
-- ----------------------------
DROP TABLE IF EXISTS `product_images`;
CREATE TABLE `product_images` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `product_id` int(10) unsigned NOT NULL DEFAULT '0',
  `image_path` varchar(100) NOT NULL,
  `sort_order` int(10) unsigned DEFAULT '0',
  `created_at` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of product_images
-- ----------------------------

-- ----------------------------
-- Table structure for `product_skus`
-- ----------------------------
DROP TABLE IF EXISTS `product_skus`;
CREATE TABLE `product_skus` (
  `id` int(10) unsigned NOT NULL,
  `product_id` int(10) unsigned NOT NULL DEFAULT '0',
  `sku_name` varchar(72) NOT NULL,
  `sku_code` varchar(32) NOT NULL,
  `specs` text,
  `price` decimal(10,2) unsigned NOT NULL DEFAULT '0.00',
  `stock` int(10) unsigned NOT NULL DEFAULT '0',
  `status` tinyint(1) unsigned DEFAULT '1',
  `created_at` int(10) unsigned NOT NULL DEFAULT '0',
  `updated_at` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of product_skus
-- ----------------------------

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(24) NOT NULL,
  `password` varchar(64) NOT NULL,
  `phone` varchar(11) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `nickname` varchar(32) DEFAULT NULL,
  `avatar` varchar(100) DEFAULT NULL,
  `gender` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '性别(0:未知,1:男,2:女)',
  `status` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '状态(0:禁用,1:正常)',
  `user_type` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '用户类型(0:管理员,1:普通,2:分销商)',
  `created_at` int(10) unsigned NOT NULL DEFAULT '0',
  `updated_at` int(10) unsigned NOT NULL DEFAULT '0',
  `last_login_at` int(10) unsigned NOT NULL DEFAULT '0',
  `last_login_ip` varchar(16) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of users
-- ----------------------------

-- ----------------------------
-- Table structure for `user_addresses`
-- ----------------------------
DROP TABLE IF EXISTS `user_addresses`;
CREATE TABLE `user_addresses` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `contact_name` varchar(32) NOT NULL,
  `contact_phone` varchar(11) NOT NULL,
  `province` varchar(50) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `district` varchar(50) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `postal_code` varchar(12) DEFAULT NULL,
  `is_default` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否默认(0:否,1:是)',
  `created_at` int(10) unsigned DEFAULT '0',
  `updated_at` int(10) unsigned DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_addresses
-- ----------------------------
