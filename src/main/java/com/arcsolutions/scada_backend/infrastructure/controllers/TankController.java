package com.arcsolutions.scada_backend.infrastructure.controllers;

import com.arcsolutions.scada_backend.domain.services.TankLevelService;
import com.arcsolutions.scada_backend.infrastructure.config.ApiConfig;
import com.arcsolutions.scada_backend.infrastructure.dtos.SensorReadingMqtt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/tank")
@RequiredArgsConstructor
public class TankController {
    private final TankLevelService tankLevelService;

    @PostMapping("/simulate")
    public ResponseEntity<String> readMqtt(@RequestBody SensorReadingMqtt readingMqtt) {
        tankLevelService.processingSensorReading(readingMqtt);
        return ResponseEntity.ok("Lectura realizada");
    }


}
