<template>
    <div>
        <span v-for="(entity, index) in dataList" :key="index">
            <span class="tag-item" :style="{
                backgroundColor: tagSelected.name === entity.name ? 'rgb(56, 183, 129)' : 'rgb(252, 252, 252)',
                color: tagSelected.name === entity.name ? '#FFFFFF' : '#1c1c1c'
            }" @click="onClick(entity)">
                {{ entity.name }}
            </span>
        </span>
    </div>
</template>

<script>
// 标签渲染组件，接收渲染参数，点击后会向父组件反馈
export default {
    name: "TagLine",
    data() {
        return {
            tagSelected: {}
        }
    },
    props: {
        dataList: {
            type: Array,
            required: true
        }
    },
    mounted() {
        this.onClick({ name: '全部',id: null });
    },
    methods: {
        onClick(tag) {
            this.tagSelected = tag;
            this.$emit('on-click', tag);
        },
        all() {
            this.$emit('on-click', { id: null, name: '全部' });
        },
    }
};
</script>

<style scoped lang="scss">
.tag-item {
    font-size: 12px;
    display: inline-block;
    margin: 25px 10px 25px 0;
    padding: 6px 20px;
    cursor: pointer;
    user-select: none;
    border-radius: 15px;
    transition: all 0.2s;
}
</style>