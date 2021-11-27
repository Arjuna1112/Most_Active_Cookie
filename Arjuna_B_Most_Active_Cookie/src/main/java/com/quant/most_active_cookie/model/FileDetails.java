package com.quant.most_active_cookie.model;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

public class FileDetails{
    @CsvBindByPosition(position = 0)
    private String cookie;

    @CsvBindByPosition(position = 1)
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime cookieLogTimestamp;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public LocalDateTime getCookieLogTimestamp() {
        return cookieLogTimestamp;
    }

    public void setCookieLogTimestamp(LocalDateTime cookieLogTimestamp) {
        this.cookieLogTimestamp = cookieLogTimestamp;
    }

}
