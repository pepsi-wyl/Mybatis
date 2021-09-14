import dao.UserMapper;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.junit.Test;
import pojo.User;
import utils.MybatisUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Mybatis_T
 *
 * @author by wyl
 * @date 2021/9/6.
 */

public class T {

    private static Logger logger = Logger.getLogger(T.class);

    @Test
    public void query_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        logger.info(sqlSession);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = userMapper.getUserList();
        userList.forEach((v) -> System.out.println(v));
        sqlSession.close();
    }

    @Test
    public void queryLimit_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("startIndex", 0);
        map.put("pageSize", 2);
        List<User> userListLimit = mapper.getUserListLimit(map);
        userListLimit.forEach((v) -> System.out.println(v));
        sqlSession.close();
    }

    @Test                                     //过气
    public void queryRowBounds_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        List<User> userListRowBounds = sqlSession.selectList("dao.UserMapper.getUserListRowBounds",null,new RowBounds(1,2));
        userListRowBounds.forEach((v) -> System.out.println(v));
        sqlSession.close();
    }

}
