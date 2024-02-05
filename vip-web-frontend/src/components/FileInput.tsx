import { Component, createSignal } from "solid-js";

export type FileInputEvent = {
  file: File;
};

export const FileInput: Component<{
  accept?: string[];
  maxSize?: number;
  onInput: (event: FileInputEvent) => void;
}> = (props) => {
  const [filename, setFilename] = createSignal<string>("");

  const handleFileChange = (event: Event) => {
    event.preventDefault();
    const target = event.target as HTMLInputElement;
    if (target !== null && target.files !== null && target.files.length === 1) {
      const file = target.files[0];
      if (file !== undefined) {
        setFilename(file.name);

        if (props.maxSize !== undefined && file.size > props.maxSize * 1024 * 1024) {
          window.dispatchEvent(
            new CustomEvent("app_error", {
              detail: `Error: the selected file exceeds the maximum file size of ${props.maxSize}MB`,
            }),
          );
          return;
        }

        props.onInput({ file });
      }
    }
  };

  return (
    <div class="file has-name is-fullwidth">
      <label class="file-label">
        <input
          class="file-input"
          type="file"
          accept={props.accept !== undefined ? props.accept.join(",") : undefined}
          onChange={handleFileChange}
        />
        <span class="file-cta">
          <span class="file-icon">
            <i class="fas fa-upload" />
          </span>
          <span class="file-label">Choose a file…</span>
        </span>
        <span class="file-name">{filename()}</span>
      </label>
    </div>
  );
};
