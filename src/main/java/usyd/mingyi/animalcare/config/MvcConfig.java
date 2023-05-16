package usyd.mingyi.animalcare.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    ProjectProperties projectProperties;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/signup", "/username", "/email", "/validate", "/images/**", "/*.JPG");//放行模式
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println(projectProperties.fileDiskLocation);
        registry.addResourceHandler("/images/**").addResourceLocations("file://" + projectProperties.fileDiskLocation);

    }
}
