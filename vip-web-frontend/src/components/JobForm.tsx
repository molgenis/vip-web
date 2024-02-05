import { Component, For, Show } from "solid-js";
import { createStore } from "solid-js/store";
import {
  AffectedStatus,
  Assembly,
  FilterTree,
  FilterTreeClassId,
  JobCreate,
  JobSample,
  SequencingMethod,
  Sex,
  Vcf,
} from "../api/Api.ts";
import { HpoInput } from "./HpoInput.tsx";
import { FilterTreeFields } from "./FilterTreeFields.tsx";
import { useStore } from "../store/store.tsx";

export type JobFormData = {
  name: string;
  vcf: Vcf;
  sequencingMethod: keyof typeof SequencingMethod;
  assembly: keyof typeof Assembly;
  samples: JobSample[];
  variantFilterTree: FilterTree;
  variantFilterClassIds: FilterTreeClassId[];
  sampleFilterTree: FilterTree;
  sampleFilterClassIds: FilterTreeClassId[];
  isPublic: boolean;
};

export type JobCreateEvent = {
  job: JobCreate;
};

export const JobForm: Component<{
  job: JobFormData;
  onSubmit: (event: JobCreateEvent) => void;
  onCancel: () => void;
}> = (props) => {
  const [fields, setFields] = createStore(props.job);
  const [state] = useStore();
  const handleSubmit = (event: Event) => {
    event.preventDefault();

    const job: JobCreate = {
      name: fields.name,
      vcfId: fields.vcf.id,
      sequencingMethod: fields.sequencingMethod,
      assembly: fields.assembly,
      samples: fields.samples.map((sample) => ({
        individualId: sample.individualId,
        paternalId: sample.paternalId,
        maternalId: sample.maternalId,
        proband: sample.proband,
        sex: sample.sex,
        affected: sample.affected,
        hpoTermIds: sample.hpoTerms.map((hpoTerm) => hpoTerm.id),
      })),
      variantFilterTreeId: fields.variantFilterTree.id,
      variantFilterClassIds: fields.variantFilterClassIds,
      sampleFilterTreeId: fields.sampleFilterTree.id,
      sampleFilterClassIds: fields.sampleFilterClassIds,
      isPublic: fields.isPublic,
    };

    props.onSubmit({ job });
  };

  const handleCancel = (event: Event) => {
    event.preventDefault();
    props.onCancel();
  };

  return (
    <form>
      <Show when={fields.vcf.isPublic && state.user && state.user.authorities.includes("ROLE_ADMIN")}>
        {/* public */}
        <div class="field is-horizontal">
          <div class="field-label">
            <label class="label">Public</label>
          </div>
          <div class="field-body">
            <div class="field">
              <div class="control">
                <input
                  class="mr-1"
                  type="checkbox"
                  checked={fields.isPublic}
                  onInput={(e) => setFields("isPublic", e.target.checked)}
                />
              </div>
              <p class="help">Make job and job results publicly readable</p>
            </div>
          </div>
        </div>
      </Show>

      <fieldset class="box">
        <legend class="label has-text-centered">File</legend>
        {/* sequencing method */}
        <div class="field is-horizontal">
          <div class="field-label">
            <label class="label">Sequencing</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="control">
                <For each={Object.entries(SequencingMethod)}>
                  {([key, value]) => (
                    <label class="radio">
                      <input
                        class="mr-1"
                        type="radio"
                        value={key}
                        checked={key === fields.sequencingMethod}
                        onInput={(e) => setFields("sequencingMethod", e.target.value as keyof typeof SequencingMethod)}
                      />
                      <abbr title={value}>{key}</abbr>
                    </label>
                  )}
                </For>
              </div>
              <p class="help">Sequencing method used to generate data</p>
            </div>
          </div>
        </div>
        {/* assembly */}
        <div class="field is-horizontal">
          <div class="field-label">
            <label class="label">Assembly</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="control">
                <For each={Object.entries(Assembly)}>
                  {([key, value]) => (
                    <label class="radio">
                      <input
                        class="mr-1"
                        type="radio"
                        value={key}
                        checked={key === fields.assembly}
                        onInput={(e) => setFields("assembly", e.target.value as keyof typeof Assembly)}
                      />
                      {value}
                    </label>
                  )}
                </For>
              </div>
              <p class="help">Human genome reference assembly used to generate data</p>
            </div>
          </div>
        </div>
      </fieldset>

      <For each={fields.samples}>
        {(sample, index) => (
          <fieldset class="box">
            <legend class="label has-text-centered">Sample: {sample.individualId}</legend>
            <Show when={fields.samples.length > 1}>
              {/* proband */}
              <div class="field is-horizontal">
                <div class="field-label">
                  <label class="label">Proband</label>
                </div>
                <div class="field-body">
                  <div class="field is-narrow">
                    <div class="control">
                      <input
                        class="mr-1"
                        type="checkbox"
                        checked={sample.proband}
                        onInput={(e) => setFields("samples", index(), "proband", e.target.checked)}
                      />
                    </div>
                    <p class="help">
                      Is this sample the index case: the patient or member of the family that brings a family under
                      study?
                    </p>
                  </div>
                </div>
              </div>
              {/* parents */}
              <div class="field is-horizontal">
                <div class="field-label is-normal">
                  <label class="label">Parents</label>
                </div>
                <div class="field-body">
                  <div class="field is-grouped">
                    <div class="field is-narrow">
                      <div class="control">
                        <div class="select">
                          <select onInput={(e) => setFields("samples", index(), "paternalId", e.target.value)}>
                            <option>Select father</option>
                            <For each={fields.samples}>
                              {(fatherSample) => (
                                <Show when={fatherSample.individualId !== sample.individualId}>
                                  <option selected={fatherSample.individualId === sample.paternalId}>
                                    {fatherSample.individualId}
                                  </option>
                                </Show>
                              )}
                            </For>
                          </select>
                        </div>
                      </div>
                    </div>
                    <div class="field is-narrow ml-3">
                      <div class="control">
                        <div class="select">
                          <select onInput={(e) => setFields("samples", index(), "maternalId", e.target.value)}>
                            <option>Select mother</option>
                            <For each={fields.samples}>
                              {(motherSample) => (
                                <Show when={motherSample.individualId !== sample.individualId}>
                                  <option selected={motherSample.individualId === sample.maternalId}>
                                    {motherSample.individualId}
                                  </option>
                                </Show>
                              )}
                            </For>
                          </select>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </Show>
            {/* sex */}
            <div class="field is-horizontal">
              <div class="field-label">
                <label class="label">Sex</label>
              </div>
              <div class="field-body">
                <div class="field is-narrow">
                  <div class="control">
                    <For each={Object.entries(Sex)}>
                      {([key, value]) => (
                        <label class="radio">
                          <input
                            class="mr-1"
                            type="radio"
                            value={key}
                            checked={key === fields.samples[index()].sex}
                            onInput={(e) => setFields("samples", index(), "sex", e.target.value as keyof typeof Sex)}
                          />
                          {value}
                        </label>
                      )}
                    </For>
                  </div>
                </div>
              </div>
            </div>
            {/* affected */}
            <div class="field is-horizontal">
              <div class="field-label">
                <label class="label">Affected</label>
              </div>
              <div class="field-body">
                <div class="field is-narrow">
                  <div class="control">
                    <For each={Object.entries(AffectedStatus)}>
                      {([key, value]) => (
                        <label class="radio">
                          <input
                            class="mr-1"
                            type="radio"
                            value={key}
                            checked={key === fields.samples[index()].affected}
                            onInput={(e) =>
                              setFields("samples", index(), "affected", e.target.value as keyof typeof AffectedStatus)
                            }
                          />
                          {value}
                        </label>
                      )}
                    </For>
                  </div>
                </div>
              </div>
            </div>
            {/* HPO ids */}

            <div class="field is-horizontal">
              <div class="field-label is-normal">
                <label class="label">Phenotypes</label>
              </div>
              <div class="field-body">
                <div class="field">
                  <HpoInput
                    hpoTerms={fields.samples[index()].hpoTerms || []}
                    onAddTerm={(e) => {
                      const oldSampleHpoTermIds = fields.samples[index()].hpoTerms || [];
                      setFields("samples", index(), "hpoTerms", [...oldSampleHpoTermIds, e.term]);
                    }}
                    onRemoveTerm={(e) => {
                      const oldSampleHpoTerms = fields.samples[index()].hpoTerms || [];
                      const sampleHpoTerms = oldSampleHpoTerms.filter((hpoTerm) => hpoTerm.id !== e.term.id);
                      setFields("samples", index(), "hpoTerms", sampleHpoTerms);
                    }}
                  />
                  <p class="help">
                    Phenotypic abnormalities described by{" "}
                    <a href="https://hpo.jax.org/" target="_blank" rel="noopener noreferrer nofollow">
                      HPO terms
                    </a>
                  </p>
                </div>
              </div>
            </div>
          </fieldset>
        )}
      </For>

      <fieldset class="box">
        <legend class="label has-text-centered">Settings</legend>
        <FilterTreeFields
          type={"VARIANT"}
          filterTree={fields.variantFilterTree}
          filterClassIds={fields.variantFilterClassIds}
          onSelectTree={(e) => {
            const tree = e.tree;
            setFields("variantFilterTree", e.tree);
            setFields(
              "variantFilterClassIds",
              tree.classes.filter((treeClass) => treeClass.defaultFilter).map((treeClass) => treeClass.id),
            );
          }}
          onAddTreeClass={(e) => {
            setFields("variantFilterClassIds", [...fields.variantFilterClassIds, e.treeClass.id]);
          }}
          onRemoveTreeClass={(e) => {
            setFields(
              "variantFilterClassIds",
              fields.variantFilterClassIds.filter((filterClassId) => filterClassId !== e.treeClass.id),
            );
          }}
        />
        <FilterTreeFields
          type={"SAMPLE"}
          filterTree={fields.sampleFilterTree}
          filterClassIds={fields.sampleFilterClassIds}
          onSelectTree={(e) => {
            const tree = e.tree;
            setFields("sampleFilterTree", e.tree);
            setFields(
              "sampleFilterClassIds",
              tree.classes.filter((treeClass) => treeClass.defaultFilter).map((treeClass) => treeClass.id),
            );
          }}
          onAddTreeClass={(e) => {
            setFields("sampleFilterClassIds", [...fields.sampleFilterClassIds, e.treeClass.id]);
          }}
          onRemoveTreeClass={(e) => {
            setFields(
              "sampleFilterClassIds",
              fields.sampleFilterClassIds.filter((filterClassId) => filterClassId !== e.treeClass.id),
            );
          }}
        />
      </fieldset>

      <div class="field is-horizontal">
        <div class="field-label" />
        <div class="field-body">
          <div class="field is-grouped is-grouped-right">
            <div class="control">
              <button class="button is-link is-light" onClick={handleCancel}>
                Cancel
              </button>
            </div>
            <div class="control">
              <button class="button is-link" onClick={handleSubmit}>
                Submit
              </button>
            </div>
          </div>
        </div>
      </div>
    </form>
  );
};
