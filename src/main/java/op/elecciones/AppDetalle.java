package op.elecciones;

import static java.nio.file.StandardOpenOption.APPEND;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AppDetalle {

  public static void main(String[] args)
    throws IOException, NoSuchAlgorithmException, KeyManagementException {
    var trustAllCerts = new TrustManager[] {
      new X509TrustManager() {
        @Override
        public void checkClientTrusted(
          X509Certificate[] chain,
          String authType
        ) {}

        @Override
        public void checkServerTrusted(
          X509Certificate[] chain,
          String authType
        ) {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[0];
        }
      },
    };
    var sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustAllCerts, new SecureRandom());

    var httpClient = HttpClient.newBuilder().sslContext(sslContext).build();
    var json = new ObjectMapper();

    var output = Path.of("resultados-filtrados-lima.ndjson");
    if (!Files.exists(output)) Files.createFile(output);

    //var lines = Files.readAllLines(Path.of("filtrados.csv"));
    var lines = Files.readAllLines(Path.of("filtrados-lima.csv"));
    lines
      .stream()
      .dropWhile(s -> s.startsWith("idHojaVida"))
      .map(line -> {
        var fields = line.split(",");
        var idHojaVida = fields[0];
        System.out.println(idHojaVida);
        return idHojaVida;
      })
      .map(idHojaVida ->
        HttpRequest
          .newBuilder()
          .GET()
          .header("Content-type", "application/json")
          .uri(
            URI.create(
              "https://apiplataformaelectoral8.jne.gob.pe/api/v1/candidato/hoja-vida?IdHojaVida=" +
              idHojaVida
            )
          )
          .build()
      )
//      .parallel() // Too much for the backend
      .forEach(httpRequest -> {
        try {
          var response = httpClient.send(
            httpRequest,
            HttpResponse.BodyHandlers.ofByteArray()
          );
          var jsonResponse = json.readTree(response.body());
          Files.writeString(
            output,
            json.writeValueAsString(jsonResponse) + "\n",
            APPEND
          );
        } catch (IOException | InterruptedException e) {
          throw new RuntimeException(e);
        }
      });
  }
}
