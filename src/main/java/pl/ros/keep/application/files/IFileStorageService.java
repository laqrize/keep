package pl.ros.keep.application.files;

import org.springframework.core.io.InputStreamResource;
import pl.ros.keep.api.enums.StorageLocation;
import pl.ros.keep.api.images.FileData;
import pl.ros.keep.api.images.StorageResult;

public interface IFileStorageService {
    StorageResult upload(FileData file);
    InputStreamResource download(String key);
    void delete(String key);
    StorageLocation getStorageLocation();
}
