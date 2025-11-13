# GsonUtils 工具类使用指南

## 简介

`GsonUtils` 是基于 Google Gson 封装的 JSON 工具类，提供了丰富的 JSON 序列化和反序列化方法，简化 JSON 操作。

## 特性

- 🚀 **简单易用**：提供简洁的 API，一行代码完成转换
- 🔧 **类型安全**：支持泛型，编译时类型检查
- 📅 **时间支持**：自动处理 LocalDateTime、LocalDate、LocalTime
- 🎨 **格式化输出**：支持格式化和压缩 JSON
- 📁 **文件操作**：直接读写 JSON 文件
- 🔄 **对象转换**：支持对象克隆和类型转换
- ✅ **工具方法**：提供 JSON 验证、格式化等实用方法

## 快速开始

### 1. 对象转 JSON 字符串

```java
// 创建对象
User user = new User();
user.setId(1L);
user.setUsername("admin");
user.setEmail("admin@example.com");

// 基本转换
String json = GsonUtils.toJson(user);
// 输出: {"id":1,"username":"admin","email":"admin@example.com"}

// 格式化输出
String jsonPretty = GsonUtils.toJsonPretty(user);
// 输出:
// {
//   "id": 1,
//   "username": "admin",
//   "email": "admin@example.com"
// }

// 不包含 null 值
user.setEmail(null);
String jsonNoNulls = GsonUtils.toJsonNoNulls(user);
// 输出: {"id":1,"username":"admin"}
```

### 2. JSON 字符串转对象

```java
String json = "{\"id\":1,\"username\":\"admin\",\"email\":\"admin@example.com\"}";

// 方式1：直接转换
User user = GsonUtils.fromJson(json, User.class);

// 方式2：使用 TypeToken
User user2 = GsonUtils.fromJson(json, new TypeToken<User>() {});
```


### 3. JSON 字符串转 Map

```java
String json = "{\"name\":\"张三\",\"age\":25,\"city\":\"北京\"}";

// 转换为 Map<String, Object>
Map<String, Object> map = GsonUtils.toMap(json);

// 转换为 Map<String, String>
Map<String, String> mapString = GsonUtils.toMapString(json);

// 转换为指定类型的 Map
String json2 = "{\"1\":\"value1\",\"2\":\"value2\"}";
Map<Integer, String> mapTyped = GsonUtils.toMap(json2, Integer.class, String.class);

// 转换为 HashMap
HashMap<String, Object> hashMap = GsonUtils.toHashMap(json);

// 转换为 LinkedHashMap（保持顺序）
LinkedHashMap<String, Object> linkedHashMap = GsonUtils.toLinkedHashMap(json);
```

### 4. JSON 字符串转 List

```java
String json = "[{\"id\":1,\"username\":\"admin\"},{\"id\":2,\"username\":\"user\"}]";

// 转换为 List<Object>
List<Object> list = GsonUtils.toList(json);

// 转换为 List<User>
List<User> userList = GsonUtils.toList(json, User.class);

// 转换为 ArrayList
ArrayList<User> arrayList = GsonUtils.toArrayList(json, User.class);

// 转换为 LinkedList
LinkedList<User> linkedList = GsonUtils.toLinkedList(json, User.class);
```

### 5. JSON 字符串转 Set

```java
String json = "[{\"id\":1,\"username\":\"admin\"},{\"id\":2,\"username\":\"user\"}]";

// 转换为 Set
Set<User> set = GsonUtils.toSet(json, User.class);

// 转换为 HashSet
HashSet<User> hashSet = GsonUtils.toHashSet(json, User.class);

// 转换为 LinkedHashSet（保持顺序）
LinkedHashSet<User> linkedHashSet = GsonUtils.toLinkedHashSet(json, User.class);
```


## 高级用法

### 1. 复杂类型转换

#### List<Map<String, Object>>

```java
String json = "[{\"name\":\"张三\",\"age\":25},{\"name\":\"李四\",\"age\":30}]";
List<Map<String, Object>> listMap = GsonUtils.toListMap(json);
```

#### Map<String, List<T>>

```java
String json = "{\"group1\":[{\"id\":1,\"username\":\"admin\"}],\"group2\":[{\"id\":2,\"username\":\"user\"}]}";
Map<String, List<User>> mapList = GsonUtils.toMapList(json, User.class);
```

#### 嵌套对象

```java
// 订单对象包含用户和订单项列表
Order order = new Order();
order.setOrderNo("ORDER001");
order.setUser(user);
order.setItems(itemList);

// 序列化
String json = GsonUtils.toJson(order);

// 反序列化
Order parsedOrder = GsonUtils.fromJson(json, Order.class);
```

### 2. 文件操作

```java
// 写入文件
User user = new User();
user.setId(1L);
user.setUsername("admin");

File file = new File("user.json");
GsonUtils.toFile(user, file);

// 格式化写入文件
GsonUtils.toFilePretty(user, file);

// 从文件读取
User readUser = GsonUtils.fromFile(file, User.class);

// 使用 Type 读取
List<User> users = GsonUtils.fromFile(file, new TypeToken<List<User>>() {}.getType());
```

### 3. 流操作

```java
// 从输入流读取
InputStream inputStream = new FileInputStream("user.json");
User user = GsonUtils.fromInputStream(inputStream, User.class);

// 写入输出流
OutputStream outputStream = new FileOutputStream("user.json");
GsonUtils.toOutputStream(user, outputStream);
```

### 4. JsonElement 操作

```java
// 对象转 JsonElement
User user = new User();
JsonElement element = GsonUtils.toJsonElement(user);

// JsonElement 转对象
User parsedUser = GsonUtils.fromJsonElement(element, User.class);

// 解析 JSON 字符串为 JsonElement
String json = "{\"name\":\"张三\"}";
JsonElement element2 = GsonUtils.parseJsonElement(json);
```


### 5. 对象克隆和转换

#### 深度克隆

```java
User original = new User();
original.setId(1L);
original.setUsername("admin");

// 深度克隆（通过 JSON 序列化和反序列化）
User cloned = GsonUtils.clone(original, User.class);

// cloned 是一个全新的对象
System.out.println(original == cloned); // false
```

#### 对象类型转换

```java
// DTO 转 Entity
UserDTO dto = new UserDTO();
dto.setUserId(1L);
dto.setUserName("admin");

User user = GsonUtils.convert(dto, User.class);
```

#### List 类型转换

```java
List<UserDTO> dtoList = Arrays.asList(dto1, dto2, dto3);

// 批量转换
List<User> userList = GsonUtils.convertList(dtoList, User.class);
```

### 6. 工具方法

#### 验证 JSON

```java
String json = "{\"name\":\"张三\"}";

// 判断是否为有效的 JSON
boolean isValid = GsonUtils.isValidJson(json);

// 判断是否为 JSON 对象
boolean isObject = GsonUtils.isJsonObject(json);

// 判断是否为 JSON 数组
boolean isArray = GsonUtils.isJsonArray(json);
```

#### 格式化和压缩

```java
String compactJson = "{\"name\":\"张三\",\"age\":25}";

// 格式化 JSON
String formatted = GsonUtils.formatJson(compactJson);
// 输出:
// {
//   "name": "张三",
//   "age": 25
// }

// 压缩 JSON（移除空格和换行）
String compressed = GsonUtils.compressJson(formatted);
// 输出: {"name":"张三","age":25}
```


## 时间类型支持

GsonUtils 内置了对 Java 8 时间类型的支持：

```java
@Data
class TimeExample {
    private LocalDateTime dateTime;  // 格式: yyyy-MM-dd HH:mm:ss
    private LocalDate date;          // 格式: yyyy-MM-dd
    private LocalTime time;          // 格式: HH:mm:ss
}

TimeExample example = new TimeExample();
example.setDateTime(LocalDateTime.now());
example.setDate(LocalDate.now());
example.setTime(LocalTime.now());

// 序列化
String json = GsonUtils.toJson(example);
// 输出: {"dateTime":"2024-01-15 10:30:45","date":"2024-01-15","time":"10:30:45"}

// 反序列化
TimeExample parsed = GsonUtils.fromJson(json, TimeExample.class);
```

## API 速查表

### 对象转 JSON

| 方法 | 说明 |
|------|------|
| `toJson(Object)` | 对象转 JSON 字符串 |
| `toJsonPretty(Object)` | 对象转格式化 JSON 字符串 |
| `toJsonNoNulls(Object)` | 对象转 JSON（不包含 null） |
| `toJson(Object, Type)` | 对象转 JSON，指定类型 |

### JSON 转对象

| 方法 | 说明 |
|------|------|
| `fromJson(String, Class<T>)` | JSON 转对象 |
| `fromJson(String, Type)` | JSON 转对象，指定类型 |
| `fromJson(String, TypeToken<T>)` | JSON 转对象，使用 TypeToken |

### JSON 转 Map

| 方法 | 说明 |
|------|------|
| `toMap(String)` | JSON 转 Map<String, Object> |
| `toMapString(String)` | JSON 转 Map<String, String> |
| `toMap(String, Class<K>, Class<V>)` | JSON 转指定类型的 Map |
| `toHashMap(String)` | JSON 转 HashMap |
| `toLinkedHashMap(String)` | JSON 转 LinkedHashMap |

### JSON 转 List

| 方法 | 说明 |
|------|------|
| `toList(String)` | JSON 转 List<Object> |
| `toList(String, Class<T>)` | JSON 转 List<T> |
| `toArrayList(String, Class<T>)` | JSON 转 ArrayList<T> |
| `toLinkedList(String, Class<T>)` | JSON 转 LinkedList<T> |

### JSON 转 Set

| 方法 | 说明 |
|------|------|
| `toSet(String, Class<T>)` | JSON 转 Set<T> |
| `toHashSet(String, Class<T>)` | JSON 转 HashSet<T> |
| `toLinkedHashSet(String, Class<T>)` | JSON 转 LinkedHashSet<T> |

### 复杂类型

| 方法 | 说明 |
|------|------|
| `toListMap(String)` | JSON 转 List<Map<String, Object>> |
| `toMapList(String, Class<T>)` | JSON 转 Map<String, List<T>> |

### 文件操作

| 方法 | 说明 |
|------|------|
| `toFile(Object, File)` | 对象写入文件 |
| `toFilePretty(Object, File)` | 对象格式化写入文件 |
| `fromFile(File, Class<T>)` | 从文件读取对象 |
| `fromFile(File, Type)` | 从文件读取对象，指定类型 |

### 流操作

| 方法 | 说明 |
|------|------|
| `fromInputStream(InputStream, Class<T>)` | 从输入流读取对象 |
| `toOutputStream(Object, OutputStream)` | 对象写入输出流 |

### 对象操作

| 方法 | 说明 |
|------|------|
| `clone(T, Class<T>)` | 深度克隆对象 |
| `convert(S, Class<T>)` | 对象类型转换 |
| `convertList(List<S>, Class<T>)` | List 类型转换 |

### 工具方法

| 方法 | 说明 |
|------|------|
| `isValidJson(String)` | 判断是否为有效 JSON |
| `isJsonObject(String)` | 判断是否为 JSON 对象 |
| `isJsonArray(String)` | 判断是否为 JSON 数组 |
| `formatJson(String)` | 格式化 JSON |
| `compressJson(String)` | 压缩 JSON |


## 常见使用场景

### 场景1：API 响应处理

```java
@RestController
public class UserController {
    
    @GetMapping("/user/{id}")
    public String getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        return GsonUtils.toJson(user);
    }
    
    @PostMapping("/user")
    public String createUser(@RequestBody String json) {
        User user = GsonUtils.fromJson(json, User.class);
        userService.save(user);
        return GsonUtils.toJson(user);
    }
}
```

### 场景2：缓存序列化

```java
@Service
public class CacheService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public void cacheUser(User user) {
        String json = GsonUtils.toJson(user);
        redisTemplate.opsForValue().set("user:" + user.getId(), json);
    }
    
    public User getUser(Long id) {
        String json = redisTemplate.opsForValue().get("user:" + id);
        return GsonUtils.fromJson(json, User.class);
    }
    
    public void cacheUserList(List<User> users) {
        String json = GsonUtils.toJson(users);
        redisTemplate.opsForValue().set("users", json);
    }
    
    public List<User> getUserList() {
        String json = redisTemplate.opsForValue().get("users");
        return GsonUtils.toList(json, User.class);
    }
}
```

### 场景3：配置文件读写

```java
@Component
public class ConfigManager {
    
    private static final String CONFIG_FILE = "config.json";
    
    public void saveConfig(AppConfig config) {
        File file = new File(CONFIG_FILE);
        GsonUtils.toFilePretty(config, file);
    }
    
    public AppConfig loadConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            return GsonUtils.fromFile(file, AppConfig.class);
        }
        return new AppConfig();
    }
}
```

### 场景4：日志记录

```java
@Aspect
@Component
public class LogAspect {
    
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    
    @Around("@annotation(com.example.annotation.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 记录请求参数
        Object[] args = point.getArgs();
        log.info("请求参数: {}", GsonUtils.toJson(args));
        
        // 执行方法
        Object result = point.proceed();
        
        // 记录响应结果
        log.info("响应结果: {}", GsonUtils.toJson(result));
        
        return result;
    }
}
```

### 场景5：对象深拷贝

```java
@Service
public class OrderService {
    
    public Order copyOrder(Order original) {
        // 使用 JSON 序列化实现深拷贝
        return GsonUtils.clone(original, Order.class);
    }
    
    public Order createDraftOrder(Order template) {
        // 复制模板订单
        Order draft = GsonUtils.clone(template, Order.class);
        draft.setId(null);
        draft.setStatus("DRAFT");
        return draft;
    }
}
```

### 场景6：DTO 转换

```java
@Service
public class UserService {
    
    public UserVO toVO(User user) {
        // 使用 JSON 转换实现 DTO 转换
        return GsonUtils.convert(user, UserVO.class);
    }
    
    public List<UserVO> toVOList(List<User> users) {
        return GsonUtils.convertList(users, UserVO.class);
    }
    
    public User toEntity(UserDTO dto) {
        return GsonUtils.convert(dto, User.class);
    }
}
```


## 与 JSONUtils 的对比

| 特性 | GsonUtils | JSONUtils (Jackson) |
|------|-----------|---------------------|
| 底层库 | Google Gson | Jackson |
| 性能 | 较快 | 更快 |
| API 简洁性 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| 泛型支持 | TypeToken | TypeReference |
| 时间类型 | 内置支持 | 需要配置 |
| 文件操作 | ✅ | ✅ |
| 流操作 | ✅ | ✅ |
| 对象克隆 | ✅ | ✅ |
| JSON 验证 | ✅ | ❌ |
| 格式化 | ✅ | ✅ |

**选择建议：**
- 追求简洁 API：使用 GsonUtils
- 追求极致性能：使用 JSONUtils
- 需要 JSON 验证：使用 GsonUtils
- Spring Boot 项目：两者都可以

## 注意事项

### 1. 循环引用

```java
// ❌ 避免循环引用
class Parent {
    private Child child;
}

class Child {
    private Parent parent;  // 循环引用
}

// ✅ 使用 @JsonIgnore 或移除循环引用
class Child {
    @JsonIgnore
    private Parent parent;
}
```

### 2. 泛型擦除

```java
// ❌ 错误：泛型擦除
List<User> users = GsonUtils.fromJson(json, List.class);  // 返回 List<LinkedTreeMap>

// ✅ 正确：使用 TypeToken
List<User> users = GsonUtils.toList(json, User.class);
// 或
List<User> users = GsonUtils.fromJson(json, new TypeToken<List<User>>() {});
```

### 3. 日期格式

```java
// 默认格式
// LocalDateTime: yyyy-MM-dd HH:mm:ss
// LocalDate: yyyy-MM-dd
// LocalTime: HH:mm:ss

// 如需自定义格式，使用 GsonBuilder
Gson customGson = GsonUtils.newGsonBuilder()
    .setDateFormat("yyyy/MM/dd HH:mm:ss")
    .create();
```

### 4. null 值处理

```java
User user = new User();
user.setId(1L);
user.setUsername("admin");
user.setEmail(null);  // null 值

// 默认包含 null
String json1 = GsonUtils.toJson(user);
// {"id":1,"username":"admin","email":null}

// 不包含 null
String json2 = GsonUtils.toJsonNoNulls(user);
// {"id":1,"username":"admin"}
```

### 5. 性能优化

```java
// ❌ 避免在循环中频繁创建 Gson 实例
for (User user : users) {
    new Gson().toJson(user);  // 性能差
}

// ✅ 使用静态实例
for (User user : users) {
    GsonUtils.toJson(user);  // 性能好
}
```

## 常见问题

### Q1: 如何处理枚举类型？

```java
enum Status {
    ACTIVE, INACTIVE
}

// Gson 默认使用枚举名称
User user = new User();
user.setStatus(Status.ACTIVE);

String json = GsonUtils.toJson(user);
// {"status":"ACTIVE"}
```

### Q2: 如何自定义字段名？

```java
class User {
    @SerializedName("user_id")
    private Long id;
    
    @SerializedName("user_name")
    private String username;
}
```

### Q3: 如何排除某些字段？

```java
class User {
    private Long id;
    
    @Expose(serialize = false, deserialize = false)
    private String password;  // 不序列化
}

// 需要使用自定义 Gson
Gson gson = GsonUtils.newGsonBuilder()
    .excludeFieldsWithoutExposeAnnotation()
    .create();
```

### Q4: 如何处理大数字精度？

```java
// 使用 String 类型存储大数字
class Order {
    private String totalAmount;  // 使用 String 避免精度丢失
}
```

## 完整示例

查看 `GsonUtilsExample.java` 获取完整的使用示例代码。

## 参考资料

- [Gson 官方文档](https://github.com/google/gson)
- [Gson 用户指南](https://github.com/google/gson/blob/master/UserGuide.md)

---

**Happy Coding! 🚀**
