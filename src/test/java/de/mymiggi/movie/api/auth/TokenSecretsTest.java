package de.mymiggi.movie.api.auth;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenSecretsTest
{
	@Test
	void secretHasPrefix()
	{
		assertTrue(TokenSecrets.generateSecret().startsWith(TokenSecrets.PREFIX));
	}

	@Test
	void secretsAreUnique()
	{
		Set<String> secrets = new HashSet<>();
		for (int i = 0; i < 1000; i++)
		{
			secrets.add(TokenSecrets.generateSecret());
		}
		assertEquals(1000, secrets.size());
	}

	@Test
	void hashIsStable()
	{
		String secret = TokenSecrets.generateSecret();
		assertEquals(TokenSecrets.hash(secret), TokenSecrets.hash(secret));
	}

	@Test
	void hashIsHexAndDiffersPerSecret()
	{
		String hash = TokenSecrets.hash(TokenSecrets.generateSecret());
		assertEquals(64, hash.length());
		assertTrue(hash.matches("[0-9a-f]{64}"));
		assertNotEquals(TokenSecrets.hash(TokenSecrets.generateSecret()), hash);
	}
}
