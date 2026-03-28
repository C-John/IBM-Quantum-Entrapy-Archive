package com.calder.archive;

import org.apache.hadoop.conf.Configuration;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * IBM Quantum Entropy Archive - Cleaning Engine
 * Copyright (c) 2026 Calder Henry. All rights reserved.
 */
public class ArchiveEngine {

    private static final String IBM_AUTH_URL = "https://iam.cloud.ibm.com/identity/token";
    public static void main(String[] args) {

        Configuration conf = new Configuration();
        
        Properties prop = new Properties();
        String apiKey = null;

        System.out.println("============================================");
        System.out.println("Initializing IBM Quantum Entropy Archive...");
        System.out.println("============================================");
        
        // Testing if the Hadoop dependency from pom.xml is active
        if (conf != null){
            System.out.println("Environment Status: Hadoop Configuration Loaded.");            
            System.out.println("\nReady to process high-entropy data.");
        }
        
        // This "opens" the file and "loads" the keys into memory
        try (FileInputStream envFile = new FileInputStream(".env")) {
            prop.load(envFile);
            
            // This grabs your specific key
            apiKey = prop.getProperty("IBM_CLOUD_API_KEY");
            
            if (apiKey != null) {
                System.out.println("Success: API Key loaded from .env without external libraries.");
            }
        } catch (IOException e) {
            System.err.println("Error: Could not find or read the .env file.");
        }

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(IBM_AUTH_URL))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(
                "grant_type=urn:ibm:params:oauth:grant-type:apikey&apikey=" + apiKey
            ))
            .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(System.out::println)
            .join();
    }
}