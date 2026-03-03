# 网络请求调试日志打印计划

## 分析项目网络请求实现

### 现有网络请求结构
1. **OkHttpClientManager**：负责创建和管理OkHttp客户端，已配置了HttpLoggingInterceptor
2. **RetrofitService**：负责创建和管理Retrofit实例
3. **NetworkRepository**：封装网络请求执行逻辑
4. **ApiService**：定义具体的API端点

### 当前日志配置
- OkHttpClientManager中已配置HttpLoggingInterceptor，级别为Level.BODY
- 这应该已经启用了详细的网络请求日志

## 任务分解

### [ ] 任务1：检查日志配置是否正确
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 检查OkHttpClientManager中的HttpLoggingInterceptor配置
  - 确认日志级别设置为Level.BODY
  - 检查是否有其他拦截器或配置影响日志打印
- **Success Criteria**:
  - 确认HttpLoggingInterceptor配置正确
  - 确认日志级别设置为Level.BODY
- **Test Requirements**:
  - `programmatic` TR-1.1: 检查OkHttpClientManager.kt文件中的日志配置
  - `human-judgement` TR-1.2: 确认配置符合预期

### [ ] 任务2：检查应用日志级别设置
- **Priority**: P1
- **Depends On**: 任务1
- **Description**:
  - 检查应用的全局日志级别设置
  - 确认没有设置过高的日志级别导致网络请求日志被过滤
- **Success Criteria**:
  - 应用的日志级别设置允许网络请求日志显示
- **Test Requirements**:
  - `programmatic` TR-2.1: 检查应用的日志配置文件
  - `human-judgement` TR-2.2: 确认日志级别设置合理

### [ ] 任务3：测试网络请求日志打印
- **Priority**: P0
- **Depends On**: 任务1, 任务2
- **Description**:
  - 构建并运行应用
  - 执行网络请求操作
  - 检查日志输出是否包含网络请求的详细信息
- **Success Criteria**:
  - 网络请求日志正常打印，包含请求和响应的详细信息
- **Test Requirements**:
  - `programmatic` TR-3.1: 构建应用成功
  - `human-judgement` TR-3.2: 运行应用并执行网络请求，检查日志输出

### [ ] 任务4：优化日志配置（如果需要）
- **Priority**: P1
- **Depends On**: 任务3
- **Description**:
  - 根据测试结果，优化日志配置
  - 确保在开发环境中启用详细日志，在生产环境中禁用或减少日志
- **Success Criteria**:
  - 日志配置在不同环境中合理
- **Test Requirements**:
  - `programmatic` TR-4.1: 优化后的配置符合环境需求
  - `human-judgement` TR-4.2: 日志输出清晰、有用

## 实现思路

1. **检查现有配置**：
   - 确认OkHttpClientManager中的HttpLoggingInterceptor配置正确
   - 检查是否有其他因素影响日志打印

2. **测试日志输出**：
   - 运行应用并执行网络请求
   - 检查Logcat中的网络请求日志

3. **优化配置**：
   - 根据测试结果，调整日志配置
   - 确保在不同环境中日志配置合理

## 预期结果

- 网络请求日志正常打印，包含请求URL、请求头、请求体、响应状态码、响应头、响应体等详细信息
- 日志配置在开发环境中启用详细日志，在生产环境中适当减少日志输出
- 网络请求调试变得更加方便，有助于排查网络问题