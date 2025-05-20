<template>
  <div class="doctor-consultation">
    <div class="page-header">
      <h2>医生咨询</h2>
      <p>在这里您可以寻找专业医生进行健康咨询，分享您的健康数据，获取专业指导</p>
    </div>

    <div class="page-content">
      <!-- 搜索筛选区 -->
      <div class="filter-container">
        <el-input
          placeholder="搜索医生姓名"
          v-model="searchQuery.name"
          class="search-input"
          prefix-icon="el-icon-search"
          @keyup.enter.native="searchDoctors"
        ></el-input>
        <el-select v-model="searchQuery.specialty" placeholder="专业领域" clearable @change="searchDoctors">
          <el-option
            v-for="item in specialtyOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
        <el-select v-model="searchQuery.title" placeholder="职称" clearable @change="searchDoctors">
          <el-option
            v-for="item in titleOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
        <el-button type="primary" @click="searchDoctors">搜索</el-button>
      </div>

      <!-- 我的咨询列表 -->
      <div class="my-consultations">
        <div class="section-header">
          <h3>我的咨询</h3>
          <el-button type="text" icon="el-icon-refresh" @click="loadMyConsultations">刷新</el-button>
        </div>
        <el-table
          v-loading="consultationsLoading"
          :data="myConsultations"
          style="width: 100%"
          empty-text="暂无咨询记录"
        >
          <el-table-column prop="title" label="咨询主题"></el-table-column>
          <el-table-column label="医生">
            <template slot-scope="scope">
              <div class="doctor-info">
                <el-avatar :size="30" :src="scope.row.doctorAvatar || '/img/default-avatar.png'"></el-avatar>
                <span>{{ scope.row.doctorName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
                {{ scope.row.status === 0 ? '进行中' : '已结束' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template slot-scope="scope">
              <el-button 
                size="mini" 
                type="primary" 
                @click="enterConsultation(scope.row.id)"
              >进入会话</el-button>
              <el-button 
                v-if="scope.row.status === 0"
                size="mini" 
                type="danger" 
                @click="endConsultation(scope.row.id)"
              >结束会话</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 医生列表 -->
      <div class="doctors-list">
        <div class="section-header">
          <h3>医生列表</h3>
        </div>
        <el-row :gutter="20" v-loading="doctorsLoading">
          <el-col :span="8" v-for="doctor in doctors" :key="doctor.id" class="doctor-card-container">
            <el-card class="doctor-card" shadow="hover">
              <div class="doctor-avatar">
                <el-avatar :size="80" :src="doctor.avatar || '/img/default-avatar.png'"></el-avatar>
              </div>
              <div class="doctor-info">
                <h4>{{ doctor.name }} <span class="doctor-title">{{ doctor.title }}</span></h4>
                <p class="specialty">{{ doctor.specialty }}</p>
                <p class="hospital">{{ doctor.hospital }}</p>
                <p class="description text-overflow">{{ doctor.description }}</p>
                <div class="actions">
                  <el-button type="primary" size="small" @click="viewDoctorDetail(doctor.id)">查看详情</el-button>
                  <el-button type="success" size="small" @click="startConsultation(doctor.id)">开始咨询</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="24" v-if="doctors.length === 0 && !doctorsLoading">
            <el-empty description="暂无医生数据"></el-empty>
          </el-col>
        </el-row>
        <div class="pagination" v-if="total > 0">
          <el-pagination
            @current-change="handleCurrentChange"
            :current-page.sync="currentPage"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
          </el-pagination>
        </div>
      </div>
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
  name: 'DoctorConsultation',
  data() {
    return {
      // 搜索条件
      searchQuery: {
        name: '',
        specialty: '',
        title: ''
      },
      // 医生数据
      doctors: [],
      // 我的咨询数据
      myConsultations: [],
      // 分页
      currentPage: 1,
      pageSize: 9,
      total: 0,
      // 加载状态
      doctorsLoading: false,
      consultationsLoading: false,
      // 专业领域选项
      specialtyOptions: [
        { value: '内科', label: '内科' },
        { value: '外科', label: '外科' },
        { value: '妇产科', label: '妇产科' },
        { value: '儿科', label: '儿科' },
        { value: '神经科', label: '神经科' },
        { value: '心理科', label: '心理科' },
        { value: '营养科', label: '营养科' },
        { value: '康复科', label: '康复科' }
      ],
      // 职称选项
      titleOptions: [
        { value: '主任医师', label: '主任医师' },
        { value: '副主任医师', label: '副主任医师' },
        { value: '主治医师', label: '主治医师' },
        { value: '住院医师', label: '住院医师' }
      ],
      // 咨询创建表单
      consultationForm: {
        title: '',
        doctorId: null
      },
      // 对话框显示状态
      consultationDialogVisible: false,
      // 创建状态
      creatingConsultation: false
    }
  },
  created() {
    // 加载医生列表和咨询列表
    this.loadDoctors()
    this.loadMyConsultations()
  },
  methods: {
    // 加载医生列表
    async loadDoctors() {
      this.doctorsLoading = true
      try {
        // 使用list接口获取医生列表
        const response = await this.$axios.get('/doctor/list')
        if (response.data.code === 200) {
          this.doctors = response.data.data || []
          this.total = this.doctors.length
        } else {
          this.$message.error(response.data.msg || '获取医生列表失败')
        }
      } catch (error) {
        console.error('获取医生列表出错', error)
        this.$message.error('获取医生列表出错')
      } finally {
        this.doctorsLoading = false
      }
    },
    // 加载我的咨询列表
    async loadMyConsultations() {
      this.consultationsLoading = true
      try {
        const response = await this.$axios.get('/consultation/list?role=1')
        if (response.data.code === 200) {
          this.myConsultations = response.data.data
        } else {
          this.$message.error(response.data.msg || '获取咨询列表失败')
        }
      } catch (error) {
        console.error('获取咨询列表出错', error)
        this.$message.error('获取咨询列表出错')
      } finally {
        this.consultationsLoading = false
      }
    },
    // 搜索医生
    searchDoctors() {
      this.currentPage = 1
      this.loadDoctors()
    },
    // 分页事件
    handleCurrentChange(val) {
      this.currentPage = val
      // 这里可以处理分页逻辑
    },
    // 开始咨询
    startConsultation(doctorId) {
      if (!doctorId) {
        this.$message.error('医生ID不能为空');
        return;
      }
      
      console.log('开始咨询医生ID:', doctorId, typeof doctorId);
      
      // 确保doctorId是数字类型
      this.consultationForm = {
        title: '',
        doctorId: Number(doctorId)
      }
      
      this.consultationDialogVisible = true
    },
    // 创建咨询
    async createConsultation() {
      if (!this.consultationForm.title) {
        this.$message.warning('请输入咨询主题')
        return
      }

      if (!this.consultationForm.doctorId) {
        this.$message.error('医生ID不能为空')
        return
      }
      
      this.creatingConsultation = true
      try {
        const consultationData = {
          doctorId: Number(this.consultationForm.doctorId),
          title: this.consultationForm.title
        }
        
        console.log('创建咨询数据:', JSON.stringify(consultationData))
        
        const response = await this.$axios.post('/consultation/create', consultationData)
        if (response.data.code === 200) {
          this.$message.success('咨询创建成功')
          this.consultationDialogVisible = false
          this.loadMyConsultations()
          
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
    // 结束会话
    async endConsultation(id) {
      try {
        const confirmed = await this.$confirm('确定要结束此次咨询吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        if (confirmed) {
          const response = await this.$axios.put(`/consultation/end/${id}`)
          if (response.data.code === 200) {
            this.$message.success('咨询已结束')
            this.loadMyConsultations()
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
    // 查看医生详情
    viewDoctorDetail(id) {
      this.$router.push(`/doctor-detail/${id}`)
    },
    // 日期格式化
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    }
  }
}
</script>

<style scoped lang="scss">
.doctor-consultation {
  padding: 20px;
  
  .page-header {
    margin-bottom: 30px;
    
    h2 {
      font-size: 24px;
      margin-bottom: 10px;
      color: #15559a;
    }
    
    p {
      color: #666;
    }
  }
  
  .page-content {
    .filter-container {
      display: flex;
      gap: 15px;
      margin-bottom: 30px;
      flex-wrap: wrap;
      
      .search-input {
        width: 200px;
      }
    }
    
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
      
      h3 {
        font-size: 18px;
        color: #333;
      }
    }
    
    .my-consultations {
      margin-bottom: 40px;
      
      .doctor-info {
        display: flex;
        align-items: center;
        gap: 10px;
      }
    }
    
    .doctors-list {
      .doctor-card-container {
        margin-bottom: 20px;
      }
      
      .doctor-card {
        display: flex;
        flex-direction: column;
        height: 100%;
        
        .doctor-avatar {
          display: flex;
          justify-content: center;
          margin-bottom: 15px;
        }
        
        .doctor-info {
          text-align: center;
          
          h4 {
            margin: 0 0 10px;
            font-size: 16px;
            
            .doctor-title {
              color: #15559a;
              font-size: 14px;
              margin-left: 5px;
            }
          }
          
          .specialty {
            color: #666;
            margin: 5px 0;
          }
          
          .hospital {
            color: #333;
            margin: 5px 0;
            font-weight: 500;
          }
          
          .description {
            color: #888;
            margin: 10px 0;
            font-size: 13px;
            height: 38px;
            overflow: hidden;
          }
          
          .actions {
            margin-top: 15px;
            display: flex;
            justify-content: center;
            gap: 10px;
          }
        }
      }
    }
    
    .pagination {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }
}

.text-overflow {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style> 