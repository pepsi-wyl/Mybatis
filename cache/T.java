/**
 * @author by wyl
 * @date 2021/9/19.20点42分
 */

import dao.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import pojo.User;
import utils.MybatisUtils;

/**
 * 测试类
 */

public class T {


    /**
     * 一级缓存  (默认)  存在于sqlSession的创建与关闭之间  避免与数据库重复连接查询
     * 查询完成数据之后，数据存在于当前会话的一级缓存中   会话关闭，数据消失
     * 增删改操作会使缓存失效
     * sqlSession的clearCache方法清除缓存
     */
    @Test
    public void T1() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User userByID1 = mapper.getUserByID(1);
        System.out.println(userByID1);
//        mapper.updateByID(mapper.getUserByID(2));
//        sqlSession.clearCache();
        System.out.println("+++++++++++++++++一级缓存+++++++++++++++++++++++");
        User userByID2 = mapper.getUserByID(1);
        System.out.println(userByID2);
        sqlSession.close();
    }


    /**
     * 二级缓存  全局缓存 基于namespace级别 (同一个mapper下有效)
     * 查询完成数据之后，数据存在于当前会话的一级缓存中 ------>转存 会话关闭，保存到二级缓存中
     * 增删改操作会使缓存失效
     * sqlSession的clearCache方法清除缓存
     */
    @Test
    public void T2() {
        SqlSession sqlSession1 = MybatisUtils.getSqlSession();
        UserMapper mapper1 = sqlSession1.getMapper(UserMapper.class);
        User userByID1 = mapper1.getUserByID(1);
        System.out.println(userByID1);
//        mapper1.updateByID(mapper1.getUserByID(2));
//        sqlSession1.clearCache();
        sqlSession1.close();

        SqlSession sqlSession2 = MybatisUtils.getSqlSession();
        UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
        User userByID2 = mapper2.getUserByID(1);
        System.out.println(userByID2);
        sqlSession2.close();
    }


    /**
     * sqlSession------>(一级缓存)  Mapper------>(二级缓存)
     * 用户------>二级缓存------>一级缓存------>数据库
     */


}
