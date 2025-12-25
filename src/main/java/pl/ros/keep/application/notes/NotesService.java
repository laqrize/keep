package pl.ros.keep.application.notes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pl.ros.keep.api.labels.LabelDto;
import pl.ros.keep.api.notes.AttachLabelsRequest;
import pl.ros.keep.api.notes.NoteDto;
import pl.ros.keep.application.labels.LabelsService;
import pl.ros.keep.commons.crud.enums.EntityStatus;
import pl.ros.keep.commons.crud.services.AbstractCustomService;
import pl.ros.keep.core.mongo.notes.Note;
import pl.ros.keep.core.mongo.notes.NoteRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotesService extends AbstractCustomService<NoteDto, Note, String> {

    private final LabelsService labelsService;

    @Override
    protected void setEntityFields(Note entity, NoteDto dto) {
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
//        entity.setLabelIds(dto.getLabelIds());
//        entity.setImagesIds(dto.getImagesIds());
    }

    public List<NoteDto> getAll() {
        List<Note> notes = getRepository().findAllByCreatedBy_IdAndStatus(
                contextService.getCurrentUserId(),
                EntityStatus.CURRENT.getCode()
        );
        List<NoteDto> dtos = converter.toDtoList(notes);
        resolveLabels(dtos);
        return dtos;
    }

    @Override
    public NoteDto findDtoById(String id) {
        NoteDto dto = super.findDtoById(id);
        resolveLabels(dto);
        return dto;
    }

    private void resolveLabels(NoteDto dto) {
        if (dto.getLabelIds() != null && !dto.getLabelIds().isEmpty()) {
            dto.setLabels(labelsService.findByIds(dto.getLabelIds()));
        }
    }

    private void resolveLabels(List<NoteDto> dtos) {
        List<Long> allLabelIds = dtos.stream()
                .filter(dto -> dto.getLabelIds() != null)
                .flatMap(dto -> dto.getLabelIds().stream())
                .distinct()
                .toList();

        if (allLabelIds.isEmpty()) {
            return;
        }

        Map<Long, LabelDto> labelMap = labelsService.findByIds(allLabelIds).stream()
                .collect(Collectors.toMap(LabelDto::getId, label -> label));

        dtos.forEach(dto -> {
            if (dto.getLabelIds() != null && !dto.getLabelIds().isEmpty()) {
                dto.setLabels(dto.getLabelIds().stream()
                        .map(labelMap::get)
                        .filter(label -> label != null)
                        .toList());
            }
        });
    }

    public NoteDto attachLabels(String id, AttachLabelsRequest request) {
        Assert.notNull(request.labelIds(), "Label ids must not be null");
        List<LabelDto> labels = labelsService.findByIds(request.labelIds());
        Assert.isTrue(labels.size() == request.labelIds().size(), "Some labels do not exist. Invalid request");
        Note note = findById(id);
        note.setLabelIds(request.labelIds());
        repository.save(note);
        return findDtoById(note.getId());
    }


    public void deletePermanent(@NonNull String id) {
        Note entity = findById(id);
        getRepository().delete(entity);
        log.info("Entity ({}) with id {} deleted permanently", getEntityClass().getSimpleName(), id);
    }

    private NoteRepository getRepository() {
        return (NoteRepository) repository;
    }
}
