package com.quant.most_active_cookie.executor;

import com.quant.most_active_cookie.exception.CustomParseException;
import com.quant.most_active_cookie.service.CookieService;
import com.quant.most_active_cookie.model.CommandLineInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.quant.most_active_cookie.parser.FileParser.parseInput;

public class CookieExecutorImpl implements CookieExecutor{
    private static final Logger LOGGER = LoggerFactory.getLogger(CookieExecutorImpl.class);
    private CookieService CookieService;

    /**
     * Initialized from CookieConfig class
     * @param CookieService
     */
    public CookieExecutorImpl(CookieService CookieService){
        this.CookieService = CookieService;
    }

    /**
     * Called from runner class, it has 2 parts
     * first step it parses give argument
     * it prints most active cookie (MAC)
     * @param args
     * @return
     */
    @Override
    public int execute(String[] args) {
        try {
            CommandLineInput input = parseInput(args);
            CookieService.processMostActiveCookie(input);
            return 0;
        }catch (CustomParseException |RuntimeException exception){
            LOGGER.error("*** Application Execution Failed {}",exception.getMessage());
        }
        return 1;
    }


}
