package com.quant.most_active_cookie.service;

import com.quant.most_active_cookie.exception.CustomParseException;
import com.quant.most_active_cookie.model.CommandLineInput;

public interface CookieService {
    void processMostActiveCookie(CommandLineInput input) throws CustomParseException;
}
