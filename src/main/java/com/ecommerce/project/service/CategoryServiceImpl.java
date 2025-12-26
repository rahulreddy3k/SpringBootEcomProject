package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()) {
            throw new APIException("No Category exists, please create category first");
        }

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setLastPage(categoryPage.isLast());


        return categoryResponse;
    }

//    @Override
//    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
//
//        Category category = modelMapper.map(categoryDTO,Category.class);
//        Category savedCategoryFromDB = categoryRepository.findByCategoryName(category.getCategoryName());
//        if(savedCategoryFromDB!=null){
//            throw new APIException("Category with name: "+category.getCategoryName() + " already exists!!!");
//        }
//        Category savedCategory = categoryRepository.save(category);
//        return modelMapper.map(savedCategory,CategoryDTO.class);
//    }


    //adding manual DTO -> object mapping due to ModelMapper issues.
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // Validate if the category name is already present (clear logic for duplicate handling)
        if (categoryRepository.findByCategoryName(categoryDTO.getCategoryName()) != null) {
            throw new APIException(
                    "Category with the name '" + categoryDTO.getCategoryName() + "' already exists."
            );
        }

        // Map DTO -> Entity
        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());

        // Save the new category and return as DTO
        Category savedCategory = categoryRepository.save(category);
        CategoryDTO savedCategoryDTO = new CategoryDTO(
                savedCategory.getCategoryId(),
                savedCategory.getCategoryName()
        );

        return savedCategoryDTO;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        categoryRepository.delete(category);

        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category category = modelMapper.map(categoryDTO, Category.class);

        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
