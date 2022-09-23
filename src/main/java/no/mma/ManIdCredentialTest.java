package no.mma;

import com.azure.identity.ManagedIdentityCredential;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

public class ManIdCredentialTest {
    public static void main(String[] args) throws IOException {

        // This probably will not work locally, but it will work in Azure
        ManagedIdentityCredential managedIdentityCredential = new ManagedIdentityCredentialBuilder()
                .clientId("d47eb562-2236-44ce-a2a9-8b2219b0ea2e")
                .maxRetry(1)
                .retryTimeout(duration -> Duration.ofMinutes(1))
                .build();

        String accountName = "teststracnt";
        String containerName = "teststrcont";

        String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName);

        BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(endpoint).credential(managedIdentityCredential).buildClient();
        BlobContainerClient blobContainerClient = storageClient.getBlobContainerClient(containerName);
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }

        String name = "testblob2";
        String content = "Hello World!";
        BlockBlobClient blobClient = blobContainerClient.getBlobClient(name).getBlockBlobClient();
        InputStream dataStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        blobClient.upload(dataStream, content.length());

        dataStream.close();
        blobClient = blobContainerClient.getBlobClient(name).getBlockBlobClient();
        int dataSize = (int) blobClient.getProperties().getBlobSize();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(dataSize);
        blobClient.download(outputStream);
        outputStream.close();

        String out = String.format("Successfully got the content of blob %s from storage account %s container %s: %s",
                name, accountName, containerName, new String(outputStream.toByteArray()));

        System.out.println(out);
    }
}
