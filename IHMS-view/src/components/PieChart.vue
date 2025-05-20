<template>
  <div class="line-main" :style="{ backgroundColor: bag }">
    <div ref="chart" :style="{ width: width, height: height }"></div>
    <!-- <div>
      <span class="tag" :style="{ color: fontColor }">
        <i class="el-icon-pie-chart"></i>
        {{ tag }}</span>
    </div> -->
  </div>
</template>
<script>
import * as echarts from 'echarts'
export default {
  name: 'PieChart',
  props: {
    types: {
      type: Array,
      default: []
    },
    values: {
      type: Array,
      default: []
    },
    width: {
      type: String,
      default: '100%'
    },
    // tag: {
    //   type: String,
    //   default: '饼状图'
    // },
    height: {
      type: String,
      default: '243px'
    },
    bag: {
      type: String,
      default: '#fff'
    },
    fontColor: {
      type: String,
      default: '#333'
    },
  },
  data() {
    return {
      chart: null,
    }
  },
  watch: {
    types(v1, v2) {
      this.initChart();
    }
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.chart)
      let option = {
        title: {
          text: '',
          subtext: '',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          show: false,
        },
        series: [
          {
            name: '',
            type: 'pie',
            radius: '70%',
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: false,
                fontSize: '24',
                fontWeight: '600'
              }
            },
            labelLine: {
              show: true
            },
            label: {
              show: true,
              position: 'outer',
              formatter: '{d}%'
            },
            data: this.values.map((value, index) => ({
              name: this.types[index],
              value: value,
            })),
            itemStyle: {
              color: function (params) {
                const colorList = [
                  '#FF6347', // 火砖红
                  '#FFD700', // 金黄色
                  '#0885f9', // 酸橙绿
                  '#FF69B4', // 热情粉
                  '#9370DB', // 紫丁香
                  '#00FFFF', // 天蓝色
                  '#b84031', // 橙红色
                ];
                return colorList[params.dataIndex % colorList.length];
              }
            }
          }
        ]
      }
      this.chart.setOption(option)
    }
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
    }
  }
}  
</script>

<style scoped lang="scss">
.line-main {
  padding-top: 40px;
  margin-bottom: 5px;
  border-radius: 3px;
  background-color: #000000;

  .tag {
    font-size: 14px;
    text-align: center;
    padding: 15px 6px;
    display: block;
  }

}
</style>