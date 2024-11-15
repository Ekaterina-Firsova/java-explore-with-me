package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateResult {

    private List<ParticipationRequestDto> confirmedRequests; //Подтвержденные событи

    private List<ParticipationRequestDto> rejectedRequests; //Неподтвержденные события
}
