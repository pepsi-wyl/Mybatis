package dao;

import org.apache.ibatis.annotations.Param;
import pojo.student;

import java.util.List;

/**
 * @author by wyl
 * @date 2021/9/14.16点43分
 */

public interface studentMapper {

    /**
     * 查询全部学生的信息
     */
    List<student> getStudentList();

    /**
     * 查询学生信息ByID
     */
    student getStudentByID(@Param("id") int id);

}
