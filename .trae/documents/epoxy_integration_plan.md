# Epoxy 集成实现计划

## [ ] 任务 1: 移除BaseRecyclerViewAdapterHelper依赖，添加Epoxy依赖
- **优先级**: P0
- **依赖**: 无
- **描述**:
  - 从app/build.gradle.kts中移除BaseRecyclerViewAdapterHelper依赖
  - 添加Epoxy核心库和处理器依赖
  - 确保使用正确的依赖配置方式
- **成功标准**:
  - build.gradle.kts文件中正确配置了Epoxy依赖
  - 项目能够成功同步依赖
- **测试要求**:
  - `programmatic` TR-1.1: 运行`./gradlew sync`无错误
  - `programmatic` TR-1.2: 依赖配置正确显示在项目结构中
- **注意事项**:
  - 由于项目使用内置Kotlin支持，需要使用annotationProcessor而不是kapt

## [ ] 任务 2: 创建Epoxy模型类SpeedModel
- **优先级**: P0
- **依赖**: 任务1
- **描述**:
  - 创建SpeedModel.kt文件，使用@EpoxyModelClass注解
  - 定义speed、isSelected和onSpeedSelected属性
  - 实现bind方法，处理视图绑定和点击事件
- **成功标准**:
  - SpeedModel.kt文件创建成功
  - 模型类能够正确编译
- **测试要求**:
  - `programmatic` TR-2.1: 运行`./gradlew compileDebugKotlin`无错误
  - `human-judgement` TR-2.2: 代码结构清晰，符合Epoxy最佳实践
- **注意事项**:
  - 确保正确使用@EpoxyAttribute注解
  - 处理好点击事件的回调

## [ ] 任务 3: 修改VideoController.kt文件，使用Epoxy
- **优先级**: P0
- **依赖**: 任务2
- **描述**:
  - 移除BaseRecyclerViewAdapterHelper相关代码
  - 使用EpoxyController创建模型
  - 配置RecyclerView使用Epoxy适配器
- **成功标准**:
  - VideoController.kt文件修改成功
  - 倍速选择功能正常工作
- **测试要求**:
  - `programmatic` TR-3.1: 运行`./gradlew build`无错误
  - `human-judgement` TR-3.2: 代码逻辑清晰，符合Epoxy使用规范
- **注意事项**:
  - 确保正确使用EpoxyController和生成的模型类
  - 处理好模型的id和属性设置

## [ ] 任务 4: 测试构建和运行
- **优先级**: P1
- **依赖**: 任务3
- **描述**:
  - 运行完整的项目构建
  - 测试倍速选择功能
  - 确保所有功能正常工作
- **成功标准**:
  - 项目构建成功
  - 倍速选择弹窗正常显示
  - 倍速选择功能正常工作
- **测试要求**:
  - `programmatic` TR-4.1: 运行`./gradlew build`无错误
  - `human-judgement` TR-4.2: 倍速选择功能运行正常
- **注意事项**:
  - 测试不同倍速的选择
  - 确保弹窗能够正常显示和关闭