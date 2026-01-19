package com.example.template.repository;

import com.example.template.entity.ExampleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExampleRepository extends JpaRepository<ExampleEntity, Long> {
    
    Optional<ExampleEntity> findByName(String name);
    
    Page<ExampleEntity> findByStatus(String status, Pageable pageable);
    
    @Query("SELECT e FROM ExampleEntity e WHERE e.name LIKE %:searchTerm% OR e.description LIKE %:searchTerm%")
    Page<ExampleEntity> search(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    boolean existsByName(String name);
}
