package com.esthetic.usermicroservices.service;

import com.esthetic.usermicroservices.config.EnvConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;


@Service
public class SpaceService {
    private final S3Client s3;
    private final EnvConfig envConfig;

    @Autowired
    public SpaceService(EnvConfig envConfig) {
        this.envConfig = envConfig;
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                envConfig.getS3AccessKey(),
                envConfig.getS3SecretKey()
        );

        this.s3 = S3Client.builder()
                .endpointOverride(java.net.URI.create("https://"+envConfig.getS3Region()+".digitaloceanspaces.com")) // cambia región
                .region(Region.US_EAST_1) // región ficticia (DO usa endpoint, no AWS region real)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public String uploadFile(String fileName, String contentType, InputStream inputStream, long size) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(envConfig.getS3Bucket())
                .key(fileName)
                .contentType(contentType)
                .acl("public-read") // 🔴 para que la url sea publica
                .build();

        s3.putObject(putRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, size));

        // URL pública
        return "https://" + envConfig.getS3Bucket() + "."+envConfig.getS3Region()+".digitaloceanspaces.com/" + fileName;
    }
    public void deleteFile(String fileKey) {
        try{
            String key = extractKeyFromUrl(fileKey);
            System.out.println("url cortada "+key);
            s3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(envConfig.getS3Bucket())
                    .key(fileKey) // aquí se pasa el "path/nombreArchivo"
                    .build());
        } catch (Exception ex) {
            System.out.println("->"+ ex.getMessage());
            System.out.println("->"+ex.getStackTrace());
        }
    }
    public String extractKeyFromUrl(String fileUrl) {
        try {
            URI uri = new URI(fileUrl);
            return uri.getPath().substring(1); // quita el "/" inicial
        } catch (URISyntaxException e) {
            throw new RuntimeException("URL inválida: " + fileUrl, e);
        }
    }
}
