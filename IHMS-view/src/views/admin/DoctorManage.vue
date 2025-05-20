<template>
  <div class="doctor-manage">
    <div class="page-header">
      <h2>医生管理</h2>
      <el-button type="primary" @click="openAddDialog">添加医生</el-button>
    </div>

    <div class="search-bar">
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
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <div class="doctor-table">
      <el-table
        v-loading="loading"
        :data="doctors"
        style="width: 100%"
        border
      >
        <el-table-column type="index" width="60"></el-table-column>
        <el-table-column prop="name" label="姓名" width="120"></el-table-column>
        <el-table-column label="头像" width="90">
          <template slot-scope="scope">
            <el-avatar :size="40" :src="scope.row.avatar || '/img/default-avatar.png'"></el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="specialty" label="专业领域" width="140"></el-table-column>
        <el-table-column prop="title" label="职称" width="140"></el-table-column>
        <el-table-column prop="hospital" label="所属医院" min-width="180"></el-table-column>
        <el-table-column prop="description" label="简介" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column label="状态" width="120">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              active-color="#13ce66"
              inactive-color="#ff4949"
              active-text="接诊"
              inactive-text="停诊"
              @change="handleStatusChange(scope.row)"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template slot-scope="scope">
            <el-button 
              size="mini" 
              type="primary" 
              @click="handleEdit(scope.row)"
            >编辑</el-button>
            <el-button 
              size="mini" 
              type="danger" 
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
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

    <!-- 添加/编辑医生对话框 -->
    <el-dialog :title="dialogType === 'add' ? '添加医生' : '编辑医生'" :visible.sync="dialogVisible" width="50%">
      <el-form :model="doctorForm" :rules="rules" ref="doctorForm" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="doctorForm.name"></el-input>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/personal-heath/v1.0/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="doctorForm.avatar" :src="doctorForm.avatar" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="专业领域" prop="specialty">
          <el-select v-model="doctorForm.specialty" placeholder="请选择专业领域">
            <el-option
              v-for="item in specialtyOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-select v-model="doctorForm.title" placeholder="请选择职称">
            <el-option
              v-for="item in titleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属医院" prop="hospital">
          <el-input v-model="doctorForm.hospital"></el-input>
        </el-form-item>
        <el-form-item label="个人简介" prop="description">
          <el-input type="textarea" v-model="doctorForm.description" :rows="4"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'DoctorManage',
  data() {
    return {
      // 医生列表
      doctors: [],
      // 搜索条件
      searchQuery: {
        name: '',
        specialty: '',
        title: ''
      },
      // 分页
      currentPage: 1,
      pageSize: 10,
      total: 0,
      // 加载状态
      loading: false,
      // 对话框
      dialogVisible: false,
      dialogType: 'add', // add or edit
      submitting: false,
      // 表单数据
      doctorForm: {
        id: null,
        name: '',
        avatar: '',
        specialty: '',
        title: '',
        hospital: '',
        description: ''
      },
      // 表单规则
      rules: {
        name: [
          { required: true, message: '请输入医生姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        specialty: [
          { required: true, message: '请选择专业领域', trigger: 'change' }
        ],
        title: [
          { required: true, message: '请选择职称', trigger: 'change' }
        ],
        hospital: [
          { required: true, message: '请输入所属医院', trigger: 'blur' }
        ]
      },
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
      ]
    }
  },
  created() {
    this.loadDoctors()
  },
  methods: {
    // 加载医生列表
    async loadDoctors() {
      this.loading = true
      try {
        // 构建查询参数
        const params = { 
          ...this.searchQuery,
          current: this.currentPage,
          size: this.pageSize
        }
        
        const response = await this.$axios.post('/doctor/query', params)
        if (response.data.code === 200) {
          // 检查返回的数据格式
          if (response.data.data && response.data.data.records) {
            // 分页数据
            this.doctors = response.data.data.records
            this.total = response.data.data.total
          } else {
            // 旧格式数据，直接使用返回的数组
            this.doctors = response.data.data || []
            this.total = this.doctors.length
          }
        } else {
          this.$message.error(response.data.msg || '获取医生列表失败')
        }
      } catch (error) {
        console.error('获取医生列表出错', error)
        this.$message.error('获取医生列表出错')
      } finally {
        this.loading = false
      }
    },
    // 搜索医生
    searchDoctors() {
      this.currentPage = 1
      this.loadDoctors()
    },
    // 重置搜索
    resetSearch() {
      this.searchQuery = {
        name: '',
        specialty: '',
        title: ''
      }
      this.currentPage = 1
      this.loadDoctors()
    },
    // 分页处理
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadDoctors()
    },
    // 打开添加对话框
    openAddDialog() {
      this.dialogType = 'add'
      this.doctorForm = {
        id: null,
        name: '',
        avatar: '',
        specialty: '',
        title: '',
        hospital: '',
        description: ''
      }
      this.dialogVisible = true
    },
    // 处理编辑
    handleEdit(row) {
      this.dialogType = 'edit'
      this.doctorForm = { ...row }
      this.dialogVisible = true
    },
    // 处理删除
    async handleDelete(row) {
      try {
        const confirmed = await this.$confirm(`确定要删除医生 ${row.name} 吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        if (confirmed) {
          const response = await this.$axios.delete(`/doctor/delete/${row.id}`)
          if (response.data.code === 200) {
            this.$message.success('删除成功')
            this.loadDoctors()
          } else {
            this.$message.error(response.data.msg || '删除失败')
          }
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除医生出错', error)
          this.$message.error('删除医生出错')
        }
      }
    },
    // 处理头像上传成功
    handleAvatarSuccess(res) {
      if (res.code === 200) {
        this.doctorForm.avatar = res.data
        this.$message.success('头像上传成功')
      } else {
        this.$message.error('头像上传失败')
      }
    },
    // 头像上传前检查
    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      
      if (!isImage) {
        this.$message.error('头像只能是图片格式!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('头像大小不能超过 2MB!')
        return false
      }
      return true
    },
    // 提交表单
    submitForm() {
      this.$refs.doctorForm.validate(async (valid) => {
        if (!valid) return
        
        this.submitting = true
        try {
          let response
          if (this.dialogType === 'add') {
            // 添加医生
            response = await this.$axios.post('/doctor/add', this.doctorForm)
          } else {
            // 更新医生
            response = await this.$axios.put('/doctor/update', this.doctorForm)
          }
          
          if (response.data.code === 200) {
            this.$message.success(this.dialogType === 'add' ? '添加成功' : '更新成功')
            this.dialogVisible = false
            this.loadDoctors()
          } else {
            this.$message.error(response.data.msg || (this.dialogType === 'add' ? '添加失败' : '更新失败'))
          }
        } catch (error) {
          console.error(this.dialogType === 'add' ? '添加医生出错' : '更新医生出错', error)
          this.$message.error(this.dialogType === 'add' ? '添加医生出错' : '更新医生出错')
        } finally {
          this.submitting = false
        }
      })
    },
    // 处理状态切换
    async handleStatusChange(row) {
      try {
        const response = await this.$axios.put(`/doctor/status/${row.id}/${row.status}`)
        if (response.data.code === 200) {
          this.$message.success(row.status === 1 ? '医生已开始接诊' : '医生已停止接诊')
        } else {
          // 如果更新失败，恢复之前的状态值
          row.status = row.status === 1 ? 0 : 1
          this.$message.error(response.data.msg || '状态更新失败')
        }
      } catch (error) {
        // 如果发生错误，恢复之前的状态值
        row.status = row.status === 1 ? 0 : 1
        console.error('更新医生状态出错', error)
        this.$message.error('更新医生状态出错')
      }
    }
  }
}
</script>

<style scoped lang="scss">
.doctor-manage {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
    }
  }
  
  .search-bar {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
    
    .search-input {
      width: 200px;
    }
  }
  
  .doctor-table {
    margin-bottom: 20px;
    
    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
}

.avatar-uploader {
  .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    
    &:hover {
      border-color: #409EFF;
    }
  }
  
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
  }
  
  .avatar {
    width: 100px;
    height: 100px;
    display: block;
  }
}
</style> 