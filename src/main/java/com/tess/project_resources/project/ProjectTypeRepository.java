package com.tess.project_resources.project;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectTypeRepository extends JpaRepository<ProjectType, Long> {

    Optional<ProjectType> findByName(String name);
    // Дополнительные методы, если необходимо
}