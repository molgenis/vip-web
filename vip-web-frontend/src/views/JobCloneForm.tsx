import { Component, Show } from "solid-js";
import { useNavigate, useRouteData } from "@solidjs/router";
import { Loader } from "../components/Loader.tsx";
import { Job } from "../api/Api.ts";
import { JobRouteData } from "./data/JobData.tsx";
import { JobCreateEvent, JobForm, JobFormData } from "../components/JobForm.tsx";
import { useStore } from "../store/store.tsx";
import api from "../api/ApiClient.ts";

function cloneJob(job: Job): JobFormData {
  return {
    name: `${job.name} (copy)`,
    vcf: job.vcf,
    sequencingMethod: job.sequencingMethod,
    assembly: job.assembly,
    samples: job.samples,
    variantFilterTree: job.variantFilterTree,
    variantFilterClassIds: job.variantFilterClassIds,
    sampleFilterTree: job.sampleFilterTree,
    sampleFilterClassIds: job.sampleFilterClassIds,
    isPublic: false,
  };
}

export const JobCloneForm: Component = () => {
  const { job } = useRouteData<JobRouteData>();
  const [state, actions] = useStore();
  const navigate = useNavigate();

  const handlePreSubmit = async () => {
    // create new user for anonymous users attempting vcf upload
    if (state.user.authorities.length === 1 && state.user.authorities[0] === "ROLE_ANONYMOUS") {
      const username = self.crypto.randomUUID();
      const password = self.crypto.randomUUID();
      await api.signup({ username, password });
      const user = await api.login({ username, password });
      actions.setUser(user);
    }
  };

  const handleSubmit = async (event: JobCreateEvent) => {
    await handlePreSubmit();

    await api.createJob(event.job);
    navigate("/jobs");
  };

  const handleCancel = () => {
    navigate(-1);
  };

  return (
    <div class="columns is-centered mt-1">
      <div class="column is-two-thirds-widescreen">
        <h1 class="title is-4">New Job</h1>
        <Show when={!job.loading} fallback={<Loader />}>
          <Show when={job()} fallback={<Loader />} keyed>
            {(job) => (
              <JobForm job={cloneJob(job)} onSubmit={(event) => void handleSubmit(event)} onCancel={handleCancel} />
            )}
          </Show>
        </Show>
      </div>
    </div>
  );
};
