/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : tea_system

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 31/01/2026 18:33:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区',
  `detail` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认：1是 0否',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_address_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, 16, '胡图', '19144381495', '广东省', '珠海市', '斗门区', '南山街道', 1, '2026-01-08 23:44:49', '2026-01-22 12:44:49');

-- ----------------------------
-- Table structure for after_sale
-- ----------------------------
DROP TABLE IF EXISTS `after_sale`;
CREATE TABLE `after_sale`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '售后ID',
  `after_sale_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '售后单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_item_id` bigint NOT NULL COMMENT '订单项ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `type` enum('REFUND_ONLY','RETURN_REFUND','EXCHANGE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '售后类型',
  `status` enum('APPLIED','MERCHANT_APPROVED','MERCHANT_REJECTED','BUYER_SHIPPED_BACK','MERCHANT_RECEIVED','REFUNDING','COMPLETED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'APPLIED' COMMENT '售后状态',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申请原因',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细描述',
  `images` json NULL COMMENT '图片列表',
  `apply_amount` decimal(10, 2) NOT NULL COMMENT '申请金额',
  `merchant_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商家处理说明',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `completed_at` datetime NULL DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `after_sale_no`(`after_sale_no` ASC) USING BTREE,
  INDEX `idx_aftersale_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_aftersale_merchant_status`(`merchant_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_aftersale_order_item`(`order_item_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '售后表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of after_sale
-- ----------------------------
INSERT INTO `after_sale` VALUES (1, 'AS17697932516579452', 1004, 1005, 16, 3, 'REFUND_ONLY', 'APPLIED', '商品质量问题', '大红柑退款', NULL, 128.00, NULL, '2026-01-31 01:14:11', '2026-01-31 01:14:11', NULL);
INSERT INTO `after_sale` VALUES (2, 'AS17697932797723669', 1034, 1038, 16, 2, 'RETURN_REFUND', 'CANCELLED', '其他原因', '不喜欢', NULL, 128.00, NULL, '2026-01-31 01:14:39', '2026-01-31 01:14:54', NULL);
INSERT INTO `after_sale` VALUES (3, 'AS17697933621593651', 1004, 1004, 16, 3, 'REFUND_ONLY', 'APPLIED', '不想要了', '太慢了', NULL, 388.00, NULL, '2026-01-31 01:16:02', '2026-01-31 01:16:02', NULL);
INSERT INTO `after_sale` VALUES (4, 'AS17697933976565259', 10, 10, 16, 3, 'REFUND_ONLY', 'APPLIED', '个人原因', '发货太慢了', NULL, 388.00, NULL, '2026-01-31 01:16:37', '2026-01-31 01:16:37', NULL);
INSERT INTO `after_sale` VALUES (5, 'AS17697933976861700', 10, 11, 16, 3, 'REFUND_ONLY', 'APPLIED', '个人原因', '发货太慢了', NULL, 128.00, NULL, '2026-01-31 01:16:37', '2026-01-31 01:16:37', NULL);
INSERT INTO `after_sale` VALUES (6, 'AS17697941314087547', 7, 6, 16, 3, 'REFUND_ONLY', 'CANCELLED', '不想要了', '大哥为我不要了', NULL, 2340.00, NULL, '2026-01-31 01:28:51', '2026-01-31 01:29:21', NULL);
INSERT INTO `after_sale` VALUES (7, 'AS17697941314606808', 7, 7, 16, 3, 'REFUND_ONLY', 'APPLIED', '不想要了', '大哥为我不要了', NULL, 768.00, NULL, '2026-01-31 01:28:51', '2026-01-31 01:28:51', NULL);
INSERT INTO `after_sale` VALUES (8, 'AS17697961770343034', 3, 1, 16, 3, 'REFUND_ONLY', 'APPLIED', '不想要了', '1', NULL, 1280.00, NULL, '2026-01-31 02:02:57', '2026-01-31 02:02:57', NULL);

-- ----------------------------
-- Table structure for audit_log
-- ----------------------------
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `request_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求ID',
  `actor_user_id` bigint NULL DEFAULT NULL COMMENT '操作者用户ID',
  `actor_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作者角色',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作动作',
  `biz_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型',
  `biz_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务ID',
  `detail_json` json NULL COMMENT '详情JSON',
  `ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `ua` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'User-Agent',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_audit_actor`(`actor_user_id` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_audit_action`(`action` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_audit_biz`(`biz_type` ASC, `biz_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审计日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of audit_log
-- ----------------------------

-- ----------------------------
-- Table structure for balance_log
-- ----------------------------
DROP TABLE IF EXISTS `balance_log`;
CREATE TABLE `balance_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` enum('RECHARGE','REFUND','WITHDRAW','PAYMENT','ADJUSTMENT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '交易类型：RECHARGE-充值 REFUND-退款 WITHDRAW-提现 PAYMENT-支付 ADJUSTMENT-调整',
  `amount` decimal(12, 2) NOT NULL COMMENT '变动金额（正数收入，负数支出）',
  `balance_before` decimal(12, 2) NOT NULL COMMENT '变动前余额',
  `balance_after` decimal(12, 2) NOT NULL COMMENT '变动后余额',
  `status` enum('PENDING','SUCCESS','FAILED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'SUCCESS' COMMENT '状态',
  `biz_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联业务类型',
  `biz_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联业务ID',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_balance_log_user`(`user_id` ASC, `created_at` DESC) USING BTREE,
  INDEX `idx_balance_log_type`(`type` ASC, `created_at` DESC) USING BTREE,
  INDEX `idx_balance_log_biz`(`biz_type` ASC, `biz_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '余额变动明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of balance_log
-- ----------------------------
INSERT INTO `balance_log` VALUES (1, 16, 'REFUND', 500.00, 0.00, 500.00, 'SUCCESS', 'AFTER_SALE', 'AS202601200001', '售后退款', '2026-01-31 01:51:33');

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'BannerID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `link_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '链接类型：PRODUCT-商品, ARTICLE-文章, URL-外链',
  `link_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '链接值',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Banner表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, '明前龙井上新', 'https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?w=1200', 'PRODUCT', '1', 1, 0, '2026-01-07 01:14:05', '2026-01-24 22:28:41');
INSERT INTO `banner` VALUES (2, '铁观音专场', '/api/uploads/2026/01/06/696fcebee22541129e3b72eab0a8b1e1.jpg', NULL, NULL, 1, 1, '2026-01-06 23:40:16', '2026-01-07 01:14:05');
INSERT INTO `banner` VALUES (3, '普洱茶品鉴', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=1200', 'PRODUCT', '9', 3, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (1, 2, '2026-01-05 00:35:37', '2026-01-05 00:35:37');
INSERT INTO `cart` VALUES (2, 16, '2026-01-08 23:58:09', '2026-01-08 23:58:09');
INSERT INTO `cart` VALUES (3, 12, '2026-01-18 20:48:12', '2026-01-18 20:48:12');

-- ----------------------------
-- Table structure for cart_item
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `cart_id` bigint NOT NULL COMMENT '购物车ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `checked` tinyint NOT NULL DEFAULT 1 COMMENT '是否选中：1是 0否',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_cartitem_cart`(`cart_id` ASC) USING BTREE,
  INDEX `idx_cartitem_sku`(`sku_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart_item
-- ----------------------------
INSERT INTO `cart_item` VALUES (12, 3, 177, 1, 1, '2026-01-18 20:48:16', '2026-01-18 20:48:16');
INSERT INTO `cart_item` VALUES (26, 2, 177, 1, 1, '2026-01-31 18:31:15', '2026-01-31 18:31:15');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `icon` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标URL',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_parent`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, 0, '绿茶', NULL, 1, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (2, 0, '红茶', NULL, 2, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (3, 0, '乌龙茶', NULL, 3, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (4, 0, '白茶', NULL, 4, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (5, 0, '黑茶', NULL, 5, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (6, 0, '普洱茶', NULL, 6, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (7, 1, '西湖龙井', NULL, 1, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (8, 1, '碧螺春', NULL, 2, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (9, 1, '黄山毛峰', NULL, 3, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (10, 2, '正山小种', NULL, 1, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (11, 2, '金骏眉', NULL, 2, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (12, 3, '铁观音', NULL, 1, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (13, 3, '大红袍', NULL, 2, 1, '2026-01-04 17:18:09');
INSERT INTO `category` VALUES (14, 1, '绿茶小弟', '', 0, 0, '2026-01-06 00:30:08');
INSERT INTO `category` VALUES (15, 4, '白毫银针', NULL, 1, 1, '2026-01-15 00:00:00');
INSERT INTO `category` VALUES (16, 4, '白牡丹', NULL, 2, 1, '2026-01-15 00:00:00');
INSERT INTO `category` VALUES (17, 4, '贡眉', NULL, 3, 1, '2026-01-15 00:00:00');
INSERT INTO `category` VALUES (18, 4, '寿眉', NULL, 4, 1, '2026-01-15 00:00:00');
INSERT INTO `category` VALUES (19, 5, '安化黑茶', NULL, 1, 1, '2026-01-15 00:00:00');
INSERT INTO `category` VALUES (20, 5, '六堡茶', NULL, 2, 1, '2026-01-15 00:00:00');
INSERT INTO `category` VALUES (21, 5, '茯砖茶', NULL, 3, 1, '2026-01-15 00:00:00');
INSERT INTO `category` VALUES (22, 5, '青砖茶', NULL, 4, 1, '2026-01-15 00:00:00');
INSERT INTO `category` VALUES (23, 6, '普洱生茶', NULL, 1, 1, '2026-01-15 00:00:00');
INSERT INTO `category` VALUES (24, 6, '普洱熟茶', NULL, 2, 1, '2026-01-15 00:00:00');
INSERT INTO `category` VALUES (25, 6, '古树普洱', NULL, 3, 1, '2026-01-15 00:00:00');
INSERT INTO `category` VALUES (26, 6, '小青柑', NULL, 4, 1, '2026-01-15 00:00:00');

-- ----------------------------
-- Table structure for content_article
-- ----------------------------
DROP TABLE IF EXISTS `content_article`;
CREATE TABLE `content_article`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `type` enum('CULTURE','BREW_TUTORIAL','NEWS') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章类型',
  `title` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `summary` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '摘要',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面URL',
  `tags_json` json NULL COMMENT '标签JSON',
  `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `status` enum('DRAFT','PUBLISHED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览量',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_article_type_status`(`type` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '内容文章表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content_article
-- ----------------------------
INSERT INTO `content_article` VALUES (13, 'CULTURE', '中国茶文化的跨千年演变：从药用到生活艺术', '“茶之为饮，发乎神农氏。”从最初的药用，到唐代的煎茶、宋代的点茶，再到明清的泡茶，带你领略中国茶文化跨越千年的历史变迁与独特魅力。', 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?q=80&w=2574&auto=format&fit=crop', '[\"历史\", \"文化\", \"茶道\"]', '<p>中国是茶的故乡...（此处为长篇正文）...</p>', 'PUBLISHED', 129, '2026-01-10 15:43:42', '2026-01-19 01:02:15');
INSERT INTO `content_article` VALUES (14, 'CULTURE', '西湖龙井：绿茶之首的“四绝”韵味', '探寻西湖龙井的产地环境与制作工艺，细细品味其“色绿、香郁、味甘、形美”的独特品质，了解为何它能稳居中国十大名茶之首。', 'https://images.unsplash.com/photo-1627435601361-ec25f5b1d0e5?q=80&w=2670&auto=format&fit=crop', '[\"绿茶\", \"龙井\", \"名茶\"]', '<p>欲把西湖比西子...（此处为长篇正文）...</p>', 'PUBLISHED', 345, '2026-01-09 15:43:42', '2026-01-10 15:43:42');
INSERT INTO `content_article` VALUES (15, 'CULTURE', '茶道六君子：茶桌上的雅致配角', '喝茶不仅是味觉的享受，更是视觉的艺术。详细介绍茶筒、茶匙、茶漏、茶则、茶夹、茶针这“六君子”的用途与文化内涵，教你如何优雅行茶。', 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?q=80&w=2574&auto=format&fit=crop', '[\"茶具\", \"科普\", \"入门\"]', '<p>工欲善其事...（此处为长篇正文）...</p>', 'PUBLISHED', 89, '2026-01-07 15:43:42', '2026-01-10 15:43:42');
INSERT INTO `content_article` VALUES (16, 'BREW_TUTORIAL', '新手必看：如何冲泡一杯完美的红茶', '红茶性温，最适合秋冬饮用。掌握95℃的水温、精准的投茶量与“快进快出”的冲泡节奏，手把手教你泡出香气浓郁、口感醇厚的红茶。', 'https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?q=80&w=2574&auto=format&fit=crop', '[\"红茶\", \"教程\", \"技巧\"]', '<p>红茶的冲泡要点...（此处为长篇正文）...</p>', 'PUBLISHED', 569, '2026-01-08 15:43:42', '2026-01-19 01:02:23');
INSERT INTO `content_article` VALUES (17, 'BREW_TUTORIAL', '盖碗泡茶法入门详解：防烫手指南', '盖碗是适用性最广的泡茶工具，但也容易烫手。图文详解盖碗泡茶的“三才”手势与注水技巧，让你轻松驾驭这一最经典的泡茶利器。', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?q=80&w=2670&auto=format&fit=crop', '[\"盖碗\", \"进阶\", \"工具\"]', '<p>盖碗又称三才碗...（此处为长篇正文）...</p>', 'PUBLISHED', 210, '2026-01-05 15:43:42', '2026-01-10 15:43:42');
INSERT INTO `content_article` VALUES (18, 'CULTURE', '普洱茶的“越陈越香”真的科学吗？', '深度解析普洱茶后期转化的化学原理，揭秘年份与品质的真实关系。从微生物发酵到酶促反应，带你理性看待老茶热潮。', 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?q=80&w=2574&auto=format&fit=crop', '[\"普洱茶\", \"茶知识\", \"收藏\"]', '<h2>普洱茶的时光魔法</h2><p>“越陈越香”是普洱茶最核心的价值标签，但它并非玄学。</p><h3>科学原理解析</h3><p>普洱茶的转化主要依赖于茶叶中残留的酶活性以及微生物的参与...</p><h3>什么样的茶适合存？</h3><p>原料好、工艺对、存储环境佳，是好老茶诞生的三个必要条件。</p>', 'PUBLISHED', 567, '2026-01-11 01:19:23', '2026-01-22 12:45:09');
INSERT INTO `content_article` VALUES (19, 'CULTURE', '中国茶系版图：一文看懂六大茶类', '绿、红、黄、白、青、黑，六大茶类的工艺区别与风味特征全解析。新手入门必读，建立你的茶叶认知框架。', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?q=80&w=2574&auto=format&fit=crop', '[\"茶类科普\", \"入门指南\", \"六大茶类\"]', '<h2>六大茶类的划分依据</h2><p>中国茶主要依据发酵程度（多酚氧化程度）进行分类。</p><ul><li><strong>绿茶：</strong> 不发酵，讲究鲜爽。</li><li><strong>白茶：</strong> 微发酵，自然萎凋。</li><li><strong>乌龙茶：</strong> 半发酵，香气馥郁。</li><li><strong>红茶：</strong> 全发酵，甜醇温暖。</li></ul>', 'PUBLISHED', 1205, '2026-01-08 01:19:23', '2026-01-13 01:19:23');
INSERT INTO `content_article` VALUES (20, 'CULTURE', '《茶经》导读：陆羽笔下的盛唐茶事', '回到一千年前，看茶圣陆羽如何定义种茶、采茶、制茶与品茶。探寻中国茶道的精神源头，感受盛唐时期的茶人之风。', 'https://images.unsplash.com/photo-1627435601361-ec25f5b1d0e5?q=80&w=2574&auto=format&fit=crop', '[\"茶经\", \"陆羽\", \"历史\"]', '<h2>一之源：茶的起源</h2><p>陆羽在开篇即言：“茶者，南方之嘉木也。”...</p><h2>七之事：茶的故事</h2><p>本书记录了唐代以前关于茶的各种历史典故和传说。</p>', 'PUBLISHED', 339, '2026-01-05 01:19:23', '2026-01-13 01:19:23');
INSERT INTO `content_article` VALUES (21, 'BREW_TUTORIAL', '绿茶冲泡避坑指南：告别苦涩', '为什么你的绿茶又苦又涩？水温过高和投茶过量是新手最易犯的错误。掌握这三点，教你泡出如兰花般鲜爽的绿茶。', 'https://images.unsplash.com/photo-1627435601361-ec25f5b1d0e5?q=80&w=2670&auto=format&fit=crop', '[\"绿茶\", \"冲泡技巧\", \"避坑\"]', '<h2>为什么绿茶容易苦？</h2><p>绿茶含有丰富的茶多酚，高温极易析出，导致汤感苦涩。</p><h3>关键点1：水温</h3><p>建议使用80-85℃的水，切忌沸水直冲。</p><h3>关键点2：器具</h3><p>推荐使用玻璃杯，既不闷茶，又能欣赏茶舞。</p>', 'PUBLISHED', 891, '2026-01-12 01:19:23', '2026-01-22 12:44:21');
INSERT INTO `content_article` VALUES (22, 'BREW_TUTORIAL', '紫砂壶开壶与养护保姆级教程', '新买的紫砂壶怎么处理？日常如何养出温润包睫？防止异味，避免养死，从开壶到日常清洗的专家级攻略。', 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?q=80&w=2574&auto=format&fit=crop', '[\"紫砂壶\", \"茶具保养\", \"进阶\"]', '<h2>开壶的必要性</h2><p>为了去除壶内的火气和泥土味，开壶是必不可少的一步。</p><ol><li>清水煮沸清洗</li><li>豆腐降火（可选）</li><li>茶汤滋润</li></ol><h2>日常养护</h2><p>茶汤淋壶，棉布擦拭，保持干燥。</p>', 'PUBLISHED', 450, '2026-01-10 01:19:23', '2026-01-13 01:19:23');
INSERT INTO `content_article` VALUES (23, 'BREW_TUTORIAL', '夏日限定：冷泡茶制作全攻略', '不用开水也能泡好茶？低咖啡因、高鲜爽度的冷泡法，是炎炎夏日的最佳解暑饮品。适合冷泡的茶叶推荐与制作比例。', 'https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?q=80&w=2574&auto=format&fit=crop', '[\"冷泡茶\", \"夏日特饮\", \"创意喝法\"]', '<h2>什么是冷泡茶？</h2><p>用常温水或冰水浸泡茶叶，长时间低温萃取。</p><h3>优势</h3><p>咖啡因析出少，不苦不涩，清甜爽口。</p><h3>制作步骤</h3><p>1. 准备密封瓶<br>2. 茶水比1:100<br>3. 放入冰箱冷藏6-8小时</p>', 'PUBLISHED', 1500, '2025-12-29 01:19:23', '2026-01-13 01:19:23');

-- ----------------------------
-- Table structure for content_product_rel
-- ----------------------------
DROP TABLE IF EXISTS `content_product_rel`;
CREATE TABLE `content_product_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint NOT NULL COMMENT '内容ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_content_product`(`content_id` ASC, `product_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '内容商品关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content_product_rel
-- ----------------------------

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '优惠券名称',
  `type` enum('AMOUNT','PERCENT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'AMOUNT' COMMENT '类型：AMOUNT-满减券, PERCENT-折扣券',
  `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠金额（满减券）',
  `discount_percent` int NULL DEFAULT NULL COMMENT '折扣比例（折扣券，如90表示9折）',
  `min_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '使用门槛金额（满多少可用）',
  `max_discount` decimal(10, 2) NULL DEFAULT NULL COMMENT '最大抵扣金额（折扣券）',
  `total_count` int NULL DEFAULT NULL COMMENT '总发行量',
  `received_count` int NOT NULL DEFAULT 0 COMMENT '已领取数量',
  `limit_per_user` int NULL DEFAULT NULL COMMENT '每人限领数量',
  `valid_days` int NULL DEFAULT NULL COMMENT '有效天数（领取后N天有效）',
  `valid_from` datetime NULL DEFAULT NULL COMMENT '固定开始时间',
  `valid_to` datetime NULL DEFAULT NULL COMMENT '固定结束时间',
  `status` enum('ACTIVE','INACTIVE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-可用, INACTIVE-停用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coupon_status`(`status` ASC) USING BTREE,
  INDEX `idx_coupon_valid_time`(`valid_from` ASC, `valid_to` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon
-- ----------------------------
INSERT INTO `coupon` VALUES (1, '新年', 'AMOUNT', 15.00, NULL, 100.00, NULL, 100, 2, 1, 7, NULL, NULL, 'ACTIVE', '2026-01-14 19:53:33', '2026-01-18 16:00:59');
INSERT INTO `coupon` VALUES (2, '福利', 'PERCENT', NULL, 90, 20.00, 50.00, 73, 2, 1, 10, NULL, NULL, 'ACTIVE', '2026-01-14 19:54:18', '2026-01-19 00:52:02');
INSERT INTO `coupon` VALUES (3, '新年大派送', 'PERCENT', NULL, 85, 100.00, 100.00, 100, 2, 1, 7, NULL, NULL, 'ACTIVE', '2026-01-14 19:59:03', '2026-01-18 16:00:58');
INSERT INTO `coupon` VALUES (4, '大派送', 'AMOUNT', 10.00, NULL, 100.00, NULL, 100, 1, 1, 7, NULL, NULL, 'ACTIVE', '2026-01-19 00:53:20', '2026-01-19 00:53:29');

-- ----------------------------
-- Table structure for idempotent_key
-- ----------------------------
DROP TABLE IF EXISTS `idempotent_key`;
CREATE TABLE `idempotent_key`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `idempotent_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '幂等键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `biz_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型',
  `biz_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idempotent_key`(`idempotent_key` ASC) USING BTREE,
  INDEX `idx_idempotent_user_biz`(`user_id` ASC, `biz_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '幂等键表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of idempotent_key
-- ----------------------------

-- ----------------------------
-- Table structure for marketing_activity
-- ----------------------------
DROP TABLE IF EXISTS `marketing_activity`;
CREATE TABLE `marketing_activity`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动标题',
  `type` enum('DISCOUNT_RATE','REDUCE_AMOUNT','THRESHOLD_REDUCE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '折扣类型',
  `scope` enum('ALL','CATEGORY','PRODUCT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '适用范围',
  `scope_value` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '范围值',
  `threshold_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '满减门槛',
  `discount_rate` decimal(5, 2) NULL DEFAULT NULL COMMENT '折扣率',
  `reduce_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '减免金额',
  `start_at` datetime NOT NULL COMMENT '开始时间',
  `end_at` datetime NOT NULL COMMENT '结束时间',
  `status` enum('DRAFT','ENABLED','DISABLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_status_time`(`status` ASC, `start_at` ASC, `end_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '营销活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of marketing_activity
-- ----------------------------

-- ----------------------------
-- Table structure for membership_level
-- ----------------------------
DROP TABLE IF EXISTS `membership_level`;
CREATE TABLE `membership_level`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '等级ID',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '等级编码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '等级名称',
  `discount_rate` decimal(5, 2) NOT NULL DEFAULT 100.00 COMMENT '折扣率（100表示无折扣）',
  `benefits_json` json NULL COMMENT '权益JSON',
  `min_points` int NOT NULL DEFAULT 0 COMMENT '所需最低积分',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员等级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of membership_level
-- ----------------------------
INSERT INTO `membership_level` VALUES (1, 'BRONZE', '青铜会员', 100.00, '{\"description\": \"基础会员权益\"}', 0, 1, '2026-01-31 18:05:44');
INSERT INTO `membership_level` VALUES (2, 'SILVER', '白银会员', 98.00, '{\"description\": \"98折优惠，优先配送\"}', 500, 2, '2026-01-31 18:05:44');
INSERT INTO `membership_level` VALUES (3, 'GOLD', '黄金会员', 95.00, '{\"description\": \"95折优惠，专属客服\"}', 2000, 3, '2026-01-31 18:05:44');
INSERT INTO `membership_level` VALUES (4, 'PLATINUM', '铂金会员', 92.00, '{\"description\": \"92折优惠，生日礼品\"}', 5000, 4, '2026-01-31 18:05:44');
INSERT INTO `membership_level` VALUES (5, 'DIAMOND', '钻石会员', 88.00, '{\"description\": \"88折优惠，专属活动\"}', 10000, 5, '2026-01-31 18:05:44');
INSERT INTO `membership_level` VALUES (6, 'BLACK_DIAMOND', '黑钻会员', 85.00, '{\"description\": \"85折优惠，尊享VIP服务\"}', 50000, 6, '2026-01-31 18:05:44');

-- ----------------------------
-- Table structure for merchant
-- ----------------------------
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商家ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `shop_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '店铺名称',
  `shop_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺描述',
  `shop_logo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺Logo',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `business_license` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_merchant_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商家表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of merchant
-- ----------------------------
INSERT INTO `merchant` VALUES (1, 3, '福建名茶坊', '卖鱼的', NULL, '19144381495', '', 1, '2026-01-05 02:41:16', '2026-01-09 00:25:06');
INSERT INTO `merchant` VALUES (3, 4, '云南古树普洱', '云南古树普洱茶专营，原产地直发', NULL, '0871-88888888', NULL, 1, '2026-01-07 01:14:05', '2026-01-09 00:25:03');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `status` enum('CREATED','PAID','CANCELLED','SHIPPED','COMPLETED','CLOSED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'CREATED' COMMENT '订单状态',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '商品总金额',
  `pay_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `coupon_id` bigint NULL DEFAULT NULL COMMENT '优惠券ID',
  `coupon_discount_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠券抵扣金额',
  `pay_status` enum('UNPAID','PAYING','PAID','FAILED','REFUNDED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态',
  `address_snapshot` json NOT NULL COMMENT '地址快照',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `paid_at` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `shipped_at` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `completed_at` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `cancelled_at` datetime NULL DEFAULT NULL COMMENT '取消时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_order_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_order_merchant_status`(`merchant_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_order_status_created`(`status` ASC, `created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1038 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (3, 'TEA17687369615966961', 16, 3, 'PAID', 1280.00, 1280.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-18 19:49:21', '2026-01-18 19:49:22', NULL, NULL, NULL);
INSERT INTO `order` VALUES (4, 'TEA17687372766477726', 16, 3, 'PAID', 318.00, 318.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-18 19:54:36', '2026-01-18 19:54:37', NULL, NULL, NULL);
INSERT INTO `order` VALUES (5, 'TEA17687379638978781', 16, 3, 'PAID', 776.00, 776.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-18 20:06:03', '2026-01-18 20:06:04', NULL, NULL, NULL);
INSERT INTO `order` VALUES (6, 'TEA17687389087006883', 16, 3, 'PAID', 1108.00, 1008.00, 2, 100.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-18 20:21:48', '2026-01-18 20:21:49', NULL, NULL, NULL);
INSERT INTO `order` VALUES (7, 'TEA17687389394172568', 16, 3, 'PAID', 3108.00, 3093.00, 1, 15.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-18 20:22:19', '2026-01-18 20:22:19', NULL, NULL, NULL);
INSERT INTO `order` VALUES (8, 'TEA17687552555965414', 16, 3, 'PAID', 136.00, 126.00, 7, 10.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-19 00:54:15', '2026-01-19 00:54:16', NULL, NULL, NULL);
INSERT INTO `order` VALUES (9, 'TEA17687983291122270', 16, 3, 'PAID', 128.00, 128.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-19 12:52:09', '2026-01-19 12:52:09', NULL, NULL, NULL);
INSERT INTO `order` VALUES (10, 'TEA17687991794238456', 16, 3, 'PAID', 516.00, 516.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-19 13:06:19', '2026-01-19 13:06:19', NULL, NULL, NULL);
INSERT INTO `order` VALUES (11, 'TEA17372020000001', 16, 1, 'CREATED', 298.00, 298.00, NULL, 0.00, 'UNPAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图2\", \"receiverPhone\": \"19144381495\"}', '测试订单', '2026-01-19 13:09:58', NULL, NULL, NULL, NULL);
INSERT INTO `order` VALUES (13, 'TEA17372020000002', 16, 1, 'CREATED', 688.00, 688.00, NULL, 0.00, 'UNPAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图2\", \"receiverPhone\": \"19144381495\"}', '测试订单2', '2026-01-19 13:11:10', NULL, NULL, NULL, NULL);
INSERT INTO `order` VALUES (14, 'TEA17372020000003', 16, 2, 'CREATED', 388.00, 388.00, NULL, 0.00, 'UNPAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图2\", \"receiverPhone\": \"19144381495\"}', '测试订单3', '2026-01-19 13:11:10', NULL, NULL, NULL, NULL);
INSERT INTO `order` VALUES (15, 'TEA17372020000004', 16, 2, 'CREATED', 298.00, 298.00, NULL, 0.00, 'UNPAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图2\", \"receiverPhone\": \"19144381495\"}', '测试订单4', '2026-01-19 13:11:10', NULL, NULL, NULL, NULL);
INSERT INTO `order` VALUES (16, 'TEA17372020000005', 16, 3, 'CREATED', 580.00, 580.00, NULL, 0.00, 'UNPAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图2\", \"receiverPhone\": \"19144381495\"}', '测试订单5', '2026-01-19 13:11:10', NULL, NULL, NULL, NULL);
INSERT INTO `order` VALUES (17, 'TEA17372020000006', 16, 3, 'CANCELLED', 88.00, 88.00, NULL, 0.00, 'UNPAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图2\", \"receiverPhone\": \"19144381495\"}', '测试订单6', '2026-01-19 13:11:10', NULL, NULL, NULL, NULL);
INSERT INTO `order` VALUES (18, 'TEA17372020000007', 16, 2, 'CANCELLED', 168.00, 168.00, NULL, 0.00, 'UNPAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图2\", \"receiverPhone\": \"19144381495\"}', '测试订单7', '2026-01-19 13:11:10', NULL, NULL, NULL, NULL);
INSERT INTO `order` VALUES (19, 'TEA17372020000008', 16, 2, 'PAID', 368.00, 368.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图2\", \"receiverPhone\": \"19144381495\"}', '测试订单8', '2026-01-19 13:11:10', '2026-01-19 13:13:57', NULL, NULL, NULL);
INSERT INTO `order` VALUES (20, 'TEA17372020000009', 16, 3, 'CANCELLED', 128.00, 128.00, NULL, 0.00, 'UNPAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图2\", \"receiverPhone\": \"19144381495\"}', '测试订单9', '2026-01-19 13:11:10', NULL, NULL, NULL, NULL);
INSERT INTO `order` VALUES (21, 'TEA17372020000010', 16, 1, 'CANCELLED', 198.00, 198.00, NULL, 0.00, 'UNPAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图2\", \"receiverPhone\": \"19144381495\"}', '测试订单10', '2026-01-19 13:11:10', NULL, NULL, NULL, NULL);
INSERT INTO `order` VALUES (22, 'TEA17372021000001', 16, 1, 'COMPLETED', 298.00, 298.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图2\", \"receiverPhone\": \"19144381495\"}', '测试已完成订单1', '2026-01-12 13:33:17', '2026-01-12 13:33:17', '2026-01-14 13:33:17', '2026-01-17 13:33:17', NULL);
INSERT INTO `order` VALUES (23, 'TEA17372021000002', 16, 2, 'COMPLETED', 388.00, 388.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图2\", \"receiverPhone\": \"19144381495\"}', '测试已完成订单2', '2026-01-09 13:33:17', '2026-01-09 13:33:17', '2026-01-11 13:33:17', '2026-01-16 13:33:17', NULL);
INSERT INTO `order` VALUES (24, 'TEA17688379227834033', 16, 3, 'PAID', 256.00, 256.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-19 23:52:02', '2026-01-19 23:52:03', NULL, NULL, NULL);
INSERT INTO `order` VALUES (1000, 'MOCK202601071000', 3, 1, 'COMPLETED', 100.00, 100.00, NULL, 0.00, 'UNPAID', '{}', NULL, '2026-01-07 13:48:38', NULL, NULL, NULL, NULL);
INSERT INTO `order` VALUES (1004, 'TEA17697874547341577', 16, 3, 'PAID', 754.00, 754.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-30 23:37:34', '2026-01-30 23:37:35', NULL, NULL, NULL);
INSERT INTO `order` VALUES (1025, 'TEA20260131001', 16, 1, 'COMPLETED', 298.00, 298.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', '西湖龙井真不错', '2026-01-20 10:00:00', '2026-01-20 10:05:00', '2026-01-21 09:00:00', '2026-01-25 10:00:00', NULL);
INSERT INTO `order` VALUES (1026, 'TEA20260131002', 16, 2, 'COMPLETED', 168.00, 168.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', '铁观音很香', '2026-01-21 14:30:00', '2026-01-21 14:35:00', '2026-01-22 10:00:00', '2026-01-26 11:00:00', NULL);
INSERT INTO `order` VALUES (1027, 'TEA20260131003', 16, 2, 'COMPLETED', 128.00, 128.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', '清香型很好喝', '2026-01-22 09:15:00', '2026-01-22 09:20:00', '2026-01-23 08:00:00', '2026-01-27 09:00:00', NULL);
INSERT INTO `order` VALUES (1028, 'TEA20260131004', 16, 1, 'COMPLETED', 698.00, 698.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', '礼盒包装精美', '2026-01-23 16:45:00', '2026-01-23 16:50:00', '2026-01-24 09:30:00', '2026-01-28 10:30:00', NULL);
INSERT INTO `order` VALUES (1029, 'TEA20260131005', 16, 2, 'COMPLETED', 388.00, 388.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', '罐装方便保存', '2026-01-24 11:20:00', '2026-01-24 11:25:00', '2026-01-25 10:00:00', '2026-01-29 11:00:00', NULL);
INSERT INTO `order` VALUES (1030, 'TEA20260131006', 16, 2, 'COMPLETED', 328.00, 328.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', '陈茶味道醇厚', '2026-01-25 08:00:00', '2026-01-25 08:10:00', '2026-01-26 09:00:00', '2026-01-30 08:30:00', NULL);
INSERT INTO `order` VALUES (1031, 'TEA20260131007', 16, 2, 'COMPLETED', 688.00, 688.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', '送礼首选', '2026-01-26 13:30:00', '2026-01-26 13:35:00', '2026-01-27 11:00:00', '2026-01-30 14:00:00', NULL);
INSERT INTO `order` VALUES (1032, 'TEA20260131008', 16, 1, 'COMPLETED', 458.00, 458.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', '口感清香', '2026-01-27 15:00:00', '2026-01-27 15:05:00', '2026-01-28 10:30:00', '2026-01-30 16:00:00', NULL);
INSERT INTO `order` VALUES (1033, 'TEA20260131009', 16, 1, 'COMPLETED', 596.00, 596.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', '多买几份', '2026-01-28 10:30:00', '2026-01-28 10:35:00', '2026-01-29 09:00:00', '2026-01-30 18:00:00', NULL);
INSERT INTO `order` VALUES (1034, 'TEA20260131010', 16, 2, 'COMPLETED', 296.00, 296.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道111\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', '两种口味都不错', '2026-01-29 09:00:00', '2026-01-29 09:05:00', '2026-01-30 08:00:00', '2026-01-30 20:00:00', NULL);
INSERT INTO `order` VALUES (1035, 'TEA17697962044251842', 16, 3, 'PAID', 388.00, 388.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-31 02:03:24', '2026-01-31 02:03:24', NULL, NULL, NULL);
INSERT INTO `order` VALUES (1036, 'TEA17698549403415337', 16, 3, 'PAID', 776.00, 776.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-31 18:22:20', '2026-01-31 18:22:20', NULL, NULL, NULL);
INSERT INTO `order` VALUES (1037, 'TEA17698550035102725', 16, 3, 'PAID', 3016.00, 3016.00, NULL, 0.00, 'PAID', '{\"address\": \"广东省珠海市斗门区南山街道\", \"receiverName\": \"胡图\", \"receiverPhone\": \"19144381495\"}', NULL, '2026-01-31 18:23:23', '2026-01-31 18:23:24', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `title_snapshot` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品标题快照',
  `sku_name_snapshot` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'SKU名称快照',
  `image_snapshot` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片快照',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '数量',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_orderitem_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_orderitem_sku`(`sku_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1044 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 3, 56, 156, '昔归古树生茶', '357g茶饼', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 1280.00, 1, 1280.00, '2026-01-18 19:49:21');
INSERT INTO `order_item` VALUES (2, 4, 65, 178, '大红柑普洱茶', '30g装', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 318.00, 1, 318.00, '2026-01-18 19:54:36');
INSERT INTO `order_item` VALUES (3, 5, 65, 179, '大红柑普洱茶', '50g礼盒', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 388.00, 2, 776.00, '2026-01-18 20:06:03');
INSERT INTO `order_item` VALUES (4, 6, 65, 177, '大红柑普洱茶', '15g装', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 128.00, 1, 128.00, '2026-01-18 20:21:48');
INSERT INTO `order_item` VALUES (5, 6, 49, 137, '陈年六堡老茶', '500g礼盒装', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 980.00, 1, 980.00, '2026-01-18 20:21:48');
INSERT INTO `order_item` VALUES (6, 7, 64, 176, '陈皮普洱茶', '500g礼盒装', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800', 468.00, 5, 2340.00, '2026-01-18 20:22:19');
INSERT INTO `order_item` VALUES (7, 7, 65, 177, '大红柑普洱茶', '15g装', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 128.00, 6, 768.00, '2026-01-18 20:22:19');
INSERT INTO `order_item` VALUES (8, 8, 52, 144, '湖北青砖茶', '100g散茶', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800', 68.00, 2, 136.00, '2026-01-19 00:54:15');
INSERT INTO `order_item` VALUES (9, 9, 65, 177, '大红柑普洱茶', '15g装', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 128.00, 1, 128.00, '2026-01-19 12:52:09');
INSERT INTO `order_item` VALUES (10, 10, 65, 179, '大红柑普洱茶', '50g礼盒', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 388.00, 1, 388.00, '2026-01-19 13:06:19');
INSERT INTO `order_item` VALUES (11, 10, 65, 177, '大红柑普洱茶', '15g装', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 128.00, 1, 128.00, '2026-01-19 13:06:19');
INSERT INTO `order_item` VALUES (12, 11, 1, 1, '明前特级西湖龙井', '50g尝鲜装', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800', 298.00, 1, 298.00, '2026-01-19 13:09:58');
INSERT INTO `order_item` VALUES (13, 13, 3, 7, '洞庭碧螺春特级', '100g精选装', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=800', 688.00, 1, 688.00, '2026-01-19 13:11:10');
INSERT INTO `order_item` VALUES (14, 14, 5, 13, '安溪铁观音特级', '250g罐装', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 388.00, 1, 388.00, '2026-01-19 13:11:10');
INSERT INTO `order_item` VALUES (15, 15, 7, 18, '武夷岩茶大红袍', '50g品鉴装', 'https://images.unsplash.com/photo-1545665277-5937489579f2?w=800', 298.00, 1, 298.00, '2026-01-19 13:11:10');
INSERT INTO `order_item` VALUES (16, 16, 9, 25, '老班章普洱生茶', '100g散茶', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=800', 580.00, 1, 580.00, '2026-01-19 13:11:10');
INSERT INTO `order_item` VALUES (17, 17, 12, 32, '小青柑普洱茶', '10粒装', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 88.00, 1, 88.00, '2026-01-19 13:11:10');
INSERT INTO `order_item` VALUES (18, 18, 13, 35, '正山小种红茶', '100g袋装', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 168.00, 1, 168.00, '2026-01-19 13:11:10');
INSERT INTO `order_item` VALUES (19, 19, 15, 41, '福鼎白毫银针', '50g尝鲜装', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800', 368.00, 1, 368.00, '2026-01-19 13:11:10');
INSERT INTO `order_item` VALUES (20, 20, 11, 30, '熟普洱茶饼', '100g散茶', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800', 128.00, 1, 128.00, '2026-01-19 13:11:10');
INSERT INTO `order_item` VALUES (21, 21, 4, 9, '黄山毛峰特级', '100g袋装', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800', 198.00, 1, 198.00, '2026-01-19 13:11:10');
INSERT INTO `order_item` VALUES (22, 22, 1, 1, '明前特级西湖龙井', '50g尝鲜装', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800', 298.00, 1, 298.00, '2026-01-19 13:33:17');
INSERT INTO `order_item` VALUES (23, 23, 5, 13, '安溪铁观音特级', '250g罐装', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 388.00, 1, 388.00, '2026-01-19 13:33:17');
INSERT INTO `order_item` VALUES (24, 24, 65, 177, '大红柑普洱茶', '15g装', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 128.00, 2, 256.00, '2026-01-19 23:52:02');
INSERT INTO `order_item` VALUES (1000, 1000, 1, 1, 'Mock Product', 'Mock SKU', NULL, 100.00, 1, 100.00, '2026-01-22 14:30:29');
INSERT INTO `order_item` VALUES (1004, 1004, 62, 169, '邦崴古树茶', '100g散茶', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=800', 388.00, 1, 388.00, '2026-01-30 23:37:34');
INSERT INTO `order_item` VALUES (1005, 1004, 65, 177, '大红柑普洱茶', '15g装', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 128.00, 1, 128.00, '2026-01-30 23:37:34');
INSERT INTO `order_item` VALUES (1006, 1004, 63, 172, '新会小青柑普洱', '30g装', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800', 238.00, 1, 238.00, '2026-01-30 23:37:34');
INSERT INTO `order_item` VALUES (1029, 1025, 1, 1, '明前特级西湖龙井', '50g尝鲜装', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800', 298.00, 1, 298.00, '2026-01-20 10:00:00');
INSERT INTO `order_item` VALUES (1030, 1026, 5, 12, '安溪铁观音特级', '100g袋装', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 168.00, 1, 168.00, '2026-01-21 14:30:00');
INSERT INTO `order_item` VALUES (1031, 1027, 6, 15, '清香型铁观音', '100g尝鲜装', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 128.00, 1, 128.00, '2026-01-22 09:15:00');
INSERT INTO `order_item` VALUES (1032, 1028, 1, 3, '明前特级西湖龙井', '250g礼盒装', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800', 698.00, 1, 698.00, '2026-01-23 16:45:00');
INSERT INTO `order_item` VALUES (1033, 1029, 5, 13, '安溪铁观音特级', '250g罐装', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 388.00, 1, 388.00, '2026-01-24 11:20:00');
INSERT INTO `order_item` VALUES (1034, 1030, 6, 16, '清香型铁观音', '200g罐装', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 328.00, 1, 328.00, '2026-01-25 08:00:00');
INSERT INTO `order_item` VALUES (1035, 1031, 5, 14, '安溪铁观音特级', '500g礼盒装', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 688.00, 1, 688.00, '2026-01-26 13:30:00');
INSERT INTO `order_item` VALUES (1036, 1032, 1, 2, '明前特级西湖龙井', '100g罐装', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800', 458.00, 1, 458.00, '2026-01-27 15:00:00');
INSERT INTO `order_item` VALUES (1037, 1033, 1, 1, '明前特级西湖龙井', '50g尝鲜装', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800', 298.00, 2, 596.00, '2026-01-28 10:30:00');
INSERT INTO `order_item` VALUES (1038, 1034, 6, 15, '清香型铁观音', '100g尝鲜装', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 128.00, 1, 128.00, '2026-01-29 09:00:00');
INSERT INTO `order_item` VALUES (1039, 1034, 5, 12, '安溪铁观音特级', '100g袋装', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 168.00, 1, 168.00, '2026-01-29 09:00:00');
INSERT INTO `order_item` VALUES (1040, 1035, 56, 155, '昔归古树生茶', '100g散茶', '/images/products/56_昔归古树生茶.png', 388.00, 1, 388.00, '2026-01-31 02:03:24');
INSERT INTO `order_item` VALUES (1041, 1036, 65, 179, '大红柑普洱茶', '50g礼盒', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 388.00, 2, 776.00, '2026-01-31 18:22:20');
INSERT INTO `order_item` VALUES (1042, 1037, 63, 173, '新会小青柑普洱', '50g礼盒', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800', 298.00, 8, 2384.00, '2026-01-31 18:23:23');
INSERT INTO `order_item` VALUES (1043, 1037, 64, 174, '陈皮普洱茶', '100g袋装', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800', 158.00, 4, 632.00, '2026-01-31 18:23:23');

-- ----------------------------
-- Table structure for payment_record
-- ----------------------------
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `pay_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `channel` enum('MOCK','ALIPAY','WECHAT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'MOCK' COMMENT '支付渠道',
  `status` enum('CREATED','PAID','FAILED','REFUNDED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'CREATED' COMMENT '支付状态',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `notify_raw` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '回调原文',
  `notify_at` datetime NULL DEFAULT NULL COMMENT '回调时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `paid_at` datetime NULL DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `pay_no`(`pay_no` ASC) USING BTREE,
  INDEX `idx_payment_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_payment_pay_no`(`pay_no` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_record
-- ----------------------------

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `title` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品标题',
  `subtitle` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '副标题',
  `origin` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产地',
  `process` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工艺',
  `taste` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '口感描述',
  `brew_method` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '冲泡方法',
  `environment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '产地环境',
  `main_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主图URL',
  `status` enum('DRAFT','ON_SHELF','OFF_SHELF') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
  `min_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最低价',
  `max_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最高价',
  `sales_count` int NOT NULL DEFAULT 0 COMMENT '销量',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览量',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_category`(`category_id` ASC) USING BTREE,
  INDEX `idx_product_merchant`(`merchant_id` ASC) USING BTREE,
  INDEX `idx_product_status`(`status` ASC) USING BTREE,
  INDEX `idx_product_origin_process`(`origin` ASC, `process` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 66 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 1, 7, '明前特级西湖龙井', '2024年新茶·核心产区·手工炒制', '浙江杭州西湖', '手工炒青', '豆香浓郁，鲜爽回甘，滋味醇厚', '85℃水温冲泡，第一泡30秒出汤', '西湖群山环抱，气候温湿', '/images/products/1_明前特级西湖龙井.png', 'ON_SHELF', 298.00, 1280.00, 1256, 8520, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (2, 1, 7, '雨前一级西湖龙井', '2024年新茶·性价比之选', '浙江杭州西湖', '机器炒青', '清香持久，口感鲜醇', '85℃水温冲泡', '西湖产区优质茶园', '/images/products/2_雨前一级西湖龙井.png', 'ON_SHELF', 168.00, 398.00, 856, 5230, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (3, 1, 8, '洞庭碧螺春特级', '苏州原产·春茶鲜叶·卷曲如螺', '江苏苏州洞庭山', '手工揉捻', '花果香浓郁，鲜爽甘甜', '80℃水温冲泡，上投法', '太湖之滨，果茶间作', '/images/products/3_洞庭碧螺春特级.png', 'ON_SHELF', 388.00, 888.00, 632, 4120, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (4, 1, 9, '黄山毛峰特级', '高山云雾茶·白毫显露·香气清雅', '安徽黄山', '烘青', '清香高长，滋味鲜醇', '85℃水温冲泡', '黄山海拔800米以上茶园', '/images/products/4_黄山毛峰特级.png', 'ON_SHELF', 198.00, 568.00, 423, 3250, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (5, 2, 12, '安溪铁观音特级', '浓香型·传统工艺·七泡有余香', '福建安溪', '重发酵', '兰花香浓郁，回甘持久', '100℃沸水冲泡，盖碗或紫砂壶', '安溪高山茶园', '/images/products/5_安溪铁观音特级.png', 'ON_SHELF', 168.00, 688.00, 2156, 12350, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (6, 2, 12, '清香型铁观音', '轻发酵·清新爽口·办公首选', '福建安溪', '轻发酵', '清香淡雅，入口甘甜', '95℃水温冲泡', '安溪感德核心产区', '/images/products/6_清香型铁观音.png', 'ON_SHELF', 128.00, 328.00, 1523, 8920, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (7, 2, 13, '武夷岩茶大红袍', '正岩产区·岩骨花香·经典款', '福建武夷山', '重焙火', '岩韵明显，香气高远', '100℃沸水冲泡', '武夷山核心景区', '/images/products/7_武夷岩茶大红袍.png', 'ON_SHELF', 298.00, 1888.00, 856, 6230, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (8, 2, 13, '肉桂武夷岩茶', '马头岩正岩·霸气辛辣·茶客最爱', '福建武夷山马头岩', '中焙火', '桂皮香明显，刺激感强', '100℃沸水冲泡', '武夷山马头岩产区', '/images/products/8_肉桂武夷岩茶.png', 'ON_SHELF', 368.00, 1580.00, 526, 4560, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (9, 3, 6, '老班章普洱生茶', '2023年春茶·古树纯料·霸气回甘', '云南西双版纳老班章', '生茶', '茶气强劲，回甘迅猛', '100℃沸水冲泡，建议用紫砂壶', '老班章古茶园', '/images/products/9_老班章普洱生茶.png', 'ON_SHELF', 580.00, 2880.00, 326, 5620, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (10, 3, 6, '易武古树普洱生茶', '古六大茶山·柔中带刚·入门之选', '云南西双版纳易武', '生茶', '蜜香浓郁，汤感柔滑', '100℃沸水冲泡', '易武古茶山', '/images/products/10_易武古树普洱生茶.png', 'ON_SHELF', 268.00, 880.00, 652, 4230, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (11, 3, 6, '熟普洱茶饼', '2018年陈年熟茶·醇厚顺滑', '云南勐海', '熟茶', '陈香浓郁，口感醇厚', '100℃沸水冲泡', '勐海茶厂', '/images/products/11_熟普洱茶饼.png', 'ON_SHELF', 128.00, 368.00, 1256, 7850, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (12, 3, 6, '小青柑普洱茶', '新会小青柑·勐海普洱·降脂消食', '云南勐海/广东新会', '熟茶', '柑香与茶香交融，清新甜润', '100℃沸水冲泡', '精选新会核心产区青柑', '/images/products/12_小青柑普洱茶.png', 'ON_SHELF', 88.00, 298.00, 2356, 15620, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (13, 2, 10, '正山小种红茶', '武夷山桐木关·传统松烟香', '福建武夷山桐木关', '全发酵', '松烟香浓郁，桂圆味明显', '95℃水温冲泡', '桐木关自然保护区', '/images/products/13_正山小种红茶.png', 'ON_SHELF', 168.00, 588.00, 856, 5620, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (14, 2, 11, '金骏眉红茶', '顶级红茶·芽头肥壮·蜜香悠长', '福建武夷山', '全发酵', '花蜜香馥郁，滋味甘甜', '90℃水温冲泡', '武夷山核心产区', '/images/products/14_金骏眉红茶.png', 'ON_SHELF', 388.00, 1688.00, 526, 4520, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (15, 2, 4, '福鼎白毫银针', '2024年新茶·头采芽头·毫香蜜韵', '福建福鼎', '萎凋干燥', '毫香明显，甘甜爽口', '85℃水温冲泡', '福鼎太姥山产区', '/images/products/15_福鼎白毫银针.png', 'ON_SHELF', 368.00, 1280.00, 423, 3560, '2026-01-07 01:14:05', '2026-01-24 23:39:23');
INSERT INTO `product` VALUES (16, 2, 4, '福鼎老白茶饼', '2019年老茶·枣香浓郁·煮饮佳品', '福建福鼎', '萎凋干燥', '药香枣香，汤感醇厚', '100℃沸水冲泡或煮饮', '福鼎高山茶园', '/images/products/16_福鼎老白茶饼.png', 'ON_SHELF', 198.00, 580.00, 652, 4230, '2026-01-07 01:14:05', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (17, 1, 8, '太湖碧螺春一级', '江苏原产·明前采摘·性价比首选', '江苏苏州洞庭山', '机器揉捻', '清香持久，入口甘甜', '80℃水温冲泡', '太湖东山茶园', '/images/products/17_太湖碧螺春一级.png', 'ON_SHELF', 158.00, 388.00, 423, 3120, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (18, 1, 9, '太平猴魁特级', '两叶抱一芽·猴韵十足·国礼名茶', '安徽黄山太平', '手工捏尖', '兰香高爽，滋味鲜醇', '90℃水温冲泡', '太平猴坑核心产区', '/images/products/18_太平猴魁特级.png', 'ON_SHELF', 268.00, 888.00, 326, 2850, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (19, 1, 1, '信阳毛尖特级', '河南信阳·细圆紧直·白毫显露', '河南信阳', '手工炒青', '栗香高长，回甘持久', '85℃水温冲泡', '信阳五云山核心产区', '/images/products/19_信阳毛尖特级.png', 'ON_SHELF', 198.00, 568.00, 512, 4230, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (20, 1, 1, '六安瓜片一级', '安徽六安·无芽无梗·独特制法', '安徽六安', '手工炒片', '清香扑鼻，醇厚爽口', '85℃水温冲泡', '六安裕安区茶园', '/images/products/20_六安瓜片一级.png', 'ON_SHELF', 178.00, 468.00, 298, 2560, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (21, 2, 2, '滇红金芽特级', '云南凤庆·金毫满披·蜜香悠长', '云南凤庆', '全发酵', '蜜香浓郁，汤色金亮', '90℃水温冲泡', '凤庆高山茶园', '/images/products/21_滇红金芽特级.png', 'ON_SHELF', 228.00, 688.00, 756, 5230, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (22, 2, 2, '祁门红茶特级', '中国红茶皇后·祁门香·世界三大高香', '安徽祁门', '全发酵', '似花似果似蜜香', '95℃水温冲泡', '祁门历口核心产区', '/images/products/22_祁门红茶特级.png', 'ON_SHELF', 188.00, 588.00, 623, 4560, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (23, 2, 11, '桐木老枞红茶', '百年老枞·枞韵明显·稀缺珍品', '福建武夷山桐木关', '全发酵', '木质香浓郁，汤感厚重', '95℃水温冲泡', '桐木关百年老枞', '/images/products/23_桐木老枞红茶.png', 'ON_SHELF', 458.00, 1280.00, 186, 2650, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (24, 2, 3, '凤凰单丛鸭屎香', '广东潮州·高香品种·茶中香水', '广东潮州凤凰山', '半发酵', '奶香银花香交织', '100℃沸水冲泡', '凤凰乌岽山', '/images/products/24_凤凰单丛鸭屎香.png', 'ON_SHELF', 288.00, 880.00, 456, 4120, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (25, 2, 3, '凤凰单丛蜜兰香', '潮州名茶·蜜韵悠长·入门首选', '广东潮州凤凰山', '半发酵', '蜜香馥郁，回甘持久', '100℃沸水冲泡', '凤凰高山茶园', '/images/products/25_凤凰单丛蜜兰香.png', 'ON_SHELF', 168.00, 468.00, 623, 5230, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (26, 2, 13, '水仙武夷岩茶', '正岩水仙·兰花香·醇厚绵长', '福建武夷山', '中焙火', '兰香浓郁，岩韵悠长', '100℃沸水冲泡', '武夷山慧苑坑', '/images/products/26_水仙武夷岩茶.png', 'ON_SHELF', 268.00, 880.00, 356, 3560, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (27, 2, 12, '陈香型铁观音', '十年陈茶·药香浓郁·养胃佳品', '福建安溪', '重发酵陈化', '陈香药香，口感醇滑', '100℃沸水冲泡', '安溪祥华高山茶园', '/images/products/27_陈香型铁观音.png', 'ON_SHELF', 328.00, 988.00, 286, 2890, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (28, 2, 4, '白牡丹特级', '福鼎白茶·一芽两叶·花香馥郁', '福建福鼎', '萎凋干燥', '花香明显，甘甜爽口', '90℃水温冲泡', '福鼎点头核心产区', '/images/products/28_白牡丹特级.png', 'ON_SHELF', 188.00, 568.00, 523, 4120, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (29, 2, 4, '贡眉白茶', '福鼎贡眉·甘醇耐泡·高性价比', '福建福鼎', '萎凋干燥', '甜香浓郁，汤感醇厚', '95℃水温冲泡', '福鼎管阳茶区', '/images/products/29_贡眉白茶.png', 'ON_SHELF', 98.00, 298.00, 856, 6230, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (30, 2, 4, '寿眉白茶饼', '2020年老寿眉·枣香蜜韵·煮饮佳品', '福建福鼎', '萎凋干燥', '枣香浓郁，甜润绵滑', '100℃沸水冲泡或煮饮', '福鼎磻溪高山', '/images/products/30_寿眉白茶饼.png', 'ON_SHELF', 128.00, 398.00, 723, 5620, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (31, 3, 5, '安化黑茶千两茶', '湖南安化·传统工艺·世界茶王', '湖南安化', '后发酵', '菌香浓郁，醇厚回甘', '100℃沸水冲泡', '安化高马二溪', '/images/products/31_安化黑茶千两茶.png', 'ON_SHELF', 368.00, 1280.00, 186, 2350, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (32, 3, 5, '安化茯砖茶', '金花茂盛·菌香馥郁·降脂消食', '湖南安化', '后发酵', '金花菌香，顺滑甜润', '100℃沸水冲泡', '安化云台山', '/images/products/32_安化茯砖茶.png', 'ON_SHELF', 98.00, 368.00, 956, 7230, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (33, 3, 5, '广西六堡茶', '中国侨销名茶·槟榔香·越陈越佳', '广西梧州', '后发酵', '槟榔香明显，甜润顺滑', '100℃沸水冲泡', '梧州六堡镇', '/images/products/33_广西六堡茶.png', 'ON_SHELF', 138.00, 498.00, 523, 4560, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (34, 3, 6, '冰岛古树普洱生茶', '临沧冰岛·甜度极高·冰糖韵', '云南临沧冰岛', '生茶', '冰糖甜韵，茶气柔和', '100℃沸水冲泡', '冰岛老寨古茶园', '/images/products/34_冰岛古树普洱生茶.png', 'ON_SHELF', 680.00, 3280.00, 126, 3560, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (35, 3, 6, '昔归古树普洱生茶', '临沧昔归·兰花香·茶气强劲', '云南临沧昔归', '生茶', '兰香高扬，回甘持久', '100℃沸水冲泡', '昔归忙麓山古茶园', '/images/products/35_昔归古树普洱生茶.png', 'ON_SHELF', 388.00, 1280.00, 256, 3890, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (36, 3, 6, '勐库大雪山野生茶', '野生古茶·霸气十足·茶中瑰宝', '云南临沧勐库', '生茶', '野韵十足，山野气息', '100℃沸水冲泡', '大雪山野生茶林', '/images/products/36_勐库大雪山野生茶.png', 'ON_SHELF', 458.00, 1580.00, 98, 2120, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (37, 3, 6, '布朗山普洱熟茶', '勐海布朗·陈香馥郁·醇厚饱满', '云南勐海布朗山', '熟茶', '陈香浓郁，口感醇厚', '100℃沸水冲泡', '布朗山老曼峨', '/images/products/37_布朗山普洱熟茶.png', 'ON_SHELF', 168.00, 580.00, 756, 5620, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (38, 3, 6, '糯米香普洱茶', '糯香叶配普洱·清甜可口·女性最爱', '云南勐海', '熟茶', '糯米香浓郁，甜润适口', '100℃沸水冲泡', '勐海茶区', '/images/products/38_糯米香普洱茶.png', 'ON_SHELF', 68.00, 198.00, 1256, 9850, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (39, 2, 15, '福鼎白毫银针特级', '2024年头采·毫香蜜韵·茶中极品', '福建福鼎', '萎凋干燥', '毫香浓郁，鲜爽甘甜', '85℃水温冲泡，盖碗为佳', '福鼎太姥山核心产区', '/images/products/39_福鼎白毫银针特级.png', 'ON_SHELF', 368.00, 1280.00, 423, 3560, '2026-01-15 00:00:00', '2026-01-25 00:39:53');
INSERT INTO `product` VALUES (40, 2, 15, '政和白毫银针', '政和高山·高氨基酸·清甜爽口', '福建政和', '萎凋干燥', '清香甜润，回甘持久', '85℃水温冲泡', '政和高山茶园', '/images/products/40_政和白毫银针.png', 'ON_SHELF', 298.00, 880.00, 312, 2890, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (41, 2, 16, '福鼎白牡丹特级', '一芽两叶·花香馥郁·性价比之王', '福建福鼎', '萎凋干燥', '花香明显，甘甜爽口', '90℃水温冲泡', '福鼎点头核心产区', '/images/products/41_福鼎白牡丹特级.png', 'ON_SHELF', 188.00, 568.00, 623, 4560, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (42, 2, 16, '荒野白牡丹', '荒野茶树·野韵十足·稀缺珍品', '福建福鼎', '萎凋干燥', '野花香浓郁，茶气足', '90℃水温冲泡', '福鼎磻溪荒野茶园', '/images/products/42_荒野白牡丹.png', 'ON_SHELF', 328.00, 880.00, 186, 2350, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (43, 2, 17, '福鼎贡眉特级', '菜茶品种·甘醇耐泡·口粮首选', '福建福鼎', '萎凋干燥', '甜香浓郁，汤感醇厚', '95℃水温冲泡', '福鼎管阳茶区', '/images/products/43_福鼎贡眉特级.png', 'ON_SHELF', 98.00, 298.00, 856, 6230, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (44, 2, 18, '福鼎老寿眉饼', '2019年老茶·枣香蜜韵·煮饮佳品', '福建福鼎', '萎凋干燥', '药香枣香，汤感醇厚', '100℃沸水冲泡或煮饮', '福鼎高山茶园', '/images/products/44_福鼎老寿眉饼.png', 'ON_SHELF', 128.00, 398.00, 723, 5620, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (45, 2, 18, '春寿眉散茶', '2024年春茶·清甜鲜爽·入门推荐', '福建福鼎', '萎凋干燥', '清香甜润，口感清爽', '95℃水温冲泡', '福鼎太姥山', '/images/products/45_春寿眉散茶.png', 'ON_SHELF', 68.00, 198.00, 956, 7230, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (46, 3, 19, '安化千两茶', '世界茶王·传统工艺·收藏佳品', '湖南安化', '后发酵', '菌香浓郁，醇厚回甘', '100℃沸水冲泡', '安化高马二溪', '/images/products/46_安化千两茶.png', 'ON_SHELF', 368.00, 1280.00, 186, 2350, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (47, 3, 19, '安化天尖茶', '黑茶贵族·清香醇和·高端礼品', '湖南安化', '后发酵', '松烟香明显，滋味醇厚', '100℃沸水冲泡', '安化云台山', '/images/products/47_安化天尖茶.png', 'ON_SHELF', 288.00, 880.00, 256, 3120, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (48, 3, 20, '广西六堡茶特级', '中国侨销名茶·槟榔香·越陈越佳', '广西梧州', '后发酵', '槟榔香明显，甜润顺滑', '100℃沸水冲泡', '梧州六堡镇', '/images/products/48_广西六堡茶特级.png', 'ON_SHELF', 138.00, 498.00, 523, 4560, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (49, 3, 20, '陈年六堡老茶', '十年陈化·红浓陈醇·茶中瑰宝', '广西梧州', '后发酵', '陈香浓郁，口感滑润', '100℃沸水冲泡', '六堡镇老茶窖藏', '/images/products/49_陈年六堡老茶.png', 'ON_SHELF', 358.00, 980.00, 168, 2180, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (50, 3, 21, '安化茯砖茶', '金花茂盛·菌香馥郁·降脂消食', '湖南安化', '后发酵', '金花菌香，顺滑甜润', '100℃沸水冲泡', '安化云台山', '/images/products/50_安化茯砖茶.png', 'ON_SHELF', 98.00, 368.00, 956, 7230, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (51, 3, 21, '泾阳茯砖茶', '陕西泾阳·古老工艺·金花闪烁', '陕西泾阳', '后发酵', '菌香浓郁，醇和回甘', '100℃沸水冲泡', '泾阳古法制茶', '/images/products/51_泾阳茯砖茶.png', 'ON_SHELF', 118.00, 398.00, 623, 4890, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (52, 3, 22, '湖北青砖茶', '赵李桥砖茶·米砖工艺·边销名茶', '湖北赤壁', '后发酵', '滋味醇厚，回味悠长', '100℃沸水冲泡', '赤壁羊楼洞', '/images/products/52_湖北青砖茶.png', 'ON_SHELF', 68.00, 238.00, 756, 5620, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (53, 3, 23, '老班章普洱生茶', '茶王产区·霸气回甘·收藏首选', '云南西双版纳老班章', '生茶', '茶气强劲，回甘迅猛', '100℃沸水冲泡', '老班章古茶园', '/images/products/53_老班章普洱生茶.png', 'ON_SHELF', 580.00, 2880.00, 326, 5620, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (54, 3, 23, '易武古树生茶', '古六大茶山·柔中带刚·茶后风范', '云南西双版纳易武', '生茶', '蜜香浓郁，汤感柔滑', '100℃沸水冲泡', '易武古茶山', '/images/products/54_易武古树生茶.png', 'ON_SHELF', 268.00, 880.00, 452, 4230, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (55, 3, 23, '冰岛古树生茶', '冰糖甜韵·茶气柔和·名山名寨', '云南临沧冰岛', '生茶', '冰糖甜韵明显，回甘持久', '100℃沸水冲泡', '冰岛老寨古茶园', '/images/products/55_冰岛古树生茶.png', 'ON_SHELF', 680.00, 3280.00, 126, 3560, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (56, 3, 23, '昔归古树生茶', '临沧昔归·兰花香·忙麓山韵', '云南临沧昔归', '生茶', '兰香高扬，回甘迅速', '100℃沸水冲泡', '昔归忙麓山', '/images/products/56_昔归古树生茶.png', 'ON_SHELF', 388.00, 1280.00, 256, 3890, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (57, 3, 24, '勐海熟普洱茶饼', '2018年陈年·醇厚顺滑·日常口粮', '云南勐海', '熟茶', '陈香浓郁，口感醇厚', '100℃沸水冲泡', '勐海茶厂', '/images/products/57_勐海熟普洱茶饼.png', 'ON_SHELF', 128.00, 368.00, 1256, 7850, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (58, 3, 24, '布朗山熟普洱', '老曼峨原料·醇厚饱满·茶气足', '云南勐海布朗山', '熟茶', '陈香木香，口感厚重', '100℃沸水冲泡', '布朗山老曼峨', '/images/products/58_布朗山熟普洱.png', 'ON_SHELF', 168.00, 580.00, 756, 5620, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (59, 3, 24, '老茶头特级', '茶之精华·糯滑甜润·耐泡之王', '云南勐海', '熟茶', '糯香十足，甜润可口', '100℃沸水冲泡', '勐海老茶头精选', '/images/products/59_老茶头特级.png', 'ON_SHELF', 158.00, 488.00, 623, 5120, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (60, 3, 25, '勐库大雪山野生茶', '野生古茶·霸气十足·茶中瑰宝', '云南临沧勐库', '生茶', '野韵十足，山野气息', '100℃沸水冲泡', '大雪山野生茶林', '/images/products/60_勐库大雪山野生茶.png', 'ON_SHELF', 458.00, 1580.00, 98, 2120, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (61, 3, 25, '景迈古树茶', '千年万亩古茶林·兰香蜜韵', '云南普洱景迈山', '生茶', '兰香蜜韵，甜润持久', '100℃沸水冲泡', '景迈山古茶园', '/images/products/61_景迈古树茶.png', 'ON_SHELF', 328.00, 980.00, 286, 3560, '2026-01-15 00:00:00', '2026-01-25 00:39:54');
INSERT INTO `product` VALUES (62, 3, 25, '邦崴古树茶', '过渡型茶王·野韵幽香·珍稀品种', '云南普洱澜沧', '生茶', '野花香明显，回甘悠长', '100℃沸水冲泡', '邦崴古茶树群落', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=800', 'ON_SHELF', 388.00, 1180.00, 168, 2350, '2026-01-15 00:00:00', '2026-01-18 16:16:13');
INSERT INTO `product` VALUES (63, 3, 26, '新会小青柑普洱', '新会青柑·勐海普洱·降脂消食', '广东新会/云南勐海', '熟茶', '柑香与茶香交融，清新甜润', '100℃沸水冲泡', '精选新会核心产区青柑', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800', 'ON_SHELF', 88.00, 298.00, 2356, 15620, '2026-01-15 00:00:00', '2026-01-18 16:16:13');
INSERT INTO `product` VALUES (64, 3, 26, '陈皮普洱茶', '十年新会陈皮·陈香四溢·养生佳品', '广东新会/云南勐海', '熟茶', '陈皮香浓郁，甜润顺滑', '100℃沸水冲泡', '新会核心产区陈皮', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800', 'ON_SHELF', 158.00, 468.00, 856, 6890, '2026-01-15 00:00:00', '2026-01-18 16:16:13');
INSERT INTO `product` VALUES (65, 3, 26, '大红柑普洱茶', '成熟大红柑·果香浓郁·甜润饱满', '广东新会/云南勐海', '熟茶', '柑果香甜，茶味醇厚', '100℃沸水冲泡', '新会大红柑优选', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 'ON_SHELF', 128.00, 388.00, 623, 5230, '2026-01-15 00:00:00', '2026-01-18 16:16:13');

-- ----------------------------
-- Table structure for product_image
-- ----------------------------
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_image_product`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 68 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_image
-- ----------------------------
INSERT INTO `product_image` VALUES (1, 1, '/images/products/1_明前特级西湖龙井.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (2, 1, '/images/products/2_雨前一级西湖龙井.png', 2, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (3, 1, '/images/products/3_洞庭碧螺春特级.png', 3, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (4, 2, '/images/products/2_雨前一级西湖龙井.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (5, 3, '/images/products/3_洞庭碧螺春特级.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (6, 4, '/images/products/4_黄山毛峰特级.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (7, 5, '/images/products/5_安溪铁观音特级.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (8, 6, '/images/products/6_清香型铁观音.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (9, 7, '/images/products/7_武夷岩茶大红袍.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (10, 8, '/images/products/8_肉桂武夷岩茶.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (11, 9, '/images/products/9_老班章普洱生茶.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (12, 10, '/images/products/10_易武古树普洱生茶.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (13, 11, '/images/products/11_熟普洱茶饼.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (14, 12, '/images/products/12_小青柑普洱茶.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (15, 13, '/images/products/13_正山小种红茶.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (16, 14, '/images/products/14_金骏眉红茶.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (17, 15, '/images/products/15_福鼎白毫银针.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (18, 16, '/images/products/16_福鼎老白茶饼.png', 1, '2026-01-07 01:14:05');
INSERT INTO `product_image` VALUES (19, 17, '/images/products/17_太湖碧螺春一级.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (20, 18, '/images/products/18_太平猴魁特级.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (21, 19, '/images/products/19_信阳毛尖特级.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (22, 20, '/images/products/20_六安瓜片一级.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (23, 21, '/images/products/21_滇红金芽特级.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (24, 22, '/images/products/22_祁门红茶特级.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (25, 23, '/images/products/23_桐木老枞红茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (26, 24, '/images/products/24_凤凰单丛鸭屎香.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (27, 25, '/images/products/25_凤凰单丛蜜兰香.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (28, 26, '/images/products/26_水仙武夷岩茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (29, 27, '/images/products/27_陈香型铁观音.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (30, 28, '/images/products/28_白牡丹特级.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (31, 29, '/images/products/29_贡眉白茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (32, 30, '/images/products/30_寿眉白茶饼.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (33, 31, '/images/products/31_安化黑茶千两茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (34, 32, '/images/products/32_安化茯砖茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (35, 33, '/images/products/33_广西六堡茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (36, 34, '/images/products/34_冰岛古树普洱生茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (37, 35, '/images/products/35_昔归古树普洱生茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (38, 36, '/images/products/36_勐库大雪山野生茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (39, 37, '/images/products/37_布朗山普洱熟茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (40, 38, '/images/products/38_糯米香普洱茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (41, 39, '/images/products/39_福鼎白毫银针特级.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (42, 40, '/images/products/40_政和白毫银针.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (43, 41, '/images/products/41_福鼎白牡丹特级.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (44, 42, '/images/products/42_荒野白牡丹.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (45, 43, '/images/products/43_福鼎贡眉特级.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (46, 44, '/images/products/44_福鼎老寿眉饼.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (47, 45, '/images/products/45_春寿眉散茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (48, 46, '/images/products/46_安化千两茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (49, 47, '/images/products/47_安化天尖茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (50, 48, '/images/products/48_广西六堡茶特级.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (51, 49, '/images/products/49_陈年六堡老茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (52, 50, '/images/products/50_安化茯砖茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (53, 51, '/images/products/51_泾阳茯砖茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (54, 52, '/images/products/52_湖北青砖茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (55, 53, '/images/products/53_老班章普洱生茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (56, 54, '/images/products/54_易武古树生茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (57, 55, '/images/products/55_冰岛古树生茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (58, 56, '/images/products/56_昔归古树生茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (59, 57, '/images/products/57_勐海熟普洱茶饼.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (60, 58, '/images/products/58_布朗山熟普洱.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (61, 59, '/images/products/59_老茶头特级.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (62, 60, '/images/products/60_勐库大雪山野生茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (63, 61, '/images/products/61_景迈古树茶.png', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (64, 62, 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=800', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (65, 63, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (66, 64, 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=800', 1, '2026-01-15 00:00:00');
INSERT INTO `product_image` VALUES (67, 65, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800', 1, '2026-01-15 00:00:00');

-- ----------------------------
-- Table structure for product_sku
-- ----------------------------
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `sku_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'SKU名称',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sku_product`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 180 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品SKU表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_sku
-- ----------------------------
INSERT INTO `product_sku` VALUES (1, 1, '50g尝鲜装', 298.00, 100, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (2, 1, '100g精选装', 568.00, 80, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (3, 1, '250g礼盒装', 1280.00, 50, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (4, 2, '100g袋装', 168.00, 200, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (5, 2, '250g罐装', 398.00, 100, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (6, 3, '50g尝鲜装', 388.00, 80, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (7, 3, '100g精选装', 688.00, 60, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (8, 3, '200g礼盒装', 888.00, 30, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (9, 4, '100g袋装', 198.00, 151, 1, '2026-01-07 01:14:05', '2026-01-19 13:13:45');
INSERT INTO `product_sku` VALUES (10, 4, '250g罐装', 458.00, 80, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (11, 4, '500g礼盒装', 568.00, 40, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (12, 5, '100g袋装', 168.00, 300, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (13, 5, '250g罐装', 388.00, 150, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (14, 5, '500g礼盒装', 688.00, 80, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (15, 6, '125g真空装', 128.00, 200, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (16, 6, '250g罐装', 238.00, 120, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (17, 6, '500g礼盒装', 328.00, 60, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (18, 7, '50g品鉴装', 298.00, 100, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (19, 7, '100g罐装', 568.00, 80, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (20, 7, '250g礼盒装', 1280.00, 40, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (21, 7, '500g收藏装', 1888.00, 20, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (22, 8, '50g品鉴装', 368.00, 80, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (23, 8, '100g罐装', 688.00, 60, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (24, 8, '250g礼盒装', 1580.00, 30, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (25, 9, '100g散茶', 580.00, 50, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (26, 9, '200g茶饼', 1080.00, 30, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (27, 9, '357g标准饼', 2880.00, 20, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (28, 10, '100g散茶', 268.00, 80, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (29, 10, '357g茶饼', 880.00, 50, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (30, 11, '100g散茶', 128.00, 201, 1, '2026-01-07 01:14:05', '2026-01-19 13:13:51');
INSERT INTO `product_sku` VALUES (31, 11, '357g茶饼', 368.00, 100, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (32, 12, '10g装', 88.00, 301, 1, '2026-01-07 01:14:05', '2026-01-31 01:00:34');
INSERT INTO `product_sku` VALUES (33, 12, '30g装', 238.00, 150, 1, '2026-01-07 01:14:05', '2026-01-18 16:50:21');
INSERT INTO `product_sku` VALUES (34, 12, '50g礼盒', 298.00, 80, 1, '2026-01-07 01:14:05', '2026-01-18 16:50:24');
INSERT INTO `product_sku` VALUES (35, 13, '100g袋装', 168.00, 151, 1, '2026-01-07 01:14:05', '2026-01-19 13:29:25');
INSERT INTO `product_sku` VALUES (36, 13, '250g罐装', 388.00, 80, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (37, 13, '500g礼盒装', 588.00, 40, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (38, 14, '50g品鉴装', 388.00, 60, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (39, 14, '100g精选装', 688.00, 40, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (40, 14, '250g礼盒装', 1688.00, 20, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (41, 15, '50g尝鲜装', 368.00, 80, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (42, 15, '100g精选装', 688.00, 50, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (43, 15, '200g礼盒装', 1280.00, 30, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (44, 16, '100g散茶', 198.00, 100, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (45, 16, '357g茶饼', 580.00, 50, 1, '2026-01-07 01:14:05', '2026-01-07 01:14:05');
INSERT INTO `product_sku` VALUES (46, 17, '100g袋装', 158.00, 150, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (47, 17, '250g罐装', 388.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (48, 18, '50g尝鲜装', 268.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (49, 18, '100g精选装', 498.00, 40, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (50, 18, '200g礼盒装', 888.00, 20, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (51, 19, '100g袋装', 198.00, 120, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (52, 19, '250g罐装', 468.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (53, 19, '500g礼盒装', 568.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (54, 20, '100g袋装', 178.00, 100, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (55, 20, '250g罐装', 468.00, 50, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (56, 21, '100g袋装', 228.00, 150, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (57, 21, '250g罐装', 528.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (58, 21, '500g礼盒装', 688.00, 40, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (59, 22, '100g袋装', 188.00, 120, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (60, 22, '250g罐装', 438.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (61, 22, '500g礼盒装', 588.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (62, 23, '50g品鉴装', 458.00, 40, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (63, 23, '100g精选装', 880.00, 25, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (64, 23, '200g礼盒装', 1280.00, 15, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (65, 24, '50g品鉴装', 288.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (66, 24, '100g精选装', 528.00, 50, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (67, 24, '200g礼盒装', 880.00, 25, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (68, 25, '100g袋装', 168.00, 120, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (69, 25, '250g罐装', 388.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (70, 25, '500g礼盒装', 468.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (71, 26, '50g品鉴装', 268.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (72, 26, '100g罐装', 498.00, 40, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (73, 26, '250g礼盒装', 880.00, 20, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (74, 27, '100g袋装', 328.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (75, 27, '250g罐装', 768.00, 40, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (76, 27, '500g礼盒装', 988.00, 20, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (77, 28, '50g尝鲜装', 188.00, 100, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (78, 28, '100g精选装', 348.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (79, 28, '200g礼盒装', 568.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (80, 29, '100g袋装', 98.00, 200, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (81, 29, '250g罐装', 228.00, 100, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (82, 29, '500g礼盒装', 298.00, 50, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (83, 30, '100g散茶', 128.00, 150, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (84, 30, '357g茶饼', 398.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (85, 31, '100g散茶', 368.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (86, 31, '500g茶砖', 888.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (87, 31, '1000g原装', 1280.00, 15, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (88, 32, '100g散茶', 98.00, 200, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (89, 32, '350g茶砖', 298.00, 100, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (90, 32, '1000g大砖', 368.00, 50, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (91, 33, '100g散茶', 138.00, 120, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (92, 33, '250g罐装', 318.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (93, 33, '500g礼盒装', 498.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (94, 34, '100g散茶', 680.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (95, 34, '200g茶饼', 1280.00, 20, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (96, 34, '357g标准饼', 3280.00, 10, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (97, 35, '100g散茶', 388.00, 50, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (98, 35, '357g茶饼', 1280.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (99, 36, '100g散茶', 458.00, 40, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (100, 36, '200g茶饼', 880.00, 25, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (101, 36, '357g标准饼', 1580.00, 15, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (102, 37, '100g散茶', 168.00, 150, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (103, 37, '357g茶饼', 580.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (104, 38, '100g袋装', 68.00, 300, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (105, 38, '250g罐装', 158.00, 150, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (106, 39, '50g尝鲜装', 368.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (107, 39, '100g精选装', 688.00, 50, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (108, 39, '200g礼盒装', 1280.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (109, 40, '50g尝鲜装', 298.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (110, 40, '100g精选装', 568.00, 40, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (111, 40, '200g礼盒装', 880.00, 20, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (112, 41, '50g尝鲜装', 188.00, 100, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (113, 41, '100g精选装', 348.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (114, 41, '200g礼盒装', 568.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (115, 42, '50g尝鲜装', 328.00, 50, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (116, 42, '100g精选装', 598.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (117, 42, '200g礼盒装', 880.00, 15, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (118, 43, '100g袋装', 98.00, 200, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (119, 43, '250g罐装', 228.00, 100, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (120, 43, '500g礼盒装', 298.00, 50, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (121, 44, '100g散茶', 128.00, 150, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (122, 44, '357g茶饼', 398.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (123, 45, '100g袋装', 68.00, 300, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (124, 45, '250g罐装', 158.00, 150, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (125, 45, '500g礼盒装', 198.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (126, 46, '100g散茶', 368.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (127, 46, '500g茶砖', 888.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (128, 46, '1000g原装', 1280.00, 15, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (129, 47, '100g散茶', 288.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (130, 47, '250g罐装', 668.00, 40, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (131, 47, '500g礼盒装', 880.00, 20, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (132, 48, '100g散茶', 138.00, 120, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (133, 48, '250g罐装', 318.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (134, 48, '500g礼盒装', 498.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (135, 49, '100g散茶', 358.00, 40, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (136, 49, '250g罐装', 788.00, 20, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (137, 49, '500g礼盒装', 980.00, 9, 1, '2026-01-15 00:00:00', '2026-01-18 20:21:48');
INSERT INTO `product_sku` VALUES (138, 50, '100g散茶', 98.00, 200, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (139, 50, '350g茶砖', 298.00, 100, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (140, 50, '1000g大砖', 368.00, 50, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (141, 51, '100g散茶', 118.00, 150, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (142, 51, '350g茶砖', 328.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (143, 51, '1000g大砖', 398.00, 40, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (144, 52, '100g散茶', 68.00, 198, 1, '2026-01-15 00:00:00', '2026-01-19 00:54:15');
INSERT INTO `product_sku` VALUES (145, 52, '250g茶砖', 158.00, 100, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (146, 52, '500g茶砖', 238.00, 50, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (147, 53, '100g散茶', 580.00, 50, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (148, 53, '200g茶饼', 1080.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (149, 53, '357g标准饼', 2880.00, 20, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (150, 54, '100g散茶', 268.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (151, 54, '357g茶饼', 880.00, 50, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (152, 55, '100g散茶', 680.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (153, 55, '200g茶饼', 1280.00, 20, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (154, 55, '357g标准饼', 3280.00, 10, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (155, 56, '100g散茶', 388.00, 49, 1, '2026-01-15 00:00:00', '2026-01-31 02:03:24');
INSERT INTO `product_sku` VALUES (156, 56, '357g茶饼', 1280.00, 29, 1, '2026-01-15 00:00:00', '2026-01-18 19:49:21');
INSERT INTO `product_sku` VALUES (157, 57, '100g散茶', 128.00, 200, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (158, 57, '357g茶饼', 368.00, 100, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (159, 58, '100g散茶', 168.00, 150, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (160, 58, '357g茶饼', 580.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (161, 59, '100g袋装', 158.00, 150, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (162, 59, '250g罐装', 368.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (163, 59, '500g礼盒装', 488.00, 40, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (164, 60, '100g散茶', 458.00, 40, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (165, 60, '200g茶饼', 880.00, 25, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (166, 60, '357g标准饼', 1580.00, 15, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (167, 61, '100g散茶', 328.00, 60, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (168, 61, '357g茶饼', 980.00, 30, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (169, 62, '100g散茶', 388.00, 49, 1, '2026-01-15 00:00:00', '2026-01-30 23:37:34');
INSERT INTO `product_sku` VALUES (170, 62, '357g茶饼', 1180.00, 25, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (171, 63, '10g装', 88.00, 300, 1, '2026-01-15 00:00:00', '2026-01-18 16:49:29');
INSERT INTO `product_sku` VALUES (172, 63, '30g装', 238.00, 149, 1, '2026-01-15 00:00:00', '2026-01-30 23:37:34');
INSERT INTO `product_sku` VALUES (173, 63, '50g礼盒', 298.00, 72, 1, '2026-01-15 00:00:00', '2026-01-31 18:23:23');
INSERT INTO `product_sku` VALUES (174, 64, '100g袋装', 158.00, 146, 1, '2026-01-15 00:00:00', '2026-01-31 18:23:23');
INSERT INTO `product_sku` VALUES (175, 64, '250g罐装', 368.00, 80, 1, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
INSERT INTO `product_sku` VALUES (176, 64, '500g礼盒装', 468.00, 35, 1, '2026-01-15 00:00:00', '2026-01-18 20:22:19');
INSERT INTO `product_sku` VALUES (177, 65, '15g装', 128.00, 188, 1, '2026-01-15 00:00:00', '2026-01-30 23:37:34');
INSERT INTO `product_sku` VALUES (178, 65, '30g装', 318.00, 99, 1, '2026-01-15 00:00:00', '2026-01-18 19:54:36');
INSERT INTO `product_sku` VALUES (179, 65, '50g礼盒', 388.00, 45, 1, '2026-01-15 00:00:00', '2026-01-31 18:22:20');

-- ----------------------------
-- Table structure for product_trace
-- ----------------------------
DROP TABLE IF EXISTS `product_trace`;
CREATE TABLE `product_trace`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '溯源ID',
  `trace_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '溯源码',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次号',
  `origin` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产区详细',
  `pick_date` date NULL DEFAULT NULL COMMENT '采摘日期',
  `process` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工艺',
  `producer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生产商',
  `inspection_report_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检测报告URL',
  `certificate_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '证书URL',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '溯源摘要',
  `status` enum('DRAFT','PENDING','APPROVED','REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DRAFT' COMMENT '审核状态',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `trace_code`(`trace_code` ASC) USING BTREE,
  INDEX `idx_trace_product`(`product_id` ASC) USING BTREE,
  INDEX `idx_trace_merchant`(`merchant_id` ASC) USING BTREE,
  INDEX `idx_trace_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 66 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品溯源表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_trace
-- ----------------------------
INSERT INTO `product_trace` VALUES (1, 'TEA20240301001', 1, 1, 'LJ2024030101', '浙江省杭州市西湖区龙井村核心产区，海拔350米，狮峰山西麓茶园', '2024-03-15', '传统手工炒青工艺，杀青、揉捻、干燥三道工序，全程无添加', '杭州西湖龙井茶业有限公司', 'https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?w=600', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', '本批次明前龙井采自西湖龙井核心产区狮峰山，由具有30年制茶经验的老师傅全程手工炒制。鲜叶于清明前三天采摘，一芽一叶初展，经传统工艺精心制作。茶叶色泽嫩绿，扁平光滑，豆香馥郁，口感鲜爽回甘。已通过国家茶叶质量检测中心检测，各项指标均符合有机食品标准。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (2, 'TEA20240315002', 3, 1, 'BLC2024031501', '江苏省苏州市吴中区洞庭东山，太湖畔果茶间作茶园', '2024-03-18', '传统手工揉捻工艺，采用独特的搓团显毫技法', '苏州洞庭碧螺春茶厂', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', '本批次碧螺春采自洞庭东山核心产区，茶园与枇杷、杨梅等果树间作，茶叶天然吸收果香。采摘时间为春分后谷雨前，选取一芽一叶初展的鲜叶，经过杀青、揉捻、搓团、焙干等多道工序，制成卷曲如螺的干茶。成品茶条索纤细，卷曲成螺，银毫遍布，香气馥郁。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (3, 'TEA20240401003', 5, 2, 'TGY2024040101', '福建省泉州市安溪县感德镇高山茶园，海拔800米以上', '2024-04-05', '传统铁观音制作工艺：晒青、摇青、杀青、包揉、烘焙', '安溪感德铁观音专业合作社', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', '本批次浓香型铁观音产自安溪感德核心产区，海拔800米以上的高山茶园。采用传统重发酵工艺，经过晒青、摇青、杀青、包揉、烘焙等十余道工序精制而成。干茶紧结重实，色泽砂绿油润，冲泡后兰花香浓郁持久，七泡有余香，汤色金黄明亮，滋味醇厚甘鲜。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (4, 'TEA20240320004', 7, 2, 'DHB2024032001', '福建省南平市武夷山市风景区内核心正岩产区', '2024-03-20', '武夷岩茶传统制作技艺：萎凋、做青、杀青、揉捻、烘焙', '武夷山大红袍茶业股份有限公司', 'https://images.unsplash.com/photo-1545665277-5937489579f2?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次大红袍原料采自武夷山风景区核心正岩产区，岩骨花香是其独特标识。制茶工艺承袭清代贡茶技法，经萎凋、做青、杀青、揉捻、多次烘焙等繁复工序，历时一个半月方成。成品茶条索紧结匀整，色泽绿褐鲜润，岩韵明显，香气高远持久，冲泡十余次仍有余香。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (5, 'TEA20240410005', 9, 3, 'LBZ2024041001', '云南省西双版纳州勐海县布朗山老班章村，古树茶园', '2024-04-10', '普洱生茶传统制作：萎凋、杀青、揉捻、日晒干燥、蒸压成饼', '老班章古树茶专业合作社', 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=600', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', '本批次老班章普洱生茶选用老班章古茶园300年以上古茶树春茶原料，人工采摘一芽二叶。采用传统普洱茶制作工艺，日晒干燥保留茶叶活性。茶饼饼形圆润端正，条索粗壮肥硕，银毫显著。冲泡后茶汤金黄透亮，茶气强劲霸道，苦底化甘迅速，生津回甘持久，是普洱茶收藏与品饮的上佳之选。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (6, 'TEA20240325006', 13, 2, 'ZSXZ2024032501', '福建省南平市武夷山市桐木关国家级自然保护区', '2024-03-25', '正山小种传统制作：萎凋、揉捻、发酵、松烟熏焙', '武夷山桐木关正山茶业有限公司', 'https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?w=600', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', '本批次正山小种产自武夷山桐木关核心产区，是世界红茶鼻祖。选用当地菜茶品种，采用传统松烟熏焙工艺，以马尾松为燃料，赋予茶叶独特的松烟香和桂圆味。干茶条索紧结匀整，色泽乌润，冲泡后汤色红艳明亮，香气高长带松烟香，滋味醇厚甘甜，叶底红亮软嫩。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (7, 'TEA20240308007', 15, 2, 'BHYZ2024030801', '福建省宁德市福鼎市太姥山核心产区，海拔600米以上', '2024-03-08', '白茶传统制作工艺：萎凋、干燥，不炒不揉', '福鼎白茶集团有限公司', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次白毫银针为2024年头采，原料选自福鼎太姥山核心产区海拔600米以上茶园的大白茶品种。采用传统白茶工艺，仅进行萎凋和干燥，最大程度保留茶叶天然成分。成品茶芽头肥壮，白毫密披，如银针直立。冲泡后汤色杏黄明亮，毫香显著，滋味鲜爽甘甜，回味悠长。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (8, 'TEA20231015008', 11, 3, 'MHS2023101501', '云南省西双版纳州勐海县勐海镇', '2023-10-15', '普洱熟茶制作：渥堆发酵、翻堆、出堆、筛分、蒸压成饼', '勐海茶厂', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', '本批次熟普洱茶饼采用勐海地区优质晒青毛茶为原料，经过45天传统渥堆发酵工艺精制而成。茶饼圆润紧实，条索清晰，陈香馥郁无堆味。冲泡后汤色红浓透亮，口感醇厚顺滑，甜度高，无苦涩感，适合日常品饮和煮饮。经过两年陈化，滋味更加醇和。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (9, 'TEA20240415009', 12, 3, 'XQG2024041501', '广东省江门市新会区核心产区及云南省西双版纳州勐海县', '2024-04-15', '小青柑制作：鲜柑掏空、装入普洱熟茶、低温烘干', '新会柑普茶业有限公司', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', 'https://images.unsplash.com/photo-1563911302283-d2bc129e7570?w=600', '本批次小青柑选用新会核心产区七八月份的青柑，搭配勐海优质宫廷普洱熟茶。青柑小巧饱满，柑皮油亮，内装普洱茶条索匀整。冲泡后柑香与陈香完美融合，清新甜润，口感丝滑，具有理气健脾、降脂消食的功效，是办公室茶饮的绝佳之选。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (10, 'TEA20240405010', 21, 2, 'DHJY2024040501', '云南省临沧市凤庆县高山茶园，海拔1800米以上', '2024-04-05', '滇红金芽传统制作：萎凋、揉捻、发酵、干燥', '凤庆滇红茶业集团有限公司', 'https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?w=600', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', '本批次滇红金芽采自凤庆县海拔1800米以上的高山茶园，选用早春一芽一叶的鲜叶，经传统滇红工艺精制。成品茶芽叶肥壮，金毫满披，色泽金黄油润。冲泡后汤色红艳透亮，蜜香浓郁持久，滋味甜醇饱满，回味悠长，是高端红茶的代表。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (11, 'TEA20240405011', 2, 1, 'LJ2024040501', '浙江省杭州市西湖区龙井村产区，海拔300米', '2024-04-08', '机器炒青工艺，标准化生产流程', '杭州西湖龙井茶业有限公司', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次雨前龙井采自西湖产区优质茶园，谷雨前采摘，一芽二叶。采用现代化机器炒青工艺，确保品质稳定。茶叶扁平光滑，色泽翠绿，清香持久，口感鲜醇，性价比高，适合日常品饮。已通过国家茶叶质量检测，各项指标符合食品安全标准。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (12, 'TEA20240318012', 4, 1, 'HSMF2024031801', '安徽省黄山市黄山区，海拔800米以上高山茶园', '2024-03-18', '传统烘青工艺，分级筛选', '黄山毛峰茶业集团有限公司', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', '本批次黄山毛峰产自黄山海拔800米以上的高山茶园，云雾缭绕，生态环境优越。采用传统烘青工艺，茶叶白毫显露，色泽嫩绿微黄。冲泡后清香高长，滋味鲜醇回甘，汤色清澈明亮，叶底嫩黄成朵。是中国十大名茶之一，品质优异。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (13, 'TEA20240412013', 6, 2, 'TGY2024041201', '福建省泉州市安溪县感德镇核心产区', '2024-04-12', '轻发酵工艺：晒青、摇青、杀青、包揉、烘干', '安溪感德铁观音专业合作社', 'https://images.unsplash.com/photo-1563911302283-d2bc129e7570?w=600', 'https://images.unsplash.com/photo-1545665277-5937489579f2?w=600', '本批次清香型铁观音采用轻发酵工艺，保留茶叶的清新香气。干茶色泽翠绿，条索紧结。冲泡后清香淡雅，入口甘甜，汤色清澈明亮。适合办公室日常品饮，提神醒脑，清新爽口。产自安溪感德核心产区，品质稳定可靠。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (14, 'TEA20240328014', 8, 2, 'RG2024032801', '福建省南平市武夷山市马头岩正岩产区', '2024-03-28', '武夷岩茶传统制作技艺：萎凋、做青、杀青、揉捻、中焙火', '武夷山岩茶研究所', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=600', '本批次肉桂产自武夷山马头岩正岩产区，岩韵明显。采用传统武夷岩茶制作技艺，中焙火处理。干茶条索紧结，色泽乌褐油润。冲泡后桂皮香明显，刺激感强，岩韵突出，汤色橙黄明亮，滋味醇厚霸气，是茶客最爱的岩茶品种之一。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (15, 'TEA20240418015', 10, 3, 'YW2024041801', '云南省西双版纳州勐腊县易武古茶山', '2024-04-18', '普洱生茶传统制作：萎凋、杀青、揉捻、日晒干燥、蒸压成饼', '易武古茶山茶业有限公司', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', '本批次易武古树生茶选用易武古茶山200年以上古茶树春茶原料，人工采摘一芽二叶。易武茶以柔著称，蜜香浓郁，汤感柔滑细腻。茶饼条索肥壮，白毫显著。冲泡后汤色金黄透亮，香气高扬持久，滋味甘甜醇和，是普洱茶入门的绝佳选择。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (16, 'TEA20240402016', 14, 2, 'JJM2024040201', '福建省南平市武夷山市核心产区', '2024-04-02', '金骏眉传统制作：萎凋、揉捻、发酵、干燥', '武夷山正山茶业有限公司', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', 'https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?w=600', '本批次金骏眉选用武夷山核心产区高山茶树的芽头为原料，全程手工制作。干茶芽头肥壮，金毫满披，色泽金黄黑相间。冲泡后花蜜香馥郁持久，汤色金黄明亮，滋味甘甜醇厚，回味悠长。是顶级红茶的代表，品质卓越。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (17, 'TEA20190310017', 16, 2, 'LBCB2019031001', '福建省宁德市福鼎市高山茶园', '2019-03-10', '白茶传统制作工艺：萎凋、干燥，自然陈化', '福鼎白茶集团有限公司', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', '本批次老白茶饼采用2019年福鼎高山白茶为原料，经过5年自然陈化。茶饼紧压成型，色泽深褐，药香枣香明显。冲泡后汤色深红透亮，口感醇厚顺滑，甜度高，适合煮饮。老白茶具有清热降火、养胃护肝的功效，是养生佳品。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (18, 'TEA20240322018', 17, 1, 'BLC2024032201', '江苏省苏州市吴中区洞庭东山茶园', '2024-03-22', '机器揉捻工艺，标准化生产', '苏州洞庭碧螺春茶厂', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次太湖碧螺春一级采自洞庭东山茶园，明前采摘。采用机器揉捻工艺，提高生产效率，保证品质稳定。干茶卷曲如螺，色泽翠绿。冲泡后清香持久，入口甘甜，汤色嫩绿明亮。性价比高，是日常品饮的优质选择。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (19, 'TEA20240325019', 18, 1, 'TPHK2024032501', '安徽省黄山市黄山区太平猴坑核心产区', '2024-03-25', '太平猴魁传统制作：手工捏尖、烘焙', '太平猴魁茶业有限公司', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', '本批次太平猴魁产自太平猴坑核心产区，采用传统手工捏尖工艺。干茶两叶抱一芽，扁平挺直，色泽苍绿。冲泡后兰香高爽，滋味鲜醇回甘，汤色清绿明亮，叶底嫩绿匀整。太平猴魁是中国十大名茶之一，曾作为国礼赠送外宾，品质卓越。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (20, 'TEA20240410020', 19, 1, 'XYMJ2024041001', '河南省信阳市浉河区五云山核心产区', '2024-04-10', '传统手工炒青工艺，精细制作', '信阳毛尖茶业集团有限公司', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', '本批次信阳毛尖产自信阳五云山核心产区，采用传统手工炒青工艺。干茶细圆紧直，白毫显露，色泽翠绿。冲泡后栗香高长，滋味鲜爽回甘，汤色嫩绿明亮。信阳毛尖是中国十大名茶之一，以\"细、圆、光、直、多白毫、香高、味浓、汤色绿\"的独特风格著称。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (21, 'TEA20240415021', 20, 1, 'LAGP2024041501', '安徽省六安市裕安区茶园', '2024-04-15', '六安瓜片传统制作：手工炒片、拉老火', '六安瓜片茶业有限公司', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', '本批次六安瓜片采用传统手工炒片工艺，无芽无梗，单片成茶。干茶形似瓜子，色泽宝绿。冲泡后清香扑鼻，滋味醇厚爽口，汤色黄绿明亮。六安瓜片是中国十大名茶之一，以其独特的单片制法和醇厚口感而闻名。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (22, 'TEA20240408022', 22, 2, 'QMHC2024040801', '安徽省黄山市祁门县历口核心产区', '2024-04-08', '祁门红茶传统制作：萎凋、揉捻、发酵、干燥', '祁门红茶集团有限公司', 'https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?w=600', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', '本批次祁门红茶产自祁门历口核心产区，采用传统制作工艺。干茶条索紧细，色泽乌润。冲泡后似花似果似蜜香，香气馥郁持久，汤色红艳明亮，滋味醇厚甘鲜。祁门红茶是世界三大高香红茶之一，被誉为\"红茶皇后\"，品质卓越。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (23, 'TEA20240330023', 23, 2, 'LCHC2024033001', '福建省南平市武夷山市桐木关百年老枞茶园', '2024-03-30', '传统红茶制作：萎凋、揉捻、发酵、干燥', '武夷山桐木关正山茶业有限公司', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', '本批次老枞红茶选用桐木关百年以上老枞茶树为原料，树龄越老，茶韵越深。干茶条索紧结，色泽乌黑油润。冲泡后木质香浓郁，枞韵明显，汤色深红透亮，滋味醇厚厚重，回甘持久。老枞红茶是稀缺珍品，产量有限，品质卓越。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (24, 'TEA20240420024', 24, 2, 'FCDC2024042001', '广东省潮州市凤凰山乌岽山产区', '2024-04-20', '凤凰单丛传统制作：晒青、做青、杀青、揉捻、烘焙', '凤凰单丛茶业有限公司', 'https://images.unsplash.com/photo-1563911302283-d2bc129e7570?w=600', 'https://images.unsplash.com/photo-1545665277-5937489579f2?w=600', '本批次鸭屎香单丛产自凤凰山乌岽山产区，采用传统单丛制作工艺。干茶条索紧结，色泽乌褐油润。冲泡后奶香银花香交织，香气高扬持久，汤色金黄明亮，滋味醇厚甘爽。鸭屎香是凤凰单丛的高香品种，被誉为\"茶中香水\"，深受茶客喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (25, 'TEA20240422025', 25, 2, 'MLXDC2024042201', '广东省潮州市凤凰山高山茶园', '2024-04-22', '凤凰单丛传统制作：晒青、做青、杀青、揉捻、烘焙', '凤凰单丛茶业有限公司', 'https://images.unsplash.com/photo-1545665277-5937489579f2?w=600', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', '本批次蜜兰香单丛产自凤凰山高山茶园，是凤凰单丛的经典品种。干茶条索紧结，色泽乌褐。冲泡后蜜香馥郁，兰花香明显，汤色金黄透亮，滋味醇厚回甘持久。蜜兰香是凤凰单丛入门的首选品种，香气优雅，口感平衡，深受茶友喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (26, 'TEA20240405026', 26, 2, 'SX2024040501', '福建省南平市武夷山市慧苑坑正岩产区', '2024-04-05', '武夷岩茶传统制作技艺：萎凋、做青、杀青、揉捻、中焙火', '武夷山岩茶研究所', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=600', '本批次水仙岩茶产自武夷山慧苑坑正岩产区，采用传统岩茶制作技艺。干茶条索肥壮，色泽乌褐油润。冲泡后兰花香浓郁，岩韵悠长，汤色橙黄明亮，滋味醇厚绵长。水仙是武夷岩茶的传统品种，以其独特的兰花香和醇厚口感而著称。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (27, 'TEA20140425027', 27, 2, 'CXTGY2014042501', '福建省泉州市安溪县祥华高山茶园', '2014-04-25', '重发酵工艺后长期陈化，定期复焙', '安溪祥华铁观音专业合作社', 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=600', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', '本批次陈香型铁观音采用2014年重发酵铁观音为原料，经过10年自然陈化和定期复焙。干茶色泽乌黑油润，陈香明显。冲泡后陈香药香浓郁，口感醇滑顺口，汤色深红透亮。陈年铁观音具有养胃护胃的功效，是养生佳品，深受老茶客喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (28, 'TEA20240312028', 28, 2, 'BMD2024031201', '福建省宁德市福鼎市点头核心产区', '2024-03-12', '白茶传统制作工艺：萎凋、干燥', '福鼎白茶集团有限公司', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次白牡丹特级采自福鼎点头核心产区，一芽两叶，采用传统白茶工艺。干茶芽叶连枝，色泽灰绿，白毫显露。冲泡后花香明显，滋味甘甜爽口，汤色杏黄明亮。白牡丹是福鼎白茶的经典品种，香气优雅，口感平衡，性价比高，深受茶友喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (29, 'TEA20240318029', 29, 2, 'GM2024031801', '福建省宁德市福鼎市管阳茶区', '2024-03-18', '白茶传统制作工艺：萎凋、干燥', '福鼎白茶集团有限公司', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', '本批次贡眉白茶采自福鼎管阳茶区，选用菜茶品种。干茶叶片稍大，色泽灰绿。冲泡后甜香浓郁，汤感醇厚，汤色橙黄明亮。贡眉白茶甘醇耐泡，性价比高，是日常口粮茶的优质选择。长期饮用具有清热降火、养生保健的功效。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (30, 'TEA20200320030', 30, 2, 'SMBCB2020032001', '福建省宁德市福鼎市磻溪高山茶园', '2020-03-20', '白茶传统制作工艺：萎凋、干燥，自然陈化', '福鼎白茶集团有限公司', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次寿眉白茶饼采用2020年福鼎磻溪高山寿眉为原料，经过4年自然陈化。茶饼紧压成型，色泽深褐，枣香蜜韵明显。冲泡后汤色深黄透亮，滋味甜润绵滑，适合煮饮。老寿眉具有清热降火、养胃护肝的功效，是煮饮的绝佳选择。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (31, 'TEA20240425031', 31, 3, 'AHQLC2024042501', '湖南省益阳市安化县高马二溪产区', '2024-04-25', '千两茶传统制作：杀青、揉捻、渥堆、踩制、日晒', '安化黑茶集团有限公司', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', '本批次千两茶产自安化高马二溪核心产区，采用传统千两茶制作工艺。茶柱紧实，外包竹篾和棕叶。冲泡后菌香浓郁，滋味醇厚回甘，汤色红浓明亮。千两茶被誉为\"世界茶王\"，是安化黑茶的代表产品，具有很高的收藏价值和保健功效。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (32, 'TEA20240428032', 32, 3, 'AHFZC2024042801', '湖南省益阳市安化县云台山产区', '2024-04-28', '茯砖茶传统制作：杀青、揉捻、渥堆、发花、干燥', '安化黑茶集团有限公司', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次茯砖茶产自安化云台山产区，采用传统发花工艺。茶砖紧实，金花茂盛，菌香馥郁。冲泡后汤色红浓透亮，滋味顺滑甜润，具有降脂消食的功效。茯砖茶中的\"金花\"是冠突散囊菌，对人体健康有益，是黑茶中的珍品。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (33, 'TEA20240502033', 33, 3, 'GXLBC2024050201', '广西壮族自治区梧州市苍梧县六堡镇', '2024-05-02', '六堡茶传统制作：杀青、揉捻、渥堆、陈化', '梧州六堡茶业有限公司', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', '本批次六堡茶产自梧州六堡镇核心产区，采用传统六堡茶制作工艺。干茶条索紧结，色泽黑褐油润。冲泡后槟榔香明显，滋味甜润顺滑，汤色红浓透亮。六堡茶是中国侨销名茶，越陈越佳，具有祛湿消暑、养胃护肝的功效，深受海外华侨喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (34, 'TEA20240505034', 34, 3, 'BD2024050501', '云南省临沧市双江县冰岛老寨古茶园', '2024-05-05', '普洱生茶传统制作：萎凋、杀青、揉捻、日晒干燥、蒸压成饼', '冰岛古茶山茶业有限公司', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', '本批次冰岛古树生茶选用冰岛老寨300年以上古茶树春茶原料。冰岛茶以甜著称，冰糖甜韵明显，茶气柔和。茶饼条索肥壮，白毫显著。冲泡后汤色金黄透亮，香气清雅持久，滋味甘甜醇和，回甘持久。冰岛是普洱茶的顶级产区，茶叶品质卓越，价格昂贵。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (35, 'TEA20240508035', 35, 3, 'XG2024050801', '云南省临沧市临翔区昔归忙麓山古茶园', '2024-05-08', '普洱生茶传统制作：萎凋、杀青、揉捻、日晒干燥、蒸压成饼', '昔归古茶山茶业有限公司', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', '本批次昔归古树生茶选用昔归忙麓山200年以上古茶树春茶原料。昔归茶以兰花香著称，茶气强劲。茶饼条索紧结，色泽墨绿。冲泡后兰香高扬持久，汤色金黄透亮，滋味醇厚回甘迅速。昔归是临沧茶区的明星产区，茶叶品质优异，深受茶客喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (36, 'TEA20240510036', 36, 3, 'MKDXS2024051001', '云南省临沧市双江县勐库大雪山野生茶林', '2024-05-10', '野生茶传统制作：萎凋、杀青、揉捻、日晒干燥、蒸压成饼', '勐库大雪山茶业有限公司', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次大雪山野生茶采自勐库大雪山海拔2500米以上的野生茶林，树龄在千年以上。野生茶野韵十足，山野气息浓郁。茶饼条索粗壮，色泽墨绿。冲泡后香气高扬，滋味霸气十足，茶气强劲，回甘持久。野生茶产量稀少，是茶中瑰宝，具有很高的收藏价值。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (37, 'TEA20230515037', 37, 3, 'BLS2023051501', '云南省西双版纳州勐海县布朗山老曼峨', '2023-05-15', '普洱熟茶制作：渥堆发酵、翻堆、出堆、筛分、蒸压成饼', '布朗山茶业有限公司', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', '本批次布朗山熟普洱采用布朗山老曼峨优质晒青毛茶为原料，经过45天传统渥堆发酵。茶饼紧实，条索清晰，陈香馥郁。冲泡后汤色红浓透亮，滋味醇厚饱满，茶气足，口感厚重。布朗山熟茶以其独特的陈香木香和醇厚口感而著称，是熟茶爱好者的首选。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (38, 'TEA20240518038', 38, 3, 'NMXPE2024051801', '云南省西双版纳州勐海县茶区', '2024-05-18', '糯米香普洱制作：熟茶配糯米香叶，混合压制', '勐海茶厂', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', 'https://images.unsplash.com/photo-1563911302283-d2bc129e7570?w=600', '本批次糯米香普洱采用勐海优质熟茶配以天然糯米香叶混合压制。茶饼紧实，糯米香浓郁。冲泡后糯米香与茶香完美融合，清甜可口，汤色红浓透亮，滋味甜润适口。糯米香普洱口感独特，香气怡人，特别受女性茶友喜爱，是日常品饮的优质选择。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (39, 'TEA20240305039', 39, 2, 'BHYZ2024030501', '福建省宁德市福鼎市太姥山核心产区', '2024-03-05', '白茶传统制作工艺：萎凋、干燥，不炒不揉', '福鼎白茶集团有限公司', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次白毫银针特级为2024年头采，原料选自福鼎太姥山核心产区大白茶品种的芽头。采用传统白茶工艺，最大程度保留茶叶天然成分。成品茶芽头肥壮，白毫密披，如银针直立。冲泡后毫香浓郁，滋味鲜爽甘甜，汤色杏黄明亮。白毫银针是白茶中的极品，品质卓越。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (40, 'TEA20240310040', 40, 2, 'ZHBHYZ2024031001', '福建省南平市政和县高山茶园', '2024-03-10', '白茶传统制作工艺：萎凋、干燥，不炒不揉', '政和白茶集团有限公司', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', '本批次政和白毫银针产自政和高山茶园，采用政和大白茶品种。政和白茶以高氨基酸含量著称，口感更加清甜爽口。成品茶芽头肥壮，白毫显露。冲泡后清香甜润，滋味鲜爽回甘持久，汤色杏黄明亮。政和白毫银针品质优异，是白茶中的珍品。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (41, 'TEA20240315041', 41, 2, 'FDBMD2024031501', '福建省宁德市福鼎市点头核心产区', '2024-03-15', '白茶传统制作工艺：萎凋、干燥', '福鼎白茶集团有限公司', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', '本批次福鼎白牡丹特级采自福鼎点头核心产区，一芽两叶，采用传统白茶工艺。干茶芽叶连枝，色泽灰绿，白毫显露。冲泡后花香明显，滋味甘甜爽口，汤色杏黄明亮。福鼎白牡丹是白茶中的经典品种，香气优雅，口感平衡，性价比高，被誉为\"性价比之王\"。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (42, 'TEA20240320042', 42, 2, 'HYBMD2024032001', '福建省宁德市福鼎市磻溪荒野茶园', '2024-03-20', '白茶传统制作工艺：萎凋、干燥', '福鼎白茶集团有限公司', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次荒野白牡丹采自福鼎磻溪荒野茶园，茶树自然生长，无人工干预。荒野茶野韵十足，茶气强劲。干茶芽叶连枝，色泽灰绿，白毫显露。冲泡后野花香浓郁，滋味醇厚，茶气足，汤色杏黄明亮。荒野白牡丹产量稀少，是白茶中的稀缺珍品，品质卓越。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (43, 'TEA20240322043', 43, 2, 'FDGM2024032201', '福建省宁德市福鼎市管阳茶区', '2024-03-22', '白茶传统制作工艺：萎凋、干燥', '福鼎白茶集团有限公司', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=600', '本批次福鼎贡眉特级采自福鼎管阳茶区，选用菜茶品种。干茶叶片稍大，色泽灰绿。冲泡后甜香浓郁，汤感醇厚，汤色橙黄明亮。福鼎贡眉甘醇耐泡，性价比高，是日常口粮茶的优质选择。长期饮用具有清热降火、养生保健的功效，深受茶友喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (44, 'TEA20190325044', 44, 2, 'FDLSMB2019032501', '福建省宁德市福鼎市高山茶园', '2019-03-25', '白茶传统制作工艺：萎凋、干燥，自然陈化', '福鼎白茶集团有限公司', 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=600', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', '本批次福鼎老寿眉饼采用2019年福鼎高山寿眉为原料，经过5年自然陈化。茶饼紧压成型，色泽深褐，药香枣香明显。冲泡后汤色深红透亮，滋味醇厚顺滑，甜度高，适合煮饮。老寿眉具有清热降火、养胃护肝的功效，是煮饮的绝佳选择，深受老茶客喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (45, 'TEA20240328045', 45, 2, 'CSMS2024032801', '福建省宁德市福鼎市太姥山茶区', '2024-03-28', '白茶传统制作工艺：萎凋、干燥', '福鼎白茶集团有限公司', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', '本批次春寿眉散茶采自福鼎太姥山茶区，2024年春茶。干茶叶片稍大，色泽翠绿。冲泡后清香甜润，口感清爽，汤色嫩黄明亮。春寿眉清甜鲜爽，性价比高，是白茶入门的推荐品种。适合日常品饮，具有清热降火、提神醒脑的功效。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (46, 'TEA20240430046', 46, 3, 'AHQLC2024043001', '湖南省益阳市安化县高马二溪产区', '2024-04-30', '千两茶传统制作：杀青、揉捻、渥堆、踩制、日晒', '安化黑茶集团有限公司', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', '本批次安化千两茶产自高马二溪核心产区，采用传统千两茶制作工艺。茶柱紧实，外包竹篾和棕叶。冲泡后菌香浓郁，滋味醇厚回甘，汤色红浓明亮。千两茶被誉为\"世界茶王\"，是安化黑茶的代表产品，具有很高的收藏价值和保健功效，是黑茶爱好者的珍藏佳品。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (47, 'TEA20240505047', 47, 3, 'AHTJC2024050501', '湖南省益阳市安化县云台山产区', '2024-05-05', '天尖茶传统制作：杀青、揉捻、渥堆、干燥', '安化黑茶集团有限公司', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次安化天尖茶产自云台山产区，选用一级黑毛茶为原料。干茶条索紧结，色泽乌黑油润。冲泡后松烟香明显，滋味醇厚，汤色红浓透亮。天尖茶是黑茶中的贵族，品质优异，是高端礼品的首选。具有降脂消食、养胃护肝的功效，深受茶友喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (48, 'TEA20240508048', 48, 3, 'GXLBCTJ2024050801', '广西壮族自治区梧州市苍梧县六堡镇', '2024-05-08', '六堡茶传统制作：杀青、揉捻、渥堆、陈化', '梧州六堡茶业有限公司', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', '本批次六堡茶特级产自梧州六堡镇核心产区，选用一级原料。干茶条索紧结，色泽黑褐油润。冲泡后槟榔香明显，滋味甜润顺滑，汤色红浓透亮。六堡茶是中国侨销名茶，越陈越佳，具有祛湿消暑、养胃护肝的功效，深受海外华侨和国内茶友喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (49, 'TEA20140510049', 49, 3, 'CNLBLC2014051001', '广西壮族自治区梧州市苍梧县六堡镇老茶窖藏', '2014-05-10', '六堡茶传统制作后长期陈化', '梧州六堡茶业有限公司', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=600', '本批次陈年六堡老茶采用2014年六堡茶为原料，经过10年窖藏陈化。干茶色泽黑褐，陈香浓郁。冲泡后汤色红浓陈醇，滋味醇厚顺滑，口感滑润。陈年六堡茶具有很高的收藏价值和保健功效，是茶中瑰宝，深受老茶客和收藏家喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (50, 'TEA20240512050', 50, 3, 'AHFZC2024051201', '湖南省益阳市安化县云台山产区', '2024-05-12', '茯砖茶传统制作：杀青、揉捻、渥堆、发花、干燥', '安化黑茶集团有限公司', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', '本批次安化茯砖茶产自云台山产区，采用传统发花工艺。茶砖紧实，金花茂盛，菌香馥郁。冲泡后汤色红浓透亮，滋味顺滑甜润，具有降脂消食的功效。茯砖茶中的\"金花\"是冠突散囊菌，对人体健康有益，是黑茶中的珍品，深受茶友喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (51, 'TEA20240515051', 51, 3, 'JYFZC2024051501', '陕西省咸阳市泾阳县古法制茶基地', '2024-05-15', '泾阳茯砖茶传统制作：杀青、揉捻、渥堆、发花、干燥', '泾阳茯砖茶业有限公司', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次泾阳茯砖茶采用陕西泾阳古老工艺制作。茶砖紧实，金花闪烁，菌香浓郁。冲泡后汤色红浓透亮，滋味醇和回甘。泾阳茯砖茶历史悠久，是丝绸之路上的重要茶叶品种，具有很高的历史文化价值和保健功效，深受茶友喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (52, 'TEA20240518052', 52, 3, 'HBQZC2024051801', '湖北省咸宁市赤壁市羊楼洞茶区', '2024-05-18', '青砖茶传统制作：杀青、揉捻、渥堆、压制、干燥', '赵李桥茶厂', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', '本批次湖北青砖茶产自赤壁羊楼洞茶区，采用传统米砖工艺。茶砖紧实，色泽青褐。冲泡后滋味醇厚，回味悠长，汤色红浓透亮。青砖茶是边销名茶，历史悠久，具有降脂消食、养胃护肝的功效，深受边疆少数民族和茶友喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (53, 'TEA20240520053', 53, 3, 'LBZ2024052001', '云南省西双版纳州勐海县布朗山老班章村古茶园', '2024-05-20', '普洱生茶传统制作：萎凋、杀青、揉捻、日晒干燥、蒸压成饼', '老班章古树茶专业合作社', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=600', '本批次老班章普洱生茶选用老班章古茶园300年以上古茶树春茶原料。老班章是普洱茶王产区，茶气强劲霸道，回甘迅猛。茶饼条索粗壮肥硕，银毫显著。冲泡后汤色金黄透亮，香气高扬持久，滋味醇厚霸气，是普洱茶收藏与品饮的顶级之选。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (54, 'TEA20240522054', 54, 3, 'YW2024052201', '云南省西双版纳州勐腊县易武古茶山', '2024-05-22', '普洱生茶传统制作：萎凋、杀青、揉捻、日晒干燥、蒸压成饼', '易武古茶山茶业有限公司', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', '本批次易武古树生茶选用易武古茶山200年以上古茶树春茶原料。易武茶以柔著称，被誉为\"茶后\"。茶饼条索肥壮，白毫显著。冲泡后蜜香浓郁，汤感柔滑细腻，汤色金黄透亮，滋味甘甜醇和，回甘持久。易武茶柔中带刚，是普洱茶的经典代表。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (55, 'TEA20240525055', 55, 3, 'BD2024052501', '云南省临沧市双江县冰岛老寨古茶园', '2024-05-25', '普洱生茶传统制作：萎凋、杀青、揉捻、日晒干燥、蒸压成饼', '冰岛古茶山茶业有限公司', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次冰岛古树生茶选用冰岛老寨300年以上古茶树春茶原料。冰岛茶以冰糖甜韵著称，茶气柔和。茶饼条索肥壮，白毫显著。冲泡后汤色金黄透亮，香气清雅持久，滋味甘甜醇和，冰糖甜韵明显，回甘持久。冰岛是普洱茶的顶级名山名寨，茶叶品质卓越。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (56, 'TEA20240528056', 56, 3, 'XG2024052801', '云南省临沧市临翔区昔归忙麓山古茶园', '2024-05-28', '普洱生茶传统制作：萎凋、杀青、揉捻、日晒干燥、蒸压成饼', '昔归古茶山茶业有限公司', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=600', '本批次昔归古树生茶选用昔归忙麓山200年以上古茶树春茶原料。昔归茶以兰花香著称，茶气强劲。茶饼条索紧结，色泽墨绿。冲泡后兰香高扬持久，汤色金黄透亮，滋味醇厚回甘迅速。昔归是临沧茶区的明星产区，忙麓山韵明显，茶叶品质优异。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (57, 'TEA20180530057', 57, 3, 'MHSPE2018053001', '云南省西双版纳州勐海县勐海镇', '2018-05-30', '普洱熟茶制作：渥堆发酵、翻堆、出堆、筛分、蒸压成饼', '勐海茶厂', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次勐海熟普洱茶饼采用勐海地区优质晒青毛茶为原料，经过45天传统渥堆发酵，已陈化6年。茶饼圆润紧实，条索清晰，陈香馥郁无堆味。冲泡后汤色红浓透亮，口感醇厚顺滑，甜度高，无苦涩感，适合日常品饮和煮饮。是口粮熟茶的优质选择。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (58, 'TEA20230602058', 58, 3, 'BLSSPE2023060201', '云南省西双版纳州勐海县布朗山老曼峨', '2023-06-02', '普洱熟茶制作：渥堆发酵、翻堆、出堆、筛分、蒸压成饼', '布朗山茶业有限公司', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', '本批次布朗山熟普洱采用布朗山老曼峨优质晒青毛茶为原料，经过45天传统渥堆发酵。茶饼紧实，条索清晰，陈香木香明显。冲泡后汤色红浓透亮，滋味醇厚饱满，口感厚重，茶气足。布朗山熟茶以其独特的陈香木香和醇厚口感而著称，是熟茶爱好者的首选。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (59, 'TEA20240605059', 59, 3, 'LCTJT2024060501', '云南省西双版纳州勐海县勐海茶厂', '2024-06-05', '老茶头自然形成于渥堆发酵过程，精选筛分', '勐海茶厂', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', '本批次老茶头特级是普洱熟茶渥堆发酵过程中自然形成的茶块，是茶之精华。干茶紧结成块，色泽黑褐油润。冲泡后糯香十足，滋味甜润可口，汤色红浓透亮。老茶头耐泡度极高，可冲泡20余次，是耐泡之王。适合煮饮，口感更佳，深受茶友喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (60, 'TEA20240608060', 60, 3, 'MKDXSYS2024060801', '云南省临沧市双江县勐库大雪山野生茶林', '2024-06-08', '野生茶传统制作：萎凋、杀青、揉捻、日晒干燥、蒸压成饼', '勐库大雪山茶业有限公司', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次勐库大雪山野生茶采自海拔2500米以上的野生茶林，树龄在千年以上。野生茶野韵十足，山野气息浓郁。茶饼条索粗壮，色泽墨绿。冲泡后香气高扬，滋味霸气十足，茶气强劲，回甘持久。野生茶产量稀少，是茶中瑰宝，具有很高的收藏价值和品饮价值。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (61, 'TEA20240610061', 61, 3, 'JM2024061001', '云南省普洱市澜沧县景迈山古茶园', '2024-06-10', '普洱生茶传统制作：萎凋、杀青、揉捻、日晒干燥、蒸压成饼', '景迈山古茶园茶业有限公司', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=600', '本批次景迈古树茶产自景迈山千年万亩古茶林，树龄在300年以上。景迈茶以兰香蜜韵著称。茶饼条索肥壮，白毫显著。冲泡后兰花香浓郁，蜜韵明显，汤色金黄透亮，滋味甘甜醇和，回甘持久。景迈山是世界茶源地之一，茶叶品质优异，深受茶友喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (62, 'TEA20240612062', 62, 3, 'BW2024061201', '云南省普洱市澜沧县邦崴古茶树群落', '2024-06-12', '普洱生茶传统制作：萎凋、杀青、揉捻、日晒干燥、蒸压成饼', '邦崴古茶树茶业有限公司', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', '本批次邦崴古树茶产自邦崴古茶树群落，是过渡型茶王的产区。邦崴茶野韵幽香，是珍稀品种。茶饼条索肥壮，色泽墨绿。冲泡后野花香明显，汤色金黄透亮，滋味醇厚回甘悠长。邦崴古茶树是野生茶树与栽培茶树的过渡型，具有很高的科研价值和品饮价值。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (63, 'TEA20240615063', 63, 3, 'XHXQG2024061501', '广东省江门市新会区核心产区及云南省西双版纳州勐海县', '2024-06-15', '小青柑制作：鲜柑掏空、装入普洱熟茶、低温烘干', '新会柑普茶业有限公司', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=600', '本批次新会小青柑普洱选用新会核心产区七八月份的青柑，搭配勐海优质宫廷普洱熟茶。青柑小巧饱满，柑皮油亮，内装普洱茶条索匀整。冲泡后柑香与陈香完美融合，清新甜润，口感丝滑，具有理气健脾、降脂消食的功效，是办公室茶饮的绝佳之选。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (64, 'TEA20140618064', 64, 3, 'CPPE2014061801', '广东省江门市新会区核心产区及云南省西双版纳州勐海县', '2014-06-18', '陈皮普洱制作：十年陈皮配熟茶，混合压制', '新会柑普茶业有限公司', 'https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次陈皮普洱茶采用十年新会陈皮配以勐海优质熟茶混合压制。茶饼紧实，陈皮香浓郁。冲泡后陈皮香与茶香完美融合，汤色红浓透亮，滋味甜润顺滑。十年陈皮药用价值高，具有理气健脾、化痰止咳的功效。陈皮普洱是养生佳品，深受茶友喜爱。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');
INSERT INTO `product_trace` VALUES (65, 'TEA20240620065', 65, 3, 'DHGPE2024062001', '广东省江门市新会区及云南省西双版纳州勐海县', '2024-06-20', '大红柑制作：成熟大红柑掏空、装入普洱熟茶、低温烘干', '新会柑普茶业有限公司', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600', 'https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600', '本批次大红柑普洱茶选用新会成熟大红柑，搭配勐海优质熟茶。大红柑果香浓郁，柑皮厚实。冲泡后柑果香甜，茶味醇厚，汤色红浓透亮，滋味甜润饱满。大红柑普洱口感醇厚，香气浓郁，具有理气健脾、润肺化痰的功效，是秋冬季节的养生佳品。', 'APPROVED', NULL, '2026-01-19 00:00:00', '2026-01-19 00:00:00');

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `order_item_id` bigint NOT NULL COMMENT '订单项ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `rating` tinyint NOT NULL COMMENT '评分1-5',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `images` json NULL COMMENT '图片列表',
  `status` enum('PENDING','APPROVED','REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `reject_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_item_id`(`order_item_id` ASC) USING BTREE,
  INDEX `idx_review_product_status`(`product_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_review_order_item`(`order_item_id` ASC) USING BTREE,
  INDEX `idx_review_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1011 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review
-- ----------------------------
INSERT INTO `review` VALUES (1000, 1, 1000, 3, 5, '味道甘甜，回味无穷', NULL, 'APPROVED', NULL, '2026-01-07 13:48:38');
INSERT INTO `review` VALUES (1004, 1, 22, 16, 4, '西湖龙井不愧是你', NULL, 'APPROVED', NULL, '2026-01-30 23:39:31');
INSERT INTO `review` VALUES (1005, 5, 23, 16, 4, '安溪铁观音你真行', NULL, 'APPROVED', NULL, '2026-01-30 23:42:20');
INSERT INTO `review` VALUES (1006, 5, 1039, 16, 5, '安溪铁观音666', NULL, 'APPROVED', NULL, '2026-01-31 00:12:03');
INSERT INTO `review` VALUES (1007, 6, 1038, 16, 5, '清香铁观音666', NULL, 'APPROVED', NULL, '2026-01-31 00:12:03');
INSERT INTO `review` VALUES (1008, 1, 1037, 16, 5, '测试图片显示', NULL, 'APPROVED', NULL, '2026-01-31 00:15:57');
INSERT INTO `review` VALUES (1009, 1, 1036, 16, 5, '非常好，我觉得我会回购', '[\"/api/uploads/2026/01/31/f9c9e924d04d4c0c87e7069e2976e58e.png\"]', 'APPROVED', NULL, '2026-01-31 00:19:58');
INSERT INTO `review` VALUES (1010, 5, 1035, 16, 5, '我喜欢这个茶非常喜欢', '[\"/api/uploads/2026/01/31/17e1c06eb25940dea60a7b252d64457e.png\", \"/api/uploads/2026/01/31/5cc93241a624479c8a92c3fc23465535.png\", \"/api/uploads/2026/01/31/623ac43c296f44c4a7e13ffc4cac899f.png\", \"/api/uploads/2026/01/31/5c8e9226e5d8429fbe91a2cee6a19538.png\"]', 'APPROVED', NULL, '2026-01-31 00:22:15');

-- ----------------------------
-- Table structure for review_reply
-- ----------------------------
DROP TABLE IF EXISTS `review_reply`;
CREATE TABLE `review_reply`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `review_id` bigint NOT NULL COMMENT '评论ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回复内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `review_id`(`review_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1004 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论回复表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review_reply
-- ----------------------------

-- ----------------------------
-- Table structure for search_outbox_event
-- ----------------------------
DROP TABLE IF EXISTS `search_outbox_event`;
CREATE TABLE `search_outbox_event`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `biz_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型',
  `biz_id` bigint NOT NULL COMMENT '业务ID',
  `event_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '事件类型',
  `payload` json NULL COMMENT '数据负载',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NEW' COMMENT '状态',
  `retry_count` int NOT NULL DEFAULT 0 COMMENT '重试次数',
  `next_retry_at` datetime NULL DEFAULT NULL COMMENT '下次重试时间',
  `last_error` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后错误',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_outbox_status_time`(`status` ASC, `next_retry_at` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_outbox_biz`(`biz_type` ASC, `biz_id` ASC, `created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '搜索同步Outbox表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of search_outbox_event
-- ----------------------------

-- ----------------------------
-- Table structure for shipment
-- ----------------------------
DROP TABLE IF EXISTS `shipment`;
CREATE TABLE `shipment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '物流ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `carrier` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '快递公司',
  `tracking_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '快递单号',
  `trace` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '物流轨迹',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '物流表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shipment
-- ----------------------------

-- ----------------------------
-- Table structure for trace_file
-- ----------------------------
DROP TABLE IF EXISTS `trace_file`;
CREATE TABLE `trace_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `trace_id` bigint NOT NULL COMMENT '溯源ID',
  `type` enum('CERT','REPORT','OTHER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件类型',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件URL',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_trace_file_trace`(`trace_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '溯源文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trace_file
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `password_hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码哈希',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `role` enum('USER','MERCHANT','ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER' COMMENT '角色',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `last_login_at` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `password_updated_at` datetime NULL DEFAULT NULL COMMENT '密码更新时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_role`(`username` ASC, `role` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone_role`(`phone` ASC, `role` ASC) USING BTREE,
  INDEX `idx_user_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_user_username`(`username` ASC) USING BTREE,
  INDEX `idx_user_role_status`(`role` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '13800000000', NULL, '202cb962ac59075b964b07152d234b70', '系统管理员', NULL, 'ADMIN', 1, '2026-01-31 17:22:23', '0:0:0:0:0:0:0:1', NULL, '2026-01-04 17:18:09', '2026-01-31 17:22:23');
INSERT INTO `user` VALUES (3, '123', '19144381496', NULL, '202cb962ac59075b964b07152d234b70', '福建茶业', NULL, 'MERCHANT', 1, '2026-01-05 23:12:01', '0:0:0:0:0:0:0:1', NULL, '2026-01-05 02:41:16', '2026-01-31 18:13:03');
INSERT INTO `user` VALUES (4, '12345', '13900000003', NULL, '202cb962ac59075b964b07152d234b70', '云南普洱', NULL, 'MERCHANT', 1, NULL, NULL, NULL, '2026-01-07 01:14:05', '2026-01-31 18:13:10');
INSERT INTO `user` VALUES (12, '1234', '19144381463', NULL, '202cb962ac59075b964b07152d234b70', '1234', NULL, 'USER', 1, '2026-01-19 13:07:02', '0:0:0:0:0:0:0:1', NULL, '2026-01-08 23:02:42', '2026-01-19 13:07:02');
INSERT INTO `user` VALUES (16, 'test', '19144381495', NULL, '202cb962ac59075b964b07152d234b70', '胡图', '/api/uploads/2026/01/24/1429362918704393af8cc99fdf025d6b.png', 'USER', 1, '2026-01-31 18:07:58', '0:0:0:0:0:0:0:1', NULL, '2026-01-08 23:24:42', '2026-01-31 18:07:57');

-- ----------------------------
-- Table structure for user_balance
-- ----------------------------
DROP TABLE IF EXISTS `user_balance`;
CREATE TABLE `user_balance`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `balance` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '可用余额',
  `frozen_balance` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '冻结余额',
  `total_income` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '累计收入',
  `total_expense` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '累计支出',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户余额表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_balance
-- ----------------------------
INSERT INTO `user_balance` VALUES (1, 16, 500.00, 0.00, 500.00, 0.00, '2026-01-31 01:51:32', '2026-01-31 01:51:32');

-- ----------------------------
-- Table structure for user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `coupon_id` bigint NOT NULL COMMENT '优惠券ID',
  `status` enum('UNUSED','USED','EXPIRED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'UNUSED' COMMENT '状态',
  `claimed_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `used_order_id` bigint NULL DEFAULT NULL COMMENT '使用的订单ID',
  `used_at` datetime NULL DEFAULT NULL COMMENT '使用时间',
  `valid_from` datetime NULL DEFAULT NULL COMMENT '有效期开始',
  `valid_to` datetime NULL DEFAULT NULL COMMENT '有效期结束',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_coupon_user_status`(`user_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_user_coupon_coupon`(`coupon_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------
INSERT INTO `user_coupon` VALUES (1, 16, 1, 'USED', '2026-01-14 21:48:12', 7, '2026-01-18 20:22:19', '2026-01-14 21:48:13', '2026-01-21 21:48:13', '2026-01-14 21:48:12');
INSERT INTO `user_coupon` VALUES (2, 16, 3, 'USED', '2026-01-14 22:23:54', 6, '2026-01-18 20:21:48', '2026-01-14 22:23:54', '2026-01-21 22:23:54', '2026-01-14 22:23:54');
INSERT INTO `user_coupon` VALUES (3, 16, 2, 'UNUSED', '2026-01-14 22:34:17', NULL, NULL, '2026-01-14 22:34:17', '2026-01-15 22:34:17', '2026-01-14 22:34:17');
INSERT INTO `user_coupon` VALUES (4, 12, 2, 'UNUSED', '2026-01-18 16:00:56', NULL, NULL, '2026-01-18 16:00:56', '2026-01-19 16:00:56', '2026-01-18 16:00:56');
INSERT INTO `user_coupon` VALUES (5, 12, 3, 'UNUSED', '2026-01-18 16:00:58', NULL, NULL, '2026-01-18 16:00:58', '2026-01-25 16:00:58', '2026-01-18 16:00:58');
INSERT INTO `user_coupon` VALUES (6, 12, 1, 'UNUSED', '2026-01-18 16:00:59', NULL, NULL, '2026-01-18 16:00:59', '2026-01-25 16:00:59', '2026-01-18 16:00:59');
INSERT INTO `user_coupon` VALUES (7, 16, 4, 'USED', '2026-01-19 00:53:29', 8, '2026-01-19 00:54:15', '2026-01-19 00:53:30', '2026-01-26 00:53:30', '2026-01-19 00:53:29');

-- ----------------------------
-- Table structure for user_event
-- ----------------------------
DROP TABLE IF EXISTS `user_event`;
CREATE TABLE `user_event`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '事件ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话ID',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '事件类型',
  `keyword` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '搜索关键词',
  `payload` json NULL COMMENT '事件数据',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_event_user_type_time`(`user_id` ASC, `type` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_event_type_time`(`type` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_event_keyword_time`(`keyword` ASC, `created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户行为事件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_event
-- ----------------------------

-- ----------------------------
-- Table structure for user_favorite
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_favorite
-- ----------------------------
INSERT INTO `user_favorite` VALUES (3, 16, 12, '2026-01-18 16:08:43');
INSERT INTO `user_favorite` VALUES (4, 16, 65, '2026-01-19 00:10:06');

-- ----------------------------
-- Table structure for user_footprint
-- ----------------------------
DROP TABLE IF EXISTS `user_footprint`;
CREATE TABLE `user_footprint`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '足迹ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `viewed_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_viewed_at`(`viewed_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 165 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户足迹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_footprint
-- ----------------------------
INSERT INTO `user_footprint` VALUES (1, 16, 16, '2026-01-14 23:46:52');
INSERT INTO `user_footprint` VALUES (3, 16, 10, '2026-01-18 16:09:08');
INSERT INTO `user_footprint` VALUES (8, 16, 15, '2026-01-14 23:08:44');
INSERT INTO `user_footprint` VALUES (13, 16, 1, '2026-01-31 00:20:02');
INSERT INTO `user_footprint` VALUES (23, 16, 12, '2026-01-24 22:41:47');
INSERT INTO `user_footprint` VALUES (26, 16, 9, '2026-01-18 16:09:08');
INSERT INTO `user_footprint` VALUES (27, 16, 35, '2026-01-18 16:09:08');
INSERT INTO `user_footprint` VALUES (29, 16, 38, '2026-01-24 22:19:56');
INSERT INTO `user_footprint` VALUES (44, 16, 64, '2026-01-31 18:23:02');
INSERT INTO `user_footprint` VALUES (46, 16, 63, '2026-01-31 18:23:14');
INSERT INTO `user_footprint` VALUES (48, 16, 61, '2026-01-18 16:48:41');
INSERT INTO `user_footprint` VALUES (57, 16, 56, '2026-01-31 02:03:18');
INSERT INTO `user_footprint` VALUES (74, 16, 49, '2026-01-18 20:09:42');
INSERT INTO `user_footprint` VALUES (79, 12, 65, '2026-01-19 13:07:23');
INSERT INTO `user_footprint` VALUES (81, 16, 52, '2026-01-24 23:08:32');
INSERT INTO `user_footprint` VALUES (88, 16, 3, '2026-01-19 01:16:04');
INSERT INTO `user_footprint` VALUES (89, 16, 5, '2026-01-31 00:23:09');
INSERT INTO `user_footprint` VALUES (95, 16, 62, '2026-01-30 23:37:04');
INSERT INTO `user_footprint` VALUES (96, 16, 59, '2026-01-19 01:24:09');
INSERT INTO `user_footprint` VALUES (109, 16, 60, '2026-01-19 01:43:23');
INSERT INTO `user_footprint` VALUES (116, 16, 65, '2026-01-31 18:31:14');
INSERT INTO `user_footprint` VALUES (130, 16, 17, '2026-01-30 23:54:24');
INSERT INTO `user_footprint` VALUES (159, 16, 57, '2026-01-31 17:36:35');

-- ----------------------------
-- Table structure for user_membership
-- ----------------------------
DROP TABLE IF EXISTS `user_membership`;
CREATE TABLE `user_membership`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `level_id` bigint NOT NULL COMMENT '等级ID',
  `points` int NOT NULL DEFAULT 0 COMMENT '积分',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `total_expense` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '累计消费金额',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户会员关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_membership
-- ----------------------------
INSERT INTO `user_membership` VALUES (10, 1, 0, '2026-01-07 01:14:05', 0.00);
INSERT INTO `user_membership` VALUES (16, 3, 0, '2026-01-31 18:23:23', 3792.00);

SET FOREIGN_KEY_CHECKS = 1;
