package main.repository;

import main.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FilesRepository extends JpaRepository<File, UUID> {
    File findByTaskId(UUID taskId);
}
