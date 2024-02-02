/* @refresh reload */
import { render } from "solid-js/web";

import "./assets/sass/main.scss";
import App from "./App";

import { dom, library } from "@fortawesome/fontawesome-svg-core";
import { faClone, faUpload, faTrash } from "@fortawesome/free-solid-svg-icons";
import { hashIntegration, Router } from "@solidjs/router";

library.add(faClone, faUpload, faTrash);

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
    <Router source={hashIntegration()}>
      <App />
    </Router>
  ),
  document.body,
);
