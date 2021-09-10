


import java.util.List;

/**
 * UserMapper 相当于UserDao接口  定义一些接口方法
 *
 * @author by wyl
 * @date 2021/9/6.22点08分
 */

public interface UserMapper {

    /**
     * 得到全部用户集合
     */
    List<User> getUserList();

}
