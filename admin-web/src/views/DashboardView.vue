<template>
  <div class="dashboard admin-page">
    <div class="admin-page-header">
      <div>
        <span class="admin-page-kicker">Overview</span>
        <h2 class="admin-page-title">数据概览</h2>
        <p class="admin-page-copy">实时统计 CodeLab 注册用户、内容产出、招新报名与核心成员数据。</p>
      </div>
    </div>

    <section class="console-hero">
      <div class="console-hero__grid" aria-hidden="true"></div>
      <div>
        <span class="admin-page-kicker">Admin Console</span>
        <h3>CodeLab 管理控制台</h3>
        <p>当前后台用于报名审核、文章管理、项目管理、成员管理、方向管理、站点配置与文件上传。</p>
      </div>
      <div class="console-hologram" aria-hidden="true">
        <span></span>
        <span></span>
        <span></span>
        <i></i>
      </div>
      <div class="console-terminal">
        <span>admin-console --modules live</span>
        <span>guard --role ADMIN</span>
        <span>sync --content article project recruit member direction config upload</span>
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
            <p class="stat-value">{{ item.value ?? '-' }}</p>
            <p v-if="item.sub" class="stat-sub">{{ item.sub }}</p>
            <p class="stat-label">{{ item.label }}</p>
          </div>
        </div>
      </el-card>
    </div>

    <section class="ops-stack">
      <div v-for="panel in opsPanels" :key="panel.title" class="ops-stack__panel">
        <div>
          <span :class="['ops-stack__state', panel.state]"></span>
          <strong>{{ panel.title }}</strong>
        </div>
        <p>{{ panel.copy }}</p>
        <div class="ops-stack__track" aria-hidden="true">
          <i v-for="item in panel.steps" :key="item"></i>
        </div>
      </div>
    </section>

    <div class="dashboard-panels">
      <el-card shadow="never">
        <div class="panel-title">当前可管理内容</div>
        <div class="pipeline-list">
          <span v-for="item in liveModules" :key="item">{{ item }}</span>
        </div>
      </el-card>
      <el-card shadow="never">
        <div class="panel-title">待接入模块</div>
        <div class="pipeline-list muted">
          <span v-for="item in pendingModules" :key="item">{{ item }}</span>
        </div>
      </el-card>
    </div>

    <section class="ops-matrix">
      <div class="ops-matrix__title">
        <span class="admin-page-kicker">Run matrix</span>
        <h3>后台工程运行矩阵</h3>
      </div>
      <div class="ops-matrix__grid">
        <div v-for="item in opsMatrix" :key="item.module" class="ops-matrix__item">
          <span :class="['ops-matrix__state', item.state]"></span>
          <strong>{{ item.module }}</strong>
          <small>{{ item.detail }}</small>
        </div>
      </div>
    </section>

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
import { computed, onBeforeUnmount, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import * as echarts from "echarts/core";
import { BarChart, PieChart } from "echarts/charts";
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
} from "echarts/components";
import { CanvasRenderer } from "echarts/renderers";
import { Document, FolderOpened, Reading, User, UserFilled } from "@element-plus/icons-vue";
import { getDashboardStats, type DashboardStats } from "@/api/dashboard";

echarts.use([BarChart, PieChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer]);

const moduleChartRef = ref<HTMLElement>();
let chart: echarts.ECharts | null = null;

const statsData = ref<DashboardStats | null>(null);

const stats = computed(() => {
  const d = statsData.value;
  if (!d) {
    return [
      { label: "注册用户", value: null, sub: undefined, icon: User, className: "user-icon" },
      { label: "文章总数", value: null, sub: undefined, icon: Reading, className: "article-icon" },
      { label: "项目总数", value: null, sub: undefined, icon: FolderOpened, className: "project-icon" },
      { label: "招新报名", value: null, sub: undefined, icon: Document, className: "recruit-icon" },
      { label: "核心成员", value: null, sub: undefined, icon: UserFilled, className: "member-icon" },
    ];
  }
  return [
    { label: "注册用户", value: d.userCount, icon: User, className: "user-icon" },
    { label: "文章总数", value: d.articleCount, sub: `已发布 ${d.publishedArticleCount}`, icon: Reading, className: "article-icon" },
    { label: "项目总数", value: d.projectCount, sub: `已发布 ${d.publishedProjectCount}`, icon: FolderOpened, className: "project-icon" },
    { label: "招新报名", value: d.recruitTotal, sub: `待处理 ${d.recruitPending}`, icon: Document, className: "recruit-icon" },
    { label: "核心成员", value: d.memberCount, sub: undefined, icon: UserFilled, className: "member-icon" },
  ];
});

const signals = [
  { label: "Recruit", value: "LIVE", className: "is-live" },
  { label: "Articles", value: "LIVE", className: "is-live" },
  { label: "Projects", value: "LIVE", className: "is-live" },
  { label: "Members", value: "LIVE", className: "is-live" },
  { label: "Directions", value: "LIVE", className: "is-live" },
  { label: "Site Config", value: "LIVE", className: "is-live" },
  { label: "Upload", value: "LIVE", className: "is-live" },
];

const opsMatrix = [
  { module: "报名审核", detail: "review queue ready", state: "live" },
  { module: "文章发布", detail: "markdown safe render", state: "live" },
  { module: "项目展台", detail: "featured pipeline ready", state: "live" },
  { module: "成员数据", detail: "crud ready", state: "live" },
  { module: "站点配置", detail: "crud ready", state: "live" },
  { module: "文件上传", detail: "crud ready", state: "live" },
];

const opsPanels = [
  {
    title: "Build lane",
    copy: "前台与后台构建作为独立通道验证，后端接口本轮保持冻结。",
    state: "live",
    steps: ["pull", "build", "type", "verify"],
  },
  {
    title: "Test matrix",
    copy: "页面视觉升级不改变认证、报名、文章和项目的接口兼容性。",
    state: "live",
    steps: ["auth", "recruit", "article", "project"],
  },
  {
    title: "Deploy watch",
    copy: "Docker 与 Nginx 配置保持现状，本轮只更新前端展示层。",
    state: "pending",
    steps: ["pack", "image", "nginx", "smoke"],
  },
];

const liveModules = [
  "报名审核",
  "文章草稿/发布/下架",
  "项目草稿/发布/精选",
  "成员管理",
  "方向管理",
  "站点配置",
  "文件上传",
];

const pendingModules: string[] = [];

async function fetchStats() {
  try {
    const res = await getDashboardStats();
    if (res.code === 200) {
      statsData.value = res.data;
    }
  } catch {
    ElMessage.error("加载统计数据失败");
  }
}

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
        data: [7, 0, 5],
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
  fetchStats();
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
  position: relative;
  overflow: hidden;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(220px, 0.42fr) minmax(320px, 0.62fr);
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

.console-hero__grid {
  position: absolute;
  inset: auto 0 -34% 0;
  height: 60%;
  pointer-events: none;
  border-top: 1px solid rgba(83, 231, 255, 0.12);
  background:
    linear-gradient(rgba(83, 231, 255, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(83, 231, 255, 0.05) 1px, transparent 1px);
  background-size: 40px 40px;
  transform: perspective(980px) rotateX(66deg);
  transform-origin: center bottom;
  opacity: 0.74;
}

.console-hero > *:not(.console-hero__grid) {
  position: relative;
  z-index: 1;
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

.console-hologram {
  position: relative;
  display: grid;
  place-items: center;
  min-height: 168px;
  transform-style: preserve-3d;
}

.console-hologram span {
  position: absolute;
  border: 1px solid rgba(83, 231, 255, 0.18);
  border-radius: 999px;
  box-shadow:
    inset 0 0 26px rgba(83, 231, 255, 0.035),
    0 0 28px rgba(83, 231, 255, 0.06);
  animation: console-orbit 12s linear infinite;
}

.console-hologram span:nth-child(1) {
  width: 150px;
  height: 150px;
}

.console-hologram span:nth-child(2) {
  width: 112px;
  height: 112px;
  border-color: rgba(47, 240, 182, 0.2);
  transform: rotateX(62deg);
  animation-duration: 9s;
  animation-direction: reverse;
}

.console-hologram span:nth-child(3) {
  width: 72px;
  height: 72px;
  border-color: rgba(255, 211, 106, 0.18);
  transform: rotateY(64deg);
  animation-duration: 15s;
}

.console-hologram i {
  width: 14px;
  height: 14px;
  border-radius: 999px;
  background: var(--admin-cyan);
  box-shadow: 0 0 32px rgba(83, 231, 255, 0.82);
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

.ops-stack {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
  margin-top: 18px;
}

.ops-stack__panel {
  position: relative;
  overflow: hidden;
  display: grid;
  gap: 13px;
  min-height: 164px;
  padding: 18px;
  border: 1px solid var(--admin-line);
  border-radius: var(--admin-radius);
  background:
    radial-gradient(circle at 18% 0%, rgba(83, 231, 255, 0.1), transparent 34%),
    linear-gradient(145deg, rgba(255, 255, 255, 0.075), rgba(255, 255, 255, 0.024)),
    rgba(8, 16, 28, 0.68);
  box-shadow: var(--admin-shadow);
  transition:
    border-color 220ms ease,
    transform 220ms ease,
    box-shadow 220ms ease;
}

.ops-stack__panel::after {
  content: "";
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    linear-gradient(115deg, transparent 20%, rgba(83, 231, 255, 0.12), transparent 48%),
    linear-gradient(rgba(255, 255, 255, 0.04) 1px, transparent 1px);
  background-size: 100% 100%, 100% 10px;
  opacity: 0.5;
}

.ops-stack__panel:hover {
  border-color: rgba(83, 231, 255, 0.34);
  transform: translateY(-3px);
  box-shadow: 0 30px 92px rgba(0, 0, 0, 0.42), 0 0 34px rgba(83, 231, 255, 0.1);
}

.ops-stack__panel > * {
  position: relative;
  z-index: 1;
}

.ops-stack__panel div:first-child {
  display: flex;
  align-items: center;
  gap: 9px;
}

.ops-stack__state {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: var(--admin-teal);
  box-shadow: 0 0 18px rgba(47, 240, 182, 0.58);
}

.ops-stack__state.pending {
  background: var(--admin-amber);
  box-shadow: 0 0 18px rgba(255, 211, 106, 0.42);
}

.ops-stack__panel strong {
  color: var(--admin-text-strong);
  font-family: var(--admin-font-data);
}

.ops-stack__panel p {
  color: var(--admin-muted);
  line-height: 1.7;
}

.ops-stack__track {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 0;
  margin-top: auto;
}

.ops-stack__track::before {
  content: "";
  grid-column: 1 / -1;
  grid-row: 1;
  align-self: center;
  height: 1px;
  background: linear-gradient(90deg, rgba(83, 231, 255, 0.2), rgba(47, 240, 182, 0.42), rgba(255, 211, 106, 0.22));
}

.ops-stack__track i {
  position: relative;
  z-index: 1;
  grid-row: 1;
  width: 11px;
  height: 11px;
  border: 1px solid rgba(216, 247, 255, 0.42);
  border-radius: 999px;
  background: rgba(4, 10, 18, 0.84);
  box-shadow: 0 0 14px rgba(83, 231, 255, 0.18);
}

.ops-stack__track i:nth-child(2),
.ops-stack__track i:nth-child(3) {
  justify-self: center;
}

.ops-stack__track i:last-child {
  justify-self: end;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
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

.project-icon {
  background: linear-gradient(135deg, #a98bff, var(--admin-blue));
}

.article-icon {
  background: linear-gradient(135deg, #a98bff, var(--admin-cyan));
}

.member-icon {
  background: linear-gradient(135deg, var(--admin-amber), #ff8c6b);
}

.stat-value {
  color: var(--admin-text-strong);
  font-family: var(--admin-font-data);
  font-size: 32px;
  font-weight: 820;
  line-height: 1;
}

.stat-sub {
  margin-top: 4px;
  color: var(--admin-muted);
  font-size: 12px;
  font-family: var(--admin-font-data);
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

.ops-matrix {
  position: relative;
  overflow: hidden;
  margin-top: 18px;
  padding: 22px;
  border: 1px solid var(--admin-line);
  border-radius: var(--admin-radius);
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.07), rgba(255, 255, 255, 0.024)),
    rgba(8, 16, 28, 0.72);
  box-shadow: var(--admin-shadow);
}

.ops-matrix::before {
  content: "";
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    radial-gradient(circle at 20% 10%, rgba(83, 231, 255, 0.12), transparent 32%),
    linear-gradient(90deg, transparent, rgba(83, 231, 255, 0.12), transparent);
  opacity: 0.56;
}

.ops-matrix__title,
.ops-matrix__grid {
  position: relative;
  z-index: 1;
}

.ops-matrix__title h3 {
  color: var(--admin-text-strong);
  font-size: 24px;
  line-height: 1.18;
}

.ops-matrix__grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.ops-matrix__item {
  display: grid;
  grid-template-columns: 10px minmax(0, 1fr);
  gap: 10px;
  align-items: center;
  min-height: 72px;
  padding: 13px;
  border: 1px solid rgba(153, 217, 255, 0.14);
  border-radius: var(--admin-radius-sm);
  background: rgba(255, 255, 255, 0.045);
  transition:
    border-color 220ms ease,
    background 220ms ease,
    transform 220ms ease;
}

.ops-matrix__item:hover {
  border-color: rgba(83, 231, 255, 0.34);
  background: rgba(83, 231, 255, 0.07);
  transform: translateY(-2px);
}

.ops-matrix__state {
  grid-row: span 2;
  width: 10px;
  height: 10px;
  border-radius: 999px;
}

.ops-matrix__state.live {
  background: var(--admin-teal);
  box-shadow: 0 0 18px rgba(47, 240, 182, 0.58);
}

.ops-matrix__state.pending {
  background: var(--admin-amber);
  box-shadow: 0 0 18px rgba(255, 211, 106, 0.42);
}

.ops-matrix__item strong {
  color: var(--admin-text-strong);
}

.ops-matrix__item small {
  color: var(--admin-muted);
  font-family: var(--admin-font-data);
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
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .console-hero,
  .ops-stack,
  .chart-panels,
  .ops-matrix__grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .stats-grid,
  .dashboard-panels {
    grid-template-columns: 1fr;
  }
}

@keyframes console-orbit {
  to {
    transform: rotateZ(360deg) rotateX(62deg);
  }
}

@media (prefers-reduced-motion: reduce) {
  .console-hologram span {
    animation: none;
  }
}
</style>
