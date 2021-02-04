package pl.dk.akamaibrowser;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "akamai")
class AkamaiProperties {

    private String cpCode;

    private String host;

    private String keyName;

    private String key;
}
