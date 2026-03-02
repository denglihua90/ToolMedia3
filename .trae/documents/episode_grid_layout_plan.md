# 选集网格布局优化计划

## 1. 项目现状分析

### 现有实现
- **SelectEpisodeView**: 实现了基础的选集功能
- **RecyclerView**: 使用 GridLayoutManager 实现 4 列网格布局
- **EpisodeAdapter**: 实现了选集的显示和选择功能
- **当前问题**: 网格布局的 item 没有平均分放，没有记录最后的选择集数

## 2. 实现计划

### [ ] Task 1: 优化网格布局平均分放
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 修改 GridLayoutManager 的配置，实现 item 平均分放
  - 调整 item 布局，确保在不同屏幕尺寸下都能均匀分布
  - 实现自适应的网格布局
- **Success Criteria**:
  - 网格布局中的 item 能够平均分放
  - 在不同屏幕尺寸下显示正常
  - 视觉效果美观
- **Test Requirements**:
  - `programmatic` TR-1.1: 代码编译通过
  - `human-judgement` TR-1.2: 网格布局显示均匀，item 大小一致
- **Notes**: 可以使用 GridLayoutManager 的 SpanSizeLookup 或自定义布局管理器

### [ ] Task 2: 实现记录最后选择集数功能
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 在 SelectEpisodeView 中添加保存最后选择集数的功能
  - 使用 SharedPreferences 或其他持久化方式存储
  - 在初始化时恢复上次的选择
- **Success Criteria**:
  - 能够记录并持久化最后选择的集数
  - 应用重启后能够恢复上次的选择
  - 选择集数后能够更新存储
- **Test Requirements**:
  - `programmatic` TR-2.1: 代码编译通过
  - `human-judgement` TR-2.2: 选择集数后重启应用，上次选择的集数仍然被选中
- **Notes**: 考虑使用 SharedPreferences 存储，键名可以使用视频 ID + "_last_episode"

### [ ] Task 3: 测试和优化
- **Priority**: P1
- **Depends On**: Task 1, Task 2
- **Description**:
  - 测试网格布局在不同屏幕尺寸下的显示效果
  - 测试记录最后选择集数的功能
  - 优化用户体验
- **Success Criteria**:
  - 网格布局在不同屏幕尺寸下显示正常
  - 记录最后选择集数功能正常工作
  - 用户体验良好
- **Test Requirements**:
  - `programmatic` TR-3.1: 应用编译通过
  - `human-judgement` TR-3.2: 网格布局美观，选择功能正常
- **Notes**: 测试不同屏幕尺寸和方向

## 3. 技术实现细节

### 3.1 网格布局平均分放实现
- 使用 GridLayoutManager 的 setSpanSizeLookup 方法
- 计算每个 item 的宽度，确保平均分放
- 考虑屏幕宽度和间距，动态调整 item 大小

### 3.2 记录最后选择集数实现
- 使用 SharedPreferences 存储最后选择的集数
- 在选集选择时更新存储
- 在初始化时从存储中读取并设置当前选择

## 4. 预期成果

- 网格布局中的 item 能够平均分放
- 能够记录并恢复最后选择的集数
- 视觉效果美观，用户体验良好

## 5. 风险评估

### 潜在问题
- 不同屏幕尺寸的适配问题
- 持久化存储的安全性
- 性能影响

### 缓解措施
- 测试不同屏幕尺寸
- 使用安全的存储方式
- 优化存储操作，避免频繁 IO