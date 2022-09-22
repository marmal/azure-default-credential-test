package no.mma;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ClientSecretCredentialTest {
    public static void main(String[] args) throws IOException {
        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId("50832492-de6d-434e-92ba-d8be534fa13d")
                .clientSecret("QB98Q~hADLNY6051RtfBIKW99nw0VHT2Tc1q~a-5")
                .tenantId("44991835-87d7-4239-9df6-fc667fce766e")
                .build();

        String accountName = "teststracnt";
        String containerName = "teststrcont";

        String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName);

        BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(endpoint).credential(clientSecretCredential).buildClient();
        BlobContainerClient blobContainerClient = storageClient.getBlobContainerClient(containerName);
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }

        String name = "testblob";
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
