import { RestApiClient } from "./RestApiClient.ts";

// lazy import MockApiClient to ensure that it is excluded from the build artifact
const api = import.meta.env.PROD
  ? new RestApiClient()
  : await (function () {
      return import("../mocks/MockApiClient").then((module) => {
        return new module.MockApiClient();
      });
    })();
export default api;
