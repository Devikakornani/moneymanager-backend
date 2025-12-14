package com.devika.moneymanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private String type;
    private String icon;
    // doesn't load until we call getProfile
    // dont load the related data until it is needed
    // efficient
    // FetchType.EAGER - loads immediately, slow
    @ManyToOne(fetch = FetchType.LAZY)
    // categories shouldn't be used without category
    @JoinColumn(name="profile_id", nullable = false)
    private ProfileEntity profileEntity;

    @OneToMany(
            mappedBy = "categoryEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ExpenseEntity> expenses=new ArrayList<>();

    @OneToMany(
            mappedBy = "categoryEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<IncomeEntity> incomes=new ArrayList<>();


}
