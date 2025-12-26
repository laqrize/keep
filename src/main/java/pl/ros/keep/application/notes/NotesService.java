package pl.ros.keep.application.notes;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import pl.ros.keep.api.images.FileDto;
import pl.ros.keep.api.labels.LabelDto;
import pl.ros.keep.api.notes.AttachLabelsRequest;
import pl.ros.keep.api.notes.CreateNoteRequest;
import pl.ros.keep.api.notes.NoteDto;
import pl.ros.keep.application.files.FileService;
import pl.ros.keep.application.labels.LabelsService;
import pl.ros.keep.commons.crud.enums.EntityStatus;
import pl.ros.keep.commons.crud.services.AbstractCustomService;
import pl.ros.keep.core.mongo.notes.Note;
import pl.ros.keep.core.mongo.notes.NoteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotesService extends AbstractCustomService<NoteDto, Note, String> {

    private final LabelsService labelsService;
    private final FileService fileService;

    @Override
    protected void setEntityFields(Note entity, NoteDto dto) {
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setImagesIds(dto.getImagesIds());
    }

    public List<NoteDto> getAll() {
        List<Note> notes = getRepository().findAllByCreatedBy_IdAndStatus(
                contextService.getCurrentUserId(),
                EntityStatus.CURRENT.getCode()
        );
        List<NoteDto> dtos = converter.toDtoList(notes);
        resolveLabels(dtos);
        resolveImages(dtos);
        return dtos;
    }

    private void resolveImages(List<NoteDto> dtos) {
        List<String> allImagesIds = dtos.stream()
                .filter(dto -> dto.getImagesIds() != null)
                .flatMap(dto -> dto.getImagesIds().stream())
                .distinct()
                .toList();

        if (allImagesIds.isEmpty()) {
            log.debug("No images found in notes");
            return;
        }

        Map<String, FileDto> filesMap = fileService.findByIds(allImagesIds).stream()
                .collect(Collectors.toMap(FileDto::getId, file -> file));

        dtos.forEach(dto -> {
            if (!CollectionUtils.isEmpty(dto.getImagesIds())) {
                dto.setImages(dto.getImagesIds().stream()
                        .map(filesMap::get)
                        .filter(img -> img != null)
                        .toList());
            }
        });
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

    @Transactional
    public NoteDto create(@NonNull CreateNoteRequest dto) {
        List<String> imagesIds = new ArrayList<>();

        if (!CollectionUtils.isEmpty(dto.images())) {
            for (String base64Image : dto.images()) {
                FileDto savedFile = processAndSaveImage(base64Image);
                imagesIds.add(savedFile.getId());
            }
        }

        NoteDto note = NoteDto.builder()
                .title(dto.title())
                .content(dto.content())
                .build();
        note.setImagesIds(imagesIds);
        return create(note);
    }

    /**
     * Processes a base64 encoded image string and saves it to storage.
     * Handles data URLs like "data:image/png;base64,iVBORw0KGgo..."
     * or raw base64 strings.
     */
    private FileDto processAndSaveImage(String base64Image) {
        String contentType = "image/png"; // default
        String base64Data = base64Image;

        // Parse data URL format: "data:image/png;base64,..."
        if (base64Image.startsWith("data:")) {
            int commaIndex = base64Image.indexOf(',');
            if (commaIndex > 0) {
                String metadata = base64Image.substring(5, commaIndex); // after "data:"
                base64Data = base64Image.substring(commaIndex + 1);

                // Extract content type (e.g., "image/png;base64" -> "image/png")
                int semicolonIndex = metadata.indexOf(';');
                if (semicolonIndex > 0) {
                    contentType = metadata.substring(0, semicolonIndex);
                } else {
                    contentType = metadata;
                }
            }
        }

        byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);

        FileDto fileDto = FileDto.builder()
                .content(imageBytes)
                .contentType(contentType)
                .size((long) imageBytes.length)
                .build();

        return fileService.create(fileDto);
    }
}
