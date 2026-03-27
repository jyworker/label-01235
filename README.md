# TravelVista 旅游景点管理系统

基于 `Spring Boot + Vue 3 + Element Plus` 的前后端分离项目，包含：
- 用户端（景点浏览、预订、收藏、评论）
- 管理端（景点/分类/用户/评论/订单管理 + 数据看板）

## 技术栈

- 后端：`Spring Boot 2.7.18`、`MyBatis-Plus`、`Sa-Token`、`MySQL 8`
- 前端：`Vue 3`、`Vite 5`、`Element Plus`、`Pinia`、`Axios`
- 部署：`Docker`、`Docker Compose`、`Nginx`

## 主要功能

### 用户端
- 用户注册/登录、个人信息管理
- 景点列表、详情（图文 + 视频）、搜索筛选
- 收藏、评论（支持上传图片）
- 订单下单、支付、取消、详情
- 0 元景点下单后自动置为“已支付”

### 管理端
- 管理员登录与权限控制
- 景点管理（新增、编辑、上下架、删除）
- 景点新增/编辑为独立页面：`/scenic/add`、`/scenic/edit/:id`
- 富文本、封面图、多图、视频上传
- 分类管理、用户管理、评论审核、订单状态管理
- 数据看板（总览、趋势、分类分布、TOP10）
- 评论管理支持按“评论内容/用户名/昵称/手机号”搜索

## 快速启动（推荐）

在项目根目录执行：

```bash
docker compose down
docker compose up -d --build
```

启动完成后访问：
- 管理端：<http://localhost:8081>
- 用户端：<http://localhost:8082>
- 后端 API：<http://localhost:8080>
- 接口文档（Knife4j）：<http://localhost:8080/doc.html>

## 本地开发启动

前置要求：
- JDK 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0

1) 初始化数据库

```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

2) 启动后端

```bash
cd backend
mvn spring-boot:run
```

3) 启动管理端

```bash
cd frontend-admin
npm install
npm run dev
```

4) 启动用户端

```bash
cd frontend-user
npm install
npm run dev
```

## 默认账号

- 管理员：`admin / admin123`
- 用户：`user / user123`
- 用户：`tourist / user123`

## 上传与静态资源说明

- 后端上传目录配置：
  - `backend/src/main/resources/application.yml`
  - `upload.path: ${UPLOAD_PATH:./uploads/}`
- Docker 默认通过环境变量指定：
  - `UPLOAD_PATH=/app/uploads/`
  - 并挂载卷：`./backend/uploads:/app/uploads`
- Nginx 已配置大文件上传上限：
  - `frontend-admin/nginx.conf`、`frontend-user/nginx.conf`
  - `client_max_body_size 210m`

> 如果修改了后端配置或 Nginx 配置，请重建容器使配置生效：
>
> `docker compose up -d --build backend frontend-admin frontend-user`

## 目录结构

```text
label-01235/
├── backend/                  # Spring Boot 后端
├── frontend-admin/           # 管理端前端
├── frontend-user/            # 用户端前端
├── docs/                     # 设计与接口文档
├── docker-compose.yml
└── README.md
```
