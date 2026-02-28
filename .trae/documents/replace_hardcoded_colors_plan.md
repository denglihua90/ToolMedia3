# 替换硬编码颜色值计划

## 任务目标
将项目中所有硬编码的颜色值替换为colors.xml中的引用，确保UI颜色的一致性和可维护性。

## 已识别的硬编码颜色值

### 1. 布局文件
- **layout_loading_view.xml**: `#80000000`
- **layout_speed_selection.xml**: `#96000000`
- **layout_video_controller.xml**: `#96000000`

### 2. Drawable文件
- **bg_speed_selected.xml**: `#44ffffff`
- **seekbar_progress.xml**: 多个颜色值
- **circle_progress_bar.xml**: `#40FFFFFF` 和 `#FFFFFF`
- **多个vector drawable文件**: `#FFFFFF` (tint属性)
- **ic_launcher_background.xml**: 多个颜色值

### 3. 代码文件
- **StatusBarUtil.kt**: `#0000FF` (注释中的示例)

## 替换计划

### 任务 1: 替换布局文件中的硬编码颜色值
- **优先级**: P0
- **依赖**: 无
- **描述**:
  - 替换layout_loading_view.xml中的`#80000000`为`@color/black_alpha_50`
  - 替换layout_speed_selection.xml中的`#96000000`为`@color/black_alpha_60`
  - 替换layout_video_controller.xml中的`#96000000`为`@color/black_alpha_60`
- **成功标准**:
  - 所有布局文件中的硬编码颜色值已替换为colors.xml引用
- **测试要求**:
  - `programmatic`: 构建项目无错误
  - `human-judgment`: 布局显示效果与替换前一致

### 任务 2: 替换Drawable文件中的硬编码颜色值
- **优先级**: P0
- **依赖**: 任务1
- **描述**:
  - 替换bg_speed_selected.xml中的`#44ffffff`为`@color/white`（带透明度）
  - 替换circle_progress_bar.xml中的`#40FFFFFF`为`@color/white`（带透明度）和`#FFFFFF`为`@color/white`
  - 替换vector drawable文件中的`#FFFFFF`为`@color/white`
- **成功标准**:
  - 所有Drawable文件中的硬编码颜色值已替换为colors.xml引用
- **测试要求**:
  - `programmatic`: 构建项目无错误
  - `human-judgment`: Drawable显示效果与替换前一致

### 任务 3: 替换seekbar_progress.xml中的硬编码颜色值
- **优先级**: P1
- **依赖**: 任务2
- **描述**:
  - 替换seekbar_progress.xml中的所有硬编码颜色值为colors.xml引用
- **成功标准**:
  - seekbar_progress.xml中的所有硬编码颜色值已替换为colors.xml引用
- **测试要求**:
  - `programmatic`: 构建项目无错误
  - `human-judgment`: 进度条显示效果与替换前一致

### 任务 4: 处理ic_launcher_background.xml中的颜色值
- **优先级**: P2
- **依赖**: 任务3
- **描述**:
  - 分析ic_launcher_background.xml中的硬编码颜色值
  - 根据需要替换为colors.xml引用
- **成功标准**:
  - ic_launcher_background.xml中的硬编码颜色值已适当处理
- **测试要求**:
  - `programmatic`: 构建项目无错误
  - `human-judgment`: 启动图标显示效果与替换前一致

### 任务 5: 清理代码文件中的硬编码颜色值
- **优先级**: P2
- **依赖**: 任务4
- **描述**:
  - 检查StatusBarUtil.kt中的硬编码颜色值
  - 替换或移除硬编码颜色值
- **成功标准**:
  - 代码文件中的硬编码颜色值已清理
- **测试要求**:
  - `programmatic`: 构建项目无错误
  - `human-judgment`: 代码可读性良好

### 任务 6: 验证所有替换
- **优先级**: P0
- **依赖**: 任务5
- **描述**:
  - 构建项目确保无错误
  - 检查所有替换是否正确
  - 验证UI显示效果
- **成功标准**:
  - 项目构建成功
  - 所有硬编码颜色值已替换为colors.xml引用
  - UI显示效果与替换前一致
- **测试要求**:
  - `programmatic`: 构建项目无错误
  - `human-judgment`: UI显示效果正常

## 替换规则
- 对于透明度颜色值，使用colors.xml中定义的对应透明度颜色
- 对于纯色值，使用colors.xml中定义的对应颜色
- 确保替换后的颜色效果与替换前一致

## 预期结果
- 所有硬编码颜色值已替换为colors.xml中的引用
- 项目构建成功
- UI显示效果与替换前一致
- 代码和资源文件更加可维护