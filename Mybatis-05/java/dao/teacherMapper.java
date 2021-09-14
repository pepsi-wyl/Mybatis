package dao;

import org.apache.ibatis.annotations.Param;
import pojo.teacher;

import java.util.List;

/**
 * @author by wyl
 * @date 2021/9/14.16点43分
 */

public interface teacherMapper {

    /**
     * 查找所有老师
     */
    List<teacher> getTeacherList();

    /**
     * 查询老师信息ByID
     */
    teacher getTeacherByID(@Param("id") int id);


}
