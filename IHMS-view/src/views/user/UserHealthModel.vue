<template>
    <div>
        <div style="border-radius: 5px;padding: 20px 0 60px 0;width: 100%;background-color: #fafafa;">
            <div style="height: 100px;line-height: 100px;text-align: center;font-size: 24px;">
                健康生活，健康人生，从此刻开始！
            </div>
            <div style="height: 50px;line-height: 50px;text-align: center;font-size: 30px;font-weight: bolder;">
                每一点改变，都值得被记录。
                <span @click="toRecord"
                    style="cursor: pointer;;padding: 5px 10px;background-color: #000;border-radius: 5px;color: #fff;">
                    前去记录
                    <i class="el-icon-right"></i>
                </span>
            </div>
        </div>
        <div style="padding: 30px 0;">
            <div style="margin: 20px 0;">
                <!-- 选择具体的指标模型 -->
                <el-select size="small" @change="modelChange" v-model="userHealthQueryDto.healthModelConfigId"
                    placeholder="请选择">
                    <el-option v-for="model in usersHealthModelConfig" :key="model.id" :label="model.name"
                        :value="model.id">
                    </el-option>
                </el-select>
            </div>
            <div>
                <LineChart @on-selected="onSelectedTime" height="500px" tag="" :values="values" :date="dates" />
            </div>
        </div>
        <div>
            <h2 style="padding-left: 20px;border-left: 2px solid rgb(43, 121, 203);">健康指标数据</h2>
            <el-row style="padding: 10px;margin-left: 10px;">
                <el-row style="display: flex;justify-content: left;align-items: center;gap: 10px;">
                    <el-select size="small" @change="fetchFreshData" v-model="healthModelConfigId" placeholder="请选择模型项">
                        <el-option :key="null" label="全部" :value="null">
                        </el-option>
                        <el-option v-for="model in usersHealthModelConfig" :key="model.id" :label="model.name"
                            :value="model.id">
                        </el-option>
                    </el-select>
                    <el-date-picker size="small" @change="timeChange" style="width: 220px;" v-model="searchTime"
                        type="daterange" range-separator="至" start-placeholder="记录开始" end-placeholder="记录结束">
                    </el-date-picker>
                </el-row>
            </el-row>
            <el-row style="margin: 0 20px;border-top: 1px solid rgb(245,245,245);">
                <el-table row-key="id" @selection-change="handleSelectionChange" :data="tableData">
                    <el-table-column prop="name" label="指标项">
                        <template slot-scope="scope">
                            <span><i class="el-icon-paperclip" style="margin-right: 3px;"></i>{{ scope.row.name
                                }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="value" width="148" label="数值" sortable>
                        <template slot-scope="scope">
                            <span style="font-weight: 800;">{{ scope.row.value }}&nbsp;{{ scope.row.unit }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="symbol" width="108" label="模型符号"></el-table-column>
                    <!-- <el-table-column prop="valueRange" width="128" label="阈值"></el-table-column> -->
                    <el-table-column prop="name" width="88" label="状态">
                        <template slot-scope="scope">
                            <i v-if="!statusCheck(scope.row)" style="margin-right: 5px;" class="el-icon-warning"></i>
                            <i v-else style="margin-right: 5px;color: rgb(253, 199, 50);" class="el-icon-success"></i>
                            <el-tooltip v-if="!statusCheck(scope.row)" class="item" effect="dark"
                                content="异常指标，提醒用户及时处理" placement="bottom-end">
                                <span style="text-decoration: underline;text-decoration-style: dashed;">异常</span>
                            </el-tooltip>
                            <span v-else>正常</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" width="178" label="记录时间" sortable></el-table-column>
                    <el-table-column label="操作" width="80">
                        <template slot-scope="scope">
                            <span class="text-button" @click="handleDelete(scope.row)">删除</span>
                        </template>
                    </el-table-column>
                </el-table>
                <el-pagination style="margin: 20px 0;" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20]"
                    :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                    :total="totalItems"></el-pagination>
            </el-row>
        </div>
    </div>
</template>
<script>
import LineChart from '@/components/LineChart.vue';
export default {
    components: { LineChart },
    data() {
        return {
            usersHealthModelConfig: [],
            modelConfigList: [],
            userHealthQueryDto: {}, // 查询的参数
            values: [],
            dates: [],
            tableData: [],
            selectedRows: [],
            currentPage: 1,
            pageSize: 20,
            totalItems: 0,
            searchTime: [],
            healthModelConfigId: null
        }
    },
    created() {
        this.loadHealthModelConfig();
        this.fetchFreshData();
    },
    methods: {
        timeChange() {
            this.current = 1;
            this.fetchFreshData();
        },
        handleDelete(row) {
            this.selectedRows.push(row);
            this.batchDelete();
        },
        // 处理用户输入的值，是正常的还是异常的，给个状态
        statusCheck(data) {
            // 用户输入的值
            const inputValue = data.value;
            // 正常值范围
            const valueRange = data.valueRange;
            if (valueRange !== null && inputValue !== null) {
                const aryValueRange = valueRange.split(',');
                const minValue = aryValueRange[0];
                const maxValue = aryValueRange[1];
                return (Number(inputValue) > Number(minValue) && Number(inputValue) < Number(maxValue))
            }
        },
        // 批量删除数据
        async batchDelete() {
            if (!this.selectedRows.length) {
                this.$message(`未选中任何数据`);
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: '删除健康记录数据',
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.selectedRows.map(entity => entity.id);
                    const response = await this.$axios.post(`/user-health/batchDelete`, ids);
                    if (response.data.code === 200) {
                        this.$swal.fire({
                            title: '删除提示',
                            text: response.data.msg,
                            icon: 'success',
                            showConfirmButton: false,
                            timer: 2000,
                        });
                        this.fetchFreshData();
                        return;
                    }
                } catch (e) {
                    this.$swal.fire({
                        title: '错误提示',
                        text: e,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: 2000,
                    });
                    console.error(`用户健康记录信息删除异常：`, e);
                }
            }
        },
        // 点击查询之后，执行的一个函数
        handleFilter() {
            this.currentPage = 1;
            this.fetchFreshData();
        },
        // 加载用户自己的健康记录数据
        async fetchFreshData() {
            try {
                let startTime = null;
                let endTime = null;
                if (this.searchTime != null && this.searchTime.length === 2) {
                    const [startDate, endDate] = await Promise.all(this.searchTime.map(date => date.toISOString()));
                    startTime = `${startDate.split('T')[0]}T00:00:00`;
                    endTime = `${endDate.split('T')[0]}T23:59:59`;
                }
                const userInfo = sessionStorage.getItem('userInfo');
                const userEntitySave = JSON.parse(userInfo);
                // 请求参数
                const params = {
                    current: this.currentPage,
                    size: this.pageSize,
                    startTime: startTime,
                    endTime: endTime,
                    healthModelConfigId: this.healthModelConfigId,
                    userId: userEntitySave.id
                };
                const response = await this.$axios.post('/user-health/query', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                console.error('查询用户健康记录信息异常:', error);
            }
        },
        // 点击输入框里面的清除按钮
        handleFilterClear() {
            this.filterText = '';
            this.handleFilter();
        },
        // 多选框选中
        handleSelectionChange(selection) {
            this.selectedRows = selection;
        },
        // 条件重置
        resetQueryCondition() {
            this.searchTime = [];
            this.healthModelConfigId = null;
            this.fetchFreshData();
        },
        // 当前页切换
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.fetchFreshData();
        },
        // 当前页跳转
        handleCurrentChange(val) {
            this.currentPage = val;
            this.fetchFreshData();
        },
        // 查询用户具体记录的值，指定时间范围内
        loadUserModelHavaRecord() {
            // 往后端什么？
            this.$axios.get(`/user-health/timeQuery/${this.userHealthQueryDto.healthModelConfigId}/${this.userHealthQueryDto.time}`).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    // 拿到的数据，要做可视化处理
                    this.values = data.data.map(entity => entity.value).reverse();
                    this.dates = data.data.map(entity => entity.createTime).reverse();
                }
            })
        },
        // 模型选中方法
        modelChange(day) {
            this.loadUserModelHavaRecord();
        },
        // 表格里面的具体模型选中
        modelUserChange() {
            // 如果想用户直接选中，数据直接回来，就要用到这一个方法
            this.fetchFreshData();
        },
        // 查询用户自己配置的模型以及全局模型
        loadHealthModelConfig() {
            this.$axios.post("/health-model-config/modelList").then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.usersHealthModelConfig = data.data;
                    this.modelConfigList = data.data;
                    this.defaultLoad();
                }
            })
        },
        // 默认加载
        defaultLoad() {
            this.userHealthQueryDto.healthModelConfigId = this.usersHealthModelConfig[0].id;
            this.userHealthQueryDto.time = 365;
            this.loadUserModelHavaRecord();
        },
        // 折线图选择指定事件范围之后，返回的一个回调
        onSelectedTime(time) {
            this.userHealthQueryDto.time = time;
            this.loadUserModelHavaRecord();
        },
        // 组件里面返回的数据
        timeSelected() {

        },
        toRecord() {
            this.$router.push('/record');
        },
    }
};
</script>
<style scoped lang="scss">
.status-success {
    display: inline-block;
    padding: 1px 5px;
    border-radius: 2px;
    background-color: rgb(201, 237, 249);
    color: rgb(111, 106, 196);
    font-size: 12px;
}

.status-error {
    display: inline-block;
    padding: 1px 5px;
    border-radius: 2px;
    background-color: rgb(233, 226, 134);
    color: rgb(131, 138, 142);
    color: rgb(111, 106, 196);
    font-size: 12px;
}
</style>