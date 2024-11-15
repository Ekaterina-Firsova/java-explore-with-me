package ru.practicum.ewm.service;

import jakarta.validation.constraints.Min;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.entity.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    Category findById(Long catId);

    List<CategoryDto> getCategories(@Min(0) int from, @Min(1) int size);
}
