package ru.practicum.ewm.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.entity.Compilation;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.Location;
import ru.practicum.ewm.entity.QCompilation;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final QCompilation qCompilation = QCompilation.compilation;
    private final LocationRepository locationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {

        Set<Event> events = new HashSet<>();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
        }

        Compilation compilation = Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .events(events)
                .build();

        try {
            Compilation savedCompilation = compilationRepository.save(compilation);
            return compilationMapper.toCompilationDto(savedCompilation);
        } catch (ConflictException e) {
            throw new ConflictException("Integrity constraint has been violated.");
        }
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        compilationRepository.delete(compilation);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(updateCompilationRequest.getEvents()));
            compilation.setEvents(events);
        }

        compilationRepository.save(compilation);

        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {

        BooleanBuilder builder = new BooleanBuilder();

        if (pinned != null) {
            builder.and(qCompilation.pinned.eq(pinned));
        }

        Pageable pageable = PageRequest.of(from / size, size);

        return compilationRepository.findAll(builder, pageable)
                .stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        return compilationMapper.toCompilationDto(compilation);
    }

    private String calculateLocationNameForEvent(Event event) {
        if (event.getLocationDto() != null) {
            Pageable pageable = PageRequest.of(0, 1);
            List<Location> locations = locationRepository.findNearestByCoordinates(
                    event.getLocationDto().getLat(),
                    event.getLocationDto().getLon(),
                    pageable
            );
            return locations.isEmpty() ? null : locations.getFirst().getName();
        }
        return null;
    }
}
