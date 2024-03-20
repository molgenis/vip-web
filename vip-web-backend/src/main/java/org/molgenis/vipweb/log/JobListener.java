package org.molgenis.vipweb.log;

import org.molgenis.vipweb.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.mapping.event.AbstractRelationalEventListener;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class JobListener extends AbstractRelationalEventListener<Job> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobListener.class);

    @Override
    protected void onAfterSave(AfterSaveEvent<Job> event) {
        Job job = Objects.requireNonNull(event.getEntity());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(
                    "event.job.create:job=%d,user=%d".formatted(job.getId(), job.getCreatedBy().getId()));
        }
    }
}
