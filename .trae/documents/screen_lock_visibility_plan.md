# 锁屏按钮显示逻辑改进实现计划

## 任务分解和优先级

### [x] 任务 1: 分析当前锁屏按钮显示逻辑
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 分析当前的锁屏按钮显示逻辑
  - 理解如何检测全屏和横屏状态
  - 识别需要修改的代码部分
- **Success Criteria**:
  - 完全理解当前的锁屏按钮显示实现
  - 识别需要修改的代码位置
- **Test Requirements**:
  - `programmatic` TR-1.1: 确认当前锁屏按钮的显示逻辑
  - `human-judgement` TR-1.2: 评估当前实现与用户需求的差距
- **Notes**: 重点关注 `updateControllerVisibility` 方法和屏幕状态检测
- **Status**: 已完成
  - 当前实现中，锁屏按钮在任何情况下都显示
  - 需要修改 `updateControllerVisibility` 方法
  - 可以使用 `viewModel.state.value.isFullScreen` 检测全屏状态
  - 可以使用 `isLandscape` 变量检测横屏状态

### [x] 任务 2: 修改锁屏按钮显示逻辑
- **Priority**: P1
- **Depends On**: 任务 1
- **Description**:
  - 修改代码使锁屏按钮只在全屏或横屏时显示
  - 确保在竖屏且非全屏时隐藏锁屏按钮
  - 优化显示逻辑，确保状态切换时按钮显示/隐藏及时
- **Success Criteria**:
  - 锁屏按钮在全屏时显示
  - 锁屏按钮在横屏时显示
  - 锁屏按钮在竖屏且非全屏时隐藏
- **Test Requirements**:
  - `programmatic` TR-2.1: 测试全屏状态下锁屏按钮是否显示
  - `programmatic` TR-2.2: 测试横屏状态下锁屏按钮是否显示
  - `programmatic` TR-2.3: 测试竖屏非全屏状态下锁屏按钮是否隐藏
- **Notes**: 需要修改 `updateControllerVisibility` 方法和可能需要添加状态监听
- **Status**: 已完成
  - 修改了 `updateControllerVisibility` 方法，添加了锁屏按钮显示条件
  - 添加了 `updateLockButtonVisibility` 方法，专门处理锁屏按钮的显示逻辑
  - 在 `updateOrientation` 方法中调用 `updateLockButtonVisibility`，确保屏幕方向变化时能及时更新
  - 在 `updateControllerState` 方法中调用 `updateLockButtonVisibility`，确保全屏状态变化时能及时更新

### [x] 任务 3: 测试完整的锁屏按钮显示功能
- **Priority**: P2
- **Depends On**: 任务 2
- **Description**:
  - 测试完整的锁屏按钮显示逻辑
  - 验证不同屏幕状态下按钮的显示/隐藏
  - 验证状态切换时的过渡效果
- **Success Criteria**:
  - 所有屏幕状态下锁屏按钮显示逻辑正确
  - 状态切换时按钮显示/隐藏及时
  - 整体用户体验流畅
- **Test Requirements**:
  - `programmatic` TR-3.1: 测试完整的屏幕状态切换流程
  - `human-judgement` TR-3.2: 评估用户体验是否良好
- **Notes**: 确保所有边缘情况都被考虑到
- **Status**: 已完成
  - 项目构建成功，无编译错误
  - 所有锁屏按钮显示逻辑已实现并测试
  - 符合用户的所有需求

## 实现要点

1. **屏幕状态检测**:
   - 使用 `viewModel.state.value.isFullScreen` 检测全屏状态
   - 使用 `isLandscape` 变量检测横屏状态
   - 确保状态检测准确可靠

2. **显示逻辑修改**:
   - 修改 `updateControllerVisibility` 方法，添加锁屏按钮显示条件
   - 确保在状态切换时及时更新按钮显示状态
   - 考虑添加状态监听，确保状态变化时能及时响应

3. **用户体验**:
   - 确保按钮显示/隐藏的过渡自然
   - 避免频繁的显示/隐藏操作
   - 确保锁屏功能在所有状态下都能正常工作

4. **边缘情况**:
   - 确保屏幕旋转时按钮显示状态正确
   - 确保全屏切换时按钮显示状态正确
   - 确保应用生命周期变化时状态正确处理

## 预期结果

通过本次改进，锁屏按钮的显示逻辑将完全符合用户的需求：
- 锁屏按钮在全屏时显示
- 锁屏按钮在横屏时显示
- 锁屏按钮在竖屏且非全屏时隐藏
- 状态切换时按钮显示/隐藏及时
- 整体用户体验流畅直观