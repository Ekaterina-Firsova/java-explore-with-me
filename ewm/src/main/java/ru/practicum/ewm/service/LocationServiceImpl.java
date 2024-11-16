package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.LocationAdminDto;
import ru.practicum.ewm.entity.Location;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.repository.LocationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

   @Override
   @Transactional
    public LocationAdminDto addLocation(LocationAdminDto locationDto) {
       try {
           Location location = LocationMapper.toLocation(locationDto);
           return LocationMapper.toLocationAdminDto(locationRepository.save(location));
       } catch (DataIntegrityViolationException e) {
           throw new ConflictException("Location with the same name or coordinates already exists.");
       }
   }

    @Override
    public List<LocationAdminDto> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(LocationMapper::toLocationAdminDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLocation(Long locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new NotFoundException("Location with id=" + locationId + " was not found");
        }

        locationRepository.deleteById(locationId);
    }
}
