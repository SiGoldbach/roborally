package dk.dtu.compute.se.pisd.roborally.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class ServerClientController {
    HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10)).build();

    public String getBoard(String boardName) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/boards/loads/" + boardName)).
                setHeader("User-Agent", "Java 17 HttpClient Bot").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        return response.body();

    }

    public List<String> possibleBoards() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/boards/getboards")).
                setHeader("User-Agent", "Java 17 HttpClient Bot").build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper=new ObjectMapper();
        String ar[]=objectMapper.readValue(response.body(),String[].class);

        return Arrays.asList(ar);
    }

    public void saveBoard(String board, String boardName) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/boards/saveboard/" + boardName))
                .POST(HttpRequest.BodyPublishers.ofString(board)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());


    }
    public String startGame() throws IOException, InterruptedException {
        String board="true-name2-player1-3";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/gamehandler/connect"))
                .POST(HttpRequest.BodyPublishers.ofString(board)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();

    }


}
