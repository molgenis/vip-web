import { Api, JobResource, JobResourceId, VcfResource, VcfResourceId } from "./Api.ts";

// TODO implement
export class ApiClient implements Api {
  async createVcf(): Promise<VcfResourceId> {
    return Promise.reject("FIXME not implemented");
  }

  async fetchVcf(): Promise<VcfResource> {
    return Promise.reject("FIXME not implemented");
  }

  async createJob(): Promise<JobResourceId> {
    return Promise.reject("FIXME not implemented");
  }

  async fetchJob(): Promise<JobResource> {
    return Promise.reject("FIXME not implemented");
  }

  async cloneJob(): Promise<JobResourceId> {
    return Promise.reject("FIXME not implemented");
  }

  async updateJob(): Promise<void> {
    return Promise.reject("FIXME not implemented");
  }

  async deleteJob(): Promise<void> {
    return Promise.reject("FIXME not implemented");
  }

  async fetchJobs(): Promise<JobResource[]> {
    return Promise.reject("FIXME not implemented");
  }
}
