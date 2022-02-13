## apple id登录主要流程如图(双重认证)附件图

## 开发前准备以及注意点
- 需要苹果开发者账户(https://developer.apple.com/account/#/welcome)
- 加入开发者计划付费

## 通信流程
- 客户端与苹果服务器通信获得identitytoken，然后把identitytoken交给业务后台验证，验证通过就可以了。其中appServer涉及到的验证，就是identitytoken，其实identitytoken就是一个jws。（jws是json web signature）,验证jws的签名，保证数据没有被篡改之后，还要校验从identitytokendecode出来的nonce,iss,aud,exp，主要是iss和exp这两个

## 获取/刷新token
- 首先获取code:GET
https://appleid.apple.com/auth/authorize?response_type=code&client_id=&redirect_uri=&state=1234
- 根据code获取token:POST
https://appleid.apple.com/auth/token?grant_type=authorization_code&code=code&redirect_uri=url&client_id=id&client_secret=secret <br>
获取令牌所需参数：
1. grant_type:'authorization_code'为获取令牌
2. client_id：client_id
3. redirect_uri：redirect_uri
4. code：上一步获取到的授权码
5. codeclient_secret：secret（一个自己生成的jwt https://developer.apple.com/documentation/signinwithapplerestapi/generate_and_validate_tokens）<br>
返回值示例
```json
{
"access_token":"a0996b16cfb674c0eb0d29194c880455b.0.nsww.5fi5MVC-i3AVNhddrNg7Qw",
"token_type":"Bearer",
"expires_in":3600,
"refresh_token":"r9ee922f1c8b048208037f78cd7dfc91a.0.nsww.KlV2TeFlTr7YDdZ0KtvEQQ",
"id_token":"eyJraWQiOiJBSURPUEsxIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLnNreW1pbmcuYXBwbGVsb2dpbmRlbW8iLCJleHAiOjE1NjU2NjU1OTQsImlhdCI6MTU2NTY2NDk5NCwic3ViIjoiMDAwMjY2LmRiZTg2NWIwYWE3MjRlMWM4ODM5MDIwOWI5YzdkNjk1LjAyNTYiLCJhdF9oYXNoIjoiR0ZmODhlX1ptc0pqQ2VkZzJXem85ZyIsImF1dGhfdGltZSI6MTU2NTY2NDk2M30.J6XFWmbr0a1hkJszAKM2wevJF57yZt-MoyZNI9QF76dHfJvAmFO9_RP9-tz4pN4ua3BuSJpUbwzT2xFD_rBjsNWkU-ZhuSAONdAnCtK2Vbc2AYEH9n7lB2PnOE1mX5HwY-dI9dqS9AdU4S_CjzTGnvFqC9H5pt6LVoCF4N9dFfQnh2w7jQrjTic_JvbgJT5m7vLzRx-eRnlxQIifEsHDbudzi3yg7XC9OL9QBiTyHdCQvRdsyRLrewJT6QZmi6kEWrV9E21WPC6qJMsaIfGik44UgPOnNnjdxKPzxUAa-Lo1HAzvHcAX5i047T01ltqvHbtsJEZxAB6okmwco78JQA"
}
```
刷新令牌所需参数：
1. grant_type:'refresh_token'为刷新令牌
2. client_id：client_id
3. client_secret：client_secret
4. refresh_token：上一步获取到的id_token

## 对id_token解密
- 通过 GET:https://appleid.apple.com/auth/keys 接口获取公钥
```json
{
    "keys": [
        {
            "kty": "RSA",
            "kid": "86D88Kf",
            "use": "sig",
            "alg": "RS256",
            "n": "iGaLqP6y-SJCCBq5Hv6pGDbG_SQ11MNjH7rWHcCFYz4hGwHC4lcSurTlV8u3avoVNM8jXevG1Iu1SY11qInqUvjJur--hghr1b56OPJu6H1iKulSxGjEIyDP6c5BdE1uwprYyr4IO9th8fOwCPygjLFrh44XEGbDIFeImwvBAGOhmMB2AD1n1KviyNsH0bEB7phQtiLk-ILjv1bORSRl8AK677-1T8isGfHKXGZ_ZGtStDe7Lu0Ihp8zoUt59kx2o9uWpROkzF56ypresiIl4WprClRCjz8x6cPZXU2qNWhu71TQvUFwvIvbkE1oYaJMb0jcOTmBRZA2QuYw-zHLwQ",
            "e": "AQAB"
        },
        {
            "kty": "RSA",
            "kid": "eXaunmL",
            "use": "sig",
            "alg": "RS256",
            "n": "4dGQ7bQK8LgILOdLsYzfZjkEAoQeVC_aqyc8GC6RX7dq_KvRAQAWPvkam8VQv4GK5T4ogklEKEvj5ISBamdDNq1n52TpxQwI2EqxSk7I9fKPKhRt4F8-2yETlYvye-2s6NeWJim0KBtOVrk0gWvEDgd6WOqJl_yt5WBISvILNyVg1qAAM8JeX6dRPosahRVDjA52G2X-Tip84wqwyRpUlq2ybzcLh3zyhCitBOebiRWDQfG26EH9lTlJhll-p_Dg8vAXxJLIJ4SNLcqgFeZe4OfHLgdzMvxXZJnPp_VgmkcpUdRotazKZumj6dBPcXI_XID4Z4Z3OM1KrZPJNdUhxw",
            "e": "AQAB"
        }
    ]
}
```
- 然后我们用jwt.verify通过公钥解密id_token
- 解密后得到的verify.sub就是用户apple账号登录在该程序中的唯一标识，我们可以把它存到程序的数据库中与用户信息做映射，用于标识用户身份      

![](../static/image-solutions/appleId授权登录.jpg)