

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Configurator {
    private static Configurator ourInstance = new Configurator();

    public static Configurator getInstance() {
        return ourInstance;
    }

    private Configurator() {
    }

    public void config() throws IOException {
        String url = "http://localhost/simpleQuiz/rest/config";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("PUT");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
    }
}
