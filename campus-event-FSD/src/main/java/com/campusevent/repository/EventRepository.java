package com.campusevent.repository;

import com.campusevent.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Search and filtering methods
    List<Event> findByDepartmentContainingIgnoreCase(String department);
    
    List<Event> findByTypeContainingIgnoreCase(String type);
    
    List<Event> findByEventDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT e FROM Event e WHERE " +
           "(:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:department IS NULL OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%'))) AND " +
           "(:type IS NULL OR LOWER(e.type) LIKE LOWER(CONCAT('%', :type, '%')))")
    List<Event> searchEvents(@Param("search") String search,
                             @Param("department") String department, 
                             @Param("type") String type);
}
