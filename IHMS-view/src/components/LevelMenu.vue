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

        <!-- 医疗咨询小助手悬浮按钮 -->
        <div class="medical-assistant-button" @click="toggleChatWindow">
            <i class="el-icon-service"></i>
        </div>

        <!-- 可拖拽、可缩放的聊天窗口（替代原来的el-dialog） -->
        <div 
            v-if="chatWindowVisible"
            class="chat-window-container"
            :style="{ 
                width: chatWindowWidth + 'px',
                height: chatWindowHeight + 'px',
                top: chatWindowTop + 'px',
                left: chatWindowLeft + 'px'
            }"
            @mousedown.stop="startDrag"
        >
            <!-- 窗口标题栏 -->
            <div class="chat-window-header">
                <div class="chat-window-title">医疗咨询小助手</div>
                <div class="chat-window-controls">
                    <i class="el-icon-minus" @click.stop="minimizeChatWindow"></i>
                    <i class="el-icon-close" @click.stop="closeChatWindow"></i>
                </div>
            </div>
            
            <!-- 聊天内容区域 -->
            <div class="chat-window-body">
                <div class="chat-container" v-loading="loading">
                    <div class="chat-messages" ref="messagesContainer">
                        <template v-if="messages.length > 0">
                            <div 
                                v-for="(message, index) in messages" 
                                :key="'msg_' + index + '_' + Date.now()" 
                                :class="['message-item', { 'self': message.senderId === userInfo.id }]"
                            >
                                <div class="avatar">
                                    <el-avatar 
                                        :size="36" 
                                        :src="message.senderId === userInfo.id ? userInfo.url : aiAvatar"
                                    ></el-avatar>
                                </div>
                                <div class="message-content">
                                    <div class="sender-name">{{ message.senderId === userInfo.id ? '我' : 'AI助手' }}</div>
                                    <div :class="['text-content', { 'loading-text': message.isLoading }]">
                                        {{ message.content }}
                                        <span v-if="message.isLoading" class="loading-dots">...</span>
                                    </div>
                                    <div v-if="message.healthData" class="health-data-preview">
                                        <div class="health-data-title">
                                            <i class="el-icon-data-analysis"></i> 健康数据
                                        </div>
                                        <div class="health-data-item">
                                            <span>{{ message.healthData.name }}: {{ message.healthData.value }} {{ message.healthData.unit }}</span>
                                        </div>
                                    </div>
                                    <div class="message-time">{{ formatDate(message.createTime) }}</div>
                                </div>
                            </div>
                        </template>
                        <div v-else class="empty-messages">
                            <el-empty description="您好，我是AI医疗助手，可以回答您的健康问题。" :image-size="80"></el-empty>
                        </div>
                    </div>

                    <div class="chat-input">
                        <div class="input-actions">
                            <el-tooltip content="分享健康数据" placement="top">
                                <el-button type="success" icon="el-icon-data-analysis" circle size="small" @click="showHealthDataDialog"></el-button>
                            </el-tooltip>
                        </div>
                        <div class="input-box">
                            <el-input
                                v-model="inputMessage"
                                placeholder="请输入您的健康问题或症状..."
                                type="textarea"
                                :rows="2"
                                @keyup.enter.native.ctrl="sendMessage"
                            ></el-input>
                        </div>
                        <div class="send-button">
                            <el-button type="success" @click="sendMessage" :disabled="!inputMessage.trim()">发送</el-button>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- 缩放手柄 -->
            <div class="resize-handle" @mousedown.stop="startResize"></div>
        </div>

        <!-- 健康数据选择对话框 -->
        <el-dialog title="选择要分享的健康数据" :visible.sync="healthDataDialogVisible" width="30%" append-to-body>
            <div v-loading="healthDataLoading">
                <template v-if="healthDataList.length > 0">
                    <el-table :data="healthDataList" style="width: 100%" @row-click="selectHealthData" highlight-current-row>
                        <el-table-column label="健康指标" prop="name"></el-table-column>
                        <el-table-column label="数值">
                            <template slot-scope="scope">
                                {{ scope.row.value }} {{ scope.row.unit }}
                            </template>
                        </el-table-column>
                        <el-table-column label="记录时间" prop="createTime" :formatter="formatTableDate"></el-table-column>
                    </el-table>
                </template>
                <el-empty v-else description="暂无健康数据记录"></el-empty>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="healthDataDialogVisible = false">取 消</el-button>
                <el-button type="success" @click="shareHealthData" :disabled="!selectedHealthData">分享数据</el-button>
            </span>
        </el-dialog>
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
            // 医疗咨询小助手
            medicalDialogVisible: false,
            messages: [],
            loading: false,
            aiLoading: false,  // 添加AI回复加载状态
            inputMessage: '',
            aiAvatar: '/img/ai-avatar.png',
            healthDataDialogVisible: false,
            healthDataList: [],
            healthDataLoading: false,
            selectedHealthData: null,
            // 聊天窗口属性
            chatWindowVisible: false,
            chatWindowWidth: 400,
            chatWindowHeight: 500,
            chatWindowTop: 100,
            chatWindowLeft: window.innerWidth - 450,
            isDragging: false,
            isResizing: false,
            dragStartX: 0,
            dragStartY: 0,
            resizeStartWidth: 0,
            resizeStartHeight: 0
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
        
        // 添加全局事件监听
        document.addEventListener('mousemove', this.onMouseMove);
        document.addEventListener('mouseup', this.onMouseUp);
    },
    beforeDestroy() {
        // 移除全局事件监听
        document.removeEventListener('mousemove', this.onMouseMove);
        document.removeEventListener('mouseup', this.onMouseUp);
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
        // 窗口相关方法
        toggleChatWindow() {
            this.chatWindowVisible = !this.chatWindowVisible;
        },
        closeChatWindow() {
            this.chatWindowVisible = false;
        },
        minimizeChatWindow() {
            // 实现最小化功能
            this.chatWindowVisible = false;
        },
        startDrag(event) {
            // 如果点击的是控制按钮或缩放手柄，不进行拖拽
            if (event.target.classList.contains('el-icon-minus') || 
                event.target.classList.contains('el-icon-close') ||
                event.target.classList.contains('resize-handle')) {
                return;
            }
            
            this.isDragging = true;
            this.dragStartX = event.clientX - this.chatWindowLeft;
            this.dragStartY = event.clientY - this.chatWindowTop;
        },
        startResize(event) {
            this.isResizing = true;
            this.resizeStartWidth = this.chatWindowWidth;
            this.resizeStartHeight = this.chatWindowHeight;
            this.dragStartX = event.clientX;
            this.dragStartY = event.clientY;
        },
        onMouseMove(event) {
            if (this.isDragging) {
                const newLeft = event.clientX - this.dragStartX;
                const newTop = event.clientY - this.dragStartY;
                
                // 限制窗口不要超出屏幕
                this.chatWindowLeft = Math.max(0, Math.min(window.innerWidth - this.chatWindowWidth, newLeft));
                this.chatWindowTop = Math.max(0, Math.min(window.innerHeight - this.chatWindowHeight, newTop));
            } else if (this.isResizing) {
                const widthDiff = event.clientX - this.dragStartX;
                const heightDiff = event.clientY - this.dragStartY;
                
                // 设置最小尺寸和最大尺寸
                this.chatWindowWidth = Math.max(300, Math.min(800, this.resizeStartWidth + widthDiff));
                this.chatWindowHeight = Math.max(400, Math.min(800, this.resizeStartHeight + heightDiff));
            }
        },
        onMouseUp() {
            this.isDragging = false;
            this.isResizing = false;
        },
        // 聊天相关方法
        showMedicalDialog() {
            this.chatWindowVisible = true;
        },
        async sendMessage() {
            if (!this.inputMessage.trim()) return;
            
            const userMessage = {
                senderId: this.userInfo.id,
                content: this.inputMessage,
                createTime: new Date().toISOString()
            };
            this.messages.push(userMessage);
            this.inputMessage = '';
            
            // 显示AI正在输入的提示
            this.aiLoading = true;
            
            // 添加临时消息作为loading指示
            const tempMessageIndex = this.messages.length;
            this.messages.push({
                senderId: 'ai',
                content: '正在思考中...',
                isLoading: true,
                createTime: new Date().toISOString()
            });
            
            // 调用DeepSeek API
            try {
                const prompt = `你是一名专业的医疗健康咨询助手。请根据用户提供的症状、健康信息，结合权威医学知识，给出初步的健康建议和可能的疾病方向。请注意：\n1. 你的回答要简明、专业、易懂。\n2. 不要做出明确诊断或处方，建议用户如有严重不适及时就医。\n3. 如果用户描述不清楚，请引导用户补充更多信息（如症状持续时间、伴随症状、既往病史等）。\n4. 回答时可结合常见疾病的症状表现，帮助用户初步判断健康状况。\n\n用户输入：${userMessage.content}`;
                
                const response = await this.$axios.post('/api/deepseek/chat', { userPrompt: userMessage.content });
                console.log('API回复:', response.data);
                
                // 检查响应格式并处理
                let content = '';
                if (response.data && response.data.content) {
                    content = response.data.content;
                } else if (response.data && response.data.success === false) {
                    throw new Error(response.data.errorMessage || '调用失败');
                } else {
                    console.error('意外的响应格式:', response.data);
                    throw new Error('无法解析AI回复');
                }
                
                // 替换临时消息
                this.messages.splice(tempMessageIndex, 1, {
                    senderId: 'ai',
                    content: content,
                    createTime: new Date().toISOString()
                });
            } catch (error) {
                console.error('调用AI接口失败:', error);
                // 移除临时消息
                this.messages.splice(tempMessageIndex, 1);
                this.$message.error('AI助手暂时无法回答，请稍后再试');
            } finally {
                this.aiLoading = false;
            }
        },
        formatDate(date) {
            return new Date(date).toLocaleString();
        },
        async showHealthDataDialog() {
            this.healthDataDialogVisible = true;
            this.healthDataLoading = true;
            try {
                const response = await this.$axios.get('/healthData/list');
                const { data } = response;
                if (data.code === 200) {
                    this.healthDataList = data.data;
                }
            } catch (error) {
                console.error('获取健康数据失败:', error);
                this.$message.error('获取健康数据失败，请稍后再试');
            } finally {
                this.healthDataLoading = false;
            }
        },
        selectHealthData(row) {
            this.selectedHealthData = row;
        },
        async shareHealthData() {
            if (!this.selectedHealthData) return;
            
            const healthDataMessage = {
                senderId: this.userInfo.id,
                content: `分享了健康数据: ${this.selectedHealthData.name}`,
                createTime: new Date().toISOString(),
                healthData: this.selectedHealthData
            };
            
            this.messages.push(healthDataMessage);
            this.healthDataDialogVisible = false;
            
            // 显示AI正在输入的提示
            this.aiLoading = true;
            
            // 添加临时消息作为loading指示
            const tempMessageIndex = this.messages.length;
            this.messages.push({
                senderId: 'ai',
                content: '正在分析健康数据...',
                isLoading: true,
                createTime: new Date().toISOString()
            });
            
            // 发送健康数据后，自动询问AI
            try {
                const prompt = `用户分享了健康数据: ${this.selectedHealthData.name}, 数值为 ${this.selectedHealthData.value} ${this.selectedHealthData.unit}。请根据这个健康数据，给出相应的健康建议。`;
                
                const response = await this.$axios.post('/api/deepseek/chat', { userPrompt: prompt });
                console.log('分享健康数据API回复:', response.data);
                
                // 检查响应格式并处理
                let content = '';
                if (response.data && response.data.content) {
                    content = response.data.content;
                } else if (response.data && response.data.success === false) {
                    throw new Error(response.data.errorMessage || '调用失败');
                } else {
                    console.error('意外的响应格式:', response.data);
                    throw new Error('无法解析AI回复');
                }
                
                // 替换临时消息
                this.messages.splice(tempMessageIndex, 1, {
                    senderId: 'ai',
                    content: content,
                    createTime: new Date().toISOString()
                });
            } catch (error) {
                console.error('调用AI接口失败:', error);
                // 移除临时消息
                this.messages.splice(tempMessageIndex, 1);
                this.$message.error('AI助手暂时无法回答，请稍后再试');
            } finally {
                this.aiLoading = false;
            }
        },
        formatTableDate(row) {
            return new Date(row.createTime).toLocaleString();
        }
    }
}
</script>
<style lang="scss">
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

/* 医疗咨询小助手按钮样式 */
.medical-assistant-button {
    position: fixed !important;
    bottom: 30px !important;
    right: 30px !important;
    width: 60px !important;
    height: 60px !important;
    border-radius: 50% !important;
    background-color: #38B781 !important; /* 绿色 */
    color: white !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    cursor: pointer !important;
    box-shadow: 0 4px 8px rgba(0,0,0,0.3) !important;
    z-index: 99999 !important;
    font-size: 28px !important;
    transition: all 0.3s ease !important;
}

.medical-assistant-button:hover {
    transform: scale(1.05) !important;
    box-shadow: 0 6px 12px rgba(0,0,0,0.4) !important;
}

/* 聊天窗口样式 */
.chat-window-container {
    position: fixed;
    border-radius: 8px;
    background-color: white;
    box-shadow: 0 4px 12px rgba(0,0,0,0.2);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    z-index: 99999;
}

.chat-window-header {
    background-color: #38B781;
    color: white;
    padding: 10px 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: move;
}

.chat-window-title {
    font-weight: bold;
    font-size: 16px;
}

.chat-window-controls {
    display: flex;
    gap: 10px;
}

.chat-window-controls i {
    cursor: pointer;
    font-size: 18px;
}

.chat-window-body {
    flex: 1;
    padding: 10px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

.resize-handle {
    position: absolute;
    width: 20px;
    height: 20px;
    bottom: 0;
    right: 0;
    cursor: nwse-resize;
    background-image: linear-gradient(135deg, transparent 70%, #38B781 70%, #38B781 100%);
}

/* 聊天内容样式 */
.chat-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    overflow: hidden;
}

.chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
    background-color: #f9f9f9;
    border-radius: 4px;
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
    margin-top: 10px;
    display: flex;
    flex-direction: column;
}

.input-actions {
    margin-bottom: 10px;
}

.input-box {
    margin-bottom: 10px;
}

.send-button {
    align-self: flex-end;
}

.health-data-preview {
    background-color: #f0f9eb;
    border-radius: 4px;
    padding: 6px 10px;
    margin-top: 5px;
    border-left: 3px solid #38B781;
}

.health-data-title {
    font-weight: bold;
    font-size: 12px;
    color: #38B781;
    margin-bottom: 3px;
}

.health-data-item {
    font-size: 12px;
}

/* 加载状态样式 */
.loading-text {
    color: #999;
    font-style: italic;
}

.loading-dots {
    display: inline-block;
    animation: loading 1.5s infinite;
}

@keyframes loading {
    0% { opacity: 0.3; }
    50% { opacity: 1; }
    100% { opacity: 0.3; }
}
</style>