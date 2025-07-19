package com.sensedia.sample.consents.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Consents Management API")
						.version("v1.0")
						.description("""
								API for managing user data usage consents, as part of a technical challenge.
								This API allows creating, reading, updating, and revoking consents,
								and includes a complete history of changes for audit purposes.
								""")
						.contact(new Contact()
								.name("Arthur Immich")
								.email("arthurimmichof@gmail.com")
								.url("https://github.com/ArthurImmich/consents"))
						.license(new License()
								.name("Apache 2.0")
								.url("http://www.apache.org/licenses/LICENSE-2.0.html")));
	}
}