# 智慧健康管理系统 (IHMS - Intelligent Health Management System)

一个完整的健康管理平台，包含前端用户界面、后台管理系统和数据库，旨在帮助用户记录、分析和管理个人健康信息。

## 项目介绍

智慧健康管理系统是一个用于个人健康数据记录与管理的Web应用。系统提供简单直观的界面，帮助用户追踪健康指标、设置健康提醒、查询健康知识等功能，辅助用户进行日常健康管理。同时提供管理后台，用于健康数据的集中管理与分析。

下面是一些图片展示

首页展示

![首页](https://media.githubusercontent.com/media/JX05120LLL/IHMS/refs/heads/main/IHMS-admin/src/main/resources/pic/%E9%A6%96%E9%A1%B5%E5%B1%95%E7%A4%BA.png?token=BDEY44WZY2AYDH5R3MHWXVLIGVX3O)

信息页面展示

![信息页面](https://media.githubusercontent.com/media/JX05120LLL/IHMS/refs/heads/main/IHMS-admin/src/main/resources/pic/%E4%BF%A1%E6%81%AF%E5%B1%95%E7%A4%BA.png?token=BDEY44RMJPWBZV7YUJOTTWLIGVYAG)

医生互动页面展示

![医生互动](https://media.githubusercontent.com/media/JX05120LLL/IHMS/refs/heads/main/IHMS-admin/src/main/resources/pic/%E5%8C%BB%E7%94%9F%E5%AF%B9%E8%AF%9D%E5%B1%95%E7%A4%BA1.png?token=BDEY44S5XU2Z6FLM3LG7CELIGVYCU)



deepseek咨询助手展示

![AI助手展示1](https://media.githubusercontent.com/media/JX05120LLL/IHMS/refs/heads/main/IHMS-admin/src/main/resources/pic/%E5%92%A8%E8%AF%A2%E5%8A%A9%E6%89%8B%E5%B1%95%E7%A4%BA1.png?token=BDEY44TX2FWUHVACGQZAUYTIGVYEY)

![AI助手展示2](https://media.githubusercontent.com/media/JX05120LLL/IHMS/refs/heads/main/IHMS-admin/src/main/resources/pic/%E5%92%A8%E8%AF%A2%E5%8A%A9%E6%89%8B%E5%B1%95%E7%A4%BA2.png?token=BDEY44RQ5AQX2PO3AEDC2ITIGVYG6)

健康数据展示

![健康数据](https://media.githubusercontent.com/media/JX05120LLL/IHMS/refs/heads/main/IHMS-admin/src/main/resources/pic/%E5%81%A5%E5%BA%B7%E6%95%B0%E6%8D%AE.png?token=BDEY44RBWUSJTGGSEZ2EJP3IGVYL6)



后台管理页面展示

![后台仪表盘](https://media.githubusercontent.com/media/JX05120LLL/IHMS/refs/heads/main/IHMS-admin/src/main/resources/pic/%E5%90%8E%E5%8F%B0%E5%B1%95%E7%A4%BA1.png?token=BDEY44Q5AQPLZ6P7PK2TMZ3IGVYI6)



## 系统架构

系统由三部分组成：
- **IHMS-view**: 前端用户界面，基于Vue.js开发
- **IHMS-admin**: 后台管理系统，基于Java Spring Boot开发
- **数据库**: MySQL数据库，存储用户健康数据和系统配置

## 功能特点

### 用户端功能
- 用户健康数据记录与存储（身高、体重、血压等基础体征数据）
- 健康数据可视化展示
- 基础健康知识查询（集成DeepSeek API）
- 健康提醒功能（服药、复诊等日常健康事项提醒）

### 管理端功能
- 用户管理
- 健康数据统计与分析
- 系统配置管理

## 技术栈

### 前端 (IHMS-view)
- 框架：Vue.js 2.x + Element UI
- 图表：ECharts 4.x
- HTTP请求：Axios
- 路由管理：Vue Router
- 加密工具：CryptoJS、js-md5
- 编辑器：WangEditor

### 后端 (IHMS-admin)
- 框架：Spring Boot
- 持久层：MyBatis
- 数据库：MySQL
- API文档：Swagger
- 权限控制：Spring Security

## 安装与运行

### 环境要求
- JDK 1.8+
- Maven 3.6+
- Node.js (推荐使用v16.x版本以获得最佳兼容性)
- npm 或 yarn
- MySQL 5.7+

### 数据库配置
1. 创建数据库
```sql
CREATE DATABASE personal_health;
```

2. 导入数据库脚本
```bash
mysql -u your_username -p personal_health < sql/personal_health.sql
```

### 后端服务 (IHMS-admin)
1. 进入后端目录
```bash
cd IHMS/IHMS-admin
```

2. 修改数据库配置
编辑 `src/main/resources/application.properties` 或 `application.yml` 文件，配置数据库连接信息

3. 编译打包
```bash
mvn clean package
```

4. 运行服务
```bash
java -jar target/IHMS-admin-*.jar
```

### 前端服务 (IHMS-view)
1. 进入前端目录
```bash
cd IHMS/IHMS-view
```

2. 安装依赖
```bash
npm install
```

3. 运行开发服务器
```bash
# 对于较新版本的Node.js (v17+)
npm run dev:compatible

# 对于较旧版本的Node.js
npm run dev
```

4. 构建生产版本
```bash
# 对于较新版本的Node.js (v17+)
npm run build:compatible

# 对于较旧版本的Node.js
npm run build
```

## 项目结构

```
IHMS/
├── IHMS-admin/           # 后端管理系统
│   ├── src/              # 源代码
│   │   ├── main/         # 主要代码
│   │   │   ├── java/     # Java代码
│   │   │   └── resources/ # 配置文件
│   │   └── test/         # 测试代码
│   ├── pom.xml           # Maven配置
│   └── target/           # 编译输出目录
├── IHMS-view/            # 前端Vue项目
│   ├── public/           # 静态资源
│   ├── src/              # 源代码
│   │   ├── assets/       # 资源文件
│   │   ├── components/   # 组件
│   │   ├── router/       # 路由配置
│   │   ├── views/        # 页面
│   │   ├── App.vue       # 根组件
│   │   └── main.js       # 入口文件
│   ├── package.json      # 依赖配置
│   └── vue.config.js     # Vue配置
├── sql/                  # 数据库脚本
│   └── personal_health.sql # 数据库初始化脚本
└── README.md             # 项目说明
```

## 使用说明

### 用户端
1. 注册/登录系统
2. 进入个人健康数据管理界面
3. 添加或更新健康数据
4. 查看健康数据统计和趋势图表
5. 设置健康提醒
6. 使用健康知识查询功能获取相关信息

### 管理端
1. 使用管理员账号登录后台
2. 管理用户信息和健康数据
3. 查看系统运行状态和数据统计
4. 管理健康知识库

## 注意事项

- **本系统仅作为个人健康管理的辅助工具，仍有诸多不完善的地方，仅作为参考学习使用**

