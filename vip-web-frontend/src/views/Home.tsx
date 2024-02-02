import { Component } from "solid-js";
import { useNavigate } from "@solidjs/router";

export const Home: Component = () => {
  const navigate = useNavigate();

  const handleLaunchClick = (event: Event) => {
    event.preventDefault();
    navigate(`/jobs`);
  };

  return (
    <>
      <div class="block">
        VIP is a flexible human variant interpretation pipeline for rare disease using state-of-the-art pathogenicity
        prediction (CAPICE) and template-based interactive reporting to facilitate decision support.
      </div>
      <button class="button is-large is-primary" onClick={handleLaunchClick}>
        Launch
      </button>
    </>
  );
};
