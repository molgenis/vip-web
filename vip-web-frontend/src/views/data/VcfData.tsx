import { createResource, Resource } from "solid-js";
import { RouteDataFuncArgs } from "@solidjs/router";
import { Vcf } from "../../api/Api.ts";
import api from "../../api/ApiClient.ts";

export type VcfRouteData = {
  vcf: Resource<Vcf>;
  isPublic: boolean;
};

export default function VcfData({ params, location }: RouteDataFuncArgs): VcfRouteData {
  const [vcf] = createResource(() => Number(params.vcfId), api.fetchVcf);
  const isPublic = location.query["public"] === "true";
  return { vcf, isPublic };
}
