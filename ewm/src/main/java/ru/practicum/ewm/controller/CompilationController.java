package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;

import ru.practicum.ewm.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationController {
    private final CompilationService compilationService;

    @PostMapping("/admin/compilations")
    public ResponseEntity<CompilationDto> saveCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Request POST /admin/compilations/ with body {}", newCompilationDto);

        CompilationDto savedCompilation = compilationService.createCompilation(newCompilationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCompilation);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compId) {
        log.info("Request DELETE /admin/compilations/{}", compId);

        compilationService.deleteCompilation(compId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/compilations/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(
            @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Request PATCH /admin/compilations/{} to update compilation", compId);

        CompilationDto updatedCompilation = compilationService.updateCompilation(compId, updateCompilationRequest);

        return ResponseEntity.ok(updatedCompilation);
    }

    @GetMapping("/compilations")
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        // Вызов метода сервиса для получения подборок
        List<CompilationDto> compilations = compilationService.getCompilations(pinned, from, size);
        return ResponseEntity.ok(compilations);
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable Long compId) {
        CompilationDto compilationDto = compilationService.getCompilationById(compId);
        return ResponseEntity.ok(compilationDto);
    }
}
