import { Component, Show } from "solid-js";
import { createStore } from "solid-js/store";
import { Vcf, VcfCreate, VcfUpload } from "../api/Api.ts";
import { useStore } from "../store/store.tsx";
import { FileInput } from "./FileInput.tsx";
import { VcfInput } from "./VcfInput.tsx";

export type VcfFormData = {
  file: File | null;
  variants: string;
  vcf: Vcf | null;
  isPublic: boolean;
};
export type VcfUploadEvent = {
  vcf: VcfUpload;
};
export type VcfCreateEvent = {
  variants: VcfCreate;
};
export type VcfSelectEvent = {
  vcf: Vcf;
  isPublic: boolean;
};

export const VcfCreateForm: Component<{
  onSubmitVcfUpload: (event: VcfUploadEvent) => void;
  onSubmitVcfCreate: (event: VcfCreateEvent) => void;
  onSubmitVcfSelect: (event: VcfSelectEvent) => void;
  onCancel: () => void;
}> = (props) => {
  const [fields, setFields] = createStore<VcfFormData>({ file: null, variants: "", vcf: null, isPublic: false });
  const [state] = useStore();

  const handleSubmit = (event: Event) => {
    event.preventDefault();

    if (fields.file === null && fields.variants === "" && fields.vcf === null) {
      window.dispatchEvent(
        new CustomEvent("app_error", {
          detail: "Error: upload a VCF file, supply variants or select an existing VCF",
        }),
      );
      return;
    } else if (
      (fields.file !== null && fields.variants !== "") ||
      (fields.file !== null && fields.vcf !== null) ||
      (fields.variants !== "" && fields.vcf !== null)
    ) {
      window.dispatchEvent(
        new CustomEvent("app_error", {
          detail: "Error: upload a VCF file, supply variants or select an existing VCF. multiple options selected.",
        }),
      );
      return;
    } else if (fields.file !== null) {
      const vcf = {
        file: fields.file,
        isPublic: fields.isPublic,
      };
      props.onSubmitVcfUpload({ vcf });
    } else if (fields.variants !== "") {
      const variants = {
        variants: fields.variants,
        isPublic: fields.isPublic,
      };
      props.onSubmitVcfCreate({ variants });
    } else if (fields.vcf !== null) {
      props.onSubmitVcfSelect({ vcf: fields.vcf, isPublic: fields.isPublic });
    } else {
      throw new Error();
    }
  };

  const handleCancel = (event: Event) => {
    event.preventDefault();
    props.onCancel();
  };

  return (
    <form>
      {/* public */}
      <Show when={state.user && state.user.authorities.includes("ROLE_ADMIN")}>
        <div class="field is-horizontal">
          <div class="field-label">
            <label class="label">Public</label>
          </div>
          <div class="field-body">
            <div class="field">
              <div class="control">
                <input class="mr-1" type="checkbox" onInput={(e) => setFields("isPublic", e.target.checked)} />
              </div>
              <p class="help">Make input data publicly readable</p>
            </div>
          </div>
        </div>
      </Show>

      <fieldset class="box">
        <legend class="label has-text-centered">Data</legend>
        {/* vcf upload */}
        <div class="field is-horizontal">
          <div class="field-label">
            <label class="label">Upload VCF file</label>
          </div>
          <div class="field-body">
            <div class="field">
              <div class="control">
                <FileInput
                  accept={[".vcf", ".vcf.bgz", ".vcf.gz"]}
                  maxSize={50}
                  onInput={(e) => setFields("file", e.file)}
                />
              </div>
              <p class="help">
                File can be uncompressed or{" "}
                <a href="https://www.htslib.org/doc/bgzip.html" target="_blank" rel="noopener noreferrer nofollow">
                  BGZF
                </a>
                -compressed{" "}
                <a
                  href="https://samtools.github.io/hts-specs/VCFv4.3.pdf"
                  target="_blank"
                  rel="noopener noreferrer nofollow"
                >
                  VCF
                </a>{" "}
                and must contain one or more samples.{" "}
                <a
                  href="https://gatk.broadinstitute.org/hc/en-us/articles/360035531812-GVCF-Genomic-Variant-Call-Format"
                  target="_blank"
                  rel="noopener noreferrer nofollow"
                >
                  GVCF
                </a>{" "}
                is not supported. File uploads are limited to 50MB in size.
              </p>
            </div>
          </div>
        </div>
        {/* divider */}
        <div class="divider">OR</div>
        {/* variant textarea */}
        <div class="field is-horizontal">
          <div class="field-label">
            <label class="label">Input Variants</label>
          </div>
          <div class="field-body">
            <div class="field">
              <div class="control">
                <textarea class="textarea" onInput={(e) => setFields("variants", e.target.value)} />
              </div>
              <p class="help">
                Supply one variant per line in the{" "}
                <a href="https://gnomad.broadinstitute.org/" target="_blank" rel="noopener noreferrer nofollow">
                  gnomAD
                </a>{" "}
                variant notation, e.g. 14-74527358-G-A. GRCh38 genome coordinates are assumed.
              </p>
            </div>
          </div>
        </div>
        {/* divider */}
        <div class="divider">OR</div>
        {/* existing VCF */}
        <div class="field is-horizontal">
          <div class="field-label">
            <label class="label">Select VCF file</label>
          </div>
          <div class="field-body">
            <div class="field">
              <div class="field is-narrow">
                <VcfInput onSelectVcf={(e) => setFields("vcf", e.vcf)} />
              </div>
              <p class="help">Select a VCF file that was previously uploaded or is publicly available.</p>
            </div>
          </div>
        </div>
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
                Continue
              </button>
            </div>
          </div>
        </div>
      </div>
    </form>
  );
};
