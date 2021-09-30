
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

/**
 * MybatisUtils
 * 实例化 SqlSessionFactory
 * 实例化 SqlSession
 * 关闭   SqlSession
 *
 * @author by wyl
 * @date 2021/9/6.21点32分
 */
public class MybatisUtils {

    /**
     * 每个线程对其进行访问的时候访问的都是线程自己的变量
     */
    private static final ThreadLocal<SqlSession> threadLocal = new InheritableThreadLocal<>();

    private static final String resource = "mybatis-config.xml";             //mybatis --- xml配置文件地址
    private static SqlSessionFactory sqlSessionFactory = null;

    /**
     * 禁止外界通过new方法创建
     */
    private MybatisUtils() {
    }


    /**
     * 从XML中构建SqlSessionFactory对象   只需构建一次
     */
    static {
        try {
            //SqlSessionFactory 的实例可以通过 SqlSessionFactoryBuilder.build方法从xml配置文件中获得
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream(resource));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取SqlSession对象实例
     * SqlSession 提供了在数据库执行 SQL 命令所需的所有方法
     */
    public static SqlSession getSqlSession() {
        SqlSession sqlSession = threadLocal.get();                 //从当前线程中获取SqlSession对象
        if (sqlSession == null) {
            sqlSession = sqlSessionFactory.openSession();          //创建SqlSession对象
            threadLocal.set(sqlSession);                           //将SqlSession对象与当前线程绑定到一起
        }
        return sqlSession;
    }

    /**
     * 关闭SqlSession对象实例与当前线程分开
     */
    public static void closeSqlSession() {
        SqlSession sqlSession = threadLocal.get();                  //从当前线程中获取SqlSession对象
        if (sqlSession != null) {
            sqlSession.close();                                     //关闭SqlSession对象
            threadLocal.remove();                                   //将SqlSession对象与当前线程绑定到一起
        }
    }

}
