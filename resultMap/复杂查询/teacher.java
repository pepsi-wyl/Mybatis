package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * @author by wyl
 * @date 2021/9/14.16点40分
 */

@Data
@AllArgsConstructor
@NoArgsConstructor

@Alias("teacher")

public class teacher {

    private int id;
    private String name;
    private List<student> students;

}
