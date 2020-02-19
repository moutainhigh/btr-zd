
# 逐道系统

## 微信预约部分
## app收单、验收、配送部分
## 后台管理部分

## 项目整体结构
- **btr-zd-api**： 服务提供
  - **server**：单表服务提供（无业务逻辑）（继承BaseQueryService，方法QueryService结尾）
  - **business**：业务服务（无base）
  
- **btr-zd-node-api**： 给前端node.js的服务提供（测试联调中）
- **btr-zd-model**：服务对象DTO，搜索参数request，常量，枚举
   - **constant**：常量
   - **dto**：返回对象
   - **enums**：枚举
   - **request**：请求参数
        - **server**：单表服务提供请求参数（BaseQueryRequest结尾）
        - **business**：前端业务服务请求参数（QueryRequest结尾）
        
- **btr-zd-publish**：业务控制层

- **btr-zd-service**： 业务实现层
    - **server**：单表服务提供（无业务逻辑）（方法baseServiceImpl结尾）
    - **business**：业务服务（无base）


