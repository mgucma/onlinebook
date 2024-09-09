package com.marek.onlinebookstore.service.category;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.marek.onlinebookstore.dto.category.CategoryDto;
import com.marek.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.marek.onlinebookstore.exception.EntityNotFoundException;
import com.marek.onlinebookstore.mapper.CategoryMapper;
import com.marek.onlinebookstore.model.Category;
import com.marek.onlinebookstore.repository.category.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    public static final String NAME = "name";
    public static final String DESCRIPTION = "ok description";

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Verify findAll verify pageable")
    public void findAll_VerifyPageable_ReturnsList() {
        Category category = getCategory();
        List<Category> categories = List.of(category);
        CategoryDto expected = getCategoryDtoFromCategory(category);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        List<CategoryDto> actual = categoryService.findAll(pageable);

        Assertions.assertEquals(expected, actual.get(0));
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify getById method with correct id should return")
    public void getById_withCorrectId_returnsCategoryDto() {
        Category category = getCategory();
        Long categoryId = category.getId();
        CategoryDto expected = getCategoryDtoFromCategory(category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.getById(categoryId);

        Assertions.assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify getById method without id should throw EntityNotFoundException")
    public void getById_withoutCorrectId_throwException() {
        Long categoryId = -123L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));

        Assertions.assertEquals("Category with id " + categoryId + " not found",
                exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify save method with correct id should return CategoryDto")
    public void save_correctInput_ReturnsCategoryDto() {
        CreateCategoryRequestDto requestDto = getCreateCategoryRequestDto();
        Category category = getCategory();
        CategoryDto expected = getCategoryDtoFromCategory(category);

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.save(requestDto);

        Assertions.assertEquals(expected, actual);
        verify(categoryMapper, times(1)).toEntity(requestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify update method with correct id should return CategoryDto")
    public void update_withCorrectId_returnsCategoryDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(NAME, DESCRIPTION);
        Category category = getCategory();
        Long categoryId = category.getId();
        CategoryDto expected = getCategoryDtoFromCategory(category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.update(categoryId, requestDto);

        Assertions.assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toEntity(requestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify update method without correct id should throw EntityNotFoundException")
    public void update_withoutCorrectId_throwException() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(NAME, DESCRIPTION);
        Long categoryId = -123L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(categoryId, requestDto));

        Assertions.assertEquals("Category with id " + categoryId + " not found",
                exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify delete method with correct id should delete Category from DB")
    public void deleteById_withCorrectId_deleteCategoryFromDB() {
        Category category = getCategory();
        Long categoryId = category.getId();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        categoryService.deleteById(categoryId);

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));

        Assertions.assertEquals("Category with id " + categoryId + " not found",
                exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    private static @NotNull Category getCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName(NAME);
        category.setDescription(DESCRIPTION);
        category.setDeleted(false);
        return category;
    }

    private static @NotNull CreateCategoryRequestDto getCreateCategoryRequestDto() {
        return new CreateCategoryRequestDto(NAME, DESCRIPTION);
    }

    private static @NotNull CategoryDto getCategoryDtoFromCategory(Category category) {
        return new CategoryDto(category.getId(), category.getName(), category.getDescription());
    }
}
