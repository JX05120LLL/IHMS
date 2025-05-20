<template>
  <div class="doctor-consultations">
    <h2>我的患者咨询</h2>
    
    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="进行中的咨询" name="active">
        <el-table
          :data="consultations"
          style="width: 100%"
          v-loading="loading"
          border
        >
          <el-table-column
            prop="title"
            label="咨询主题"
            min-width="180"
          ></el-table-column>
          <el-table-column
            prop="patientName"
            label="患者姓名"
            width="120"
          ></el-table-column>
          <el-table-column
            prop="createTime"
            label="创建时间"
            width="180"
            :formatter="formatDate"
          ></el-table-column>
          <el-table-column
            label="状态"
            width="100"
            align="center"
          >
            <template slot-scope="scope">
              <el-tag
                :type="scope.row.status === 0 ? 'success' : 'info'"
              >
                {{ scope.row.status === 0 ? '进行中' : '已结束' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            width="180"
            align="center"
          >
            <template slot-scope="scope">
              <el-button
                type="primary"
                size="small"
                @click="enterChat(scope.row)"
              >
                {{ scope.row.status === 0 ? '继续咨询' : '查看记录' }}
              </el-button>
              <el-button
                v-if="scope.row.status === 0"
                type="danger"
                size="small"
                @click="endConsultation(scope.row)"
              >
                结束
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination" v-if="total > 0">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            :current-page.sync="currentPage"
            @current-change="handleCurrentChange"
          ></el-pagination>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="已结束的咨询" name="completed">
        <el-table
          :data="consultations"
          style="width: 100%"
          v-loading="loading"
          border
        >
          <el-table-column
            prop="title"
            label="咨询主题"
            min-width="180"
          ></el-table-column>
          <el-table-column
            prop="patientName"
            label="患者姓名"
            width="120"
          ></el-table-column>
          <el-table-column
            prop="createTime"
            label="创建时间"
            width="180"
            :formatter="formatDate"
          ></el-table-column>
          <el-table-column
            prop="endTime"
            label="结束时间"
            width="180"
            :formatter="formatDate"
          ></el-table-column>
          <el-table-column
            label="操作"
            width="120"
            align="center"
          >
            <template slot-scope="scope">
              <el-button
                type="info"
                size="small"
                @click="enterChat(scope.row)"
              >
                查看记录
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination" v-if="total > 0">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            :current-page.sync="currentPage"
            @current-change="handleCurrentChange"
          ></el-pagination>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  name: 'DoctorConsultations',
  data() {
    return {
      activeTab: 'active',
      consultations: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      doctorId: null
    }
  },
  created() {
    // 获取医生ID
    const doctorInfo = JSON.parse(sessionStorage.getItem('doctorInfo') || '{}')
    this.doctorId = doctorInfo.id
    
    // 加载咨询列表
    this.loadConsultations()
  },
  methods: {
    // 加载咨询列表
    async loadConsultations() {
      if (!this.doctorId) {
        this.$message.error('医生信息不存在，请重新登录')
        return
      }
      
      this.loading = true
      try {
        // 根据当前选项卡加载不同状态的咨询
        const status = this.activeTab === 'active' ? 0 : 1
        const response = await this.$axios.get('/consultation/doctor-list', {
          params: {
            doctorId: this.doctorId,
            status: status,
            current: this.currentPage - 1,
            size: this.pageSize
          }
        })
        
        if (response.data.code === 200) {
          this.consultations = response.data.data.records || []
          this.total = response.data.data.total || 0
          
          // 加载患者信息
          for (const consultation of this.consultations) {
            await this.loadPatientInfo(consultation)
          }
        } else {
          this.$message.error(response.data.msg || '获取咨询列表失败')
        }
      } catch (error) {
        console.error('加载咨询列表出错', error)
        this.$message.error('加载咨询列表出错')
      } finally {
        this.loading = false
      }
    },
    // 加载患者信息
    async loadPatientInfo(consultation) {
      try {
        const response = await this.$axios.get(`/user/detail/${consultation.patientId}`)
        if (response.data.code === 200) {
          const patient = response.data.data
          this.$set(consultation, 'patientName', patient.userName || '未知患者')
        }
      } catch (error) {
        console.error('获取患者信息出错', error)
        this.$set(consultation, 'patientName', '未知患者')
      }
    },
    // 进入聊天页面
    enterChat(consultation) {
      this.$router.push(`/doctor-chat/${consultation.id}`)
    },
    // 结束咨询
    async endConsultation(consultation) {
      try {
        const confirmed = await this.$confirm('确定要结束此次咨询吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        if (confirmed) {
          const response = await this.$axios.put(`/consultation/end/${consultation.id}`)
          if (response.data.code === 200) {
            this.$message.success('咨询已结束')
            this.loadConsultations()
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
    // 切换标签页
    handleTabChange() {
      this.currentPage = 1
      this.loadConsultations()
    },
    // 切换页码
    handleCurrentChange() {
      this.loadConsultations()
    },
    // 格式化日期
    formatDate(row, column, cellValue) {
      if (!cellValue) return '-'
      const date = new Date(cellValue)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    }
  }
}
</script>

<style scoped lang="scss">
.doctor-consultations {
  padding: 20px;
  
  h2 {
    margin-bottom: 20px;
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style> 