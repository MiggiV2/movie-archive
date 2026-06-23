package de.mymiggi.movie.api.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Generates API token secrets and computes their storage hash.
 * <p>
 * A secret is 256 bits of {@link SecureRandom} entropy, URL-safe Base64 encoded
 * and tagged with the {@code mvk_} prefix. Only {@code SHA-256(secret)} (hex) is
 * ever persisted; the high entropy of the secret makes an unsalted hash safe and
 * allows O(1) lookup by hash.
 */
public final class TokenSecrets
{
	public static final String PREFIX = "mvk_";

	private static final SecureRandom RANDOM = new SecureRandom();

	private TokenSecrets()
	{
	}

	public static String generateSecret()
	{
		byte[] bytes = new byte[32];
		RANDOM.nextBytes(bytes);
		return PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	public static String hash(String secret)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashed = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hashed);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new IllegalStateException("SHA-256 not available", e);
		}
	}
}
