<template>
  <div class="doctor-home">
    <div class="header">
      <h1>医生工作台</h1>
      <div class="actions">
        <el-button type="primary" plain icon="el-icon-s-grid" @click="goToConsultations">患者咨询管理</el-button>
        <el-button type="info" plain icon="el-icon-switch-button" @click="logout">退出登录</el-button>
      </div>
    </div>
    
    <el-row :gutter="24">
      <!-- 左侧个人信息面板 -->
      <el-col :md="8" :sm="24" v-if="doctorInfo">
        <el-card class="profile-card" shadow="hover">
          <div class="profile-header">
            <el-avatar :size="90" :src="doctorInfo.avatar || '/img/default-avatar.png'" class="doctor-avatar"></el-avatar>
            <h2 class="doctor-name">{{ doctorInfo.name }}</h2>
            <div class="doctor-title">{{ doctorInfo.title }}</div>
          </div>
          <div class="profile-info">
            <div class="info-item">
              <i class="el-icon-collection-tag"></i>
              <span>{{ doctorInfo.specialty }}</span>
            </div>
            <div class="info-item">
              <i class="el-icon-office-building"></i>
              <span>{{ doctorInfo.hospital }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 右侧统计和内容区 -->
      <el-col :md="16" :sm="24">
        <div class="stats-container">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-card class="stat-card active-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-icon-container">
                    <i class="el-icon-chat-dot-round"></i>
                  </div>
                  <div class="stat-details">
                    <div class="stat-value">{{ stats.activeConsultations }}</div>
                    <div class="stat-label">进行中咨询</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="stat-card completed-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-icon-container">
                    <i class="el-icon-finished"></i>
                  </div>
                  <div class="stat-details">
                    <div class="stat-value">{{ stats.completedConsultations }}</div>
                    <div class="stat-label">已完成咨询</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="stat-card patient-card" shadow="hover">
                <div class="stat-content">
                  <div class="stat-icon-container">
                    <i class="el-icon-user"></i>
                  </div>
                  <div class="stat-details">
                    <div class="stat-value">{{ stats.totalPatients }}</div>
                    <div class="stat-label">患者总数</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
        
        <!-- 可以添加最近咨询列表或其他有用信息 -->
        <el-card class="recent-card" shadow="hover">
          <div slot="header" class="recent-header">
            <span>工作提示</span>
          </div>
          <div class="welcome-message">
            <i class="el-icon-s-opportunity"></i>
            <p>欢迎回来，{{ doctorInfo && doctorInfo.name }}医生！您可以在此管理患者咨询并查看健康数据统计。</p>
          </div>
          <div class="tips">
            <p><i class="el-icon-info"></i> 您可以通过点击"患者咨询管理"按钮查看所有患者咨询记录</p>
            <p><i class="el-icon-info"></i> 在患者咨询页面，您可以回复患者消息并提供专业建议</p>
            <p><i class="el-icon-info"></i> 系统会自动统计您的咨询数据，帮助您更好地管理工作</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { removeToken } from '@/utils/storage.js'

export default {
  name: 'DoctorHome',
  data() {
    return {
      doctorInfo: null,
      stats: {
        activeConsultations: 0,
        completedConsultations: 0,
        totalPatients: 0
      }
    }
  },
  created() {
    // 从sessionStorage获取医生信息
    this.getDoctorInfo()
    // 加载统计数据
    this.loadStats()
  },
  methods: {
    // 获取医生信息
    getDoctorInfo() {
      const doctorInfo = JSON.parse(sessionStorage.getItem('doctorInfo') || '{}')
      
      if (!doctorInfo.id) {
        this.$message.error('医生信息不存在，请重新登录')
        this.logout()
        return
      }
      
      this.doctorInfo = doctorInfo
    },
    
    // 加载统计数据
    async loadStats() {
      if (!this.doctorInfo || !this.doctorInfo.id) return
      
      try {
        const response = await this.$axios.get(`/consultation/doctor-stats/${this.doctorInfo.id}`)
        if (response.data.code === 200) {
          this.stats = response.data.data
        }
      } catch (error) {
        console.error('加载统计数据失败', error)
      }
    },
    
    // 跳转到患者咨询页面
    goToConsultations() {
      this.$router.push('/doctor-consultations')
    },
    
    // 退出登录
    logout() {
      // 清除token和医生信息
      removeToken()
      sessionStorage.removeItem('doctorInfo')
      
      // 跳转到登录页
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped lang="scss">
.doctor-home {
  padding: 25px;
  background-color: #f8f9fa;
  min-height: calc(100vh - 50px);
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 25px;
    
    h1 {
      margin: 0;
      font-size: 24px;
      color: #303133;
      font-weight: 500;
    }
    
    .actions {
      display: flex;
      gap: 12px;
    }
  }
  
  .profile-card {
    height: 100%;
    border-radius: 8px;
    
    .profile-header {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20px 0;
      border-bottom: 1px solid #f0f0f0;
      
      .doctor-avatar {
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
        margin-bottom: 16px;
      }
      
      .doctor-name {
        margin: 10px 0 5px;
        font-size: 20px;
        font-weight: 500;
        color: #303133;
      }
      
      .doctor-title {
        font-size: 14px;
        color: #409EFF;
        background: rgba(64, 158, 255, 0.1);
        padding: 4px 12px;
        border-radius: 15px;
        margin-top: 5px;
      }
    }
    
    .profile-info {
      padding: 20px 10px;
      
      .info-item {
        display: flex;
        align-items: center;
        margin-bottom: 16px;
        padding: 8px 12px;
        background-color: #fafafa;
        border-radius: 6px;
        
        i {
          font-size: 18px;
          color: #606266;
          margin-right: 12px;
        }
        
        span {
          font-size: 14px;
          color: #606266;
        }
      }
    }
  }
  
  .stats-container {
    margin-bottom: 24px;
    
    .stat-card {
      border-radius: 8px;
      
      .stat-content {
        display: flex;
        align-items: center;
        padding: 5px;
        
        .stat-icon-container {
          width: 55px;
          height: 55px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 15px;
          
          i {
            font-size: 24px;
            color: white;
          }
        }
        
        .stat-details {
          flex: 1;
          
          .stat-value {
            font-size: 24px;
            font-weight: 600;
            margin-bottom: 4px;
          }
          
          .stat-label {
            font-size: 13px;
            color: #909399;
          }
        }
      }
    }
    
    .active-card .stat-icon-container {
      background-color: #409EFF;
    }
    
    .completed-card .stat-icon-container {
      background-color: #67C23A;
    }
    
    .patient-card .stat-icon-container {
      background-color: #E6A23C;
    }
  }
  
  .recent-card {
    border-radius: 8px;
    
    .recent-header {
      padding: 10px 0;
      font-size: 16px;
      font-weight: 500;
    }
    
    .welcome-message {
      background-color: #ecf5ff;
      padding: 16px;
      border-radius: 6px;
      margin-bottom: 20px;
      display: flex;
      align-items: center;
      
      i {
        font-size: 24px;
        color: #409EFF;
        margin-right: 15px;
      }
      
      p {
        margin: 0;
        color: #303133;
        font-size: 14px;
      }
    }
    
    .tips {
      p {
        margin: 12px 0;
        padding: 10px;
        background-color: #f8f8f8;
        border-radius: 6px;
        font-size: 13px;
        color: #606266;
        
        i {
          color: #909399;
          margin-right: 8px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .doctor-home {
    padding: 15px;
    
    .header {
      flex-direction: column;
      align-items: flex-start;
      
      h1 {
        margin-bottom: 15px;
      }
    }
    
    .profile-card {
      margin-bottom: 20px;
    }
  }
}
</style> 