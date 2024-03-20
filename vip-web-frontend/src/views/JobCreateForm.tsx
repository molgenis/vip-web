import { Component, createResource, Show } from "solid-js";
import { useNavigate, useRouteData } from "@solidjs/router";
import { Loader } from "../components/Loader.tsx";
import { FilterTree, Vcf } from "../api/Api.ts";
import { VcfRouteData } from "./data/VcfData.tsx";

import { JobCreateEvent, JobForm, JobFormData } from "../components/JobForm.tsx";
import api from "../api/ApiClient.ts";

function createJob(vcf: Vcf, variantFilterTree: FilterTree, sampleFilterTree: FilterTree): JobFormData {
  return {
    name: `${vcf.file.filename} (${vcf.samples.length} samples)`,
    vcf: vcf,
    sequencingMethod: "WES",
    assembly: vcf.assembly || "GRCh38",
    samples: vcf.samples.map((sample, index) => ({
      individualId: sample.name,
      proband: index === 0,
      sex: "UNKNOWN",
      affected: index === 0 ? "TRUE" : "UNKNOWN",
      hpoTerms: [],
    })),
    variantFilterTree: variantFilterTree,
    variantFilterClassIds: variantFilterTree.classes
      .filter((filterClass) => filterClass.defaultFilter)
      .map((filterClass) => filterClass.id),
    sampleFilterTree: sampleFilterTree,
    sampleFilterClassIds: sampleFilterTree.classes
      .filter((filterClass) => filterClass.defaultFilter)
      .map((filterClass) => filterClass.id),
    isPublic: vcf.isPublic,
  };
}

export const JobCreateForm: Component = () => {
  const { vcf } = useRouteData<VcfRouteData>();
  const navigate = useNavigate();

  const [variantFilterTree] = createResource({}, () => api.fetchDefaultFilterTree("VARIANT"));
  const [sampleFilterTree] = createResource({}, () => api.fetchDefaultFilterTree("SAMPLE"));

  const handleSubmit = async (event: JobCreateEvent) => {
    await api.createJob(event.job);
    navigate("/jobs");
  };

  const handleCancel = async () => {
    const vcfId = vcf()?.id;
    if (vcfId !== undefined) {
      await api.deleteVcf(vcfId);
    }
    navigate(-1);
  };

  return (
    <div class="columns is-centered mt-1">
      <div class="column is-two-thirds-widescreen">
        <h1 class="title is-4">New Job (Step 2 of 2)</h1>
        <Show when={!vcf.loading && !variantFilterTree.loading && !sampleFilterTree.loading} fallback={<Loader />}>
          <Show when={vcf()} keyed>
            {(vcf) => (
              <Show when={variantFilterTree()} keyed>
                {(variantFilterTree) => (
                  <Show when={sampleFilterTree()} keyed>
                    {(sampleFilterTree) => (
                      <JobForm
                        job={createJob(vcf, variantFilterTree, sampleFilterTree)}
                        onSubmit={(event) => void handleSubmit(event)}
                        onCancel={() => void handleCancel()}
                      />
                    )}
                  </Show>
                )}
              </Show>
            )}
          </Show>
        </Show>
      </div>
    </div>
  );
};
