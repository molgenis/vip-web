package org.molgenis.vipweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableConfigurationProperties(VipWebProperties.class)
public class VipWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(VipWebApplication.class, args);
    }
}
