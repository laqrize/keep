package pl.ros.keep.application.files;

import org.springframework.stereotype.Component;
import pl.ros.keep.api.enums.StorageLocation;
import pl.ros.keep.api.images.FileDto;
import pl.ros.keep.commons.converters.IStandardRecordConverter;
import pl.ros.keep.core.mongo.files.File;

import java.nio.file.FileStore;

import static pl.ros.keep.commons.utils.NpeUtils.getName;
import static pl.ros.keep.commons.utils.NpeUtils.getValue;

@Component
public class FileConverter implements IStandardRecordConverter<FileDto, File> {


    @Override
    public File toEntity(FileDto dto) {
        File file = File.builder()
                .key(dto.getImagePath())
                .contentType(dto.getContentType())
                .size(dto.getSize())
                .storageLocation(getName(dto.getStorageLocation()))
                .filename(dto.getFilename())
                .build();
        setCommonFieldsToEntity(dto, file);
        return file;
    }

    @Override
    public FileDto toDto(File entity) {
        FileDto file = FileDto.builder()
                .imagePath(entity.getKey())
                .contentType(entity.getContentType())
                .size(entity.getSize())
                .filename(entity.getFilename())
                .storageLocation(getValue(entity.getStorageLocation(), StorageLocation.class))
                .build();
        setCommonFieldsToDTO(file, entity);
        return file;
    }
}
