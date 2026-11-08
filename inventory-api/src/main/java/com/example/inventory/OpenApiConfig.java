
package com.example.inventory;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventoryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Inventory Management API")
                        .description("REST API for managing products, stock levels, "
                                + "inventory value and inventory reports.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Inventory Management Project")));
    }
}
