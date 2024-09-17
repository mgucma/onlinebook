package com.marek.onlinebookstore.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.marek.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.marek.onlinebookstore.dto.category.CategoryDto;
import com.marek.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.marek.onlinebookstore.model.Category;
import com.marek.onlinebookstore.service.book.BookService;
import com.marek.onlinebookstore.service.category.CategoryService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {
    public static final String NAME = "name";
    public static final String DESCRIPTION = "ok description";
    public static final Long CATEGORY_ID = 1L;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    @DisplayName("Create Category - Success")
    public void createCategory_Success() {
        CreateCategoryRequestDto requestDto = getCreateCategoryRequestDto();
        Category category = getCategory();
        CategoryDto expectedCategory = getCategoryDtoFromCategory(category);

        when(categoryService.save(any(CreateCategoryRequestDto.class)))
                .thenReturn(expectedCategory);

        CategoryDto actualCategory = categoryController.createCategory(requestDto);

        assertNotNull(actualCategory);
        assertEquals(expectedCategory.id(), actualCategory.id());
        assertEquals(expectedCategory.name(), actualCategory.name());
        assertEquals(expectedCategory.description(), actualCategory.description());
    }

    @Test
    @DisplayName("Get All Categories - Success")
    public void getAllCategories_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<CategoryDto> expectedCategories = List.of(
                getCategoryDtoFromCategory(getCategory()),
                getCategoryDtoFromCategory(getCategory())
        );

        when(categoryService.findAll(eq(pageable))).thenReturn(expectedCategories);

        List<CategoryDto> actualCategories = categoryController.getAll(pageable);

        assertNotNull(actualCategories);
        assertEquals(expectedCategories.size(), actualCategories.size());
        assertEquals(expectedCategories, actualCategories);
    }

    @Test
    @DisplayName("Get Category By ID - Success")
    public void getCategoryById_Success() {
        CategoryDto expectedCategory = getCategoryDtoFromCategory(getCategory());

        when(categoryService.getById(eq(CATEGORY_ID))).thenReturn(expectedCategory);

        CategoryDto actualCategory = categoryController.getCategoryById(CATEGORY_ID);

        assertNotNull(actualCategory);
        assertEquals(expectedCategory.id(), actualCategory.id());
        assertEquals(expectedCategory.name(), actualCategory.name());
    }

    @Test
    @DisplayName("Update Category - Success")
    public void updateCategory_Success() {
        CreateCategoryRequestDto requestDto = getCreateCategoryRequestDto();
        CategoryDto expectedCategory = getCategoryDtoFromCategory(getCategory());

        when(categoryService.update(eq(CATEGORY_ID), any(CreateCategoryRequestDto.class)))
                .thenReturn(expectedCategory);

        CategoryDto actualCategory = categoryController.updateCategory(CATEGORY_ID, requestDto);

        assertNotNull(actualCategory);
        assertEquals(expectedCategory.id(), actualCategory.id());
        assertEquals(expectedCategory.name(), actualCategory.name());
        assertEquals(expectedCategory.description(), actualCategory.description());
    }

    @Test
    @DisplayName("Delete Category - Success")
    public void deleteCategory_Success() {
        Long categoryId = 1L;
        doNothing().when(categoryService).deleteById(eq(categoryId));

        categoryController.deleteCategory(categoryId);

        Mockito.verify(categoryService).deleteById(eq(categoryId));
    }

    @Test
    @DisplayName("Get Books By Category ID - Success")
    public void getBooksByCategoryId_Success() {
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<BookDtoWithoutCategoryIds> expectedBooks = List.of(
                new BookDtoWithoutCategoryIds(1L, "Book 1", "Author 1", "ISBN123",
                        BigDecimal.valueOf(9.99), "Description 1", "cover1.jpg"),
                new BookDtoWithoutCategoryIds(2L, "Book 2", "Author 2", "ISBN456",
                        BigDecimal.valueOf(19.99), "Description 2", "cover2.jpg")
        );

        when(bookService.findByCategoryId(eq(categoryId), eq(pageable))).thenReturn(expectedBooks);

        List<BookDtoWithoutCategoryIds> actualBooks = categoryController
                .getBooksByCategoryId(categoryId, pageable);

        assertNotNull(actualBooks);
        assertEquals(expectedBooks.size(), actualBooks.size());
        assertEquals(expectedBooks, actualBooks);
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
