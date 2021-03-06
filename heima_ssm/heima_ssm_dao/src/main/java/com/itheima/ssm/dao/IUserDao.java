package com.itheima.ssm.dao;

import cn.itcast.domain.Role;
import cn.itcast.domain.UserInfo;
import org.apache.ibatis.annotations.*;

import javax.annotation.Resources;
import java.util.List;

public interface IUserDao {
    @Select("select * from users where username=#{username}")
    @Results({
            @Result(id = true,property = "id",column = "id"),
            @Result(property = "username",column = "username"),
            @Result(property = "email",column = "email"),
            @Result(property = "password",column = "password"),
            @Result(property = "phoneNum", column = "phoneNum"),
            @Result(property = "status", column = "status"),
            @Result(property = "roles",column = "id",javaType = java.util.List.class,many = @Many(select = "com.itheima.ssm.dao.IRoleDao.findRoleByUserId"))
    })
    public UserInfo findByUserName(String username) throws Exception;

    @Select("select * from users")
    List<UserInfo> findAll() throws Exception;

    @Insert("insert into users(email,username,password,phoneNum,status)values(#{email},#{username},#{password},#{phoneNum},#{status})")
    void save(UserInfo userInfo) throws Exception;

    @Select("select * from users where id=#{id}")
   @Results({
           @Result(id = true,property = "id",column = "id"),
           @Result(property = "username", column = "username"),
           @Result(property = "email", column = "email"),
           @Result(property = "password", column = "password"),
           @Result(property = "phoneNum", column = "phoneNum"),
           @Result(property = "status", column = "status"),
           @Result(property = "roles",column = "id",javaType = java.util.List.class,many = @Many(select = "com.itheima.ssm.dao.IRoleDao.findRoleByUserId"))
   })
    UserInfo findById(String id);

    @Select("select * from role where id not in(select roleId from users_role where userId=#{userId})")
    List<Role> findOtherRoles(String userId);

    @Insert("insert nto users_role(userId,roleId) values(#{userId},#{roleId})")
    void addRoleToUser(@Param("userId") String userId,@Param("roleId") String roleId) throws Exception;

}
