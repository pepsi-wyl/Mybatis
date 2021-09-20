package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * @author by wyl
 * @date 2021/9/19.20点42分
 */

@Alias("user")

@Data
@AllArgsConstructor
@NoArgsConstructor

public class User implements Serializable {

    private int id;
    private String name;
    private String pwd;

}
