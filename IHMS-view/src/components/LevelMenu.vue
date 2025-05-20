<template>
    <div class="nav">
        <Logo sysName="健康有道" />
        <div v-if="!item.isHidden" :style="{
            color: selectedIndex === index ? '#1c1c1c' : 'rgb(102 102 102)'
        }" class="menu-item" v-for="(item, index) in menus" :key="index" @click="menuClick(`${item.path}`, index)">
            <span>
                <i :class="item.icon"></i>
                <span>&nbsp; {{ item.name }}</span>
            </span>
        </div>
        <div class="search">
            <input placeholder="搜索资讯" @keyup.enter="search" v-model="filterText" />
            <span @click="search">立即搜索</span>
        </div>
        <div class="record">
            <span @click="healthDataRecord" style="margin:14px 10px;">
                <i class="el-icon-connection"></i>
                健康记录
            </span>
        </div>
        <!-- 只有管理员用户才显示这个医生咨询入口，避免与左侧菜单重复 -->
        <div class="doctor-consultation" v-if="userInfo.role === 1">
            <span @click="doctorConsultation" style="margin:14px 10px;">
                <i class="el-icon-first-aid-kit"></i>
                医生咨询
            </span>
        </div>
        <div class="messsage">
            <el-badge v-if="noReadMsg !== 0" :value="noReadMsg">
                <span class="message-span" @click="messageCenter">
                    <i class="el-icon-chat-dot-round"></i>
                </span>
            </el-badge>
            <span v-else class="message-span" @click="messageCenter">
                <i class="el-icon-chat-dot-round"></i>
            </span>
        </div>
        <div>
            <el-dropdown class="user-dropdown">
                <span class="el-dropdown-link" style="display: flex; align-items: center;">
                    <el-avatar :size="25" :src="userInfo.url" style="margin-top: 0;"></el-avatar>
                    <span class="userName"
                        style="margin-left: 5px;font-size: 14px;width: 100px;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">{{
                            userInfo.name }}</span>
                    <i class="el-icon-arrow-down el-icon--right" style="margin-left: 5px;"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item icon="el-icon-user" @click.native="userCenterPanel">个人中心</el-dropdown-item>
                    <el-dropdown-item icon="el-icon-warning-outline" @click.native="resetPwd">修改密码</el-dropdown-item>
                    <el-dropdown-item icon="el-icon-back" @click.native="loginOut">退出登录</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</template>
<script>
import { clearToken } from "@/utils/storage.js";
import Logo from '@/components/Logo.vue';
export default {
    name: "UserMenu",
    components: {
        Logo
    },
    data() {
        return {
            selectedIndex: 0,
            messagePath: '/message',
            loginPath: '/login',
            sysName: '健康有道',
            defaultPath: '/news-record',
            filterText: '',
            noReadMsg: 0,
        }
    },
    props: {
        // 路由菜单数据
        menus: {
            type: Array,
            required: true
        },
        // 用户信息数据
        userInfo: {
            type: Object,
            required: true
        }
    },
    mounted() {
        this.pathToDo(this.defaultPath);
        this.loadMsgCount();
    },
    methods: {
        // 搜索关键词，返回父组件处理
        search() {
            // 如果当前是搜索页面了，更新关键词即可
            if (this.$route.path === '/search-detail') {
                sessionStorage.setItem('keyWord', this.filterText);
                return;
            }
            // 将关键词存起来
            sessionStorage.setItem('keyWord', this.filterText);
            this.$emit('eventListener', 'search-detail');
        },
        // 个人中心，传回父组件处理
        userCenterPanel() {
            this.$emit('eventListener', 'center');
        },
        // 重置密码，传回父组件处理
        resetPwd() {
            this.$emit('eventListener', 'resetPwd');
        },
        // 退出登录，传回父组件处理
        loginOut() {
            this.$emit('eventListener', 'loginOut');
        },
        // 记录饮食，传回父组件处理
        dietRecord() {
            this.$emit('eventListener', 'dietRecord');
        },
        // 记录个人健康指标，传回父组件处理
        healthDataRecord() {
            this.$emit('eventListener', 'healthDataRecord');
        },
        // 医生咨询，传回父组件处理
        doctorConsultation() {
            this.$emit('eventListener', 'doctorConsultation');
        },
        // 退出登录，传回父组件处理
        loginOut() {
            this.$emit('eventListener', 'loginOut');
        },
        async loadMsgCount() {
            const userInfo = sessionStorage.getItem('userInfo');
            const userInfoEntity = JSON.parse(userInfo);
            const messageQueryDto = { userId: userInfoEntity.id, isRead: false }
            const response = await this.$axios.post(`/message/query`, messageQueryDto);
            const { data } = response;
            if (data.code === 200) {
                this.noReadMsg = data.data.length;
            }
        },
        // 不是存量路由，则跳转
        pathToDo(path) {
            if (this.$route.path !== path) {
                this.$router.push(path);
            }
        },
        //路由跳转
        menuClick(path, index) {
            this.selectedIndex = index;
            this.pathToDo(path);
        },
        // 消息中心
        messageCenter() {
            this.selectedIndex = null;
            this.pathToDo(this.messagePath);
        },
        // 退出登录
        async out() {
            const confirmed = await this.$swalConfirm({
                title: '是否退出登录',
                text: `退出后将重新登录，才能使用系统功能`,
                icon: 'warning',
            });
            if (confirmed) {
                this.$swal.fire({
                    title: '退出登录',
                    text: '您已成功退出登录。',
                    icon: 'success', // 使用'success'图标表示操作成功
                    showConfirmButton: false, // 隐藏确认按钮，使得弹窗只展示信息后自动关闭
                    timer: 1300, // 自动关闭弹窗的延迟时间，这里是2秒
                });
                setTimeout(() => {
                    clearToken();
                    this.$router.push('/loginPath');
                }, 1300)
            } else {
                console.log('用户取消了退出操作');
            }
        },
    }
}
</script>
<style scoped lang="scss">
.nav {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 20px;
    height: 80px;

    .menu-item {
        font-size: 14px;
        cursor: pointer;
    }

    .search {
        border-radius: 15px;
        font-size: 14px;
        display: flex;
        justify-content: space-between;
        align-items: center;

        input {
            outline: none;
            border: none;
            border-left: 1px solid rgb(51, 51, 51);
            border-top: 1px solid rgb(51, 51, 51);
            border-bottom: 1px solid rgb(51, 51, 51);
            padding: 6px 10px;
            font-size: 12px;
        }

        input:focus {
            color: rgb(31, 31, 31);
        }

        span:hover {
            background-color: rgb(31, 31, 31);
        }

        span {
            background-color: rgb(51, 51, 51);
            display: inline-block;
            cursor: pointer;
            font-size: 12px;
            color: rgb(245, 245, 245);
            padding: 6px 10px;
            border-top-right-radius: 15px;
            border-bottom-right-radius: 15px;
        }
    }

    .record:hover {
        background-color: rgb(40, 150, 103);
    }

    .doctor-consultation:hover {
        background-color: rgb(21, 85, 154);
    }

    .doctor-consultation {
        font-size: 14px;
        cursor: pointer;
        background-color: rgb(30, 111, 201);
        color: rgb(255, 255, 255);
        padding: 4px 10px;
        border-radius: 15px;
    }

    .messsage:hover{
        background-color: rgb(40, 150, 103);
    }

    .messsage{
        background-color: rgb(56, 183, 129);
        cursor: pointer;
        padding: 2px 14px;
        border-radius: 10px;
        color: rgb(255,255,255);
    }

    .record {
        font-size: 14px;
        cursor: pointer;
        background-color: rgb(56, 183, 129);
        color: rgb(255, 255, 255);
        padding: 4px 10px;
        border-radius: 15px;
    }
}
</style>