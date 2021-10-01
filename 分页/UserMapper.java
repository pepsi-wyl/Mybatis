

import java.util.List;
import java.util.Map;

/**
 * UserMapper 相当于UserDao接口  定义一些接口方法
 *
 * @author by wyl
 * @date 2021/9/9.11点02分
 */

public interface UserMapper {

    /**
     * 分页得到全部用户集合
     */
    List<User> getUserListLimit(Map<String, Object> map);


    /**
     * 得到全部用户集合
     */
    List<User> getUserListRowBounds();
  
}



