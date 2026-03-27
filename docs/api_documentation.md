# API 接口文档 - 旅游景点管理系统 (TravelVista)

> Base URL: `http://localhost:8080`
> 认证方式: Sa-Token JWT Token，请求头 `satoken: {token}`

## 通用响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 分页响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

## 一、用户端接口

### 1.1 用户认证

#### POST /api/user/register
注册新用户

**请求体:**
```json
{
  "username": "zhangsan",
  "password": "abc123456",
  "nickname": "张三",
  "email": "zhangsan@email.com",
  "phone": "13800138000"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJ...",
    "userInfo": {
      "id": 1,
      "username": "zhangsan",
      "nickname": "张三",
      "avatar": "/uploads/avatar/default.png",
      "email": "zhangsan@email.com",
      "phone": "13800138000"
    }
  }
}
```
（注册成功后自动登录，返回 token 和 userInfo）

#### POST /api/user/login
用户登录

**请求体:**
```json
{
  "username": "zhangsan",
  "password": "abc123456"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJ...",
    "userInfo": {
      "id": 1,
      "username": "zhangsan",
      "nickname": "张三",
      "avatar": "/uploads/avatar/default.png",
      "email": "zhangsan@email.com",
      "phone": "13800138000"
    }
  }
}
```

#### POST /api/user/logout
用户登出（需认证）

#### GET /api/user/info
获取当前用户信息（需认证）

#### PUT /api/user/info
更新用户信息（需认证）

**请求体:**
```json
{
  "nickname": "新昵称",
  "email": "new@email.com",
  "phone": "13900139000",
  "gender": 1
}
```

#### PUT /api/user/password
修改密码（需认证）

**请求体:**
```json
{
  "oldPassword": "abc123456",
  "newPassword": "newpass123"
}
```

#### POST /api/user/avatar
上传头像（需认证）

**请求:** `multipart/form-data`，字段名 `file`

### 1.2 景点浏览

#### GET /api/scenic/list
景点列表（分页、筛选）

**参数:**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页数量，默认12 |
| categoryId | long | 否 | 分类ID |
| keyword | string | 否 | 搜索关键词（名称/地址） |
| province | string | 否 | 省份 |
| city | string | 否 | 城市 |
| sortBy | string | 否 | 排序：rating/visit/price/latest |
| minPrice | decimal | 否 | 最低票价 |
| maxPrice | decimal | 否 | 最高票价 |

**响应 data.records 元素:**
```json
{
  "id": 1,
  "name": "西湖风景区",
  "coverImage": "/uploads/scenic/xihu.jpg",
  "categoryName": "自然风光",
  "province": "浙江",
  "city": "杭州",
  "ticketPrice": 0.00,
  "avgRating": 4.8,
  "reviewCount": 1234,
  "visitCount": 56789,
  "isHot": true
}
```

#### GET /api/scenic/{id}
景点详情

**响应:**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "name": "西湖风景区",
    "categoryId": 1,
    "categoryName": "自然风光",
    "coverImage": "/uploads/scenic/xihu.jpg",
    "images": [
      { "id": 1, "imageUrl": "/uploads/scenic/xihu1.jpg", "sortOrder": 0 },
      { "id": 2, "imageUrl": "/uploads/scenic/xihu2.jpg", "sortOrder": 1 }
    ],
    "description": "西湖位于杭州市区西面...",
    "detailContent": "<p>富文本内容...</p>",
    "province": "浙江",
    "city": "杭州",
    "address": "浙江省杭州市西湖区龙井路1号",
    "longitude": 120.148583,
    "latitude": 30.242845,
    "ticketPrice": 0.00,
    "openTime": "06:00",
    "closeTime": "18:00",
    "tips": "建议游玩时间3-4小时",
    "avgRating": 4.8,
    "reviewCount": 1234,
    "visitCount": 56789,
    "isHot": true,
    "isFavorited": false
  }
}
```

#### GET /api/scenic/hot
热门景点推荐（返回前8个）

#### GET /api/scenic/search?keyword=xxx
景点搜索

#### GET /api/scenic/categories
获取所有启用的分类

### 1.3 评论

#### GET /api/review/list
获取景点评论列表

**参数:** scenicSpotId (必填), pageNum, pageSize

**响应 data.records:**
```json
{
  "id": 1,
  "userId": 1,
  "nickname": "张三",
  "avatar": "/uploads/avatar/1.jpg",
  "scenicSpotId": 1,
  "scenicSpotName": "西湖风景区",
  "content": "风景太美了...",
  "rating": 5,
  "images": ["/uploads/review/r1.jpg"],
  "createTime": "2026-02-10 14:30:00"
}
```

#### POST /api/review/add
发表评论（需认证）

**请求:** `multipart/form-data`
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| scenicSpotId | long | 是 | 景点ID |
| content | string | 是 | 评论内容 |
| rating | int | 是 | 评分1-5 |
| images | file[] | 否 | 评论图片（最多9张） |

#### DELETE /api/review/{id}
删除我的评论（需认证）

#### GET /api/review/my
我的评论列表（需认证）

### 1.4 订单

#### POST /api/order/create
创建订单（需认证）

**请求体:**
```json
{
  "scenicSpotId": 1,
  "quantity": 2,
  "visitDate": "2026-03-15",
  "contactName": "张三",
  "contactPhone": "13800138000"
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "orderNo": "ORD20260211143000001",
    "totalAmount": 0.00,
    "status": 0,
    "createTime": "2026-02-11 14:30:00"
  }
}
```

#### POST /api/order/{id}/pay
模拟支付（需认证）

#### POST /api/order/{id}/cancel
取消订单（需认证，仅待支付状态可取消）

#### GET /api/order/my
我的订单列表（需认证）

**参数:** pageNum, pageSize, status (可选)

#### GET /api/order/{id}
订单详情（需认证）

### 1.5 收藏

#### POST /api/favorite/toggle
收藏/取消收藏（需认证）

**请求体:**
```json
{ "scenicSpotId": 1 }
```

**响应:**
```json
{ "code": 200, "data": { "favorited": true } }
```

#### GET /api/favorite/my
我的收藏列表（需认证）

#### GET /api/favorite/check/{scenicSpotId}
检查是否已收藏

---

## 二、管理端接口

### 2.1 管理员认证

#### POST /api/admin/login
管理员登录

**请求体:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

#### POST /api/admin/logout
管理员登出

#### GET /api/admin/info
获取管理员信息

### 2.2 景点管理

#### GET /api/admin/scenic/list
景点列表（分页）

**参数:** pageNum, pageSize, keyword, categoryId, status

#### POST /api/admin/scenic/add
新增景点

**请求体:**
```json
{
  "name": "西湖风景区",
  "categoryId": 1,
  "coverImage": "/uploads/scenic/xihu.jpg",
  "images": ["/uploads/scenic/xihu1.jpg", "/uploads/scenic/xihu2.jpg"],
  "description": "简要描述",
  "detailContent": "<p>富文本详情</p>",
  "province": "浙江",
  "city": "杭州",
  "address": "浙江省杭州市西湖区龙井路1号",
  "longitude": 120.148583,
  "latitude": 30.242845,
  "ticketPrice": 0.00,
  "openTime": "06:00",
  "closeTime": "18:00",
  "tips": "建议游玩时间3-4小时",
  "isHot": false
}
```

#### PUT /api/admin/scenic/update
编辑景点

#### GET /api/admin/scenic/{id}
景点详情

#### DELETE /api/admin/scenic/{id}
删除景点

#### PUT /api/admin/scenic/{id}/status
上架/下架景点

**请求体:**
```json
{ "status": 1 }
```

### 2.3 分类管理

#### GET /api/admin/category/list
分类列表

#### POST /api/admin/category/add
新增分类

**请求体:**
```json
{ "name": "自然风光", "icon": "icon-mountain", "sortOrder": 1 }
```

#### PUT /api/admin/category/update
编辑分类

#### DELETE /api/admin/category/{id}
删除分类

### 2.4 用户管理

#### GET /api/admin/user/list
用户列表

**参数:** pageNum, pageSize, keyword, status

#### PUT /api/admin/user/{id}/status
启用/禁用用户

**请求体:**
```json
{ "status": 0 }
```

### 2.5 评论管理

#### GET /api/admin/review/list
评论列表

**参数:** pageNum, pageSize, status, keyword

#### PUT /api/admin/review/{id}/audit
审核评论

**请求体:**
```json
{ "status": 1 }
```
status: 1-通过, 2-拒绝

#### DELETE /api/admin/review/{id}
删除评论

### 2.6 订单管理

#### GET /api/admin/order/list
订单列表

**参数:** pageNum, pageSize, status, keyword, orderNo

#### GET /api/admin/order/{id}
订单详情

#### PUT /api/admin/order/{id}/status
更新订单状态

**请求体:**
```json
{ "status": 2 }
```

### 2.7 数据统计

#### GET /api/admin/stats/overview
总览数据

**响应:**
```json
{
  "code": 200,
  "data": {
    "userCount": 1234,
    "scenicCount": 56,
    "orderCount": 789,
    "totalRevenue": 123456.00,
    "todayVisit": 345,
    "todayOrder": 12
  }
}
```

#### GET /api/admin/stats/visit-trend
近7天访问量趋势

#### GET /api/admin/stats/order-trend
近7天订单趋势

#### GET /api/admin/stats/top-scenic
热门景点TOP10

#### GET /api/admin/stats/category-distribution
景点分类分布

### 2.8 文件上传

#### POST /api/admin/file/upload
文件上传

**请求:** `multipart/form-data`，字段名 `file`

**响应:**
```json
{
  "code": 200,
  "data": { "url": "/uploads/20260211/abc123.jpg" }
}
```
