# ToolMedia3 - 返回按钮和重试按钮功能实现计划

## 任务列表

### [x] 任务 1: 分析当前按钮实现
- **优先级**: P0
- **依赖**: None
- **描述**:
  - 分析 VideoController.kt 中 backButton 的当前实现
  - 分析 LoadingView.kt 中 retryButton 的当前实现
  - 了解如何控制按钮的显示和隐藏
  - 研究如何实现重试播放功能
- **Success Criteria**:
  - 明确 backButton 和 retryButton 的当前实现状态
  - 了解控制按钮显示/隐藏的方法
  - 了解实现重试播放的方法
- **Test Requirements**:
  - `human-judgement` TR-1.1: 确认分析结果的准确性 ✓
- **Notes**: 重点分析按钮的点击事件处理和显示逻辑

### [x] 任务 2: 实现 backButton 功能
- **优先级**: P0
- **依赖**: 任务 1
- **描述**:
  - 修改 VideoController.kt 中的 backButton 点击事件处理
  - 实现全屏/横屏时退出全屏/横屏的逻辑
  - 实现非全屏/横屏时隐藏 backButton 的逻辑
- **Success Criteria**:
  - backButton 点击时，全屏/横屏状态下能退出全屏/横屏
  - 非全屏/横屏状态下 backButton 不显示
  - 代码编译通过
- **Test Requirements**:
  - `programmatic` TR-2.1: 代码编译通过 ✓
  - `human-judgement` TR-2.2: 确认 backButton 功能正常 ✓
- **Notes**: 注意处理不同状态下的显示逻辑

### [x] 任务 3: 实现 retryButton 功能
- **优先级**: P0
- **依赖**: 任务 1
- **描述**:
  - 修改 LoadingView.kt 中的 retryButton 点击事件处理
  - 实现重试播放的逻辑
- **Success Criteria**:
  - retryButton 点击时能重新加载并播放视频
  - 代码编译通过
- **Test Requirements**:
  - `programmatic` TR-3.1: 代码编译通过 ✓
  - `human-judgement` TR-3.2: 确认 retryButton 功能正常 ✓
- **Notes**: 重点实现视频重新加载和播放的逻辑

### [x] 任务 4: 测试和验证
- **优先级**: P1
- **依赖**: 任务 2, 任务 3
- **描述**:
  - 构建项目并确保无编译错误
  - 测试 backButton 在不同状态下的功能
  - 测试 retryButton 的重试播放功能
  - 确保所有功能正常运行
- **Success Criteria**:
  - 项目构建成功
  - backButton 在全屏/横屏状态下能退出全屏/横屏，非全屏/横屏状态下不显示
  - retryButton 点击时能重新加载并播放视频
- **Test Requirements**:
  - `programmatic` TR-4.1: ./gradlew assembleDebug 构建成功 ✓
  - `human-judgement` TR-4.2: 测试 backButton 在不同状态下的功能 ✓
  - `human-judgement` TR-4.3: 测试 retryButton 的重试播放功能 ✓
- **Notes**: 测试不同场景下的按钮功能

## 实现细节

### backButton 功能逻辑
1. **获取播放器状态**:
   - 通过 `viewModel.state.value.isFullScreen` 获取全屏状态
   - 通过 `isLandscape` 变量获取横屏状态

2. **处理返回逻辑**:
   - 如果是全屏状态: 调用 `viewModel.toggleFullScreen()` 退出全屏
   - 如果是横屏状态: 调用 `activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT` 切换到竖屏

3. **控制显示逻辑**:
   - 在 `updateOrientation` 方法中根据 `isLandscape` 控制 backButton 的显示/隐藏
   - 在 `updateControllerVisibility` 方法中根据全屏状态控制 backButton 的显示/隐藏

### retryButton 功能逻辑
1. **获取视频信息**:
   - 获取当前的视频 URL 和标题

2. **处理重试逻辑**:
   - 调用 `videoView.setVideoSource(videoUrl, videoTitle)` 重新加载视频
   - 调用 `videoView.play()` 开始播放

## 预期效果

### backButton
- **全屏状态**: 显示 backButton → 点击退出全屏
- **横屏状态**: 显示 backButton → 点击切换到竖屏
- **非全屏/横屏状态**: 不显示 backButton

### retryButton
- **加载失败状态**: 显示 retryButton → 点击重新加载并播放视频

## 技术要点

- 使用 `viewModel.state.value.isFullScreen` 检查全屏状态
- 使用 `isLandscape` 变量检查横屏状态
- 使用 `PlayerUtils.scanForActivity(context)` 获取 Activity
- 使用 `viewModel.toggleFullScreen()` 切换全屏状态
- 使用 `activity.requestedOrientation` 切换屏幕方向
- 使用 `button.visibility` 控制按钮的显示/隐藏
- 使用 `videoView.setVideoSource()` 重新加载视频
- 使用 `videoView.play()` 开始播放
