package pl.dk.akamaibrowser;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import pl.dk.akamaibrowser.model.Stat;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Base64;

import static org.springframework.util.StringUtils.isEmpty;

@Component
class AkamaiBrowser {

    private final WebClient webClient;
    private StatUnmarshaller statUnmarshaller;
    private AkamaiProperties akamaiProperties;

    AkamaiBrowser(WebClient.Builder webClientBuilder, StatUnmarshaller statUnmarshaller, AkamaiProperties akamaiProperties) {
        webClient = webClientBuilder.build();
        this.statUnmarshaller = statUnmarshaller;
        this.akamaiProperties = akamaiProperties;
    }

    Mono<Stat> dir(String path) {
        String url = "/" + akamaiProperties.getCpCode();
        if (!isEmpty(path)) {
            url += "/" + path;
        }
        String acsAction = "action=dir&format=xml&version=1";
        String acsAuthDataHeader = acsAuthDataHeader();
        String acsAuthSignHeader = acsAuthSignHeader(url, acsAction, acsAuthDataHeader);
        return webClient.get()
                                   .uri(UriComponentsBuilder.newInstance()
                                                            .host(akamaiProperties.getHost())
                                                            .scheme("http")
                                                            .path(url)
                                                            .build()
                                                            .toUri()
                                   )
                                   .header("X-Akamai-ACS-Action", acsAction)
                                   .header("X-Akamai-ACS-Auth-Data", acsAuthDataHeader)
                                   .header("X-Akamai-ACS-Auth-Sign", acsAuthSignHeader)
                                   .retrieve()
                                   .bodyToMono(String.class)
            .map(response -> {
                System.out.println("------------------------ ");
                System.out.println(response);
                System.out.println("------------------------ ");
                return statUnmarshaller.unmarshall(response);
            });
    }

    private String acsAuthDataHeader() {
        long time = System.currentTimeMillis() / 1000;
        return "5, 0.0.0.0, 0.0.0.0, " + time + ", 325228449, " + akamaiProperties.getKeyName(); // TODO change me to unique random id
        // https://learn.akamai.com/en-us/webhelp/netstorage/netstorage-http-api-developer-guide/GUID-238D3CA6-4933-4AF6-BDB6-912BBCD7AAF1.html
    }

    private String acsAuthSignHeader(String url, String acsAction, String acsAuthDataHeader) {
        String signature = acsAuthDataHeader +
            url + "\n" +
            "x-akamai-acs-action:" + acsAction + "\n";
        byte[] signatureEncoded = calcHmacSha256(akamaiProperties.getKey(), signature);
        return Base64.getEncoder().encodeToString(signatureEncoded);
    }

    private byte[] calcHmacSha256(String secretKey, String message) {
        byte[] hmacSha256;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(Charset.forName("UTF-8")), "HmacSHA256");
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(message.getBytes(Charset.forName("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return hmacSha256;
    }
}
