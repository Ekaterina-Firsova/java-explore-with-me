package ru.practicum.ewm.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.entity.Category;

import java.util.Optional;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    // Удаление категории по ID
    @Transactional
    void deleteCategory(Long catId);

    // Обновление данных категории
    @Transactional
    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

   Category findById(Long catId);
}
