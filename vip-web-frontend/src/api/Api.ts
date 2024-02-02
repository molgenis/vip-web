import { ApiClient } from "./ApiClient.ts";
import { MockApiClient } from "../mocks/MockApiClient.ts";

export interface Api {
  createVcf(vcf: Vcf): Promise<VcfResourceId>;
  fetchVcf(id: VcfResourceId): Promise<VcfResource>;
  createJob(job: Job): Promise<JobResourceId>;
  fetchJob(id: JobResourceId): Promise<JobResource>;
  cloneJob(job: Job): Promise<JobResourceId>;
  updateJob(job: JobResource): Promise<void>;
  deleteJob(id: JobResourceId): Promise<void>;
  fetchJobs(): Promise<JobResource[]>;
}

// FIXME (see vip-report-template) lazy import MockApiClient to ensure that it is excluded from the build artifact
const api = import.meta.env.PROD ? new ApiClient() : new MockApiClient();

export default api;

export type Vcf = { file: File; name: string; size: number };
export type VcfResourceId = string;
export type VcfResource = { id: VcfResourceId; name: string; size: number; sampleIds: string[] };
export type Job = { vcf: VcfResource };
export type JobResourceId = string;
export type JobResource = { id: JobResourceId } & Job & {
    submitted: number;
    status: JobStatus;
    sequencingMethod: SequencingMethod;
    assembly: Assembly;
    samples: Sample[];
    filterClasses: Classes[];
    sampleFilterClasses: SampleClasses[];
    report?: string;
  };

export enum Sex {
  MALE = "Male",
  FEMALE = "Female",
  UNKNOWN = "Unknown",
}

export enum AffectedStatus {
  TRUE = "True",
  FALSE = "False",
  UNKNOWN = "Unknown",
}

export enum SequencingMethod {
  WES = "WES",
  WGS = "WGS",
}

export enum Assembly {
  GRCh37 = "GRCh37",
  GRCh38 = "GRCh38",
  T2T = "T2T",
}

export enum JobStatus {
  CANCELLED = "Cancelled",
  COMPLETED = "Completed",
  CREATED = "Created",
  FAILED = "Failed",
  PENDING = "Pending",
  RUNNING = "Running",
  SUBMITTED = "Submitted",
}

export type Sample = {
  individual_id: string;
  paternal_id?: string;
  maternal_id?: string;
  proband?: boolean;
  sex?: Sex;
  affected?: AffectedStatus;
  hpo_ids?: string;
};

export enum Classes {
  LQ = "LQ",
  B = "B",
  LB = "LB",
  VUS = "VUS",
  LP = "LP",
  P = "P",
}

export enum SampleClasses {
  U1 = "U1",
  U2 = "U2",
  U3 = "U3",
}
