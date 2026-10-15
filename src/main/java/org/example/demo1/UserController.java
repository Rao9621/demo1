// @author cly202405315310
package org.example.demo1;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
/*
@auth:计算机243 陈琳烨
@data:2026 9 22
 */
@RestController
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 1. 注册
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        String checkSql = "SELECT COUNT(*) FROM user WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, user.getUsername());

        if (count > 0) {
            return "用户名已存在，请换一个";
        }

        String insertSql = "INSERT INTO user (username, password) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, user.getUsername(), user.getPassword());
        return "注册成功";
    }

    // 2. 登录
    @PostMapping("/login")
    public String login(@RequestBody User user, HttpSession session) {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ? AND password = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, user.getUsername(), user.getPassword());

        if (count > 0) {
            session.setAttribute("username", user.getUsername());
            return "登录成功，欢迎 " + user.getUsername();
        }
        return "用户名或密码错误";
    }

    // 3. 退出登录
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "已退出登录";
    }

    // 4. 获取当前登录用户名
    @GetMapping("/currentUser")
    public Map<String, String> currentUser(HttpSession session) {
        Object username = session.getAttribute("username");
        if (username == null) {
            return Map.of("username", "");
        }
        return Map.of("username", username.toString());
    }
}