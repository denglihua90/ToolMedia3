# 替换Epoxy为BaseRecyclerViewAdapterHelper实现计划

## 1. 任务概述
将SpeedListView中的Epoxy实现替换为使用BaseRecyclerViewAdapterHelper（BRVAH）来实现倍速选择列表。

## 2. 详细任务

### [x] 任务1: 添加BaseRecyclerViewAdapterHelper依赖
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 在app模块的build.gradle.kts文件中添加BaseRecyclerViewAdapterHelper依赖
- **Success Criteria**:
  - 依赖添加成功，项目能够正常编译
- **Test Requirements**:
  - `programmatic` TR-1.1: 检查build.gradle.kts文件中是否添加了BaseRecyclerViewAdapterHelper依赖
  - `programmatic` TR-1.2: 执行项目构建，确认没有编译错误

### [x] 任务2: 创建SpeedAdapter适配器
- **Priority**: P0
- **Depends On**: 任务1
- **Description**:
  - 创建一个基于BaseQuickAdapter的SpeedAdapter适配器
  - 实现倍速选项的显示和点击事件
  - 处理选中状态的逻辑
- **Success Criteria**:
  - SpeedAdapter能够正确显示倍速选项
  - 点击倍速选项能够触发回调
  - 选中状态能够正确显示
- **Test Requirements**:
  - `programmatic` TR-2.1: 检查SpeedAdapter是否正确继承自BaseQuickAdapter
  - `programmatic` TR-2.2: 确认适配器能够正确处理倍速选项的显示和点击事件

### [x] 任务3: 修改SpeedListView实现
- **Priority**: P0
- **Depends On**: 任务2
- **Description**:
  - 修改SpeedListView，使用SpeedAdapter替代Epoxy实现
  - 实现与之前相同的功能，包括单选逻辑和回调机制
- **Success Criteria**:
  - SpeedListView能够正常显示倍速选择列表
  - 点击倍速选项能够正确触发回调
  - 选中状态能够正确更新
- **Test Requirements**:
  - `programmatic` TR-3.1: 检查SpeedListView是否使用了SpeedAdapter
  - `programmatic` TR-3.2: 打开倍速选择列表，确认所有倍速选项都能正确显示
  - `programmatic` TR-3.3: 点击倍速选项，确认能够触发回调并关闭列表

### [x] 任务4: 测试集成
- **Priority**: P1
- **Depends On**: 任务3
- **Description**:
  - 测试倍速选择功能是否正常工作
  - 确认与VideoController的集成是否正常
  - 验证播放速度能够正确更新
- **Success Criteria**:
  - 点击视频控制器的倍速按钮能够打开倍速选择列表
  - 选择倍速后，播放器的播放速度能够正确更新
  - 整个流程流畅，无错误
- **Test Requirements**:
  - `programmatic` TR-4.1: 点击视频控制器中的倍速按钮，确认倍速选择列表能够打开
  - `programmatic` TR-4.2: 在倍速选择列表中选择一个倍速值，确认播放器的播放速度能够正确更新
  - `human-judgement` TR-4.3: 确认倍速选择操作流畅，用户体验良好

### [x] 任务5: 清理Epoxy相关代码
- **Priority**: P2
- **Depends On**: 任务4
- **Description**:
  - 移除Epoxy相关的依赖
  - 删除不再使用的Epoxy相关文件
  - 清理代码中的Epoxy引用
- **Success Criteria**:
  - 项目中不再有Epoxy相关的依赖和代码
  - 项目能够正常编译和运行
- **Test Requirements**:
  - `programmatic` TR-5.1: 检查build.gradle.kts文件中是否移除了Epoxy依赖
  - `programmatic` TR-5.2: 执行项目构建，确认没有编译错误

## 3. 技术实现要点

1. **BaseRecyclerViewAdapterHelper使用**:
   - 添加依赖: `implementation 'com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.4'`
   - 创建继承自BaseQuickAdapter的SpeedAdapter
   - 使用BaseViewHolder来处理视图绑定

2. **适配器实现**:
   - 创建SpeedItem数据类来存储倍速信息
   - 在SpeedAdapter中实现onCreateViewHolder和onBindViewHolder方法
   - 处理点击事件和选中状态

3. **SpeedListView修改**:
   - 移除Epoxy相关代码
   - 初始化SpeedAdapter并设置给RecyclerView
   - 实现与VideoController的集成

4. **集成测试**:
   - 测试倍速选择功能
   - 验证与VideoController的集成
   - 确保播放速度能够正确更新

## 4. 预期成果

完成后，SpeedListView将使用BaseRecyclerViewAdapterHelper实现倍速选择列表，功能与之前的Epoxy实现相同，但代码更加简洁和易于维护。用户仍然可以通过视频控制器的倍速按钮打开倍速选择列表，选择不同的播放速度，并且选中的倍速选项会在列表中高亮显示。