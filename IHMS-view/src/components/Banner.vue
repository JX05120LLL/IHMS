<template>
    <div style="position: relative;">
        <img :src="activeData.cover" style="min-height: 218px;max-height: 308px;"
            :style="{ width: width, borderRadius: borderRadius }" />
        <h3 class="tip-name" style="position: absolute;bottom: 0;">
            <div @click="onClick" style="font-size: 12px;">{{ activeData.name }}</div>
            <div class="point-container">
                <span :style="{
                    backgroundColor: index === indexPoint ? 'rgb(254, 244, 203)' : '',
                    height: index === indexPoint ? '15px' : '10px',
                    width: index === indexPoint ? '30px' : '20px'
                }" v-for="(point, indexPoint) in data" :key="index">
                </span>
            </div>
        </h3>
    </div>
</template>

<script>
// 轮播图组件
export default {
    name: "Banner",
    props: {
        data: {
            type: Array,
            required: true
        },
        width: { // 宽度
            type: String,
            default: '100%'
        },
        borderRadius: { // 图片边框曲度
            type: String,
            default: '10px'
        },
        time: { // 轮播时间
            type: Number,
            default: 3000
        }
    },
    watch: {
        data: {
            handler(v1, v2) {
                this.activeData = { ... this.data[0] };
                this.config();
            },
            deep: true,
            immediate: true,
        },
    },
    data() {
        return {
            activeData: {},
            index: 0
        }
    },
    methods: {
        onClick(data) {
            this.$emit('on-click', this.activeData);
        },
        config() {
            setInterval(() => {
                this.index = (this.index >= this.data.length) ? 0 : this.index;
                this.activeData = this.data[this.index++];
            }, this.time);
        },
    }
};
</script>

<style scoped lang="scss">
.tip-name {
    text-align: center;
    width: 100%;
    padding: 10px 0;
    color: aliceblue;
    margin: 0;
    margin-bottom: 4px;
    border-bottom-left-radius: 5px;
    border-bottom-right-radius: 5px;
    cursor: pointer;
    background-color: rgba(5, 0, 0, 0.6);
}

.point-container {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    margin-top: 10px;

    span {
        width: 20px;
        height: 10px;
        background-color: rgb(245, 245, 245);
        border-radius: 5px;
    }

    span:hover {
        background-color: rgb(237, 222, 105);
    }
}
</style>
