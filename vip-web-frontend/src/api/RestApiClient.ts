import axios from "axios";
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
  UserCreate,
  Vcf,
  VcfCreate,
  VcfId,
  VcfUpload,
} from "./Api.ts";

export class RestApiClient implements Api {
  async login(login: Login): Promise<User> {
    const formData = new FormData();
    formData.append("username", login.username);
    formData.append("password", login.password);

    const { data } = await axios.post<User>("/api/auth/login", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return data;
  }

  async signup(user: UserCreate): Promise<User> {
    const { data } = await axios.post<User>("/api/auth/signup", user);
    return data;
  }

  async fetchUser(): Promise<User> {
    const { data } = await axios.get<User>("/api/auth/me");
    return data;
  }

  async logout(): Promise<void> {
    await axios.post("/api/auth/logout");
  }

  async uploadVcf(vcf: VcfUpload): Promise<VcfId> {
    const { data } = await axios.put<Vcf>(
      `/api/vcf/${encodeURIComponent(vcf.file.name)}?public=${encodeURIComponent(vcf.isPublic)}`,
      vcf.file,
      {
        headers: {
          Accept: "application/json",
          "Content-Type": "application/octet-stream",
        },
      },
    );
    return data.id;
  }

  async createVcf(vcf: VcfCreate): Promise<VcfId> {
    const { data } = await axios.post<Vcf>(`/api/vcf`, vcf, {
      headers: {
        Accept: "application/json",
      },
    });
    return data.id;
  }

  async fetchVcf(id: VcfId): Promise<Vcf> {
    const { data } = await axios.get<Vcf>(`/api/vcf/${id}`, {
      headers: {
        Accept: "application/json",
      },
    });
    return data;
  }

  async fetchVcfs(): Promise<Page<Vcf>> {
    const { data } = await axios.get<Page<Vcf>>("/api/vcf", {
      headers: {
        Accept: "application/json",
      },
    });
    return data;
  }

  async fetchDefaultFilterTree(type: keyof typeof FilterTreeType): Promise<FilterTree> {
    const { data } = await axios.get<FilterTree>("/api/filtertree/default?t=" + encodeURIComponent(type), {
      headers: {
        Accept: "application/json",
      },
    });
    return data;
  }

  async fetchFilterTrees(type: keyof typeof FilterTreeType): Promise<Page<FilterTree>> {
    const { data } = await axios.get<Page<FilterTree>>("/api/filtertree?t=" + encodeURIComponent(type), {
      headers: {
        Accept: "application/json",
      },
    });
    return data;
  }

  async deleteVcf(id: VcfId): Promise<void> {
    await axios.delete(`/api/vcf/${id}`);
  }

  async createJob(job: JobCreate): Promise<JobId> {
    const { data } = await axios.post<Job>(`/api/job`, job, {
      headers: {
        Accept: "application/json",
      },
    });
    return data.id;
  }

  async fetchJob(id: JobId): Promise<Job> {
    const { data } = await axios.get<Job>(`/api/job/${id}`, {
      headers: {
        Accept: "application/json",
      },
    });
    return data;
  }

  async deleteJob(id: JobId): Promise<void> {
    await axios.delete(`/api/job/${id}`);
  }

  async fetchJobs(): Promise<Page<Job>> {
    const { data } = await axios.get<Page<Job>>("/api/job", {
      headers: {
        Accept: "application/json",
      },
    });
    return data;
  }

  async fetchHpoTerms(query: string): Promise<Page<HpoTerm>> {
    const { data } = await axios.get<Page<HpoTerm>>("/api/hpo?q=" + encodeURIComponent(query), {
      headers: {
        Accept: "application/json",
      },
    });
    return data;
  }

  async fetchJobReport(id: JobId): Promise<Blob> {
    const { data } = await axios.get<Blob>(`/api/job/${id}/report`, {
      responseType: "blob",
    });
    return data;
  }
}
