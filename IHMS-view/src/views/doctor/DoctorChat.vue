<template>
  <div class="doctor-chat">
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
                :src="message.senderId === currentUserId ? doctorAvatar : patientAvatar"
              ></el-avatar>
            </div>
            <div class="message-content">
              <div class="sender-name">{{ message.senderId === currentUserId ? '我(医生)' : patientName }}</div>
              
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
                    <span>患者健康数据</span>
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
                    <div class="health-data-analysis">
                      <el-button type="primary" size="small" @click="analyzeHealthData(message.healthData)">分析数据</el-button>
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
        <div class="input-box">
          <el-input
            v-model="inputMessage"
            placeholder="输入专业建议..."
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
          description="咨询已结束，无法继续发送消息"
          show-icon
          :closable="false"
        ></el-alert>
      </div>
    </div>

    <!-- 健康数据分析对话框 -->
    <el-dialog title="健康数据分析" :visible.sync="analysisDialogVisible" width="50%">
      <div class="health-data-analysis-content">
        <div class="health-data-info" v-if="selectedHealthData">
          <h3>{{ selectedHealthData.name }}</h3>
          <div class="health-data-value">
            <span class="label">记录值:</span>
            <span class="value">{{ selectedHealthData.value }} {{ selectedHealthData.unit }}</span>
          </div>
          <div class="health-data-range" v-if="selectedHealthData.valueRange">
            <span class="label">正常范围:</span>
            <span class="value">{{ selectedHealthData.valueRange.split(',')[0] }} - {{ selectedHealthData.valueRange.split(',')[1] }} {{ selectedHealthData.unit }}</span>
          </div>
          <div class="analysis-result">
            <h4>分析结果</h4>
            <el-input
              type="textarea"
              :rows="4"
              placeholder="请输入您的专业分析与建议..."
              v-model="analysisText"
            ></el-input>
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="analysisDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="sendAnalysis">发送给患者</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import websocketService from '@/utils/websocketService'

export default {
  name: 'DoctorChat',
  data() {
    return {
      consultationId: null,
      consultation: null,
      messages: [],
      loading: false,
      inputMessage: '',
      currentUserId: null,
      doctorAvatar: '',
      patientAvatar: '/img/default-avatar.png',
      patientName: '患者',
      patientId: null,
      // 分析相关
      analysisDialogVisible: false,
      selectedHealthData: null,
      analysisText: '',
      pollingInterval: null
    }
  },
  created() {
    this.consultationId = this.$route.params.id
    // 获取当前医生信息
    const doctorInfo = JSON.parse(sessionStorage.getItem('doctorInfo') || '{}')
    this.currentUserId = doctorInfo.id
    this.doctorAvatar = doctorInfo.avatar || '/img/default-avatar.png'
    
    // 初始化数据
    this.loadConsultationDetail()
    this.loadMessages()
    
    // 初始化WebSocket连接
    this.initWebSocket()
  },
  beforeDestroy() {
    // 断开WebSocket连接
    websocketService.disconnect()
    // 停止轮询
    this.stopPolling()
  },
  methods: {
    // 返回上一页
    goBack() {
      this.$router.push('/doctor-consultations')
    },
    // 加载咨询详情
    async loadConsultationDetail() {
      if (!this.consultationId) return
      
      this.loading = true
      try {
        const response = await this.$axios.get(`/consultation/detail/${this.consultationId}`)
        if (response.data.code === 200) {
          this.consultation = response.data.data
          this.patientId = this.consultation.patientId
          
          // 加载患者信息
          await this.loadPatientInfo(this.consultation.patientId)
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
    // 加载患者信息
    async loadPatientInfo(patientId) {
      try {
        const response = await this.$axios.get(`/user/detail/${patientId}`)
        if (response.data.code === 200) {
          const patient = response.data.data
          this.patientName = patient.userName || '患者'
          this.patientAvatar = patient.userAvatar || '/img/default-avatar.png'
        }
      } catch (error) {
        console.error('获取患者信息出错', error)
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
        // 使用新的轮询方式
        this.startPolling()
      }
    },
    // 使用轮询替代WebSocket
    startPolling() {
      console.log('启动消息轮询机制')
      // 使用WebSocketService的轮询功能
      websocketService.startPolling(this.consultationId, this.$axios, 3000)
      
      // 保留原有轮询作为备份
      if (!this.pollingInterval) {
        this.pollingInterval = setInterval(() => {
          this.loadMessages()
        }, 5000)
      }
    },
    // 停止轮询
    stopPolling() {
      websocketService.stopPolling(this.consultationId)
      
      if (this.pollingInterval) {
        clearInterval(this.pollingInterval)
        this.pollingInterval = null
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
          senderType: 1, // 1:医生
          receiverId: this.patientId,
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
    // 分析健康数据
    analyzeHealthData(healthData) {
      this.selectedHealthData = healthData
      
      // 生成默认分析文本
      let analysisTemplate = `患者${this.patientName}的${healthData.name}为${healthData.value}${healthData.unit}，`
      
      // 如果有正常范围，判断是否在范围内
      if (healthData.valueRange) {
        const ranges = healthData.valueRange.split(',')
        const minValue = parseFloat(ranges[0])
        const maxValue = parseFloat(ranges[1])
        const currentValue = parseFloat(healthData.value)
        
        if (currentValue < minValue) {
          analysisTemplate += `低于正常范围(${minValue}-${maxValue}${healthData.unit})。建议...`
        } else if (currentValue > maxValue) {
          analysisTemplate += `高于正常范围(${minValue}-${maxValue}${healthData.unit})。建议...`
        } else {
          analysisTemplate += `处于正常范围(${minValue}-${maxValue}${healthData.unit})内。`
        }
      } else {
        analysisTemplate += `需要结合患者其他健康数据进行综合分析。`
      }
      
      this.analysisText = analysisTemplate
      this.analysisDialogVisible = true
    },
    // 发送分析结果
    async sendAnalysis() {
      if (!this.analysisText.trim()) {
        this.$message.warning('请输入分析结果')
        return
      }
      
      try {
        const message = {
          consultationId: this.consultationId,
          senderId: this.currentUserId,
          senderType: 1, // 1:医生
          receiverId: this.patientId,
          content: this.analysisText,
          messageType: 0, // 0:文本
          isRead: 0 // 0:未读
        }
        
        // 如果WebSocket已连接，使用WebSocket发送分析
        if (websocketService.isConnected()) {
          const success = websocketService.sendMessage(this.consultationId, message)
          if (success) {
            this.analysisDialogVisible = false
            this.analysisText = ''
            return
          }
        }
        
        // WebSocket发送失败或未连接，使用HTTP API发送
        const response = await this.$axios.post('/doctor-message/send', message)
        if (response.data.code === 200) {
          this.analysisDialogVisible = false
          this.analysisText = ''
          this.loadMessages()
        } else {
          this.$message.error(response.data.msg || '发送分析结果失败')
        }
      } catch (error) {
        console.error('发送分析结果出错', error)
        this.$message.error('发送分析结果出错')
      }
    },
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    // 滚动到底部
    scrollToBottom() {
      const container = this.$refs.messagesContainer
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    }
  }
}
</script>

<style scoped lang="scss">
.doctor-chat {
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
              background-color: #67c23a;
              color: white;
              border-top-right-radius: 0;
              border-top-left-radius: 8px;
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
                  color: #67c23a;
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
                    width: 70px;
                  }
                  
                  .item-value {
                    font-weight: 500;
                  }
                }
                
                .health-data-analysis {
                  margin-top: 10px;
                  display: flex;
                  justify-content: flex-end;
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
  
  .health-data-analysis-content {
    padding: 20px;
    
    .health-data-info {
      h3 {
        margin-top: 0;
        color: #67c23a;
      }
      
      .health-data-value, .health-data-range {
        margin-bottom: 10px;
        
        .label {
          font-weight: bold;
          color: #666;
          margin-right: 10px;
        }
        
        .value {
          font-size: 16px;
        }
      }
      
      .analysis-result {
        margin-top: 20px;
        
        h4 {
          margin-bottom: 10px;
        }
      }
    }
  }
}
</style> 