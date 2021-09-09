package dao;

import pojo.User;

import java.util.List;
import java.util.Map;

/**
 * UserMapper 相当于UserDao接口  定义一些接口方法
 *
 * @author by wyl
 * @date 2021/9/6.21点50分
 */

public interface UserMapper {

    /**
     * 得到全部用户集合
     */
//    @Select("select * from user;")
    List<User> getUserList();

    /**
     * 查询用户ByID
     */
//    @Select("select * from user where id=#{id};")
    User getUserByID(int id);

    /**
     * 添加新用户
     */
//    @Insert("insert into user value (#{id},#{name},#{pwd});")
    int addUser(User user);

    /**
     * 删除用户ByID
     */
//    @Delete("delete from mybatis.user where id = #{id};")
    int deleteUserByID(int id);

    /**
     * 修改用户ByID
     */
//    @Update("update mybatis.user set name=#{name}, pwd=#{pwd} where id = #{id};")
    int modifyUserByID(User user);

    /**
     * 修改密码ByID------>万能的Map
     * 利用map存储数据   参数名不需要域数据库字段名称对应
     */
    int modifyPwdByID(Map<String, Object> map);

    /**
     * 模糊查询
     * 1.传入参数时候就进行拼串        "%"+"name"+"%"
     * 2.在xml中的sql语句中进行拼串   name like concat('%', #{name}, '%')
     * 查询ByName
     */
    List<User> getUserByName(String name);

}
