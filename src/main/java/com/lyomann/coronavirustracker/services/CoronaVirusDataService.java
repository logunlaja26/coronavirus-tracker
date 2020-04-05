package com.lyomann.coronavirustracker.services;

import com.lyomann.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    private static final String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static final Logger log = LoggerFactory.getLogger(CoronaVirusDataService.class);

    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException {
        List<LocationStats> newStats = new ArrayList<>();
        CSVFormat format = CSVFormat.DEFAULT.withDelimiter(',').withFirstRecordAsHeader();
        CSVParser parser = CSVParser.parse(new URL(VIRUS_DATA_URL), Charset.defaultCharset(), format);
        List<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records) {
            int lastIndex = record.size() - 1;
            int secondToLastIndex = lastIndex - 1;
            // This protects against records ending with just a ,
            if (record.get(lastIndex).isEmpty()){
                lastIndex--;
                secondToLastIndex--;
            }

            LocationStats locationStats = new LocationStats(
                    record.get(0),
                    record.get(1),
                    Integer.parseInt(record.get(lastIndex)),
                    Integer.parseInt(record.get(lastIndex)) - Integer.parseInt(record.get(secondToLastIndex))
            );
            log.info("Country: " + locationStats.getCountry() +
                    ", State: " + locationStats.getState() +
                    ", Latest Total Cases: " + locationStats.getLatestTotalCases() +
                    ", Difference from Pre day: " + locationStats.getDiffFromPrevDay());
            newStats.add(locationStats);
        }

        this.allStats = newStats;
    }


}