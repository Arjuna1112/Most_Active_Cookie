package com.quant.most_active_cookie.config;

import com.quant.most_active_cookie.executor.CookieExecutor;
import com.quant.most_active_cookie.executor.CookieExecutorImpl;
import com.quant.most_active_cookie.parser.FileParser;
import com.quant.most_active_cookie.service.CookieServiceImpl;
import com.quant.most_active_cookie.service.CookieService;
import com.quant.most_active_cookie.runner.CookieRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieConfig {

    @Bean
    public CookieRunner cookieRunner(ApplicationContext context, CookieExecutor executor){
        return new CookieRunner(context,executor);
    }

    @Bean
    public CookieService cookieService(){
        return new CookieServiceImpl(new FileParser());
    }

    @Bean
    public CookieExecutor cookieExecutor(CookieService CookieService){
        return new CookieExecutorImpl(CookieService);
    }
}
