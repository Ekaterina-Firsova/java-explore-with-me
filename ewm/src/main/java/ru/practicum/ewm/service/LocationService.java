package ru.practicum.ewm.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.practicum.ewm.dto.LocationAdminDto;

import java.util.List;

public interface LocationService {
    LocationAdminDto addLocation(@Valid LocationAdminDto locationDto);

    List<LocationAdminDto> getAllLocations();

    void deleteLocation(@NotNull Long locationId);
}
