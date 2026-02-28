# 倍速选择列表实现计划

## 1. 任务概述
通过SpeedListView使用Epoxy实现单选列表，用于选择视频播放倍速。

## 2. 详细任务

### [x] 任务1: 完善SpeedListView的Epoxy实现
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 取消注释SpeedListView中的Epoxy实现代码
  - 使用SpeedModel替代匿名EpoxyModel
  - 实现倍速选项的显示和点击事件
- **Success Criteria**:
  - 倍速选项列表能够正常显示
  - 点击倍速选项能够触发回调
- **Test Requirements**:
  - `programmatic` TR-1.1: 打开倍速选择列表，确认所有倍速选项（0.5x, 0.75x, 1.0x, 1.25x, 1.5x, 2.0x）都能正确显示
  - `programmatic` TR-1.2: 点击任意倍速选项，确认能够触发回调并关闭列表

### [x] 任务2: 实现单选功能
- **Priority**: P0
- **Depends On**: 任务1
- **Description**:
  - 添加当前倍速状态管理
  - 实现点击倍速选项后的选中状态更新
  - 确保只有一个倍速选项被选中
- **Success Criteria**:
  - 初始状态下，默认倍速（1.0x）被选中
  - 点击其他倍速选项后，该选项被选中，之前选中的选项取消选中
- **Test Requirements**:
  - `programmatic` TR-2.1: 打开倍速选择列表，确认1.0x选项默认被选中
  - `programmatic` TR-2.2: 点击其他倍速选项（如1.5x），确认该选项被选中，1.0x选项取消选中
  - `programmatic` TR-2.3: 再次点击其他倍速选项（如2.0x），确认该选项被选中，之前的1.5x选项取消选中

### [x] 任务3: 添加回调接口
- **Priority**: P0
- **Depends On**: 任务2
- **Description**:
  - 添加倍速选择回调接口
  - 实现回调方法，将选中的倍速值传递给调用者
- **Success Criteria**:
  - 当用户选择倍速时，能够通过回调接口通知调用者
  - 调用者能够获取到选中的倍速值
- **Test Requirements**:
  - `programmatic` TR-3.1: 实现回调接口并注册到SpeedListView
  - `programmatic` TR-3.2: 选择不同的倍速选项，确认回调能够正确传递选中的倍速值

### [x] 任务4: 集成到视频控制器
- **Priority**: P1
- **Depends On**: 任务3
- **Description**:
  - 在VideoController中添加倍速选择功能
  - 实现倍速选择按钮的点击事件，打开SpeedListView
  - 实现倍速选择回调，更新播放器的播放速度
- **Success Criteria**:
  - 点击视频控制器中的倍速按钮，能够打开倍速选择列表
  - 选择倍速后，播放器的播放速度能够正确更新
- **Test Requirements**:
  - `programmatic` TR-4.1: 点击视频控制器中的倍速按钮，确认倍速选择列表能够打开
  - `programmatic` TR-4.2: 在倍速选择列表中选择一个倍速值，确认播放器的播放速度能够正确更新
  - `human-judgement` TR-4.3: 确认倍速选择操作流畅，用户体验良好

## 3. 技术实现要点

1. **Epoxy使用**:
   - 使用SpeedModel来创建倍速选项的模型
   - 在EpoxyController中构建模型列表
   - 为每个模型设置speed、isSelected和onSpeedSelected属性

2. **单选逻辑**:
   - 维护一个当前选中的倍速值
   - 当用户点击倍速选项时，更新选中状态并刷新EpoxyController

3. **回调机制**:
   - 定义一个接口用于倍速选择回调
   - 在SpeedListView中添加回调设置方法
   - 在倍速选项被点击时调用回调方法

4. **集成到控制器**:
   - 在VideoController中添加倍速按钮
   - 实现按钮点击事件，创建并显示SpeedListView
   - 实现回调处理，调用viewModel.setPlaybackSpeed()更新播放速度

## 4. 预期成果

完成后，用户将能够通过视频控制器的倍速按钮打开一个弹出的倍速选择列表，选择不同的播放速度（0.5x, 0.75x, 1.0x, 1.25x, 1.5x, 2.0x），选择后播放器的播放速度会立即更新，并且选中的倍速选项会在列表中高亮显示。