package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper {

        /**
         * 根据ID查询用户
         */
        @Select("SELECT * FROM user WHERE id = #{id}")
        User findById(@Param("id") Long id);

        /**
         * 根据用户名查询用户
         */
        @Select("SELECT * FROM user WHERE username = #{username}")
        User findByUsername(@Param("username") String username);

        /**
         * 根据手机号查询用户
         */
        @Select("SELECT * FROM user WHERE phone = #{phone}")
        User findByPhone(@Param("phone") String phone);

        /**
         * 根据用户名和角色查询用户
         */
        @Select("SELECT * FROM user WHERE username = #{username} AND role = #{role}")
        User findByUsernameAndRole(@Param("username") String username, @Param("role") String role);

        /**
         * 根据手机号和角色查询用户
         */
        @Select("SELECT * FROM user WHERE phone = #{phone} AND role = #{role}")
        User findByPhoneAndRole(@Param("phone") String phone, @Param("role") String role);

        /**
         * 根据邮箱查询用户
         */
        @Select("SELECT * FROM user WHERE email = #{email}")
        User findByEmail(@Param("email") String email);

        /**
         * 根据账号查询用户（手机号/用户名/邮箱）
         */
        @Select("SELECT * FROM user WHERE phone = #{account} OR username = #{account} OR email = #{account}")
        User findByAccount(@Param("account") String account);

        /**
         * 根据账号和角色查询用户（手机号/用户名/邮箱 + 角色）
         */
        @Select("SELECT * FROM user WHERE (phone = #{account} OR username = #{account} OR email = #{account}) AND role = #{role}")
        User findByAccountAndRole(@Param("account") String account, @Param("role") String role);

        /**
         * 插入用户
         */
        @Insert("INSERT INTO user (username, phone, email, password_hash, nickname, avatar, role, status, created_at) "
                        +
                        "VALUES (#{username}, #{phone}, #{email}, #{passwordHash}, #{nickname}, #{avatar}, #{role}, #{status}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(User user);

        /**
         * 更新用户信息
         */
        @Update("<script>" +
                        "UPDATE user SET updated_at = NOW() " +
                        "<if test='nickname != null'>, nickname = #{nickname}</if>" +
                        "<if test='avatar != null'>, avatar = #{avatar}</if>" +
                        "<if test='phone != null'>, phone = #{phone}</if>" +
                        "<if test='email != null'>, email = #{email}</if>" +
                        " WHERE id = #{id}" +
                        "</script>")
        int update(User user);

        /**
         * 更新密码
         */
        @Update("UPDATE user SET password_hash = #{passwordHash}, password_updated_at = NOW(), updated_at = NOW() WHERE id = #{id}")
        int updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);

        /**
         * 更新最后登录信息
         */
        @Update("UPDATE user SET last_login_at = #{lastLoginAt}, last_login_ip = #{lastLoginIp}, updated_at = NOW() WHERE id = #{id}")
        int updateLastLogin(@Param("id") Long id, @Param("lastLoginAt") LocalDateTime lastLoginAt,
                        @Param("lastLoginIp") String lastLoginIp);

        /**
         * 更新用户状态
         */
        @Update("UPDATE user SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") Integer status);

        /**
         * 分页查询用户列表
         */
        @Select("<script>" +
                        "SELECT * FROM user WHERE 1=1 " +
                        "<if test='role != null'>AND role = #{role}</if>" +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "<if test='keyword != null and keyword != \"\"'>AND (username LIKE CONCAT('%',#{keyword},'%') OR phone LIKE CONCAT('%',#{keyword},'%') OR nickname LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<User> findPage(@Param("role") String role, @Param("status") Integer status,
                        @Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计用户数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM user WHERE 1=1 " +
                        "<if test='role != null'>AND role = #{role}</if>" +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "<if test='keyword != null and keyword != \"\"'>AND (username LIKE CONCAT('%',#{keyword},'%') OR phone LIKE CONCAT('%',#{keyword},'%') OR nickname LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        "</script>")
        long countByRole(@Param("role") String role, @Param("status") Integer status, @Param("keyword") String keyword);

        /**
         * 根据ID查询用户（别名）
         */
        @Select("SELECT * FROM user WHERE id = #{id}")
        User selectById(@Param("id") Long id);

        /**
         * 分页查询用户列表（管理员）- 只查询普通用户
         */
        @Select("<script>" +
                        "SELECT * FROM user WHERE role = 'USER' " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "<if test='keyword != null and keyword != \"\"'>AND (username LIKE CONCAT('%',#{keyword},'%') OR phone LIKE CONCAT('%',#{keyword},'%') OR nickname LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{size}" +
                        "</script>")
        List<User> selectList(@Param("keyword") String keyword, @Param("status") Integer status,
                        @Param("offset") int offset, @Param("size") int size);

        /**
         * 统计用户数量（管理员）- 只统计普通用户
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM user WHERE role = 'USER' " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "<if test='keyword != null and keyword != \"\"'>AND (username LIKE CONCAT('%',#{keyword},'%') OR phone LIKE CONCAT('%',#{keyword},'%') OR nickname LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        "</script>")
        int count(@Param("keyword") String keyword, @Param("status") Integer status);

        /**
         * 统计总用户数
         */
        @Select("SELECT COUNT(*) FROM user")
        long countAll();
}
