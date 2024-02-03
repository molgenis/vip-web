import { Component, For, Show } from "solid-js";
import { createStore } from "solid-js/store";
import {
  AffectedStatus,
  Assembly,
  Classes,
  JobResource,
  JobStatus,
  SampleClasses,
  SequencingMethod,
  Sex,
} from "../api/Api.ts";

export type JobUpdateEvent = {
  job: JobResource;
};

export const JobUpdateForm: Component<{
  job: JobResource;
  onSubmit: (event: JobUpdateEvent) => void;
  onCancel: () => void;
}> = (props) => {
  // eslint-disable-next-line
  const [fields, setFields] = createStore(props.job);

  const handleSubmit = (event: Event) => {
    event.preventDefault();
    setFields("status", JobStatus.SUBMITTED);
    props.onSubmit({ job: fields });
  };

  const handleCancel = (event: Event) => {
    event.preventDefault();
    setFields("status", JobStatus.CANCELLED);
    props.onCancel();
  };

  return (
    <form>
      <fieldset class="box">
        <legend class="label has-text-centered">File: {fields.vcf.name}</legend>
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
                        checked={value === fields.sequencingMethod}
                        onInput={(e) => setFields("sequencingMethod", e.target.value as SequencingMethod)}
                      />
                      {value}
                    </label>
                  )}
                </For>
              </div>
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
                        checked={value === fields.assembly}
                        onInput={(e) => setFields("assembly", e.target.value as Assembly)}
                      />
                      {value}
                    </label>
                  )}
                </For>
              </div>
            </div>
          </div>
        </div>
      </fieldset>

      <For each={fields.samples}>
        {(sample, index) => (
          <fieldset class="box">
            <legend class="label has-text-centered">Sample: {sample.individual_id}</legend>
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
                  </div>
                </div>
              </div>
              {/* parents */}
              <div class="field is-horizontal">
                <div class="field-label">
                  <label class="label">Parents</label>
                </div>
                <div class="field-body">
                  <div class="field is-grouped">
                    <div class="field is-narrow">
                      <div class="control">
                        <div class="select">
                          <select onInput={(e) => setFields("samples", index(), "paternal_id", e.target.value)}>
                            <option>Select father</option>
                            <For each={fields.samples}>
                              {(fatherSample) => (
                                <Show when={fatherSample.individual_id !== sample.individual_id}>
                                  <option>{fatherSample.individual_id}</option>
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
                          <select onInput={(e) => setFields("samples", index(), "maternal_id", e.target.value)}>
                            <option>Select mother</option>
                            <For each={fields.samples}>
                              {(motherSample) => (
                                <Show when={motherSample.individual_id !== sample.individual_id}>
                                  <option>{motherSample.individual_id}</option>
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
                            name={key}
                            checked={sample.sex === value}
                            onInput={(e) => setFields("samples", index(), "sex", e.target.value as Sex)}
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
                            name={key}
                            checked={sample.affected === value}
                            onInput={(e) => setFields("samples", index(), "affected", e.target.value as AffectedStatus)}
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
              <div class="field-label">
                <label class="label">HPO ids</label>
              </div>
              <div class="field-body">
                <div class="field is-narrow">
                  <div class="control">
                    <input
                      class="input"
                      type="text"
                      onInput={(e) => setFields("samples", index(), "hpo_ids", e.target.value)}
                    />
                  </div>
                </div>
              </div>
            </div>
          </fieldset>
        )}
      </For>

      <fieldset class="box">
        <legend class="label has-text-centered">Settings</legend>
        <div class="field is-horizontal">
          <div class="field-label">
            <label class="label">Variant filter</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="field is-horizontal">
                <For each={Object.values(Classes)}>
                  {(value) => (
                    <div class="control mr-3">
                      <label class="checkbox">
                        <input type="checkbox" class="mr-1" checked={fields.filterClasses.includes(value)} />
                        {value}
                      </label>
                    </div>
                  )}
                </For>
              </div>
            </div>
          </div>
        </div>

        <div class="field is-horizontal">
          <div class="field-label">
            <label class="label">Sample filter</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="field is-horizontal">
                <For each={Object.values(SampleClasses)}>
                  {(value) => (
                    <div class="control mr-3">
                      <label class="checkbox">
                        <input type="checkbox" class="mr-1" checked={fields.sampleFilterClasses.includes(value)} />
                        {value}
                      </label>
                    </div>
                  )}
                </For>
              </div>
            </div>
          </div>
        </div>
      </fieldset>

      <div class="field is-grouped">
        <div class="control">
          <button class="button is-link" onClick={handleSubmit}>
            Run
          </button>
        </div>
        <div class="control">
          <button class="button is-link is-light" onClick={handleCancel}>
            Cancel
          </button>
        </div>
      </div>
    </form>
  );
};
