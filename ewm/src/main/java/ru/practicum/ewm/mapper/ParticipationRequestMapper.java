package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.entity.ParticipationRequest;

@UtilityClass
public class ParticipationRequestMapper {

    public ParticipationRequestDto toDto(ParticipationRequest request) {
        if (request == null) {
            return null;
        }
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .event(request.getEvent())
                .created(request.getCreated())
                .build();
    }
}
