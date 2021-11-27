package com.quant.most_active_cookie.runner;

import com.quant.most_active_cookie.executor.CookieExecutor;
import com.quant.most_active_cookie.executor.CookieExecutorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import static java.lang.System.exit;


public class CookieRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CookieRunner.class);
    private ApplicationContext applicationContext;
    private CookieExecutor cookieExecutor;


    /**
     * Initialized from CookieConfig class
     * @param applicationContext
     * @param cookieExecutor
     */
    public CookieRunner(ApplicationContext applicationContext, CookieExecutor cookieExecutor){
        this.applicationContext = applicationContext;
        this.cookieExecutor = cookieExecutor;
    }

    /**
     * Execution Starts here
     * @param args : input argument passed
     */
    @Override
    public void run(String... args) {
        LOGGER.info("****** Application Started! *****");
        endExecution(()->cookieExecutor.execute(args));
    }

    /**
     * It's used to end execution of application
     * @param exitCodeGenerator
     */
    public void endExecution(ExitCodeGenerator exitCodeGenerator){
        exit(SpringApplication.exit(applicationContext,exitCodeGenerator));
    }


}
