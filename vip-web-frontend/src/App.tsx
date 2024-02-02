import { Component, ErrorBoundary } from "solid-js";
import { Link, Route, Routes } from "@solidjs/router";
import { Error } from "./components/Error";
import { Home } from "./views/Home";
import JobData from "./views/data/JobData.tsx";
import { Jobs } from "./views/Jobs.tsx";
import { JobUpdate } from "./views/JobUpdate.tsx";

const App: Component = () => {
  return (
    <>
      <nav class="navbar is-fixed-top is-light" role="navigation" aria-label="main navigation">
        <div class="navbar-brand">
          <Link class="navbar-item has-text-weight-semibold" href="/">
            VIP
          </Link>
        </div>
        <div class="navbar-menu">
          <div class="navbar-start" />
          <div class="navbar-end" />
        </div>
      </nav>
      <div class="container is-fluid">
        <ErrorBoundary fallback={(err) => <Error error={err as unknown} />}>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/jobs">
              <Route path="/" element={<Jobs />} />
              <Route path="/:jobId" data={JobData} element={<JobUpdate />} />
            </Route>
          </Routes>
        </ErrorBoundary>
      </div>
    </>
  );
};

export default App;
