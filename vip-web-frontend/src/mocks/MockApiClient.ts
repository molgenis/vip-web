import {
  AffectedStatus,
  Api,
  Assembly,
  Classes,
  Job,
  JobResource,
  JobResourceId,
  JobStatus,
  Sample,
  SampleClasses,
  SequencingMethod,
  Sex,
  Vcf,
  VcfResource,
  VcfResourceId,
} from "../api/Api.ts";

export class MockApiClient implements Api {
  private vcfs: VcfResource[] = [];
  private jobs: JobResource[] = [];

  constructor() {
    // workaround: see https://stackoverflow.com/questions/4011793/this-is-undefined-in-javascript-class-methods/72197516#72197516
    Object.getOwnPropertyNames(MockApiClient.prototype).forEach((key) => {
      if (key !== "constructor") {
        // add eslint-disable / eslint-disable for eslint
        /* eslint-disable */
        // add @ts-ignore for IDE
        // @ts-ignore
        this[key] = this[key].bind(this);
        /* eslint-enable */
      }
    });
  }
  async createVcf(vcf: Vcf): Promise<VcfResourceId> {
    const lines = await this.parse(vcf.file);
    const line = lines.find((line) => line.startsWith("#CHROM"));
    if (line === undefined)
      return Promise.reject("file is not a valid uncompressed or BGZF-compressed VCF or BCF file");
    const tokens = line.split("\t", -1);

    if (tokens.length < 9) {
      return Promise.reject("file contains no samples, but must contain one or more samples");
    }
    const sampleIds = tokens.splice(9);

    const id = Date.now().toString();
    const vcfResource: VcfResource = { id: id, name: vcf.name, size: vcf.size, sampleIds: sampleIds };
    this.vcfs.push(vcfResource);
    return Promise.resolve(id);
  }

  async fetchVcf(id: VcfResourceId): Promise<VcfResource> {
    const vcf = this.vcfs.find((vcf) => vcf.id === id);
    return vcf !== undefined ? Promise.resolve(vcf) : Promise.reject();
  }

  async createJob(job: Job): Promise<JobResourceId> {
    const id: JobResourceId = Date.now().toString();
    const samples: Sample[] = job.vcf.sampleIds.map((sampleId) => ({
      individual_id: sampleId,
      proband: job.vcf.sampleIds.length === 1,
      sex: Sex.UNKNOWN,
      affected: job.vcf.sampleIds.length === 1 ? AffectedStatus.TRUE : AffectedStatus.UNKNOWN,
    }));
    const jobResource: JobResource = {
      id: id,
      submitted: Date.now(),
      status: JobStatus.CREATED,
      vcf: job.vcf,
      assembly: Assembly.GRCh38,
      sequencingMethod: SequencingMethod.WES,
      samples: samples,
      filterClasses: [Classes.VUS, Classes.LP, Classes.P],
      sampleFilterClasses: [SampleClasses.U1, SampleClasses.U2, SampleClasses.U3],
    };
    this.jobs.push(jobResource);
    return Promise.resolve(id);
  }

  async fetchJob(id: JobResourceId): Promise<JobResource> {
    const job = this.jobs.find((job) => job.id === id);
    return job !== undefined ? Promise.resolve(job) : Promise.reject();
  }

  async cloneJob(job: Job): Promise<JobResourceId> {
    const id = Date.now().toString();
    const clonedJob = {
      ...JSON.parse(JSON.stringify(job)),
      id: id,
      submitted: Date.now(),
      status: JobStatus.CREATED,
      report: null,
    } as JobResource;
    this.jobs.push(clonedJob);
    return Promise.resolve(id);
  }

  async updateJob(job: JobResource): Promise<void> {
    const index = this.jobs.findIndex((aJob) => aJob.id === job.id);
    if (index !== -1) this.jobs[index] = job;
    return index !== -1 ? Promise.resolve() : Promise.reject();
  }

  async deleteJob(id: JobResourceId): Promise<void> {
    const index = this.jobs.findIndex((job) => job.id === id);
    if (index !== -1) this.jobs.splice(index, 1);
    return index !== -1 ? Promise.resolve() : Promise.reject();
  }

  async fetchJobs(): Promise<JobResource[]> {
    return Promise.resolve([...this.jobs].reverse());
  }

  // mock util based on https://stackoverflow.com/questions/51026420/filereader-readastext-async-issues
  parse(file: File): Promise<string[]> {
    // Always return a Promise
    return new Promise((resolve, reject) => {
      let content = "";
      const reader = new FileReader();
      // Wait till complete
      reader.onloadend = function (e) {
        if (e.target !== null) {
          content = e.target.result as string;
          const result = content.split(/\r\n|\n/);
          resolve(result);
        }
      };
      // Make sure to handle error states
      reader.onerror = function (e) {
        reject(e);
      };
      reader.readAsText(file);
    });
  }
}
