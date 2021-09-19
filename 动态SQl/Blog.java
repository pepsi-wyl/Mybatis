package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * @author by wyl
 * @date 2021/9/19.10点21分
 */

/**
 * 博客实体类
 */

@Data
@NoArgsConstructor
@AllArgsConstructor

@Alias("blog")
public class Blog {


    private String id;
    private String title;
    private String author;
    private Date createTime;     //属性名称和字段名称不一致
    private int views;


}
