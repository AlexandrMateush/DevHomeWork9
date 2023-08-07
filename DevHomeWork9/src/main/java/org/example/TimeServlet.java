package org.example;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.example.TimezoneValidator.validateTimezone;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine templateEngine;

    private static final String TIMEZONE_COOKIE_NAME = "lastTimezone";
    @Override
    public void init() throws ServletException {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
        Date currentDate = new Date();
        String timezoneParam = request.getParameter("timezone");
        TimeZone timezone = parseTimeZone(timezoneParam);
        String currentTime = convertToTimeZoneFormat(currentDate, timezone);

        response.getWriter().println("<html><body>");
        response.getWriter().println("<h1>Поточний час (" + timezone.getID() + ")</h1>");
        response.getWriter().println("<p>" + currentTime + "</p>");
        response.getWriter().println("</body></html>");

    }


    private void createTimezoneCookie(HttpServletRequest request, HttpServletResponse response, String timezone) {
        Cookie timezoneCookie = new Cookie("lastTimezone", timezone);
        timezoneCookie.setPath(request.getContextPath());
        response.addCookie(timezoneCookie);
    }
    private boolean validateTimezone(String timezone) {
        return TimeZone.getTimeZone(timezone) != null;
    }
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String timezone = request.getParameter("timezone");

        if (timezone != null && !timezone.isEmpty()) {
            if (validateTimezone(timezone)) {
                createTimezoneCookie(request, response, timezone);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid timezone");
            }
        }
    }

    private String getLastTimezoneFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("lastTimezone".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private TimeZone parseTimeZone(String timezoneParam) {
        if (timezoneParam != null && timezoneParam.matches("^UTC[+-]\\d{1,2}$")) {
            try {
                int totalOffset = Integer.parseInt(timezoneParam.substring(3));
                if (totalOffset >= -12 * 60 && totalOffset <= 14 * 60) {
                    if( totalOffset < 0 && totalOffset > -13){
                        return TimeZone.getTimeZone("GMT" + totalOffset);
                    }return TimeZone.getTimeZone("GMT" + (totalOffset > 0 && totalOffset < 15 ? "+" : " ") + totalOffset);
                }
            } catch (NumberFormatException e) {
            }
        }
        return TimeZone.getTimeZone("UTC");
    }

    private String convertToTimeZoneFormat(Date date, TimeZone timezone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(timezone);
        String formattedDate = sdf.format(date);
        int offsetInMillis = timezone.getOffset(date.getTime());
        int offsetHours = offsetInMillis / (60 * 60 * 1000);
        return formattedDate.replace("GMT", "UTC") ;
    }
}
