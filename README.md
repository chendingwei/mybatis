# mybatis

# 一、简介



- MyBatis 是一款优秀的持久层框架
- 它支持自定义 SQL、存储过程以及高级映射。
- MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作。
- MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。

# 二、三大核心

## 1、SqlSessionFactoryBuilder

该类可以被实例化、使用和丢弃，一旦创建了SqlSessionFactory，就不再需要它，故最佳的作用域是方法作用域。

## 2、SqlSessionFactory

该对象一旦创建，就应该在应用的期间一直存在，用静态单例模式实现。

## 3、SqlSession

每个线程都有它自己的SqlSession实例，但是由于它并非线程安全，故不能被共享，所以它的最佳的作用域是请求或方法作用域。每次收到 HTTP 请求，就可以打开一个 SqlSession，返回一个响应后，就关闭它。

```java
public class MybatisUtils {
    private static SqlSessionFactory sqlSessionFactory;
    static {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static SqlSession getSqlSession(){
        return sqlSessionFactory.openSession();
    }
}
```

# 三、核心配置文件

maven静态资源问题：

```xml
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*.properties</include>
                <include>**/*.xml</include>
            </includes>
            <filtering>true</filtering>
        </resource>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.properties</include>
                <include>**/*.xml</include>
            </includes>
            <filtering>true</filtering>
        </resource>
    </resources>
</build>
```

mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=false&amp;useUnicode=true&amp;characterEncoding=UTF-8"/>
                <property name="username" value="root"/>
                <property name="password" value="0000"/>
            </dataSource>
        </environment>
    </environments>
</configuration>
```





configuration（配置）

- [properties（属性）](https://mybatis.org/mybatis-3/zh/configuration.html#properties)
- [settings（设置）](https://mybatis.org/mybatis-3/zh/configuration.html#settings)
- [typeAliases（类型别名）](https://mybatis.org/mybatis-3/zh/configuration.html#typeAliases)
- [typeHandlers（类型处理器）](https://mybatis.org/mybatis-3/zh/configuration.html#typeHandlers)
- [objectFactory（对象工厂）](https://mybatis.org/mybatis-3/zh/configuration.html#objectFactory)
- [plugins（插件）](https://mybatis.org/mybatis-3/zh/configuration.html#plugins)
- environments（环境配置）
  - environment（环境变量）
    - transactionManager（事务管理器）
    - dataSource（数据源）
- [databaseIdProvider（数据库厂商标识）](https://mybatis.org/mybatis-3/zh/configuration.html#databaseIdProvider)
- [mappers（映射器）](https://mybatis.org/mybatis-3/zh/configuration.html#mappers)

## 1、环境配置（environments）

Mybatis可以配置成适应多个环境，不过要记住：尽管可以配置多个环境，但每个 SqlSessionFactory 实例只能选择一种环境。默认的事物管理器是JDBC[MANAGED]，连接池POOLED[UNPOOLED|JNDI ]

- JDBC – 这个配置直接使用了 JDBC 的提交和回滚设施，它依赖从数据源获得的连接来管理事务作用域。
- MANAGED – 这个配置几乎没做什么。它从不提交或回滚一个连接，而是让容器来管理事务的整个生命周期（比如 JEE 应用服务器的上下文）。 默认情况下它会关闭连接。然而一些容器并不希望连接被关闭，因此需要将 closeConnection 属性设置为 false 来阻止默认的关闭行为。

## 2、属性（properties）

可以通过properties属性来实现引用配置文件。这些属性可以在外部进行配置，并可以进行动态替换。你既可以在典型的 Java 属性文件中配置这些属性，也可以在 properties 元素的子元素中设置。

①可以直接引入外部文件

②可以在其中增加一些属性配置

③如果两个文件中有相同的字段，优先使用外部配置文件的内容。

## 3、类型别名（typeAliases）

类型别名可为 Java 类型设置一个缩写名字。 它仅用于 XML 配置，意在降低冗余的全限定类名书写。

两种方法：

①指定类固定的类名，当这样配置时，User 可以用在任何使用 com.chen.pojo.User的地方。

```java
<typeAliases>
    <package name="com.chen.pojo"/>
</typeAliases>
```



②指定包中所有类,MyBatis 会在包名下面搜索需要的 Java Bean。

```java
<typeAliases>
    <package name="com.chen.pojo"/>
</typeAliases>
```

## 4、设置（settings）

这是 MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的运行时行为。 下表描述了设置中各项设置的含义、默认值等。

重要项：

| 设置名             | 描述                                                         |
| ------------------ | ------------------------------------------------------------ |
| cacheEnabled       | 全局性地开启或关闭所有映射器配置文件中已配置的任何缓存。     |
| lazyLoadingEnabled | 延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置 fetchType 属性来覆盖该项的开关状态。 |
| logImpl            | 指定 MyBatis 所用日志的具体实现，未指定时将自动查找。        |

## 5、其他（了解即可）

### ①类型处理器（typeHandlers）

MyBatis 在设置预处理语句（PreparedStatement）中的参数或从结果集中取出一个值时， 都会用类型处理器将获取到的值以合适的方式转换成 Java 类型。

### ②对象工厂（objectFactory）

来完成实例化工作。

### ③插件（plugins）

允许你在映射语句执行过程中的某一点进行拦截调用。

调用 如 mybatis-plus， mybatis-generator-core， 通用mapper等。

## 6、映射器（mappers）

配置SQL映射语句， 告诉 MyBatis 到哪里去找到这些语句。

①相对于类路径的资源引用

```java
<mappers>
    <mapper resource="com/chen/mapper/UserMapper.xml" />
</mappers>
```

②映射器接口完全限定类名

```java
<mappers>
    <mapper resource="com.chen.mapper.UserMapper.xml"/>
</mappers>
```

## 7、结果集映射(resultMap)

解决属性名和字段名不一致的问题。

POJO类：

```java
public class User {
    private int id;
    private String name;
    private String password;
}
```

UserMapper.xml

```java
    <!-- column对应数据库列名, property对应实体类属性名-->
    <!-- 通过结果集映射进行一一匹配-->
    <!-- 若两个属性相同的内容，可以省略-->
	<resultMap id="UserMap" type="User">
        <result column="id" property="id"/> #可省略
        <result column="name" property="name"/> #可省略
        <result column="pwd" property="password"/>
    </resultMap>

    <select id="getUserList" resultMap="UserMap">
      select * from mybatis.user
    </select>
```

# 四、日志

## 1、日志工厂

Mybatis 通过使用内置的日志工厂提供日志功能。

内置日志工厂将会把日志工作委托给下面的实现之一：

- SLF4J

- Apache Commons Logging

- Log4j 2

- Log4j 【掌握】

- JDK logging

- STDOUT_LOGGING 【掌握】  

  

## 2、STDOUT_LOGGING标准日志输出

在mybatis核心配置文件中配置

```java
    <settings>
        <setting name="logImpl" value="STDOUT_LOGGING"/>
    </settings>
```



![image-20201019155708467](C:\Users\chend\AppData\Roaming\Typora\typora-user-images\image-20201019155708467.png)

## 3、LOG4J

- 能将日志信息输送的目的地是控制台、文件、GUI组件
- 能控制每一条日志的输出格式
- 通过定义每一条日志信息的级别，更加细致控制日志生成
-  可以通过一个配置文件来灵活地进行配置，不需要修改代码

1. 先导入log4j的包

   ```xml
   <dependency>
   	<groupId>log4j</groupId>
   	<artifactId>log4j</artifactId>
   	<version>1.2.17</version>
   </dependency>
   ```

2. log4j.properties

3. 配置log4j为日志的实现

   ```xml
   <settings>
   	<setting name="logImpl" value="LOG4J"/>
   </settings>
   ```

4. 

# 五、分页

目的：减少数据的处理量

## 1、Limit分页

```sql
SELECT * FROM user limit startIndex,pageSize;
```

## 2、Mybatis实现分页（核心SQL）

①接口

```java
public interface UserMapper {
    List<User> getUserByLimit(Map<String,Integer> map);
}
```

②Mapper.xml

```xml
<select id="getUserByLimit" parameterType="map" resultType="User">
    //键值对的形式传入参数，以键为变量名，传入键对应的值
    select * from  mybatis.user limit #{startIndex},#{pageSize}
</select>
```

③测试

```java
    public void getUserByLimit(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        
        HashMap<String, Integer> map = new HashMap<>();
        map.put("startIndex",0);  //起始
        map.put("pageSize",2);	  //分页数量

        List<User> userByLimit = mapper.getUserByLimit(map);
        for (User user : userByLimit) {
            System.out.println(user.toString());
        }
        sqlSession.close();
    }
```

# 六、使用注解开发

## 1、面向接口编程

**根本原因： 解耦**

关于接口的理解：

- 接口从更深层次的理解，应该是定义（规范、约束）与实现（名实分离的原则）的分离。
- 接口的本身反映了系统设计人员对系统的抽象理解。
- 接口应该有两类
  - 第一类是对一个个体的抽象，它可对应为一个抽象体；
  - 第二类是对一个个体某一方面的抽象，形成一个抽象面；

- 一个个体可能有多个抽象面。



三个面向区别：

- 面向对象是指，我们考虑问题时候，以对象为单位，考虑它的属性及方法；
- 面向过程是指，我们考虑问题时候，以一个具体的流程（事务过程）为单位，考虑它的实现；
- 接口设计与非接口设计是针对复用技术而言的，与面向对象（过程）不是一个问题，更多的体现就是对系统整体的架构

## 2、使用注解开发（mybatis-05）

**使用注解来映射简单语句会使代码显得更加简洁，但对于稍微复杂一点的语句，Java 注解不仅力不从心，还会让你本就复杂的 SQL 语句更加混乱不堪。 因此，如果你需要做一些很复杂的操作，最好用 XML 来映射语句。**

### 2.1 查找所有

①接口

```java
public interface UserMapper {
    @Select("select * from user")
    List<User> getUser();
}
```

②Mybatis-config.xml

```xml
    <mappers>
        <mapper class="com.chen.mapper.UserMapper"/>
    </mappers>
```

③测试

### 2.2 查找

①接口

```java
	@Select("select * from user where id = #{id}")
	// 以Param注解中的参数为查询参数，多个Param必须用Param进行说明
    User getUserByID(@Param("id") int id, @Param("name") String name);
```

**本质：反射机制**

**底层：动态代理**

### 2.3 @Param注解

- 基本类型的参数或者String类型，需要加。
- 引用类型不用加。
- 若只有一个基本类型，可以忽略，但是建议加上，规范。
- 在SQL中引用的就是Param中的属性名。

# 七、Mybatis详细执行流程

1. Resources获取加载全局配置文件
2. 实例化SqlSessionFatoryBuilder构造器
   1. 解析文件流XMLConfigBuilder
   2. Configuration对象保存所有的配置信息
3.  实例化SqlSessionFactory
   1. transactional 事务管理器
   2. **创建executor执行器** 
4. 创建SqlSession，实现CRUD
5. 提交事务
6. 关闭SqlSession

# 八、多对一处理（mybatis-06）

## 1、环境搭建

1. 新建实体类 Teacher Student
2. 建立Mapper接口
3. 建立Mapper.xml文件
4. 在核心配置文件中，注册Mapper接口类或者xml配置文件

## 2、**背景**：查询结果中包含java对象，非基本数据类型

pojo类

```java
public class Teacher {
    private int id;
    private String name;
}
```

```java
public class Student {
    private int id;
    private String name;
    private Teacher teacher;
}
```

多个学生 对应 一个老师  **关联** 关系

常规sql操作：输出所有学生

```xml
<select id="getStudent" resultType="Student">
    select * from mybatis.student;
</select>
```

结果：

![image-20201023151908139](C:\Users\chend\AppData\Roaming\Typora\typora-user-images\image-20201023151908139.png)

teacher为null，无法查询到非基本类型。



## 3、方法一：按照查询嵌套处理

**思路**

1. 查询所有的学生信息
2. 根据查询出来的学生的tid，寻找对应老师（子查询）

①StudentMapper.xml

```xml
<select id="getStudent" resultMap="StudentTeacher">
    select * from mybatis.student;
</select>

<resultMap id="StudentTeacher" type="Student">
    <!-- 复杂的属性，需要单独处理，对象：association 集合：collection-->
    <association property="teacher" column="tid" javaType="Teacher" select="getTeacher"/>
</resultMap>

<select id="getTeacher" resultType="Teacher">
    select * from mybatis.teacher where id = #{id};
</select>
```

## 4、方法二：按照结果嵌套处理

```xml
<select id="getStudent2" resultMap="StudentTeacher2">
    select s.id sid, s.name sname, t.name tname
    from student s, teacher t
    where s.tid = t.id;
</select>
<!-- 查询sql语句写完之后，只需要对应每一项的关系，写出resultMap即可-->
<resultMap id="StudentTeacher2" type="Student">
    <result property="id" column="sid"/>
    <result property="name" column="sname"/>
    <association property="teacher" javaType="Teacher">
        <result property="name" column="tname"/>
    </association>
</resultMap>
```

## 5、回顾Mysql多对一查询方式：

- 子查询——方法一：按照查询嵌套处理
- 联表查询——方法二：按照结果嵌套处理

# 九、一对多处理（mybatis-07）

**背景同上，一个老师对应多个学生，对于老师而言，就是一对多关系**

## 1、环境搭建

POJO类：

一个老师对应多个学生

```java
public class Teacher {
    private int id;
    private String name;
    //一个老师对应多个学生
    private List<Student> students;
}
```

```java
public class Student {
    private int id;
    private String name;
    private int tid;
}
```

查询语句：

```xml
<select id="getTeacher" resultType="Teacher">
    select * from mybatis.teacher;
</select>
```

查询结果：

![image-20201023155038821](C:\Users\chend\AppData\Roaming\Typora\typora-user-images\image-20201023155038821.png)

## 2、按照结果嵌套处理

```xml
<select id="getTeacher" resultMap="TeacherStudent">
    select s.id sid, s.name sname, t.name tname, t.id tid
    from student s, teacher t
    where  s.tid = t.id and t.id = #{tid};
</select>

<resultMap id="TeacherStudent" type="Teacher">
    <result property="id" column="tid"/>
    <result property="name" column="tname"/>
    <collection property="students" ofType="Student">
        <result property="id" column="sid"/>
        <result property="name" column="sname"/>
        <result property="tid" column="tid"/>
    </collection>
</resultMap>
```

结果集：

![image-20201023155945555](C:\Users\chend\AppData\Roaming\Typora\typora-user-images\image-20201023155945555.png)

Teacher{

id=1,

name='陈', 

students=[

​	Student{id=1, name='chen', tid=1}, 

​	Student{id=2, name='li', tid=1}, 

​	Student{id=3, name='wang', tid=1}, 

​	Student{id=4, name='xiang', tid=1}

​	]

}

## 3、按照查询嵌套处理(select 语句嵌套)

```xml
<resultMap id="TeacherStudent2" type="Teacher">
    <collection property="students" javaType="ArrayList" ofType="Student" select="getStudentByTeacherID0" column="id"/>
</resultMap>

<select id="getStudentByTeacherID" resultType="Student">
    select * from mybatis.student where tid = #{tid}
</select>
```

## 4、小结

1. 关联 - association 多对一
2. 集合 - collection 一对多
3. javaType：用来指定实体类中属性的类型
4. ofType：用来指定映射到List或集合中的pojo类，即集合中的泛型

注意点：

- 保证SQL的可读性，尽量可以使用按照结果嵌套查询方式
- 注意一对多和多对一的差异，属性名和字段的问题等
- 日志方面，可以使用log4j

![image-20201023161211504](C:\Users\chend\AppData\Roaming\Typora\typora-user-images\image-20201023161211504.png)

# 十、动态SQL(mybatis-08)

## 1、什么是动态SQL？

**根据不同的条件，生成不同的SQL语句**

- if
- choose (when, otherwise)
- trim (where, set)
- foreach

## 2、搭建环境

```sql
CREATE TABLE `blog`(
    `id` varchar(50) not null comment '博客id',
    `title` varchar(100) not null comment '博客标题',
    `author` varchar(30) not null comment '博客作者',
    `create_time` datetime not null comment '创建时间',
    `views` int(30) not null comment '浏览量'
) ENGINE = InnoDB DEFAULT CHARSET=utf8


```

编写实体类

```java
public class Blog {
    private String id;
    private String title;
    private String author;
    private Date createTime;
    private int views;
}
```

属性名与数据库列名不一致情况，可以在核心配置文件中增加设置：

```xml
<settings>
    <setting name="mapUnderscoreToCamelCase" value="true"/>
    <!--将数据大写列名，转成驼峰命名法-->
</settings>
```

## 3、if语句

这条语句提供了可选的查找文本功能。如果不传入 “title”，那么所有处于 “ACTIVE” 状态的 BLOG 都会返回；如果传入了 “title” 参数，那么就会对 “title” 一列进行模糊查找并返回对应的 BLOG 结果。

```xml
<select id="queryBlogIF" parameterType="map" resultType="Blog">
    select *
    from mybatis.blog
    where 1=1
    <if test="title != null">
        and title = #{title}
    </if>
    <if test="author ！= null">
        and author = #{author}
    </if>
</select>
```

## 4、choose（when, otherwise）

```xml
<select id="findActiveBlogLike"
     resultType="Blog">
  SELECT * FROM BLOG WHERE state = ‘ACTIVE’
  <choose>
    <when test="title != null">
      AND title like #{title}
    </when>
    <when test="author != null and author.name != null">
      AND author_name like #{author.name}
    </when>
    <otherwise>
      AND featured = 1
    </otherwise>
  </choose>
</select>
```

## 5、trim(where,set)

```xml
<select id="findActiveBlogLike"
     resultType="Blog">
  SELECT * FROM BLOG
  WHERE
  <if test="state != null">
    state = #{state}
  </if>
  <if test="title != null">
    AND title like #{title}
  </if>
  <if test="author != null and author.name != null">
    AND author_name like #{author.name}
  </if>
</select>
```

## 6、foreach

```xml
<select id="selectPostIn" resultType="domain.blog.Post">
  SELECT *
  FROM POST P
  WHERE ID in
  <foreach item="item" index="index" collection="list"
      open="(" separator="," close=")">
        #{item}
  </foreach>
</select>
```

# 十一、缓存（mybatis-09）

1. 什么是缓存

   - 存在内存中的临时数据
   - 将用户经常查询的数据存放在缓存中，再次查询时不用从数据库中查询，可直接从缓冲中查询，从而提高查询效率，解决了高并发系统的性能问题。

2. 为什么使用缓存

   减少和数据库的交互次数，减少系统开销，提高系统效率

3. 什么样的数据能够使用缓存？

   经常查询且不经常改变的数据，反之，不适合使用缓存

## 1、Mybatis缓存

- Mybatis包含一个非常强大的查询缓存特性，它可以非常方便地定制和配置缓存，极大提升效率
- Mybatis系统中默认定义了两级缓存：**一级缓存**和**二级缓存**
  - 默认情况下，会启动一级缓存（Sqlsession级别的缓存，也称为本地缓存）
  - 二级缓存需要手动开启和配置，基于namespace级别的缓存，一个Mapper
  - 为了提高拓展性，Mybatis定义了缓存接口cache。可以通过cache接口来自定义二级缓存。

测试：

1. 开启日志
2. 测试在一个SqlSession中，查询两次相同的记录
3. 查看日志输出

![image-20201024142613385](C:\Users\chend\AppData\Roaming\Typora\typora-user-images\image-20201024142613385.png)

缓存失效的情况：

1. 查询不同的SQL语句
2. 增删改操作，可能会改变原来的数据，所以必定会刷新缓存
3. 查询不同的mapper
4. 手动清理缓存

```java
	sqlSession.clearCache();
```



## 2、一级缓存

- 一级缓存也叫本地缓存
  - 与数据库同一次会话期间查询到的数据会放在本地缓存中
  - 以后如果需要相同的数据，直接从缓冲中拿，不再于数据库交互。
  - 默认开启，只在一次SqlSession中有效

## 3、二级缓存

- 二级缓存也叫全局缓存，由于一级缓存的作用域太低，因此而诞生
- 基于namespace的缓存，一个命名空间，对应一个二级缓存
- 工作机制
  - 一个会话查询一条数据，这个数据就会放在当前会话的一级缓存中
  - 如果当前会话关闭了，这个会话对应的一级缓存就没了，但是我们想要的是，会话关闭了，一级缓存会被保存到二级缓存中；
  - 新的会话查询信息，就可以从二级缓存中获取内容
  - 不同的mapper查出的数据会放出自己对应的缓存中

- 步骤

  1. 开启全局缓存

     ```xml
     <settings>
         <!--默认开启，显式提高可读性-->
         <setting name="cacheEnabled" value="true"/>
     </settings>
     ```

  2. 在要使用二级缓存的mapper中开启

     ```xml
     <cache eviction="FIFO"
                 flushInterval="60000"
                 size="512"
                 readOnly="true"/>
     ```

  3. 测试

     ```java
     public void getUserByID(){
         SqlSession sqlSession = MybatisUtils.getSqlSession();
         UserMapper mapper = sqlSession.getMapper(UserMapper.class);
         User userByID = mapper.getUserByID(1);
         System.out.println(userByID.toString());
         sqlSession.close();
     
         SqlSession sqlSession2 = MybatisUtils.getSqlSession();
         UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
         User userByID2 = mapper2.getUserByID(1);
         System.out.println(userByID2.toString());
         sqlSession2.close();
     }
     ```

     ![image-20201024145417940](C:\Users\chend\AppData\Roaming\Typora\typora-user-images\image-20201024145417940.png)

小结：

- 只要开启了二级缓存，在同一个mapper下就会生效
- 所有的数据都会放在一级缓存中
- 只有当会话提交，或者关闭的时候，才会提交到二级缓存中

## 4、缓存原理

缓存顺序

1. 第一次查询先看二级缓存中有没有
2. 在看一级缓存中有没有
3. 走数据库，然后放在一级缓存中
4. 关闭SqlSession之后，一级缓存会被放在对应Mapper的二级缓存中

