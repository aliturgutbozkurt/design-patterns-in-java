-- Pandoc filters for lesson PDFs. The Markdown stays the single source and still reads well on GitHub:
--  * ```mermaid blocks → PNG (cached by content hash), sized to fit the page
--  * relative links (../x.md, ../src/…) → absolute GitHub URLs, so they work inside a PDF
--  * <details><summary>X</summary> … </details> → a bold "X" label (answers stay visible in print)
local cache = os.getenv("MERMAID_CACHE") or "build/mermaid"
local mmdc = os.getenv("MMDC") or "npx -y @mermaid-js/mermaid-cli@12.0.0"
local MAX_WIDTH_PT = 450   -- A4 minus margins
local MAX_HEIGHT_PT = 255  -- ~9 cm, keeps a diagram and its explanation on one page

local function file_exists(path)
  local f = io.open(path, "r")
  if f then f:close() return true end
  return false
end

function CodeBlock(el)
  if el.classes[1] ~= "mermaid" then return nil end
  os.execute('mkdir -p "' .. cache .. '"')
  local hash = pandoc.sha1(el.text)
  local png = cache .. "/" .. hash .. ".png"
  if not file_exists(png) then
    local mmd = cache .. "/" .. hash .. ".mmd"
    local out = assert(io.open(mmd, "w"))
    out:write(el.text)
    out:close()
    local cmd = string.format('%s -q -i "%s" -o "%s" -s 2 -b white >/dev/null 2>&1', mmdc, mmd, png)
    local ok = os.execute(cmd)
    if not ok or not file_exists(png) then
      error("mermaid render failed for block " .. hash .. " — try: " .. cmd)
    end
  end
  if FORMAT ~= "typst" then
    return pandoc.Para({ pandoc.Image({}, png) })
  end
  -- Size from the PNG header: rendered at scale 2, CSS px → pt is 0.75.
  local f = assert(io.open(png, "rb"))
  local header = f:read(24)
  f:close()
  local w_px, h_px = string.unpack(">I4I4", header, 17)
  local w_pt, h_pt = w_px / 2 * 0.75, h_px / 2 * 0.75
  local scale = math.min(1, MAX_WIDTH_PT / w_pt, MAX_HEIGHT_PT / h_pt)
  local typ = string.format('#align(center, image("%s", width: %.1fpt))', png, w_pt * scale)
  return pandoc.RawBlock("typst", typ)
end

local REPO_BLOB = "https://github.com/aliturgutbozkurt/design-patterns-in-java/blob/main/"
local source_dir = os.getenv("SOURCE_DIR") or "."   -- repo-relative directory of the Markdown file

local function normalize(path)
  local parts = {}
  for part in path:gmatch("[^/]+") do
    if part == ".." then table.remove(parts)
    elseif part ~= "." then table.insert(parts, part) end
  end
  return table.concat(parts, "/")
end

function Link(el)
  local t = el.target
  if t:match("^%a[%w+.-]*:") or t:match("^#") then return nil end  -- absolute URL or in-page anchor
  el.target = REPO_BLOB .. normalize(source_dir .. "/" .. t)
  return el
end

function RawBlock(el)
  if el.format ~= "html" then return nil end
  local summary = el.text:match("<summary>(.-)</summary>")
  if summary then return pandoc.Para({ pandoc.Strong({ pandoc.Str(summary) }) }) end
  if el.text:match("^%s*</?details>%s*$") then return {} end
  return nil
end
