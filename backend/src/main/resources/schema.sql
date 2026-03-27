-- -*- coding: utf-8 -*-
-- TravelVista 旅游景点管理系统 - 数据库初始化脚本

CREATE DATABASE IF NOT EXISTS travel_vista DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE travel_vista;

-- ============================================
-- 用户表
-- ============================================
DROP TABLE IF EXISTS `favorite`;
DROP TABLE IF EXISTS `review_image`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `ticket_order`;
DROP TABLE IF EXISTS `scenic_spot_image`;
DROP TABLE IF EXISTS `scenic_spot`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `admin`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT '/uploads/avatar/default.png' COMMENT '头像URL',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `gender` TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 管理员表
-- ============================================
CREATE TABLE `admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT '/uploads/avatar/admin_default.png' COMMENT '头像URL',
    `role` VARCHAR(20) DEFAULT 'ADMIN' COMMENT '角色',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ============================================
-- 景点分类表
-- ============================================
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点分类表';

-- ============================================
-- 景点信息表
-- ============================================
CREATE TABLE `scenic_spot` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '景点ID',
    `name` VARCHAR(100) NOT NULL COMMENT '景点名称',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片',
    `description` TEXT COMMENT '简要描述',
    `detail_content` LONGTEXT COMMENT '富文本详情内容',
    `video_url` varchar(500) DEFAULT NULL COMMENT '视频链接',
    `province` VARCHAR(50) DEFAULT NULL COMMENT '省份',
    `city` VARCHAR(50) DEFAULT NULL COMMENT '城市',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
    `longitude` DECIMAL(10,7) DEFAULT NULL COMMENT '经度',
    `latitude` DECIMAL(10,7) DEFAULT NULL COMMENT '纬度',
    `ticket_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '门票价格',
    `open_time` VARCHAR(100) DEFAULT NULL COMMENT '开放时间',
    `close_time` VARCHAR(100) DEFAULT NULL COMMENT '关闭时间',
    `tips` TEXT COMMENT '游玩提示',
    `avg_rating` DECIMAL(3,2) DEFAULT 0.00 COMMENT '平均评分',
    `review_count` INT DEFAULT 0 COMMENT '评论数量',
    `visit_count` INT DEFAULT 0 COMMENT '访问量',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-下架 1-正常',
    `is_hot` TINYINT DEFAULT 0 COMMENT '是否热门 0-否 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_hot` (`is_hot`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点信息表';

-- ============================================
-- 景点图片表
-- ============================================
CREATE TABLE `scenic_spot_image` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `scenic_spot_id` BIGINT NOT NULL COMMENT '景点ID',
    `image_url` VARCHAR(255) NOT NULL COMMENT '图片URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_scenic_spot_id` (`scenic_spot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点图片表';

-- ============================================
-- 评论表
-- ============================================
CREATE TABLE `review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `scenic_spot_id` BIGINT NOT NULL COMMENT '景点ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `rating` TINYINT NOT NULL COMMENT '评分1-5',
    `status` TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-已通过 2-已拒绝',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_scenic_spot_id` (`scenic_spot_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ============================================
-- 评论图片表
-- ============================================
CREATE TABLE `review_image` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `review_id` BIGINT NOT NULL COMMENT '评论ID',
    `image_url` VARCHAR(255) NOT NULL COMMENT '图片URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_review_id` (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论图片表';

-- ============================================
-- 订单表
-- ============================================
CREATE TABLE `ticket_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `scenic_spot_id` BIGINT NOT NULL COMMENT '景点ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
    `visit_date` DATE DEFAULT NULL COMMENT '游玩日期',
    `contact_name` VARCHAR(50) DEFAULT NULL COMMENT '联系人姓名',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系人电话',
    `status` TINYINT DEFAULT 0 COMMENT '状态 0-待支付 1-已支付 2-已使用 3-已取消 4-已退款',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_scenic_spot_id` (`scenic_spot_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================
-- 收藏表
-- ============================================
CREATE TABLE `favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `scenic_spot_id` BIGINT NOT NULL COMMENT '景点ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_scenic` (`user_id`, `scenic_spot_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_scenic_spot_id` (`scenic_spot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ============================================
-- 初始数据
-- ============================================

-- 管理员账号 (密码: admin123, BCrypt加密)
INSERT INTO `admin` (`username`, `password`, `nickname`, `role`) VALUES
('admin', '$2a$10$uZHUbgohmpM5YHKFJ3EHiuWtnAB83ZphgPGqdJFojo2eYIv4czn32', '超级管理员', 'SUPER_ADMIN');

-- 测试用户 (密码: user123, BCrypt加密)
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `phone`, `gender`) VALUES
('user', '$2a$10$xNBr6tGNuDpP8FmUowSreuaXCdS6v2gspDVYR.wHnaM4jMasYGUE6', '旅行达人', 'user@test.com', '13800138000', 1),
('tourist', '$2a$10$xNBr6tGNuDpP8FmUowSreuaXCdS6v2gspDVYR.wHnaM4jMasYGUE6', '快乐游客', 'tourist@test.com', '13900139000', 2);

-- 景点分类
INSERT INTO `category` (`name`, `icon`, `sort_order`) VALUES
('自然风光', 'Mountain', 1),
('历史古迹', 'Landmark', 2),
('主题乐园', 'Ferris', 3),
('宗教文化', 'Temple', 4),
('城市公园', 'Trees', 5),
('海滨沙滩', 'Waves', 6),
('博物馆', 'Museum', 7),
('古镇村落', 'Home', 8);

-- 示例景点数据
INSERT INTO `scenic_spot` (`name`, `category_id`, `cover_image`, `description`, `detail_content`, `video_url`, `province`, `city`, `address`, `longitude`, `latitude`, `ticket_price`, `open_time`, `close_time`, `tips`, `avg_rating`, `review_count`, `visit_count`, `is_hot`) VALUES
('西湖风景区', 1, '/uploads/scenic/default1.jpg', '杭州西湖是世界文化遗产，以"一山、二塔、三岛、三堤、五湖"为基本格局，以秀丽的湖光山色和众多的名胜古迹闻名中外。', '<h2>景区简介</h2><p>杭州西湖位于浙江省杭州市西湖区龙井路1号，以秀丽的湖光山色和众多的名胜古迹闻名中外，是中国著名的旅游胜地。</p><h2>主要景点</h2><ul><li>断桥残雪</li><li>三潭印月</li><li>雷峰夕照</li><li>苏堤春晓</li></ul>', '/uploads/video/sample-intro.mp4', '浙江', '杭州', '浙江省杭州市西湖区龙井路1号', 120.1485830, 30.2428450, 0.00, '全天开放', '全天开放', '建议游玩时间3-4小时，建议自带饮用水', 4.80, 2345, 89012, 1),
('故宫博物院', 2, '/uploads/scenic/default2.jpg', '故宫博物院建立于1925年，是世界上现存规模最大、保存最为完整的木质结构古建筑群之一。', '<h2>景区简介</h2><p>北京故宫，又名紫禁城，是明清两代的皇家宫殿，旧称为紫禁城，位于北京中轴线的中心。</p><h2>推荐路线</h2><ol><li>午门入 → 太和殿 → 中和殿 → 保和殿</li><li>乾清宫 → 坤宁宫 → 御花园</li><li>神武门出</li></ol>', '/uploads/video/sample-intro.mp4', '北京', '北京', '北京市东城区景山前街4号', 116.3972300, 39.9163400, 60.00, '08:30', '17:00', '建议提前网上预约购票，周一闭馆', 4.90, 5678, 156789, 1),
('张家界国家森林公园', 1, '/uploads/scenic/default3.jpg', '张家界国家森林公园是中国第一个国家森林公园，以独特的石英砂岩峰林峡谷地貌闻名世界。', '<h2>景区简介</h2><p>张家界国家森林公园位于湖南省张家界市武陵源区，以独特的石英砂岩峰林地貌著称。</p>', '/uploads/video/sample-intro.mp4', '湖南', '张家界', '湖南省张家界市武陵源区', 110.4791870, 29.3252670, 225.00, '07:00', '18:00', '建议游玩2-3天，需穿舒适的运动鞋', 4.70, 1234, 67890, 1),
('上海迪士尼度假区', 3, '/uploads/scenic/default4.jpg', '上海迪士尼度假区是中国内地首座迪士尼主题乐园，拥有七大主题园区。', '<h2>景区简介</h2><p>上海迪士尼度假区位于上海市浦东新区，包含上海迪士尼乐园、迪士尼小镇等。</p>', '/uploads/video/sample-intro.mp4', '上海', '上海', '上海市浦东新区川沙镇黄赵路310号', 121.6740050, 31.1440490, 435.00, '08:00', '22:00', '建议工作日前往避开人流高峰', 4.60, 3456, 123456, 1),
('丽江古城', 8, '/uploads/scenic/default5.jpg', '丽江古城是中国以整座古城申报世界文化遗产获得成功的两座古城之一。', '<h2>景区简介</h2><p>丽江古城位于云南省丽江市古城区，始建于宋末元初。</p>', '/uploads/video/sample-intro.mp4', '云南', '丽江', '云南省丽江市古城区', 100.2330840, 26.8721760, 50.00, '全天开放', '全天开放', '建议晚上去感受古城夜景', 4.50, 2100, 78901, 1),
('泰山', 1, '/uploads/scenic/default6.jpg', '泰山是中国五岳之首，有"天下第一山"之称，是世界自然与文化遗产。', '<h2>景区简介</h2><p>泰山位于山东省泰安市泰山区红门路，被誉为"五岳之首"。</p>', '/uploads/video/sample-intro.mp4', '山东', '泰安', '山东省泰安市泰山区红门路', 117.1009040, 36.2525260, 115.00, '全天开放', '全天开放', '建议夜间登山看日出', 4.70, 1890, 56234, 0),
('鼓浪屿', 6, '/uploads/scenic/default7.jpg', '鼓浪屿是厦门最具代表性的旅游景点，以其独特的建筑风格和优美的自然风光闻名。', '<h2>景区简介</h2><p>鼓浪屿位于福建省厦门市思明区，是一座面积约1.88平方公里的小岛。</p>', '/uploads/video/sample-intro.mp4', '福建', '厦门', '福建省厦门市思明区鼓浪屿', 118.0644370, 24.4487020, 0.00, '全天开放', '全天开放', '需乘渡轮前往，建议提前购票', 4.60, 1567, 45678, 0),
('兵马俑博物馆', 7, '/uploads/scenic/default8.jpg', '秦始皇兵马俑博物馆是世界最大的地下军事博物馆，被誉为"世界第八大奇迹"。', '<h2>景区简介</h2><p>秦始皇兵马俑博物馆位于陕西省西安市临潼区秦陵北路，是以秦始皇兵马俑为基础建立的遗址类博物馆。</p>', '/uploads/video/sample-intro.mp4', '陕西', '西安', '陕西省西安市临潼区秦陵北路', 109.2732210, 34.3842770, 120.00, '08:30', '18:00', '建议请导游讲解历史背景', 4.80, 2890, 98765, 1);

-- 示例评论
INSERT INTO `review` (`user_id`, `scenic_spot_id`, `content`, `rating`, `status`) VALUES
(1, 1, '西湖真的太美了！断桥残雪名不虚传，荷花盛开的季节来最好。建议带上相机，处处是风景。', 5, 1),
(1, 2, '故宫的建筑震撼人心，建议请一个导游，能了解更多历史故事。记得提前网上预约！', 5, 1),
(2, 1, '环湖骑行非常舒适，不过周末人很多，建议工作日来。苏堤春晓特别推荐！', 4, 1),
(2, 4, '迪士尼乐园很好玩！翱翔飞越地平线和创极速光轮是必玩项目，排队较长要有心理准备。', 5, 1),
(1, 5, '丽江古城很有味道，晚上的酒吧街很热闹。不过商业化有点重，建议去偏僻的小巷转转。', 4, 1);

-- 示例订单
INSERT INTO `ticket_order` (`order_no`, `user_id`, `scenic_spot_id`, `quantity`, `total_amount`, `visit_date`, `contact_name`, `contact_phone`, `status`, `pay_time`) VALUES
('ORD20260210001', 1, 2, 2, 120.00, '2026-03-01', '旅行达人', '13800138000', 1, '2026-02-10 10:30:00'),
('ORD20260210002', 1, 4, 1, 435.00, '2026-03-15', '旅行达人', '13800138000', 0, NULL),
('ORD20260211001', 2, 3, 2, 450.00, '2026-04-01', '快乐游客', '13900139000', 1, '2026-02-11 09:00:00');

-- 示例收藏
INSERT INTO `favorite` (`user_id`, `scenic_spot_id`) VALUES
(1, 1), (1, 2), (1, 5),
(2, 1), (2, 4), (2, 8);
