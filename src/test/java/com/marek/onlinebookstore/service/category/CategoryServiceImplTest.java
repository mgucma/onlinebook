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
    @DisplayName("Find all categories with pagination - verify pageable")
    public void findAll_VerifyPageable_ReturnsList() {
        // Given
        Category category = getCategory();
        List<Category> categories = List.of(category);
        CategoryDto expected = getCategoryDtoFromCategory(category);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        // When
        List<CategoryDto> actual = categoryService.findAll(pageable);

        // Then
        Assertions.assertEquals(expected, actual.get(0));
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get category by valid ID - returns CategoryDto")
    public void getById_withCorrectId_returnsCategoryDto() {
        // Given
        Category category = getCategory();
        Long categoryId = category.getId();
        CategoryDto expected = getCategoryDtoFromCategory(category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        // When
        CategoryDto actual = categoryService.getById(categoryId);

        // Then
        Assertions.assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get category by invalid ID - throws EntityNotFoundException")
    public void getById_withoutCorrectId_throwException() {
        // Given
        Long categoryId = -123L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));

        // Then
        Assertions.assertEquals("Category with id " + categoryId
                + " not found", exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Save category with valid input - returns CategoryDto")
    public void save_correctInput_ReturnsCategoryDto() {
        // Given
        CreateCategoryRequestDto requestDto = getCreateCategoryRequestDto();
        Category category = getCategory();
        CategoryDto categoryDto = getCategoryDtoFromCategory(category);

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        // When
        CategoryDto actual = categoryService.save(requestDto);

        // Then
        Assertions.assertEquals(categoryDto, actual);
        verify(categoryMapper, times(1)).toEntity(requestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Update category by valid ID - returns updated CategoryDto")
    public void update_withCorrectId_returnsCategoryDto() {
        // Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(NAME, DESCRIPTION);
        Category category = getCategory();
        Long categoryId = category.getId();
        CategoryDto expected = getCategoryDtoFromCategory(category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        // When
        CategoryDto actual = categoryService.update(categoryId, requestDto);

        // Then
        Assertions.assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toEntity(requestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Update category by invalid ID - throws EntityNotFoundException")
    public void update_withoutCorrectId_throwException() {
        // Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(NAME, DESCRIPTION);
        Long categoryId = -123L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(categoryId, requestDto));

        // Then
        Assertions.assertEquals("Category with id " + categoryId
                + " not found", exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Delete category by valid ID - category removed from DB")
    public void deleteById_withCorrectId_deleteCategoryFromDB() {
        // Given
        Category category = getCategory();
        Long categoryId = category.getId();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When
        categoryService.deleteById(categoryId);

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));

        // Then
        Assertions.assertEquals("Category with id " + categoryId
                + " not found", exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    private static Category getCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName(NAME);
        category.setDescription(DESCRIPTION);
        category.setDeleted(false);
        return category;
    }

    private static CreateCategoryRequestDto getCreateCategoryRequestDto() {
        return new CreateCategoryRequestDto(NAME, DESCRIPTION);
    }

    private static CategoryDto getCategoryDtoFromCategory(Category category) {
        return new CategoryDto(category.getId(), category.getName(), category.getDescription());
    }
}
