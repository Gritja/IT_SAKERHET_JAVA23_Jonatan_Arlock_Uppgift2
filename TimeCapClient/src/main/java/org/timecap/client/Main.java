package org.timecap.client;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    private static final String BASE_URL = "http://localhost:8080/";
    private static String token;
    public static boolean isLoggedIn(){
        return token != null;
    }
    private static void printMenu(Scanner sr) {
        System.out.println("\nPlease input for the desired operation:");
        System.out.println("[1] Log in");
        System.out.println("[2] Register");
        System.out.println("[3] Create time capsule");
        System.out.println("[4] View time capsule");
        System.out.println("[Q] Quit");
    }
    //To-do handle in separate class
    private static void performRequest(HttpClient httpClient, String url, String method, String body) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token);

            if (!method.equals("Get")) {
                requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(body));
            } else {
                requestBuilder.GET();
            }

            HttpRequest request = requestBuilder.build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            handleResponse(response);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error performing request: " + e.getMessage());
        }
    }
    //To-do handle in separate class
    private static void handleResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (statusCode == 200 && !responseBody.isEmpty()) {
            if (token == null) {
                token = responseBody;
                System.out.println("Logged in successfully as: " + token);
            } else {
                System.out.println("Operation successful: " + responseBody);
            }
        } else {
            System.out.println("Error: " + statusCode + ". Response: " + responseBody);
        }
    }
    private static void viewCapsuleOperation(HttpClient httpClient) {
        if (!isLoggedIn()) {
            System.out.println("Please log in.");
            return;
        }
        performRequest(httpClient, BASE_URL + "capsule/view", "GET", "view");
    }
    private static void createCapsuleOperation(HttpClient httpClient, Scanner scanner) {
        if (!isLoggedIn()) {
            System.out.println("Please log in.");
            return;
        }
        System.out.println("Type a message: ");
        String message = scanner.nextLine().trim();
        JSONObject json = new JSONObject();
        json.put("message", message);
        performRequest(httpClient, BASE_URL + "capsule/create", "POST", json.toJSONString());
    }
    private static void loginOperation(HttpClient httpClient, Scanner scanner) {
        System.out.println("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.println("Enter Password: ");
        String password = scanner.nextLine().trim();

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        performRequest(httpClient, BASE_URL + "login", "POST", json.toJSONString());
    }
    private static void registerOperation(HttpClient httpClient, Scanner scanner) {
        System.out.println("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.println("Enter Password: ");
        String password = scanner.nextLine().trim();

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        performRequest(httpClient, BASE_URL + "register", "POST", json.toJSONString());
    }
    public static void main(String[] args) {

        System.out.println("TimeCap(tm) 1988 all rights reserved");
        HttpClient httpClient = HttpClient.newHttpClient();
        Scanner sr = new Scanner(System.in);
        String userInput;

        do {
            printMenu(sr);

            userInput = sr.nextLine();

            switch (userInput){
                case "1": loginOperation(httpClient, sr); break;
                case "2": registerOperation(httpClient,sr); break;
                case "3": createCapsuleOperation(httpClient,sr); break;
                case "4": viewCapsuleOperation(httpClient); break;
                default: System.out.println("Incorrect input.");
            }
        } while (!userInput.equalsIgnoreCase("q"));
    }
}