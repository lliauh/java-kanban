package httpservice;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String apiToken;
    private final String serverURL;
    HttpClient client = HttpClient.newHttpClient();

    public KVTaskClient(String serverURL) {
        this.serverURL = serverURL;
        URI url = URI.create(serverURL + "register");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                apiToken = response.body();
            } else {
                System.out.println("Не получилось зарегистрироваться на сервере, код ответа: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public void put(String key, String json) {
        URI url = URI.create(serverURL + "save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public String load(String key) {
        URI url = URI.create(serverURL + "load/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Не получилось получить данные по ключу, код сервера : " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

        return null;
    }
}
