package com.farmeazy.common.storage;

import com.farmeazy.common.exception.ServiceUnavailableException;
import com.farmeazy.common.logging.AppLogger;
import com.farmeazy.common.logging.LogEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;

@Component
public class SupabaseStorageProvider implements StorageProvider {

    private final String supabaseUrl;
    private final String serviceRoleKey;
    private final RestTemplate restTemplate;

    public SupabaseStorageProvider(
            @Value("${supabase.url:}") String supabaseUrl,
            @Value("${supabase.service-role-key:}") String serviceRoleKey) {
        this.supabaseUrl = supabaseUrl;
        this.serviceRoleKey = serviceRoleKey;
        this.restTemplate = new RestTemplate();
    }

    private void checkConfig() {
        if (supabaseUrl == null || supabaseUrl.isBlank() || serviceRoleKey == null || serviceRoleKey.isBlank()) {
            AppLogger.error(LogEvent.SYSTEM_ERROR, "Supabase storage provider is not configured properly", "Missing URL or Service Role Key");
            throw new ServiceUnavailableException("Production storage is not configured.");
        }
    }

    @Override
    public String upload(String bucket, String objectKey, InputStream inputStream, String contentType, long size) {
        checkConfig();
        String url = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucket, objectKey);
        
        try {
            byte[] body = StreamUtils.copyToByteArray(inputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(serviceRoleKey);
            headers.setContentType(MediaType.parseMediaType(contentType));
            
            HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                AppLogger.info(LogEvent.BUSINESS_LOGIC, "Uploaded object to Supabase Storage", "bucket=" + bucket + " objectKey=" + objectKey);
                return objectKey;
            } else {
                throw new ServiceUnavailableException("Failed to upload object: " + response.getStatusCode());
            }
        } catch (IOException e) {
            throw new ServiceUnavailableException("Failed to read input stream for upload: " + e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new ServiceUnavailableException("Storage API error: " + e.getStatusCode());
        }
    }

    @Override
    public Resource download(String bucket, String objectKey) {
        checkConfig();
        String url = String.format("%s/storage/v1/object/authenticated/%s/%s", supabaseUrl, bucket, objectKey);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(serviceRoleKey);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return new ByteArrayResource(response.getBody());
            }
            throw new ServiceUnavailableException("Failed to download object: " + response.getStatusCode());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null; // Return null if not found
            }
            throw new ServiceUnavailableException("Storage API error on download: " + e.getStatusCode());
        }
    }

    @Override
    public void delete(String bucket, String objectKey) {
        checkConfig();
        String url = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucket, objectKey);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(serviceRoleKey);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
            AppLogger.info(LogEvent.BUSINESS_LOGIC, "Deleted object from Supabase Storage", "bucket=" + bucket + " objectKey=" + objectKey);
        } catch (HttpClientErrorException e) {
            AppLogger.warn(LogEvent.SYSTEM_ERROR, "Failed to delete object from storage", "objectKey=" + objectKey + " error=" + e.getMessage());
        }
    }

    @Override
    public boolean exists(String bucket, String objectKey) {
        checkConfig();
        // Supabase has an API to get object info, but we can do a HEAD request.
        String url = String.format("%s/storage/v1/object/info/public/%s/%s", supabaseUrl, bucket, objectKey);
        // We use authenticated info if bucket is private
        String authUrl = String.format("%s/storage/v1/object/authenticated/%s/%s", supabaseUrl, bucket, objectKey);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(serviceRoleKey);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Void> response = restTemplate.exchange(authUrl, HttpMethod.HEAD, entity, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            return false;
        }
    }
}
