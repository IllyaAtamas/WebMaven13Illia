package project13;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonPlaceholderAPI {
    private static final String API_BASE_URL = "https://jsonplaceholder.typicode.com";

    public static void main(String[] args) {
        JsonPlaceholderAPI example = new JsonPlaceholderAPI();


        example.createUser();
        example.updateUser();
        example.deleteUser();
        example.getAllUsers();
        example.getUserById(1);
        example.getUserByUsername("Bret");


        example.getCommentsForLatestPostByUser(1);


        example.getOpenTodosForUser(1);
    }




    private String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    private void sendRequestWithBody(String urlString, String method, String body) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);




        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            writer.write(body);
            writer.flush();
        }




        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            System.out.println("Request successful");
        } else {
            System.out.println("Request failed with response code: " + responseCode);
        }
    }




    //створення нового користувача
    private void createUser() {
        try {
            String url = API_BASE_URL + "/users";
            String body = "{ \"name\": \"John Doe\", \"username\": \"johndoe\", \"email\": \"johndoe@example.com\" }";

            String response = sendPostRequest(url, body);
            System.out.println("Created user: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //оновлення користувача
    private void updateUser() {
        try {
            String url = API_BASE_URL + "/users/1";
            String body = "{ \"name\": \"John Doe\", \"username\": \"johndoe\", \"email\": \"johndoe@example.com\" }";

            String response = sendPutRequest(url, body);
            System.out.println("Updated user: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //видалення користувача
    private void deleteUser() {
        try {
            String url = API_BASE_URL + "/users/1";

            sendDeleteRequest(url);
            System.out.println("User deleted successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //отримання всіх користувачів
    private void getAllUsers() {
        try {
            String url = API_BASE_URL + "/users";

            String response = sendGetRequest(url);
            System.out.println("All users: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //отримання користувача за id
    private void getUserById(int userId) {
        try {
            String url = API_BASE_URL + "/users/" + userId;

            String response = sendGetRequest(url);
            System.out.println("User with ID " + userId + ": " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //отримання користувача за username
    private void getUserByUsername(String username) {
        try {
            String url = API_BASE_URL + "/users?username=" + username;

            String response = sendGetRequest(url);
            System.out.println("User with username " + username + ": " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //отримання коментарів до останнього посту користувача і запис у файл
    private void getCommentsForLatestPostByUser(int userId) {
        try {


            //останній пост користувача
            String postsUrl = API_BASE_URL + "/users/" + userId + "/posts";
            String postsResponse = sendGetRequest(postsUrl);
            String latestPostId = getLastPostId(postsResponse);




            if (latestPostId != null) {
                String commentsUrl = API_BASE_URL + "/posts/" + latestPostId + "/comments";
                String commentsResponse = sendGetRequest(commentsUrl);

                String fileName = "user-" + userId + "-post-" + latestPostId + "-comments.json";
                writeToFile(fileName, commentsResponse);
                System.out.println("Comments saved to file: " + fileName);
            } else {
                System.out.println("No posts found for user with ID " + userId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //отримання відкритих задач для користувача за id
    private void getOpenTodosForUser(int userId) {
        try {
            String url = API_BASE_URL + "/users/" + userId + "/todos?completed=false";

            String response = sendGetRequest(url);
            System.out.println("Open todos for user with ID " + userId + ": " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String sendPostRequest(String urlString, String body) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);




        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            writer.write(body);
            writer.flush();
        }




        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    private String sendPutRequest(String urlString, String body) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            writer.write(body);
            writer.flush();
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    private void sendDeleteRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            System.out.println("Request successful");
        } else {
            System.out.println("Request failed with response code: " + responseCode);
        }
    }

    private String getLastPostId(String response) {
        String[] objects = response.split("\\},\\s*\\{");
        String lastObject = objects[objects.length - 1];
        int idIndex = lastObject.indexOf("\"id\":");
        if (idIndex != -1) {
            int commaIndex = lastObject.indexOf(",", idIndex);
            if (commaIndex != -1) {
                return lastObject.substring(idIndex + 6, commaIndex);
            }
        }
        return null;
    }

    private void writeToFile(String fileName, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        }
    }
}