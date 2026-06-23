package de.mymiggi.movie.api.entity.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mymiggi.movie.api.entity.TokenRole;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

/**
 * A user-owned API token. Only the SHA-256 hash of the secret is persisted; the
 * plaintext secret is shown once at creation and never stored. The token is bound
 * to the creating user's principal and snapshots their role at creation time.
 */
@Entity
public class ApiTokenEntity extends PanacheEntity
{
	public String name;

	// Sensitive: the secret's hash must never be serialized into any response.
	@JsonIgnore
	@Column(unique = true)
	public String tokenHash;

	public String principal;

	@Enumerated(EnumType.STRING)
	public TokenRole role;

	public LocalDateTime createdAt;

	public LocalDateTime lastUsedAt;

	public ApiTokenEntity()
	{
	}

	public ApiTokenEntity(String name, String tokenHash, String principal, TokenRole role)
	{
		this.name = name;
		this.tokenHash = tokenHash;
		this.principal = principal;
		this.role = role;
		this.createdAt = LocalDateTime.now();
	}
}
