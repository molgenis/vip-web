import { Component, createSignal, Show } from "solid-js";
import { Login, User } from "../api/Api.ts";
import api from "../api/ApiClient.ts";
import { createStore } from "solid-js/store";

export const LoginModal: Component<{
  active: boolean;
  onClose: () => void;
  onLogIn: (user: User) => void;
}> = (props) => {
  const [fields, setFields] = createStore<Login>({ username: "", password: "" });
  const [help, setHelp] = createSignal<string>();
  const handleLogin = async () => {
    try {
      const user = await api.login({ username: fields.username, password: fields.password });
      setFields("username", "");
      setFields("password", "");
      setHelp();
      props.onLogIn(user);
    } catch {
      setHelp("Invalid username and/or password");
    }
  };

  const handleCancel = () => {
    props.onClose();
    setFields("username", "");
    setFields("password", "");
    setHelp();
  };

  return (
    <div classList={{ modal: true, "is-active": props.active }}>
      <div class="modal-background" onClick={handleCancel} />
      <div class="modal-content">
        <div class="box">
          {/* username */}
          <div class="field is-horizontal">
            <div class="field-label is-normal">
              <label class="label">Username</label>
            </div>
            <div class="field-body">
              <div class="field">
                <div class="control">
                  <input
                    classList={{ input: true }}
                    type="text"
                    value={fields.username}
                    onInput={(e) => {
                      setFields("username", e.target.value);
                    }}
                  />
                </div>
              </div>
            </div>
          </div>
          {/* password */}
          <div class="field is-horizontal">
            <div class="field-label is-normal">
              <label class="label">Password</label>
            </div>
            <div class="field-body">
              <div class="field">
                <div class="control">
                  <input
                    classList={{ input: true }}
                    type="password"
                    value={fields.password}
                    onInput={(e) => {
                      setFields("password", e.target.value);
                    }}
                  />
                </div>
                <Show when={help()} keyed>
                  {(help) => <p class="help is-danger">{help}</p>}
                </Show>
              </div>
            </div>
          </div>
          {/* buttons */}
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
                  <button class="button is-link" onClick={() => void handleLogin()}>
                    Log in
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
