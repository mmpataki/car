package car.app.beans;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

@Component
public class UrlPathHelperFixed extends UrlPathHelper {

    public UrlPathHelperFixed() {
        super.setUrlDecode(false);
    }

    @Override
    public void setUrlDecode(boolean urlDecode) {
        if (urlDecode) {
            throw new IllegalArgumentException("Handler [" + UrlPathHelperFixed.class.getName() + "] does not support URL decoding.");
        }
    }

    @Override
    public String getServletPath(HttpServletRequest request) {
        return getOriginatingServletPath(request);
    }

    @Override
    public String getOriginatingServletPath(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }
}
