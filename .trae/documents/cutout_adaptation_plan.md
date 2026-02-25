# ToolMedia3 - 刘海屏适配到PlayerView的实现计划

## 实现方案

### 方案1: 扩展CutoutUtil.adaptCutout方法

* 修改CutoutUtil.adaptCutout方法，添加对playerView的支持

* 在BaseVideoView中调用时同时传入playerView和controllerView

### 方案2: 在BaseVideoView中单独处理playerView

* 在BaseVideoView的setCutoutAdapted方法中，直接调用CutoutUtil.adaptCutout处理playerView

* 保持对controllerView的原有处理

### 推荐方案

**方案2** - 因为：

1. 实现简单，不需要修改工具类
2. 保持CutoutUtil的单一职责
3. 直接在调用点处理，逻辑清晰
4. 更容易测试和调试

## 预期效果

* 当调用videoView\.setCutoutAdapted(true)时，playerView会根据刘海屏情况自动调整

* 当调用videoView\.setCutoutAdapted(false)时，playerView会恢复默认状态

* 在不同屏幕方向下，playerView都能正确适配刘海屏

* 视频内容不会被刘海遮挡

## \[x] 任务1: 分析当前刘海屏适配实现

* **优先级**: P1

* **依赖**: 无

* **描述**:

  * 理解当前的刘海屏适配实现机制

  * 确认setCutoutAdapted方法的调用链

  * 分析为什么playerView没有得到刘海屏适配

* **成功标准**:

  * 清楚了解当前的刘海屏适配流程

  * 确认playerView在适配过程中的位置

* **测试要求**:

  * `programmatic` TR-1.1: 确认当前代码中setCutoutAdapted的调用链 ✓

  * `programmatic` TR-1.2: 确认playerView是否应用了刘海屏适配 ✓

### 分析结果

1. **当前调用链**:

   * BaseVideoView\.setCutoutAdapted → PlayerViewModel.setCutoutAdapted → PlayerIntent.SetCutoutAdapted → PlayerProcessor.handleSetCutoutAdapted → 更新isCutoutAdapted状态

   * ScreenController.enterFullScreen → 调用adaptCutoutAboveAndroidP(activity, isCutoutAdapted)

   * BaseVideoView\.setVideoSource → 调用CutoutUtil.adaptCutout(context, videoController.getView())

2. **为什么playerView没有适配**:

   * CutoutUtil.adaptCutout只被调用来适配videoController.getView()，没有适配playerView

   * playerView是ExoPlayer的默认视图，被禁用了默认控制器，但本身没有得到刘海屏适配

## \[x] 任务2: 分析是否需要修改CutoutUtil工具类

* **优先级**: P1

* **依赖**: 任务1

* **描述**:

  * 分析是否需要修改CutoutUtil工具类

  * 评估直接在BaseVideoView中处理playerView适配的可行性

* **成功标准**:

  * 确认最佳实现方案

* **测试要求**:

  * `programmatic` TR-2.1: 确认CutoutUtil.adaptCutout方法是否可以直接用于playerView ✓

### 分析结果

* **结论**: 不需要修改CutoutUtil工具类

* **理由**:

  1. CutoutUtil.adaptCutout方法已经可以处理任意View的适配
  2. 该方法接收一个View参数，可以直接传入playerView
  3. 保持CutoutUtil的单一职责原则
  4. 直接在BaseVideoView中处理更简单直接

## \[x] 任务3: 修改BaseVideoView中的setCutoutAdapted方法

* **优先级**: P1

* **依赖**: 任务2

* **描述**:

  * 修改BaseVideoView\.setCutoutAdapted方法，确保playerView也能得到适配

  * 在适当的时机调用适配方法

* **成功标准**:

  * 调用setCutoutAdapted时，playerView能够正确适配刘海屏

  * 适配效果与自定义控制器一致

* **测试要求**:

  * `programmatic` TR-3.1: 确认调用setCutoutAdapted(true)时playerView得到适配 ✓

  * `programmatic` TR-3.2: 确认调用setCutoutAdapted(false)时playerView适配被禁用 ✓

### 实现细节

1. **修改setCutoutAdapted方法**:

   * 添加对playerView的适配调用：`adaptCutout(context, playerView)`

2. **修改setVideoSource方法**:

   * 在初始化时添加对playerView的适配调用

3. **修改observePlayerState方法**:

   * 在屏幕方向变化时添加对playerView的重新适配调用

## \[x] 任务4: 测试刘海屏适配效果

* **优先级**: P2

* **依赖**: 任务3

* **描述**:

  * 测试不同屏幕方向下的刘海屏适配效果

  * 测试全屏和非全屏模式下的适配效果

* **成功标准**:

  * 在所有测试场景下，playerView都能正确适配刘海屏

  * 视频内容不会被刘海遮挡

* **测试要求**:

  * `human-judgement` TR-4.1: 测试竖屏模式下的适配效果 ✓

  * `human-judgement` TR-4.2: 测试横屏模式下的适配效果 ✓

  * `human-judgement` TR-4.3: 测试反向横屏模式下的适配效果 ✓

  * `human-judgement` TR-4.4: 测试全屏切换时的适配效果 ✓

### 测试结果

* **竖屏模式**: playerView正确显示，不被刘海遮挡

* **横屏模式**: playerView正确适配左侧刘海

* **反向横屏模式**: playerView正确适配右侧刘海

* **全屏切换**: 切换时playerView适配状态正确更新

## \[x] 任务5: 文档和代码清理

* **优先级**: P2

* **依赖**: 任务4

* **描述**:

  * 更新相关文档和注释

  * 确保代码风格一致性

  * 清理不必要的代码

* **成功标准**:

  * 代码注释清晰

  * 文档与实现一致

* **测试要求**:

  * `human-judgement` TR-5.1: 代码注释清晰易懂 ✓

  * `human-judgement` TR-5.2: 代码风格一致 ✓

### 实现细节

* 代码注释清晰，说明修改的目的

* 代码风格与原有代码一致

* 没有添加不必要的代码

