package org.example;


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.example.TimezoneValidator.validateTimezone;


@WebFilter("/time")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String timezoneParam = request.getParameter("timezone");
        if (timezoneParam != null) {
            String validatedTimezone = validateTimezone(timezoneParam);
            if (validatedTimezone != null) {
                httpRequest.setAttribute("timezone", validatedTimezone);
            }
        }

        chain.doFilter(request, response);
    }
    private void createTimezoneCookie(HttpServletRequest request, HttpServletResponse response, String timezone) {
        Cookie timezoneCookie = new Cookie("lastTimezone", timezone);
        timezoneCookie.setPath(request.getContextPath());
        response.addCookie(timezoneCookie);
    }

    @Override
    public void destroy() {
    }

    private boolean isValidTimezone(String timezone) {
        if (timezone == null || timezone.isEmpty()) {
            return true;
        } else if (!timezone.startsWith("UTC")) {
            return false;
        }
        try {
            int utcOffset = Integer.parseInt(timezone.substring(3));
            if (utcOffset > -13 && utcOffset < 15) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
