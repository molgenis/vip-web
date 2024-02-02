import { Component } from "solid-js";
import api, { Vcf, VcfResourceId } from "../api/Api.ts";

export type VcfUploadEvent = {
  vcfId: VcfResourceId;
};

export const VcfUpload: Component<{
  accept: string;
  onUpload?: (event: VcfUploadEvent) => void;
}> = (props) => {
  const handleFileChange = async (event: Event) => {
    event.preventDefault();
    const target = event.target as HTMLInputElement;
    if (target !== null && target.files !== null && target.files.length === 1) {
      const file = target.files[0];
      if (file !== undefined) {
        if (file.size > 50 * 1024 * 1024) {
          alert("Error: the uploaded file exceeds the maximum file size of 50MB");
          return;
        }

        const vcf: Vcf = { file: file, name: file.name, size: file.size };
        let vcfId;
        try {
          vcfId = await api.createVcf(vcf);
        } catch (error) {
          alert(`Error: ${error as string}`);
          return;
        }

        if (props.onUpload !== undefined) {
          props.onUpload({ vcfId: vcfId });
        }
      }
    }
  };

  return (
    <div class="field">
      <div class="file">
        <label class="file-label">
          <input class="file-input" type="file" accept={props.accept} onChange={() => handleFileChange} />
          <span class="file-cta">
            <span class="file-icon">
              <i class="fas fa-upload" />
            </span>
            <span class="file-label">Choose a file…</span>
          </span>
        </label>
      </div>
      <p class="help">
        File can be both{" "}
        <a href="https://samtools.github.io/hts-specs/VCFv4.3.pdf" target="_blank" rel="noopener noreferrer nofollow">
          VCF or BCF
        </a>
        , uncompressed or{" "}
        <a href="https://www.htslib.org/doc/bgzip.html" target="_blank" rel="noopener noreferrer nofollow">
          BGZF-compressed
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
  );
};
