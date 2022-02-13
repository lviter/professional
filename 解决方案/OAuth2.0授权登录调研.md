## oauth2授权登录
### Oauth是什么
OAuth 引入了一个授权层，用来分离两种不同的角色：客户端和资源所有者。......资源所有者同意以后，资源服务器可以向客户端颁发令牌。客户端通过令牌，去请求数据。
### Oauth定义了四种角色
- client,这里的“客户端"不包含任何特定的实现特性,比如辰星就是一个客户端.
- resource owner 资源拥有者，知蓝认证. 此时你就是资源拥有者.
- authorization server 授权服务器，我们提供的登录授权服务器
- resource server 资源服务器, 当你点击同意授权之后，资源服务器会将你的信息发往client.
### Oauth提供四种获得令牌的流程，向第三方应用颁布
- 授权码（authorization-code）
- 隐藏式（implicit）
- 密码式（resource owner password credentials）
- 客户端凭证（client credentials）
### 四种模式不同的应用场景
- 授权码（authorization code）方式，指的是第三方应用先申请一个授权码，然后再用该码获取令牌，最常用的流程，安全性也最高，它适用于那些有后端的Web应用，授权码通过前端传送，令牌则是储存在后端，而且所有与资源服务器的通信都在后端完成。这样的前后端分离，可以避免令牌泄漏
- 隐藏式，有些 Web 应用是纯前端应用，没有后端。这时就不能用上面的方式了，必须将令牌储存在前端。RFC 6749 就规定了第二种方式，允许直接向前端颁发令牌。这种方式没有授权码这个中间步骤，所以称为（授权码）"隐藏式"（implicit）
- 密码式，如果你高度信任某个应用，RFC6749也允许用户把用户名和密码，直接告诉该应用。该应用就使用你的密码，申请令牌，这种方式称为"密码式"（password）
- 凭证式，适用于没有前端的命令行应用，即在命令行下请求令牌
### 授权码模式
#### 请求参数
- response_type(必须携带)，在授权码模式中response_type的参数值必须为"code".(为了区分和其他三种请求模式的区别, 而且从字面上也方便理解, "code" 意思是先拿到授权码在拿token)
- client_id(必须携带)，客户端标识符
- redirect_uri(可选参数)，表示重定向的uri(可以理解为当授权服务器返回code时的返回地址)
- scope(可选参数)，表示申请权限的范围
- state(推荐参数)，state参数通常是一个客户端随机值，发送给授权服务器,授权服务器在原封不动的返回给客户端。这样做是为了预防CSRF攻击。

#### 接口设计
- 第一步：获取code：
eg：oauthServer+"/oauth/authorize?client_id="+clientId+"&response_type=code&redirect_uri="+redirectUrl+"&scope=all"
如果没有登录，则会跳转到统一身份认证登录页面。如果用户登录了，调用接口后，会重定向到redirect_uri，授权码会作为它的参数（返回给当前请求授权的客户端的授权码，通常设置为10分钟,并且只能使用一次）
- 第二步：获取access_token
eg：oauthServer+"/oauth/token?code="+code+"&grant_type=authorization_code&client_secret="+clientSecret+"&redirect_uri="+redirectUri+"&client_id="+clientId
```json
{
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1ODk1MzQ5NzMsInVzZXJfbmFtZSI6Im5pY2t5IiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiJmMjM0M2Q0NC1hODViLTQyOGYtOWE1ZS1iNTE4NTAwNTM5ODgiLCJjbGllbnRfaWQiOiJvYSIsInNjb3BlIjpbImFsbCJdfQ.LWkN2gC2dBrGTn5uSPzfdW6yRj7jhlX87EE8scY02hI",
    "token_type": "bearer",
    "expires_in": 59,
    "scope": "all",
    "user_name": "nicky",
    "jti": "f2343d44-a85b-428f-9a5e-b51850053988"
}
```
token_type的值大小写不敏感，通常是bearer类型或mac类型
refresh_token(可选)，用来获取新的授权令牌
- 第三步：访问系统资源，此时统一认证服务会根据该认证客户端权限信息判断，决定是否返回信息。
eg:http://localhost:8084/api/userinfo?access_token=${accept_token}
