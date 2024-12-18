package com.enicarthage.GraphQlService;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class EmissionController {

    private final EmissionService emissionService;

    public EmissionController(EmissionService emissionService) {
        this.emissionService = emissionService;
    }

    // Query to fetch the emission with the highest CO2 emissions by region
    @QueryMapping
    public CO2Emission getMostEmissionsByRegion() {
        return emissionService.getMostEmissionsByRegion();
    }

    // Query to fetch the emission with the highest CO2 emissions by sector
    @QueryMapping
    public List<CO2Emission> getMostEmissionsBySector() {
        return emissionService.getMostEmissionsBySector();
    }
    @MutationMapping
    public CO2Emission addEmission(@Argument String region,
                              @Argument String date,
                              @Argument String sector,
                              @Argument Float value) {
        CO2Emission newEmission = new CO2Emission(region, date, sector, value);
        emissionService.addEmission(newEmission);
        return newEmission;
    }
}
