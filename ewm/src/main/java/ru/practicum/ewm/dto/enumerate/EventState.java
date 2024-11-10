package ru.practicum.ewm.dto.enumerate;

public enum EventState {
    PENDING,   // Событие ожидает публикации
    PUBLISHED, // Событие опубликовано
    CANCELED   // Событие отменено
}
