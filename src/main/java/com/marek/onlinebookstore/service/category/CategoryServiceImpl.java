package com.marek.onlinebookstore.service.category;

import com.marek.onlinebookstore.dto.category.CategoryDto;
import com.marek.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.marek.onlinebookstore.exception.EntityNotFoundException;
import com.marek.onlinebookstore.mapper.CategoryMapper;
import com.marek.onlinebookstore.model.Category;
import com.marek.onlinebookstore.repository.category.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryMapper.toDto(
                categoryRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Category with id " + id + " not found")
                )
        );
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(
            Long id,
            CreateCategoryRequestDto categoryDto) {
        if (!categoryRepository.findById(id).isPresent()) {
            throw new EntityNotFoundException("Category with id " + id + " not found");
        }
        Category category = categoryMapper.toEntity(categoryDto);
        category.setId(id);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
