import { Component, createSignal, onMount, Show } from "solid-js";
import { Route, Routes, useNavigate } from "@solidjs/router";
import { Home } from "./views/Home";
import { Jobs } from "./views/Jobs.tsx";
import { JobCreateForm } from "./views/JobCreateForm.tsx";
import VcfData from "./views/data/VcfData.tsx";
import JobData from "./views/data/JobData.tsx";
import { JobCloneForm } from "./views/JobCloneForm.tsx";
import { LoginModal } from "./components/LoginModal.tsx";
import { useStore } from "./store/store.tsx";
import { User } from "./api/Api.ts";
import { VcfCreate } from "./views/VcfCreate.tsx";
import { Navbar } from "./components/Navbar.tsx";
import { Error, ErrorNotification } from "./components/ErrorNotification.tsx";
import api from "./api/ApiClient.ts";

const App: Component = () => {
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
        {(error) => <ErrorNotification error={error} onClose={() => setError()} />}
      </Show>
      <div class="container is-fluid">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/vcf">
            <Route path="/create" element={<VcfCreate />} />
          </Route>
          <Route path="/jobs">
            <Route path="/" element={<Jobs />} />
            <Route path="/create/:vcfId" data={VcfData} element={<JobCreateForm />} />
            <Route path="/clone/:jobId" data={JobData} element={<JobCloneForm />} />
          </Route>
        </Routes>
      </div>
      <LoginModal
        active={isModalOpen()}
        onClose={() => setIsModalOpen(false)}
        onLogIn={(user: User) => handleLogin(user)}
      />
    </>
  );
};

export default App;
