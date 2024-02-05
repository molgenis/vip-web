import { Component } from "solid-js";
import { useNavigate } from "@solidjs/router";

export const Home: Component = () => {
  const navigate = useNavigate();

  const handleLaunchClick = (event: Event) => {
    event.preventDefault();
    navigate(`/vcf/create`);
  };

  const handleSeeJobsClick = (event: Event) => {
    event.preventDefault();
    navigate(`/jobs`);
  };

  return (
    <>
      <div class="columns mt-1">
        <div class="column">
          <h1 class="title">Variant Interpretation Pipeline (VIP)</h1>
          <h2 class="subtitle is-4">
            Discover, annotate, classify, filter and report variants to <strong>solve rare-disease patients</strong>
          </h2>
          <div class="columns">
            <div class="column">
              <div class="block">
                <div class="content">
                  <p>
                    VIP is an easy to install, easy to use, portable and flexible pipeline implemented using Nextflow.
                    Features include:
                  </p>
                  <ul>
                    <li>
                      Workflows for a broad range of input file types: <strong>bam</strong>, <strong>cram</strong>,{" "}
                      <strong>fastq</strong>, <strong>g.vcf</strong>, <strong>vcf</strong>
                    </li>
                    <li>
                      Produces stand-alone variant interpretation HTML <strong>report</strong> with integrated genome
                      browser
                    </li>
                    <li>
                      <strong>Short-read</strong> and <strong>Long-read</strong> sequencing support (Illumina, Oxford
                      Nanopore, PacBio HiFi)
                    </li>
                    <li>
                      Supports <strong>GRCh38</strong>, supports GRCh37 and T2T input via liftover
                    </li>
                    <li>Detects short variants, structural variants and short tandem repeats</li>
                    <li>
                      Rich set of variant <strong>annotations</strong>
                    </li>
                    <li>
                      Pathogenic variant <strong>prioritization</strong> (CAPICE)
                    </li>
                    <li>Phenotype support (HPO)</li>
                    <li>Inheritance matching</li>
                    <li>
                      Variant <strong>classification and filtration</strong> using customizable decision trees
                    </li>
                    <li>Variant reporting using customizable report templates</li>
                  </ul>
                </div>
              </div>
            </div>
            <div class="column">
              <div class="block">
                <div class="card">
                  <div class="card-content">
                    <div class="media">
                      <div class="media-content">
                        <p class="title is-4">Web application</p>
                      </div>
                    </div>
                    <div class="content">
                      Demonstrates the VCF workflow: see example reports or run with your own small data
                    </div>
                  </div>
                  <footer class="card-footer">
                    <button class="button is-large is-primary card-footer-item" onClick={handleLaunchClick}>
                      Launch
                    </button>
                    <div class="card-footer-item">
                      <a href="#" onClick={handleSeeJobsClick}>
                        See Jobs & Examples
                      </a>
                    </div>
                  </footer>
                </div>
              </div>
              <div class="block">
                <div class="card mt-3">
                  <div class="card-content">
                    <div class="media">
                      <div class="media-content">
                        <p class="title is-4">Command-line application</p>
                      </div>
                    </div>
                    <div class="content">
                      Install and use VIP on your own system: enables all workflows and allows running with large data
                    </div>
                  </div>
                  <footer class="card-footer">
                    <a
                      href="https://molgenis.github.io/vip/"
                      target="_blank"
                      rel="noopener noreferrer nofollow"
                      class="card-footer-item"
                    >
                      Documentation
                    </a>
                    <a
                      href="https://github.com/molgenis/vip/"
                      target="_blank"
                      rel="noopener noreferrer nofollow"
                      class="card-footer-item"
                    >
                      GitHub
                    </a>
                  </footer>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
