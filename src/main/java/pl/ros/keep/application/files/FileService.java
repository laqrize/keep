package pl.ros.keep.application.files;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import pl.ros.keep.api.images.FileData;
import pl.ros.keep.api.images.FileDto;
import pl.ros.keep.api.images.StorageResult;
import pl.ros.keep.commons.crud.enums.CrudOperation;
import pl.ros.keep.commons.crud.enums.EntityStatus;
import pl.ros.keep.commons.crud.services.AbstractCustomService;
import pl.ros.keep.core.mongo.files.File;
import pl.ros.keep.core.mongo.files.FileRepository;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static pl.ros.keep.api.images.FileContentUtil.getExtensionFromContentType;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService extends AbstractCustomService<FileDto, File, String> {
    @Autowired
    private IFileStorageService fileStorageService;

    @Override
    protected void setEntityFields(File entity, FileDto dto) {
        entity.setKey(dto.getImagePath());
        entity.setContentType(dto.getContentType());
        entity.setSize(dto.getSize());
        entity.setStorageLocation(fileStorageService.getStorageLocation().name());
    }

    @Override
    protected void validate(FileDto dto, CrudOperation mode) {
        Assert.isTrue(!CrudOperation.UPDATE.equals(mode), "File cannot be updated.");
        super.validate(dto, mode);
    }


    @Override
    public FileDto create(FileDto dto) {
        Assert.notNull(dto.getContent(), "File content must not be null");
        Assert.notNull(dto.getContentType(), "Content type must not be null");

        String extension = getExtensionFromContentType(dto.getContentType());
        String filename = UUID.randomUUID() + extension;

        FileData data = FileData.of(dto, filename);

        StorageResult result = fileStorageService.upload(data);
        log.info("File uploaded to storage: {}", result.path());

        dto.setContent(null);
        dto.setImagePath(result.path());
        dto.setSize(data.getSize());
        dto.setFilename(filename);
        dto.setStorageLocation(fileStorageService.getStorageLocation());

        return super.create(dto);
    }

    @Override
    public void delete(@NonNull String id) {
        FileDto dto = findDtoById(id);
        if (dto != null && dto.getImagePath() != null) {
            fileStorageService.delete(dto.getImagePath());
            log.info("File deleted from storage: {}", dto.getImagePath());
        }
        super.delete(id);
    }

    public FileDownloadResult download(@NonNull String id) {
        FileDto dto = findDtoById(id);
        Assert.notNull(dto, "File not found: " + id);
        Assert.notNull(dto.getImagePath(), "File has no storage path: " + id);

        var resource = fileStorageService.download(dto.getImagePath());
        return new FileDownloadResult(resource, dto.getContentType());
    }

    public List<FileDto> findByIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        return converter.toDtoList(getRepository().findAllByIdInAndStatus(ids, EntityStatus.CURRENT.getCode()));
    }

    public record FileDownloadResult(
            InputStreamResource resource,
            String contentType
    ) {}

    private FileRepository getRepository() {
        return (FileRepository) repository;
    }
}
