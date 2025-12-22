package pl.ros.keep.application.notes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ros.keep.api.notes.NoteDto;
import pl.ros.keep.application.labels.LabelsService;
import pl.ros.keep.commons.crud.enums.EntityStatus;
import pl.ros.keep.commons.crud.services.AbstractCustomService;
import pl.ros.keep.core.mongo.notes.Note;
import pl.ros.keep.core.mongo.notes.NoteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotesService extends AbstractCustomService<NoteDto, Note, String > {

    private final LabelsService labelsService;
    @Override
    protected void setEntityFields(Note entity, NoteDto dto) {
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        //TODO images and labels
    }


    public List<NoteDto> getAll() {
        List<Note> notes = getRepository().findAllByCreatedBy_IdAndStatus(contextService.getCurrentUserId(), EntityStatus.CURRENT.getCode());
        List<NoteDto> dtos = converter.toDtoList(notes);
        return dtos;
    }


    private NoteRepository getRepository() {
        return (NoteRepository) repository;
    }
}
