import dao.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import pojo.User;
import utils.MybatisUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mybatis_T
 * @author by wyl
 * @date 2021/9/6.
 */
public class T {

    @Test
    public void query_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = userMapper.getUserList();
        userList.forEach((v) -> System.out.println(v));
        sqlSession.close();
    }

    @Test
    public void queryByID_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User userByID = userMapper.getUserByID(1);
        System.out.println(userByID);
        sqlSession.close();
    }

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
        sqlSession.close();
    }

    @Test
    public void addUser_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        userMapper.addUser(new User(5, "武扬岚", "8888888"));
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void delUser_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        userMapper.deleteUserByID(5);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void modifyUser_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        userMapper.modifyUserByID(new User(4, "武扬岚", "888888"));
        sqlSession.commit();
        sqlSession.close();
    }

    /**
     * 利用map储存数据   可以避免传入参数过多
     */
    @Test
    public void modifyPwd_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        Map<String, Object> map = new HashMap<>();
        map.put("pwd","999999");
        map.put("id",3);
        userMapper.modifyPwdByID(map);

        sqlSession.commit();
        sqlSession.close();
    }


}
