import { Component } from "solid-js";

export type Error = {
  message: string;
};
export const ErrorNotification: Component<{
  error: Error;
  onClose: () => void;
}> = (props) => {
  return (
    <div class="notification is-danger is-light">
      <button class="delete" onClick={() => props.onClose()} />
      {props.error.message}
    </div>
  );
};
