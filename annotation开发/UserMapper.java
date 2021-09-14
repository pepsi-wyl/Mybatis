package dao;

import org.apache.ibatis.annotations.*;
import pojo.User;

import java.util.List;

/**
 * UserMapper 相当于UserDao接口
 *
 * @author by wyl
 * @date 2021/9/14 10点45分
 */

/**

   适合简单的sql语句
    
 * 注解编写SQL语句
 * @Param 设置属性名称  在sql语句中引用
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
