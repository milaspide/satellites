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
                throw new RuntimeException("Lunghezze delle liste degli istanti di inizio e di fine diverse");
            
            //Ordino la lista degli istanti di inizio
            Collections.sort(startTimes);

            List<Integer> numberOfStartInclusionsList = new ArrayList<>();
            List<Integer> numberOfEndInclusionsList = new ArrayList<>();

            int listSize = startTimes.size();

            for (int i = 0; i < listSize; i++) {

                LocalTime start = startTimes.get(i);

                List<TimeInterval> otherIntervals = intervals.stream().filter(e ->
                        !e.getStartTime().equals(start)).toList();

                int numberOfIntervalsStartIsIncluded = otherIntervals.stream().filter(e ->
                        !start.isBefore(e.getStartTime()) && !start.isAfter(e.getEndTime())).toList().size();

                numberOfStartInclusionsList.add(numberOfIntervalsStartIsIncluded);

                LocalTime end = endTimes.get(i);

                List<TimeInterval> otherEndTimes = intervals.stream().filter(e ->
                        !e.getEndTime().equals(end)).toList();

                int numberOfIntervalsEndIsIncluded = otherEndTimes.stream().filter(e ->
                        !end.isBefore(e.getStartTime()) && !end.isAfter(e.getEndTime())).toList().size();

                numberOfEndInclusionsList.add(numberOfIntervalsEndIsIncluded);
            }
            Integer maxNumberOfIntervalsStartIsIncluded = Collections.max(numberOfStartInclusionsList);
            System.out.println(maxNumberOfIntervalsStartIsIncluded);

            Integer maxNumberOfIntervalsEndIsIncluded = Collections.max(numberOfEndInclusionsList);
            System.out.println(maxNumberOfIntervalsEndIsIncluded);

            List<Integer> indexesOfMaxNumberStart = new ArrayList<>();
            List<Integer> indexesOfMaxNumberEnd = new ArrayList<>();

            for (int i = 0; i < listSize; i++) {
                if (numberOfStartInclusionsList.get(i).equals(maxNumberOfIntervalsStartIsIncluded)) {
                    indexesOfMaxNumberStart.add(i);
                }

                if (numberOfEndInclusionsList.get(i).equals(maxNumberOfIntervalsEndIsIncluded))
                    indexesOfMaxNumberEnd.add(i);
            }
            for (int i = 0; i < indexesOfMaxNumberStart.size(); i++) {

                LocalTime timeOfIthMaxOfStart = startTimes.get(indexesOfMaxNumberStart.get(i));

                LocalTime timeOfIthMaxOfEnd = endTimes.get(indexesOfMaxNumberEnd.get(i));

                int ithMaxNumberOfSatellites = 0;
                if (Objects.equals(maxNumberOfIntervalsStartIsIncluded, maxNumberOfIntervalsEndIsIncluded))
                    ithMaxNumberOfSatellites = maxNumberOfIntervalsStartIsIncluded;
                else
                    throw new RuntimeException("Il numero massimo di satelliti di inizio e fine non coincidono");

                System.out.println(timeOfIthMaxOfStart + "-" + timeOfIthMaxOfEnd + ";" + ithMaxNumberOfSatellites);
            }

        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }
    }

}
