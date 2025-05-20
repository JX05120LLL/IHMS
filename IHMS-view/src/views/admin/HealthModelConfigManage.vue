<template>
    <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 5px;">
        <el-row style="padding: 10px;margin-left: 5px;">
            <el-row>
                <el-input size="small" style="width: 188px;margin-left: 5px;margin-right: 6px;"
                    v-model="healthModelConfigQueryDto.name" placeholder="配置名" clearable @clear="handleFilterClear">
                    <el-button slot="append" @click="handleFilter" icon="el-icon-search"></el-button>
                </el-input>
                <span style="float: right;">
                    <el-button size="small"
                        style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;"
                        class="customer" type="info" @click="add()"><i class="el-icon-plus"></i>新增模型</el-button>
                </span>
            </el-row>
        </el-row>
        <el-row style="margin: 0 20px;border-top: 1px solid rgb(245,245,245);">
            <el-table row-key="id" @selection-change="handleSelectionChange" :data="tableData" style="width: 100%">
                <el-table-column prop="cover" width="80" label="模型图">
                    <template slot-scope="scope">
                        <img :src="scope.row.cover" style="width: 30px;height: 30px;border-radius: 5px;" />
                    </template>
                </el-table-column>
                <el-table-column prop="name" width="218" label="模型名"></el-table-column>
                <el-table-column prop="isGlobal" label="权限" width="128">
                    <template slot-scope="scope">
                        <span>{{ scope.row.isGlobal ? '全局模型' : '私有模型' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="userName" width="108" label="配置用户"></el-table-column>
                <el-table-column prop="valueRange" width="128" label="值范围"></el-table-column>
                <el-table-column prop="unit" width="88" label="单位"></el-table-column>
                <el-table-column prop="symbol" width="88" label="符号"></el-table-column>
                <el-table-column prop="detail" label="模型介绍"></el-table-column>
                <el-table-column label="操作" width="120">
                    <template slot-scope="scope">
                        <span class="text-button" @click="handleEdit(scope.row)">编辑</span>
                        <span class="text-button" @click="handleDelete(scope.row)">删除</span>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination style="margin: 20px 0;" @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="currentPage" :page-sizes="[10, 20]" :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="totalItems"></el-pagination>
        </el-row>
        <el-dialog :show-close="false" :visible.sync="dialogUserOperaion" width="26%">
            <div slot="title">
                <p class="dialog-title">{{ !isOperation ? '健康模型新增' : '健康模型修改' }}</p>
            </div>
            <div style="padding:0 20px;">
                <p>*图标</p>
                <!-- 图标 -->
                <el-row style="margin-top: 10px;">
                    <el-upload class="avatar-uploader" action="/api/personal-heath/v1.0/file/upload"
                        :show-file-list="false" :on-success="handleAvatarSuccess">
                        <img v-if="data.cover" :src="data.cover" style="height: 64px;width: 64px;">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-row>
                <!-- 配置名 -->
                <el-row style="padding: 0 10px 0 0;">
                    <p>
                        <span class="modelName">*配置名</span>
                    </p>
                    <input class="input-title" v-model="data.name" placeholder="请输入">
                </el-row>
                <!-- 单位 -->
                <el-row style="padding: 0 10px 0 0;">
                    <p style="font-size: 12px;padding: 3px 0;">
                        <span class="modelName">*单位</span>
                    </p>
                    <input class="input-title" v-model="data.unit" placeholder="请输入">
                </el-row>
                <!-- 符号 -->
                <el-row style="padding: 0 10px 0 0;">
                    <p style="font-size: 12px;padding: 3px 0;">
                        <span class="modelName">*符号</span>
                    </p>
                    <input class="input-title" v-model="data.symbol" placeholder="请输入">
                </el-row>
                <!-- 正常值 -->
                <el-row style="padding: 0 20px 0 0;">
                    <p style="font-size: 12px;padding: 3px 0;">
                        <span class="modelName">*阈值（格式：最小值,最大值）</span>
                    </p>
                    <input class="input-title" v-model="data.valueRange" placeholder="请输入">
                </el-row>
                <!-- 简介 -->
                <el-row style="padding: 0 10px 0 0;">
                    <p style="font-size: 12px;padding: 3px 0;">
                        <span class="modelName">*简介</span>
                    </p>
                    <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 3 }" placeholder="简介"
                        v-model="data.detail">
                    </el-input>
                </el-row>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button size="small" v-if="!isOperation" style="background-color: rgb(43, 121, 203);border: none;"
                    class="customer" type="info" @click="addOperation">新增</el-button>
                <el-button size="small" v-else style="background-color: rgb(43, 121, 203);border: none;"
                    class="customer" type="info" @click="updateOperation">修改</el-button>
                <el-button class="customer" size="small" style="background-color: rgb(241, 241, 241);border: none;"
                    @click="cannel()">取消</el-button>
            </span>
        </el-dialog>
    </el-row>
</template>

<script>
export default {
    data() {
        return {
            data: { cover: '' },
            filterText: '',
            currentPage: 1,
            pageSize: 10,
            totalItems: 0,
            dialogUserOperaion: false, // 开关
            isOperation: false, // 开关-标识新增或修改
            tableData: [],
            searchTime: [],
            selectedRows: [],
            status: null,
            healthModelConfigQueryDto: {}, // 搜索条件
            messsageContent: '',
            tagsList: [],
            valuesRange: [10, 50]
        };
    },
    created() {
        this.fetchFreshData();
    },
    methods: {
        handleAvatarSuccess(res, file) {
            this.$notify({
                duration: 2000,
                title: '图标上传',
                message: res.code === 200 ? '成功' : '异常',
                type: res.code === 200 ? 'success' : 'error'
            });
            this.data.cover = res.data;
        },
        // 多选框选中
        handleSelectionChange(selection) {
            this.selectedRows = selection;
        },
        // 批量删除数据
        async batchDelete() {
            if (!this.selectedRows.length) {
                this.$message(`未选中任何数据`);
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: '删除健康模型数据',
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.selectedRows.map(entity => entity.id);
                    const response = await this.$axios.post(`/health-model-config/batchDelete`, ids);
                    if (response.data.code === 200) {
                        this.$notify({
                            duration: 2000,
                            title: '删除操作',
                            message: '成功',
                            type: 'success'
                        });
                        this.fetchFreshData();
                        return;
                    }
                } catch (e) {
                    console.error(`健康模型信息删除异常：`, e);
                }
            }
        },
        resetQueryCondition() {
            this.healthModelConfigQueryDto = {};
            this.searchTime = [];
            this.fetchFreshData();
        },
        // 修改信息
        async updateOperation() {
            this.$axios.put('/health-model-config/update', this.data).then(res => {
                if (res.data.code === 200) {
                    this.cannel();
                    this.fetchFreshData();
                    this.$notify({
                        duration: 2000,
                        title: '修改操作',
                        message: '成功',
                        type: 'success'
                    });
                }
            }).catch(error => {
                console.log('模型修改异常=>', error);
            })
        },
        cannel() {
            this.dialogUserOperaion = false;
            this.isOperation = false;
            this.data = {};
            this.valueRange = null;
        },
        // 模型新增
        addOperation() {
            this.$axios.post('/health-model-config/config/save', this.data).then(res => {
                if (res.data.code === 200) {
                    this.cannel();
                    this.fetchFreshData();
                    this.$notify({
                        duration: 2000,
                        title: '新增操作',
                        message: '成功',
                        type: 'success'
                    });
                }
            }).catch(error => {
                console.log('模型新增异常=>', error);
            })
        },
        // 模型查询
        async fetchFreshData() {
            try {
                this.tableData = [];
                let startTime = null;
                let endTime = null;
                if (this.searchTime != null && this.searchTime.length === 2) {
                    const [startDate, endDate] = await Promise.all(this.searchTime.map(date => date.toISOString()));
                    startTime = `${startDate.split('T')[0]}T00:00:00`;
                    endTime = `${endDate.split('T')[0]}T23:59:59`;
                }
                // 请求参数
                const params = {
                    current: this.currentPage,
                    size: this.pageSize,
                    startTime: startTime,
                    endTime: endTime,
                    ...this.healthModelConfigQueryDto
                };
                const response = await this.$axios.post('/health-model-config/query', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                console.error('查询健康模型信息异常:', error);
            }
        },
        add() {
            this.dialogUserOperaion = true;
        },
        handleFilter() {
            this.currentPage = 1;
            this.fetchFreshData();
        },
        handleFilterClear() {
            this.filterText = '';
            this.handleFilter();
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.fetchFreshData();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.fetchFreshData();
        },
        handleEdit(row) {
            this.dialogUserOperaion = true;
            this.isOperation = true;
            this.data = { ...row }
        },
        handleDelete(row) {
            this.selectedRows.push(row);
            this.batchDelete();
        }
    },
};
</script>
<style scoped lang="scss"></style>