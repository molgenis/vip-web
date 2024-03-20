package org.molgenis.vipweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableConfigurationProperties(VipWebProperties.class)
public class VipWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(VipWebApplication.class, args);
    }
}
