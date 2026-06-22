# Logo 方案候选

## 1. 设计目标

本轮 Logo 方向根据当前用户反馈调整为更简洁的版本，优先考虑：

```text
简洁
可缩小
适合页眉
适合 favicon
有 CodeLab / 终端 / 代码识别
能融入深色高级科技界面
```

本文件记录候选方向与最终确认记录。当前用户已确认“终端提示符 + C 形框 + 光标竖线 + 琥珀状态点”的 Logo 方向，并已将该方向落地为站内 SVG mark、favicon 和品牌位展示。

## 2. 候选方案

### 2.1 方案 A：CL 终端提示符

核心想法：

```text
用紧凑的 CL 字母、终端提示符和光标线形成标识。
```

优点：

- 最贴近 CodeLab 和终端气质。
- 图形结构简单，适合页眉、后台登录、favicon。
- 和当前页面里的终端窗口、命令控制台、代码流视觉一致。

风险：

- 如果做得过于工具化，可能会偏“开发者工具”而不是“实验室品牌”。
- 需要在最终 SVG 中加强 `NYNU Code Lab` 的横向字标平衡。

适合场景：

```text
官网页眉
favicon
后台登录页
社团技术资料封面
```

### 2.2 方案 B：代码括号符号

核心想法：

```text
用简洁的 < / > 代码括号符号与 C / L 的字母感融合。
```

优点：

- 代码识别非常直接。
- 比复杂节点徽章更简洁，缩小后依然容易识别。
- 适合做正式官网品牌露出和文档封面。

风险：

- 代码括号是常见符号，需要通过字重、比例和 `NYNU` 字标做差异化。
- favicon 版本需要进一步压缩细节。

适合场景：

```text
官网横向 Logo
README / 文档封面
项目演示页
技术分享海报
```

### 2.3 方案 C：CL 方形小标

核心想法：

```text
用深色方形底、极简 CL 线条和一个终端光标点形成应用图标。
```

优点：

- 三个方案里最适合 favicon、PWA 图标和后台侧栏图标。
- 几何结构清晰，和当前深色科技界面匹配。
- 可以和横向 `NYNU Code Lab` 字标组合成完整品牌锁定。

风险：

- 单独看时学校 / 实验室属性较弱，需要配合 wordmark 使用。
- 如果线条过细，小尺寸可能需要专门优化。

适合场景：

```text
favicon
后台系统图标
浏览器标签页
移动端桌面图标
```

## 3. 当前确认方向

已确认方向：

```text
终端提示符 + C 形框 + 光标竖线 + 琥珀状态点
```

确认原因：

- 与当前官网深色控制台、终端窗口、状态灯、能量光边风格一致。
- 使用 `#06090f`、`#0b1220`、`#53e7ff`、`#2ff0b6`、`#ffd36a` 等现有前端 token。
- 小尺寸下仍能识别为 CodeLab 终端 / 代码实验室标识。
- 适合前台页眉、后台侧栏、后台登录页和 favicon。

## 4. 资产路径

用户确认的原始 PNG 预览图已保存到：

```text
docs/assets/logo/nynu-code-lab-selected-logo.png
```

前台实际使用的 SVG mark：

```text
web/src/assets/brand/nynu-code-lab-mark.svg
web/public/favicon.svg
```

后台实际使用的 SVG mark：

```text
admin-web/src/assets/brand/nynu-code-lab-mark.svg
admin-web/public/favicon.svg
```

## 5. 已接入位置

```text
前台页眉 AppHeader
前台页脚 AppFooter
前台 favicon
后台 AdminLayout 侧栏品牌位
后台 LoginView 品牌位
后台 favicon
```

## 6. 动效策略

站内 Logo 只使用轻量效果：

- SVG 内部琥珀状态点轻微呼吸。
- 页眉和后台侧栏 hover 时出现一次能量扫光。
- 遵守 `prefers-reduced-motion`，减少动画偏好开启时关闭状态点循环动画。
- 不使用重 3D、不增加 Canvas、不影响页面性能。
