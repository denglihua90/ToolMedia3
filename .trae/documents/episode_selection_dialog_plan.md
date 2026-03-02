# 选集对话框实现计划

## 1. 项目现状分析

### 现有组件
- **SelectEpisodeView**: 基础的选集视图，继承自 DrawerPopupView
- **SpeedListView**: 速度选择对话框，使用 XPopup 实现，包含单选功能
- **VideoController**: 视频控制器，包含 btnSelections 按钮

### 布局文件
- **layout_select_episode_view.xml**: 选集视图的布局文件，包含 RecyclerView

## 2. 实现计划

### [ ] Task 1: 创建选集适配器
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 创建 EpisodeAdapter 类，用于显示选集列表
  - 实现网格布局和单选功能
  - 支持选集点击回调
- **Success Criteria**:
  - EpisodeAdapter 类创建完成
  - 支持网格布局显示
  - 实现单选功能和点击回调
- **Test Requirements**:
  - `programmatic` TR-1.1: 适配器编译通过
  - `human-judgement` TR-1.2: 适配器实现正确，支持网格布局和单选
- **Notes**: 参考 SpeedAdapter 的实现方式

### [ ] Task 2: 更新 SelectEpisodeView 类
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - 更新 SelectEpisodeView 类，集成 EpisodeAdapter
  - 实现网格布局管理器
  - 添加选集数据和回调
- **Success Criteria**:
  - SelectEpisodeView 类更新完成
  - 支持网格布局显示选集
  - 实现选集选择回调
- **Test Requirements**:
  - `programmatic` TR-2.1: SelectEpisodeView 编译通过
  - `human-judgement` TR-2.2: 选集视图显示正确，支持网格布局
- **Notes**: 参考 SpeedListView 的实现方式

### [ ] Task 3: 添加 btnSelections 点击事件处理
- **Priority**: P0
- **Depends On**: Task 2
- **Description**:
  - 在 VideoController 中添加 btnSelections 按钮的点击事件
  - 实现显示选集对话框的方法
  - 处理选集选择回调
- **Success Criteria**:
  - btnSelections 点击事件处理实现完成
  - 选集对话框能够正确显示
  - 选集选择能够正确处理
- **Test Requirements**:
  - `programmatic` TR-3.1: VideoController 编译通过
  - `human-judgement` TR-3.2: 点击 btnSelections 能够显示选集对话框
- **Notes**: 参考 showSpeedDialog 方法的实现

### [ ] Task 4: 测试和优化
- **Priority**: P1
- **Depends On**: Task 3
- **Description**:
  - 测试选集对话框的显示和功能
  - 优化布局和交互体验
  - 确保与速度选择对话框的风格一致
- **Success Criteria**:
  - 选集对话框功能正常
  - 布局和交互体验良好
  - 与速度选择对话框风格一致
- **Test Requirements**:
  - `programmatic` TR-4.1: 应用编译通过
  - `human-judgement` TR-4.2: 选集对话框使用体验良好
- **Notes**: 测试不同屏幕尺寸下的显示效果

## 3. 技术实现细节

### 3.1 选集适配器实现
- 创建 EpisodeAdapter 类，继承自 BaseQuickAdapter
- 实现网格布局的 item 布局
- 支持单选功能，高亮显示选中项
- 提供选集选择回调

### 3.2 SelectEpisodeView 实现
- 使用 GridLayoutManager 实现网格布局
- 初始化 EpisodeAdapter 并设置数据
- 处理选集选择回调
- 与 SpeedListView 保持一致的风格

### 3.3 VideoController 集成
- 添加 showEpisodeDialog 方法
- 处理 btnSelections 点击事件
- 实现选集选择后的处理逻辑

## 4. 预期成果

- 成功实现选集对话框功能
- 支持网格布局显示选集
- 实现单选功能
- 与速度选择对话框风格一致
- 提供良好的用户体验

## 5. 风险评估

### 潜在问题
- 布局适配问题
- 数据加载问题
- 与现有代码的兼容性

### 缓解措施
- 测试不同屏幕尺寸
- 确保数据处理的健壮性
- 保持与现有代码的一致性