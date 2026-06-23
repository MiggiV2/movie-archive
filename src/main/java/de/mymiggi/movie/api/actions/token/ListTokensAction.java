package de.mymiggi.movie.api.actions.token;

import de.mymiggi.movie.api.entity.db.ApiTokenEntity;
import de.mymiggi.movie.api.entity.token.TokenView;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ListTokensAction
{
	public List<TokenView> run(String principal)
	{
		return ApiTokenEntity.<ApiTokenEntity>list("principal", principal)
			.stream()
			.map(TokenView::from)
			.toList();
	}
}
