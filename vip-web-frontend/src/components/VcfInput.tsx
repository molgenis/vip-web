import { Component, createResource, For, onMount, Show } from "solid-js";
import { Vcf, VcfId } from "../api/Api.ts";
import { Loader } from "./Loader.tsx";
import api from "../api/ApiClient.ts";

export type VcfEvent = {
  vcf: Vcf | null; // null for deselect
};

export const VcfInput: Component<{
  onSelectVcf: (event: VcfEvent) => void;
}> = (props) => {
  const [vcfs] = createResource({}, () => api.fetchVcfs());

  const handleTreeChange = (vcfId: VcfId) => {
    const vcfPage = vcfs();
    if (vcfPage == undefined) throw new Error();

    const vcf = vcfPage.content.find((vcf) => vcf.id === vcfId) || null;
    if (vcf === undefined) throw new Error();

    props.onSelectVcf({ vcf: vcf });
  };

  onMount(() => {});

  return (
    <>
      <div class="control">
        <div classList={{ select: true, "is-fullwidth": true, "is-loading": vcfs.loading }}>
          <Show when={!vcfs.loading} fallback={<Loader />}>
            <Show when={vcfs()} keyed>
              {(vcfs) => (
                <select
                  onChange={(event) => {
                    event.preventDefault();
                    handleTreeChange(Number(event.currentTarget.value));
                  }}
                >
                  <option />
                  <For each={vcfs.content}>
                    {(vcf) => (
                      <option value={vcf.id}>
                        {vcf.file.filename} {vcf.isPublic ? " (public)" : ""}
                      </option>
                    )}
                  </For>
                </select>
              )}
            </Show>
          </Show>
        </div>
      </div>
    </>
  );
};
