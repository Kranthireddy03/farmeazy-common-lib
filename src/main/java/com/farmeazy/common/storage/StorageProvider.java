package com.farmeazy.common.storage;

import org.springframework.core.io.Resource;
import java.io.InputStream;

public interface StorageProvider {
    /**
     * Uploads a file to the durable storage.
     * @param bucket The namespace/bucket.
     * @param objectKey The unique object key (e.g. vendor-documents/123/doc-uuid.pdf).
     * @param inputStream The data stream.
     * @param contentType The MIME type.
     * @param size The size of the file.
     * @return The canonical storage reference or URL.
     */
    String upload(String bucket, String objectKey, InputStream inputStream, String contentType, long size);

    /**
     * Downloads a file from durable storage.
     * @param bucket The namespace/bucket.
     * @param objectKey The unique object key.
     * @return The resource representing the file.
     */
    Resource download(String bucket, String objectKey);

    /**
     * Deletes a file from durable storage.
     * @param bucket The namespace/bucket.
     * @param objectKey The unique object key.
     */
    void delete(String bucket, String objectKey);

    /**
     * Checks if a file exists.
     * @param bucket The namespace/bucket.
     * @param objectKey The unique object key.
     * @return true if exists.
     */
    boolean exists(String bucket, String objectKey);
}
