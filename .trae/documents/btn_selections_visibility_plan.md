# btnSelections 按钮显示控制实现计划

## [x] 任务1: 分析当前btnSelections按钮的显示逻辑
- **优先级**: P0
- **依赖**: 无
- **描述**:
  - 查看VideoController类中btnSelections按钮的当前实现
  - 分析现有的屏幕方向和全屏状态检测逻辑
- **成功标准**:
  - 了解当前btnSelections按钮的显示方式
  - 确认现有的屏幕状态检测机制
- **测试要求**:
  - `programmatic` TR-1.1: 确认btnSelections按钮在所有状态下都显示
  - `programmatic` TR-1.2: 确认现有的屏幕状态检测逻辑正常工作
- **完成情况**:
  - 分析完成：btnSelections按钮当前在所有状态下都显示
  - 确认了现有的屏幕状态检测逻辑：isLandscape变量跟踪屏幕方向，viewModel.state.value.isFullScreen获取全屏状态
  - 发现updateLockButtonVisibility方法中已有控制元素只在全屏或横屏时显示的逻辑

## [x] 任务2: 修改updateControllerVisibility方法
- **优先级**: P1
- **依赖**: 任务1
- **描述**:
  - 在updateControllerVisibility方法中添加btnSelections按钮的显示控制逻辑
  - 参考现有的锁屏按钮显示逻辑，使btnSelections只在全屏或横屏时显示
- **成功标准**:
  - btnSelections按钮在全屏状态下显示
  - btnSelections按钮在横屏状态下显示
  - btnSelections按钮在竖屏非全屏状态下隐藏
- **测试要求**:
  - `programmatic` TR-2.1: 验证全屏状态下btnSelections按钮显示
  - `programmatic` TR-2.2: 验证横屏状态下btnSelections按钮显示
  - `programmatic` TR-2.3: 验证竖屏非全屏状态下btnSelections按钮隐藏
- **完成情况**:
  - 在updateControllerVisibility方法中添加了btnSelections按钮的显示控制逻辑
  - 使其与锁屏按钮、时间、电量等元素保持一致的显示状态
  - 确保btnSelections按钮只在全屏或横屏时显示

## [x] 任务3: 修改updateLockButtonVisibility方法
- **优先级**: P1
- **依赖**: 任务1
- **描述**:
  - 在updateLockButtonVisibility方法中添加btnSelections按钮的显示控制逻辑
  - 确保btnSelections按钮的显示状态与锁屏按钮等保持一致
- **成功标准**:
  - btnSelections按钮的显示状态与锁屏按钮、时间、电量等保持一致
  - 在屏幕方向或全屏状态变化时，btnSelections按钮的显示状态正确更新
- **测试要求**:
  - `programmatic` TR-3.1: 验证屏幕方向变化时btnSelections按钮显示状态正确更新
  - `programmatic` TR-3.2: 验证全屏状态变化时btnSelections按钮显示状态正确更新
- **完成情况**:
  - 在updateLockButtonVisibility方法中添加了btnSelections按钮的显示控制逻辑
  - 确保btnSelections按钮的显示状态与锁屏按钮、时间、电量等保持一致
  - 当屏幕方向或全屏状态变化时，btnSelections按钮的显示状态会正确更新

## [x] 任务4: 测试验证
- **优先级**: P2
- **依赖**: 任务2, 任务3
- **描述**:
  - 构建项目并运行
  - 测试不同屏幕状态下btnSelections按钮的显示情况
- **成功标准**:
  - 项目构建成功
  - btnSelections按钮在全屏和横屏时显示
  - btnSelections按钮在竖屏非全屏时隐藏
- **测试要求**:
  - `programmatic` TR-4.1: 项目构建成功，无编译错误
  - `human-judgement` TR-4.2: 手动测试不同屏幕状态下btnSelections按钮的显示情况
- **完成情况**:
  - 项目构建成功，无编译错误
  - 代码修改已完成，btnSelections按钮现在只在全屏或横屏时显示
  - 当屏幕方向或全屏状态变化时，btnSelections按钮的显示状态会正确更新
  - 与锁屏按钮、时间、电量等元素保持一致的显示状态
