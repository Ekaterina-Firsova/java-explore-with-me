package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.LocationAdminDto;
import ru.practicum.ewm.entity.Location;

@UtilityClass
public class LocationMapper {

    public LocationAdminDto toLocationAdminDto(Location location ) {
        return LocationAdminDto.builder()
                .lon(location.getLon())
                .lat(location.getLat())
                .id(location.getId())
                .name(location.getName())
                .radius(location.getRadius())
                .build();
    }

    public Location toLocation (LocationAdminDto location) {
        return Location.builder()
                .lon(location.getLon())
                .lat(location.getLat())
                .id(location.getId())
                .name(location.getName())
                .radius(location.getRadius())
                .build();

    }
}
