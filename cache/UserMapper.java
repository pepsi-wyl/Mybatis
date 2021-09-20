package dao;

import org.apache.ibatis.annotations.Param;
import pojo.User;

/**
 * @author by wyl
 * @date 2021/9/19.20点43分
 */
public interface UserMapper {

    /**
     * 按照ID查找User
     */
    User getUserByID(@Param("id") int id);

    /**
     * 按照ID修改User
     */
    int updateByID(User user);

}
