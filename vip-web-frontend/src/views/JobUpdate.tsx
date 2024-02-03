import { Component, Show } from "solid-js";
import { useRouteData } from "@solidjs/router";
import { Loader } from "../components/Loader.tsx";
import { useNavigate } from "@solidjs/router";
import { JobRouteData } from "./data/JobData.tsx";
import { JobUpdateEvent, JobUpdateForm } from "../components/JobUpdateForm.tsx";
import api from "../api/Api.ts";

export const JobUpdate: Component = () => {
  const { job } = useRouteData<JobRouteData>();
  const navigate = useNavigate();

  const handleSubmit = async (event: JobUpdateEvent) => {
    await api.updateJob(event.job);
    navigate("/jobs");
  };

  const handleCancel = () => {
    navigate("/jobs");
  };

  return (
    <div class="columns">
      <div class="column is-half-widescreen">
        <h1 class="title">New Job</h1>
        <Show when={!job.loading} fallback={<Loader />}>
          <Show when={job()} fallback={<Loader />} keyed>
            {(job) => (
              <JobUpdateForm job={job} onSubmit={(event) => void handleSubmit(event)} onCancel={handleCancel} />
            )}
          </Show>
        </Show>
      </div>
    </div>
  );
};
