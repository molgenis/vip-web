import { Component } from "solid-js";
import { useNavigate } from "@solidjs/router";

const Home: Component = () => {
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
                    <li>Analysis of single patients, duo/trio and complex family pedigrees</li>
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
                    <p class="title is-4">Web application</p>
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
                    <p class="title is-4">Command-line application</p>
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
                    <a
                      href="https://doi.org/10.1101/2024.04.11.24305656"
                      target="_blank"
                      rel="noopener noreferrer nofollow"
                      class="card-footer-item"
                    >
                      Preprint
                    </a>
                  </footer>
                </div>
              </div>
            </div>
          </div>
          <hr />
          <div class="columns mt-3">
            <div class="column is-half">
              <article class="message is-info">
                <div class="message-body">
                  <p class="title is-6">CAPICE</p>
                  <div class="content">
                    <p>
                      At the heart of VIP lies CAPICE, a machine-learning-based method for prioritizing pathogenic
                      variants, including SNVs and short InDels. CAPICE outperforms the best general (
                      <a href="http://dx.doi.org/10.1093/nar/gky1016">CADD</a>,{" "}
                      <a href="https://doi.org/10.1186/s13059-016-1141-7">GAVIN</a>) and consequence-type-specific (
                      <a href="https://doi.org/10.1016/j.ajhg.2016.08.016">REVEL</a>,{" "}
                      <a href="https://doi.org/10.1016/j.ajhg.2018.08.005">ClinPred</a>) computational prediction
                      methods, for both rare and ultra-rare variants.
                    </p>
                    <span class="is-size-7">
                      <a href="https://doi.org/10.1186/s13073-020-00775-w">
                        Li, S., van der Velde, K.J., de Ridder, D. et al. CAPICE: a computational method for
                        Consequence-Agnostic Pathogenicity Interpretation of Clinical Exome variations. Genome Med 12,
                        75 (2020)
                      </a>
                    </span>
                  </div>
                </div>
              </article>
            </div>
            <div class="column is-half">
              <article class="message is-info">
                <div class="message-body">
                  <p class="title is-6">Annotations</p>
                  <div class="content">
                    <p>
                      VIP provides an extensive up-to-date set of both coding and non-coding annotations. Annotations
                      include <a href="https://doi.org/10.1038/s41586-020-2308-7">gnomAD</a>,{" "}
                      <a href="https://doi.org/10.1093/nar/gkv1222">ClinVar</a>,{" "}
                      <a href="https://doi.org/10.1101/gr.3715005">phyloP</a>,{" "}
                      <a href="https://doi.org/10.1093/bioinformatics/btad280">AlphScore</a>,{" "}
                      <a href="https://doi.org/10.1093/bioinformatics/btv009">FATHMM-MKL</a>,{" "}
                      <a href="https://doi.org/10.1038/s41467-019-13212-3">ncER</a>,{" "}
                      <a href="https://doi.org/10.1016/j.cell.2018.12.015">SpliceAI</a>,{" "}
                      <a href="https://doi.org/10.1016/j.ajhg.2016.07.005">REMM</a>,{" "}
                      <a href="https://doi.org/10.1093/nar/gkg509">SIFT</a> and{" "}
                      <a href="https://doi.org/10.1002/0471142905.hg0720s76">PolyPhen</a>. Inheritance annotations are
                      provided by our custom{" "}
                      <a href="https://github.com/molgenis/vip-inheritance-matcher">inheritance matcher</a> and include
                      denovo, compound and inheritance pattern match information. Coding and non-coding structural
                      variant annotations are provided by <a href="https://10.1093/bioinformatics/bty304">AnnotSV</a>.
                    </p>
                    <span class="is-size-7">
                      Consult the <a href="https://molgenis.github.io/vip/advanced/annotations/">documentation</a> for
                      the full list of annotations.
                    </span>
                  </div>
                </div>
              </article>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Home;
