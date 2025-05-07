package com.satellites;

import com.satellites.entity.TimeInterval;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SatellitesApplication {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("specificare il percorso del file .dat dei periodi di visibilità");
            return;
        }

        String filePath = args[0];

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<TimeInterval> intervals = new ArrayList<>();
            List<LocalTime> startTimes = new ArrayList<>();
            List<LocalTime> endTimes = new ArrayList<>();

            //Leggo il file e inserisco gli intervalli in una lista ordinabile
            while ((line = br.readLine()) != null) {
                String startTimeString = line.substring(0, line.indexOf(","));
                String endTimeString = line.substring(line.indexOf(",") + 1);
                // Parser dell'orario
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
                LocalTime startTime = LocalTime.parse(startTimeString, formatter);
                LocalTime endTime = LocalTime.parse(endTimeString, formatter);
                // aggiungo l'intervallo alla lista
                intervals.add(new TimeInterval(startTime, endTime));

                // aggiungo l'inizio dell'intervallo alla lista di istanti di inizio
                startTimes.add(startTime);
                // aggiungo la fine dell'intervallo alla lista di istanti di fine
                endTimes.add(endTime);
            }

            if (startTimes.size() != endTimes.size())
                throw new RuntimeException("The start and end time instant lists have different lengths.");

            int listSize = startTimes.size();

            //Ordino la lista degli istanti di inizio
            Collections.sort(startTimes);

            List<Integer> numberOfIntervalsStartIsIncludedList = new ArrayList<>();
            List<Integer> numberOfIntervalsEndIsIncludedList = new ArrayList<>();

            for (int i = 0; i < listSize; i++) {

                LocalTime startTime = startTimes.get(i);

                List<TimeInterval> otherIntervals = intervals.stream().filter(e ->
                        !e.getStartTime().equals(startTime)).toList();

                int numberOfIntervalsStartIsIncluded = otherIntervals.stream().filter(e ->
                        !startTime.isBefore(e.getStartTime()) && !startTime.isAfter(e.getEndTime())).toList().size();

                numberOfIntervalsStartIsIncludedList.add(numberOfIntervalsStartIsIncluded);

                LocalTime end = endTimes.get(i);

                List<TimeInterval> otherEndTimes = intervals.stream().filter(e ->
                        !e.getEndTime().equals(end)).toList();

                int numberOfIntervalsEndIsIncluded = otherEndTimes.stream().filter(e ->
                        !end.isBefore(e.getStartTime()) && !end.isAfter(e.getEndTime())).toList().size();

                numberOfIntervalsEndIsIncludedList.add(numberOfIntervalsEndIsIncluded);
            }

            Integer maxNumberOfIntervalsStartIsIncluded = Collections.max(numberOfIntervalsStartIsIncludedList);
            Integer maxNumberOfIntervalsEndIsIncluded = Collections.max(numberOfIntervalsEndIsIncludedList);

            if (!Objects.equals(maxNumberOfIntervalsStartIsIncluded, maxNumberOfIntervalsEndIsIncluded))
                throw new RuntimeException("The maximum number of satellites at the start time and at the end time is not the same.");

            int maxNumberOfSatellites = maxNumberOfIntervalsStartIsIncluded;

            List<Integer> indexesOfMaxNumberStart = new ArrayList<>();
            List<Integer> indexesOfMaxNumberEnd = new ArrayList<>();

            for (int i = 0; i < listSize; i++) {
                if (numberOfIntervalsStartIsIncludedList.get(i).equals(maxNumberOfIntervalsStartIsIncluded)) {
                    indexesOfMaxNumberStart.add(i);
                }

                if (numberOfIntervalsEndIsIncludedList.get(i).equals(maxNumberOfIntervalsEndIsIncluded))
                    indexesOfMaxNumberEnd.add(i);
            }
            for (int i = 0; i < indexesOfMaxNumberStart.size(); i++) {

                LocalTime startTimeOfMaxOverlap = startTimes.get(indexesOfMaxNumberStart.get(i));

                LocalTime endTimeOfMaxOverlap = endTimes.get(indexesOfMaxNumberEnd.get(i));

                System.out.println(startTimeOfMaxOverlap + "-" + endTimeOfMaxOverlap + ";" + maxNumberOfSatellites);
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }
    }

}
