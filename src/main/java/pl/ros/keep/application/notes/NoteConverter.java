package pl.ros.keep.application.notes;

import org.springframework.stereotype.Component;
import pl.ros.keep.api.notes.NoteDto;
import pl.ros.keep.commons.converters.IStandardRecordConverter;
import pl.ros.keep.core.mongo.notes.Note;

@Component
public class NoteConverter implements IStandardRecordConverter<NoteDto, Note> {
    @Override
    public Note toEntity(NoteDto dto) {
        Note note = Note.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
        setCommonFieldsToEntity(dto, note);
        return note;
    }

    @Override
    public NoteDto toDto(Note entity) {
        NoteDto noteDto = NoteDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .build();
        setCommonFieldsToDTO(noteDto, entity);
        return noteDto;
    }
}
