package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.Section;
import main.repository.SectionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sections")
public class SectionController {
    private final SectionRepository sectionRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<String> createSection(@RequestBody Section section) {
        sectionRepository.save(section);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<Section>> getAllSections() {
        final var sections = sectionRepository.findAll();
        return ResponseEntity.ok(sections);
    }

    @PutMapping("/{sectionId}")
    @Transactional
    public ResponseEntity<Void> updateSectionById(@PathVariable UUID sectionId, @RequestBody Section updatedSection) {
        sectionRepository.findById(sectionId).ifPresent(section -> {
            section.setName(updatedSection.getName());
            section.setGeologicalClasses(updatedSection.getGeologicalClasses());
            sectionRepository.save(section);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSectionById(@PathVariable UUID sectionId) {
        sectionRepository.findById(sectionId).ifPresent(sectionRepository::delete);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/by-code")
    public ResponseEntity<List<Section>> getSectionsByGeologicalClassCode(@RequestParam("code") String code) {
        final var sections = sectionRepository.findByGeologicalClassesCode(code);
        return new ResponseEntity<>(sections, HttpStatus.OK);
    }
}
