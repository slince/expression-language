package io.github.slince.expression.fixtures;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of = {"name"})
public class ClassRoom{

    public String name;

    @Setter
    @Getter
    private String description;

    @Getter
    private final List<Group> groups = new ArrayList<>();

    @Getter
    private final List<Student> students = new ArrayList<>();

    public String hello(String prop, Integer num){
        return "A";
    }

    public String hello(String prop, String num){
        return "B";
    }

    public String getName(){
        return "GOOD";
    }

    public int getStudentNum(){
        return students.size();
    }

    public void getSize(){

    }
}