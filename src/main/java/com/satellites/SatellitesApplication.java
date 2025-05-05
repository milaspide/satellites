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
            String linea;
            List<TimeInterval> intervals = new ArrayList<>();

            //Leggo il file e inserisco gli intervalli in una lista ordinabile
            while ((linea = br.readLine()) != null) {
                String startTimeString = linea.substring(0, linea.indexOf(","));
                String endTimeString = linea.substring(linea.indexOf(",") + 1);
                // Parser dell'orario
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
                LocalTime startTime = LocalTime.parse(startTimeString, formatter);
                LocalTime endTime = LocalTime.parse(endTimeString, formatter);
                // aggiungo l'intervallo alla lista
                intervals.add(new TimeInterval(startTime, endTime));
            }
            //Ordino la lista per ora di inizio
//            intervals.sort(Comparator.comparing(TimeInterval::getStartTime));

            //Dichiaro le variabili globali
            int maxNumberOfSatellites = 0;
            LocalTime startTimeOfMaxInterval = null;
            LocalTime endTimeOfMaxInterval = null;

            // Stampa intervalli ordinati
            /*for (int i = 1; i < intervals.size(); i++) {
                TimeInterval interval = intervals.get(i);

                System.out.println(interval.toString());

                LocalTime startTime = interval.getStartTime();
                LocalTime endTime = interval.getEndTime();

                boolean isIncludedInPrev = false;
                int numberOfSatellites = 1;

                List<TimeInterval> previousIntervals = intervals.subList(0, i - 1);

                for (int j = i; j >= 0; j--) {

                    if (!startTime.isAfter(previousIntervals.get(j).getEndTime())) {
                        numberOfSatellites++;
                        startTimeOfMaxInterval = startTime;
                        //
                        endTimeOfMaxInterval = endTime;
                    } else {
                        numberOfSatellites--;
                    }
                }
            }*/

            List<Integer> numberOfStartInclusionsList = new ArrayList<>();
            List<Integer> numberOfEndInclusionsList = new ArrayList<>();
            for (int i = 0; i < intervals.size(); i++) {

                TimeInterval interval = intervals.get(i);

                List<TimeInterval> otherIntervals = intervals.stream().filter(e ->
                        !e.equals(interval)).toList();

                int numberOfIntervalsStartIsIncluded = otherIntervals.stream().filter(e ->
                        !interval.getStartTime().isBefore(e.getStartTime()) && !interval.getStartTime().isAfter(e.getEndTime())).toList().size();

                numberOfStartInclusionsList.add(numberOfIntervalsStartIsIncluded);

                int numberOfIntervalsEndIsIncluded = otherIntervals.stream().filter(e ->
                        !interval.getEndTime().isBefore(e.getStartTime()) && !interval.getEndTime().isAfter(e.getEndTime())).toList().size();

                numberOfEndInclusionsList.add(numberOfIntervalsEndIsIncluded);

            }
            Integer maxNumberOfStart = Collections.max(numberOfStartInclusionsList);
            System.out.println(maxNumberOfStart);

            List<Integer> massimiInizio = numberOfStartInclusionsList.stream().filter(e ->
                    e.equals(maxNumberOfStart)).toList();

//            System.out.println(numberOfStartInclusionsList.stream().filter(e -> e < maxNumberOfStart).toList().size());
            System.out.println(massimiInizio.size());

            int indexOfMaxStart = numberOfStartInclusionsList.indexOf(maxNumberOfStart);

            startTimeOfMaxInterval = intervals.get(indexOfMaxStart).getStartTime();

            Integer maxNumberOfEnd = Collections.max(numberOfEndInclusionsList);
            System.out.println(maxNumberOfEnd);

            List<Integer> massimiFine = numberOfEndInclusionsList.stream().filter(e ->
                    e.equals(maxNumberOfEnd)).toList();

            System.out.println(massimiFine.size());

            int indexOfMaxEnd = numberOfStartInclusionsList.indexOf(maxNumberOfEnd);

            endTimeOfMaxInterval = intervals.get(indexOfMaxEnd).getEndTime();

            if (Objects.equals(maxNumberOfStart, maxNumberOfEnd))
                maxNumberOfSatellites = maxNumberOfStart;

            System.out.println(startTimeOfMaxInterval + "-" + endTimeOfMaxInterval + ";" + maxNumberOfSatellites);
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }
    }

}
