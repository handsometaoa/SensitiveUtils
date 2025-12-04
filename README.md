## 背景

在开发过程中，很容易将用户敏感信息，例如手机号码、身份证等，打印在日志平台。为了保护用户数据，又不影响日志的打印，需要将日志中的敏感信息进行脱敏。

![截图](img/79ce2fc02bb612c2868e8ef4e5caeec8.png)
## 效果

![截图](img/089f7d7f01aa6e33fb551371b4b372bd.png)
> 强烈建议 pull项目，执行一下项目中`SensitiveUtils#main`方法。

## 特性

1. 支持多层级【JSON】/【对象】字段脱敏
2. 支持一次多字段脱敏
3. 支持自定义脱敏逻辑
4. 支持一次脱敏多种数据类型，例如：一个字符串同时脱敏手机号、身份证号
5. 连续数组层级脱敏
6. 减少侵入业务代码（例如使用注解进行脱敏）

## 使用

### 1. 输入为字符串/对象及单JSON路径

```java
   // 传入对象
   User user = new User();
   user.setName("handsometaoa");
   user.setPhone("13455556666");
   List<SensitivePath> sensitivePaths = Collections.singletonList(SensitivePath.withBuiltInRule("phone", DesensitizedType.MOBILE_PHONE));
   String result1 = SensitiveUtils.des(user, new HashSet<>(sensitivePaths));
   System.out.println(result1); // {"phone":"134****6666","name":"handsometaoa"}
   
   // 传入JSON字符串
   String param2 = "{\"name\":\"handsometaoa\",\"phone\":\"13455556666\"}";
   List<SensitivePath> sensitivePaths2 = Collections.singletonList(SensitivePath.withBuiltInRule("phone", DesensitizedType.MOBILE_PHONE));
   String result2 = SensitiveUtils.des(param2, new HashSet<>(sensitivePaths2));
   System.out.println(result2); // {"phone":"134****6666","name":"handsometaoa"}
```

### 2. 输入为字符串/对象及多JSON路径

```json
   [
       {
           "name": "handsometaoa",
           "phone": "13444445555",
           "parent": [
               {
                   "name": "handsometaoa",
                   "phone": "13444445555"
               }
           ]
       },
       {
           "name": "handsometaoa",
           "phone": "13444445555",
           "parent": [
               {
                   "name": "handsometaoa",
                   "phone": "13444445555",
                   "parent": [
                       {
                           "name": "handsometaoa",
                           "phone": "13444445555"
                       }
                   ]
               }
           ]
       }
   ]
```

上图中，如果要脱敏全部手机号，路径则为 ：`phone` , `parent#phone` , `parent#parent#phone`

```java
   String param3 = "[{\"name\":\"handsometaoa\",\"phone\":\"13444445555\",\"parent\":[{\"name\":\"handsometaoa\",\"phone\":\"13444445555\"}]}" +
           ",{\"name\":\"handsometaoa\",\"phone\":\"13444445555\",\"parent\":[{\"name\":\"handsometaoa\",\"phone\":\"13444445555\",\"parent\"" +
           ":[{\"name\":\"handsometaoa\",\"phone\":\"13444445555\"}]}]}]";
   Set<SensitivePath> sensitivePaths3 = SensitivePath.withBuiltInRuleSet(Arrays.asList("phone","parent#phone","parent#parent#phone")
           , DesensitizedType.MOBILE_PHONE);
   String result3 = SensitiveUtils.des(param3, sensitivePaths3);
   System.out.println(result3);
```

结果展示：

```json
   [
       {
           "parent": [
               {
                   "phone": "134****5555",
                   "name": "handsometaoa"
               }
           ],
           "phone": "134****5555",
           "name": "handsometaoa"
       },
       {
           "parent": [
               {
                   "parent": [
                       {
                           "phone": "134****5555",
                           "name": "handsometaoa"
                       }
                   ],
                   "phone": "134****5555",
                   "name": "handsometaoa"
               }
           ],
           "phone": "134****5555",
           "name": "handsometaoa"
       }
   ]
```

### 3. 连续数组JSON脱敏

```json
   [
       [
           {
               "parent": [
                   {
                       "phone": "15555555555"
                   },
                   {
                       "phone": "15555555555"
                   }
               ]
           },
           {
               "parent": [
                   [
                       [
                           {
                               "phone": "15555555555"
                           },
                           {
                               "phone": "15555555555"
                           }
                       ]
                   ]
               ]
           }
       ],
       [
           {
               "parent": [
                   {
                       "phone": "15555555555"
                   }
               ]
           }
       ]
   ]
```

分析可知，上图中，如果要脱敏全部手机号，路径则为 ：`parent#phone`

```java
           String param4 = "[[{\"parent\":[{\"phone\":\"15555555555\"},{\"phone\":\"15555555555\"}]},{\"parent\":[[[{\"phone\":\"15555555555\"}" +
                   ",{\"phone\":\"15555555555\"}]]]}],[{\"parent\":[{\"phone\":\"15555555555\"}]}]]";
           Set<SensitivePath> sensitivePaths4 = SensitivePath.withBuiltInRuleSet(Collections.singletonList("parent#phone")
                   , DesensitizedType.MOBILE_PHONE);
           String result4 = SensitiveUtils.des(param4, sensitivePaths4);
           System.out.println(result4);
```

结果展示：

```json
   [
       [
           {
               "parent": [
                   {
                       "phone": "155****5555"
                   },
                   {
                       "phone": "155****5555"
                   }
               ]
           },
           {
               "parent": [
                   [
                       [
                           {
                               "phone": "155****5555"
                           },
                           {
                               "phone": "155****5555"
                           }
                       ]
                   ]
               ]
           }
       ],
       [
           {
               "parent": [
                   {
                       "phone": "155****5555"
                   }
               ]
           }
       ]
   ]
```

### 4. 同时脱敏手机号与密码

```json
   {
       "name": "handsometaoa",
       "password": "123123123",
       "phone": "13444445555"
   }
```

分析可知，上图中，如果要脱敏手机号和密码

```java
   String param5 = "{\"name\":\"handsometaoa\",\"password\":\"123123123\",\"phone\":\"13444445555\"}";
   SensitivePath sensitivePaths51 = SensitivePath.withBuiltInRule("password", DesensitizedType.PASSWORD);
   SensitivePath sensitivePaths52 = SensitivePath.withBuiltInRule("phone", DesensitizedType.MOBILE_PHONE);
   HashSet<SensitivePath> sensitivePaths5 = new HashSet<>(Arrays.asList(sensitivePaths51,sensitivePaths52));
   String result5 = SensitiveUtils.des(param5, sensitivePaths5);
   System.out.println(result5);
```

结果展示：

```json
   {
       "password": "*********",
       "phone": "134****5555",
       "name": "handsometaoa"
   }
```

### 5. 支持自定义脱敏逻辑

```json
   {
       "name": "张一二"
   }
```

如果想要脱敏上面代码中名字，只留姓名，则可以自定义脱敏逻辑

```java
   String param6 = "{\"name\":\"张一二\"}";
   SensitivePath sensitivePath = SensitivePath.withCustomRule("name", value -> {
       if (ToolUtils.isEmpty( value)){
           return "";
       }
       return MaskUtils.replaceWithAsterisk(value, 1, value.length());
   });
   String result6 = SensitiveUtils.des(param6, Collections.singleton(sensitivePath));
   System.out.println(result6);
```

结果展示：

```json
   {
       "name": "张**"
   }
```

## 已知缺陷

1. 暂不支持对String以外类型脱敏
2. 暂不支持字符串中【对象JSON字符串】脱敏
   ```json
   {
       "info": "{\"data\":\"{\\\"phone\\\":\\\"13444444444\\\"}\"}"
   }
   ```

## 未来优化方向

1. ~~增加更多脱敏类型（如身份证号码）~~ 已支持 2025-12-04
2. ~~支持一个对象/JSON字符串多种脱敏类型，例如：一个字符串同时脱敏手机号、身份证号~~ 已支持 2025-12-04
3. ~~连续数组脱敏（待定）~~ 已支持 2025-12-04
4. 支持非String类型字段脱敏（待定）
5. 字符串中【对象JSON字符串】脱敏（待定）

## 博客地址

https://www.cnblogs.com/handsometaoa/p/18578888
