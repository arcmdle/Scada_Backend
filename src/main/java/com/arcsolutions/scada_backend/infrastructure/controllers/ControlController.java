package com.arcsolutions.scada_backend.infrastructure.controllers;

import com.arcsolutions.scada_backend.domain.services.ControlService;
import com.arcsolutions.scada_backend.infrastructure.config.ApiConfig;
import com.arcsolutions.scada_backend.infrastructure.dtos.SetpointReqDto;
import com.arcsolutions.scada_backend.infrastructure.dtos.SetpointResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/control")
@RequiredArgsConstructor
public class ControlController {
    private final ControlService controlService;

    @GetMapping("/setpoint")
    public ResponseEntity<SetpointResDto> getSetpoint() {
        return ResponseEntity.ok(controlService.getSetpoint());
    }

    @PostMapping("/setpoint")
    public ResponseEntity<Void> updateSetpoint(@RequestBody @Valid SetpointReqDto setpointReqDto) {
        controlService.updateSetpoint(setpointReqDto);
        return ResponseEntity.ok().build();
    }


}
