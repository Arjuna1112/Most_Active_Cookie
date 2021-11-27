package com.quant.most_active_cookie.service;

import com.quant.most_active_cookie.exception.CustomParseException;
import com.quant.most_active_cookie.model.FileDetails;
import com.quant.most_active_cookie.model.CommandLineInput;
import com.quant.most_active_cookie.parser.FileParser;
import com.quant.most_active_cookie.util.ValidationUtil;

import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class CookieServiceImpl implements CookieService {
    private FileParser fileParser;

    public CookieServiceImpl(FileParser fileParser){
        this.fileParser = fileParser;
    }
    /**
     * This method used to filter most active cookie
     * @param input
     * @throws CustomParseException
     */
    @Override
    public void processMostActiveCookie(CommandLineInput input) throws CustomParseException {
        //Option1 - Using BufferedReader & only hash map without reading complete file
        Option1(input);
        //Option2 - Using CsvToBeanBuilder, binary search and hash map
        //sOption2(input);
        //Option3 - Using CsvToBeanBuilder & concepts of streams
        //Option3(input);
    }

    /**
     * This flow uses File reader to parse file, read only up to given date
     * hash map to store cookie with count on give date finally to print
     * @param input
     * @throws CustomParseException
     */
    private void Option1(CommandLineInput input) throws CustomParseException {
        Map<String,Integer> cookiesWithCount = fileParser.parseAndPopulate(input);
        printMostActiveCookie(cookiesWithCount);
    }

    /**
     * This flow uses CsvToBeanBuilder to parse file, concepts of binary search to get cookies on selected date
     * and hash map to store cookie with count, finally to print
     * @param input
     * @throws CustomParseException
     */
    private void Option2(CommandLineInput input) throws CustomParseException {
        List<FileDetails> cookieAndTimeList = fileParser.parseFile(input.getInputFileName());
        List<FileDetails> cookiesOnSelectedDate = getCookiesOnSelectedDate(cookieAndTimeList, input.getInputDate());
        ValidationUtil.checkForCookiesGivenDate(cookiesOnSelectedDate.size());
        Map<String,Integer> cookiesWithCount = groupCookieByCount(cookiesOnSelectedDate);
        printMostActiveCookie(cookiesWithCount);
    }

    /**
     * This flow uses CsvToBeanBuilder and concepts of java 8 streams to fetch most active cookie
     * @param input
     * @throws CustomParseException
     */
    private void Option3(CommandLineInput input) throws CustomParseException {
        List<FileDetails> cookieAndTimeList = fileParser.parseFile(input.getInputFileName());
        Map<String,Long> cookieByDate = groupCookieByDate(input.getInputDate(),cookieAndTimeList);
        ValidationUtil.checkForCookiesGivenDate(cookieByDate.size());
        OptionalLong mac = macCount(cookieByDate);
        mac.ifPresent(maxFreq -> outputMAC(cookieByDate, maxFreq));
    }

    /**
     * It's used to group cookie based on selected date
     * @param selectedDate
     * @param cookieAndTimeList
     * @return
     */
    private Map<String, Long> groupCookieByDate(
            LocalDate selectedDate, List<FileDetails> cookieAndTimeList) {
        return cookieAndTimeList.stream()
                .filter(x -> selectedDate.isEqual(x.getCookieLogTimestamp().toLocalDate()))
                .collect(groupingBy(FileDetails::getCookie, counting()));
    }

    /**
     *It returns count of most active cookies
     * @param groupOfCookieByDate
     * @return
     */
    private OptionalLong macCount(Map<String, Long> groupOfCookieByDate) {
        return groupOfCookieByDate.values().stream().mapToLong(count -> count).max();
    }

    /**
     * It prints most active cookie
     * @param groupOfCookieByDate
     * @param mostActiveCookieFreq
     */
    private void outputMAC(Map<String, Long> groupOfCookieByDate, long mostActiveCookieFreq) {
        groupOfCookieByDate.entrySet().stream()
                .filter(x -> x.getValue().equals(mostActiveCookieFreq))
                .map(Map.Entry::getKey)
                .forEach(cookie->System.out.println("*** Most Active Cookie *** "+cookie));
    }

    /**
     * To fetch list of cookies on given date
     * @param cookieAndTimeList
     * @param inputDate
     * @return
     */
    private List<FileDetails> getCookiesOnSelectedDate(List<FileDetails> cookieAndTimeList, LocalDate inputDate){
        int posOfFirstCookie, posOfLastCookie;
        posOfFirstCookie = fetchFirstCookieOnInputDate(cookieAndTimeList, 0, cookieAndTimeList.size() - 1, inputDate);
        if(posOfFirstCookie==-1) return new ArrayList<>();
        posOfLastCookie = fetchLastCookieOnInputDate(cookieAndTimeList,posOfFirstCookie,cookieAndTimeList.size()-1,inputDate);
        return new ArrayList<>(cookieAndTimeList.subList(posOfFirstCookie,posOfLastCookie+1));
    }

    /**
     * To fetch first cookie position in the lost for the given date
     * @param cookieAndTimeList
     * @param low
     * @param high
     * @param inputDate
     * @return
     */
    private int fetchFirstCookieOnInputDate (List<FileDetails> cookieAndTimeList, int low, int high, LocalDate inputDate) {
        if (high >= low) {
            int mid = (low + high) / 2;
            if ((mid == 0 || (compareDates(inputDate, cookieAndTimeList.get(mid - 1)) < 0))
                    && (compareDates(inputDate, cookieAndTimeList.get(mid)) == 0))
                return mid;
            else if ((compareDates(inputDate, cookieAndTimeList.get(mid)) < 0))
                return fetchFirstCookieOnInputDate(cookieAndTimeList, (mid + 1), high, inputDate);
            else
                return fetchFirstCookieOnInputDate(cookieAndTimeList, low, (mid - 1), inputDate);
        }
        return -1;
    }

    /**
     * To fetch last cookie position in the list for the given date
     * @param cookieAndTimeList
     * @param low
     * @param high
     * @param inputDate
     * @return
     */
    private int fetchLastCookieOnInputDate(List<FileDetails> cookieAndTimeList, int low, int high, LocalDate inputDate) {
        if (high >= low) {
            int mid = (low + high) / 2;
            if ((mid == cookieAndTimeList.size() - 1 || (compareDates(inputDate, cookieAndTimeList.get(mid + 1)) > 0))
                    && (compareDates(inputDate, cookieAndTimeList.get(mid)) == 0))
                return mid;
            else if (compareDates(inputDate, cookieAndTimeList.get(mid)) > 0)
                return fetchLastCookieOnInputDate(cookieAndTimeList, low, (mid - 1), inputDate);
            else
                return fetchLastCookieOnInputDate(cookieAndTimeList, (mid + 1), high, inputDate);
        }
        return -1;
    }

    /**
     * To compare input date with date present in log file
     * @param inputDate
     * @param fileDetails
     * @return
     */
    private int compareDates(LocalDate inputDate, FileDetails fileDetails){
        LocalDate cookieDate = fileDetails.getCookieLogTimestamp().toLocalDate();
        return Integer.compare(inputDate.compareTo(cookieDate), 0);
    }

    /**
     * To group cookie based on number of times it's present in given date
     * @param fileDetailsList
     * @return
     */
    private Map<String,Integer> groupCookieByCount(List<FileDetails> fileDetailsList){
        Map<String,Integer> cookiesByCount = new HashMap<>();
        for(FileDetails fileDetails:fileDetailsList){
            if (cookiesByCount.containsKey(fileDetails.getCookie())) {
                cookiesByCount.put(fileDetails.getCookie(), cookiesByCount.get(fileDetails.getCookie()) + 1);
            } else {
                cookiesByCount.put(fileDetails.getCookie(), 1);
            }
        }
        return cookiesByCount;
    }

    /**
     * To print most active cookie in hash map
     * @param cookiesWithCount
     */
    private void printMostActiveCookie(Map<String,Integer> cookiesWithCount){
        List<Map.Entry<String,Integer>> list = new ArrayList<>(cookiesWithCount.entrySet());
        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
        int max = list.get(0).getValue();
        for(Map.Entry<String, Integer> mapEntry : list){
            if(max>mapEntry.getValue()) break;
            System.out.println("*** Most Active Cookie *** "+mapEntry.getKey());
        }
    }
}
