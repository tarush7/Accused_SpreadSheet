# CCTNS accused journey viewer

`index.html` contains the complete diagram, styles, table directory, and viewer
code. It works when opened locally and on GitHub Pages, with no build step,
external fonts, CDN scripts, or network requests.

## Publish with GitHub Pages

1. Put the contents of this folder in the root of the GitHub repository you want
   to publish. Keep `index.html` at the root, rather than inside an extra folder.
2. In the repository, open **Settings → Pages**.
3. Under **Build and deployment**, select **Deploy from a branch**.
4. Select your publishing branch (`gh-pages` for the dedicated website branch)
   and **/(root)**, then **Save**.
5. When deployment finishes, copy the website URL shown by GitHub Pages into
   your Google spreadsheet.

Official setup guide:
https://docs.github.com/en/pages/getting-started-with-github-pages/configuring-a-publishing-source-for-your-github-pages-site

Only this folder is needed for publication. The original analysis workspace is
not part of the website.

## Publish with Netlify

Import the GitHub repository and select `gh-pages` as the production branch.
Choose no framework, leave the build command empty, and set the publish directory
to `.`. Pushes to `gh-pages` will then deploy the website automatically.

https://docs.netlify.com/deploy/create-deploys/

## Explore

- Search table names, schemas, or PK/FK column names. Press Enter to jump through
  matches, or Shift+Enter to move backward.
- Select a table in the directory to center it. Select a diagram node to show
  its full table name and keys.
- Drag to pan; scroll to zoom. On touchscreens, drag with one finger and pinch
  with two fingers.
- **FIR root** returns to the starting table; **Fit all** shows the whole map.
- Focus the map for arrow-key panning, `+`/`-` zoom, `0` to fit, or `Home` to
  return to the FIR root. Press `/` to focus search; Escape clears search.
- Download the original Mermaid source or the full SVG from the header.
- Optional: append `#court.t_accused_remand` (or any exact table name) to the
  page URL to open centered on that table.

The source labels and relationships are preserved. A solid arrow represents a
confirmed FK link, a thick arrow represents journey progression, and a dashed
arrow represents a logical or optional link. `—` means no FK returned within
reviewed coverage; “not verified” means coverage is incomplete.

## Update the diagram

The rendered diagram is embedded, so uploading a changed `.mmd` alone will not
update the page. From the parent analysis folder, edit the original
`CCTNS_Accused_Journey_Simplified_Vertical.mmd`, then rebuild using Python,
Playwright, an installed Chromium browser, and a local Mermaid 10.9.3 UMD script:

```bash
python3 build_journey_page.py --mermaid-js /path/to/mermaid.min.js
```

Pass `--chromium /path/to/chromium` if Playwright's default browser is unavailable.
The page template is `journey_page_template.html`. Publish the regenerated
`index.html` and source snapshot afterward.

SVG layout generated with Mermaid 10.9.3 (MIT license):
https://github.com/mermaid-js/mermaid/tree/v10.9.3
