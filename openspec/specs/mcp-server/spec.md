# mcp-server

## Purpose

The MCP endpoint, its transport, tool discovery, and how MCP requests authenticate via API tokens and map to the token owner's role. Lets MCP-capable AI clients call archive operations as tools.

## Requirements

### Requirement: MCP server endpoint

The system SHALL expose a Model Context Protocol (MCP) server over streamable HTTP that MCP-capable clients can connect to and from which they can discover available tools.

#### Scenario: Client discovers tools

- **WHEN** an authenticated MCP client initializes a session and requests the tool list
- **THEN** the server returns the set of available tools with their names, descriptions, and input schemas

#### Scenario: Server runs alongside the existing REST API

- **WHEN** the application starts
- **THEN** the MCP endpoint is served without disabling or altering the existing `/api/v2` REST endpoints or `/ui`

### Requirement: MCP requests authenticate via API tokens

MCP client requests SHALL authenticate using an API token (`Authorization: Bearer <token>`) and act as the token's owner with the token's snapshotted role.

#### Scenario: Valid token grants access

- **WHEN** an MCP client connects with a valid API token
- **THEN** the session is authenticated as the token's owner and tool calls are authorized using the token's role

#### Scenario: Missing or invalid token is rejected

- **WHEN** an MCP client connects without a token or with an invalid/revoked token
- **THEN** the server rejects the connection/call as unauthenticated

### Requirement: Tool authorization follows token role

The MCP server SHALL authorize each tool call according to the token's role, so that read tools are available to any valid token and write tools require an `ADMIN`-role token.

#### Scenario: USER token cannot call write tools

- **WHEN** an MCP client using a `USER`-role token calls a write tool (e.g. `create_movie`)
- **THEN** the call is denied with an authorization error and no data is modified

#### Scenario: ADMIN token can call write tools

- **WHEN** an MCP client using an `ADMIN`-role token calls a write tool
- **THEN** the call is authorized and executed
