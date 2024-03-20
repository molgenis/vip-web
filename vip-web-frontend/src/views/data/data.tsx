import { cache } from "@solidjs/router";
import api from "../../api/ApiClient.ts";
import { Job, JobId, Vcf, VcfId } from "../../api/Api.ts";

export const getVcf = cache(async (id: VcfId): Promise<Vcf> => await api.fetchVcf(id), "vcf");
export const getJob = cache(async (id: JobId): Promise<Job> => await api.fetchJob(id), "job");
