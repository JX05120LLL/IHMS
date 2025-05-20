<template>
    <div style="box-sizing: border-box;overflow-y: hidden;overflow-x: hidden;padding: 10px;">
        <el-row>
            <el-col :span="8">
                <div style="padding: 15px 5px;box-sizing: border-box;">
                    <PieChart fontColor="rgb(51,51,51)" bag="rgb(248,248,248)" :values="pieValues" :types="pieTypes" />
                </div>
                <div style="padding: 10px 5px;box-sizing: border-box;">
                    <div v-for="(message,index) in messageList" :key="message">
                        <div style="font-size: 16px;font-weight: 800;">{{ message.receiverName }}</div>
                        <div style="padding: 8px 0;font-size: 14px;color: #6f6d6d;">{{ message.content }}</div>
                        <div style="padding: 5px 0;font-size: 14px;color: #6f6d6d;">{{ message.createTime }}</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="16">
                <div style="padding: 15px;box-sizing: border-box;">
                    <LineChart height="290px" tag="存量用户" @on-selected="userDatesSelected" :values="userValues"
                        :date="userDates" />
                </div>
                <div style="padding: 15px;box-sizing: border-box;">
                    <LineChart height="290px" tag="健康指标" @on-selected="modelDatesSelected" :values="modelValues"
                        :date="modelDates" />
                </div>
            </el-col>
        </el-row>
    </div>
</template>
<script>
import LineChart from "@/components/LineChart"
import PieChart from "@/components/PieChart"
import { timeAgo } from "@/utils/data"
export default {
    components: { LineChart, PieChart },
    data() {
        return {
            userValues: [],
            userDates: [],
            modelDates: [],
            modelValues: [],
            pieValues: [],
            pieTypes: [],
            messageList: []
        }
    },
    created() {
        // 数据较少，默认查365天
        this.userDatesSelected(365);
        // 数据较少，默认查365天
        this.modelDatesSelected(365);
        this.loadPieCharts();
        this.loadMessages();
    },
    methods: {
        time(createTime) {
            return timeAgo(createTime);
        },
        // 加载资讯
        loadMessages () {
            const messageQueryDto = {
                current: 1,
                size: 4
            }
            this.$axios.post(`/message/query`, messageQueryDto).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.messageList = data.data;
                }
            })
        },
        loadPieCharts() {
            this.$axios.get(`/views/staticControls`).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.pieValues = data.data.map(entity => entity.count);
                    this.pieTypes = data.data.map(entity => entity.name);
                }
            })
        },
        modelDatesSelected(time) {
            this.$axios.get(`/user-health/daysQuery/${time}`).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.modelValues = data.data.map(entity => entity.count);
                    this.modelDates = data.data.map(entity => entity.name);
                }
            })
        },
        userDatesSelected(time) {
            this.$axios.get(`/user/daysQuery/${time}`).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.userValues = data.data.map(entity => entity.count);
                    this.userDates = data.data.map(entity => entity.name);
                }
            })
        },
    },
};
</script>
<style scoped lang="scss">
.new-item {
    display: flex;
    justify-content: flex-start;

    .item {
        padding: 5px;
        box-sizing: border-box;

        img {
            width: 168px;
            height: 104px;
            border-radius: 5px;
        }

    }

    .item-buttom {
        padding: 5px;
        box-sizing: border-box;

        .title {
            font-size: 16px;
            font-weight: 800;
        }

    }
}
</style>