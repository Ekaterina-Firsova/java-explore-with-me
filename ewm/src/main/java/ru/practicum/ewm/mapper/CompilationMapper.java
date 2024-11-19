package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.entity.Compilation;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.service.EventService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventService eventService;

    public Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        if (newCompilationDto == null) {
            return null;
        }
        Compilation compilation = new Compilation();

        Set<Event> compilationEvents = events.stream()
                .filter(event -> newCompilationDto.getEvents().contains(event.getId()))
                .collect(Collectors.toSet());

        compilation.setEvents(compilationEvents);
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());

        return compilation;
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        if (compilation == null) {
            return null;
        }
        CompilationDto compilationDto = new CompilationDto();

        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());

        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .peek(event -> event.setLocationName(eventService.findLocationName(event.getLocationDto())))
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        compilationDto.setEvents(eventShortDtos);

        return compilationDto;
    }
}