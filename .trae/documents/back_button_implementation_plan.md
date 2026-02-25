# ToolMedia3 - 返回按钮功能实现计划

## 任务列表

### [x] 任务 1: 分析当前返回按钮实现
- **优先级**: P0
- **依赖**: None
- **描述**:
  - 分析 VideoController.kt 中返回按钮的当前实现
  - 了解如何获取播放器的全屏和横屏状态
  - 研究如何退出全屏/横屏以及如何退出应用
- **Success Criteria**:
  - 明确返回按钮的当前实现状态
  - 了解获取播放器状态的方法
  - 了解退出全屏/横屏和退出应用的实现方法
- **Test Requirements**:
  - `human-judgement` TR-1.1: 确认分析结果的准确性 ✓
- **Notes**: 重点分析 binding.btnBack.setOnClickListener 的实现

### [x] 任务 2: 实现返回按钮功能
- **优先级**: P0
- **依赖**: 任务 1
- **描述**:
  - 修改 VideoController.kt 中的返回按钮点击事件处理
  - 实现全屏/横屏时退出全屏/横屏的逻辑
  - 实现非全屏/横屏时退出应用的逻辑
- **Success Criteria**:
  - 返回按钮点击时，全屏/横屏状态下能退出全屏/横屏
  - 非全屏/横屏状态下能退出应用
  - 代码编译通过
- **Test Requirements**:
  - `programmatic` TR-2.1: 代码编译通过 ✓
  - `human-judgement` TR-2.2: 确认返回按钮功能正常 ✓
- **Notes**: 注意处理不同状态的判断逻辑

### [x] 任务 3: 测试和验证
- **优先级**: P1
- **依赖**: 任务 2
- **描述**:
  - 构建项目并确保无编译错误
  - 测试返回按钮在不同状态下的功能
  - 确保所有功能正常运行
- **Success Criteria**:
  - 项目构建成功
  - 返回按钮在全屏/横屏状态下能退出全屏/横屏
  - 返回按钮在非全屏/横屏状态下能退出应用
- **Test Requirements**:
  - `programmatic` TR-3.1: ./gradlew assembleDebug 构建成功 ✓
  - `human-judgement` TR-3.2: 测试返回按钮在不同状态下的功能 ✓
- **Notes**: 测试不同场景下的返回按钮功能

## 实现细节

### 功能逻辑
1. **获取播放器状态**:
   - 通过 `viewModel.state.value.isFullScreen` 获取全屏状态
   - 通过 `isLandscape` 变量获取横屏状态

2. **处理返回逻辑**:
   - 如果是全屏状态: 调用 `viewModel.toggleFullScreen()` 退出全屏
   - 如果是横屏状态: 调用 `activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT` 切换到竖屏
   - 如果既不是全屏也不是横屏: 调用 `activity.finish()` 退出应用

3. **获取 Activity**:
   - 使用 `PlayerUtils.scanForActivity(context)` 获取当前 Activity

## 预期效果

- **全屏状态**: 点击返回按钮 → 退出全屏
- **横屏状态**: 点击返回按钮 → 切换到竖屏
- **非全屏/横屏状态**: 点击返回按钮 → 退出应用

## 技术要点

- 使用 `viewModel.state.value.isFullScreen` 检查全屏状态
- 使用 `isLandscape` 变量检查横屏状态
- 使用 `PlayerUtils.scanForActivity(context)` 获取 Activity
- 使用 `viewModel.toggleFullScreen()` 切换全屏状态
- 使用 `activity.requestedOrientation` 切换屏幕方向
- 使用 `activity.finish()` 退出应用
