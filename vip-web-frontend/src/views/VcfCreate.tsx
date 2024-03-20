import { Component } from "solid-js";
import { useNavigate } from "@solidjs/router";
import { VcfCreateEvent, VcfUploadEvent, VcfCreateForm, VcfSelectEvent } from "../components/VcfCreateForm.tsx";
import { useStore } from "../store/store.tsx";
import { ApiError, VcfId } from "../api/Api.ts";
import axios from "axios";
import api from "../api/ApiClient.ts";

const VcfCreate: Component = () => {
  const [state, actions] = useStore();
  const navigate = useNavigate();

  const handlePreSubmit = async () => {
    // create new user for anonymous users attempting vcf upload
    if (state.user.authorities.length === 1 && state.user.authorities[0] === "ROLE_ANONYMOUS") {
      const username = self.crypto.randomUUID();
      const password = self.crypto.randomUUID();
      await api.signup({ username, password });
      const user = await api.login({ username, password });
      actions.setUser(user);
    }
  };

  const handlePostSubmit = (vcfId: VcfId, isPublic: boolean) => {
    navigate(`/jobs/create/${vcfId}?public=${isPublic}`);
  };
  const handleSubmitVcf = async (event: VcfUploadEvent) => {
    await handlePreSubmit();

    let vcfId;
    try {
      vcfId = await api.uploadVcf(event.vcf);
    } catch (error) {
      let message: string | undefined;
      if (axios.isAxiosError(error)) {
        message = (error.response?.data as ApiError).error.message;
      }
      if (message === undefined) {
        message = "Error uploading file";
      }
      window.dispatchEvent(
        new CustomEvent("app_error", {
          detail: `Error: ${message}`,
        }),
      );
      return;
    }

    handlePostSubmit(vcfId, event.vcf.isPublic);
  };

  const handleSubmitVariants = async (event: VcfCreateEvent) => {
    await handlePreSubmit();

    let vcfId;
    try {
      vcfId = await api.createVcf(event.variants);
    } catch (error) {
      let message: string | undefined;
      if (axios.isAxiosError(error)) {
        message = (error.response?.data as ApiError).error.message;
      }
      if (message === undefined) {
        message = "Error uploading variants";
      }
      window.dispatchEvent(
        new CustomEvent("app_error", {
          detail: `Error: ${message}`,
        }),
      );
      return;
    }

    handlePostSubmit(vcfId, event.variants.isPublic);
  };

  const handleSelectVcf = async (event: VcfSelectEvent) => {
    await handlePreSubmit();
    handlePostSubmit(event.vcf.id, event.vcf.isPublic);
  };

  const handleCancel = () => {
    navigate(-1);
  };

  return (
    <div class="columns is-centered mt-1">
      <div class="column is-two-thirds-widescreen">
        <h1 class="title is-4">New Job (Step 1 of 2)</h1>
        <VcfCreateForm
          onSubmitVcfUpload={(e) => void handleSubmitVcf(e)}
          onSubmitVcfCreate={(e) => void handleSubmitVariants(e)}
          onSubmitVcfSelect={(e) => void handleSelectVcf(e)}
          onCancel={() => void handleCancel()}
        />
      </div>
    </div>
  );
};

export default VcfCreate;
