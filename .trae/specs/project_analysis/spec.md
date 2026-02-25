# ToolMedia3 - 项目分析文档

## Overview
- **Summary**: ToolMedia3 是一个基于 Android Media3/ExoPlayer 开发的视频播放器应用，提供完整的视频播放功能、自定义控制器、刘海屏适配和屏幕控制等特性。
- **Purpose**: 分析项目结构、功能实现和技术架构，为后续的优化和扩展提供基础。
- **Target Users**: 开发者、测试人员和项目维护者。

## Goals
- 全面分析项目的目录结构和代码组织
- 详细了解核心功能的实现方式
- 识别潜在的优化点和问题
- 为后续的功能扩展和技术升级提供参考

## Non-Goals (Out of Scope)
- 实现新功能或修复现有问题
- 进行代码重构或优化
- 修改项目的技术架构
- 编写测试代码

## Background & Context
- 项目基于 Android Media3/ExoPlayer 构建，使用 Kotlin 语言开发
- 采用 MVI 架构模式进行状态管理
- 包含刘海屏适配、屏幕控制、错误处理等功能
- 最近的修改集中在刘海屏适配和废弃 API 优化

## Functional Requirements
- **FR-1**: 视频播放核心功能 - 支持视频加载、播放、暂停、进度控制等基础操作
- **FR-2**: 自定义控制器 - 包含播放/暂停、进度条、全屏、倍速、清晰度选择等控制
- **FR-3**: 屏幕控制 - 支持横竖屏切换、全屏模式、刘海屏适配
- **FR-4**: 错误处理 - 处理网络错误、加载失败等异常情况
- **FR-5**: 其他功能 - 支持视频下载、预加载、播放进度保存等

## Non-Functional Requirements
- **NFR-1**: 性能 - 视频播放流畅，加载速度快
- **NFR-2**: 兼容性 - 支持不同 Android 版本和设备
- **NFR-3**: 可维护性 - 代码结构清晰，易于理解和扩展
- **NFR-4**: 用户体验 - 界面美观，操作流畅

## Constraints
- **Technical**: Android 平台，Kotlin 语言，Media3/ExoPlayer 库
- **Dependencies**: 依赖 Media3/ExoPlayer 进行视频播放

## Assumptions
- 项目处于正常开发状态，代码可以正常编译和运行
- 所有依赖库都已正确配置
- 项目遵循 Android 开发最佳实践

## Acceptance Criteria

### AC-1: 项目结构分析
- **Given**: 项目代码完整可用
- **When**: 分析项目目录结构和代码组织
- **Then**: 生成详细的项目结构分析报告
- **Verification**: `human-judgment`

### AC-2: 核心功能分析
- **Given**: 项目代码完整可用
- **When**: 分析视频播放、屏幕控制、刘海屏适配等核心功能
- **Then**: 生成详细的功能实现分析报告
- **Verification**: `human-judgment`

### AC-3: 技术架构分析
- **Given**: 项目代码完整可用
- **When**: 分析 MVI 架构、状态管理、数据流等技术实现
- **Then**: 生成详细的技术架构分析报告
- **Verification**: `human-judgment`

### AC-4: 问题识别
- **Given**: 项目代码完整可用
- **When**: 分析代码中的潜在问题和优化点
- **Then**: 生成详细的问题识别报告
- **Verification**: `human-judgment`

## Open Questions
- 项目的具体业务需求和目标用户群体是什么？
- 项目是否有后续的功能扩展计划？
- 项目的性能和稳定性要求是什么？
- 项目的测试覆盖情况如何？