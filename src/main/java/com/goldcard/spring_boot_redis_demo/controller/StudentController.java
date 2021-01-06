package com.goldcard.spring_boot_redis_demo.controller;

import com.goldcard.spring_boot_redis_demo.pojo.Student;
import com.goldcard.spring_boot_redis_demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stu")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @RequestMapping("/getStu")
    @ResponseBody
    public Student getStudent(Long id){
        return studentService.getStudent(id);
    }

    @RequestMapping("/insertStu")
    @ResponseBody
    public Student insertStudent(String name,String note){
        Student student = new Student();
        student.setStudentName(name);
        student.setNote(note);
        return studentService.insertStudent(student);
    }

    @RequestMapping("/findStu")
    @ResponseBody
    public List<Student> findStudents(String name,String note){
        return studentService.findStudents(name,note);
    }

    @RequestMapping("/updateStu")
    @ResponseBody
    public Map<String,Object> updateStudentName(Long id,String name){
        Student student = studentService.updateStudent(id,name);
        boolean flag = student != null;
        String message = flag?"更新成功":"更新失败";
        return resultMap(flag,message);
    }

    @RequestMapping("/deleteStu")
    @ResponseBody
    public Map<String,Object> deleteStudent(Long id){
        int result = studentService.deleteStudent(id);
        boolean flag = result == 1;
        String message = flag?"删除成功":"删除失败";
        return resultMap(flag,message);
    }

    private Map<String,Object> resultMap(boolean success,String message){
        Map<String,Object> result = new HashMap<>();
        result.put("success",success);
        result.put("meaasge",message);
        return result;
    }
}
