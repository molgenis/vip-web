package org.molgenis.vipweb.populate;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.VipWebProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopulatorCommandLineRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PopulatorCommandLineRunner.class);

    private final VipWebProperties properties;
    private final PopulatorService populatorService;

    @Override
    public void run(String... args) {
        VipWebProperties.Initializer initializer = properties.initializer();
        if (initializer.enabled()) {
            LOGGER.info("initializing application");
            populatorService.populate(initializer);
            LOGGER.info("done initializing application");
        }
    }
}
