<template>
  <div class="consultation-chat">
    <div class="chat-header">
      <el-button icon="el-icon-arrow-left" @click="goBack">返回</el-button>
      <div class="consultation-info" v-if="consultation">
        <h2>{{ consultation.title }}</h2>
        <div class="status">
          <el-tag :type="consultation.status === 0 ? 'success' : 'info'">
            {{ consultation.status === 0 ? '进行中' : '已结束' }}
          </el-tag>
        </div>
      </div>
      <div class="actions" v-if="consultation && consultation.status === 0">
        <el-button type="danger" size="small" @click="endConsultation">结束会话</el-button>
      </div>
    </div>

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
                :src="message.senderId === currentUserId ? userAvatar : doctorAvatar"
              ></el-avatar>
            </div>
            <div class="message-content">
              <div class="sender-name">{{ message.senderId === currentUserId ? '我' : doctorName }}</div>
              
              <!-- 文本消息 -->
              <div v-if="message.messageType === 0" class="text-content">
                {{ message.content }}
              </div>
              
              <!-- 图片消息 -->
              <div v-else-if="message.messageType === 1" class="image-content">
                <el-image 
                  :src="message.content" 
                  :preview-src-list="[message.content]"
                  fit="cover"
                  class="message-image"
                ></el-image>
              </div>
              
              <!-- 健康数据消息 -->
              <div v-else-if="message.messageType === 2" class="health-data-content">
                <div class="health-data-card">
                  <div class="health-data-header">
                    <i class="el-icon-data-analysis"></i>
                    <span>健康数据分享</span>
                  </div>
                  <div class="health-data-body" v-if="message.healthData">
                    <div class="health-data-item">
                      <span class="item-label">健康模型:</span>
                      <span class="item-value">{{ message.healthData.name }}</span>
                    </div>
                    <div class="health-data-item">
                      <span class="item-label">记录值:</span>
                      <span class="item-value">{{ message.healthData.value }} {{ message.healthData.unit }}</span>
                    </div>
                    <div class="health-data-item">
                      <span class="item-label">记录时间:</span>
                      <span class="item-value">{{ formatDate(message.healthData.createTime) }}</span>
                    </div>
                  </div>
                  <div class="health-data-body" v-else>
                    <p>加载健康数据中...</p>
                  </div>
                </div>
              </div>

              <div class="message-time">{{ formatDate(message.createTime) }}</div>
            </div>
          </div>
        </template>
        <div v-else class="empty-messages">
          <el-empty description="暂无消息记录"></el-empty>
        </div>
      </div>

      <div class="chat-input" v-if="consultation && consultation.status === 0">
        <div class="input-actions">
          <el-tooltip content="分享健康数据" placement="top">
            <el-button 
              icon="el-icon-data-analysis" 
              circle 
              size="small"
              @click="showHealthDataDialog"
            ></el-button>
          </el-tooltip>
          <el-tooltip content="发送图片" placement="top">
            <el-upload
              class="image-upload"
              action="/api/personal-heath/v1.0/file/upload"
              :show-file-list="false"
              :on-success="handleImageSuccess"
              :before-upload="beforeImageUpload"
            >
              <el-button 
                icon="el-icon-picture" 
                circle 
                size="small"
              ></el-button>
            </el-upload>
          </el-tooltip>
        </div>
        <div class="input-box">
          <el-input
            v-model="inputMessage"
            placeholder="输入消息..."
            type="textarea"
            :rows="3"
            @keyup.enter.native.ctrl="sendMessage"
          ></el-input>
        </div>
        <div class="send-button">
          <el-button type="primary" @click="sendMessage" :disabled="!inputMessage.trim()">发送</el-button>
        </div>
      </div>
      <div class="chat-ended" v-else-if="consultation && consultation.status === 1">
        <el-alert
          title="此咨询会话已结束"
          type="info"
          description="您可以通过创建新的咨询继续与医生交流"
          show-icon
          :closable="false"
        ></el-alert>
      </div>
    </div>

    <!-- 健康数据分享对话框 -->
    <el-dialog title="分享健康数据" :visible.sync="healthDataDialogVisible" width="50%">
      <div class="health-data-list" v-loading="healthDataLoading">
        <template v-if="healthDataList.length > 0">
          <el-table
            :data="healthDataList"
            style="width: 100%"
            @row-click="selectHealthData"
            highlight-current-row
          >
            <el-table-column type="index" width="50"></el-table-column>
            <el-table-column label="健康模型" prop="name"></el-table-column>
            <el-table-column label="记录值">
              <template slot-scope="scope">
                {{ scope.row.value }} {{ scope.row.unit }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="记录时间" :formatter="formatTableDate"></el-table-column>
          </el-table>
        </template>
        <el-empty v-else description="暂无健康数据记录"></el-empty>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="healthDataDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="shareHealthData" :disabled="!selectedHealthData">确认分享</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import websocketService from '@/utils/websocketService'

export default {
  name: 'ConsultationChat',
  data() {
    return {
      consultationId: null,
      consultation: null,
      messages: [],
      loading: false,
      inputMessage: '',
      currentUserId: null,
      userAvatar: '',
      doctorAvatar: '',
      doctorName: '',
      // 健康数据
      healthDataDialogVisible: false,
      healthDataList: [],
      healthDataLoading: false,
      selectedHealthData: null,
      // 轮询定时器
      pollingTimer: null
    }
  },
  created() {
    this.consultationId = this.$route.params.id
    // 获取当前用户信息
    const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
    this.currentUserId = userInfo.id
    this.userAvatar = userInfo.url || '/img/default-avatar.png'
    
    // 初始化数据
    this.loadConsultationDetail()
    this.loadMessages()
    
    // 初始化WebSocket连接
    this.initWebSocket()
  },
  beforeDestroy() {
    // 断开WebSocket连接
    websocketService.disconnect()
  },
  methods: {
    // 返回上一页
    goBack() {
      this.$router.push('/doctor-consultation')
    },
    // 加载咨询详情
    async loadConsultationDetail() {
      if (!this.consultationId) return
      
      this.loading = true
      try {
        const response = await this.$axios.get(`/consultation/detail/${this.consultationId}`)
        if (response.data.code === 200) {
          this.consultation = response.data.data
          
          // 加载医生信息
          await this.loadDoctorInfo(this.consultation.doctorId)
        } else {
          this.$message.error(response.data.msg || '获取咨询详情失败')
        }
      } catch (error) {
        console.error('获取咨询详情出错', error)
        this.$message.error('获取咨询详情出错')
      } finally {
        this.loading = false
      }
    },
    // 加载医生信息
    async loadDoctorInfo(doctorId) {
      try {
        const response = await this.$axios.get(`/doctor/detail/${doctorId}`)
        if (response.data.code === 200) {
          const doctor = response.data.data
          this.doctorName = doctor.name
          this.doctorAvatar = doctor.avatar || '/img/default-avatar.png'
        }
      } catch (error) {
        console.error('获取医生信息出错', error)
      }
    },
    // 加载消息列表
    async loadMessages() {
      if (!this.consultationId) return
      
      try {
        const response = await this.$axios.get(`/doctor-message/list?consultationId=${this.consultationId}`)
        if (response.data.code === 200) {
          this.messages = response.data.data
          
          // 加载消息中的健康数据
          this.loadMessagesHealthData()
          
          // 滚动到底部
          this.$nextTick(() => {
            this.scrollToBottom()
          })
        } else {
          this.$message.error(response.data.msg || '获取消息列表失败')
        }
      } catch (error) {
        console.error('获取消息列表出错', error)
      }
    },
    // 加载消息中的健康数据
    async loadMessagesHealthData() {
      const healthDataMessages = this.messages.filter(m => m.messageType === 2 && m.healthDataId)
      
      for (const message of healthDataMessages) {
        if (message.healthData) continue
        
        try {
          const response = await this.$axios.get(`/health/record/detail/${message.healthDataId}`)
          if (response.data.code === 200) {
            this.$set(message, 'healthData', response.data.data)
          }
        } catch (error) {
          console.error('获取健康数据详情出错', error)
        }
      }
    },
    // 初始化WebSocket连接
    async initWebSocket() {
      try {
        await websocketService.connect(this.consultationId, this.handleNewMessage)
      } catch (error) {
        console.error('WebSocket连接失败，将使用轮询模式', error)
        // 如果WebSocket连接失败，使用轮询作为备选方案
        this.startPolling()
      }
    },
    // 处理新消息
    handleNewMessage(message) {
      // 检查消息是否已存在（防止重复）
      const exists = this.messages.some(m => m.id === message.id)
      if (!exists) {
        this.messages.push(message)
        
        // 如果是健康数据消息但没有健康数据详情，尝试加载
        if (message.messageType === 2 && message.healthDataId && !message.healthData) {
          this.loadHealthData(message)
        }
        
        // 滚动到底部
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },
    // 加载单条消息的健康数据
    async loadHealthData(message) {
      try {
        const response = await this.$axios.get(`/health/record/detail/${message.healthDataId}`)
        if (response.data.code === 200) {
          this.$set(message, 'healthData', response.data.data)
        }
      } catch (error) {
        console.error('获取健康数据详情出错', error)
      }
    },
    // 发送消息
    async sendMessage() {
      if (!this.inputMessage.trim()) return
      
      try {
        const message = {
          consultationId: this.consultationId,
          senderId: this.currentUserId,
          senderType: 0, // 0:患者
          receiverId: this.consultation.doctorId,
          content: this.inputMessage,
          messageType: 0, // 0:文本
          isRead: 0 // 0:未读
        }
        
        // 如果WebSocket已连接，使用WebSocket发送消息
        if (websocketService.isConnected()) {
          const success = websocketService.sendMessage(this.consultationId, message)
          if (success) {
            // 清空输入框
            this.inputMessage = ''
            return
          }
        }
        
        // WebSocket发送失败或未连接，使用HTTP API发送
        const response = await this.$axios.post('/doctor-message/send', message)
        if (response.data.code === 200) {
          // 清空输入框
          this.inputMessage = ''
          // 重新加载消息
          this.loadMessages()
        } else {
          this.$message.error(response.data.msg || '发送消息失败')
        }
      } catch (error) {
        console.error('发送消息出错', error)
        this.$message.error('发送消息出错')
      }
    },
    // 结束会话
    async endConsultation() {
      try {
        const confirmed = await this.$confirm('确定要结束此次咨询吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        if (confirmed) {
          const response = await this.$axios.put(`/consultation/end/${this.consultationId}`)
          if (response.data.code === 200) {
            this.$message.success('咨询已结束')
            this.loadConsultationDetail()
          } else {
            this.$message.error(response.data.msg || '结束咨询失败')
          }
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('结束咨询出错', error)
          this.$message.error('结束咨询出错')
        }
      }
    },
    // 显示健康数据对话框
    async showHealthDataDialog() {
      this.healthDataDialogVisible = true
      this.healthDataLoading = true
      this.selectedHealthData = null
      
      try {
        const userInfo = JSON.parse(sessionStorage.getItem('userInfo'))
        const response = await this.$axios.get(`/health/record/list?userId=${userInfo.id}`)
        if (response.data.code === 200) {
          this.healthDataList = response.data.data || []
        }
      } catch (error) {
        console.error('获取健康数据列表出错', error)
        this.$message.error('获取健康数据列表出错')
      } finally {
        this.healthDataLoading = false
      }
    },
    // 选择健康数据
    selectHealthData(row) {
      this.selectedHealthData = row
    },
    // 分享健康数据
    async shareHealthData() {
      if (!this.selectedHealthData) return
      
      try {
        const message = {
          consultationId: this.consultationId,
          senderId: this.currentUserId,
          senderType: 0, // 0:患者
          receiverId: this.consultation.doctorId,
          content: '分享健康数据',
          messageType: 2, // 2:健康数据
          healthDataId: this.selectedHealthData.id,
          isRead: 0 // 0:未读
        }
        
        // 如果WebSocket已连接，使用WebSocket分享健康数据
        if (websocketService.isConnected()) {
          const success = websocketService.sendMessage(this.consultationId, message)
          if (success) {
            this.healthDataDialogVisible = false
            this.$message.success('健康数据分享成功')
            return
          }
        }
        
        // WebSocket发送失败或未连接，使用HTTP API发送
        const response = await this.$axios.post('/doctor-message/share', message)
        if (response.data.code === 200) {
          this.healthDataDialogVisible = false
          this.$message.success('健康数据分享成功')
          this.loadMessages()
        } else {
          this.$message.error(response.data.msg || '分享健康数据失败')
        }
      } catch (error) {
        console.error('分享健康数据出错', error)
        this.$message.error('分享健康数据出错')
      }
    },
    // 图片上传前验证
    beforeImageUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5
      
      if (!isImage) {
        this.$message.error('只能上传图片文件')
        return false
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过 5MB')
        return false
      }
      return true
    },
    // 图片上传成功
    async handleImageSuccess(res) {
      if (res.code !== 200) {
        this.$message.error('图片上传失败')
        return
      }
      
      try {
        const imageUrl = res.data
        const message = {
          consultationId: this.consultationId,
          receiverId: this.consultation.doctorId,
          content: imageUrl,
          messageType: 1, // 1:图片
          isRead: 0 // 0:未读
        }
        
        const response = await this.$axios.post('/doctor-message/send', message)
        if (response.data.code === 200) {
          this.$message.success('图片发送成功')
          this.loadMessages()
        } else {
          this.$message.error(response.data.msg || '发送图片失败')
        }
      } catch (error) {
        console.error('发送图片出错', error)
        this.$message.error('发送图片出错')
      }
    },
    // 格式化表格日期
    formatTableDate(row, column, cellValue) {
      return this.formatDate(cellValue)
    },
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    // 滚动到底部
    scrollToBottom() {
      if (this.$refs.messagesContainer) {
        this.$refs.messagesContainer.scrollTop = this.$refs.messagesContainer.scrollHeight
      }
    },
    // 开始轮询
    startPolling() {
      this.pollingTimer = setInterval(() => {
        this.loadMessages()
      }, 5000) // 每5秒轮询一次
    },
    // 停止轮询
    stopPolling() {
      if (this.pollingTimer) {
        clearInterval(this.pollingTimer)
        this.pollingTimer = null
      }
    }
  }
}
</script>

<style scoped lang="scss">
.consultation-chat {
  height: calc(100vh - 150px);
  display: flex;
  flex-direction: column;
  
  .chat-header {
    display: flex;
    align-items: center;
    padding: 15px 20px;
    border-bottom: 1px solid #eee;
    
    .consultation-info {
      flex: 1;
      margin-left: 15px;
      
      h2 {
        margin: 0 0 5px;
        font-size: 18px;
      }
      
      .status {
        font-size: 14px;
      }
    }
  }
  
  .chat-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    
    .chat-messages {
      flex: 1;
      padding: 20px;
      overflow-y: auto;
      
      .empty-messages {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100%;
      }
      
      .message-item {
        display: flex;
        margin-bottom: 20px;
        
        &.self {
          flex-direction: row-reverse;
          
          .message-content {
            align-items: flex-end;
            margin-right: 10px;
            margin-left: 0;
            
            .text-content {
              background-color: #15559a;
              color: white;
              border-top-right-radius: 0;
              border-top-left-radius: 8px;
            }
            
            .health-data-content {
              align-self: flex-end;
            }
          }
        }
        
        .avatar {
          flex-shrink: 0;
        }
        
        .message-content {
          display: flex;
          flex-direction: column;
          margin-left: 10px;
          max-width: 70%;
          
          .sender-name {
            font-size: 12px;
            color: #999;
            margin-bottom: 5px;
          }
          
          .text-content {
            background-color: #f5f5f5;
            padding: 10px 15px;
            border-radius: 8px;
            border-top-left-radius: 0;
            word-break: break-word;
          }
          
          .image-content {
            max-width: 300px;
            
            .message-image {
              max-width: 100%;
              border-radius: 8px;
            }
          }
          
          .health-data-content {
            .health-data-card {
              border: 1px solid #eee;
              border-radius: 8px;
              overflow: hidden;
              width: 300px;
              
              .health-data-header {
                background-color: #f6f8fa;
                padding: 10px;
                font-weight: bold;
                border-bottom: 1px solid #eee;
                display: flex;
                align-items: center;
                
                i {
                  margin-right: 5px;
                  color: #15559a;
                }
              }
              
              .health-data-body {
                padding: 10px;
                background-color: white;
                
                .health-data-item {
                  display: flex;
                  margin-bottom: 5px;
                  
                  .item-label {
                    color: #666;
                    width: 60px;
                  }
                  
                  .item-value {
                    font-weight: 500;
                  }
                }
              }
            }
          }
          
          .message-time {
            font-size: 12px;
            color: #999;
            margin-top: 5px;
          }
        }
      }
    }
    
    .chat-input {
      padding: 15px;
      border-top: 1px solid #eee;
      display: flex;
      
      .input-actions {
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 10px;
        margin-right: 10px;
        
        .image-upload {
          display: inline-block;
        }
      }
      
      .input-box {
        flex: 1;
      }
      
      .send-button {
        align-self: flex-end;
        margin-left: 10px;
      }
    }
    
    .chat-ended {
      padding: 20px;
    }
  }
  
  .health-data-list {
    max-height: 400px;
    overflow-y: auto;
  }
}
</style> 