<template>
    <div>
        <div style="padding: 0 50px;">
            <div>
                <p style="font-size: 24px;padding: 10px 0;font-weight: bolder;">
                    <span @click="goBack" style="cursor: pointer;;display: inline-block;padding: 0 20px 0 0;">
                        <i class="el-icon-arrow-left"></i>
                        返回首页
                    </span>
                    健康记录
                </p>
            </div>
        </div>
        <div style="height: 6px;background-color: rgb(248, 248, 248);"></div>
        <div style="padding: 10px 50px;">
            <el-row>
                <el-col :span="6" style="border-right: 1px solid #f1f1f1;min-height: calc(100vh - 250px);">
                    <el-tabs v-model="activeName" @tab-click="handleClick" style="margin-right: 40px;">
                        <el-tab-pane label="全局模型" name="first"></el-tab-pane>
                        <el-tab-pane label="我的模型" name="second"></el-tab-pane>
                    </el-tabs>
                    <div style="padding: 20px 0 30px 0;">
                        <span @click="addModel"
                            style="cursor: pointer;;padding: 10px 20px;background-color: #000;border-radius: 5px;color: #fff;">
                            新增模型
                            <i class="el-icon-right"></i>
                        </span>
                    </div>
                    <div>
                        <span style="margin-right: 20px;">配置名</span>
                        <el-input style="width: 148px;" v-model="userHealthModel.name" placeholder="输入处" clearable
                            @clear="handleFilterClear">
                        </el-input>
                        <el-button class="customer"
                            style="margin-left: 20px;background-color: rgb(43, 121, 203);border: none;" type="primary"
                            @click="searModel">搜索</el-button>
                    </div>
                    <div
                        style="padding: 10px 6px;margin-right: 40px;height: 500px;overflow-y: scroll;overflow-x: hidden;">
                        <div @click="modelSelected(model)" class="item-model" v-for="(model, index) in modelList"
                            :key="index">
                            <el-tooltip class="item" effect="dark" :content="'该项配置【' + model.name + '】，点击即可选中'"
                                placement="bottom">
                                <el-row style="padding: 20px 0;">
                                    <el-col :span="4">
                                        <img :src="model.cover" style="width: 50px;height: 50px;margin-top: 5px;">
                                    </el-col>
                                    <el-col :span="20">
                                        <div style="padding: 0 10px;">
                                            <div style="font-size: 24px;font-weight: bolder;">{{ model.name }}</div>
                                            <div style="font-size: 14px;margin-top: 5px;">
                                                <span>{{ model.unit }}</span>
                                                <span style="margin-left: 10px;">{{ model.symbol }}</span>
                                                <span @click="updateModel(model)" v-if="!model.isGlobal"
                                                    style="margin-left: 10px;color: #333;">修改</span>
                                                <span @click="deleteModel(model)" v-if="!model.isGlobal"
                                                    style="margin-left: 10px;color: red;">删除</span>
                                            </div>
                                        </div>
                                    </el-col>
                                </el-row>
                            </el-tooltip>
                        </div>
                    </div>
                </el-col>
                <el-col :span="18">
                    <div style="padding: 0 150px;box-sizing: border-box;">
                        <div style="padding: 15px 0;font-size:24px;">
                            数据录入面板
                            <span @click="clearData" style="font-size: 14px;margin-left: 20px;">重置</span>
                        </div>
                        <el-row>
                            <el-row v-if="selectedModel.length === 0">
                                <el-empty description="快选中模型记录吧"></el-empty>
                            </el-row>
                            <el-row>
                                <el-col :span="12" v-for="(model, index) in selectedModel" :key="index">
                                    <h3>{{ model.name }}({{ model.unit }})</h3>
                                    <input type="text" v-model="model.value" class="input-model"
                                        :placeholder="'正常值范围：' + model.valueRange">
                                </el-col>
                            </el-row>
                        </el-row>

                    </div>
                    <div style="padding: 50px 150px;">
                        <span @click="toRecord"
                            style="cursor: pointer;padding: 10px 20px;background-color: #000;border-radius: 5px;color: #fff;">
                            立即记录
                            <i class="el-icon-right"></i>
                        </span>
                    </div>
                </el-col>
            </el-row>
        </div>
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
    </div>
</template>
<script>
import Logo from '@/components/Logo';
export default {
    components: { Logo },
    data() {
        return {
            data: { cover: '' },
            userInfo: {},
            modelList: [],
            activeName: 'first',
            userHealthModel: { isGlobal: true },
            dialogUserOperaion: false,
            isOperation: false,
            userId: null,
            selectedModel: [],
        };
    },
    created() {
        this.getUserInfo();
        this.getAllModelConfig();
        this.getUser();
    },
    methods: {
        async clearData() {
            const confirmed = await this.$swalConfirm({
                title: "重置数据？",
                text: `重置之后需要重新输入,是否继续`,
                icon: 'warning',
            });
            if (confirmed) {
                this.selectedModel = [];
            }
        },
        cannel(){
            this.data = {};
            this.dialogUserOperaion = false;
            this.isOperation = false;
            this.cover = '';
        },
        // 发送修改请求
        updateOperation() {
            this.$axios.put('/health-model-config/update', this.data).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.dialogUserOperaion = false;
                    this.isOperation = false;
                    this.data = {};
                    this.$swal.fire({
                        title: '模型修改',
                        text: '模型修改成功',
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1000,
                    });
                    // 继续加载最新的模型数据
                    this.getAllModelConfig();
                }
            })
        },
        // 修改自己配置的模型
        updateModel(model) {
            this.data = model;
            this.dialogUserOperaion = true;
            this.isOperation = true;
        },
        // 删除自己配置的模型
        async deleteModel(model) {
            const confirmed = await this.$swalConfirm({
                title: '删除模型【' + model.name + "】",
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                const ids = [];
                ids.push(model.id);
                // 写删除请求
                this.$axios.post('/health-model-config/batchDelete', ids).then(response => {
                    const { data } = response;
                    if (data.code === 200) {
                        this.$swal.fire({
                            title: '模型删除',
                            text: '模型删除成功',
                            icon: 'success',
                            showConfirmButton: false,
                            timer: 1000,
                        });
                        // 继续加载最新的模型数据
                        this.getAllModelConfig();
                        // 如果已经选中对应的模型，从列表中删除对应的项
                        this.selectedModel = this.selectedModel.filter(entity => entity.id !== model.id);
                    }
                })
            }
        },
        goBack() {
            this.$router.push('/user');
        },
        // 记录值
        toRecord() {
            const userHealths = this.selectedModel.map(entity => {
                return {
                    healthModelConfigId: entity.id,
                    value: entity.value
                }
            });
            this.$axios.post('/user-health/save', userHealths).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.$notify({
                        title: '记录操作',
                        message: '记录成功',
                        type: 'success'
                    });
                    // 两秒后跳转出去
                    setTimeout(() => {
                        this.$router.push('/user');
                    }, 2000)
                }
            })
        },
        modelSelected(model) {
            const saveFlag = this.selectedModel.find(entity => entity.id === model.id);
            if (!saveFlag) {
                // 不存在则添加
                this.selectedModel.push(model);
            }
        },
        searModel() {
            this.getAllModelConfig();
        },
        handleFilterClear() {
            this.userHealthModel.name = '';
            this.getAllModelConfig();
        },
        handleAvatarSuccess(res, file) {
            if (res.code !== 200) {
                this.$message.error(`健康模型封面上传异常`);
                return;
            }
            this.$message.success(`健康模型封面上传成功`);
            this.data.cover = res.data;
        },
        getUser() {
            const userInfo = sessionStorage.getItem('userInfo');
            const entity = JSON.parse(userInfo);
            this.userId = entity.id;
        },
        async addOperation() {
            try {
                this.data.userId = this.userId;
                const response = await this.$axios.post('/health-model-config/save', this.data);
                this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
                if (response.data.code === 200) {
                    this.dialogUserOperaion = false;
                    this.getAllModelConfig();
                    this.data = {};
                }
            } catch (error) {
                console.error('提交表单时出错:', error);
                this.$message.error('提交失败，请稍后再试！');
            }
        },
        addModel() {
            this.dialogUserOperaion = true;
        },
        handleClick(tab, event) {
            // 先去清空条件
            this.userHealthModel = {};
            if (this.activeName === 'first') {
                this.userHealthModel.isGlobal = true;
            } else {
                const userInfo = sessionStorage.getItem('userInfo');
                const entity = JSON.parse(userInfo);
                this.userHealthModel.userId = entity.id;
            }
            this.getAllModelConfig();
        },
        getAllModelConfig() {
            this.$axios.post('/health-model-config/query', this.userHealthModel).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.modelList = data.data;
                }
            });
        },
        getUserInfo() {
            const userInfo = sessionStorage.getItem('userInfo');
            this.userInfo = JSON.parse(userInfo);
        },
    },
};
</script>
<style scoped lang="scss">
.item-model:hover {
    cursor: pointer;
    background-color: #fafafa;
    border-radius: 5px;
}

.item-model{
    padding: 8px;
    box-sizing: border-box;
}

.input-model {
    font-size: 20px;
    box-sizing: border-box;
    font-weight: bold;
    padding: 20px;
    user-select: none;
    border-radius: 5px;
    border: none;
    outline: none;
    background-color: #f1f1f1;
    height: 50px;
    width: 85%;
}
</style>