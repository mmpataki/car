package car.app.beans;

import car.app.models.Credential;
import car.app.service.api.UserGroupService;
import car.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Component
public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Autowired
    UserGroupService AS;

    static Random R = new Random();
    Map<String, SessionHeader> sessMap = new HashMap<>();

    private static class SessionHeader {
        String user;
        String tok = R.nextDouble() + "";

        public SessionHeader(String user) {
            this.user = user;
        }

        public SessionHeader(String user, String tok) {
            this.user = user;
            this.tok = tok;
        }

        static SessionHeader fromHeader(String s) {
            return new SessionHeader(s.split(":")[0], s.split(":")[1]);
        }

        @Override
        public String toString() {
            return String.format("%s:%s", user, tok);
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getTok() {
            return tok;
        }

        public void setTok(String tok) {
            this.tok = tok;
        }
    }

    private final static List<Pattern> allowedUrls = new ArrayList<Pattern>() {
        {
            add("/auth/register");
            add("/nocors");
            add(".*/query/query");
        }

        void add(String s) {
            add(Pattern.compile(s));
        }
    };


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = ((HttpServletResponse) servletResponse);
        String url = req.getServletPath().replaceFirst("/api", "");
        String method = req.getMethod();
        String ip = req.getRemoteAddr();

        String tName = Thread.currentThread().getName();
        Thread.currentThread().setName(method + " " + url);

        try {

            resp.setHeader("Access-Control-Allow-Credentials", "true");
            resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
            resp.setHeader("Access-Control-Expose-Headers", "*");
            resp.setHeader("Access-Control-Allow-Headers", "authorization,authinfo,content-type");
            resp.setHeader("Access-Control-Allow-Methods", "POST,PUT,DELETE");

            try {
                if (!method.equals("OPTIONS")) {
                    if (url.equals("/auth/login")) {
                        if (!doLogin(req, resp, true)) {
                            sendNoAuth(req, resp);
                        }
                        return;
                    } else if (url.equals("/auth/logout")) {
                        doLogout(req, resp);
                        return;
                    } else {
                        if (allowedUrls.stream().noneMatch(x -> x.matcher(url).matches())) {
                            if (!isLoggedIn(req, resp)) {
                                if (!method.equals("GET")) {
                                    sendNoAuth(req, resp);
                                    return;
                                } else {
                                    SecurityUtil.setCurrentUser("public");
                                }
                            }
                        }
                    }
                }

                if (!url.equals("/auth/login") && !url.equals("/auth/logout"))
                    filterChain.doFilter(servletRequest, servletResponse);

                Object user = SecurityUtil.getCurrentUser();
                log.info("[{}] - ({}) - {} {} - {}", user == null ? "--" : user.toString(), ip, method, url, resp.getStatus());
            } finally {
                SecurityUtil.unsetCurrentUser();
            }
        } finally {
            Thread.currentThread().setName(tName);
        }
    }

    private boolean isLoggedIn(HttpServletRequest req, HttpServletResponse resp) {
        String sessHdr = req.getHeader("AuthInfo");
        String authHdr = req.getHeader("Authorization");

        if (authHdr != null) {
            return doLogin(req, resp, false);
        } else if (sessHdr != null) {
            SessionHeader sh = SessionHeader.fromHeader(sessHdr);
            boolean ret = sessMap.containsKey(sh.getUser()) && sessMap.get(sh.getUser()).getTok().equals(sh.getTok());
            if (ret) {
                SecurityUtil.setCurrentUser(sh.getUser());
                return ret;
            }
        } else {
            if (req.getCookies() == null)
                return false;
            Optional<String> user = Arrays.stream(req.getCookies()).filter(c -> c.getName().equals("user")).map(c -> c.getValue()).findFirst();
            Optional<String> tok = Arrays.stream(req.getCookies()).filter(c -> c.getName().equals("tok")).map(c -> c.getValue()).findFirst();
            if (user.isPresent() && tok.isPresent() && sessMap.containsKey(user.get()) && sessMap.get(user.get()).getTok().equals(tok.get())) {
                SecurityUtil.setCurrentUser(user.get());
                return true;
            }
        }
        return false;
    }

    private void doLogout(HttpServletRequest req, HttpServletResponse resp) {
        String sessHdr = req.getHeader("AuthInfo");
        if (sessHdr != null) {
            SessionHeader sh = SessionHeader.fromHeader(sessHdr);
            if (isLoggedIn(req, resp) && sessMap.containsKey(sh.getUser())) {
                sessMap.remove(sh.getUser());
            }
        }
    }

    private boolean doLogin(HttpServletRequest req, HttpServletResponse resp, boolean makeSess) {
        String authHdr = req.getHeader("Authorization");
        if (authHdr != null) {
            Credential cred = makeCredential(authHdr);
            try {
                if (AS.authenticate(cred)) {
                    if (makeSess) {
                        SessionHeader sh = new SessionHeader(cred.getUserName());
                        sessMap.put(cred.getUserName(), sh);
                        resp.setHeader("AuthInfo", sh.toString());
                    }
                    SecurityUtil.setCurrentUser(cred.getUserName());
                    return true;
                }
            } catch (Exception e) {
                log.error("error while logging in: ", e);
            }
        }
        return false;
    }

    private Credential makeCredential(String authHdr) {
        String chunks[] = new String(Base64.getDecoder().decode(authHdr.split(" ")[1].getBytes())).split(":");
        return Credential.builder().userName(chunks[0]).password(chunks[1]).build();
    }

    private void sendNoAuth(HttpServletRequest req, HttpServletResponse r) throws IOException {
        r.setStatus(401);
        r.setHeader("401", "Unauthorized");
        //r.setHeader("WWW-Authenticate", "Basic");
        r.getOutputStream().write("{\"message\": \"Unauthorized, login first!\"}".getBytes());
    }

    @Override
    public void destroy() {

    }
}
