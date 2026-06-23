## Why

Users want to interact with the Movie Archive through AI assistants — asking things like "recommend a thriller", "how many Star Wars movies do we have?", or "add these movies we just bought". Exposing the archive as an MCP (Model Context Protocol) server lets any MCP-capable client (Claude, etc.) discover and call archive operations as tools, using a personal API token for authentication and the token owner's permissions.

## What Changes

- Add an **MCP server** to the application that exposes the archive's read and write operations as MCP tools over streamable HTTP.
- Authenticate MCP clients with the **API tokens** from the `add-api-tokens` change (`Authorization: Bearer <token>`); the MCP session acts as the token's owner with the token's snapshotted role.
- Expose **read tools** (available to any valid token):
  - `search_movies` — free-text + metadata/tag search (powers "recommend a thriller", "how many Star Wars movies").
  - `list_genres` / `find_movies_by_genre` — genre-based discovery for genre/mood queries (uses the IMDb genres shown in the UI, not the legacy `TagEntity`).
  - `get_movie` — full detail for a movie id.
- Expose **write tools** (require an `ADMIN`-role token):
  - `create_movie` and `update_movie` — add/update archive entries (powers "add these movies we bought").
- Reuse existing `actions`/`services` (search, get, add, update) so tool behavior matches the REST API; enforce admin-only on write tools via the same role check.

## Capabilities

### New Capabilities
- `mcp-server`: The MCP endpoint, its transport, tool discovery, and how MCP requests authenticate via API tokens and map to the owner's role.
- `mcp-movie-tools`: The concrete tools exposed (search/list/get read tools and admin-only create/update write tools), their inputs/outputs, and authorization.

### Modified Capabilities
<!-- Depends on add-api-tokens (token-authentication); no existing openspec/specs to modify. -->

## Impact

- **Depends on** the `add-api-tokens` change (token-authentication capability) — must ship first.
- **New dependency**: `io.quarkiverse.mcp:quarkus-mcp-server-http` (provides both Streamable HTTP and HTTP/SSE transports).
- **New code**: an `mcp/` package with `@Tool`-annotated methods delegating to existing actions/services; an `actions/mcp/` layer if orchestration is needed.
- **Security**: MCP endpoint authenticated by the Bearer mechanism from `add-api-tokens`; write tools gated on the admin role.
- **Config**: MCP server path and enablement in `application.properties` (per-profile).
- **Docs**: README section on connecting an MCP client.
