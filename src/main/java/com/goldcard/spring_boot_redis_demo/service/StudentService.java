package com.goldcard.spring_boot_redis_demo.service;

import com.goldcard.spring_boot_redis_demo.pojo.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentService {
    Student getStudent(Long id);

    Student insertStudent(Student student);

    Student updateStudent(Long id,String name);

    //查询学生信息，指定Mybatis的参数名称
    List<Student> findStudents(String studentName,String note);

    int deleteStudent(Long id);
}
