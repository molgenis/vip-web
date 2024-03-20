import { ParentComponent } from "solid-js";

type type = "primary" | "link" | "info" | "success" | "warning" | "danger";

const Notification: ParentComponent<{
  type: type;
  onClose: () => void;
}> = (props) => {
  return (
    <div class={`notification is-${props.type} is-light`}>
      <button class="delete" onClick={() => props.onClose()} />
      {props.children}
    </div>
  );
};

export default Notification;
