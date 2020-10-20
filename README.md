
# HUAWEI-Wallet-Server Demo Development Tutorial
### Table of Contents
1. [Introduction](#introduction)
1. [Supported Environments](#supported-environments)
1. [Apply for Wallet Kit Service](#apply-for-wallet-kit-service)
1. [Set Configuration Values](#set-configuration-values)
1. [Pass Models and Pass Instances](#pass-models-and-pass-instances)
1. [Compile the Demo as a Maven Project](#compile-the-demo-as-a-maven-project)
1. [Example Methods](#example-methods)
   1. [Example Methods for Pass Models](#example-methods-for-pass-models)
   1. [Example Methods for Pass Instances](#example-methods-for-pass-instances)
   1. [Generate JWE](#generate-jwe)
   1. [Signature Validation](#signature-validation)
1. [Question or issues](#question-or-issues)
1. [License](#license)



## Introduction
HUAWEI-Wallet-Server Demo is sample code showing how to use the HUAWEI-Wallet-Server interfaces. The HUAWEI-Wallet-Server interfaces contain REST APIs for six types of passes (Loyalty Card, Offer, Gift Card, Boarding Pass, Transit Pass, and Event Ticket). You can use these REST APIs to implement operations such as adding, querying or updating passes.<br>
Before you use this Demo, you should have a HUAWEI developer account, and have already created an app to implement the HUAWEI wallet service. This “app” doesn’t have to be a software that will be actually installed on cell phones. It means an application of using the HUAWEI wallet service. So please create an app on the HUAWEI AppGallery Connect (AGC) website even if you are not going to develop mobile apps. If you haven't, please refer to [Register a HUAWEI ID](https://developer.huawei.com/consumer/en/doc/start/10104) and [Create an App](https://developer.huawei.com/consumer/en/doc/distribution/app/agc-create_app). 

## Supported Environments
Maven and Oracle Java 1.8.0.211 or higher are required to run the Demo project.

## Apply for Wallet Kit Service
Follow [Enabling Services](https://developer.huawei.com/consumer/en/doc/distribution/app/agc-enable_service) to apply for wallet service.<br>
Please notice that you will set a “Service ID” in your application. The “Service ID” here is “passTypeIdentifier” you will use in the Demo code. You will set the “passTypeIdentifier” later.<br>
You will generate a pair of RSA keys during this process. Store them properly, and you will use the private key to sign JWEs (see [Generate JWE](#generate-jwe)). The wallet server will use the public key you upload to verify signatures.<br>
After you finished applying for a service ID, you can begin to test the corresponding pass. Apply for other service IDs if you want to test other passes.

## Set Configuration Values
Before running the Demo project，you need to set the following configuration values in the “src\test\resources\release.config.properties” file: “gw.appid”, “gw.appid.secret”, “gw.tokenUrl”, “walletServerBaseUrl”, “servicePrivateKey”, and “walletWebsiteBaseUrl”

#### Set "gw.appid" and "gw.appid.secret":
To implement the Wallet Kit to an app, "gw.appid" and "gw.appid.secret" are this app's "App ID" and "App secret". Go to the AGC website, login to your account, click “My apps” and then click the app you want to operate. Then you can find its App ID and App secret.

#### Set “gw.tokenUrl”
Set gw.tokenUrl = https://oauth-login.cloud.huawei.com/oauth2/v3/token. This is the address to obtain a REST API authentication token.

#### Set "walletServerBaseUrl":
“walletServerBaseUrl” is a common section of the REST APIs’ http requests. Its format is: walletServerBaseUrl = https://{walletkit_server_url}/hmspass. Set {walletkit_server_url} with one of the values in the following table according to your account’s location. 
| location         	| walletkit_server_url                	|
|------------------	|-------------------------------------	|
| Chinese Mainland 	| passentrust-drcn.wallet.hicloud.com 	|
| Asia             	| passentrust-dra.wallet.hicloud.com  	|
| Europe           	| passentrust-dre.wallet.hicloud.com  	|
| Latin America    	| passentrust-dra.wallet.hicloud.com  	|
| Russia           	| passentrust-drru.wallet.hicloud.com 	|

#### Set “servicePrivateKety”
You generated a pair of RSA private key and public key while you applying for Wallet Kit service on the AGC website. Set the private key here and you will use it to sign JWEs.

#### Set “walletWebsiteBaseUrl”
“walletWebsiteBaseUrl” is the address of HUAWEI-Wallet-H5 server. Its format is: walletWebsiteBaseUrl=https://{walletkit_website_url}/walletkit/consumer/pass/save. Set {walletkit_website_url} with one of the values in the following table according to your account’s location.
| location      	  | walletkit_website_url               |
|-------------------|------------------------------------	|
| Chinese Mainland  | walletpass-drcn.cloud.huawei.com 	  |
| Asia          	  | walletpass-dra.cloud.huawei.com     |
| Europe        	  | walletpass-dre.cloud.huawei.com     |
| Latin America 	  | walletpass-dra.cloud.huawei.com     |
| Russia        	  | walletpass-drru.cloud.huawei.com    |

## Pass Models and Pass Instances
A pass model is a style of pass instances. Instances belonging to the same model share some common parameters. For example, a boarding pass model contains information about departure time and arrival time, while a boarding pass instance contains a passenger’s name, his seat, his boarding sequence, etc. Each pass instance belongsto a specific model. Hence, you should first create a model before creating instances and performing other operations.<br>
All pass models and instances have the same data format, which is HwWalletObject. Refer to [HwWalletObject Definition](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/def-0000001050160319-V5) for more details.<br>
Input parameters of models and instances are passed by JSON files in the Demo project. You can generate your own data by modifying the JSON files in the “src/test/resources/data” folder.<br>
Remember to set the correct “passTypeIdentifier” in these input JSON files, which should be identical to your “Service ID” on the AGC website.

## Compile the Demo as a Maven Project
After you set all the configurations values, compile the Demo as a Maven project. It may take a few minutes to deploy all dependencies. How long it takes depends on your settings files and your network environment.

## Example Methods
### Example Methods for Pass Models
#### Register a Pass Model
The HUAWEI wallet server provides REST APIs to create pass models. See [Creating a Model](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/create-model-0000001050158390-V5). You can add a loyalty model to the server’s database by calling the “createLoyaltyModel” method, and create other types of models likewise. You should create a pass model first before calling any other methods.

#### Query a Pass Model
The HUAWEI wallet server provides REST APIs to query a pass model by its model ID. See [Query a Model](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/query-model-0000001050160345-V5). You can query a loyalty model by calling the “getLoyaltyModel” method, and query other types of models likewise.

#### Query a List of Pass Models
If your app created multiple models of a pass type (e.g. a gold loyalty card model and a diamond loyalty card model), you can use these APIs to query a list of these models. See [Query Models](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/list-model-0000001050158392-V5). You can query a list of loyalty models by calling the “getLoyaltyModelList” method, and query other types of models likewise.

#### Overwrite a Pass Model
The HUAWEI wallet server provides REST APIs to overwrite an entire pass model by its model ID. See [Overwrite a Model](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/overwrite-model-0000001050160347-V5). You can overwrite a loyalty model by calling the “fullUpdateLoyaltyModel” method, and overwrite other types of models likewise.

#### Update a Pass Model
The HUAWEI wallet server provides REST APIs to update part of a pass model by its model ID. See [Update a Model](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/update-model-0000001050158394-V5). You can overwrite a loyalty model by calling the “partialUpdateLoyaltyModel” method, and update other types of models likewise.

#### Add Messages to a Pass Model
“messageList” is one of the attributes in a pass model, which is a list of messages. You can add messages to a loyalty model by calling the “addMessageToLoyaltyModel” method, and add to other types of models likewise. The “messageList” in a pass model has at most 10 messages. You cannot add more than 10 messages at a time. If the list’s size is already 10 and you keep adding messages, the oldest messages will be removed. See [Add Messages to a Model](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/model-add-message-0000001050160349-V5).


### Example Methods for Pass Instances
#### Add a Pass Instance
The HUAWEI wallet server provides REST APIs to add pass instances. See [Add an Instance](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/add-instance-0000001050158396-V5). You can add a loyalty instance to the server’s database by calling the “createLoyaltyInstance” method, and create other types of instances likewise. You should create a pass model first before creating instances belonging to it.

#### Query a Pass Instance
The HUAWEI wallet server provides REST APIs to query a pass instance by its instance ID. See [Query an Instance](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/query-instance-0000001050160351-V5). You can query a loyalty instance by calling the “getLoyaltyInstance” method, and query other types of instances likewise.

#### Overwrite a Pass Instance
The HUAWEI wallet server provides REST APIs to overwrite an entire pass instance by its instance ID. See [Overwrite an Instance](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/overwrite-instance-0000001050160353-V5). You can overwrite a loyalty instance by calling the “fullUpdateLoyaltyInstance” method, and overwrite other types of instances likewise.

#### Update a Pass Instance
The HUAWEI wallet server provides REST APIs to update part of a pass instance by its instance ID. See [Update an Instance](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/update-instance-0000001050158400-V5). You can overwrite a loyalty instance by calling the “partialUpdateLoyaltyInstance” method, and update other types of instances likewise.

#### Add Messages to a Pass Instance
“messageList” can also be an attribute of a pass instance. You can add messages to a loyalty instance by calling the “addMessageToLoyaltyInstance” method, and add to other types of instances likewise. The “messageList” in a pass instance has at most 10 messages. You cannot add more than 10 messages at a time. If the list’s size is already10 and you keep adding messages, the oldest messages will be removed. See [Add Messages to an Instance](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/instance-add-message-0000001050160355-V5).

#### Link/unlink Offer Instances to/from a Loyalty Instance
This API is only provided for loyalty instances. See [Link/Unlink an Offer](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/link-offer-instance-0000001050158402-V5). You can link/unlink offer instances to/from a loyalty instance by calling the “updateLinkedOffersToLoyaltyInstance” method. You should make sure the offers you want to add already exist before you use this API. Otherwise, the client cannot show an offer that is not in the server’s database. These offer instances can belong to other apps or other developers.

### Generate JWE
Developers need to generate JSON Web Encryption(JWE) strings and send them to HUAWI-Wallet-H5 server (See [Set “walletWebsiteBaseUrl”](#set-“walletwebsitebaseurl”)) to bind a pass instance to a Huawei Wallet user.<br>
There are two ways to generate JWEs. The first way: you can generate a JWE string with complete information of a pass instance and send it to the HUAWEI-Wallet-H5 server. By this way, you don’t need to call methods in 8.1. The second way: you can call methods in 8.1 to add a pass instance to wallet server first, and then generate a thin JWE (with only instance ID information) and send it to the HUAWEI-Wallet-H5 server to bind the pass instance to a user.<br>
The demo has example methods for generating JWEs and thin JWEs. Please refer to the demo and implement the code in your own system. Please also refer to [Integrate Add to HUAWEI Wallet Button](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/guide-webpage-0000001050042334-V5) (section “Generating a JWE and Sending It to the Huawei Server” and section “Generating a Thin JWE and Sending It to the Huawei Server”) for more details.

### Signature Validation
If you provides a Callback URL or a NFC Callback URL during [applying for Wallet Kit service](#apply-for-wallet-kit-service)), you will receive callback notification requests from HUAWEI wallet server. You should verify the signature in the request header with Huawei's fixed signature public key. The test case "verifySignature" gives a example of verifying a signature. Refer to [Callback APIs](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/public-0000001050158472-V5) for more details.

## Question or issues
If you want to evaluate more about HMS Core,
[r/HMSCore on Reddit](https://www.reddit.com/r/HuaweiDevelopers/) is for you to keep up with latest news about HMS Core, and to exchange insights with other developers.

If you have questions about how to use HMS samples, try the following options:
- [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services) is the best place for any programming questions. Be sure to tag your question with 
`huawei-mobile-services`.
- [Huawei Developer Forum](https://forums.developer.huawei.com/forumPortal/en/home?fid=0101187876626530001) HMS Core Module is great for general questions, or seeking recommendations and opinions.

If you run into a bug in our samples, please submit an [issue](https://github.com/HMS-Core/hms-scan-demo/issues) to the Repository. Even better you can submit a [Pull Request](https://github.com/HMS-Core/hms-scan-demo/pulls) with a fix.

## License
HUAWEI-Wallet-Server Demo is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
