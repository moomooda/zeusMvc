package group.zeus.web;

/**
 * @Author: maodazhan
 * @Date: 2020/10/21 20:26
 */
public class TestPeople {

    private String name;
    private Integer age;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "TestPeople{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
