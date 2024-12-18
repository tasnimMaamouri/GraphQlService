package com.enicarthage.GraphQlService;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmissionService {

    private List<CO2Emission> emissions;
    private String csvFilePath = "E:/Users/tassm/Downloads/ProjetSoap/SoapService/src/main/java/data/dataset.csv";

    public EmissionService() {
        emissions = new ArrayList<>();
        loadEmissionsFromCSV("E:/Users/tassm/Downloads/ProjetSoap/SoapService/src/main/java/data/dataset.csv");
    }

    private void loadEmissionsFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                String[] values = line.split(",");
                if (values.length >= 4) {
                    try {
                        CO2Emission emission = new CO2Emission(
                                values[0].trim().replace("\"", ""),
                                values[1].trim().replace("\"", ""),
                                values[2].trim().replace("\"", ""),
                                Float.parseFloat(values[3].trim().replace("\"", ""))
                        );
                        emissions.add(emission);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid CO2 value: " + values[3]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to get the emission record for the region with the highest total CO2 emissions
    public CO2Emission getMostEmissionsByRegion() {
        // Group emissions by region, excluding "WORLD", and sum the CO2 emissions for each region
        Map<String, Float> totalEmissionsByRegion = emissions.stream()
                .filter(emission -> !emission.region().equals("WORLD"))  // Filter out "WORLD" region
                .collect(Collectors.groupingBy(
                        CO2Emission::region,
                        Collectors.reducing(0f, CO2Emission::value, Float::sum)
                ));

        // Find the region with the highest total emissions
        String regionWithMostEmissions = totalEmissionsByRegion.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new IllegalStateException("No emissions data found"))
                .getKey();

        // Find and return the emission record for that region
        return emissions.stream()
                .filter(emission -> emission.region().equals(regionWithMostEmissions))
                .max(Comparator.comparing(CO2Emission::value))
                .orElseThrow(() -> new IllegalStateException("No emissions data found for the region"));
    }


    // Method to get emissions by sector for each year (2019-2023)
    public List<CO2Emission> getMostEmissionsBySector() {
        return emissions.stream()
                .collect(Collectors.groupingBy(CO2Emission::sector))
                .entrySet().stream()
                .map(entry -> entry.getValue().stream()
                        .max(Comparator.comparing(CO2Emission::value))
                        .orElseThrow())
                .collect(Collectors.toList());
    }
    // Method to add a new CO2 emission record
    public void addEmission(CO2Emission newEmission) {
        emissions.add(newEmission);
        appendEmissionToCSV(newEmission);
    }

    // Method to append the new emission to the CSV file
    private void appendEmissionToCSV(CO2Emission emission) {
        try (FileWriter fw = new FileWriter(csvFilePath, true)) {
            String newLine = String.format("\"%s\",\"%s\",\"%s\",%f%n",
                    emission.region(), emission.date(), emission.sector(), emission.value());
            fw.append(newLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}