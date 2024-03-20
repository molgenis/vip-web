import { Component, createResource, For, Show } from "solid-js";
import { useNavigate } from "@solidjs/router";
import { Job, JobId } from "../api/Api.ts";
import api from "../api/ApiClient.ts";
import { useStore } from "../store/store.tsx";

export const Jobs: Component = () => {
  const navigate = useNavigate();
  const [state] = useStore();
  const [jobs, { refetch }] = createResource({}, api.fetchJobs);

  const onClone = (jobId: JobId) => {
    navigate(`/jobs/clone/${jobId}`);
  };

  const onDelete = async (id: JobId) => {
    await api.deleteJob(id);
    await refetch();
  };

  const onReportView = async (event: Event, job: Job) => {
    event.preventDefault();

    const data = await api.fetchJobReport(job.id);
    const tab = window.open("about:blank", "");
    if (tab !== null) {
      const text = await data.text();
      tab.document.write(text);
      tab.document.close();
    }
  };

  const handleCreateNewJob = () => {
    navigate(`/vcf/create`);
  };

  return (
    <>
      <div class="columns is-centered mt-1">
        <div class="column is-two-thirds-widescreen">
          <h1 class="title is-4">Recent Jobs</h1>
          <Show when={!jobs.loading}>
            <Show when={jobs()} keyed>
              {(jobs) => (
                <>
                  <div class="columns">
                    <div class="column">
                      <button class="button is-info is-pulled-right" onClick={() => void refetch()}>
                        Refresh
                      </button>
                    </div>
                  </div>
                  <div class="columns">
                    <div class="column">
                      <div style={{ display: "grid" }}>
                        {/* workaround for https://github.com/jgthms/bulma/issues/2572#issuecomment-523099776 */}
                        <div class="table-container">
                          <table class="table is-fullwidth">
                            <thead>
                              <tr>
                                <th>Job</th>
                                <th>Submitted</th>
                                <th>Status</th>
                                <th>Report</th>
                                <th>Actions</th>
                              </tr>
                            </thead>
                            <tbody>
                              <For each={jobs.content}>
                                {(job) => (
                                  <tr>
                                    <td>{job.name}</td>
                                    <td>
                                      {new Date(job.submitted).toLocaleString([], {
                                        year: "numeric",
                                        month: "2-digit",
                                        day: "2-digit",
                                        hour: "2-digit",
                                        minute: "2-digit",
                                        second: "2-digit",
                                      })}
                                    </td>
                                    <td
                                      classList={{
                                        "has-background-warning-light": job.status === "PENDING",
                                        "has-background-info-light": job.status === "RUNNING",
                                        "has-background-success-light": job.status === "COMPLETED",
                                        "has-background-danger-light": job.status === "FAILED",
                                      }}
                                    >
                                      {job.status}
                                    </td>
                                    <td>
                                      <Show when={job.report} keyed>
                                        {(report) => (
                                          <>
                                            <a
                                              href="#"
                                              class="is-underlined"
                                              onClick={(e) => void onReportView(e, job)}
                                            >
                                              {report.file.filename}
                                            </a>
                                          </>
                                        )}
                                      </Show>
                                    </td>
                                    <td>
                                      <Show when={job.report} fallback={<span class="icon" />}>
                                        <a href={`/api/job/${job.id}/report`}>
                                          <span class="icon is-left is-clickable" title="Download report">
                                            <i class="fas fa-download" />
                                          </span>
                                        </a>
                                      </Show>
                                      <span
                                        class="icon is-left is-clickable"
                                        title="Create a new job based on the data and settings of this job"
                                        onClick={() => void onClone(job.id)}
                                      >
                                        <i class="fas fa-clone" />
                                      </span>
                                      <Show when={job.isOwner || state.user.authorities.includes("ROLE_ADMIN")}>
                                        <span
                                          class="icon has-text-danger is-left is-clickable ml-3"
                                          title="Delete this job"
                                          onClick={() => void onDelete(job.id)}
                                        >
                                          <i class="fas fa-trash" />
                                        </span>
                                      </Show>
                                    </td>
                                  </tr>
                                )}
                              </For>
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="columns">
                    <div class="column">
                      <button class="button is-primary" onClick={() => handleCreateNewJob()}>
                        Create New Job
                      </button>
                    </div>
                  </div>
                </>
              )}
            </Show>
          </Show>
        </div>
      </div>
    </>
  );
};
