## 开发技术

- Spring Boot2.3.1 
- Mysql 
- Mybatis 
- thymeleaf

## 环境配置

#### pom.xml所需依赖

```xml
<dependencies>
   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-thymeleaf</artifactId>
   </dependency>
   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   <dependency>
      <groupId>org.mybatis.spring.boot</groupId>
      <artifactId>mybatis-spring-boot-starter</artifactId>
      <version>2.1.3</version>
   </dependency>

   <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <scope>runtime</scope>
   </dependency>
   <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
   </dependency>
   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
      <exclusions>
         <exclusion>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
         </exclusion>
      </exclusions>
   </dependency>
</dependencies>
```



#### application.yml配置

```yml
#数据库配置（注意时区serverTimezone不可省略）
#关闭thymeleaf缓存（否则在更改页面代码时可能不会进行重新编译）
spring:
  datasource:
    username: root
    password: root
    url: jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
  thymeleaf:
    cache: false
#Mybatis配置（设置别名包，使得包内所有实体类在*mapper.xml中的类型指定中省略包名）
#mapper-locations设置xml文件路径，在此处告诉系统扫描rescourses目录下的mybatis/mapper的*mapper。xml文件
#所有的*mapper.xml文件都在这里编辑
mybatis:
  type-aliases-package: com.zhou.pojo
  mapper-locations: classpath:mybatis/mapper/*.xml
```

#### java代码结构

> com.zhou.config包下定义了拦截器以及视图解析器
>
> com.zhou.controller下定义了前端路由的各种接口，实现请求的转发，填充页面，以及页面跳转
>
> com.zhou.mapper下定义了数据库表的操作方法，配合xml文件在mybatis运行规则下编写
>
> com.zhou.pojo定义的是的项目纯实体对象,通过lombok插件简化代码规模
>
> MainAoolication作为Spring boot的main启动程序

![image-20200702101024988](C:\Users\XiaoChuanye\AppData\Roaming\Typora\typora-user-images\image-20200702101024988.png)

#### mybaits编写流程

> mybatis是在jdbc上的一层封装，在Spring Boot项目中,需要定义对应pojo实体的，包含数据库操作方法的声明的Mapper类以及包含具体实现sql语言的*mapper.xml文件即可

#### Employee.mapper文件



![image-20200702102500004](C:\Users\XiaoChuanye\AppData\Roaming\Typora\typora-user-images\image-20200702102500004.png)

> @Mapper表明这是一个Mybtis的Mapper类，@Repository将其注册为Spring的一个bean，这样就可以通过@Autowired注入直接使用



#### *Mapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zhou.mapper.EmployeeMapper">
    <!--显示全体雇员-->
         <!--根据id查找雇员-->
     <select id="getEmployeeById" parameterType="String" resultType="Employee">
        SELECT * from employee where id=#{id}
     </select>

    <select id="getEmployeeList" resultMap="EmployeeDepartment">
        SELECT e.id eid,e.last_name elast_name,e.email eemail,e.birth ebirth,e.gender egender,e.departmentId did,d.departmentName dname
        FROM
        employee e,department d
        WHERE e.departmentId = d.id
    </select>
    <resultMap id="EmployeeDepartment" type="com.zhou.pojo.Employee">
        <result property="id" column="eid"></result>
        <result property="last_name" column="elast_name"></result>
        <result property="email" column="eemail"></result>
        <result property="birth" column="ebirth"></result>
        <result property="gender" column="egender"></result>
        <association property="department" javaType="com.zhou.pojo.Department">
            <result property="id" column="did"></result>
            <result property="departmentName" column="dname"></result>
        </association>
    </resultMap>
    <!--增加一个雇员-->
     <insert id="addEmployee" parameterType="Employee">
       insert into employee (id,last_name,email,birth,gender,departmentId) values (#{id},#{last_name},#{email},#{birth},#{gender},#{department.id})
     </insert>

     <!--更新雇员-->
     <update id="updateEmployee" parameterType="Employee">
        update employee set last_name=#{last_name},email=#{email},birth=#{birth},gender=#{gender},departmentId=#{department.id}
        where id=#{id}
     </update>

     <!--删除雇员-->
     <delete id="deleteEmployeeById" parameterType="int">
        delete from employee where id=#{id}
     </delete>
</mapper>
```

> 注意事项:

1. namespace为完整包名下的对应mapper

2. select等操作标签的 id属性必不可少，且与mapper下的声明方法一一对应

3. 在标签中的复杂类型都是pojo中对应的类型，mybatis在解析其中属性变量时要能找到对应的get，set方法（比如以下的department.id就是在Employee类中的department属性，通过Employee无法直接访问

   ```xml
    <insert id="addEmployee" parameterType="Employee">
    		insert into employee (id,last_name,email,birth,gender,departmentId) values (#{id},#{last_name},
    		#{email},#{birth},#{gender},#{department.id})
    </insert>
   
   ```

#### Mybatis复杂查询

###### 多对一查询（例多个雇员属于一个部门）



```xml
<select id="getEmployeeList" resultMap="EmployeeDepartment">
    SELECT e.id eid , e.last_name elast_name , e.email eemail , e.birth ebirth , e.gender egender,
    e.departmentId did , d.departmentName dname
    FROM
    employee e,department d
    WHERE e.departmentId = d.id
</select>
<!--指定type为要返回的类型-->
<resultMap id="EmployeeDepartment" type="com.zhou.pojo.Employee">
    <result property="id" column="eid"></result>
    <result property="last_name" column="elast_name"></result>
    <result property="email" column="eemail"></result>
    <result property="birth" column="ebirth"></result>
    <result property="gender" column="egender"></result>
    <!--javaType表示的是包含上层返回类中包含的类型-->
    <association property="department" javaType="com.zhou.pojo.Department">
        <result property="id" column="did"></result>
        <result property="departmentName" column="dname"></result>
    </association>
</resultMap>
```

> 从数据返回的字段并没办法自动映射到该返回对象Employee中，因为该对象的属性包含了一个复杂对象类型Department，因此通过association设置返回的两个字段手动映射到复杂类型department中，这样才能正确填充返回类型Employee

###### 一对多查询(例如一个老板手下一堆雇员)

```xml
<select id="getBoss" resultMap="BossWorker">
	select b.id bid,b.name bname,w.id wid, w.name wname,w.tid wtid
	from teacher b,student w
	where b.id=w.tid and b.id=#{id}
</select>
<resultMap id="BossWorker" type="pojo.Boss">
    <result property="id" column="bid"></result>
    <result property="name" column="bname"></result>
    <!--collection中的property指定返回的泛型属性名，ofType指定泛型的类型-->
    <collection property="workerList" ofType="pojo.Worker">
        <result property="id" column="wid"></result>
        <result property="name" column="wname"></result>
        <result property="tid" column="wtid"></result>
     </collection>
 </resultMap>

```

> 这里实现的是通过一个老板ID返回员工列表，显然返回的列表无法直接填充到一个Boss对象中，因此要针对老板类包含的员工进行一一填充映射，提供的标签名为collection



#### 设置拦截器

> 要实现自定义拦截器，要先让类实现HandlerIntercepter接口，并在其中实现preHandle接口

```java
public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object loginUser = request.getSession().getAttribute("loginUser");
        if(loginUser==null){
            request.setAttribute("msg","please login first");
            request.getRequestDispatcher("/index.html").forward(request,response);
            return false;
        }else{
            return true;
        }
    }
}
```

> 拦截器在访问页面之前生效，可以通过验证逻辑来决定：允许通过则返回true，不符合条件则重定向到其他页面。

> 拦截器要生效还要在视图解析器中注册，并可设置排除的路由

```java
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

@Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(new MyInterceptor()).
       addPathPatterns("/**")               .excludePathPatterns("/index.html","/index","/user/login","/asserts/css/*","/asserts/img/*","/asserts/js/*", "/");
    }

}
```

> 在此处，先设置对所有进行过略，再设置排除的路由

####  视图解析器

```java
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/main.html").setViewName("dashboard");
        registry.addViewController("/index.html").setViewName("index");
    }
 }
```

> 可以在此设置页面跳转的逻辑，可被Controller取代



#### Controller知识点

```java
/*确认添加操作*/
@PostMapping("/submitAdd")
public String submitAdd(Employee employee){
    System.out.println("提交了数据：");
    System.out.println(employee);
    employeeMapper.addEmployee(employee);
    return "redirect:/employees";
}
```

> 因为是post，表单的内容默认注入到了函数参数中，在此注入的都是html标签中包含name=？的值

![image-20200702112346046](C:\Users\XiaoChuanye\AppData\Roaming\Typora\typora-user-images\image-20200702112346046.png)

```html
<form th:action="@{/submitAdd}" method="post">
    <div class="form-group">
        <label>employeeName</label>
        <label>
            <input type="text" name="last_name" class="form-control" placeholder="a">
        </label>
    </div>

    <div class="form-group">
        <label>email</label>
        <label>
            <input type="text" name="email" class="form-control" placeholder="123@123.com">
        </label>
    </div>

    <div class="form-group">
        <label>gender</label>
        <div class="form-check form-check-inline">
            <label>
                <input class="form-check-input" type="radio" name="gender" value="1">
            </label>
            <label class="form-check-label">男</label>
        </div>
        <div class="form-check form-check-inline">
            <label>
                <input class="form-check-input" type="radio" name="gender" value="0">
            </label>
            <label class="form-check-label">女</label>
        </div>
    </div>


    <div class="form-group">
        <label>department</label>
        <label>
            <select class="form-control" name="department.id">
                <option th:each="department:${departments}"
                        th:text="${department.getDepartmentName()}"
                        th:value="${department.getId()}">
                </option>
            </select>
        </label>
    </div>


    <div class="form-group">
        <label>birth</label>
        <label>
            <input type="text" name="birth" class="form-control" placeholder="1990-01-01">
        </label>
    </div>

    <div>
        <button type="submit" class="btn btn-primary">添加</button>
    </div>
</form>
```

> 获取路由参数时，使用在路由末尾加上/{id}，在方法中使用注解（@PathVariable("id") String id）

```java
@GetMapping("/employees/delete/{id}")
public String deleteEmployee(@PathVariable("id") String id,Model model){
    employeeMapper.deleteEmployeeById(Integer.valueOf(id));
    List<Employee> employees = employeeMapper.getEmployeeList();
    model.addAttribute("employees",employees);
    return "employees/list";
}
```

