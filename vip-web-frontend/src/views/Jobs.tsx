import { Component, createResource, For, Show } from "solid-js";
import { Loader } from "../components/Loader.tsx";
import { VcfUpload, VcfUploadEvent } from "../components/VcfUpload.tsx";
import { useNavigate } from "@solidjs/router";
import api, { Job, JobResourceId, JobStatus } from "../api/Api.ts";

export const Jobs: Component = () => {
  const navigate = useNavigate();

  const [jobs, { refetch }] = createResource({}, api.fetchJobs);

  const onUpload = async (event: VcfUploadEvent) => {
    const vcf = await api.fetchVcf(event.vcfId);
    const id = await api.createJob({ vcf: vcf });
    navigate(`/jobs/${id}`);
  };

  const onClone = async (job: Job) => {
    const jobId = await api.cloneJob(job);
    navigate(`/jobs/${jobId}`);
  };

  const onDelete = async (id: JobResourceId) => {
    await api.deleteJob(id);
    await refetch();
  };

  return (
    <>
      <h1 class="title">Jobs</h1>
      <VcfUpload accept=".vcf,.vcf.bgz,.vcf.gz,.bcf,.bcf.bgz,.bcf.gz" onUpload={(event) => void onUpload(event)} />

      <Show when={!jobs.loading} fallback={<Loader />}>
        <Show when={jobs()} keyed>
          {(jobs) => (
            <Show when={jobs.length > 0}>
              <hr />
              <div style={{ display: "grid" }}>
                {/* workaround for https://github.com/jgthms/bulma/issues/2572#issuecomment-523099776 */}
                <div class="table-container">
                  <table class="table is-narrow">
                    <thead>
                      <tr>
                        <th>File</th>
                        <th>Submitted</th>
                        <th>Status</th>
                        <th>Result</th>
                        <th />
                      </tr>
                    </thead>
                    <tbody>
                      <For each={jobs}>
                        {(job) => (
                          <tr>
                            <td>{job.vcf.name}</td>
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
                            <td>
                              <span
                                classList={{
                                  "has-background-info":
                                    job.status === JobStatus.CREATED ||
                                    job.status === JobStatus.SUBMITTED ||
                                    job.status === JobStatus.PENDING ||
                                    job.status === JobStatus.RUNNING,
                                  "has-background-success": job.status === JobStatus.COMPLETED,
                                  "has-background-danger": job.status === JobStatus.FAILED,
                                }}
                              >
                                {job.status}
                              </span>
                            </td>
                            <td>{job.report}</td>
                            <td>
                              <span class="icon is-left is-clickable" onClick={() => void onClone(job)}>
                                <i class="fas fa-clone" />
                              </span>
                              <span class="icon is-left is-clickable" onClick={() => void onDelete(job.id)}>
                                <i class="fas fa-trash" />
                              </span>
                            </td>
                          </tr>
                        )}
                      </For>
                    </tbody>
                  </table>
                </div>
              </div>
            </Show>
          )}
        </Show>
      </Show>
    </>
  );
};
