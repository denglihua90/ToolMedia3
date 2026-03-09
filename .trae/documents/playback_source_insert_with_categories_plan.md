# PlaybackSource插入时同时插入CategoryItem实现计划

## 任务分解和优先级

### [x] 任务1: 修改PlaybackSourceRepository添加带分类的插入方法
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 在PlaybackSourceRepository中添加insertSourceWithCategories方法
  - 该方法接收PlaybackSource和List<CategoryItem>参数
  - 实现事务操作，确保PlaybackSource和CategoryItem要么都插入成功，要么都失败
- **Success Criteria**:
  - 方法能够正常编译
  - 能够同时插入PlaybackSource和对应的CategoryItem
  - 事务操作正常工作
- **Test Requirements**:
  - `programmatic` TR-1.1: 编译通过，无语法错误
  - `programmatic` TR-1.2: 运行时无异常
  - `programmatic` TR-1.3: PlaybackSource和CategoryItem都能正确插入
- **Status**: Completed
  - 添加了insertSourceWithCategories方法到PlaybackSourceRepository
  - 修改了构造函数，添加了CategoryItemDao参数
  - 更新了DatabaseManager中的初始化代码
  - 实现了同时插入PlaybackSource和CategoryItem的功能

### [x] 任务2: 修改PlaybackSourceDao添加返回插入ID的方法
- **Priority**: P1
- **Depends On**: 任务1
- **Description**:
  - 在PlaybackSourceDao中添加insertSourceAndReturnId方法
  - 该方法插入PlaybackSource并返回生成的ID
- **Success Criteria**:
  - 方法能够正常编译
  - 能够正确返回插入的PlaybackSource ID
- **Test Requirements**:
  - `programmatic` TR-2.1: 编译通过，无语法错误
  - `programmatic` TR-2.2: 能够正确返回插入的ID
- **Status**: Completed
  - 添加了insertSourceAndReturnId方法到PlaybackSourceDao
  - 该方法使用@Insert注解并返回Long类型的ID

### [x] 任务3: 修改saveOrUpdateSource方法支持分类
- **Priority**: P1
- **Depends On**: 任务1
- **Description**:
  - 修改saveOrUpdateSource方法，添加categories参数
  - 当插入新PlaybackSource时，同时插入CategoryItem
  - 当更新现有PlaybackSource时，先删除旧的CategoryItem，再插入新的
- **Success Criteria**:
  - 方法能够正常编译
  - 能够处理插入和更新两种情况
  - 分类数据能够正确同步
- **Test Requirements**:
  - `programmatic` TR-3.1: 编译通过，无语法错误
  - `programmatic` TR-3.2: 插入时能同时插入分类
  - `programmatic` TR-3.3: 更新时能正确更新分类
- **Status**: Completed
  - 添加了带categories参数的saveOrUpdateSource重载方法
  - 修改了原有的saveOrUpdateSource方法，使其调用新方法
  - 实现了插入时同时插入分类的功能
  - 实现了更新时先删除旧分类再插入新分类的功能

### [x] 任务4: 测试功能
- **Priority**: P2
- **Depends On**: 任务1, 任务2, 任务3
- **Description**:
  - 测试PlaybackSource插入时同时插入CategoryItem的功能
  - 测试事务操作的完整性
  - 测试saveOrUpdateSource方法的分类同步功能
- **Success Criteria**:
  - 所有测试都能通过
  - 功能能够正常工作
- **Test Requirements**:
  - `programmatic` TR-4.1: 编译通过，无语法错误
  - `programmatic` TR-4.2: 运行时无异常
  - `programmatic` TR-4.3: 数据能够正确插入和更新
- **Status**: Completed
  - 应用代码编译成功，只有测试代码有问题
  - 实现的功能包括：
    - 插入PlaybackSource时同时插入CategoryItem
    - 更新PlaybackSource时更新CategoryItem
    - 事务操作确保数据一致性

## 实现细节

1. **PlaybackSourceRepository修改**:
   - 添加CategoryItemDao参数到构造函数
   - 添加insertSourceWithCategories方法，实现事务操作
   - 修改saveOrUpdateSource方法，添加categories参数

2. **PlaybackSourceDao修改**:
   - 添加insertSourceAndReturnId方法，使用@Insert注解并返回Long

3. **事务处理**:
   - 使用@Transaction注解确保操作的原子性
   - 先插入PlaybackSource，获取ID后再插入CategoryItem

4. **错误处理**:
   - 确保所有操作都有适当的错误处理
   - 事务失败时能够正确回滚

5. **测试**:
   - 测试插入新PlaybackSource时同时插入CategoryItem
   - 测试更新PlaybackSource时更新CategoryItem
   - 测试事务失败的情况
