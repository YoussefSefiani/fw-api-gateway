package fw.apigateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import fw.apigateway.feign.UserClientProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AccessFilter extends ZuulFilter {

    @Autowired
    UserClientProxy proxyService;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestURI = request.getRequestURI();
        String token = request.getHeader("Authorization");
        try {

            proxyService.check(token);
            return null;
        } catch (Exception e) {
            if (
                    token == null
                            && !requestURI.equals("/api/login")
                            && !requestURI.equals("/api/user")
                            || requestURI.contains("/api/influencer")
                            || requestURI.contains("/api/brand")
            ) {
                ctx.setSendZuulResponse(false);
                ctx.setResponseBody("Invalid token or no token");
                ctx.setResponseStatusCode(403);
                return null;
            }
            return null;
        }
    }
}
