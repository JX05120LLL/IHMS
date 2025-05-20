// 移除对外部库的依赖，改为纯轮询方式
// import SockJS from 'sockjs-client'
// import Stomp from 'webstomp-client'

class WebSocketService {
  constructor() {
    this.stompClient = null
    this.connected = false
    this.pollingCallbacks = new Map()
    this.pollingIntervals = new Map()
  }

  // 连接WebSocket（模拟方法，实际使用轮询）
  connect(consultationId, messageCallback) {
    console.log('使用轮询模式替代WebSocket连接')
    // 保存回调函数
    this.pollingCallbacks.set(consultationId, messageCallback)
    this.connected = false
    
    return Promise.reject('WebSocket依赖缺失，使用轮询模式')
  }

  // 断开WebSocket连接
  disconnect() {
    this.connected = false
    console.log('聊天连接已断开')
    
    // 清除所有轮询间隔
    this.pollingCallbacks.clear()
    this.pollingIntervals.forEach((interval) => {
      clearInterval(interval)
    })
    this.pollingIntervals.clear()
  }

  // 发送消息
  sendMessage(consultationId, message) {
    console.log('使用HTTP API发送消息')
    return false
  }

  // 检查是否已连接
  isConnected() {
    return this.connected
  }

  // 启动对特定会话的轮询
  startPolling(consultationId, axios, interval = 3000) {
    if (this.pollingIntervals.has(consultationId)) {
      return // 如果已经在轮询，不要再创建新的
    }
    
    let lastMessageId = 0
    
    const pollingInterval = setInterval(async () => {
      try {
        const response = await axios.get(`/doctor-message/list?consultationId=${consultationId}`)
        if (response.data.code === 200) {
          const messages = response.data.data
          
          // 找出新消息（ID大于上次看到的最大ID）
          const newMessages = messages.filter(m => m.id > lastMessageId)
          
          if (newMessages.length > 0) {
            // 更新最后消息ID
            lastMessageId = Math.max(...messages.map(m => m.id))
            
            // 对每条新消息调用回调
            const callback = this.pollingCallbacks.get(consultationId)
            if (callback) {
              newMessages.forEach(message => callback(message))
            }
          }
        }
      } catch (error) {
        console.error('轮询消息失败:', error)
      }
    }, interval)
    
    this.pollingIntervals.set(consultationId, pollingInterval)
  }
  
  // 停止对特定会话的轮询
  stopPolling(consultationId) {
    const interval = this.pollingIntervals.get(consultationId)
    if (interval) {
      clearInterval(interval)
      this.pollingIntervals.delete(consultationId)
    }
  }
}

export default new WebSocketService() 