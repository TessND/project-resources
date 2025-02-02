package com.tess.project_resources.project;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tess.project_resources.user.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByName(String name);
    List<Project> findByUser(User user);
    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Project> findByNameContaining(String search, Pageable pageable);
}