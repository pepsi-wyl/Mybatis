/**
 * @author by wyl
 * @date 2021/9/19.10点35分
 */

import dao.BlogMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import pojo.Blog;
import utils.IDUtils;
import utils.MybatisUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 测试类
 */

public class T {

    /**
     * 插入Blog
     */
    @Test
    public void addBlog_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        mapper.addBlog(new Blog(IDUtils.getID(), "Mybatis如此简单", "wyl", new Date(), 9999));
        sqlSession.commit();
        mapper.addBlog(new Blog(IDUtils.getID(), "java如此简单", "wyl", new Date(), 9999));
        sqlSession.commit();
        mapper.addBlog(new Blog(IDUtils.getID(), "C如此简单", "wyl", new Date(), 9999));
        sqlSession.commit();
        mapper.addBlog(new Blog(IDUtils.getID(), "C++如此简单", "wyl", new Date(), 9999));
        sqlSession.commit();
        sqlSession.close();
    }

    /**
     * 查询Blog  模糊查询  IF
     */
    @Test
    public void getBlogIF_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", "java");
        map.put("author", "w");
        List<Blog> blogList = mapper.getBlogIF(map);
        blogList.forEach((value) -> System.out.println(value));
        sqlSession.close();
    }


    /**
     * 查询Blog  模糊查询  CHOOSE
     */
    @Test
    public void getBlogChoose_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", "java");
        map.put("author", "w");
        map.put("views", 9999);
        List<Blog> blogList = mapper.getBlogCHOOSE(map);
        blogList.forEach((value) -> System.out.println(value));
        sqlSession.close();
    }


    /**
     * 修改blog  SET
     */
    @Test
    public void updateBlogSET_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", "java");
        map.put("author", "w");
        map.put("id", "1");
        int i = mapper.updateBlogSET(map);
        if (i > 0) {
            sqlSession.commit();
            System.out.println("success");
        }
        sqlSession.close();
    }

    /**
     * 查询第 1- 2- 3 号记录的博客
     * Foreach
     */
    @Test
    public void getBlogForeach_T() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<Object> list = new ArrayList<>();
        list.add("1");
        list.add("3");
        map.put("ids",list);
        List<Blog> blogList = mapper.getBlogForeach(map);
        blogList.forEach((value)-> System.out.println(value));
        sqlSession.close();
    }


}
