package com.goldcard.spring_boot_redis_demo.pojo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;
@Alias("stu")
public class Student implements Serializable {
    private Long id;

    private String studentName;

    private String note;

    public Student() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
