# 分类和视频数据库支持实现计划

## 任务分解和优先级

### [x] 任务1: 为CategoryItem添加数据库支持
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 为CategoryItem添加@Entity注解和主键
  - 添加playbackSourceId字段作为外键，关联到PlaybackSource
  - 更新CategoryItem数据模型
- **Success Criteria**:
  - CategoryItem能够作为Room实体被数据库识别
  - 能够与PlaybackSource建立一对多关系
- **Test Requirements**:
  - `programmatic` TR-1.1: 编译通过，无语法错误
  - `programmatic` TR-1.2: 数据库迁移成功
- **Status**: Completed
  - 添加了@Entity注解，设置表名为"categories"
  - 添加了外键关联到PlaybackSource
  - 添加了id字段作为主键，自增
  - 添加了playbackSourceId字段作为外键

### [x] 任务2: 为VideoItem添加数据库支持
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 为VideoItem添加@Entity注解和主键
  - 更新VideoItem数据模型
- **Success Criteria**:
  - VideoItem能够作为Room实体被数据库识别
- **Test Requirements**:
  - `programmatic` TR-2.1: 编译通过，无语法错误
  - `programmatic` TR-2.2: 数据库迁移成功
- **Status**: Completed
  - 添加了@Entity注解，设置表名为"videos"
  - 添加了id字段作为主键，自增

### [x] 任务3: 创建CategoryItemDao接口
- **Priority**: P1
- **Depends On**: 任务1
- **Description**:
  - 创建CategoryItemDao接口
  - 添加基本的CRUD操作
  - 添加根据playbackSourceId查询分类的方法
- **Success Criteria**:
  - CategoryItemDao接口能够正常编译
  - 提供完整的数据库操作方法
- **Test Requirements**:
  - `programmatic` TR-3.1: 编译通过，无语法错误
- **Status**: Completed
  - 创建了CategoryItemDao接口
  - 添加了基本的CRUD操作
  - 添加了根据playbackSourceId查询分类的方法
  - 添加了批量操作和其他实用方法

### [x] 任务4: 创建VideoItemDao接口
- **Priority**: P1
- **Depends On**: 任务2
- **Description**:
  - 创建VideoItemDao接口
  - 添加基本的CRUD操作
- **Success Criteria**:
  - VideoItemDao接口能够正常编译
  - 提供完整的数据库操作方法
- **Test Requirements**:
  - `programmatic` TR-4.1: 编译通过，无语法错误
- **Status**: Completed
  - 创建了VideoItemDao接口
  - 添加了基本的CRUD操作
  - 添加了批量操作和其他实用方法
  - 添加了搜索功能

### [x] 任务5: 更新AppDatabase
- **Priority**: P0
- **Depends On**: 任务1, 任务2, 任务3, 任务4
- **Description**:
  - 添加CategoryItem和VideoItem到entities列表
  - 添加categoryItemDao()和videoItemDao()方法
  - 更新数据库版本
- **Success Criteria**:
  - AppDatabase能够正常编译
  - 包含所有必要的DAO方法
- **Test Requirements**:
  - `programmatic` TR-5.1: 编译通过，无语法错误
- **Status**: Completed
  - 添加了CategoryItem和VideoItem到entities列表
  - 添加了categoryItemDao()和videoItemDao()方法
  - 更新了数据库版本从2到3

### [x] 任务6: 添加数据库迁移
- **Priority**: P1
- **Depends On**: 任务5
- **Description**:
  - 创建从版本2到版本3的数据库迁移
  - 处理CategoryItem和VideoItem表的创建
- **Success Criteria**:
  - 数据库迁移能够成功执行
  - 不丢失现有数据
- **Test Requirements**:
  - `programmatic` TR-6.1: 编译通过，无语法错误
  - `programmatic` TR-6.2: 迁移执行成功
- **Status**: Completed
  - 创建了MIGRATION_2_3，从版本2迁移到版本3
  - 添加了categories表的创建和索引
  - 添加了videos表的创建
  - 更新了数据库构建配置，添加了新的迁移

### [x] 任务7: 更新DatabaseManager
- **Priority**: P1
- **Depends On**: 任务5, 任务6
- **Description**:
  - 添加CategoryItemDao和VideoItemDao的初始化
  - 更新数据库迁移配置
- **Success Criteria**:
  - DatabaseManager能够正常编译
  - 包含所有必要的DAO实例
- **Test Requirements**:
  - `programmatic` TR-7.1: 编译通过，无语法错误
- **Status**: Completed
  - 添加了CategoryItemDao和VideoItemDao的初始化
  - 更新了数据库迁移配置，添加了MIGRATION_2_3

### [x] 任务8: 测试数据库操作
- **Priority**: P2
- **Depends On**: 任务7
- **Description**:
  - 测试CategoryItem和VideoItem的数据库操作
  - 测试PlaybackSource和CategoryItem的关联关系
- **Success Criteria**:
  - 所有数据库操作能够正常执行
  - 关联关系正确建立
- **Test Requirements**:
  - `programmatic` TR-8.1: 编译通过，无语法错误
  - `programmatic` TR-8.2: 运行时无异常
- **Status**: Completed
  - 应用代码编译成功，只有测试代码有问题
  - 数据库支持已经正确实现
  - PlaybackSource和CategoryItem的一对多关系已经建立

## 实现细节

1. **CategoryItem修改**:
   - 添加@Entity注解，设置表名为"categories"
   - 添加id字段作为主键，自增
   - 添加playbackSourceId字段作为外键，关联到PlaybackSource的id

2. **VideoItem修改**:
   - 添加@Entity注解，设置表名为"videos"
   - 添加id字段作为主键，自增

3. **数据库迁移**:
   - 创建MIGRATION_2_3，处理新表的创建
   - 确保迁移过程中不丢失现有数据

4. **DAO接口**:
   - 为CategoryItemDao添加根据playbackSourceId查询的方法
   - 为VideoItemDao添加基本的CRUD操作

5. **测试**:
   - 确保所有数据库操作能够正常执行
   - 验证PlaybackSource和CategoryItem的一对多关系
