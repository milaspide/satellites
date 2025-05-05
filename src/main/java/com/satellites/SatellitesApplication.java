package com.satellites;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class SatellitesApplication {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("specificare il percorso del file .dat dei periodi di visibilità");
            return;
        }

        String percorsoFile = args[0];

        try (BufferedReader br = new BufferedReader(new FileReader(percorsoFile))) {
            String linea;
            int maxNumberOfSatellites = 0;
            LocalTime startTimeOfMaxInterval = LocalTime.MAX;
            LocalTime endTimeOfMaxInterval = LocalTime.MAX;
            while ((linea = br.readLine()) != null) {
                String startTimeString = linea.substring(0, linea.indexOf(","));
                String endTimeString = linea.substring(linea.indexOf(",") + 1);
                // Parser dell'orario
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
                LocalTime startTime = LocalTime.parse(startTimeString, formatter);
                LocalTime endTime = LocalTime.parse(endTimeString, formatter);
                LocalTime daVerificare = LocalTime.now();

                boolean compreso = !daVerificare.isBefore(startTime) && !daVerificare.isAfter(endTime);
            }
            System.out.println(startTimeOfMaxInterval + "-" + endTimeOfMaxInterval + ";" + maxNumberOfSatellites);
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }
    }

}
