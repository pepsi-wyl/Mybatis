Mybtais  文档

---

[点击查看【bilibili】](https://player.bilibili.com/player.html?bvid=BV1NE411Q7Nx)

---

# 简介
## 什么是Mybatis

- Mybatis是一个优秀的持久层框架
- 支持定制化SQL、存储过程以及高级映射
- Mybatis避免了所有的JDBC代码和手动设置参数以及获取结果集
- MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。
- Mybatis本是apache的一个开源项目iBatis。2010年这个项目有apache software foundation 迁移到了google code，更名为Mybatis
- 2013年11月迁移到GitHub
# 入门   
## 安装
在pom.xml中导入mysql的驱动 
```xml
<dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.24</version>
</dependency>
```
使用 Maven 来构建项目，在 pom.xml 文件中导入Mybatis-jar包
```xml
<dependency>
       <groupId>org.mybatis</groupId>
       <artifactId>mybatis</artifactId>
       <version>3.4.6</version>
</dependency>
```
如需要简化实体类则需要在pom.xml导入lombok-jar包
```xml
<dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>1.18.12</version>
      <scope>provided</scope>
</dependency>
```
为了防止maven的静态资源导出问题需要在pom.xml中添加
```xml
    <!--在build中配置resources，避免资源导出失败问题-->
    <build>
        <resources>
            <resource>
                <directory>
                    src/main/resources
                </directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
            <resource>
                <directory>
                    src/main/java
                </directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
        </resources>
    </build>
```
推荐使用Idea安装MybatisX插件 可以完成Mapper层java代码与xml代码的跳转
![569c23e2e21e430a3c373c3be8fe8a1.png](https://cdn.nlark.com/yuque/0/2021/png/23219042/1640955466239-80ca7213-b6db-4070-9314-f01da976106a.png#crop=0&crop=0&crop=1&crop=1&height=73&id=uf057e7ee&margin=%5Bobject%20Object%5D&name=569c23e2e21e430a3c373c3be8fe8a1.png&originHeight=73&originWidth=332&originalType=binary&ratio=1&rotation=0&showTitle=false&size=4486&status=done&style=none&title=&width=332)![e6c263b5c967d84241174d24909cf26.png](https://cdn.nlark.com/yuque/0/2021/png/23219042/1640955543102-a64221e3-8ce6-4e6f-aa10-5d6ecca1f8a2.png#crop=0&crop=0&crop=1&crop=1&height=73&id=u5ceca1da&margin=%5Bobject%20Object%5D&name=e6c263b5c967d84241174d24909cf26.png&originHeight=64&originWidth=193&originalType=binary&ratio=1&rotation=0&showTitle=false&size=12145&status=done&style=none&title=&width=219)
## 编写XML配置文件
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC
        "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd"
        >
<!--xml文件的头部用来验证xml文档的正确性-->


<!--核心配置文件-->
<configuration>

    <!--起别名-->
    <typeAliases>
        <!--扫描包,别名默认为小写 @Alias("XXX")可以起别名 -->
        <package name="pojo"/>
    </typeAliases>
  
    <!--连接数据库环境-->
    <environments default="development">
        <environment id="development">
            <!--事务管理  JDBC-->
            <transactionManager type="JDBC"/>
            <!--数据池配置 连接池-->
            <dataSource type="POOLED">
                <!--mysql驱动-->
                <property name="driver"
                          value="com.mysql.cj.jdbc.Driver"/>
                <!--连接url-->
                <property name="url"
                          value="jdbc:mysql://localhost:3306/mybatis?useSSL=true&amp;useUnicode=true&amp;characterEncoding=utf8&amp;serverTimezone=UTC&amp;rewriteBatchedStatements=true"/>
                <!--数据库userName-->
                <property name="username"
                          value="root"/>
                <!--数据库密码-->
                <property name="password"
                          value="123456"/>
            </dataSource>
        </environment>
    </environments>

    <!--配置映射器 映射器的 XML 映射文件包含了 SQL 代码和映射定义信息 -->
    <mappers>
        <!--        <mapper resource="dao/userMapper.xml"/>-->
        <!--userMapper.xml 的注册   注意:必须使用 / -->
        <package name="mapper"/>
    </mappers>

</configuration>
```
## 编写MybatisUtils
```java
package utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

/**
 * MybatisUtils  线程安全
 * 实例化 SqlSessionFactory
 * 实例化 SqlSession
 * 关闭   SqlSession
 *
 * @author by wyl
 */

public class MybatisUtils {

    /**
     * 每个线程对其进行访问的时候访问的都是线程自己的变量
     * ThreadLocal
     */
    private static final ThreadLocal<SqlSession> threadLocal = new InheritableThreadLocal<>();

    private static final String resource = "mybatis-config.xml"; //mybatis --- xml配置文件地址   约定大于配置
    private static SqlSessionFactory sqlSessionFactory = null;   //Mybatis程序都是以SqlSessionFactory为核心

    /**
     * 禁止外界通过new方法创建(私有化构造器)
     */
    private MybatisUtils() {
    }


    /**
     * 从XML中构建 SqlSessionFactory对象   只需构建一次
     * static 构建
     */
    static {
        try {
            //SqlSessionFactory 的实例可以通过 SqlSessionFactoryBuilder.build方法从xml配置文件中获得
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream(resource));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取SqlSession对象实例
     * SqlSession 提供了在数据库执行 SQL 命令所需的所有方法
     * 线程安全  再该线程中获取sqlSession对象 没有则创建
     */
    public static SqlSession getSqlSession() {
        SqlSession sqlSession = threadLocal.get();              //从当前线程中获取SqlSession对象
        if (sqlSession == null) {
            sqlSession = sqlSessionFactory.openSession();       //创建SqlSession对象
            threadLocal.set(sqlSession);                        //将SqlSession对象与当前线程绑定到一起
        }
        return sqlSession;
    }

    /**
     * 关闭SqlSession对象实例与当前线程分开
     * 线程安全   该线程中sqlSession不为空 则可以关闭
     */
    public static void closeSqlSession() {
        SqlSession sqlSession = threadLocal.get();                  //从当前线程中获取SqlSession对象
        if (sqlSession != null) {
            sqlSession.close();                                     //关闭SqlSession对象
            threadLocal.remove();                                   //将SqlSession对象与当前线程绑定到一起
        }
    }

}
```
## 编写Pojo层实体类
```java
package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

/**
 * @author by wyl
 */

@Alias("user")

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class User {
    private int id;
    private String name;
    private String pwd;
}
```
## 编写Mapper层
### UserMapper.java
```java
package mapper;

import pojo.User;

import java.util.List;
import java.util.Map;

/**
 * UserMapper 相当于UserDao接口  定义一些接口方法
 *
 * @author by wyl
 */

public interface UserMapper{
  
    /**
     * 得到全部用户集合
     */
    List<User> getUserList();

    /**
     * 查询用户ByID
     */
    User getUserByID(int id);

    /**
     * 添加新用户
     */
    int addUser(User user);

    /**
     * 删除用户ByID
     */
    int deleteUserByID(int id);

    /**
     * 修改用户ByID
     */
    int modifyUserByID(User user);
}

```
### UserMapper.xml
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC
        "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd"
>

<!--
注意:
     mapper.xml一定要在主配置文件中注册映射
-->

<!--命名空间  绑定对应的接口(全类名)   注意:必须使用  -->
<mapper namespace="mapper.UserMapper">

    <!-- #{}是经过预编译的，是安全的；${}是未经过预编译的，仅仅是取变量的值，是非安全的，存在SQL注入-->

    <!--绑定接口中对应的方法(id)   参数类型(parameterType)  返回值类型(resultType)  注意:集合需要返回对应的实体类  -->
    <select id="getUserList" resultType="user">
        <!--编写sql语句-->
        select * from mybatis.user;
    </select>

    <select id="getUserByID" parameterType="int" resultType="user">
        select *
        from mybatis.user
        where id = #{id};
    </select>

    <insert id="addUser" parameterType="user">
        insert into mybatis.user
            value (#{id}, #{name}, #{pwd});
    </insert>

    <delete id="deleteUserByID" parameterType="int">
        delete
        from mybatis.user
        where id = #{id};
    </delete>

    <update id="modifyUserByID" parameterType="user">
        update mybatis.user
        set name=#{name},
            pwd=#{pwd}
        where id = #{id};
    </update>
  
</mapper>

```
## 测试
```java
import mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import pojo.User;
import utils.MybatisUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mybatis_T
 *
 * @author by wyl
 */

public class T {

    @Test
    public void query_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = userMapper.getUserList();
        userList.forEach((v) -> System.out.println(v));
        MybatisUtils.closeSqlSession();
    }

    @Test
    public void queryByID_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User userByID = userMapper.getUserByID(100);
        System.out.println(userByID);
        MybatisUtils.closeSqlSession();
    }
    
    @Test
    public void addUser_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        userMapper.addUser(new User(5, "武扬岚", "8888888"));
        sqlSession.commit();               //update数据必须提交
        MybatisUtils.closeSqlSession();
    }

    @Test
    public void delUser_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        userMapper.deleteUserByID(5);
        sqlSession.commit();              //update数据必须提交
        MybatisUtils.closeSqlSession();
    }

    @Test
    public void modifyUser_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        userMapper.modifyUserByID(new User(4, "武扬岚", "888888"));
        sqlSession.commit();              //update数据必须提交
        MybatisUtils.closeSqlSession();
    }
}

```
## 秀一波
### 万能的map
Mapper接口
```java
    /**
     * 修改密码ByID------>万能的Map
     * 利用map存储数据   参数名不需要域数据库字段名称对应
     */
    int modifyPwdByID(Map<String, Object> map);
```
xml配置sql代码
```xml
    <!--
    万能的map
    -->
    <update id="modifyPwdByID" parameterType="map">
        update mybatis.user
        set pwd=#{pwd}
        where id = #{id};
    </update>
```
测试类
```java
    /**
     * 利用map储存数据   可以避免传入参数过多
     */
    @Test
    public void modifyPwd_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        Map<String, Object> map = new HashMap<>();
        map.put("pwd", "999999");
        map.put("id", 3);
        userMapper.modifyPwdByID(map);

        sqlSession.commit();         //update数据必须提交
        MybatisUtils.closeSqlSession();
    }
```
### 模糊查询
Mapper接口
```java
    /**
     * 模糊查询
     * 1.传入参数时候就进行拼串        "%"+"name"+"%"
     * 2.在xml中的sql语句中进行拼串   name like concat('%', #{name}, '%')
     * 查询ByName
     */
    List<User> getUserByName(String name);
```
xml配置sql代码
```xml
    <!--
    模糊查询
        在xml中的sql语句中进行拼串   name like concat('%', #{name}, '%')
    -->
    <select id="getUserByName" parameterType="String" resultType="user">
        select *
        from mybatis.user
        where name like concat('%', #{name}, '%');
    </select>

```
测试类
```java
    /**
     * 模糊查询
     * 传入参数时候就进行拼串        "%"+"name"+"%"
     */
    @Test
    public void queryByName_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = userMapper.getUserByName("%武%");
        userList.forEach((v) -> System.out.println(v));
        MybatisUtils.closeSqlSession();
    }
```
### 分页查询
#### Limit实现分页
Mapper接口
```java
    /**
     * 分页得到全部用户集合
     */
    List<User> getUserListLimit(Map<String, Object> map);
```
xml配置sql代码
```xml
    <!--limit实现分页  sql层-->
    <select id="getUserListLimit" parameterType="map" resultMap="user">
        select *
        from mybatis.user
        limit #{startIndex} , #{pageSize};
    </select>
```
测试类
```java
    @Test
    public void queryLimit_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageSize", 2);
        map.put("startIndex", 4 * (int) map.get("pageSize"));

        List<User> userListLimit = mapper.getUserListLimit(map);
        userListLimit.forEach((v) -> System.out.println(v));
        sqlSession.close();
    }
```
#### RowBounds实现分页
Mapper接口
```java
    /**
     * 得到全部用户集合
     */
    List<User> getUserListRowBounds();
```
xml配置sql代码
```xml
    <!--RowBounds实现分页   面向对象层-->
    <select id="getUserListRowBounds" resultMap="user">
        select *
        from mybatis.user
    </select>
```
测试类
```java
    @Test                                     //过时
    public void queryRowBounds_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        List<User> userListRowBounds = sqlSession.selectList("dao.UserMapper.getUserListRowBounds", null, new RowBounds(0, 2));
        userListRowBounds.forEach((v) -> System.out.println(v));
        sqlSession.close();
    }
```
## 其他
### 作用域(Scoope)和生命周期
SqlSessionFactoryBuilder 
这个类可以被实例化、使用和丢弃，一旦创建了 SqlSessionFactory，就不再需要它了。 因此 SqlSessionFactoryBuilder 实例的最佳作用域是方法作用域（也就是局部方法变量）。 你可以重用 SqlSessionFactoryBuilder 来创建多个 SqlSessionFactory 实例，但最好还是不要一直保留着它，以保证所有的 XML 解析资源可以被释放给更重要的事情。
SqlSessionFactory
SqlSessionFactory 一旦被创建就应该在应用的运行期间一直存在，没有任何理由丢弃它或重新创建另一个实例。 使用 SqlSessionFactory 的最佳实践是在应用运行期间不要重复创建多次，多次重建 SqlSessionFactory 被视为一种代码“坏习惯”。因此 SqlSessionFactory 的最佳作用域是应用作用域。 有很多方法可以做到，最简单的就是使用单例模式或者静态单例模式。
SqlSession
每个线程都应该有它自己的 SqlSession 实例。SqlSession 的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。 绝对不能将 SqlSession 实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。 也绝不能将 SqlSession 实例的引用放在任何类型的托管作用域中，比如 Servlet 框架中的 HttpSession。 如果你现在正在使用一种 Web 框架，考虑将 SqlSession 放在一个和 HTTP 请求相似的作用域中。 换句话说，每次收到 HTTP 请求，就可以打开一个 SqlSession，返回一个响应后，就关闭它。 
_/**
 * 生命周期
 * SqlSessionFactoryBuilder    创建sqlSessionFactory                    匿名对象
 * sqlSessionFactory               创建SqlSession                               单例模式，静态单例模式          数据库连接池
 * SqlSession                          getMapper(连接连接池的请求)       线程不安全，用完就要关闭        一次连接
 * Mapper                                                                                    执行数据库的一次操作
 */_
### 映射器
     映射器是一些绑定映射语句的接口。映射器接口的实例是从 SqlSession 中获得的。虽然从技术层面上来讲，任何映射器实例的最大作用域与请求它们的 SqlSession 相同。但方法作用域才是映射器实例的最合适的作用域。 也就是说，映射器实例应该在调用它们的方法中被获取，使用完毕之后即可丢弃。 映射器实例并不需要被显式地关闭。尽管在整个请求作用域保留映射器实例不会有什么问题，但是你很快会发现，在这个作用域上管理太多像 SqlSession 的资源会让你忙不过来。
# 注解开发
## 注解实现CRUD
Mapper接口    
```java
package mapper;

import org.apache.ibatis.annotations.*;
import pojo.User;

import java.util.List;

/**
 * @author by wyl
 */

/**
 * 注解编写SQL语句
 */

public interface UserMapper {

    /**
     * 得到全部用户集合
     */
    @Select("select * from user;")
    List<User> getUserList();

    /**
     * 查询用户ByID
     */
    @Select("select * from user where id=#{UserId};")
    User getUserByID(@Param("UserId") int id);
    //

    /**
     * 模糊查询
     */
    @Select("select * from mybatis.user where name like concat('%', #{UserName}, '%');")
    List<User> getUserByName(@Param("UserName") String name);

    /**
     * 添加新用户
     */
    @Insert("insert into user value (#{id},#{name},#{pwd});")
    int addUser(User user);

    /**
     * 删除用户ByID
     */
    @Delete("delete from mybatis.user where id = #{UserId};")
    int deleteUserByID(@Param("UserId") int id);

    /**
     * 修改用户ByID
     */
    @Update("update mybatis.user set name=#{name}, pwd=#{pwd} where id = #{id};")
    int modifyUserByID(User user);

}

```
注意  @Param  解决参数名称与SQL语句中名称不一致问题
```java
    @Select("select * from user where id=#{UserId};")
    User getUserByID(@Param("UserId") int id);
```
# 配置
## 属性(propserties)
### 配置和使用属性
编写mysql.properties数据库配置文件(将连接数据库的参数写入配置文件中)
```properties
#mysql.propserties
driver=com.mysql.cj.jdbc.Driver
user=root
password=XXXXXX
url=jdbc:mysql://x.x.x.x:3306/mybatis?useSSL=true&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&rewriteBatchedStatements=true
```
配置属性 （从Mybatis3.4.2开始为这占位符指定默认值）
```java
<!--引入外部的配置文件 mysql.properties-->
    <properties resource="mysql.properties">   <!--资源路径-->
    
        <!--内部配置-->
        <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
        
    ----------------------占位符配置-----------------------------
        <!--    启动为占位符指定一个默认值   默认关闭，需要开启 -->
        <property name="org.apache.ibatis.parsing.PropertyParser.enable-default-value"
                  value="true"/>
        <!--    修改默认分割符号  默认为: (diy占位符) -->
        <property name="org.apache.ibatis.parsing.PropertyParser.default-value-separator"
                  value="?:"/> <!-- 修改默认值的分隔符为: ?:  -->
    --------------------------------------------------------------

    </properties>
```
对数据库资源进行配置
```xml
 <dataSource type="POOLED">   <!--池子-->
       <!--配置的值
           1.写在当前文件中
           2.写在properties中property标签中
           3.利用properties引入外部配置文件
           4:配置默认值  MyBatis 3.4.2开始       需要在properties启用这个特性   分割符号可自定义
       -->
       <property name="driver" value="${driver?:com.mysql.cj.jdbc.Driver}"/>    <!--mysql驱动-->
       <property name="url" value="${url}"/>     <!--连接url-->
       <property name="username" value="${user?:root}"/>    <!--数据库name-->
       <property name="password" value="${password}"/>   <!--数据库密码-->
</dataSource>
```
### 注意   
一个属性在多个地方配置，加载顺序为

1. 首先读取在 properties 元素体内指定的属性
1. 然后根据 properties 元素中的 resource 属性读取类路径下属性文件，或根据 url 属性指定的路径读取属性文件，并覆盖之前读取过的同名属性
1. 最后读取作为方法参数传递的属性，并覆盖之前读取过的同名属性

因此，通过方法参数传递的属性具有最高优先级，resource/url 属性中指定的配置文件次之，最低优先级的则是 properties 元素中指定的属性。
## 设置(settings)
这是 MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的运行时行为
### 常用设置
```xml
<!--Mybatis调整设置 会改变Mybatis的运行时行为-->
    <settings>

        <!--配置全局性 cache 的 ( 开 / 关) default:true -->
        <setting name="cacheEnabled" value="true"/>
        <!--是否允许在嵌套语句中使用分页（RowBounds） 如果允许使用则设置为 false-->
        <setting name="safeRowBoundsEnabled" value="false"/>
        <!--[是否 启用  数据中 A_column 自动映射 到 Java类中驼峰命名的属性 default:false]-->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <!-- 全局启用或禁用延迟加载。当禁用时，所有关联对象都会即时加载。 -->
        <setting name="lazyLoadingEnabled" value="true"/>
        <!-- 当启用时，有延迟加载属性的对象在被调用时将会完全加载任意属性。否则，每种属性将会按需要加载。 -->
        <setting name="aggressiveLazyLoading" value="true"/>
        <!-- 是否允许单条sql 返回多个数据集  (取决于驱动的兼容性) default:true -->
        <setting name="multipleResultSetsEnabled" value="true"/>
        <!-- 是否可以使用列的别名 (取决于驱动的兼容性) default:true -->
        <setting name="useColumnLabel" value="true"/>
        <!-- 允许JDBC 生成主键。需要驱动器支持。如果设为了true，这个设置将强制使用被生成的主键，有一些驱动器不兼容不过仍然可以执行。  default:false  -->
        <setting name="useGeneratedKeys" value="true"/>
        <!-- 指定 MyBatis 如何自动映射 数据基表的列 NONE：不隐射　PARTIAL:部分  FULL:全部  -->
        <setting name="autoMappingBehavior" value="PARTIAL"/>
        <!-- 这是默认的执行类型  （SIMPLE: 简单； REUSE: 执行器可能重复使用prepared statements语句；BATCH: 执行器可以重复执行语句和批量更新）  -->
        <setting name="defaultExecutorType" value="SIMPLE"/>
        <!-- 设置本地缓存范围 session:就会有数据的共享  statement:语句范围 (这样就不会有数据的共享 ) default:session -->
        <setting name="localCacheScope" value="SESSION"/>
        <!-- 设置但JDBC类型为空时,某些驱动程序 要指定值,default:OTHER，插入空值时不需要指定类型 -->
        <setting name="jdbcTypeForNull" value="NULL"/>

        <!--日志工厂-->
        <!--STDOUT_LOGGING标准日志输出-->
        <!--<setting name="logImpl" value="STDOUT_LOGGING"/>-->
        <!--LOG4J-->
        <setting name="logImpl" value="LOG4J"/>

    </settings>
```
### 官方文档设置
| 设置名 | 描述 | 有效值 | 默认值 |
| --- | --- | --- | --- |
| cacheEnabled | 全局性地开启或关闭所有映射器配置文件中已配置的任何缓存。 | true &#124; false | true |
| lazyLoadingEnabled | 延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置 fetchType 属性来覆盖该项的开关状态。 | true &#124; false | false |
| aggressiveLazyLoading | 开启时，任一方法的调用都会加载该对象的所有延迟加载属性。 否则，每个延迟加载属性会按需加载（参考 lazyLoadTriggerMethods)。 | true &#124; false | false （在 3.4.1 及之前的版本中默认为 true） |
| multipleResultSetsEnabled | 是否允许单个语句返回多结果集（需要数据库驱动支持）。 | true &#124; false | true |
| useColumnLabel | 使用列标签代替列名。实际表现依赖于数据库驱动，具体可参考数据库驱动的相关文档，或通过对比测试来观察。 | true &#124; false | true |
| useGeneratedKeys | 允许 JDBC 支持自动生成主键，需要数据库驱动支持。如果设置为 true，将强制使用自动生成主键。尽管一些数据库驱动不支持此特性，但仍可正常工作（如 Derby）。 | true &#124; false | False |
| autoMappingBehavior | 指定 MyBatis 应如何自动映射列到字段或属性。 NONE 表示关闭自动映射；PARTIAL 只会自动映射没有定义嵌套结果映射的字段。 FULL 会自动映射任何复杂的结果集（无论是否嵌套）。 | NONE, PARTIAL, FULL | PARTIAL |
| autoMappingUnknownColumnBehavior | 指定发现自动映射目标未知列（或未知属性类型）的行为。
- NONE: 不做任何反应
- WARNING: 输出警告日志（'org.apache.ibatis.session.AutoMappingUnknownColumnBehavior' 的日志等级必须设置为 WARN）
- FAILING: 映射失败 (抛出 SqlSessionException)
 | NONE, WARNING, FAILING | NONE |
| defaultExecutorType | 配置默认的执行器。SIMPLE 就是普通的执行器；REUSE 执行器会重用预处理语句（PreparedStatement）； BATCH 执行器不仅重用语句还会执行批量更新。 | SIMPLE REUSE BATCH | SIMPLE |
| defaultStatementTimeout | 设置超时时间，它决定数据库驱动等待数据库响应的秒数。 | 任意正整数 | 未设置 (null) |
| defaultFetchSize | 为驱动的结果集获取数量（fetchSize）设置一个建议值。此参数只可以在查询设置中被覆盖。 | 任意正整数 | 未设置 (null) |
| defaultResultSetType | 指定语句默认的滚动策略。（新增于 3.5.2） | FORWARD_ONLY &#124; SCROLL_SENSITIVE &#124; SCROLL_INSENSITIVE &#124; DEFAULT（等同于未设置） | 未设置 (null) |
| safeRowBoundsEnabled | 是否允许在嵌套语句中使用分页（RowBounds）。如果允许使用则设置为 false。 | true &#124; false | False |
| safeResultHandlerEnabled | 是否允许在嵌套语句中使用结果处理器（ResultHandler）。如果允许使用则设置为 false。 | true &#124; false | True |
| mapUnderscoreToCamelCase | 是否开启驼峰命名自动映射，即从经典数据库列名 A_COLUMN 映射到经典 Java 属性名 aColumn。 | true &#124; false | False |
| localCacheScope | MyBatis 利用本地缓存机制（Local Cache）防止循环引用和加速重复的嵌套查询。 默认值为 SESSION，会缓存一个会话中执行的所有查询。 若设置值为 STATEMENT，本地缓存将仅用于执行语句，对相同 SqlSession 的不同查询将不会进行缓存。 | SESSION &#124; STATEMENT | SESSION |
| jdbcTypeForNull | 当没有为参数指定特定的 JDBC 类型时，空值的默认 JDBC 类型。 某些数据库驱动需要指定列的 JDBC 类型，多数情况直接用一般类型即可，比如 NULL、VARCHAR 或 OTHER。 | JdbcType 常量，常用值：NULL、VARCHAR 或 OTHER。 | OTHER |
| lazyLoadTriggerMethods | 指定对象的哪些方法触发一次延迟加载。 | 用逗号分隔的方法列表。 | equals,clone,hashCode,toString |
| defaultScriptingLanguage | 指定动态 SQL 生成使用的默认脚本语言。 | 一个类型别名或全限定类名。 | org.apache.ibatis.scripting.xmltags.XMLLanguageDriver |
| defaultEnumTypeHandler | 指定 Enum 使用的默认 TypeHandler 。（新增于 3.4.5） | 一个类型别名或全限定类名。 | org.apache.ibatis.type.EnumTypeHandler |
| callSettersOnNulls | 指定当结果集中值为 null 的时候是否调用映射对象的 setter（map 对象时为 put）方法，这在依赖于 Map.keySet() 或 null 值进行初始化时比较有用。注意基本类型（int、boolean 等）是不能设置成 null 的。 | true &#124; false | false |
| returnInstanceForEmptyRow | 当返回行的所有列都是空时，MyBatis默认返回 null。 当开启这个设置时，MyBatis会返回一个空实例。 请注意，它也适用于嵌套的结果集（如集合或关联）。（新增于 3.4.2） | true &#124; false | false |
| logPrefix | 指定 MyBatis 增加到日志名称的前缀。 | 任何字符串 | 未设置 |
| logImpl | 指定 MyBatis 所用日志的具体实现，未指定时将自动查找。 | SLF4J &#124; LOG4J &#124; LOG4J2 &#124; JDK_LOGGING &#124; COMMONS_LOGGING &#124; STDOUT_LOGGING &#124; NO_LOGGING | 未设置 |
| proxyFactory | 指定 Mybatis 创建可延迟加载对象所用到的代理工具。 | CGLIB &#124; JAVASSIST | JAVASSIST （MyBatis 3.3 以上） |
| vfsImpl | 指定 VFS 的实现 | 自定义 VFS 的实现的类全限定名，以逗号分隔。 | 未设置 |
| useActualParamName | 允许使用方法签名中的名称作为语句参数名称。 为了使用该特性，你的项目必须采用 Java 8 编译，并且加上 -parameters 选项。（新增于 3.4.1） | true &#124; false | true |
| configurationFactory | 指定一个提供 Configuration 实例的类。 这个被返回的 Configuration 实例用来加载被反序列化对象的延迟加载属性值。 这个类必须包含一个签名为static Configuration getConfiguration() 的方法。（新增于 3.2.3） | 一个类型别名或完全限定类名。 | 未设置 |

## 类型别名(typeAliases)
### 自定义别名
类型别名可为 Java 类型设置一个缩写名字。 它仅用于 XML 配置，意在降低冗余的全限定类名书写
```xml
<typeAliases>
  
<!--方式一:给类起别名      适用于实体类较少   parameterType可以自定义  -->
<typeAlias type="pojo.User" alias="user"></typeAlias>
        
<!--方式二:给指定包起别名 (扫描包)  适用于实体类较多 parameterType默认为类名小写    自定义需要再实体类上添加注解:@Alias("user")  -->
<package name="pojo"/>
  
</typeAliases>
```
通常配置为扫描指定包，对包中的实体类添加注解起别名(虽然默认别名为小写，但写上较为直观)
### Java 类型内建的类型别名

- 基本类型      int       _int    ......
- 

| 别名 | 映射的类型 |
| --- | --- |
| _byte | byte |
| _long | long |
| _short | short |
| _int | int |
| _integer | int |
| _double | double |
| _float | float |
| _boolean | boolean |
| string | String |
| byte | Byte |
| long | Long |
| short | Short |
| int | Integer |
| integer | Integer |
| double | Double |
| float | Float |
| boolean | Boolean |
| date | Date |
| decimal | BigDecimal |
| bigdecimal | BigDecimal |
| object | Object |
| map | Map |
| hashmap | HashMap |
| list | List |
| arraylist | ArrayList |
| collection | Collection |
| iterator | Iterator |

## 环境配置(environments)
可以配置多组环境，但每个 SqlSessionFactory 实例只能选择一种环境
### 配置环境
```xml
<!--连接数据库环境-->
    <!--  可以有多套配置环境       通过   default <-> id   的值来控制      每次只能选择一个环境   -->
    <environments default="configuration-1">

        <environment id="configuration-1">
          
            <!-- 事务管理器  { JDBC , MANAGED } JDBC提交事物的回滚与提交  注意:Spring+Mybatis中不用配置事务管理器(spring自带会覆盖)-->
            <transactionManager type="JDBC"/>
          
            <!-- 数据源   UNPOILED  POOLED  JNDI -->
            <dataSource type="POOLED">   <!--池子-->
                <property name="driver" value="${driver?:com.mysql.cj.jdbc.Driver}"/>    <!--mysql驱动-->
                <property name="url" value="${url}"/>     <!--连接url-->
                <property name="username" value="${user?:root}"/>    <!--数据库name-->
                <property name="password" value="${password}"/>   <!--数据库密码-->
            </dataSource>
          
        </environment>

        <environment id="configuration-2">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url"
                          value="jdbc:mysql://localhost:3306/mybatis?useSSL=true&amp;useUnicode=true&amp;characterEncoding=utf8&amp;serverTimezone=UTC&amp;rewriteBatchedStatements=true"/>
                <property name="username" value="root"/>
                <property name="password" value="bsy8023.00"/>
            </dataSource>
        </environment>

    </environments>
```
### 事务管理器
在 MyBatis 中有两种类型的事务管理器（也就是 type="[JDBC|MANAGED]"）

- JDBC – 这个配置直接使用了 JDBC 的提交和回滚设施，它依赖从数据源获得的连接来管理事务作用域。
- MANAGED – 这个配置几乎没做什么。它从不提交或回滚一个连接，而是让容器来管理事务的整个生命周期（比如 JEE 应用服务器的上下文）。 默认情况下它会关闭连接。然而一些容器并不希望连接被关闭，因此需要将 closeConnection 属性设置为 false 来阻止默认的关闭行为。例如:<transactionManager type="MANAGED">   <property name="closeConnection" value="false"/> </transactionManager>

如果你正在使用 Spring + MyBatis，则没有必要配置事务管理器，因为 Spring 模块会使用自带的管理器来覆盖前面的配置。
### **数据源（dataSource）**
有三种内建的数据源类型（也就是 type="[UNPOOLED|POOLED|JNDI]"）
**UNPOOLED**– 这个数据源的实现会每次请求时打开和关闭连接。虽然有点慢，但对那些数据库连接可用性要求不高的简单应用程序来说，是一个很好的选择。 性能表现则依赖于使用的数据库，对某些数据库来说，使用连接池并不重要，这个配置就很适合这种情形。UNPOOLED 类型的数据源仅仅需要配置以下 5 种属性：

- driver – 这是 JDBC 驱动的 Java 类全限定名（并不是 JDBC 驱动中可能包含的数据源类）。
- url – 这是数据库的 JDBC URL 地址。
- username – 登录数据库的用户名。
- password – 登录数据库的密码。
- defaultTransactionIsolationLevel – 默认的连接事务隔离级别。
- defaultNetworkTimeout – 等待数据库操作完成的默认网络超时时间（单位：毫秒）。查看 java.sql.Connection#setNetworkTimeout() 的 API 文档以获取更多信息。

作为可选项，你也可以传递属性给数据库驱动。只需在属性名加上“driver.”前缀即可，例如：

- driver.encoding=UTF8

这将通过 DriverManager.getConnection(url, driverProperties) 方法传递值为 UTF8 的 encoding 属性给数据库驱动。
**POOLED**– 这种数据源的实现利用“池”的概念将 JDBC 连接对象组织起来，避免了创建新的连接实例时所必需的初始化和认证时间。 这种处理方式很流行，能使并发 Web 应用快速响应请求。
除了上述提到 UNPOOLED 下的属性外，还有更多属性用来配置 POOLED 的数据源：

- poolMaximumActiveConnections – 在任意时间可存在的活动（正在使用）连接数量，默认值：10
- poolMaximumIdleConnections – 任意时间可能存在的空闲连接数。
- poolMaximumCheckoutTime – 在被强制返回之前，池中连接被检出（checked out）时间，默认值：20000 毫秒
- poolTimeToWait – 这是一个底层设置，如果获取连接花费了相当长的时间，连接池会打印状态日志并重新尝试获取一个连接（避免在误配置的情况下一直失败且不打印日志），默认值：20000 毫秒（即 20 秒）。
- poolMaximumLocalBadConnectionTolerance – 这是一个关于坏连接容忍度的底层设置， 作用于每一个尝试从缓存池获取连接的线程。 如果这个线程获取到的是一个坏的连接，那么这个数据源允许这个线程尝试重新获取一个新的连接，但是这个重新尝试的次数不应该超过 poolMaximumIdleConnections 与 poolMaximumLocalBadConnectionTolerance 之和。 默认值：3（新增于 3.4.5）
- poolPingQuery – 发送到数据库的侦测查询，用来检验连接是否正常工作并准备接受请求。默认是“NO PING QUERY SET”，这会导致多数数据库驱动出错时返回恰当的错误消息。
- poolPingEnabled – 是否启用侦测查询。若开启，需要设置 poolPingQuery 属性为一个可执行的 SQL 语句（最好是一个速度非常快的 SQL 语句），默认值：false。
- poolPingConnectionsNotUsedFor – 配置 poolPingQuery 的频率。可以被设置为和数据库连接超时时间一样，来避免不必要的侦测，默认值：0（即所有连接每一时刻都被侦测 — 当然仅当 poolPingEnabled 为 true 时适用）。

**JNDI** – 这个数据源实现是为了能在如 EJB 或应用服务器这类容器中使用，容器可以集中或在外部配置数据源，然后放置一个 JNDI 上下文的数据源引用。这种数据源配置只需要两个属性：

- initial_context – 这个属性用来在 InitialContext 中寻找上下文（即，initialContext.lookup(initial_context)）。这是个可选属性，如果忽略，那么将会直接从 InitialContext 中寻找 data_source 属性。
- data_source – 这是引用数据源实例位置的上下文路径。提供了 initial_context 配置时会在其返回的上下文中进行查找，没有提供时则直接在 InitialContext 中查找。

和其他数据源配置类似，可以通过添加前缀“env.”直接把属性传递给 InitialContext。比如：

- env.encoding=UTF8

这就会在 InitialContext 实例化时往它的构造方法传递值为 UTF8 的 encoding 属性
## 数据库厂商标识(databaseIdProvider)
### 配置
```xml
<!--   
数据库厂商标识（databaseIdProvider）  可以根据不同的数据库厂商执行不同的语句   
在Mapper.xml文件中配置     databaseId="mysql"  databaseId="oracle"  
-->
<databaseIdProvider type="DB_VENDOR">
     <property name="MySQL" value="mysql"/>
     <property name="Oracle" value="oracle"/>
</databaseIdProvider>
```
### 使用
```xml
<select id="getUserList" resultMap="userMap" databaseId="Mysql">
      select *
      from mybatis.user;
</select>
```
## 映射器(mappers)
既然 MyBatis 的行为已经由上述元素配置完了，我们现在就要来定义 SQL 映射语句了。 但首先，我们需要告诉 MyBatis 到哪里去找到这些语句。 在自动查找资源方面，Java 并没有提供一个很好的解决方案，所以最好的办法是直接告诉 MyBatis 到哪里去找映射文件。 你可以使用相对于类路径的资源引用，或完全限定资源定位符（包括 file:/// 形式的 URL），或类名和包名等
### 配置
```xml
<!--映射器 mappers    告诉 MyBatis 到哪里去找映射文件 -->
<mappers>
  
     <!--使用相对于类路径的资源引用            注意:必须使用 / -->
     <mapper resource="dao/serMapper.xml"/>  
  
     <!--使用映射器接口实现类的完全限定类名      注意:接口和mapper配置文件必须同名且在一共包下 -->
     <mapper class="dao.UserMapper"/>
  
     <!--将包内的映射器接口实现全部注册为映射器   注意:接口和mapper配置文件必须同名且在一共包下 -->
     <package name="mapper"/>
  
</mappers>
```
通常经常采用扫描包(将包内的映射器接口全部注册为映射器)
# XML映射器
MyBatis 的真正强大在于它的语句映射，这是它的魔力所在。MyBatis 致力于减少使用成本，让用户能更专注于 SQL 代码
## xml文件结构
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 
#{}是经过预编译的，是安全的；${}是未经过预编译的，仅仅是取变量的值，是非安全的，存在SQL注入
-->

<!--绑定对应的接口(全类名)   注意:命名空间必须使用  -->
<mapper namespace="mapper.UserMapper">
    
</mapper>

```
## 参数
### select
```xml
<select
  id="selectPerson"    
  parameterType="int"
  parameterMap="deprecated"
  resultType="hashmap"
  resultMap="personResultMap"
  flushCache="false"
  useCache="true"
  timeout="10"
  fetchSize="256"
  statementType="PREPARED"
  resultSetType="FORWARD_ONLY"
  databaseId="mysql"
>
```
| 属性 | 描述 |
| --- | --- |
| id | 在命名空间中唯一的标识符，可以被用来引用这条语句。 |
| parameterType | 将会传入这条语句的参数的类全限定名或别名。这个属性是可选的，因为 MyBatis 可以通过类型处理器（TypeHandler）推断出具体传入语句的参数，默认值为未设置（unset）。 |
| ~~parameterMap~~ | ~~用于引用外部 parameterMap 的属性，目前已被废弃。请使用行内参数映射和 parameterType 属性。~~ |
| resultType | 期望从这条语句中返回结果的类全限定名或别名。 注意，如果返回的是集合，那应该设置为集合包含的类型，而不是集合本身的类型。 resultType 和 resultMap 之间只能同时使用一个。 |
| resultMap | 对外部 resultMap 的命名引用。结果映射是 MyBatis 最强大的特性，如果你对其理解透彻，许多复杂的映射问题都能迎刃而解。 resultType 和 resultMap 之间只能同时使用一个。 |
| flushCache | 将其设置为 true 后，只要语句被调用，都会导致本地缓存和二级缓存被清空，默认值：false。 |
| useCache | 将其设置为 true 后，将会导致本条语句的结果被二级缓存缓存起来，默认值：对 select 元素为 true。 |
| timeout | 这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数。默认值为未设置（unset）（依赖数据库驱动）。 |
| fetchSize | 这是一个给驱动的建议值，尝试让驱动程序每次批量返回的结果行数等于这个设置值。 默认值为未设置（unset）（依赖驱动）。 |
| statementType | 可选 STATEMENT，PREPARED 或 CALLABLE。这会让 MyBatis 分别使用 Statement，PreparedStatement 或 CallableStatement，默认值：PREPARED。 |
| resultSetType | FORWARD_ONLY，SCROLL_SENSITIVE, SCROLL_INSENSITIVE 或 DEFAULT（等价于 unset） 中的一个，默认值为 unset （依赖数据库驱动）。 |
| databaseId | 如果配置了数据库厂商标识（databaseIdProvider），MyBatis 会加载所有不带 databaseId 或匹配当前 databaseId 的语句；如果带和不带的语句都有，则不带的会被忽略。 |
| resultOrdered | 这个设置仅针对嵌套结果 select 语句：如果为 true，将会假设包含了嵌套结果集或是分组，当返回一个主结果行时，就不会产生对前面结果集的引用。 这就使得在获取嵌套结果集的时候不至于内存不够用。默认值：false。 |
| resultSets | 这个设置仅适用于多结果集的情况。它将列出语句执行后返回的结果集并赋予每个结果集一个名称，多个名称之间以逗号分隔 |

### update
```xml
<insert
  id="insertAuthor"
  parameterType="domain.blog.Author"
  flushCache="true"
  statementType="PREPARED"
  keyProperty=""
  keyColumn=""
  useGeneratedKeys=""
  timeout="20"
>

<update
  id="updateAuthor"
  parameterType="domain.blog.Author"
  flushCache="true"
  statementType="PREPARED"
  timeout="20"
>

<delete
  id="deleteAuthor"
  parameterType="domain.blog.Author"
  flushCache="true"
  statementType="PREPARED"
  timeout="20"
>
```
### SQL参数
#### ${}与#{}
其中 ${column} 会被直接替换，而 #{value} 会使用 ? 预处理
```xml
@Select("select * from user where ${column} = #{value}")
User findByColumn(@Param("column") String column, @Param("value") String value);
```
注意：
      用这种方式接受用户的输入，并用作语句参数是不安全的，会导致潜在的 SQL 注入攻击。因此，要么不允许用户输入这些字段，要么自行转义并检验这些参数
#### 参数类型(parameterType)
简单参数
```xml
<select id="selectUsers" resultType="User">   <!--结果集映射-->
  select id, username, password
  from users
  where id = #{id}
</select>
```
对象参数
```xml
<insert id="insertUser" parameterType="User">
  insert into users (id, username, password)
  values (#{id}, #{username}, #{password})
</insert>
```
参数指定特殊类型
```xml
#{property,javaType=int,jdbcType=NUMERIC}
可以推断出javType  除非是HashMap(显示指定)
```
对于数值类型，还可以设置 numericScale 指定小数点后保留的位数
```xml
#{height,javaType=double,jdbcType=NUMERIC,numericScale=2}
```
## 结果映射(resultMap)
### 简单映射
只是简单地将所有的列映射到 HashMap 的键上
```xml
<select id="selectUsers" resultType="map">
  select id, username, hashedPassword
  from some_table
  where id = #{id}
</select>
```
### JavaBean(POJO)
#### 普通情况
```java
package pojo;

import lombok.*;
import org.apache.ibatis.type.Alias;

/**
 * @author by wyl
 */

@Alias("user")  //对该实体类起别名

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class User {
    private int id;
    private String name;
    private String pwd;  
}

```
这样JavaBean被映射到ResultMap上   （忽略大小写）
```xml
//假设数据库中为pwd
<select id="getUserByID" parameterType="int" resultType="user">
        select id,name,pwd
        from mybatis.user
        where id = #{id};
</select>
```
MyBatis 会在幕后自动创建一个 ResultMap，再根据属性名来映射列到 JavaBean 的属性上
#### 列名和属性名称不匹配(起别名)
```xml
//假设数据库中字段为password
<select id="getUserByID" resultType="User">
  select
    id          as "id",
    name        as "name",
    password    as "pwd"
  from mybatis.user
  where id = #{id}
</select>
```
#### 列名和属性名称不匹配(配置ResultMap)
配置ResultMap
```xml
//假设数据库中字段为password
<!--    column数据库中的字段    property实体类中的属性    -->
<resultMap id="userMap" type="user"> 
    <result column="password" property="pwd"/>
</resultMap>
```
注意：

- column数据库中的字段    property实体类中的属性
- 字段映射只需配置字段不同的即可(相同的字段会自己映射)

引用ResultMap
```xml
<select id="getUserByID" resultMap="userMap">
        select id,name,pwd
        from mybatis.user
        where id = #{id};
</select>
```
### 高级映射
编写student实体类  (多个学生对一个老师)
```java
package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

/**
 * @author by wyl
 */

@Data
@AllArgsConstructor
@NoArgsConstructor

@Alias("student")

public class student {
    private int id;
    private String name;
    private teacher teacher;
}
```
编写teacher实体类  (一个老师对多个学生)
```java
package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * @author by wyl
 */

@Data
@AllArgsConstructor
@NoArgsConstructor

@Alias("teacher")

public class teacher {
    private int id;
    private String name;
    private List<student> students;
}

```
####  多对一
编写Mapper接口
```java
package mapper;

import org.apache.ibatis.annotations.Param;
import pojo.student;

import java.util.List;

/**
 * @author by wyl
 */

public interface studentMapper {

    /**
     * 查询全部学生的信息
     */
    List<student> getStudentList();

    /**
     * 查询学生信息ByID
     */
    student getStudentByID(@Param("id") int id);

}
```
编写XML配置文件
<!--结果 嵌套处理-->
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="mapper.studentMapper">

    <resultMap id="StudentTeacher" type="student">
        <result property="id" column="sid"/>
        <result property="name" column="sname"/>
        
        <association property="teacher" javaType="teacher">
            <!--做映射-->
            <result property="name" column="tname"/>
        </association>
        
    </resultMap>

    <select id="getStudentList" resultMap="StudentTeacher">
        select s.id sid, s.name sname, t.name tname
        from mybatis.student s,
             mybatis.teacher t
        where s.tid = t.id;
    </select>

    <select id="getStudentByID" resultMap="StudentTeacher">
        select s.id sid, s.name sname, t.name tname
        from mybatis.student s,
             mybatis.teacher t
        where s.tid = t.id
          and s.id = #{id};
    </select>
    
</mapper>

```
<!--查询 嵌套处理-->
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="mapper.studentMapper">
    
    <resultMap id="StudentTeacher" type="student">
        <result property="id" column="id"/>
        <result property="name" column="name"/>
        <association property="teacher" javaType="teacher"
                     column="tid" select="getTeacher"/>
    </resultMap>

    <select id="getStudentList" resultMap="StudentTeacher">
        select *
        from mybatis.student;
    </select>

    <select id="getStudentByID" resultMap="StudentTeacher">
        select *
        from mybatis.student
        where id = #{id};
    </select>

    <select id="getTeacher" resultType="teacher">
        select *
        from mybatis.teacher
        where id = #{tid};
    </select>


</mapper>

```
#### 一对多
编写Mapper接口
```java
package mapper;

import org.apache.ibatis.annotations.Param;
import pojo.teacher;

import java.util.List;

/**
 * @author by wyl
 */

public interface teacherMapper {

    /**
     * 查找所有老师
     */
    List<teacher> getTeacherList();

    /**
     * 查询老师信息ByID
     */
    teacher getTeacherByID(@Param("id") int id);
}

```
编写XML配置文件
<!--结果 嵌套处理-->
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="mapper.teacherMapper">
  
    <!--    ofType  JavaBean（或字段）属性的类型和集合存储的类型区分开来  -->
    <resultMap id="TeacherStudent" type="teacher">
        <result property="id" column="tid"/>
        <result property="name" column="tname"/>
        <collection property="students" javaType="ArrayList" ofType="student">
            <result property="id" column="sid"/>
            <result property="name" column="sname"/>
        </collection>
    </resultMap>

    <select id="getTeacherList" resultMap="TeacherStudent">
        select t.id tid, t.name tname, s.id sid, s.name sname
        from mybatis.student s,
             mybatis.teacher t
        where s.tid = t.id;
    </select>

    <select id="getTeacherByID" resultMap="TeacherStudent">
        select t.id tid, t.name tname, s.id sid, s.name sname
        from mybatis.student s,
             mybatis.teacher t
        where s.tid = t.id
          and t.id = #{id};
    </select>

</mapper>
```
<!--查询 嵌套处理-->
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="mapper.teacherMapper">

    <resultMap id="TeacherStudent" type="teacher">
        <result property="id" column="id"/>
        <result property="name" column="name"/>
        <!--javaType指定属性的类型   ofType 用来指定映射到List或者集合中的pojop类型-->
        <collection property="students" javaType="ArrayList" ofType="student"
                    column="tid" select="getStudent"/>
    </resultMap>

    <select id="getTeacherList" resultMap="TeacherStudent">
        select *
        from mybatis.teacher;
    </select>

    <select id="getTeacherByID" resultMap="TeacherStudent">
        select *
        from mybatis.teacher
        where id = #{tid};
    </select>

    <select id="getStudent" resultType="student">
        select *
        from mybatis.student
        where tid = #{id};
    </select>

</mapper>
```
### 自动映射
#### 基本使用(提供了结果映射后，自动映射也能工作)
数据库(下划线)------>java(驼峰命名法)
```xml
 <setting name="mapUnderscoreToCamelCase" value="true"/>
```
实体类POJO
```java
package pojo;

import lombok.*;
import org.apache.ibatis.type.Alias;

/**
 * @author by wyl
 */

@Alias("user")  //对该实体类起别名

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class User {
    private int id;
    private String userName;
    private String pwd;  
}
```
xml配置文件中SQL语句
```xml
<select id="getUserByID" parameterType="int" resultType="user">
        select id,user_name,pwd
        from mybatis.user
        where id = #{id};
</select>
```

#### 三种映射等级

- NONE - 禁用自动映射。仅对手动映射的属性进行映射
- PARTIAL - 对除在内部定义了嵌套结果映射（也就是连接的属性）以外的属性进行映射 默认
- FULL - 自动映射所有属性。

FULL的弊端
```xml
<select id="selectBlog" resultMap="blogResult">
  select
    B.id,
    B.title,
    A.username,
  from Blog B left outer join Author A on B.author_id = A.id
  where B.id = #{id}
</select>

<resultMap id="blogResult" type="Blog">
  <association property="author" resultMap="authorResult"/>
</resultMap>

<resultMap id="authorResult" type="Author">
  <result property="username" column="author_username"/>
</resultMap>
```
_Blog_ 和 _Author_ 均将被自动映射。 _Author_ 有 _id_ 属性，在 ResultSet 中也有 _id_ 的列， Author 的 id 将填入 Blog 的 id

#### 启动和禁用自动映射
结果映射上设置 autoMapping 属性
```xml
<resultMap id="userResultMap" type="User" autoMapping="false">
  <result property="password" column="hashed_password"/>
</resultMap>
```

## sql片段
用来定义可重用的 SQL 代码片段，以便在其它语句中使用，参数可以静态或者动态确定，并且可以在不同的include中定义不同的参数值
### 编写sql片段  
```xml
<sql id="userColumns"> 
  ${alias}.id,${alias}.username,${alias}.password
</sql>
```
### 引用sql片段
```xml
<select id="selectUsers" resultType="map">
  select
    <include refid="userColumns">
      <property name="alias" value="t1"/>
    </include>
    ,
   <include refid="userColumns">
     <property name="alias" value="t2"/>
   </include>
  from some_table t1
    cross join some_table t2
</select>
```
### sql片段嵌套
```xml
<sql id="sometable">
  ${prefix}Table
</sql>

<sql id="someinclude">
  from
    <include refid="${include_target}"/>
</sql>

<select id="select" resultType="map">
  select
    field1, field2, field3
  <include refid="someinclude">
    <property name="prefix" value="Some"/>
    <property name="include_target" value="sometable"/>
  </include>
</select>
```

## 动态sql
避免sql语句拼接问题
### if
```xml
<!--
sql片段抽取公用部分 复用(最好基于单表查询  不要存在where标签)
-->
<sql id="if-title-author">        <!--支持模糊查询-->
     <if test="title != null">and title like concat('%', #{title}, '%')</if>
     <if test="author != null">and author like concat('%', #{author}, '%')</if>
</sql>

<!--
按照条件查询数据   IF   WHERE
支持模糊查询
-->
<select id="getBlogIF" parameterType="map" resultType="Blog">
   select * from mybatis.blog where 1=1
   <include refid="if-title-author"/>   <!--引入sql片段-->
</select>
```
### choose、when、otherwise
```xml
<!--
类似与switch-case
按照条件查询数据     CHOOSE   WHEN   OTHERWISE       - WHERE
支持模糊查询
#类似与switch语句   匹配则退出
-->
<select id="getBlogCHOOSE" parameterType="map" resultType="Blog">
    select * from mybatis.blog where 1=1
         <choose>
            <when test="title!=null">
                 and title like concat('%', #{title}, '%')
            </when>
            <when test="author!=null">
                 and author like concat('%', #{author}, '%')
            </when>
            <otherwise>
                 and views=#{views}
            </otherwise>
         </choose>
</select>
```
### where、trim、set
#### where
```xml
<select id="getBlogIF" parameterType="map" resultType="Blog">
   select * from mybatis.blog
   <where>  
         <include refid="if-title-author"/>   <!--引入sql片段-->
   </where>
</select>
```
#### set
```xml
<!--
按照条件修改数据   SET
-->
<update id="updateBlogSET" parameterType="map">
   update mybatis.blog
   <set>
     <if test="title!=null">title=#{title},</if>
     <if test="author!=null">author=#{author},</if>
   </set>
   where id=#{id}
</update>
```
#### trim
```xml
<trim prefix="WHERE" prefixOverrides="AND |OR ">
  ...sql代码
</trim>
<trim prefix="SET" suffixOverrides=",">
  ...sql代码
</trim>
```
### foreach
```xml
<!--
查询第 1- 2- 3 号记录的博客
Foreach   collection:传入数组或者集合  item:为一项   open   close  separator:分隔符
-->
<select id="getBlogForeach" parameterType="map" resultType="Blog">
   select * from mybatis.blog
   <where>
       <foreach collection="ids" item="id"
              open="(" separator="or" close=")">
           id=#{id}
       </foreach>
   </where>
</select>
```
# 缓存
## 默认缓存
在 SQL 映射文件中配置以下代码
```xml
<cache eviction="FIFO" flushInterval="60000" size="1024" readOnly="true"/>
<!--FIFO缓存  60s刷新一次  最多存储512个引用  返回的对象为只读-->
```
eviction：

- LRU – 最近最少使用：移除最长时间不被使用的对象。 默认
- FIFO – 先进先出：按对象进入缓存的顺序来移除它们。
- SOFT – 软引用：基于垃圾回收器状态和软引用规则移除对象。
- WEAK – 弱引用：更积极地基于垃圾收集器状态和弱引用规则移除对象。

flushInterval：（刷新间隔）属性可以被设置为任意的正整数，设置的值应该是一个以毫秒为单位的合理时间量。 默认情况是不设置，也就是没有刷新间隔，缓存仅仅会在调用语句时刷新。
size（引用数目）：属性可以被设置为任意正整数，要注意欲缓存对象的大小和运行环境中可用的内存资源。默认值是 1024。
readOnly（只读）：属性可以被设置为 true 或 false。只读的缓存会给所有调用者返回缓存对象的相同实例。 因此这些对象不能被修改。这就提供了可观的性能提升。而可读写的缓存会（通过序列化）返回缓存对象的拷贝。 速度上会慢一些，但是更安全，因此默认值是 false。
## 自定义缓存
在SQL 映射文件中配置以下代码
```xml
<cache type="org.mybatis.caches.ehcache.EhcacheCache"/>
```
编写缓存配置文件
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd" updateCheck="false">

    <!--
    diskStore: 缓存路径, ehcache分为内存和磁盘两级, 此属性定义磁盘的缓存位置
    参数:
    user.home - 用户主目录
    user.dir - 用户当前工作目录
    java.io.tmpdir - 默认临时文件路径
    -->

    <!--当二级缓存的对象 超过内存限制时（缓存对象的个数>maxElementsInMemory），存放入的硬盘文件  -->
    <diskStore path="./tempdir/Tmp_Ehcache"/>

    <!--default 默认缓冲策略, 当ehcache找不到定义的缓存时, 则使用这个缓存策略, 这个只能定义一个-->
    <defaultCache
            eternal="false"
            maxElementsInMemory="10000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="1800"
            timeToLiveSeconds="259200"
            memoryStoreEvictionPolicy="LRU"/>

    <cache
            name="cloud_user"
            eternal="false"
            maxElementsInMemory="5000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="1800"
            timeToLiveSeconds="1800"
            memoryStoreEvictionPolicy="LRU"/>

    <!--
       maxElementsInMemory:设置 在内存中缓存 对象的个数
       maxElementsOnDisk：设置 在硬盘中缓存 对象的个数
       eternal：设置缓存是否 永远不过期
       overflowToDisk：当系统宕机的时候是否保存到磁盘上
       maxElementsInMemory的时候，是否转移到硬盘中
       timeToIdleSeconds：当2次访问 超过该值的时候，将缓存对象失效
       timeToLiveSeconds：一个缓存对象 最多存放的时间（生命周期）
       diskExpiryThreadIntervalSeconds：设置每隔多长时间，通过一个线程来清理硬盘中的缓存
       clearOnFlush: 内存数量最大时是否清除
       memoryStoreEvictionPolicy：当超过缓存对象的最大值时，处理的策略；LRU (最少使用)，FIFO (先进先出), LFU (最少访问次数)
     -->
</ehcache>
```
## 缓存
_   用户------>二级缓存------>一级缓存------>数据库_
### 一级缓存
_sqlSession------>(一级缓存)_
* 一级缓存  (默认)  存在于sqlSession的创建与关闭之间  避免与数据库重复连接查询
* 查询完成数据之后，数据存在于当前会话的一级缓存中   会话关闭，数据消失
* 增删改操作会使缓存失效
* sqlSession的clearCache方法清除缓存
```java
    @Test
    public void T1() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        User userByID1 = mapper.getUserByID(1);
        System.out.println(userByID1);

        System.out.println("+++++++++++++++++一+级缓存++++++++++++++++++++++");

        User userByID2 = mapper.getUserByID(1);
        System.out.println(userByID2);

        sqlSession.close();
    }
```
![8ad7c66d44d634a575c9eb4fba144c2.png](https://cdn.nlark.com/yuque/0/2022/png/23219042/1641117666665-768f1d05-2f75-4147-bf07-c60d3161e12b.png#crop=0&crop=0&crop=1&crop=1&height=323&id=udadcf0e7&margin=%5Bobject%20Object%5D&name=8ad7c66d44d634a575c9eb4fba144c2.png&originHeight=323&originWidth=1037&originalType=binary&ratio=1&rotation=0&showTitle=false&size=466080&status=done&style=none&title=&width=1037)
```java
    @Test
    public void T1() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        User userByID1 = mapper.getUserByID(1);
        System.out.println(userByID1);

        mapper.updateByID(mapper.getUserByID(2));
        sqlSession.clearCache();

        System.out.println("+++++++++++++++++一+级缓存++++++++++++++++++++++");

        User userByID2 = mapper.getUserByID(1);
        System.out.println(userByID2);

        sqlSession.close();
    }
```
![47c3f520bbaff93992a5c15ddc1b330.png](https://cdn.nlark.com/yuque/0/2022/png/23219042/1641117688598-471560aa-292b-4b1f-baa3-3efc5a265f8a.png#crop=0&crop=0&crop=1&crop=1&height=558&id=uaeae0d19&margin=%5Bobject%20Object%5D&name=47c3f520bbaff93992a5c15ddc1b330.png&originHeight=558&originWidth=1031&originalType=binary&ratio=1&rotation=0&showTitle=false&size=764620&status=done&style=none&title=&width=1031)
### 二级缓存
_Mapper------>(二级缓存)_
* 二级缓存  全局缓存 基于namespace级别 (同一个mapper下有效)
* 查询完成数据之后，数据存在于当前会话的一级缓存中 ------>转存 会话关闭，保存到二级缓存中
* 增删改操作会使缓存失效    (避免脏读) 
* sqlSession的clearCache方法清除缓存
```java
    @Test
    public void T2() {
        SqlSession sqlSession1 = MybatisUtils.getSqlSession();
        UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
        User userByID1 = mapper1.getUserByID(1);
        System.out.println(userByID1);
        sqlSession1.close();

        SqlSession sqlSession2 = MybatisUtils.getSqlSession();
        UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
        User userByID2 = mapper2.getUserByID(1);
        System.out.println(userByID2);
        sqlSession2.close();
    }
```
![1d74be35081a7c61da8226654020cf0.png](https://cdn.nlark.com/yuque/0/2022/png/23219042/1641117905252-56273159-b34c-4ed4-8e9f-795e730cd3eb.png#crop=0&crop=0&crop=1&crop=1&height=300&id=ua526d177&margin=%5Bobject%20Object%5D&name=1d74be35081a7c61da8226654020cf0.png&originHeight=300&originWidth=1035&originalType=binary&ratio=1&rotation=0&showTitle=false&size=430448&status=done&style=none&title=&width=1035)
```java
    @Test
    public void T2() {
        SqlSession sqlSession1 = MybatisUtils.getSqlSession();
        UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
        User userByID1 = mapper1.getUserByID(1);
        System.out.println(userByID1);
        mapper1.updateByID(mapper1.getUserByID(1));
        sqlSession1.clearCache();
        sqlSession1.close();

        SqlSession sqlSession2 = MybatisUtils.getSqlSession();
        UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
        User userByID2 = mapper2.getUserByID(1);
        System.out.println(userByID2);
        sqlSession2.close();
    }
```
![45951e7ef5d92d9588f3b631233ffe0.png](https://cdn.nlark.com/yuque/0/2022/png/23219042/1641117957659-4197f39b-c6f7-4ac8-927c-6c0cc6d1e43b.png#crop=0&crop=0&crop=1&crop=1&height=611&id=u8b24d17b&margin=%5Bobject%20Object%5D&name=45951e7ef5d92d9588f3b631233ffe0.png&originHeight=611&originWidth=1048&originalType=binary&ratio=1&rotation=0&showTitle=false&size=856681&status=done&style=none&title=&width=1048)
# 日志
## 简介
Mybatis 通过使用内置的日志工厂提供日志功能。内置日志工厂将会把日志工作委托给下面的实现之一：

- SLF4J
- Apache Commons Logging
- Log4j 2
- Log4j
- JDK logging

MyBatis 内置日志工厂会基于运行时检测信息选择日志委托实现。它会（按上面罗列的顺序）使用第一个查找到的实现。当没有找到这些实现时，将会禁用日志功能。
## Log4j
### 添加Log4j-jar包
```xml
        <!-- log4日志工厂jar包-->
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>
```
### 添加setting
```xml
        <!--STDOUT_LOGGING标准日志输出-->
        <!--<setting name="logImpl" value="STDOUT_LOGGING"/>-->
        <!--LOG4J-->
        <setting name="logImpl" value="LOG4J"/>
```
### 配置Log4j
```properties
#将等级为DEBUG的日志信息输出到console和file这两个目的地，console和file的定义在下面的代码
log4j.rootLogger=DEBUG,console,file

#控制台输出的相关设置
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.out
log4j.appender.console.Threshold=DEBUG
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=[%c]-%m%n

#文件输出的相关设置
log4j.appender.file = org.apache.log4j.RollingFileAppender
log4j.appender.file.File=./log/log.log
log4j.appender.fileAppender.Append = false  #文件追加
log4j.appender.file.MaxFileSize=10mb
log4j.appender.file.Threshold=DEBUG
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=[%p][%d{yy-MM-dd}][%c]%m%n

#日志输出级别
log4j.logger.org.mybatis=DEBUG
log4j.logger.org.apache=DEBUG
log4j.logger.java.sql=DEBUG
log4j.logger.java.sql.Connection=DEBUG
log4j.logger.java.sql.Statement=DEBUG
log4j.logger.java.sql.ResultSet=DEBUG
log4j.logger.java.sql.PreparedStatement=DEBUG
```




