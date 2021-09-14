package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

/**
 * @author by wyl
 * @date 2021/9/14.16点42分
 */

@Data
@AllArgsConstructor
@NoArgsConstructor

@Alias("student")

public class student {

    private int id;
    private String name;
    private teacher teacher;

}
