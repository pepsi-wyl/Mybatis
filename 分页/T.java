



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
 * @date 2021/9/9.11点02分
 */

public class T {


    @Test
    public void queryLimit_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageSize", 2);              
        map.put("startIndex", 4*(int)map.get("pageSize"));       
        List<User> userListLimit = mapper.getUserListLimit(map);
        userListLimit.forEach((v) -> System.out.println(v));
        sqlSession.close();
    }

    @Test
    public void queryRowBounds_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        List<User> userListRowBounds = sqlSession.selectList("dao.UserMapper.getUserListRowBounds",null,new RowBounds(1,2));
        userListRowBounds.forEach((v) -> System.out.println(v));
        sqlSession.close();
    }

}
