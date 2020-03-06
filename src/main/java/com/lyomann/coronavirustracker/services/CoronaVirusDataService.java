package com.lyomann.coronavirustracker.services;

import com.lyomann.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";

    private List<LocationStats> allStats = new ArrayList<>();
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();
        CSVParser parser = CSVParser.parse(new URL(VIRUS_DATA_URL), Charset.defaultCharset(), CSVFormat.DEFAULT);
        List<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            System.out.println(record.get(0));
            LocationStats locationStats = new LocationStats();
            locationStats.setState(record.get(0));
            locationStats.setCountry(record.get(1));
            //DateFormat df = new SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH);
            locationStats.setLatestTotalCases(Integer.parseInt(record.get(record.size() - 1)));
            System.out.println(locationStats);
            newStats.add(locationStats);
        }
        this.allStats = newStats;
    }

}