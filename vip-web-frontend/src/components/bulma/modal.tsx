import { ParentComponent } from "solid-js";

const Modal: ParentComponent<{
  onClose: () => void;
}> = (props) => {
  return (
    <div class="modal is-active">
      <div class="modal-background" onClick={() => props.onClose()} />
      <div class="modal-content">{props.children}</div>
    </div>
  );
};

export default Modal;
