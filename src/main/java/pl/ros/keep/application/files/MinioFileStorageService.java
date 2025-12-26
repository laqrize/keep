package pl.ros.keep.application.files;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import pl.ros.keep.api.enums.StorageLocation;
import pl.ros.keep.api.images.FileData;
import pl.ros.keep.api.images.StorageResult;
import pl.ros.keep.infrastracture.config.MinioProperties;

@Slf4j
@Service
@Profile("minio")
@RequiredArgsConstructor
public class MinioFileStorageService implements IFileStorageService{
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public StorageResult upload(FileData file) {
        try {
            var putObjectRequest = PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(file.getFilename())
                    .stream(file.getContent(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();

            ObjectWriteResponse response = minioClient.putObject(putObjectRequest);
            log.info("File uploaded successfully. Response: {}", response);
            return new StorageResult(response.object());
        } catch (Exception ex ) {
            log.error("error [{}] occurred while uploading file.", ex.getMessage());
            throw new RuntimeException("Could not upload file");
        }
    }

    @Override
    public InputStreamResource download(String key) {
        try {
            GetObjectResponse inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(key)
                    .build());
            return new InputStreamResource(inputStream);
        } catch (Exception ex) {
            log.error("error [{}] occurred while download [{}] ", ex.getMessage(), key);
            throw new RuntimeException("Error: Could not download file: " + key);
        }
    }

    @Override
    public void delete(String key) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(key)
                    .build());
        } catch (Exception ex) {
            log.error("error [{}] occurred while removing [{}] ", ex.getMessage(), key);
            throw new RuntimeException("Error: Could not delete file");
        }
    }

    @Override
    public StorageLocation getStorageLocation() {
        return StorageLocation.MINIO;
    }
}
