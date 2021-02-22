# 华为钱包服务服务端示例代码
### 目录
* [安装](#安装)
* [环境要求](#环境要求)
* [申请华为钱包服务](#申请华为钱包服务)
* [配置参数](#配置参数)
* [配置模板和实例](#配置模板和实例)
* [编译Maven工程](#编译Maven工程)
* [示例方法](#示例方法)
   1. [卡券模板示例方法](#卡券模板示例方法)
   1. [卡券实例示例方法](#卡券实例示例方法)
   1. [生成JWE](#生成JWE)
   1. [验签](#验签)
* [技术支持](#技术支持)
* [授权许可](#授权许可)


## 简介
华为钱包服务（HUAWEI Wallet Kit）服务端示例代码介绍了如何调用华为钱包服务器接口。华为钱包服务器提供多个REST接口，支持六类卡券（会员卡、优惠券、礼品卡、登机牌、交通车票和活动门票）。您可以调用这些接口进行卡券添加、查询、更新等操作。
在使用此示例代码前，您需在华为开发者联盟上创建一个开发者账号，并在[AppGallery Connect](https://developer.huawei.com/consumer/cn/service/josp/agc/index.html)上创建一个应用。该应用仅表明您要申请使用华为钱包服务，并不一定是手机上的真实应用，因此即使您不打算开发端侧应用，也需要进行此操作。详情请参阅[注册账号](https://developer.huawei.com/consumer/cn/doc/start/10104)和[创建应用](https://developer.huawei.com/consumer/cn/doc/distribution/app/agc-create_app)。

## 环境要求
示例代码的运行环境为Maven和Oracle Java（1.8.0.211及以上版本）。

## 申请华为钱包服务
参照[开通服务](https://developer.huawei.com/consumer/en/doc/distribution/app/agc-enable_service) 申请华为钱包服务。<br>

请注意在申请钱包服务的过程中需要创建一个服务号，该服务号即为passTypeIdentifier的值，后续将在配置示例代码中用到。<br>

您在申请过程中还会创建一对RSA公私钥，请妥善保管。其中私钥用于后续对JWE进行签名 (详情请参阅 [生成JWE](#generate-jwe)). 公钥用于钱包服务器验签。<br>

在设置完一类卡券的服务号之后，你就可以运行这个卡券对应的示例代码了。如果你想测试其他卡券，则需申请其他卡券的服务号。

## 配置参数
运行示例代码前，在”src\test\resources\release.config.properties”文件中配置如下参数：”gw.appid”, “gw.appid.secret”, “gw.tokenUrl”, “walletServerBaseUrl”, “servicePrivateKey”和“walletWebsiteBaseUrl”。

#### 设置 "gw.appid" and "gw.appid.secret":
"gw.appid"和"gw.appid.secret"为应用的"APP ID"和"SecretKey"。登录[AppGallery Connect](https://developer.huawei.com/consumer/cn/service/josp/agc/index.html)，点击“我的应用”，选择目标应用，即可找到该应用的APP ID和SecretKey。

#### 设置 "gw.tokenUrl"
将gw.tokenUrl设置为https://oauth-login.cloud.huawei.com/oauth2/v3/token，即获取REST接口认证token的地址。

#### 设置 "walletServerBaseUrl":
"walletServerBaseUrl" 为REST接口请求的公共部分，格式为https://{walletkit_server_url}/hmspass。其中{walletkit_server_url}需要根据您的账号所属国家/地区自行替换，如下表：
| 国家/地区         	| walletkit_server_url                	|
|------------------	|-------------------------------------	|
| 中国大陆 	| wallet-passentrust-drcn.cloud.huawei.com.cn 	|
| 亚洲            	| wallet-passentrust-dra.cloud.huawei.asia  	|
| 欧洲           	| wallet-passentrust-dre.cloud.huawei.eu  	|
| 拉丁美洲    	| wallet-passentrust-dra.cloud.huawei.lat  	|
| 俄罗斯          	| wallet-passentrust-drru.cloud.huawei.ru 	|

#### 设置 "servicePrivateKety"
将"servicePrivateKey"设置为您在[申请华为钱包服务](#申请华为钱包服务)时生成的RSA私钥。您将用此参数对JWE进行签名。

#### 设置 "walletWebsiteBaseUrl"
"walletWebsiteBaseUrl" 是华为钱包H5服务器地址，格式为https://{walletkit_website_url}/walletkit/consumer/pass/save。其中{walletkit_server_url}需要根据您的账号所属国家/地区自行替换，如下表：
| 国家/地区      	  | walletkit_website_url               |
|-------------------|------------------------------------	|
| 中国大陆  | walletpass-drcn.cloud.huawei.com 	  |
| 亚洲          	  | walletpass-dra.cloud.huawei.com     |
| 欧洲      	  | walletpass-dre.cloud.huawei.com     |
| 拉丁美洲 	  | walletpass-dra.cloud.huawei.com     |
| 俄罗斯       	  | walletpass-drru.cloud.huawei.com    |

## 卡券模板和实例
一个模板代表一种卡券。同一模板下的实例会共享某些参数。例如，一个登机牌模板含有出发和抵达时间等信息，而一个登机牌实例则含有某乘客的姓名、座位号、登机顺序等信息。每一个卡券实例都有一个对应的模板。因此，您需要先创建一个卡券模板，然后才能创建卡券并进行其他操作。<br>

所有卡券模板和实例的数据类型均为HwWalletObject。详情请参阅[HwWalletObject Definition](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/def-0000001050160319-V5) for more details.<br>

在示例代码中，卡券模板和实例的入参均通过"src\test\resources\data"文件夹下的JSON文件传入。您可以自行修改JSON文件来生成您想要的卡券。<br>

请把JSON文件中"passTypeIdentifier"的值设置为您在[AppGallery Connect](https://developer.huawei.com/consumer/cn/service/josp/agc/index.html)上设置的服务号。

## 编译Maven工程
设置完上述参数后，请将示例代码编译为Maven工程。将全部依赖部署完毕可能要花几分钟时间，具体取决于您的配置文件和网络状况。

## 示例方法
### 卡券模板示例方法
#### 1. 创建一个卡券模板
华为钱包服务器提供REST接口用于创建卡券模板。例如，您可通过调用createLoyaltyModel添加会员卡模板到服务器数据库，具体请参阅[创建会员卡模板](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/create-model-0000001050158390-V5)。添加其他类型卡券方法类似。调用其他相关方法前须先创建卡券模板。

#### 2. 查询单个卡券模板
华为钱包服务器提供REST接口用于根据模板ID查询卡券模板。例如，您可通过调用getLoyaltyModel查询会员卡模板，详情请参阅[单个查询会员卡模板](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/query-model-0000001050160345-V5)。查询其他类型卡券模板方法类似。

#### 3. 查询卡券模板列表
如果您创建了多个同一卡券类型的模板（如金卡模板和钻石卡模板），您可通过调用相应接口查询模板列表。例如，可通过调用getLoyaltyModelList查询会员卡模板列表，详情请参阅[批量查询会员卡模板](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/list-model-0000001050158392-V5)。查询其他类型卡券模板列表方法类似。

#### 4. 全量更新卡券模板
华为钱包服务器提供REST接口用于根据模板ID全量更新卡券模板。例如，可通过调用fullUpdateLoyaltyModel全量更新会员卡模板，详情请参阅[全量更新会员卡模板](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/overwrite-model-0000001050160347-V5)。全量更新其他类型卡券模板方法类似。

#### 5. 局部更新卡券模板
华为钱包提供REST接口用于根据模板ID局部更新卡券模板。例如，可通过调用partialUpdateLoyaltyModel局部更新会员卡模板，详情请参阅[局部更新会员卡模板](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/update-model-0000001050158394-V5)。局部更新其他类型卡券模板方法类似。

#### 6. 增加消息到卡券模板
华为钱包提供REST接口用于添加消息到卡券模板。例如，您可通过调用addMessageToLoyaltyModel添加消息到会员卡模板。详情请参阅会员卡模板增加message。添加消息到其他类型模板方法类似。卡券模板中messageList表示消息列表，最多支持十条消息。如果已添加的消息数量达到十条，继续添加将导致最早的消息被删除。

### 卡券实例示例方法
#### 1. 新增一个卡券实例
华为钱包服务器提供REST接口用于新增卡券实例。例如，可通过调用createLoyaltyInstance新增会员卡实例到服务器数据库，详情请参阅[新增会员卡实例](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/add-instance-0000001050158396-V5)。新增其他类型卡券实例方法类似。创建实例前，您需先创建该实例所属的模板。

#### 2. 查询卡券实例
华为钱包服务器提供REST接口用于根据实例ID查询卡券实例。例如，可通过调用getLoyaltyInstance查询会员卡实例，详情可参阅查询会员卡实例。查询其他类型卡券方法类似。

#### 3. 全量更新卡券实例
华为钱包服务器提供REST接口用于根据实例ID全量更新卡券实例。例如，可通过调用fullUpdateLoyaltyInstance全量更新会员卡实例，详情请参阅[全量更新会员卡实例](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/overwrite-instance-0000001050160353-V5)。全量更新其他类型卡券实例方法类似。

#### 4. 局部更新卡券实例
华为钱包服务器提供REST接口用于根据实例ID局部更新卡券实例。例如，可通过调用partialUpdateLoyaltyInstance局部更新会员卡实例，详情请参阅[局部更新会员卡实例](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/update-instance-0000001050158400-V5)。局部更新其他类型卡券实例方法类似。

#### 5. 添加消息到卡券实例
华为钱包服务器提供REST接口用于添加消息到卡券实例。例如，可通过调用addMessageToLoyaltyInstance增加消息到某个会员卡实例，详情请参阅[会员卡实例增加message](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/instance-add-message-0000001050160355-V5)。增加消息到其他类型卡券实例方法类似。messageList是卡券实例的一个属性，最多支持十条消息。如果已添加的消息达到十条，继续添加将导致最早的消息被删除。

#### 6. 关联/去关联会员卡实例的优惠券实例
本接口仅适用于会员卡实例。可通过调用updateLinkedOffersToLoyaltyInstance为一个会员卡实例新增或删除关联的优惠券实例。详情请参阅[关联/去关联优惠券实例](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/link-offer-instance-0000001050158402-V5)。在调用此接口用于关联优惠券前，需确保想要关联的优惠券实例存在于钱包服务器中，否则客户端无法展示该优惠券。您要关联的优惠券可以属于其他应用。

### 生成JWE
您需要生成JWE字符串并发送到[华为钱包H5服务器](#set-"walletwebsitebaseurl")，从而将卡券与某个华为钱包用户绑定。</br>
您可通过以下两种方式生成JWE。第一种方法：	生成包含完整卡券实例信息的JWE并发送到华为钱包H5服务器。此时您无需调用[添加卡券实例](https://github.com/HMS-Core/hms-wallet-severdemo#add-a-pass-instance)接口。第二种方法：调用接口[添加卡券实例](https://github.com/HMS-Core/hms-wallet-severdemo#add-a-pass-instance)到钱包服务器，生成仅含卡券实例ID信息的瘦JWE并发送到华为钱包H5服务器，从而将卡券实例与某个用户绑定。<br>
本示例代码提供生成JWE和瘦JWE的方法供您参考。更多信息请参阅[网页集成“添加到华为钱包”按钮领取卡券](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/guide-webpage-0000001050042334-V5)中“生成JWE并发送到华为服务器”和“生成瘦JWE并发送到华为服务器”部分。

### 验签
若您在[申请华为钱包服务](#apply-for-wallet-kit-service)过程中设置了回调地址或NFC回调地址，您可接收到华为钱包服务器发送的通知请求。此类请求的请求头携带了签名，您需用华为提供的固定公钥验证该签名。您可参考示例代码中的verifySignature进行验签。详情请参阅[回调接口公共定义](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/public-0000001050158472-V5)。

## 技术支持
如果您对HMS Core还处于评估阶段，可在[Reddit社区](https://www.reddit.com/r/HuaweiDevelopers/)获取关于HMS Core的最新讯息，并与其他开发者交流见解。

如果您对使用HMS示例代码有疑问，请尝试：
- 开发过程遇到问题上[Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services)，在`huawei-mobile-services`标签下提问，有华为研发专家在线一对一解决您的问题。
- 到[华为开发者论坛](https://developer.huawei.com/consumer/cn/forum/blockdisplay?fid=18) HMS Core板块与其他开发者进行交流。

如果您在尝试示例代码中遇到问题，请向仓库提交[issue](https://github.com/HMS-Core/hms-wallet-severdemo/issues)，也欢迎您提交[Pull Request](https://github.com/HMS-Core/hms-wallet-severdemo/pulls)。

## 授权许可
华为钱包服务服务端示例代码经过[Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0)授权许可.
