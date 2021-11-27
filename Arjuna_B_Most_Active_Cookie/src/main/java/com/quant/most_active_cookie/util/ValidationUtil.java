package com.quant.most_active_cookie.util;

import com.quant.most_active_cookie.exception.CustomParseException;
import com.quant.most_active_cookie.parser.FileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileParser.class);

    public static void checkForCookiesGivenDate(int count) throws CustomParseException {
        if(count==0){
            LOGGER.error("*** No Cookie Present for Given Date ***");
            throw new CustomParseException(new RuntimeException());
        }
    }
}
