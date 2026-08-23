import { readFileSync, writeFileSync } from "node:fs";
import { buildPortableArtifact } from "/home/passwordisact/.codex/plugins/cache/openai-curated-remote/data-analytics/0.2.8-13ceeea1f599/skills/build-report/scripts/build_portable_artifact.mjs";
import { extractPortableChartSvgs } from "/home/passwordisact/.codex/plugins/cache/openai-curated-remote/data-analytics/0.2.8-13ceeea1f599/skills/build-report/scripts/extract_portable_chart_svgs.mjs";

const base = "/home/passwordisact/Desktop/Analysis/cctns_accused_journey";
const artifact = JSON.parse(readFileSync(`${base}/artifact.json`, "utf8"));
const output = `${base}/report.html`;
writeFileSync(output, buildPortableArtifact(artifact), "utf8");
const staticCharts = await extractPortableChartSvgs({
  htmlPath: output,
  readyTimeoutMs: 20_000,
  actionTimeoutMs: 10_000,
});
let html = buildPortableArtifact(artifact, { staticCharts });
html = html.replace(
  "</head>",
  "<style>html,body{overflow-x:hidden}.portable-page-header{width:100%;margin-left:0;margin-right:0}</style></head>",
);
writeFileSync(output, html, "utf8");
console.log(output);
