package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.LocationAdminDto;
import ru.practicum.ewm.service.LocationService;

import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationAdminDto> addLocation(@RequestBody @Valid LocationAdminDto locationDto) {
        log.info("Request POST /admin/locations with body: {}", locationDto);

        LocationAdminDto createdLocation = locationService.addLocation(locationDto);
        return new ResponseEntity<>(createdLocation, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LocationAdminDto>> getAllLocations() {
        log.info("Request GET /admin/locations");
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocation(@PathVariable @NotNull Long locationId) {
        log.info("Request DELETE /admin/locations/{}", locationId);

        locationService.deleteLocation(locationId);
        return ResponseEntity.noContent().build();
    }
}
