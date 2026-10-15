// @author cly202405315310
package org.example.demo1;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/students/**", "/courses/**", "/currentUser")
                .excludePathPatterns("/login", "/register", "/index.html", "/courses.html", "/login.html", "/register.html", "/home.html", "/hello");
    }
}