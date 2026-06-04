# WordMind - 英语单词思维导图记忆系统

## 项目概述

WordMind 是一个全栈英语单词记忆系统，通过思维导图的方式帮助用户建立单词之间的关联，提高记忆效率。

## 技术栈

- **前端**: Vue 3 + TypeScript + Vite + Element Plus + Pinia + Vue Router + Axios + ECharts
- **后端**: Spring Boot 2.6.x + Spring Security + Spring Data JPA + JWT
- **数据库**: H2 (Server Mode)
- **容器化**: Docker + Docker Compose

## 端口配置

| 服务 | 端口 |
|------|------|
| Frontend | 3139 |
| Backend | 8139 |
| H2 Database | 9139 |

## 快速启动

### 使用 Docker Compose（推荐）

```bash
# 启动所有服务（唯一命令）
docker compose up

# 后台运行
docker compose up -d

# 查看日志
docker compose logs -f

# 停止服务
docker compose down
```

### 访问地址

- 前端: http://localhost:3139
- 后端 API: http://localhost:8139/api
- H2 Console: http://localhost:9139

## 测试账号

### 普通用户
- 用户名: `user`
- 密码: `user123`

### 管理员
- 用户名: `admin`
- 密码: `admin123`

## 功能清单

### F1: 用户认证与权限
- [x] 用户注册/登录/登出
- [x] JWT Token 鉴权
- [x] BCrypt 密码加密
- [x] USER/ADMIN 角色区分

### F2: 词库管理
- [x] 单词增删改查（ADMIN）
- [x] CSV 批量导入
- [x] 单词搜索筛选

### F3: 思维导图记忆
- [x] 可视化思维导图展示
- [x] 一度/二度关系展开
- [x] 节点点击交互
- [x] 关系类型：同义、反义、主题、词根、前缀、后缀、场景

### F4: 背诵与复习
- [x] 学习计划管理
- [x] 今日复习列表
- [x] 复习标记（认识/模糊/不认识）
- [x] 艾宾浩斯简化算法

### F5: 测验与统计
- [x] 10题单词测验
- [x] 测验结果统计
- [x] 个人学习数据仪表盘

### F6: 统一体验与错误处理
- [x] Loading 状态
- [x] 空状态提示
- [x] 错误消息提示（Element Plus Message）
- [x] 统一 API 响应格式

## 页面清单

- [x] 登录/注册页
- [x] 仪表盘（今日任务、掌握统计）
- [x] 单词列表页（筛选/搜索）
- [x] 单词详情页（含音标、词义、例句、记忆提示）
- [x] 思维导图页（节点展开、关系连线、点击跳转）
- [x] 今日复习页（认识/模糊/不认识）
- [x] 个人学习记录页
- [x] 管理员词库管理页（增删改查、批量导入）

## API 文档

详见 [API_SPEC.md](./API_SPEC.md)

## 验证步骤

1. 执行 `docker compose up` 启动服务
2. 访问 http://localhost:3139
3. 使用测试账号登录
4. 测试各功能模块
5. 查看 evidence/ 目录下的截图证据

## 项目结构

```
.
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 3 前端
├── evidence/         # 验证截图
├── docker-compose.yml
├── API_SPEC.md       # API 接口文档
├── checklist.md      # 功能检查清单
└── README.md         # 项目说明
```

## 开发说明

### 后端开发

```bash
cd backend
./gradlew bootRun
```

### 前端开发

```bash
cd frontend
npm install
npm run dev
```

## 许可证

MIT
