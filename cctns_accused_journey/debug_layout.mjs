import { readFileSync, writeFileSync } from "node:fs";
const base = "/home/passwordisact/Desktop/Analysis/cctns_accused_journey";
const output = `${base}/debug-report.html`;
let finalHtml = readFileSync(`${base}/report.html`, "utf8");
const diagnostic = `<script>setTimeout(() => {
  const clientWidth = document.documentElement.clientWidth;
  const offenders = [...document.querySelectorAll("body *")]
    .map((node) => {
      const rect = node.getBoundingClientRect();
      return {
        tag: node.tagName,
        id: node.id || null,
        cls: typeof node.className === "string" ? node.className.slice(0, 160) : null,
        left: Math.round(rect.left),
        right: Math.round(rect.right),
        width: Math.round(rect.width),
        scrollWidth: node.scrollWidth,
        clientWidth: node.clientWidth,
      };
    })
    .filter((item) => item.right > clientWidth + 1 || item.scrollWidth > item.clientWidth + 1)
    .sort((a, b) => Math.max(b.right, b.scrollWidth) - Math.max(a.right, a.scrollWidth))
    .slice(0, 40);
  const result = {
    htmlClientWidth: clientWidth,
    htmlScrollWidth: document.documentElement.scrollWidth,
    bodyClientWidth: document.body.clientWidth,
    bodyScrollWidth: document.body.scrollWidth,
    offenders,
  };
  document.body.innerHTML = "<pre id='layout-result'></pre>";
  document.getElementById("layout-result").textContent = JSON.stringify(result, null, 2);
}, 2000);</script>`;
finalHtml = finalHtml.replace("</body>", `${diagnostic}</body>`);
writeFileSync(output, finalHtml, "utf8");
console.log(output);
