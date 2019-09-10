package com.example.demo.dao;

import com.example.demo.domin.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

//这个注解代表这是一个mybatis的操作数据库的类
@Repository

public interface UserDao {
    // 根据username获得一个User类
    @Select("select * from students where username=#{name}")
    User getOneUser(String name);


    //插入一个User
    @Insert("insert into students (username,password) values(#{username},#{password})")
    boolean setOneUser(User user);

}