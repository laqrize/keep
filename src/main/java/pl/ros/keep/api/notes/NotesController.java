package pl.ros.keep.api.notes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ros.keep.application.notes.NotesService;

import java.util.List;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NotesController {

    private final NotesService notesService;

    @GetMapping
    public List<NoteDto> getAll(){
        return notesService.getAll();
    }

    @GetMapping("/{id}")
    public NoteDto getById(@PathVariable String id){
        return notesService.findDtoById(id);
    }

    @PostMapping
    public ResponseEntity<NoteDto> create(@RequestBody NoteDto dto){
        NoteDto noteDto = notesService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(noteDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDto> update(@PathVariable String id, @RequestBody NoteDto dto){
        NoteDto noteDto = notesService.update(id, dto);
        return ResponseEntity.ok(noteDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        notesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
