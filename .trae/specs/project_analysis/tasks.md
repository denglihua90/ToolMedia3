# ToolMedia3 - 项目分析任务列表

## [ ] 任务 1: 目录结构分析
- **优先级**: P0
- **Depends On**: None
- **Description**:
  - 分析项目的目录结构和文件组织
  - 识别核心模块和功能组件
  - 了解代码的分层结构
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `human-judgement` TR-1.1: 确认目录结构的完整性和合理性
  - `human-judgement` TR-1.2: 确认模块划分的清晰度
- **Notes**: 重点关注 app/src/main/java/com/dlh/toolmedia3/ 目录下的结构

## [ ] 任务 2: 核心功能分析 - 视频播放
- **优先级**: P0
- **Depends On**: 任务 1
- **Description**:
  - 分析 BaseVideoView 和相关类的实现
  - 了解 ExoPlayer 的集成方式
  - 分析视频加载、播放控制的实现
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `human-judgement` TR-2.1: 确认视频播放核心功能的完整性
  - `human-judgement` TR-2.2: 确认 ExoPlayer 集成的正确性
- **Notes**: 重点分析 BaseVideoView.kt 和相关文件

## [ ] 任务 3: 核心功能分析 - 屏幕控制
- **优先级**: P0
- **Depends On**: 任务 1
- **Description**:
  - 分析 ScreenController 的实现
  - 了解横竖屏切换和全屏模式的处理
  - 分析刘海屏适配的实现
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `human-judgement` TR-3.1: 确认屏幕控制功能的完整性
  - `human-judgement` TR-3.2: 确认刘海屏适配的正确性
- **Notes**: 重点分析 ScreenController.kt 和 CutoutUtil.kt

## [ ] 任务 4: 核心功能分析 - 自定义控制器
- **优先级**: P0
- **Depends On**: 任务 1
- **Description**:
  - 分析 VideoController 的实现
  - 了解控制器的布局和交互
  - 分析播放/暂停、进度条、全屏等控制的实现
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `human-judgement` TR-4.1: 确认控制器功能的完整性
  - `human-judgement` TR-4.2: 确认控制器交互的流畅性
- **Notes**: 重点分析 VideoController.kt 和 layout_video_controller.xml

## [ ] 任务 5: 技术架构分析 - MVI 架构
- **优先级**: P1
- **Depends On**: 任务 1
- **Description**:
  - 分析 MVI 架构的实现
  - 了解 PlayerIntent、PlayerState、PlayerEvent 的使用
  - 分析数据流和状态管理的实现
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `human-judgement` TR-5.1: 确认 MVI 架构的正确性
  - `human-judgement` TR-5.2: 确认状态管理的有效性
- **Notes**: 重点分析 architecture 目录下的文件

## [ ] 任务 6: 技术架构分析 - 其他功能
- **优先级**: P1
- **Depends On**: 任务 1
- **Description**:
  - 分析错误处理、下载管理、预加载等功能
  - 了解这些功能的实现方式和集成
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `human-judgement` TR-6.1: 确认其他功能的完整性
  - `human-judgement` TR-6.2: 确认功能集成的合理性
- **Notes**: 重点分析 download、util 等目录下的文件

## [ ] 任务 7: 问题识别和优化建议
- **优先级**: P1
- **Depends On**: 任务 2, 任务 3, 任务 4, 任务 5, 任务 6
- **Description**:
  - 识别代码中的潜在问题和优化点
  - 分析性能瓶颈和内存使用
  - 提出技术改进建议
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `human-judgement` TR-7.1: 确认问题识别的全面性
  - `human-judgement` TR-7.2: 确认优化建议的可行性
- **Notes**: 重点关注代码质量、性能和可维护性

## [ ] 任务 8: 文档整理和报告生成
- **优先级**: P2
- **Depends On**: 所有其他任务
- **Description**:
  - 整理分析结果
  - 生成详细的项目分析报告
  - 提供技术建议和后续计划
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4
- **Test Requirements**:
  - `human-judgement` TR-8.1: 确认报告的完整性和准确性
  - `human-judgement` TR-8.2: 确认分析结果的清晰度
- **Notes**: 报告应包含项目结构、功能实现、技术架构和优化建议等部分