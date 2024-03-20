import { createSignal, onMount, ParentComponent, Show } from "solid-js";
import { useNavigate } from "@solidjs/router";
import { useStore } from "./store/store.tsx";
import { User } from "./api/Api.ts";
import { Navbar } from "./components/Navbar.tsx";
import Notification from "./components/bulma/notification.tsx";
import api from "./api/ApiClient.ts";
import LoginModal from "./components/LoginModal.tsx";

export type Error = {
  message: string;
};

export const App: ParentComponent = (props) => {
  const navigate = useNavigate();
  const [, actions] = useStore();
  const [isModalOpen, setIsModalOpen] = createSignal<boolean>(false);
  const [error, setError] = createSignal<Error>();

  window.addEventListener("unhandledrejection", function () {
    setError({ message: "An unexpected error occurred" });
  });

  window.addEventListener("error", () => {
    setError({ message: "An unexpected error occurred" });
  });

  window.addEventListener("app_error", (event) => {
    setError({ message: (event as CustomEvent).detail as string });
  });

  const handleLogin = (user: User) => {
    setIsModalOpen(false);
    actions.setUser(user);
    navigate("/");
  };
  const handleLogout = async () => {
    await api.logout();
    actions.clearUser();
    navigate("/");
  };

  onMount(() => {
    (async () => {
      const user = await api.fetchUser();
      actions.setUser(user);
    })().catch((err) => console.error(err));
  });

  return (
    <>
      <Navbar onLogin={() => setIsModalOpen(true)} onLogout={() => void handleLogout()} />
      <Show when={error()} keyed>
        {(error) => (
          <Notification type="danger" onClose={() => setError()}>
            {error.message}
          </Notification>
        )}
      </Show>
      <div class="container is-fluid">{props.children}</div>
      <Show when={isModalOpen()}>
        <LoginModal onClose={() => setIsModalOpen(false)} onLogIn={(user: User) => handleLogin(user)} />
      </Show>
    </>
  );
};
