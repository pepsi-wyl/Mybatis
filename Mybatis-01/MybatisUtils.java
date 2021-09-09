
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

/**
 * MybatisUtils
 * 实例化 SqlSessionFactory  和 SqlSession
 *
 * @author by wyl
 * @date 2021/9/6.21点32分
 */
public class MybatisUtils {

    private static String resource = "mybatis-config.xml";             //mybatis --- xml配置文件地址
    private static SqlSessionFactory sqlSessionFactory = null;

    /**
     * 从XML中构建SqlSessionFactory对象
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
        return sqlSessionFactory.openSession();
    }

}
