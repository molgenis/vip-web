// enums
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
  WES = "Whole exome sequencing",
  WGS = "Whole genome sequencing",
}

export enum Assembly {
  GRCh37 = "GRCh37",
  GRCh38 = "GRCh38",
  T2T = "T2T",
}

export enum JobStatus {
  COMPLETED,
  FAILED,
  PENDING,
  RUNNING,
}

export type User = {
  id: number;
  username: string;
  authorities: string[];
};
export type UserCreate = {
  username: string;
  password: string;
};
export type Login = { username: string; password: string };
// File
export type FileMetaId = number;
export type FileMeta = { id: FileMetaId; blobId: string; filename: string; size: number };

// Vcf
export type VcfId = number;
export type VcfSample = { name: string };
export type Vcf = {
  id: VcfId;
  file: FileMeta;
  samples: VcfSample[];
  assembly: keyof typeof Assembly;
  isOwner: boolean;
  isPublic: boolean;
};

// Tree
export type FilterTreeId = number;
export type FilterTree = {
  id: FilterTreeId;
  name: string;
  description?: string;
  classes: FilterTreeClass[];
};
export type FilterTreeClassId = number;
export type FilterTreeClass = { id: FilterTreeClassId; name: string; description?: string; defaultFilter: boolean };

export enum FilterTreeType {
  VARIANT,
  SAMPLE,
}

export type VcfUpload = {
  file: File;
  isPublic: boolean;
};
export type VcfCreate = {
  variants: string;
  isPublic: boolean;
};
// Job
export type JobId = number;
export type JobSampleCreate = {
  individualId: string;
  paternalId?: string;
  maternalId?: string;
  proband: boolean;
  sex: keyof typeof Sex;
  affected: keyof typeof AffectedStatus;
  hpoTermIds: HpoTermId[];
};
export type JobCreate = {
  name: string;
  vcfId: VcfId;
  sequencingMethod: keyof typeof SequencingMethod;
  assembly: keyof typeof Assembly;
  samples: JobSampleCreate[];
  variantFilterTreeId: FilterTreeId;
  variantFilterClassIds: FilterTreeClassId[];
  sampleFilterTreeId: FilterTreeId;
  sampleFilterClassIds: FilterTreeClassId[];
  isPublic: boolean;
};
export type JobReport = {
  file: FileMeta;
};
export type JobSample = {
  individualId: string;
  paternalId?: string;
  maternalId?: string;
  proband: boolean;
  sex: keyof typeof Sex;
  affected: keyof typeof AffectedStatus;
  hpoTerms: HpoTerm[];
};
export type Job = {
  id: JobId;
  name: string;
  vcf: Vcf;
  submitted: number;
  status: keyof typeof JobStatus;
  sequencingMethod: keyof typeof SequencingMethod;
  assembly: keyof typeof Assembly;
  samples: JobSample[];
  variantFilterTree: FilterTree;
  variantFilterClassIds: FilterTreeClassId[];
  sampleFilterTree: FilterTree;
  sampleFilterClassIds: FilterTreeClassId[];
  report?: JobReport;
  isOwner: boolean;
  isPublic: boolean;
};
export type HpoTermId = number;
export type HpoTerm = {
  id: HpoTermId;
  term: string;
  name: string;
};
export type Page<T> = {
  content: T[];
};
export type ApiError = {
  error: {
    message: string;
  };
};

export interface Api {
  login(login: Login): Promise<User>;

  signup(user: UserCreate): Promise<User>;

  fetchUser(): Promise<User>;

  logout(): Promise<void>;

  uploadVcf(vcf: VcfUpload): Promise<VcfId>;

  createVcf(vcf: VcfCreate): Promise<VcfId>;

  fetchVcf(id: VcfId): Promise<Vcf>;

  fetchVcfs(): Promise<Page<Vcf>>;

  deleteVcf(id: VcfId): Promise<void>;

  fetchDefaultFilterTree(type: keyof typeof FilterTreeType): Promise<FilterTree>;

  fetchFilterTrees(type: keyof typeof FilterTreeType): Promise<Page<FilterTree>>;

  createJob(job: JobCreate): Promise<JobId>;

  fetchJob(id: JobId): Promise<Job>;

  deleteJob(id: JobId): Promise<void>;

  fetchJobs(): Promise<Page<Job>>;

  fetchHpoTerms(query: string): Promise<Page<HpoTerm>>;

  fetchJobReport(id: JobId): Promise<Blob>;
}
