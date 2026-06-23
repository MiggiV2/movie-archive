## ADDED Requirements

### Requirement: Search movies tool

The MCP server SHALL provide a `search_movies` tool that searches movies by free text across name and metadata/tags and returns matching movies, reusing the existing search behavior.

#### Scenario: Free-text recommendation query

- **WHEN** a client calls `search_movies` with a query like "thriller"
- **THEN** the tool returns movies whose name, metadata, or tags match, each with at least id, title, and year, so the assistant can recommend one

#### Scenario: Counting a franchise

- **WHEN** a client calls `search_movies` with "Star Wars"
- **THEN** the tool returns all matching movies so the assistant can report how many exist

#### Scenario: No matches

- **WHEN** a query matches no movies
- **THEN** the tool returns an empty result set rather than an error

### Requirement: Genre discovery tools

The MCP server SHALL provide `list_genres` and `find_movies_by_genre` tools so assistants can browse by genre/mood. These operate on the IMDb genres shown in the UI (`MovieMetaData.genres`), not the legacy `TagEntity` system.

#### Scenario: List available genres

- **WHEN** a client calls `list_genres`
- **THEN** the tool returns the distinct genres present in the archive

#### Scenario: Find movies for a genre

- **WHEN** a client calls `find_movies_by_genre` with a genre name (case-insensitive)
- **THEN** the tool returns the movies whose metadata includes that genre

### Requirement: Get movie detail tool

The MCP server SHALL provide a `get_movie` tool returning the full detail for a movie id.

#### Scenario: Fetch detail

- **WHEN** a client calls `get_movie` with a valid id
- **THEN** the tool returns the movie's detail including metadata and tags

#### Scenario: Unknown id

- **WHEN** a client calls `get_movie` with an id that does not exist
- **THEN** the tool returns a not-found result

### Requirement: Create and update movie tools (admin only)

The MCP server SHALL provide `create_movie` and `update_movie` write tools, available only to `ADMIN`-role tokens, that add or modify archive entries by reusing the existing admin movie actions (including metadata enrichment and audit logging).

#### Scenario: Admin adds purchased movies

- **WHEN** an `ADMIN`-role client calls `create_movie` for each newly bought movie
- **THEN** each movie is persisted with a generated uuid and enriched metadata, and the action is audit-logged

#### Scenario: Admin updates a movie

- **WHEN** an `ADMIN`-role client calls `update_movie` with an existing id and changed fields
- **THEN** the movie is updated and the change is audit-logged

#### Scenario: Non-admin attempt is rejected

- **WHEN** a `USER`-role client calls `create_movie` or `update_movie`
- **THEN** the call is denied and no movie is created or changed
