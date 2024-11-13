package ru.practicum.ewm.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    // Добавление новой категории
    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Имя категории должно быть уникальным");
        }
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        Category savedCategory = categoryRepository.save(category);
        return new CategoryDto(savedCategory.getId(), savedCategory.getName());
    }

    // Удаление категории по ID
    @Transactional
    @Override
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        if (categoryHasEvents(category)) {
            throw new ConflictException("The category is not empty");
        }
        categoryRepository.delete(category);
    }

    // Обновление данных категории
    @Transactional
    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        if (!category.getName().equals(categoryDto.getName()) && categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("Имя категории должно быть уникальным");
        }

        category.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);
        return new CategoryDto(updatedCategory.getId(), updatedCategory.getName());
    }

    // Проверка, связана ли категория с событиями
    private boolean categoryHasEvents(Category category) {
        // Здесь добавьте логику проверки на связь категории с событиями, например, через дополнительный метод репозитория
        return false; // Временно возвращаем false для примера
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с таким ID не найдена"));
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        BooleanBuilder builder = new BooleanBuilder();

        return categoryRepository.findAll(builder, pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}
