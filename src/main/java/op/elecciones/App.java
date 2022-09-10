package op.elecciones;

import static java.nio.file.StandardOpenOption.APPEND;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

public class App {

  public static void main(String[] args)
    throws IOException, InterruptedException, NoSuchAlgorithmException, KeyManagementException {
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

    var lines = Files.readAllLines(Path.of("candidatos.csv"));
    for (int i = 1; i < lines.size(); i++) {
      var line = lines.get(i);
      var fields = line.split(",");
      var dni = fields[0];
      System.out.println(dni);

      var request1 = json.createObjectNode().put("pageSize", 10).put("skip", 1);
      request1.set(
        "filter",
        json
          .createObjectNode()
          .put("idProcesoElectoral", 113)
          .put("numeroDocumento", dni)
      );
      var response = httpClient.send(
        HttpRequest
          .newBuilder()
          .POST(
            HttpRequest.BodyPublishers.ofByteArray(
              json.writeValueAsBytes(request1)
            )
          )
          .header("Content-type", "application/json")
          .uri(
            URI.create(
              "https://apiplataformaelectoral2.jne.gob.pe/api/v1/candidato"
            )
          )
          .build(),
        HttpResponse.BodyHandlers.ofByteArray()
      );
      var jsonResponse = json.readTree(response.body());

      var data = (ArrayNode) jsonResponse.get("data");
      var first = data.get(0);

      var output = Path.of("resultados.ndjson");
      if (!Files.exists(output)) Files.createFile(output);
      Files.write(output, json.writeValueAsBytes(first), APPEND);
      Files.writeString(output, "\n", APPEND);
    }
  }
}
