package nl.topicus;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.transaction.Transactional;

import org.apache.wicket.protocol.http.WicketFilter;

@WebFilter(filterName = "app", value = "/*", initParams = {
		@WebInitParam(name = "applicationClassName", value = "nl.topicus.WicketApplication"),
		@WebInitParam(name = "filterMappingUrlPattern", value = "/*") })
public class TxWicketFilter extends WicketFilter {
	@Inject
	private DAO dao;

	@Transactional(Transactional.TxType.REQUIRED)
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		super.doFilter(request, response, chain);
		if (Boolean.TRUE.equals(request.getAttribute("rollback"))) {
			dao.markForRollback();
		}
	}
}
