package car.app.beans;

import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Component
public class VueFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(VueFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String path = request.getServletPath();
            System.out.println(path);
            if (path.startsWith("/ui")) {
                InputStream is = getClass().getResourceAsStream("/static/index.html");
                response.setContentType("text/html");
                response.setContentLength(is.available());
                try (ServletOutputStream out = response.getOutputStream()) {
                    IOUtils.copy(is, out);
                    out.flush();
                }
                return;
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().startsWith("/ui");
    }
}
