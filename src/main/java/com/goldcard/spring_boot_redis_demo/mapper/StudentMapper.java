package com.goldcard.spring_boot_redis_demo.mapper;

import com.goldcard.spring_boot_redis_demo.pojo.Student;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface StudentMapper {
    Student getStudent(Long id);

    int insertStudent(Student student);

    int updateStudent(Student student);

    //查询学生信息，指定Mybatis的参数名称
    List<Student> findStudents(@Param("studentName")String studentName, @Param("note")String note);

    int deleteStudent(Long id);
}
