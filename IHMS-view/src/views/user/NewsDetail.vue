<template>
    <el-row style="width: 1000px;margin: 0 auto;">
        <el-col :span="16">
            <div style="padding-right: 50px;box-sizing: border-box;">
                <div>
                    <h1>{{ newsInfo.name }}</h1>
                    <div
                        style="font-size: 12px;background-color: rgb(250, 250, 250);padding: 16px 6px;border-radius: 5px;">
                        <span class="news-tags">{{ newsInfo.tagName }}</span>
                        <span style="margin-left: 10px;">{{ parseTime(newsInfo.createTime) }}</span>
                        <el-button style="margin-left: 20px;" @click="saveNewsOperation" class="customer" size="mini">{{
                            !saveFlag ?
                                '立即收藏' : '取消收藏' }}</el-button>
                    </div>
                    <div v-html="newsInfo.content"></div>
                </div>
                <div>
                    <Evaluations :contentId="newsInfo.id" contentType="NEWS" />
                </div>
            </div>
        </el-col>
        <el-col :span="8">
            <h3 style="padding: 10px 30px;">资讯推荐</h3>
            <el-col @click.native="newsItemClick(news)" :span="24" :key="index" v-for="(news, index) in newsTopList">
                <div style="padding: 25px 30px;box-sizing: border-box;">
                    <img :src="news.cover" :alt="news.name" style="width: 100%;height: 118px;border-radius: 5px;">
                    <h3 class="news-title">{{ news.name }}</h3>
                    <div style="font-size: 12px;">
                        <span class="news-tags">{{ news.tagName }}</span>
                        <span style="margin-left: 10px;">{{ parseTime(news.createTime) }}</span>
                    </div>
                </div>
            </el-col>
        </el-col>
    </el-row>
</template>
<script>
import { timeAgo } from "@/utils/data"
import Evaluations from "@/components/Evaluations.vue";
export default {
    components: { Evaluations },
    name: "NewsDetail",
    data() {
        return {
            newsInfo: {},
            newsTopList: [],
            saveFlag: false,
            newsSaveList: []
        }
    },
    created() {
        // 第一件事：是不是拿到存起来的资讯数据？
        this.getStorageInfo();
        this.loadAllTopNews();
    },
    methods: {
        loadSaveStatus() {
            const newsSaveQueryDto = {
                newsId: this.newsInfo.id
            }
            console.log("查询的参数：" + JSON.stringify(newsSaveQueryDto));
            this.$axios.post('/news-save/queryUser', newsSaveQueryDto).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.saveFlag = data.data.length !== 0;
                }
            })
        },
        // 收藏或取消收藏操作
        saveNewsOperation() {
            this.$axios.post('/news-save/operation', { newsId: this.newsInfo.id }).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.$message.success(!this.saveFlag ? '收藏成功' : '取消收藏成功');
                    this.saveFlag = !this.saveFlag;
                }
            })
        },
        newsItemClick(news) {
            this.newsInfo = news;
            this.loadSaveStatus();
        },
        // 转换时间
        parseTime(time) {
            return timeAgo(time);
        },
        getStorageInfo() {
            const newInfo = sessionStorage.getItem('newsInfo');
            this.newsInfo = JSON.parse(newInfo);
            this.loadSaveStatus();
        },
        // 查询推荐资讯
        loadAllTopNews() {
            const newQueryDto = { isTop: true };
            this.$axios.post('/news/query', newQueryDto).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.newsTopList = data.data;
                }
            })
        },
    }
};
</script>

<style scoped lang="scss">
.news-tags {
    display: inline-block;
    padding: 4px 10px;
    //background-color: rgb(226, 242, 249);
    color: #1d3cc4;
    border-radius: 3px;
}
</style>
