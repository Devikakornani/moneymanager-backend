package com.devika.moneymanager.service;

import com.devika.moneymanager.dto.CategoryDTO;
import com.devika.moneymanager.entity.CategoryEntity;
import com.devika.moneymanager.entity.ProfileEntity;
import com.devika.moneymanager.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    public CategoryDTO saveCategory(CategoryDTO categoryDTO){
        ProfileEntity currentProfile= profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileEntity_Id(categoryDTO.getName(),currentProfile.getId())){
            throw new RuntimeException("Category is already exists");
        }
        //creating new category
        CategoryEntity newCategory= toEntity(categoryDTO, currentProfile);
        newCategory=categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    //get categories for current user
    public List<CategoryDTO> getCategories(){
        ProfileEntity currentUser= profileService.getCurrentProfile();
        List<CategoryEntity> categories=categoryRepository.findByProfileEntity_Id(currentUser.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type){
        ProfileEntity currentUser= profileService.getCurrentProfile();
        List<CategoryEntity> categories=categoryRepository.findByTypeAndProfileEntity_Id(type,currentUser.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId,CategoryDTO categoryDTO){
        ProfileEntity currentUser= profileService.getCurrentProfile();
        CategoryEntity existingCategory= categoryRepository.findByIdAndProfileEntity_Id(categoryId, currentUser.getId())
                .orElseThrow(()-> new RuntimeException("Category Not Found"));
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setIcon(categoryDTO.getIcon());
        existingCategory=categoryRepository.save(existingCategory);
        return toDTO(existingCategory);

    }
    //delete category
    @Transactional
    public void deleteCategory(Long categoryId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository
                .findByIdAndProfileEntity_Id(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryRepository.delete(category);
    }

    // helper methods
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profileEntity){
        return CategoryEntity.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .type(categoryDTO.getType())
                .profileEntity(profileEntity)
                .build();
    }
    private CategoryDTO toDTO(CategoryEntity categoryEntity){
        return CategoryDTO.builder()
                .id(categoryEntity.getId())
                .profileId(categoryEntity.getProfileEntity() != null ? categoryEntity.getProfileEntity().getId() : null)
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .type(categoryEntity.getType())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .build();
    }
}
