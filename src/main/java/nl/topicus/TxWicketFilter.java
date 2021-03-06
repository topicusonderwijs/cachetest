package nl.topicus;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.transaction.Transactional;

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
