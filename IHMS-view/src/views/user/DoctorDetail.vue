<template>
  <div class="doctor-detail">
    <div class="page-header">
      <el-button icon="el-icon-arrow-left" @click="goBack">返回</el-button>
      <h2>医生详情</h2>
    </div>

    <div class="page-content" v-loading="loading">
      <template v-if="doctor">
        <div class="doctor-basic-info">
          <div class="doctor-avatar">
            <el-avatar :size="100" :src="doctor.avatar || '/img/default-avatar.png'"></el-avatar>
          </div>
          <div class="doctor-info">
            <h2>{{ doctor.name }} <span class="title">{{ doctor.title }}</span></h2>
            <p class="specialty"><i class="el-icon-s-custom"></i> {{ doctor.specialty }}</p>
            <p class="hospital"><i class="el-icon-office-building"></i> {{ doctor.hospital }}</p>
            <div class="actions">
              <el-button type="primary" @click="startConsultation">开始咨询</el-button>
            </div>
          </div>
        </div>

        <div class="doctor-description">
          <h3>个人简介</h3>
          <p>{{ doctor.description || '暂无个人简介' }}</p>
        </div>

        <div class="past-consultations" v-if="pastConsultations.length > 0">
          <h3>过往咨询</h3>
          <el-timeline>
            <el-timeline-item
              v-for="(consultation, index) in pastConsultations"
              :key="index"
              :timestamp="formatDate(consultation.createTime)"
              placement="top"
            >
              <el-card>
                <h4>{{ consultation.title }}</h4>
                <p class="status">
                  状态: 
                  <el-tag :type="consultation.status === 0 ? 'success' : 'info'">
                    {{ consultation.status === 0 ? '进行中' : '已结束' }}
                  </el-tag>
                </p>
                <div class="actions">
                  <el-button size="small" type="primary" @click="enterConsultation(consultation.id)">查看详情</el-button>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </template>
      <el-empty v-else description="未找到医生信息"></el-empty>
    </div>

    <!-- 创建咨询对话框 -->
    <el-dialog title="开始咨询" :visible.sync="consultationDialogVisible" width="30%">
      <el-form :model="consultationForm" label-width="80px">
        <el-form-item label="咨询主题">
          <el-input v-model="consultationForm.title" placeholder="请输入咨询主题"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="consultationDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="createConsultation" :loading="creatingConsultation">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'DoctorDetail',
  data() {
    return {
      doctor: null,
      loading: false,
      doctorId: null,
      pastConsultations: [],
      consultationDialogVisible: false,
      consultationForm: {
        title: '',
        doctorId: null
      },
      creatingConsultation: false
    }
  },
  created() {
    this.doctorId = this.$route.params.id
    this.loadDoctorDetail()
    this.loadPastConsultations()
  },
  methods: {
    // 返回上一页
    goBack() {
      this.$router.push('/doctor-consultation')
    },
    // 加载医生详情
    async loadDoctorDetail() {
      if (!this.doctorId) return
      
      this.loading = true
      try {
        const response = await this.$axios.get(`/doctor/detail/${this.doctorId}`)
        if (response.data.code === 200) {
          this.doctor = response.data.data
        } else {
          this.$message.error(response.data.msg || '获取医生详情失败')
        }
      } catch (error) {
        console.error('获取医生详情出错', error)
        this.$message.error('获取医生详情出错')
      } finally {
        this.loading = false
      }
    },
    // 加载过往咨询
    async loadPastConsultations() {
      if (!this.doctorId) return
      
      try {
        const response = await this.$axios.get('/consultation/list?role=1')
        if (response.data.code === 200) {
          // 筛选出与当前医生的咨询
          this.pastConsultations = response.data.data.filter(item => item.doctorId == this.doctorId)
        }
      } catch (error) {
        console.error('获取咨询记录出错', error)
      }
    },
    // 开始咨询
    startConsultation() {
      this.consultationForm = {
        title: '',
        doctorId: this.doctorId
      }
      this.consultationDialogVisible = true
    },
    // 创建咨询
    async createConsultation() {
      if (!this.consultationForm.title) {
        this.$message.warning('请输入咨询主题')
        return
      }
      
      this.creatingConsultation = true
      try {
        const consultationData = {
          doctorId: this.consultationForm.doctorId,
          title: this.consultationForm.title
        }
        
        const response = await this.$axios.post('/consultation/create', consultationData)
        if (response.data.code === 200) {
          this.$message.success('咨询创建成功')
          this.consultationDialogVisible = false
          
          // 创建成功后直接进入会话
          const consultationId = response.data.data
          this.$router.push(`/consultation-chat/${consultationId}`)
        } else {
          this.$message.error(response.data.msg || '创建咨询失败')
        }
      } catch (error) {
        console.error('创建咨询出错', error)
        this.$message.error('创建咨询出错')
      } finally {
        this.creatingConsultation = false
      }
    },
    // 进入会话
    enterConsultation(id) {
      this.$router.push(`/consultation-chat/${id}`)
    },
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    }
  }
}
</script>

<style scoped lang="scss">
.doctor-detail {
  padding: 20px;
  
  .page-header {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 30px;
    
    h2 {
      margin: 0;
      color: #15559a;
    }
  }
  
  .page-content {
    .doctor-basic-info {
      display: flex;
      gap: 30px;
      margin-bottom: 40px;
      
      .doctor-avatar {
        flex-shrink: 0;
      }
      
      .doctor-info {
        h2 {
          margin: 0 0 15px;
          
          .title {
            font-size: 16px;
            color: #15559a;
            margin-left: 10px;
          }
        }
        
        p {
          margin: 10px 0;
          font-size: 16px;
          display: flex;
          align-items: center;
          
          i {
            margin-right: 8px;
            color: #666;
          }
        }
        
        .specialty {
          color: #666;
        }
        
        .hospital {
          color: #333;
          font-weight: 500;
        }
        
        .actions {
          margin-top: 20px;
        }
      }
    }
    
    .doctor-description {
      margin-bottom: 40px;
      
      h3 {
        font-size: 18px;
        border-bottom: 1px solid #eee;
        padding-bottom: 10px;
        margin-bottom: 15px;
      }
      
      p {
        line-height: 1.6;
        color: #666;
      }
    }
    
    .past-consultations {
      h3 {
        font-size: 18px;
        border-bottom: 1px solid #eee;
        padding-bottom: 10px;
        margin-bottom: 15px;
      }
      
      .el-timeline {
        padding-left: 0;
        
        .el-card {
          h4 {
            margin-top: 0;
          }
          
          .status {
            margin: 10px 0;
          }
          
          .actions {
            margin-top: 10px;
          }
        }
      }
    }
  }
}
</style> 