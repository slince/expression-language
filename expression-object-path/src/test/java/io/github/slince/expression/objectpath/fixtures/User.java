package io.github.slince.expression.objectpath.fixtures;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class User {

    private String name;

    private Integer age;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    private final List<Book> books;

    private final String[] tags = {"A", null, "C"};

    private List<String> nickNames;
    public String sayHello(String prefix, String suffix){
        return prefix + name + suffix;
    }
}
