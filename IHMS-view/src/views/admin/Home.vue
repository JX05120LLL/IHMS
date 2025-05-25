<template>
    <div>
    <div class="menu-container">
        <div class="menu-side" :class="{ 'menu-side-narrow': flag }">
            <div style="display: flex;align-items: center;">
                <Logo style="padding: 0 40px;margin: 10px 0;" sysName="健康有道" :flag="flag" :bag="colorLogo" />
            </div>
            <div style="margin-top: 12px;">
                <AdminMenu :flag="flag" :routes="adminRoutes" :bag="bagMenu" @select="handleRouteSelect" />
            </div>
        </div>
        <div class="main">
            <div class="header-section">
                <LevelHeader @eventListener="eventListener" @selectOperation="selectOperation" :tag="tag"
                    :userInfo="userInfo" />
            </div>
            <div class="content-section">
                <router-view></router-view>
            </div>
        </div>
        <!-- 个人中心 -->
        <el-dialog :show-close="false" :visible.sync="dialogOperaion" width="26%">
            <div slot="title" style="padding: 25px 0 0 20px;">
                <span style="font-size: 18px;font-weight: 800;">个人中心</span>
            </div>
            <el-row style="padding: 10px 20px 20px 20px;">
                <el-row>
                    <p style="font-size: 12px;padding: 3px 0;margin-bottom: 10px;">
                        <span class="modelName">*头像</span>
                    </p>
                    <el-upload class="avatar-uploader" action="/api/personal-heath/v1.0/file/upload"
                        :show-file-list="false" :on-success="handleAvatarSuccess">
                        <img v-if="userInfo.url" :src="userInfo.url" style="width: 80px;height: 80px;">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-row>
                <el-row>
                    <p style="font-size: 12px;padding: 3px 0;">
                        <span class="modelName">*用户名</span>
                    </p>
                    <input class="input-title" v-model="userInfo.name" placeholder="用户名">
                </el-row>
                <el-row>
                    <p style="font-size: 12px;padding: 3px 0;">
                        <span class="modelName">*用户邮箱</span>
                    </p>
                    <input class="input-title" v-model="userInfo.email" placeholder="用户邮箱">
                </el-row>
            </el-row>
            <span slot="footer" class="dialog-footer">
                <el-button class="customer" size="small" style="background-color: rgb(241, 241, 241);border: none;"
                    @click="dialogOperaion = false">取 消</el-button>
                <el-button size="small" style="background-color: #15559a;border: none;" class="customer" type="info"
                    @click="updateUserInfo">修改</el-button>
            </span>
            </el-dialog>
        </div>
        <!-- 使用更明显的样式和内联样式确保可见性 -->
        <div class="medical-assistant-button" style="position: fixed; bottom: 30px; right: 30px; width: 80px; height: 80px; border-radius: 50%; background-color: #f44336; color: white; display: flex; align-items: center; justify-content: center; cursor: pointer; box-shadow: 0 4px 8px rgba(0,0,0,0.3); z-index: 99999; font-size: 40px;" @click="showMedicalDialog">
            <i class="el-icon-service"></i>
        </div>
        <el-dialog title="医疗咨询小助手" :visible.sync="medicalDialogVisible" width="50%">
            <div class="medical-consultation">
                <div class="chat-container" v-loading="loading">
                    <div class="chat-messages" ref="messagesContainer">
                        <template v-if="messages.length > 0">
                            <div 
                                v-for="(message, index) in messages" 
                                :key="index" 
                                :class="['message-item', { 'self': message.senderId === currentUserId }]"
                            >
                                <div class="avatar">
                                    <el-avatar 
                                        :size="40" 
                                        :src="message.senderId === currentUserId ? userInfo.url : aiAvatar"
                                    ></el-avatar>
                                </div>
                                <div class="message-content">
                                    <div class="sender-name">{{ message.senderId === currentUserId ? '我' : 'AI助手' }}</div>
                                    <div class="text-content">{{ message.content }}</div>
                                    <div class="message-time">{{ formatDate(message.createTime) }}</div>
                                </div>
                            </div>
                        </template>
                        <div v-else class="empty-messages">
                            <el-empty description="暂无消息记录"></el-empty>
                        </div>
                    </div>

                    <div class="chat-input">
                        <div class="input-box">
                            <el-input
                                v-model="inputMessage"
                                placeholder="请输入您的健康问题或症状..."
                                type="textarea"
                                :rows="3"
                                @keyup.enter.native.ctrl="sendMessage"
                            ></el-input>
                        </div>
                        <div class="send-button">
                            <el-button type="primary" @click="sendMessage" :disabled="!inputMessage.trim()">发送</el-button>
                        </div>
                    </div>
                </div>
            </div>
        </el-dialog>
    </div>
</template>
<script>
import request from "@/utils/request.js";
import router from "@/router/index";
import { clearToken } from "@/utils/storage"
import AdminMenu from '@/components/VerticalMenu.vue';
import Logo from '@/components/Logo.vue';
import LevelHeader from '@/components/LevelHeader.vue';
export default {
    name: "Admin",
    components: {
        Logo,
        LevelHeader,
        AdminMenu
    },
    data() {
        return {
            adminRoutes: [],
            activeIndex: '',
            userInfo: {
                id: null,
                url: '',
                name: '',
                role: null,
                email: ''
            },
            flag: false,
            tag: '可视化',
            bag: 'rgb(246,246,246)',
            colorLogo: 'rgb(51,51,51)',
            bagMenu: 'rgb(248,248,248)',
            dialogOperaion: false,
            // 医疗咨询小助手
            medicalDialogVisible: false,
            messages: [],
            loading: false,
            inputMessage: '',
            currentUserId: 'user1', // 模拟用户ID
            aiAvatar: '/img/ai-avatar.png'
        };
    },
    created() {
        let menus = router.options.routes.filter(route => route.path == '/admin')[0];
        this.adminRoutes = menus.children;
        this.tokenCheckLoad();
        this.menuOperationHistory();
    },
    mounted() {
        // 确保按钮在DOM加载后立即显示
        this.$nextTick(() => {
            console.log("医疗助手按钮已加载");
        });
    },
    methods: {
        async updateUserInfo() {
            try {
                const userUpdateDTO = {
                    userAvatar: this.userInfo.url,
                    userName: this.userInfo.name,
                    userEmail: this.userInfo.email
                }
                const resposne = await this.$axios.put(`/user/update`, userUpdateDTO);
                const { data } = resposne;
                if (data.code === 200) {
                    this.dialogOperaion = false;
                    this.tokenCheckLoad();
                    this.$swal.fire({
                        title: '修改个人信息',
                        text: data.msg,
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1000,
                    });
                }
            } catch (e) {
                this.dialogOperaion = false;
                this.$swal.fire({
                    title: '修改个人信息异常',
                    text: e,
                    icon: 'error',
                    showConfirmButton: false,
                    timer: 2000,
                });
                console.error(`修改个人信息异常:${e}`);
            }
        },
        handleAvatarSuccess(res, file) {
            if (res.code !== 200) {
                this.$message.error(`头像上传异常`);
                return;
            }
            this.$message.success(`头像上传成功`);
            this.userInfo.url = res.data;
        },
        eventListener(event) {
            // 个人中心
            if (event === 'center') {
                this.dialogOperaion = true;
            }
            // 退出登录
            if (event === 'loginOut') {
                this.loginOut();
            }
        },
        async loginOut() {
            const confirmed = await this.$swalConfirm({
                title: '退出登录？',
                text: `推出后需重新登录？`,
                icon: 'warning',
            });
            if (confirmed) {
                this.$swal.fire({
                    title: '退出登录成功',
                    text: '1s 后返回登录页面',
                    icon: 'success',
                    showConfirmButton: false,
                    timer: 1000,
                });
                setTimeout(() => {
                    clearToken();
                    this.$router.push("/login");
                }, 1000)
            }
        },
        menuOperationHistory() {
            this.flag = sessionStorage.getItem('flag') === 'true';
        },
        selectOperation(flag) {
            this.flag = flag;
        },
        handleRouteSelect(index) {
            let ary = this.adminRoutes.filter(entity => entity.path == index);
            this.tag = ary[0].name;
            if (this.$router.currentRoute.fullPath == index) {
                return;
            }
            this.$router.push(index);
        },
        // Token检验
        async tokenCheckLoad() {
            try {
                const res = await request.get('user/auth');
                // 错误处理
                if (res.data.code === 400) {
                    this.$message.error(res.data.msg);
                    this.$router.push('/login');
                    return;
                }
                // 用户信息赋值
                const { id, userAvatar: url, userName: name, userRole: role, userEmail: email } = res.data.data;
                this.userInfo = { id, url, name, role, email };
                // 根据角色解析路由
                const rolePath = role === 1 ? '/admin' : '/user';
                const targetMenu = router.options.routes.find(route => route.path === rolePath);
                if (targetMenu) {
                    this.routers = targetMenu.children;
                } else {
                    console.warn(`未找到与角色对应的路由：${rolePath}`);
                }
            } catch (error) {
                console.error('获取用户认证信息时发生错误:', error);
                this.$message.error('认证信息加载失败,请重试！');
            }
        },
        // 医疗咨询小助手
        showMedicalDialog() {
            this.medicalDialogVisible = true
        },
        async sendMessage() {
            if (!this.inputMessage.trim()) return
          
            const userMessage = {
                senderId: this.currentUserId,
                content: this.inputMessage,
                createTime: new Date().toISOString()
            }
            this.messages.push(userMessage)
            this.inputMessage = ''
          
            // 调用DeepSeek API
            try {
                const prompt = `你是一名专业的医疗健康咨询助手。请根据用户提供的症状、健康信息，结合权威医学知识，给出初步的健康建议和可能的疾病方向。请注意：\n1. 你的回答要简明、专业、易懂。\n2. 不要做出明确诊断或处方，建议用户如有严重不适及时就医。\n3. 如果用户描述不清楚，请引导用户补充更多信息（如症状持续时间、伴随症状、既往病史等）。\n4. 回答时可结合常见疾病的症状表现，帮助用户初步判断健康状况。\n\n用户输入：${userMessage.content}`
              
                const response = await this.$axios.post('/api/deepseek/chat', { prompt })
                const aiMessage = {
                    senderId: 'ai',
                    content: response.data.answer,
                    createTime: new Date().toISOString()
                }
                this.messages.push(aiMessage)
            } catch (error) {
                console.error('调用AI接口失败:', error)
                this.$message.error('AI助手暂时无法回答，请稍后再试')
            }
        },
        formatDate(date) {
            return new Date(date).toLocaleString()
        }
    }
};
</script>
<style lang="scss">
.menu-container {
    display: flex;
    height: 100vh;
    width: 100%;


    .menu-side {
        width: 253px;
        min-width: 115px;
        height: 100vh;
        padding-top: 10px;
        box-sizing: border-box;
        transition: width 0.3s ease;
        background-color: rgb(248,248,248);
        border-right: 1px solid rgb(240, 240, 240);
    }

    .menu-side-narrow {
        width: 115px;
    }

    .main {
        flex-grow: 1;
        overflow-x: hidden;

        .header-section {
            max-width: 100%;
            padding: 0 15px;
        }

        .content-section {
            flex-grow: 1;
            padding: 5px;
            overflow-y: auto;
        }
    }



}

.floating-button {
    position: fixed;
    bottom: 20px;
    right: 20px;
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background-color: #409EFF;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    z-index: 9999;
}

.floating-button i {
    font-size: 24px;
}

.medical-consultation {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.chat-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
    overflow: hidden;
}

.chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
    background-color: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.message-item {
    display: flex;
    margin-bottom: 20px;
}

.message-item.self {
    flex-direction: row-reverse;
}

.avatar {
    margin: 0 10px;
}

.message-content {
    max-width: 70%;
    padding: 10px;
    background-color: #e6f7ff;
    border-radius: 4px;
}

.message-item.self .message-content {
    background-color: #f0f9eb;
}

.sender-name {
    font-weight: bold;
    margin-bottom: 5px;
}

.text-content {
    word-break: break-word;
}

.message-time {
    font-size: 12px;
    color: #999;
    margin-top: 5px;
}

.chat-input {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
}

.input-box {
    margin-bottom: 10px;
}

.send-button {
    align-self: flex-end;
}

/* 全局确保医疗助手按钮样式 */
.medical-assistant-button {
    position: fixed !important;
    bottom: 30px !important;
    right: 30px !important;
    width: 80px !important;
    height: 80px !important;
    border-radius: 50% !important;
    background-color: #f44336 !important;
    color: white !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    cursor: pointer !important;
    box-shadow: 0 4px 8px rgba(0,0,0,0.3) !important;
    z-index: 99999 !important;
    font-size: 40px !important;
}
</style>