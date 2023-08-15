package vn.fs.config;

import io.jsonwebtoken.lang.Assert;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkipPathRequestMatcher implements RequestMatcher {
    private final OrRequestMatcher matchers;

    public SkipPathRequestMatcher(Map<String, List<HttpMethod>> pathsToSkip) {
        Assert.notNull(pathsToSkip, "pathsToSkip cannot be null");
        List<RequestMatcher> m = new ArrayList<>(pathsToSkip.size());
        pathsToSkip.forEach((pattern, methods) -> {
            if (!CollectionUtils.isEmpty(methods)) {
                methods.forEach(method -> m.add(new AntPathRequestMatcher(pattern, method.toString(), true)));
            } else {
                m.add(new AntPathRequestMatcher(pattern, null, true));
            }
        });
        matchers = new OrRequestMatcher(m);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return matchers.matches(request);
    }
}
