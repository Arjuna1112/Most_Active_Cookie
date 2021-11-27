package com.quant.most_active_cookie.parser;

import com.opencsv.bean.CsvToBeanBuilder;
import com.quant.most_active_cookie.exception.CustomParseException;
import com.quant.most_active_cookie.model.FileDetails;
import com.quant.most_active_cookie.model.CommandLineInput;
import com.quant.most_active_cookie.util.ValidationUtil;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Paths.get;
import static java.time.LocalDate.parse;

public class FileParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileParser.class);

    /**
     * Called from executor this method used to parse input arguments
     * such as file name and input date
     * @param args
     * @return
     * @throws CustomParseException
     */
    public static CommandLineInput parseInput(String[] args) throws CustomParseException {
        CommandLineInput input = new CommandLineInput();
        CommandLineParser commandLineParser = new DefaultParser();
        Options options = parseCommandOption();
        try {
            CommandLine commandLine = commandLineParser.parse(options, args);
            input.setInputFileName(commandLine.getOptionValue("file"));
            input.setInputDate(parse(commandLine.getOptionValue("date")));
            return input;
        } catch (ParseException e) {
            LOGGER.error("Exception in Paring Command Line Input{}",e.getMessage());
            throw new CustomParseException(e);
        }
    }

    /**
     * In this method options dependency used to read file name and input date
     * @return
     */
    private static Options parseCommandOption(){
        Options options = new Options();
        // File name of cookie log
        Option fileName = new Option("f", "file", true, "Cookie Log File Path");
        fileName.setRequired(true);
        options.addOption(fileName);
        // Selected date to get the most active cookie
        Option selectedDate =
                new Option("d", "date", true, "Input Date To Fetch Most Active Cookie");
        selectedDate.setRequired(true);
        options.addOption(selectedDate);
        return options;
    }

    /**
     * This method used to parseFile and initialize to cookie and timestamp of each
     * @param fileName
     * @return
     * @throws CustomParseException
     */
    public List<FileDetails> parseFile(String fileName) throws CustomParseException {
        try {
            return new CsvToBeanBuilder<FileDetails>(newBufferedReader(get(fileName)))
                    .withType(FileDetails.class)
                    .withFilter(line->!line[0].contains("cookie"))
                    .build()
                    .parse();
        }catch (Exception exception){
            LOGGER.error("Exception in Parsing File {}",exception.getMessage());
            throw new CustomParseException(exception);
        }
    }

    /**
     * @param input
     * @return
     * @throws CustomParseException
     */
    public Map<String,Integer> parseAndPopulate(CommandLineInput input) throws CustomParseException {
        Map<String,Integer> cookiesWithCount = new HashMap<>();
        File file = new File(input.getInputFileName());
        Boolean readFile = true;
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine())!=null && readFile){
                if(line.startsWith("cookie,timestamp")) continue;
                readFile = populateCookieMapWithCount(line,input.getInputDate(),cookiesWithCount);
            }
        } catch (FileNotFoundException exception){
            LOGGER.error("File not found in specified path "+exception.getMessage());
            throw new CustomParseException(exception);
        } catch (IOException exception) {
            LOGGER.error("File not read properly "+exception.getMessage());
            throw new CustomParseException(exception);
        }
        ValidationUtil.checkForCookiesGivenDate(cookiesWithCount.size());
        return cookiesWithCount;
    }

    /**
     * Create Hash Map with cookies and number of occrence of cookie on given date
     * @param line
     * @param localDate
     * @param cookiesWithCount
     * @return
     */
    private boolean populateCookieMapWithCount(String line,LocalDate localDate,Map<String,Integer> cookiesWithCount){
        String[] details = line.split(",");
        if(details.length!=2){
            System.err.println("Improper details present in File");
            return true;
        }
        LocalDate cookieDate = LocalDateTime.parse(details[1],
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")).toLocalDate();
        if(cookieDate.compareTo(localDate)==0){
            cookiesWithCount.put(details[0],
                    cookiesWithCount.containsKey(details[0])?cookiesWithCount.get(details[0])+1:1);
        }
        if(cookieDate.compareTo(localDate)<0){
            return false;
        }
        return true;
    }



}
