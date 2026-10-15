// @author cly202405315310
package org.example.demo1;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取 Session，判断有没有登录过
        HttpSession session = request.getSession();
        Object username = session.getAttribute("username");

        if (username == null) {
            // 没登录，拒绝访问，返回提示
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"请先登录\"}");
            return false;
        }
        return true;  // 登录过，放行
    }
}