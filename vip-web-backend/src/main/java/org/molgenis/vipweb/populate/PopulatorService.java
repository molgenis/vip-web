package org.molgenis.vipweb.populate;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipweb.VipWebProperties;
import org.molgenis.vipweb.model.dto.UserDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class PopulatorService {
    private final UserDetailsService userDetailsService;
    private final UserPopulator userPopulator;
    private final JobPopulator jobPopulator;
    private final FilterTreePopulator filterTreePopulator;

    @Transactional
    public void populate(VipWebProperties.Initializer initializer) {
        UserDto adminUser = userPopulator.populate(initializer.users());
        UserDetails userDetails = userDetailsService.loadUserByUsername(adminUser.getUsername());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            Authentication adminAuthentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(adminAuthentication);

            String trees = initializer.trees();
            if (trees != null && !trees.isEmpty()) {
                populateTrees(trees);
            }

            String jobs = initializer.jobs();
            if (jobs != null && !jobs.isEmpty()) {
                populateJobs(jobs);
            }
        } finally {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private void populateTrees(String trees) {
        Path treesJson = Path.of(trees);
        filterTreePopulator.populate(treesJson);
    }

    private void populateJobs(String jobs) {
        Path jobsJson = Path.of(jobs);
        SecurityContextHolder.getContext().getAuthentication();
        jobPopulator.populate(jobsJson);
    }
}
