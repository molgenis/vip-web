/* @refresh reload */
import "./assets/sass/main.scss";
import { lazy } from "solid-js";
import { render } from "solid-js/web";
import { HashRouter, Route } from "@solidjs/router";
import { App } from "./App";
import { dom, library } from "@fortawesome/fontawesome-svg-core";
import { faClone, faDownload, faTrash, faUpload } from "@fortawesome/free-solid-svg-icons";
import { Provider } from "./store/store.tsx";
import Home from "./views/Home.tsx";

const Jobs = lazy(() => import("./views/Jobs.tsx"));
const JobCloneForm = lazy(() => import("./views/JobCloneForm.tsx"));
const JobCreateForm = lazy(() => import("./views/JobCreateForm.tsx"));
const VcfCreate = lazy(() => import("./views/VcfCreate.tsx"));

library.add(faClone, faDownload, faUpload, faTrash);

function processIcons() {
  void dom.i2svg();
  dom.watch();
}

if (document.readyState === "complete") {
  processIcons();
} else {
  window.addEventListener("DOMContentLoaded", processIcons);
}

render(
  () => (
    <Provider>
      <HashRouter root={App}>
        <Route path="/" component={Home} />
        <Route path="/vcf">
          <Route path="/create" component={VcfCreate} />
        </Route>
        <Route path="/jobs">
          <Route path="/" component={Jobs} />
          <Route path="/create/:vcfId" component={JobCreateForm} />
          <Route path="/clone/:jobId" component={JobCloneForm} />
        </Route>
      </HashRouter>
    </Provider>
  ),
  document.body,
);
