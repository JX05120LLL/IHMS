```vue
 * 调用方法：
 * 引入
  import {SM4Util} from "sm4util";
  const sm4 = new SM4Util();

 * 不使用自定义 secretKey，一般用于前端自行加解密,如果是前端加密后端解密，则需要自定义secretKey，前后端一致才能正确解密
  sm4.encryptDefault_ECB('123456');
  sm4.decryptDefault_ECB('123456');
 * secretKey必须为16位字符串，可包含字母数字标点，不能包含非法字符
  sm4.encryptCustom_ECB('123456', 'asdfghjklmnbvcx,');
  sm4.decryptCustom_ECB('123456', 'asdfghjklmnbvcx,');
 
 * CBC 加密模式（更加安全），需要两个密钥
  sm4.encryptDefault_CBC('123456');
  sm4.decryptDefault_CBC('123456');
 * 同样可以自定义 secretKey 和 iv，需要两个密钥前后端都一致，secretKey和iv必须为16位字符串，可包含字母数字标点，不能包含非法字符
  sm4.encryptCustom_CBC('123456', 'asdfghjklmnbvcx,', 'mnbvcxzpoiuytre1');
  sm4.decryptCustom_CBC('123456', 'asdfghjklmnbvcx,', 'mnbvcxzpoiuytre1');
```