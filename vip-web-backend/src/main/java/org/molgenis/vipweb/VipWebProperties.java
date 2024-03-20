package org.molgenis.vipweb;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vipweb")
@Builder(toBuilder = true)
public record VipWebProperties(String fsPath, Initializer initializer, Rememberme rememberme) {
    @Builder(toBuilder = true)
    public record Initializer(boolean enabled, String jobs, String trees, Users users) {
        @Builder(toBuilder = true)
        public record Users(Admin admin, Vipbot vipbot) {
            @Builder(toBuilder = true)
            public record Admin(String username, String password) {
            }

            @Builder(toBuilder = true)
            public record Vipbot(String username, String password) {
            }
        }
    }

    @Builder(toBuilder = true)
    public record Rememberme(String key) {
    }
}
