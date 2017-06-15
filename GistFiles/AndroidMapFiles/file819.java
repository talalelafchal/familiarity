package po;

import utils.sqllite.AutoGen;
import utils.sqllite.Key;
import utils.sqllite.Table;

/**
 * Description:
 * <p/>
 * Date: 14-2-3
 * Author: Administrator
 */
@Table
public class PersonPO {
    @Key
    @AutoGen
    private Integer id;
    private String name;
    private Integer age;
    private Float score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }
}
