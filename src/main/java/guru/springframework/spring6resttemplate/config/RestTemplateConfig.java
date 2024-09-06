package guru.springframework.spring6resttemplate.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

	private static final Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);

	@Value("${rest.template.rootUrl}")
	public String BASE_URL;

	@Value("${rest.template.authentication.username}")
	private String username;

	@Value("${rest.template.authentication.password}")
	private String password;

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
			.setConnectTimeout(Duration.ofSeconds(5))
			.setReadTimeout(Duration.ofSeconds(5))
			.rootUri(BASE_URL)
			.basicAuthentication(username, password)
			.build();
	}
}
