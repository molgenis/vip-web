import { hashIntegration, Router } from "@solidjs/router";
import { Context, createContext, ParentComponent, useContext } from "solid-js";
import { createStore } from "solid-js/store";
import { User } from "../api/Api.ts";

export type AppState = {
  user: User;
};

export type AppActions = {
  setUser(user: User): void;
  clearUser(): void;
};

export type AppStore = [state: AppState, actions: AppActions];

const anonymousUser: User = {
  id: -1,
  username: "anonymous",
  authorities: ["ROLE_ANONYMOUS"],
};
const defaultState: AppState = { user: anonymousUser };

const StoreContext = createContext<AppStore>() as Context<AppStore>;

export const Provider: ParentComponent = (props) => {
  const [state, setState] = createStore(defaultState);

  const actions: AppActions = {
    setUser(user: User) {
      setState({ user: user });
    },
    clearUser() {
      setState({ user: anonymousUser });
    },
  };
  const store: AppStore = [state, actions];

  return (
    <Router source={hashIntegration()}>
      <StoreContext.Provider value={store}>{props.children}</StoreContext.Provider>
    </Router>
  );
};

export function useStore() {
  return useContext(StoreContext);
}
