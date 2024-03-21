import { Component, Show } from "solid-js";
import logoImgUrl from "../assets/img/logo.png";
import { useStore } from "../store/store.tsx";

export const Navbar: Component<{
  onLogin: () => void;
  onLogout: () => void;
}> = (props) => {
  const [state] = useStore();

  return (
    <nav class="navbar is-fixed-top is-info" role="navigation" aria-label="main navigation">
      <div class="navbar-brand">
        <a class="navbar-item" href="/">
          <img src={logoImgUrl} alt="Logo" />
        </a>
      </div>
      <div class="navbar-menu">
        <div class="navbar-start">
          <a class="navbar-item" href="/">
            Variant Interpretation Pipeline
          </a>
        </div>
        <div class="navbar-end">
          <div class="navbar-item">
            <div class="buttons">
              <Show
                when={
                  state.user && state.user.authorities.length === 1 && state.user.authorities[0] === "ROLE_ANONYMOUS"
                }
                fallback={
                  <a class="button is-outlined is-white" onClick={() => props.onLogout()}>
                    Log out
                  </a>
                }
              >
                <a class="button is-outlined is-white" onClick={() => props.onLogin()}>
                  Log in
                </a>
              </Show>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};
