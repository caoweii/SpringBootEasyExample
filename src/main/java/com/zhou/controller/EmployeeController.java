package com.zhou.controller;

import com.zhou.mapper.DepartmentMapper;
import com.zhou.mapper.EmployeeMapper;
import com.zhou.pojo.Department;
import com.zhou.pojo.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EmployeeController {
    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    DepartmentMapper departmentMapper;

    //显示所有雇员
    @GetMapping("/employees")
    public String getList(Model model){
        List<Employee> employees = employeeMapper.getEmployeeList();
        model.addAttribute("employees",employees);
        return "employees/list";
    }

    //添加雇员
    @GetMapping("/add")
    public String addPage(Model model){
        //添加所有部门的信息给到下拉列表
        System.out.println("访问添加页面");
        List<Department> departments = departmentMapper.getDepartmentList();
        model.addAttribute("departments",departments);
        return "employees/add";
    }


    /*确认添加操作*/
    @PostMapping("/submitAdd")
    public String submitAdd(Employee employee){
        System.out.println("提交了数据：");
        System.out.println(employee);
        employeeMapper.addEmployee(employee);
        return "redirect:/employees";
    }

    /*进入添加页面*/
    @GetMapping("/employees/update/{id}")
    public String updateEmployee(@PathVariable("id") String id,Model model){
        Employee employee = employeeMapper.getEmployeeById(id);
        List<Department> departments = departmentMapper.getDepartmentList();
        model.addAttribute("departments",departments);
        model.addAttribute("employee",employee);
        return "employees/update";
    }

    /*确认更新*/
    @PostMapping("/submitUpdate/{id}")
    public String submitUpdate(@PathVariable("id") String id, Employee employee,Model model){
        int temp = employeeMapper.updateEmployee(employee);
        employee.setId(Integer.valueOf(id));
        System.out.println("temp:"+temp);
        List<Employee> employees = employeeMapper.getEmployeeList();
        model.addAttribute("employees",employees);
        return "employees/list";
    }

    /*根据id删除雇员*/
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable("id") String id,Model model){
        employeeMapper.deleteEmployeeById(Integer.valueOf(id));
        List<Employee> employees = employeeMapper.getEmployeeList();
        model.addAttribute("employees",employees);
        return "employees/list";
    }
/*    @GetMapping("/get")
    @ResponseBody
    public Employee getById(){
        Employee employee = employeeMapper.getEmployeeById("3");
        return employee;
    }*/

}
