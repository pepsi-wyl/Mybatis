package dao;

/**
 * @author by wyl
 * @date 2021/9/19.
 */

import pojo.Blog;

import java.util.HashMap;
import java.util.List;

/**
 * blog功能接口
 */

public interface BlogMapper {

    /**
     * 插入数据
     */
    int addBlog(Blog blog);

    /**
     * 按照条件查询数据
     * IF  WHERE
     */
    List<Blog> getBlogIF(HashMap<String, Object> map);

    /**
     * 按照条件查询数据
     * choose  (when otherwise)  WHERE
     */
    List<Blog> getBlogCHOOSE(HashMap<String, Object> map);

    /**
     * 按照条件修改数据
     * SET
     */
    int updateBlogSET(HashMap<String, Object> map);


    /**
     * 查询第 1- 2- 3 号记录的博客
     * Foreach
     */
    List<Blog> getBlogForeach(HashMap<String, Object> map);



}
