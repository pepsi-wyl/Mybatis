import dao.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import pojo.User;
import utils.MybatisUtils;

import java.util.List;

/**
 * Mybatis_T
 * @author by wyl
 * @date 2021/9/14 10点46分
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
     */
    @Test
    public void queryByName_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = userMapper.getUserByName("武");
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
        userMapper.modifyUserByID(new User(5, "武扬", "888888"));
        sqlSession.commit();
        sqlSession.close();
    }




}
