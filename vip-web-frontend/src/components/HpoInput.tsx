import { Component, onMount } from "solid-js";
import BulmaTagsInput from "@creativebulma/bulma-tagsinput";
import { HpoTerm } from "../api/Api.ts";
import api from "../api/ApiClient.ts";

export type TermEvent = {
  term: HpoTerm;
};

type HpoTermTag = {
  id: string;
  termAndName: string;
};

function mapHpoTermToTag(hpoTerm: HpoTerm): HpoTermTag {
  return { id: hpoTerm.id.toString(), termAndName: hpoTerm.term + " " + hpoTerm.name };
}

function mapTagToHpoTerm(tag: HpoTermTag): HpoTerm {
  return {
    id: Number(tag.id),
    term: tag.termAndName.substring(0, 10),
    name: tag.termAndName.substring(11),
  };
}

export const HpoInput: Component<{
  hpoTerms: HpoTerm[];
  onAddTerm: (event: TermEvent) => void;
  onRemoveTerm: (event: TermEvent) => void;
}> = (props) => {
  let tagsInputRef: HTMLInputElement | undefined;
  onMount(() => {
    if (!tagsInputRef) return;

    const bulmaTagsInput = new BulmaTagsInput(tagsInputRef, {
      caseSensitive: false,
      freeInput: false,
      source: async (value: string) => {
        const hpoTerms = await api.fetchHpoTerms(value);
        return hpoTerms.content.map((hpoTerm) => mapHpoTermToTag(hpoTerm));
      },
      itemText: "termAndName",
      itemValue: "id",
      searchMinChars: 0,
      selectable: false,
      tagClass: undefined,
      trim: false,
    });
    bulmaTagsInput.on("after.add", (data: { item: HpoTermTag }) => {
      props.onAddTerm({
        term: mapTagToHpoTerm(data.item),
      });
    });
    bulmaTagsInput.on("after.remove", (data: { item: HpoTermTag }) => {
      props.onRemoveTerm({
        term: mapTagToHpoTerm(data.item),
      });
    });
  });
  return (
    <div class="control is-fullwidth">
      <input
        ref={tagsInputRef}
        class="input"
        type="text"
        placeholder="Search for HPO terms..."
        value={JSON.stringify(props.hpoTerms.map((hpoTerm) => mapHpoTermToTag(hpoTerm)))}
      />
    </div>
  );
};
