package ru.practicum.ewm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор пользователя

    @Column(nullable = false, unique = true, length = 255)
    private String email; // Почтовый адрес пользователя

    @Column(nullable = false, length = 100)
    private String name; // Имя пользователя

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Дата и время создания записи

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Дата и время последнего обновления записи

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
