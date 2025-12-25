package pl.ros.keep.api.labels;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ros.keep.application.labels.LabelsService;

import java.util.List;

@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
public class LabelsController {

    private final LabelsService service;

    @GetMapping
    public List<LabelDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public LabelDto getById(@PathVariable Long id) {
        return service.findDtoById(id);
    }

    @PostMapping
    public ResponseEntity<LabelDto> create(@RequestBody LabelDto dto) {
        LabelDto labelDto = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(labelDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelDto> update(@PathVariable Long id, @RequestBody LabelDto dto) {
        dto.setId(id);
        LabelDto labelDto = service.update(id, dto);
        return ResponseEntity.ok(labelDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
