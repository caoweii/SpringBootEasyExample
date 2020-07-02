package com.zhou.mapper;

import com.zhou.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
/*  雇员的操作方法声明 */
@Mapper
@Repository
public interface EmployeeMapper {
    /*获取所有雇员信息*/
    List<Employee> getEmployeeList();

    /*添加一个雇员*/
    int addEmployee(Employee e);

    /*通过id获取雇员信息*/
    Employee getEmployeeById(String id);

    /*根据传入的整个雇员获取雇员信息，再通过雇员id更新雇员信息*/
    int updateEmployee(Employee e);

    /*根据id删除雇员*/
    int deleteEmployeeById(int id);
}
