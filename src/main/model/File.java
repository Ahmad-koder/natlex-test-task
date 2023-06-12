package main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "file", schema = "natlex")
@NoArgsConstructor
@AllArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    byte[] file;

    @Column(name = "task_id")
    private UUID taskId;

    String contentType;

    String name;

    public File(byte[] file, String contentType) {
        this.file = file;
        this.contentType = contentType;
    }
}
