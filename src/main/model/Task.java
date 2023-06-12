package main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.dictionaries.TaskStatus;
import main.model.dictionaries.TaskType;

import java.util.UUID;

@Entity
@Table(name = "task", schema = "natlex")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus taskStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "taskType")
    private TaskType taskType;
}
