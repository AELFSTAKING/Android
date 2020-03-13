# Staking Planet Android 版本 App



## 简单功能介绍

一款去中心化跨链数字货币交易App，支持限价/市价交易，查看实时行情、K线指标等功能，在进行交易操作前需要先在App本地创建或导入账户。

## 代码参数配置

正式使用需要在base/build.gradle中配置三个Url参数

跨链兑换API：gwUrl

交易业务API：baseUrl

交易行情推送API：mqttUrl

```
// Gateway Url
buildConfigField "String", "gwUrl", "\"\""
// Business Url
buildConfigField "String", "baseUrl", "\"\""
// Mqtt Url
buildConfigField "String", "mqttUrl", "\"\""
```

## 代码编译

1.导入项目代码到Android Studio（File -> Open -> 选择本地代码路径）

2.使用Android Studio 进行编译（Build -> Build Bundle(s)/Apk(s) -> Build Apk(s)）

3.将生成app安装文件Apk文件（./app/build/outputs/apk/debug/StakingPlanet_1.0.2_debug_yyyymmddhhmm.apk）

## 如何创建或导入账户

  `如果你有ETH账户，请查看'二、导入充值账户'` 

  `如果你没有ETH账户，请查看'一、创建充值账户'。` 

#### 一、创建充值账户(如果你有ETH账户，请查看`'二.导入充值账户'`)
##### 首次进入App，进入'资产'页, 点击'创建充值账户'进入创建账户页
![image](https://github.com/AELFSTAKING/Android/blob/master/images/C1.jpeg)
##### 输入账户名，密码等信息，点击'立即创建'
![image](https://github.com/AELFSTAKING/Android/blob/master/images/C2.png)
##### 账户创建成功，弹出'导出私钥'页，注意保存私钥到安全的地方
![image](https://github.com/AELFSTAKING/Android/blob/master/images/C3.jpeg)
##### 保存完点击'私钥已备份'回到首页。
![image](https://github.com/AELFSTAKING/Android/blob/master/images/C4.jpeg)

#### 二、导入充值账户
##### 首次进入App，进入'资产'页, 点击'导入充值账户'进入创建账户页
![image](https://github.com/AELFSTAKING/Android/blob/master/images/C1.jpeg)
##### 输入ETH私钥，账户名称，密码等信息，点击'导入'
![image](https://github.com/AELFSTAKING/Android/blob/master/images/C5.jpeg)
