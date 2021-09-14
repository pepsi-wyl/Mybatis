import dao.studentMapper;
import dao.teacherMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import pojo.student;
import pojo.teacher;
import utils.MybatisUtils;

import java.util.List;

/**
 * @author by wyl
 * @date 2021/9/14.16点44分
 */

public class T {

    @Test
    public void getStudentList() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        studentMapper mapper = sqlSession.getMapper(studentMapper.class);
        List<student> studentList = mapper.getStudentList();
        studentList.forEach((v) -> System.out.println(v));
        sqlSession.close();
    }

    @Test
    public void getStudentByID() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        studentMapper mapper = sqlSession.getMapper(studentMapper.class);
        student studentByID = mapper.getStudentByID(1);
        System.out.println(studentByID);
        sqlSession.close();
    }




    @Test
    public void getTeacherList() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        teacherMapper mapper = sqlSession.getMapper(teacherMapper.class);
        List<teacher> teacherList = mapper.getTeacherList();
        teacherList.forEach((v) -> System.out.println(v));
        sqlSession.close();
    }

    @Test
    public void getTeacherByID() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        teacherMapper mapper = sqlSession.getMapper(teacherMapper.class);
        teacher teacherByID = mapper.getTeacherByID(1);
        System.out.println(teacherByID);
        sqlSession.close();
    }


}
