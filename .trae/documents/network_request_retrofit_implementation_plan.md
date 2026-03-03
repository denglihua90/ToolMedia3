# 网络请求通过Retrofit实现的计划

## 分析项目网络请求实现

### 现有网络请求结构
1. **RetrofitService**：负责创建和管理Retrofit实例
2. **ApiService**：定义具体的API端点
3. **NetworkRepository**：封装网络请求执行逻辑，包括错误处理和网络状态检查

### 当前问题
- `NetworkRepository`中的`genericRequest`方法使用的是直接的OkHttpClient请求，而不是通过Retrofit
- 这导致代码不一致，部分请求使用Retrofit，部分直接使用OkHttpClient

## 任务分解

### [x] 任务1：修改ApiService接口，添加通用请求方法
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 在ApiService接口中添加一个通用的GET请求方法，支持动态URL
  - 该方法应该能够处理任意URL的GET请求
- **Success Criteria**:
  - ApiService接口中添加了通用请求方法
  - 方法能够接受动态URL参数
- **Test Requirements**:
  - `programmatic` TR-1.1: 编译通过，无语法错误
  - `human-judgement` TR-1.2: 方法签名清晰，符合Retrofit规范

### [x] 任务2：修改NetworkRepository的genericRequest方法
- **Priority**: P0
- **Depends On**: 任务1
- **Description**:
  - 修改`genericRequest`方法，使用Retrofit来执行网络请求
  - 移除直接使用OkHttpClient的代码
  - 保持方法的参数和返回值不变，确保向后兼容
- **Success Criteria**:
  - `genericRequest`方法使用Retrofit执行请求
  - 方法功能与之前保持一致
- **Test Requirements**:
  - `programmatic` TR-2.1: 编译通过，无语法错误
  - `programmatic` TR-2.2: 运行时能够正常执行网络请求
  - `human-judgement` TR-2.3: 代码结构清晰，符合项目风格

### [x] 任务3：测试网络请求功能
- **Priority**: P1
- **Depends On**: 任务2
- **Description**:
  - 构建项目，确保无编译错误
  - 运行应用，测试网络请求功能是否正常
- **Success Criteria**:
  - 项目构建成功
  - 网络请求功能正常工作
- **Test Requirements**:
  - `programmatic` TR-3.1: 执行`./gradlew assembleDebug`命令，构建成功
  - `human-judgement` TR-3.2: 应用能够正常启动，网络请求功能正常

## 实现思路

1. **修改ApiService接口**：
   - 添加一个使用`@GET`注解的方法，但使用`@Url`注解来支持动态URL
   - 方法返回`Response<String>`，以便直接获取响应体的字符串内容

2. **修改NetworkRepository**：
   - 修改`genericRequest`方法，使用`apiService`的新方法执行请求
   - 保持错误处理和网络状态检查逻辑不变

3. **测试**：
   - 构建项目，确保无编译错误
   - 运行应用，测试网络请求功能是否正常

## 预期结果

- 所有网络请求都通过Retrofit实现，代码更加一致
- 保持现有功能不变，确保向后兼容
- 提高代码的可维护性和可测试性