<template>
    <el-row style="background-color: #FFFFFF;padding: 10px 0;border-radius: 5px;">
        <el-row style="padding: 10px;margin-left: 5px;">
            <el-row>
                <el-date-picker size="small" style="margin-left: 10px;width: 220px;" v-model="searchTime"
                    type="daterange" range-separator="至" start-placeholder="消息开始" end-placeholder="消息结束">
                </el-date-picker>
                <el-input size="small" style="width: 188px;margin-left: 5px;margin-right: 6px;"
                    v-model="messageQueryDto.content" placeholder="消息内容" clearable @clear="handleFilterClear">
                    <el-button slot="append" @click="handleFilter" icon="el-icon-search"></el-button>
                </el-input>
                <span style="float: right;">
                    <el-button size="small"
                        style="background-color: rgb(96, 98, 102);color: rgb(247,248,249);border: none;"
                        class="customer" type="info" @click="allMessagePush"><i
                            class="el-icon-plus"></i>定向推送</el-button>
                </span>
            </el-row>
        </el-row>
        <el-row style="margin: 0 20px;border-top: 1px solid rgb(245,245,245);">
            <el-table :data="tableData" style="width: 100%">
                <el-table-column prop="name" width="98" label="读取状态">
                    <template slot-scope="scope">
                        <span>{{ scope.row.isRead ? '已读' : '未读' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="messageType" width="148" label="消息类型">
                    <template slot-scope="scope">
                        <span v-if="scope.row.messageType === 1">评论回复</span>
                        <span v-else-if="scope.row.messageType === 2">评论点赞</span>
                        <span v-else-if="scope.row.messageType === 3">指标提醒</span>
                        <span v-else>系统通知</span>
                    </template>
                </el-table-column>
                <el-table-column prop="receiverName" width="108" label="接收者"></el-table-column>
                <el-table-column prop="content" label="消息体"></el-table-column>
                <el-table-column prop="createTime" width="168" label="发送时间"></el-table-column>
                <el-table-column label="操作" width="88">
                    <template slot-scope="scope">
                        <span class="text-button" @click="handleDelete(scope.row)">删除</span>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination style="margin: 20px 0;" @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="currentPage" :page-sizes="[10, 20]" :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="totalItems"></el-pagination>
        </el-row>
        <!-- 全站消息推送 -->
        <el-dialog :show-title="false" :show-close="false" :visible.sync="dialogMessageOperation" width="24%">
            <p style="padding: 20px 0  0 20px;">消息推送</p>
            <div style="padding:0 20px;">
                <el-row>
                    <span class="dialog-hover">消息内容</span>
                    <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4 }" placeholder="消息内容"
                        v-model="messageContent">
                    </el-input>
                </el-row>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button size="small" style="background-color: rgb(43, 121, 203);border: none;" class="customer"
                    type="info" @click="messagePushOperation">确定推送</el-button>
                <el-button class="customer" size="small" style="background-color: rgb(241, 241, 241);border: none;"
                    @click="dialogMessageOperation = false">取消</el-button>
            </span>
        </el-dialog>
    </el-row>
</template>

<script>
export default {
    data() {
        return {
            data: {},
            filterText: '',
            currentPage: 1,
            pageSize: 10,
            totalItems: 0,
            tableData: [],
            searchTime: [],
            selectedRows: [],
            messageQueryDto: {}, // 搜索条件
            messsageContent: '',
            messageTypeList: [],
            dialogMessageOperation: false, // 开关变量，控制消息的对话框显示与否
            messageContent: ''
        };
    },
    created() {
        this.fetchFreshData();
        this.loadAllMessageType();
    },
    methods: {
        messagePushOperation() {
            const message = {
                content: this.messageContent
            }
            this.$axios.post('/message/systemInfoUsersSave', message).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.$notify({
                        duration: 2000,
                        title: '推送操作',
                        message: '成功',
                        type: 'success'
                    });
                    this.dialogMessageOperation = false;
                    this.messageContent = '';
                }
            })
        },
        allMessagePush() {
            this.dialogMessageOperation = true;
        },
        loadAllMessageType() {
            this.$axios.get('/message/types').then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.messageTypeList = data.data;
                }
            })
        },
        // 批量删除数据
        async batchDelete() {
            if (!this.selectedRows.length) {
                this.$message(`未选中任何数据`);
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: '删除消息数据',
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.selectedRows.map(entity => entity.id);
                    const response = await this.$axios.post(`/message/batchDelete`, ids);
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
                    console.error(`消息信息删除异常：`, e);
                }
            }
        },
        resetQueryCondition() {
            this.messageQueryDto = {};
            this.searchTime = [];
            this.fetchFreshData();
        },
        async fetchFreshData() {
            try {
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
                    ...this.messageQueryDto
                };
                const response = await this.$axios.post('/message/query', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                console.error('查询消息信息异常:', error);
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