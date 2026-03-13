# PlayerState 和 PlayerEvent 集成 - 产品需求文档

## 概述
- **摘要**：本项目旨在结合使用 PlayerState 和 PlayerEvent，使用状态存储持久的 UI 状态，使用事件处理状态转换和一次性事件，以实现更清晰、更可维护的播放器架构。
- **目的**：解决当前播放器架构中状态管理和事件处理的分离问题，使代码结构更清晰，易于维护和扩展。
- **目标用户**：开发人员，特别是负责播放器功能开发和维护的工程师。

## 目标
- 结合使用 PlayerState 和 PlayerEvent，实现状态和事件的分离管理
- 使用 PlayerState 存储持久的 UI 状态，确保 UI 展示的一致性
- 使用 PlayerEvent 处理状态转换和一次性事件，提高代码的可维护性
- 确保 PlayerEvent 能够正确发送和处理，包括播放状态变化、错误事件等
- 保持与现有代码的兼容性，最小化对现有功能的影响

## 非目标（范围外）
- 不修改现有 PlayerState 的数据结构
- 不重写现有的播放器核心逻辑
- 不引入新的依赖库
- 不改变现有的 UI 布局和交互方式

## 背景与上下文
- 项目当前使用 PlayerState 存储播放器状态，但 PlayerEvent 尚未被充分利用
- PlayerProcessor 中已经定义了 events 流，但没有实际发送事件
- BaseVideoView 中只观察了 PlayerState，没有处理 PlayerEvent
- 结合使用状态和事件可以使代码结构更清晰，便于维护和扩展

## 功能需求
- **FR-1**：修改 PlayerProcessor，在适当的时机发送 PlayerEvent
- **FR-2**：更新 BaseVideoView，实现对 PlayerEvent 的观察和处理
- **FR-3**：确保 PlayerEvent 能够正确处理状态转换和一次性事件
- **FR-4**：保持与现有 PlayerState 管理的兼容性

## 非功能需求
- **NFR-1**：代码结构清晰，易于理解和维护
- **NFR-2**：性能影响最小化，不影响播放器的流畅运行
- **NFR-3**：遵循项目现有的代码风格和架构模式

## 约束
- **技术**：使用 Kotlin 语言，基于现有的 Coroutine Flow 架构
- **依赖**：依赖现有的 ExoPlayer 库和项目架构

## 假设
- 项目已经正确配置了 Coroutine Flow 和播放器架构
- 开发人员熟悉 Kotlin 协程和 Flow API
- 现有代码结构可以支持 PlayerEvent 的集成

## 验收标准

### AC-1：PlayerProcessor 能够发送 PlayerEvent
- **给定**：PlayerProcessor 初始化完成
- **当**：播放器状态发生变化时
- **那么**：PlayerProcessor 应该发送相应的 PlayerEvent
- **验证**：`programmatic`
- **说明**：需要在 Player.Listener 的回调方法中发送相应的事件

### AC-2：BaseVideoView 能够观察和处理 PlayerEvent
- **给定**：BaseVideoView 初始化完成
- **当**：PlayerEvent 发送时
- **那么**：BaseVideoView 应该能够接收并处理这些事件
- **验证**：`programmatic`
- **说明**：需要实现 observePlayerEvents 方法来观察和处理事件

### AC-3：PlayerEvent 能够正确处理状态转换
- **给定**：播放器状态发生转换（如从缓冲到就绪）
- **当**：相应的 PlayerEvent 被发送和处理时
- **那么**：UI 应该能够正确响应这些状态转换
- **验证**：`human-judgment`
- **说明**：需要确保事件处理逻辑能够正确反映状态变化

### AC-4：PlayerEvent 能够正确处理一次性事件
- **给定**：发生一次性事件（如下载完成、截图成功）
- **当**：相应的 PlayerEvent 被发送和处理时
- **那么**：UI 应该能够正确响应这些一次性事件
- **验证**：`human-judgment`
- **说明**：需要确保一次性事件能够被正确处理，不会重复触发

### AC-5：与现有 PlayerState 管理兼容
- **给定**：PlayerState 和 PlayerEvent 同时使用
- **当**：播放器状态发生变化时
- **那么**：PlayerState 和 PlayerEvent 应该保持一致，不会产生冲突
- **验证**：`programmatic`
- **说明**：需要确保状态更新和事件发送的一致性

## 开放问题
- [ ] 是否需要为所有 PlayerState 的变化都发送对应的 PlayerEvent？
- [ ] 如何处理事件处理过程中的错误？
- [ ] 是否需要添加新的 PlayerEvent 类型？