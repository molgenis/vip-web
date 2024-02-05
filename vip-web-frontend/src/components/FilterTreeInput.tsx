import { Component, createResource, For, onMount, Show } from "solid-js";
import { FilterTree, FilterTreeId, FilterTreeType } from "../api/Api.ts";
import { Loader } from "./Loader.tsx";
import api from "../api/ApiClient.ts";

export type FilterTreeEvent = {
  tree: FilterTree;
};

export const FilterTreeInput: Component<{
  type: keyof typeof FilterTreeType;
  tree: FilterTree;
  onSelectTree: (event: FilterTreeEvent) => void;
}> = (props) => {
  const [filterTrees] = createResource({}, () => api.fetchFilterTrees(props.type));

  const handleTreeChange = (filterTreeId: FilterTreeId) => {
    const trees = filterTrees();
    if (trees == undefined) throw new Error();

    const filterTree = trees.content.find((filterTree) => filterTree.id === filterTreeId);
    if (filterTree === undefined) throw new Error();

    props.onSelectTree({ tree: filterTree });
  };

  onMount(() => {});

  return (
    <>
      <div class="control">
        <div classList={{ select: true, "is-loading": filterTrees.loading }}>
          <Show when={!filterTrees.loading} fallback={<Loader />}>
            <Show when={filterTrees()} keyed>
              {(filterTrees) => (
                <select
                  onChange={(event) => {
                    event.preventDefault();
                    handleTreeChange(Number(event.currentTarget.value));
                  }}
                >
                  <For each={filterTrees.content}>
                    {(filterTree, index) => (
                      <option
                        value={filterTree.id}
                        selected={props.tree !== undefined ? props.tree.id === filterTree.id : index() === 0}
                      >
                        {filterTree.name + (filterTree.description ? ` (${filterTree.description})` : "")}
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
