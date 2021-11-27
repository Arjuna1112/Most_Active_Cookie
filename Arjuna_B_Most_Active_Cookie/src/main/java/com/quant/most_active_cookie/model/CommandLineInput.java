package com.quant.most_active_cookie.model;

import java.time.LocalDate;

public class CommandLineInput {
    private String inputFileName;
    private LocalDate inputDate;

    public CommandLineInput(){

    }

    public CommandLineInput(String inputFileName, LocalDate inputDate){
        this.inputFileName = inputFileName;
        this.inputDate = inputDate;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public LocalDate getInputDate() {
        return inputDate;
    }

    public void setInputDate(LocalDate inputDate) {
        this.inputDate = inputDate;
    }
}
