import { createResource, Resource } from "solid-js";
import { RouteDataFuncArgs } from "@solidjs/router";
import api, { JobResource } from "../../api/Api.ts";

export type JobRouteData = { job: Resource<JobResource> };

export default function JobData({ params }: RouteDataFuncArgs): JobRouteData {
  const [job] = createResource(() => params.jobId, api.fetchJob);
  return { job };
}
