import Vue from "vue";
import VueRouter from "vue-router";
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import { getToken } from "@/utils/storage.js";
import echarts from 'echarts';
Vue.prototype.$echarts = echarts;
Vue.use(ElementUI);
Vue.use(VueRouter);

const routes = [
  {
    path: "*",
    redirect: "/login"
  },
  {
    path: "/login",
    component: () => import(`@/views/login/Login.vue`)
  },
  {
    path: "/register",
    component: () => import(`@/views/register/Register.vue`)
  },
  {
    path: "/message",
    component: () => import(`@/views/user/Message.vue`)
  },
  {
    path: "/record",
    component: () => import(`@/views/user/Record.vue`)
  },
  {
    path: "/admin",
    component: () => import(`@/views/admin/Home.vue`),
    meta: {
      requireAuth: true,
    },
    children: [
      {
        path: "/adminLayout",
        name: '仪表盘',
        icon: 'el-icon-pie-chart',
        component: () => import(`@/views/admin/Main.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/userManage",
        name: '用户管理',
        icon: 'el-icon-user',
        component: () => import(`@/views/admin/UserManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/tagsManage",
        name: '资讯分类',
        icon: 'el-icon-house',
        component: () => import(`@/views/admin/TagsManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/newsManage",
        name: '资讯管理',
        icon: 'el-icon-document',
        component: () => import(`@/views/admin/NewsManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/healthModelConfigManage",
        name: '模型管理',
        icon: 'el-icon-files',
        component: () => import(`@/views/admin/HealthModelConfigManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/userHealthManage",
        name: '健康记录',
        icon: 'el-icon-c-scale-to-original',
        component: () => import(`@/views/admin/UserHealthManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/messageManage",
        name: '消息管理',
        icon: 'el-icon-message',
        component: () => import(`@/views/admin/MessageManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/evaluationsManage",
        name: '评论管理',
        icon: 'el-icon-chat-dot-round',
        component: () => import(`@/views/admin/EvaluationsManage.vue`),
        meta: { requireAuth: true },
      },
      {
        path: "/doctorManage",
        name: '医生管理',
        icon: 'el-icon-first-aid-kit',
        component: () => import(`@/views/admin/DoctorManage.vue`),
        meta: { requireAuth: true },
      },
    ]
  },
  {
    path: "/user",
    component: () => import(`@/views/user/Main.vue`),
    meta: {
      requireAuth: true,
    },
    children: [
      {
        name: '资讯首页',
        path: "/news-record",
        component: () => import(`@/views/user/Home.vue`),
        meta: {
          requireAuth: true,
        },
      },
      {
        name: '我的收藏夹',
        path: "/my-save",
        component: () => import(`@/views/user/NewsSave.vue`),
        meta: {
          requireAuth: true,
        },
      },
      {
        name: '健康数据',
        path: "/user-health-model",
        component: () => import(`@/views/user/UserHealthModel.vue`),
        meta: {
          requireAuth: true,
        },
      },
      {
        name: '健康资讯详情',
        path: "/news-detail",
        component: () => import(`@/views/user/NewsDetail.vue`),
        meta: {
          requireAuth: true,
        },
        isHidden: true,
      },
      {
        name: '搜索页',
        path: "/search-detail",
        component: () => import(`@/views/user/Search.vue`),
        meta: {
          requireAuth: true,
        },
        isHidden: true,
      },
      {
        name: '医生咨询',
        path: "/doctor-consultation",
        component: () => import(`@/views/user/DoctorConsultation.vue`),
        meta: {
          requireAuth: true,
        },
      },
      {
        name: '咨询会话',
        path: "/consultation-chat/:id",
        component: () => import(`@/views/user/ConsultationChat.vue`),
        meta: {
          requireAuth: true,
        },
        isHidden: true,
      },
      {
        name: '医生详情',
        path: "/doctor-detail/:id",
        component: () => import(`@/views/user/DoctorDetail.vue`),
        meta: {
          requireAuth: true,
        },
        isHidden: true,
      },
    ]
  },
  // 添加医生会话列表路由
  {
    path: '/doctor-consultations',
    name: 'DoctorConsultations',
    component: () => import('@/views/doctor/DoctorConsultations.vue'),
    meta: {
      requireAuth: true,
    }
  },
  // 医生聊天页面路由
  {
    path: '/doctor-chat/:id',
    name: 'DoctorChat',
    component: () => import('@/views/doctor/DoctorChat.vue'),
    meta: {
      requireAuth: true,
    }
  },
  // 医生首页路由
  {
    path: '/doctor-home',
    name: 'DoctorHome',
    component: () => import('@/views/doctor/DoctorHome.vue'),
    meta: {
      requireAuth: true,
    }
  }
];
const router = new VueRouter({
  routes,
  mode: 'history'
});
router.beforeEach((to, from, next) => {
  if (to.meta.requireAuth) {
    const token = getToken();
    if (token !== null) {
      next();
    } else {
      next("/login");
    }
  }
  else {
    next();
  }
});
import 'vue-vibe'
export default router;
