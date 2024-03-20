import { createResource, Resource } from "solid-js";
import { RouteDataFuncArgs } from "@solidjs/router";
import { Job } from "../../api/Api.ts";
import api from "../../api/ApiClient.ts";

export type JobRouteData = { job: Resource<Job> };

export default function JobData({ params }: RouteDataFuncArgs): JobRouteData {
  const [job] = createResource(() => Number(params.jobId), api.fetchJob);
  return { job };
}
