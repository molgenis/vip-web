import {
  Api,
  FilterTree,
  FilterTreeType,
  HpoTerm,
  Job,
  JobCreate,
  JobId,
  Login,
  Page,
  User,
  Vcf,
  VcfUpload,
  VcfId,
  VcfCreate,
} from "../api/Api.ts";

export class MockApiClient implements Api {
  private vcfs: Vcf[] = [
    {
      id: 1,
      file: { id: 1, blobId: "mockBlobId", filename: "mock.vcf", size: 123 },
      samples: [],
      assembly: "GRCh38",
      isOwner: true,
      isPublic: true,
    },
  ];
  private variantFilterTrees: FilterTree[] = [
    {
      id: 1,
      name: "variant tree #1",
      description: "variant tree #1 description",
      classes: [
        { id: 1, name: "B", description: "Benign", defaultFilter: false },
        {
          id: 2,
          name: "LB",
          description: "Likely Benign",
          defaultFilter: false,
        },
        { id: 3, name: "VUS", description: "Uncertain Significance", defaultFilter: true },
        {
          id: 4,
          name: "LP",
          description: "Likely Pathogenic",
          defaultFilter: true,
        },
        { id: 5, name: "P", description: "Pathogenic", defaultFilter: true },
      ],
    },
    {
      id: 2,
      name: "variant tree #2",
      description: "variant tree #2 description",
      classes: [
        {
          id: 6,
          name: "LB",
          description: "Likely Benign",
          defaultFilter: false,
        },
        { id: 7, name: "VUS", description: "Uncertain Significance", defaultFilter: true },
        {
          id: 8,
          name: "LP",
          description: "Likely Pathogenic",
          defaultFilter: true,
        },
      ],
    },
    {
      id: 3,
      name: "variant tree #3",
      description: "variant tree #3 description",
      classes: [
        {
          id: 9,
          name: "LB",
          description: "Likely Benign",
          defaultFilter: false,
        },
        {
          id: 10,
          name: "LP",
          description: "Likely Pathogenic",
          defaultFilter: true,
        },
      ],
    },
  ];
  private sampleFilterTrees: FilterTree[] = [
    {
      id: 4,
      name: "sample tree #1",
      description: "sample tree #1 description",
      classes: [
        {
          id: 11,
          name: "LB",
          description: "Likely Benign",
          defaultFilter: false,
        },
        {
          id: 12,
          name: "LP",
          description: "Likely Pathogenic",
          defaultFilter: true,
        },
      ],
    },
    {
      id: 5,
      name: "sample tree #2",
      description: "sample tree #2 description",
      classes: [
        {
          id: 13,
          name: "LB",
          description: "Likely Benign",
          defaultFilter: false,
        },
        {
          id: 14,
          name: "LP",
          description: "Likely Pathogenic",
          defaultFilter: true,
        },
      ],
    },
    {
      id: 6,
      name: "sample tree #3",
      description: "sample tree #3 description",
      classes: [
        {
          id: 15,
          name: "LB",
          description: "Likely Benign",
          defaultFilter: false,
        },
        {
          id: 16,
          name: "LP",
          description: "Likely Pathogenic",
          defaultFilter: true,
        },
      ],
    },
  ];
  private hpoTerms: HpoTerm[] = [
    { id: 1, term: "HP:0000001", name: "All" },
    { id: 2, term: "HP:0000002", name: "Abnormality of body height" },
    {
      id: 3,
      term: "HP:0000003",
      name: "Multicystic kidney dysplasia",
    },
    { id: 4, term: "HP:0000005", name: "Mode of inheritance" },
    { id: 5, term: "HP:0000006", name: "Autosomal dominant inheritance" },
    { id: 6, term: "HP:0000007", name: "Autosomal recessive inheritance" },
    {
      id: 7,
      term: "HP:0000008",
      name: "Abnormal morphology of female internal genitalia",
    },
    { id: 8, term: "HP:0000009", name: "Functional abnormality of the bladder" },
    { id: 9, term: "HP:0000010", name: "Recurrent urinary tract infections" },
    { id: 10, term: "HP:0000011", name: "Neurogenic bladder" },
  ];
  private adminUser: User = { id: 1, username: "admin", authorities: ["ROLE_ADMIN"] };

  private anonymousUser: User = { id: 2, username: "anonymous", authorities: ["ROLE_ANONYMOUS"] };
  private user: User = { id: 3, username: "user", authorities: ["ROLE_USER"] };
  private activeUser = this.anonymousUser;
  private report = `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <link rel="icon" href="data:,">
    <title>VIP report</title>
</head>
<body>
<h1>Mock VIP report</h1>
</body>
</html>
`;
  private jobs: Job[] = [
    {
      id: 1,
      name: "mock",
      vcf: this.vcfs[0],
      submitted: Date.now(),
      status: "COMPLETED",
      sequencingMethod: "WES",
      assembly: "GRCh38",
      samples: [],
      variantFilterTree: this.variantFilterTrees[0],
      variantFilterClassIds: [3, 4, 5],
      sampleFilterTree: this.sampleFilterTrees[0],
      sampleFilterClassIds: [12],
      report: { file: { id: 1, blobId: "blob1", filename: "vip.html", size: 123 } },
      isOwner: true,
      isPublic: false,
    },
  ];

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

  login(login: Login): Promise<User> {
    let user;
    switch (login.username) {
      case "admin":
        this.activeUser = this.adminUser;
        user = this.activeUser;
        break;
      default:
        this.activeUser = this.user;
        user = this.activeUser;
        break;
    }
    return Promise.resolve(user);
  }

  signup(): Promise<User> {
    this.activeUser = this.user;
    return Promise.resolve(this.activeUser);
  }

  fetchUser(): Promise<User> {
    return Promise.resolve(this.activeUser);
  }

  logout(): Promise<void> {
    this.activeUser = this.anonymousUser;
    return Promise.resolve();
  }

  async uploadVcf(vcf: VcfUpload): Promise<VcfId> {
    const lines = await this.parse(vcf.file);
    const line = lines.find((line) => line.startsWith("#CHROM"));
    if (line === undefined)
      return Promise.reject("file is not a valid uncompressed or BGZF-compressed VCF or BCF file");
    const tokens = line.split("\t", -1);

    if (tokens.length < 9) {
      return Promise.reject("file contains no samples, but must contain one or more samples");
    }
    const sampleIds = tokens.splice(9);

    const id = this.vcfs.length;
    this.vcfs.push({
      id,
      file: {
        id: this.vcfs.length,
        blobId: `blob${this.vcfs.length}`,
        filename: vcf.file.name,
        size: vcf.file.size,
      },
      samples: sampleIds.map((sampleId) => ({ name: sampleId })),
      assembly: "GRCh38",
      isOwner: true,
      isPublic: vcf.isPublic,
    });
    return id;
  }

  createVcf(vcf: VcfCreate): Promise<VcfId> {
    console.log(vcf);
    return Promise.resolve(this.vcfs[0].id);
  }

  fetchVcf(id: VcfId): Promise<Vcf> {
    const vcf = this.vcfs.find((vcf) => vcf.id === id);
    if (vcf === undefined) throw `unknown vcf '${id}'`;
    return Promise.resolve(vcf);
  }

  fetchVcfs(): Promise<Page<Vcf>> {
    return Promise.resolve({ content: [...this.vcfs] });
  }

  deleteVcf(id: VcfId): Promise<void> {
    const index = this.vcfs.findIndex((vcf) => vcf.id === id);
    if (index !== -1) this.vcfs.splice(index, 1);
    return Promise.resolve();
  }

  fetchDefaultFilterTree(type: keyof typeof FilterTreeType): Promise<FilterTree> {
    let filterTree;
    switch (type) {
      case "VARIANT":
        filterTree = { ...this.variantFilterTrees[0] };
        break;
      case "SAMPLE":
        filterTree = { ...this.sampleFilterTrees[0] };
        break;
      default:
        throw `unexpected enum value`;
    }
    return Promise.resolve(filterTree);
  }

  fetchFilterTrees(type: keyof typeof FilterTreeType): Promise<Page<FilterTree>> {
    let filterTrees;
    switch (type) {
      case "VARIANT":
        filterTrees = [...this.variantFilterTrees];
        break;
      case "SAMPLE":
        filterTrees = [...this.sampleFilterTrees];
        break;
      default:
        throw `unexpected enum value`;
    }
    return Promise.resolve({ content: filterTrees });
  }

  createJob(job: JobCreate): Promise<JobId> {
    const id = this.jobs.length;

    this.jobs.push({
      id,
      name: job.name,
      vcf: this.vcfs[job.vcfId],
      submitted: Date.now(),
      status: "PENDING",
      sequencingMethod: job.sequencingMethod,
      assembly: job.assembly,
      samples: job.samples.map((sample) => ({
        individualId: sample.individualId,
        paternalId: sample.paternalId,
        maternalId: sample.maternalId,
        proband: sample.proband,
        sex: sample.sex,
        affected: sample.affected,
        hpoTerms: sample.hpoTermIds.map(
          (hpoTermId) => this.hpoTerms.find((hpoTerm) => hpoTerm.id === hpoTermId) as HpoTerm,
        ),
      })),
      variantFilterTree: this.variantFilterTrees.find(
        (filterTree) => filterTree.id === job.variantFilterTreeId,
      ) as FilterTree,
      variantFilterClassIds: job.variantFilterClassIds,
      sampleFilterTree: this.sampleFilterTrees.find(
        (filterTree) => filterTree.id === job.sampleFilterTreeId,
      ) as FilterTree,
      sampleFilterClassIds: job.sampleFilterClassIds,
      isOwner: true,
      isPublic: false,
    });

    return Promise.resolve(id);
  }

  fetchJob(id: JobId): Promise<Job> {
    const job = this.jobs.find((job) => job.id === id);
    if (job === undefined) throw `unknown job '${id}'`;
    return Promise.resolve(job);
  }

  deleteJob(id: JobId): Promise<void> {
    const index = this.jobs.findIndex((job) => job.id === id);
    if (index !== -1) this.jobs.splice(index, 1);
    else throw `unknown job '${id}'`;
    return Promise.resolve();
  }

  fetchJobs(): Promise<Page<Job>> {
    return Promise.resolve({ content: [...this.jobs].reverse() });
  }

  fetchHpoTerms(query: string): Promise<Page<HpoTerm>> {
    const regex = new RegExp(`.*${query}.*`);
    return Promise.resolve({
      content: this.hpoTerms.filter((hpoTerm) => hpoTerm.term.match(regex) || hpoTerm.name.match(regex)),
    });
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

  fetchJobReport(): Promise<Blob> {
    return Promise.resolve(new Blob([this.report]));
  }
}
