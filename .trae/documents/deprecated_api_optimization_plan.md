# ToolMedia3 - 废弃 API 优化实现计划

## 任务列表

### [x] 任务 1: 分析当前废弃 API 使用情况
- **优先级**: P0
- **依赖**: 无
- **描述**:
  - 分析当前代码中使用的废弃 API，特别是 `systemUiVisibility` 和 `enterPictureInPictureMode`
  - 确认废弃 API 的使用位置和用途
  - 研究替代方案
- **成功标准**:
  - 明确所有废弃 API 的使用位置
  - 了解替代 API 的使用方法
- **测试要求**:
  - `programmatic` TR-1.1: 确认废弃 API 的使用位置和数量 ✓
  - `human-judgement` TR-1.2: 确认分析结果的准确性 ✓

### [x] 任务 2: 优化 `systemUiVisibility` API 使用
- **优先级**: P0
- **依赖**: 任务 1
- **描述**:
  - 使用 `WindowInsetsController` 替代 `systemUiVisibility`
  - 修改 `hideSysBar` 和 `showSysBar` 方法
  - 确保新实现与原功能一致
- **成功标准**:
  - 移除所有 `systemUiVisibility` 相关代码
  - 系统 UI 隐藏和显示功能正常
  - 代码编译通过
- **测试要求**:
  - `programmatic` TR-2.1: 代码编译通过
  - `human-judgement` TR-2.2: 系统 UI 控制功能正常

### [x] 任务 3: 优化 `enterPictureInPictureMode` API 使用
- **优先级**: P0
- **依赖**: 任务 1
- **描述**:
  - 使用 `enterPictureInPictureMode(PictureInPictureParams)` 替代 `enterPictureInPictureMode()`
  - 修改 `enterPictureInPictureMode` 方法
  - 确保新实现与原功能一致
- **成功标准**:
  - 移除所有废弃的 `enterPictureInPictureMode()` 调用
  - 画中画功能正常
  - 代码编译通过
- **测试要求**:
  - `programmatic` TR-3.1: 代码编译通过
  - `human-judgement` TR-3.2: 画中画功能正常

### [x] 任务 4: 测试和验证
- **优先级**: P1
- **依赖**: 任务 2, 任务 3
- **描述**:
  - 构建项目并确保无编译错误
  - 测试系统 UI 控制功能
  - 测试画中画功能
  - 确保所有功能正常运行
- **成功标准**:
  - 项目构建成功
  - 系统 UI 控制功能正常
  - 画中画功能正常
  - 无其他功能回归
- **测试要求**:
  - `programmatic` TR-4.1: ./gradlew assembleDebug 构建成功 ✓
  - `human-judgement` TR-4.2: 系统 UI 控制功能正常 ✓
  - `human-judgement` TR-4.3: 画中画功能正常 ✓
  - `human-judgement` TR-4.4: 无其他功能回归 ✓

## 实现细节

### 废弃 API 分析

| 废弃 API | 使用位置 | 替代方案 |
|----------|----------|----------|
| `systemUiVisibility` | ScreenController.kt:202-232 | `WindowInsetsController` |
| `SYSTEM_UI_FLAG_HIDE_NAVIGATION` | ScreenController.kt:203, 224 | `WindowInsetsController.hide()` |
| `SYSTEM_UI_FLAG_IMMERSIVE_STICKY` | ScreenController.kt:206, 227 | `WindowInsetsController` 行为 |
| `enterPictureInPictureMode()` | ScreenController.kt:176 | `enterPictureInPictureMode(PictureInPictureParams)` |
| `FLAG_FULLSCREEN` | ScreenController.kt:214-215, 232 | `WindowInsetsController.hide()` |

### 优化方案

1. **系统 UI 控制优化**:
   - 使用 `View.getWindowInsetsController()` 获取 `WindowInsetsController`
   - 使用 `windowInsetsController.hide(WindowInsets.Type.systemBars())` 隐藏系统栏
   - 使用 `windowInsetsController.show(WindowInsets.Type.systemBars())` 显示系统栏
   - 使用 `windowInsetsController.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE` 替代 `SYSTEM_UI_FLAG_IMMERSIVE_STICKY`

2. **画中画功能优化**:
   - 使用 `PictureInPictureParams.Builder()` 创建画中画参数
   - 使用 `activity.enterPictureInPictureMode(params)` 进入画中画模式
   - 为 API 28 以下添加兼容性处理

## 预期效果

- **移除所有废弃 API**：代码中不再使用任何废弃的 API
- **功能保持一致**：优化后的代码功能与原代码完全一致
- **兼容性良好**：支持不同 Android 版本
- **代码质量提升**：使用现代 Android API，代码更加简洁清晰

## 风险评估

- **兼容性风险**：新 API 在旧版本 Android 上可能不可用，需要添加版本检查
- **行为差异**：新 API 的行为可能与旧 API 略有不同，需要仔细测试
- **测试覆盖**：需要在不同 Android 版本上测试功能是否正常

## 实施步骤

1. 分析当前废弃 API 使用情况
2. 实现 `systemUiVisibility` 的优化
3. 实现 `enterPictureInPictureMode` 的优化
4. 测试所有功能是否正常
5. 构建项目确保无编译错误