import { Component } from "solid-js";

export const Error: Component<{
  error: unknown;
}> = (props) => {
  // eslint-disable-next-line
  console.error(props.error);
  return <div class="notification is-danger is-light">An unexpected error occurred</div>;
};
