<template>
  <div class="dashboard admin-page">
    <div class="admin-page-header">
      <div>
        <span class="admin-page-kicker">Overview</span>
        <h2 class="admin-page-title">数据概览</h2>
        <p class="admin-page-copy">当前统计接口尚未实现，因此保持占位，不编造注册人数、报名人数或内容数量。</p>
      </div>
    </div>

    <section class="console-hero">
      <div>
        <span class="admin-page-kicker">Admin Console</span>
        <h3>CodeLab 管理控制台</h3>
        <p>当前后台用于报名审核、文章管理、项目管理；成员、方向、站点配置和上传仍为占位入口。</p>
      </div>
      <div class="console-terminal">
        <span>admin-console --modules live</span>
        <span>guard --role ADMIN</span>
        <span>sync --content article project recruit</span>
      </div>
    </section>

    <div class="stats-grid">
      <el-card shadow="hover" class="stat-card" v-for="item in stats" :key="item.label">
        <div class="stat-content">
          <div class="stat-icon" :class="item.className">
            <el-icon :size="30">
              <component :is="item.icon" />
            </el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">-</p>
            <p class="stat-label">{{ item.label }}</p>
          </div>
        </div>
      </el-card>
    </div>

    <div class="dashboard-panels">
      <el-card shadow="never">
        <div class="panel-title">当前可管理内容</div>
        <div class="pipeline-list">
          <span>报名审核</span>
          <span>文章草稿/发布/下架</span>
          <span>项目草稿/发布/精选</span>
        </div>
      </el-card>
      <el-card shadow="never">
        <div class="panel-title">待接入模块</div>
        <div class="pipeline-list muted">
          <span>成员管理</span>
          <span>方向管理</span>
          <span>站点配置</span>
          <span>文件上传</span>
        </div>
      </el-card>
    </div>

    <div class="dashboard-panels chart-panels">
      <el-card shadow="never">
        <div class="panel-title">模块接入状态</div>
        <div ref="moduleChartRef" class="module-chart"></div>
      </el-card>
      <el-card shadow="never">
        <div class="panel-title">控制台信号</div>
        <div class="signal-grid">
          <div v-for="signal in signals" :key="signal.label">
            <span :class="signal.className"></span>
            <strong>{{ signal.label }}</strong>
            <small>{{ signal.value }}</small>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from "vue";
import * as echarts from "echarts/core";
import { BarChart, PieChart } from "echarts/charts";
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
} from "echarts/components";
import { CanvasRenderer } from "echarts/renderers";
import { Clock, Document, Reading, User } from "@element-plus/icons-vue";

echarts.use([BarChart, PieChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer]);

const moduleChartRef = ref<HTMLElement>();
let chart: echarts.ECharts | null = null;

const stats = [
  { label: "注册用户数", icon: User, className: "user-icon" },
  { label: "报名人数", icon: Document, className: "recruit-icon" },
  { label: "待审核人数", icon: Clock, className: "pending-icon" },
  { label: "文章数量", icon: Reading, className: "article-icon" },
];

const signals = [
  { label: "Recruit", value: "LIVE", className: "is-live" },
  { label: "Articles", value: "LIVE", className: "is-live" },
  { label: "Projects", value: "LIVE", className: "is-live" },
  { label: "Members", value: "PENDING", className: "is-pending" },
  { label: "Directions", value: "PENDING", className: "is-pending" },
  { label: "Upload", value: "PENDING", className: "is-pending" },
];

function renderChart() {
  if (!moduleChartRef.value) return;

  chart = echarts.init(moduleChartRef.value, undefined, { renderer: "canvas" });
  const option = {
    color: ["#53e7ff", "#2ff0b6", "#ffd36a"],
    tooltip: {
      trigger: "axis",
      backgroundColor: "rgba(6, 11, 20, 0.96)",
      borderColor: "rgba(83, 231, 255, 0.28)",
      textStyle: { color: "#eef7ff" },
    },
    grid: { left: 28, right: 16, top: 24, bottom: 28, containLabel: true },
    xAxis: {
      type: "category",
      data: ["已接入", "占位中", "待实现"],
      axisLine: { lineStyle: { color: "rgba(153, 217, 255, 0.18)" } },
      axisLabel: { color: "#9eb1c4" },
    },
    yAxis: {
      type: "value",
      minInterval: 1,
      splitLine: { lineStyle: { color: "rgba(153, 217, 255, 0.1)" } },
      axisLabel: { color: "#9eb1c4" },
    },
    series: [
      {
        type: "bar",
        barWidth: 28,
        data: [3, 4, 5],
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: "#53e7ff" },
              { offset: 1, color: "rgba(47, 240, 182, 0.26)" },
            ],
          },
        },
      },
    ],
  };
  chart.setOption(option);
}

function resizeChart() {
  chart?.resize();
}

onMounted(() => {
  renderChart();
  window.addEventListener("resize", resizeChart);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeChart);
  chart?.dispose();
  chart = null;
});
</script>

<style scoped>
.console-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(320px, 0.62fr);
  gap: 18px;
  align-items: center;
  margin-bottom: 18px;
  padding: 24px;
  border: 1px solid var(--admin-line);
  border-radius: var(--admin-radius);
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.025)),
    var(--admin-surface);
  box-shadow: var(--admin-shadow);
}

.console-hero h3 {
  color: var(--admin-text-strong);
  font-size: 30px;
  line-height: 1.18;
}

.console-hero p {
  max-width: 720px;
  margin-top: 10px;
  color: var(--admin-muted);
  line-height: 1.7;
}

.console-terminal {
  display: grid;
  gap: 10px;
  padding: 16px;
  border: 1px solid rgba(153, 217, 255, 0.16);
  border-radius: var(--admin-radius-sm);
  background: rgba(4, 10, 18, 0.72);
  color: var(--admin-cyan);
  font-family: var(--admin-font-data);
  font-size: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  display: grid;
  place-items: center;
  width: 58px;
  height: 58px;
  border-radius: 18px;
  color: #041017;
}

.user-icon {
  background: linear-gradient(135deg, var(--admin-cyan), var(--admin-blue));
}

.recruit-icon {
  background: linear-gradient(135deg, var(--admin-teal), #7df5cf);
}

.pending-icon {
  background: linear-gradient(135deg, var(--admin-amber), #fff0a8);
}

.article-icon {
  background: linear-gradient(135deg, #a98bff, var(--admin-cyan));
}

.stat-value {
  color: var(--admin-text-strong);
  font-family: var(--admin-font-data);
  font-size: 32px;
  font-weight: 820;
  line-height: 1;
}

.stat-label {
  margin-top: 7px;
  color: var(--admin-muted);
}

.dashboard-panels {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-top: 18px;
}

.chart-panels {
  grid-template-columns: minmax(0, 1fr) minmax(340px, 0.72fr);
}

.module-chart {
  height: 280px;
}

.signal-grid {
  display: grid;
  gap: 12px;
}

.signal-grid div {
  display: grid;
  grid-template-columns: 10px minmax(0, 1fr) auto;
  gap: 10px;
  align-items: center;
  padding: 12px;
  border: 1px solid rgba(153, 217, 255, 0.14);
  border-radius: var(--admin-radius-sm);
  background: rgba(255, 255, 255, 0.045);
}

.signal-grid span {
  width: 10px;
  height: 10px;
  border-radius: 999px;
}

.signal-grid .is-live {
  background: var(--admin-teal);
  box-shadow: 0 0 18px rgba(47, 240, 182, 0.58);
}

.signal-grid .is-pending {
  background: var(--admin-amber);
  box-shadow: 0 0 18px rgba(255, 211, 106, 0.45);
}

.signal-grid strong {
  color: var(--admin-text-strong);
}

.signal-grid small {
  color: var(--admin-muted);
  font-family: var(--admin-font-data);
}

.panel-title {
  margin-bottom: 14px;
  color: var(--admin-text-strong);
  font-size: 18px;
  font-weight: 720;
}

.pipeline-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.pipeline-list span {
  padding: 8px 11px;
  border: 1px solid rgba(153, 217, 255, 0.16);
  border-radius: 999px;
  color: var(--admin-cyan);
  background: rgba(83, 231, 255, 0.07);
}

.pipeline-list.muted span {
  color: var(--admin-muted);
  background: rgba(255, 255, 255, 0.045);
}

@media (max-width: 1100px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .console-hero,
  .chart-panels {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .stats-grid,
  .dashboard-panels {
    grid-template-columns: 1fr;
  }
}
</style>
