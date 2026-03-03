# PlaybackSourceActivity 网络请求 MVI 架构实现计划

## [ ] Task 1: 扩展 SourceIntent 密封类
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 在 SourceIntent 中添加测试源地址的意图 TestSource
  - 为 TestSource 意图添加 url 参数
- **Success Criteria**:
  - Intent 类能够正确定义测试源地址的操作
- **Test Requirements**:
  - `programmatic` TR-1.1: Intent 类编译通过
  - `human-judgement` TR-1.2: Intent 设计合理
- **Notes**: 考虑添加错误处理相关的 Intent

## [ ] Task 2: 扩展 SourceState 数据类
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 在 SourceState 中添加网络请求相关的状态字段
  - 包括 isTesting (是否正在测试)、testResult (测试结果) 等字段
- **Success Criteria**:
  - State 类能够正确表示网络请求的各种状态
- **Test Requirements**:
  - `programmatic` TR-2.1: State 类编译通过
  - `human-judgement` TR-2.2: State 设计合理
- **Notes**: 考虑添加网络请求的加载状态、成功/失败状态等

## [ ] Task 3: 扩展 SourceEvent 密封类
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 在 SourceEvent 中添加网络请求相关的事件
  - 包括测试成功、测试失败等事件
- **Success Criteria**:
  - Event 类能够正确表示网络请求的各种事件
- **Test Requirements**:
  - `programmatic` TR-3.1: Event 类编译通过
  - `human-judgement` TR-3.2: Event 设计合理
- **Notes**: 考虑添加错误事件和成功事件

## [ ] Task 4: 扩展 SourceProcessor 类
- **Priority**: P0
- **Depends On**: Task 1, Task 2, Task 3
- **Description**:
  - 在 SourceProcessor 中添加处理 TestSource 意图的方法
  - 实现网络请求的逻辑，使用 OkHttpClient 执行 HTTP 请求
  - 更新状态和发送事件
- **Success Criteria**:
  - Processor 能够正确处理 TestSource 意图并更新状态
- **Test Requirements**:
  - `programmatic` TR-4.1: Processor 类编译通过
  - `human-judgement` TR-4.2: Processor 逻辑合理
- **Notes**: 考虑添加网络请求的错误处理

## [ ] Task 5: 扩展 SourceViewModel 类
- **Priority**: P0
- **Depends On**: Task 4
- **Description**:
  - 在 SourceViewModel 中添加 testSource 方法
  - 该方法发送 TestSource 意图
- **Success Criteria**:
  - ViewModel 能够正确发送 TestSource 意图
- **Test Requirements**:
  - `programmatic` TR-5.1: ViewModel 类编译通过
  - `human-judgement` TR-5.2: ViewModel 设计合理
- **Notes**: 考虑添加参数验证

## [ ] Task 6: 修改 PlaybackSourceActivity
- **Priority**: P0
- **Depends On**: Task 5
- **Description**:
  - 修改 testSourceUrl 方法，使用 SourceViewModel 的 testSource 方法
  - 更新观察状态的逻辑，处理网络请求相关的状态
  - 更新观察事件的逻辑，处理网络请求相关的事件
- **Success Criteria**:
  - 测试按钮能够正确执行网络请求并显示结果
- **Test Requirements**:
  - `programmatic` TR-6.1: Activity 编译通过
  - `human-judgement` TR-6.2: UI 响应合理
- **Notes**: 考虑添加加载状态和错误处理的 UI 反馈

## [ ] Task 7: 测试功能
- **Priority**: P1
- **Depends On**: Task 6
- **Description**:
  - 测试网络请求功能是否正常
  - 测试加载状态和错误处理是否合理
  - 测试与保存功能的集成是否正常
- **Success Criteria**:
  - 所有功能都能正常工作
- **Test Requirements**:
  - `programmatic` TR-7.1: 功能测试通过
  - `human-judgement` TR-7.2: 用户体验良好
- **Notes**: 考虑测试边界情况和错误情况
