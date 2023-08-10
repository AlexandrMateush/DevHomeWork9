package org.example;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        templateEngine = new TemplateEngine();
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix(getClass().getClassLoader().getResource("templates").getPath());
        templateResolver.setSuffix(".html");
        templateResolver.setOrder(templateEngine.getTemplateResolvers().size());
        templateEngine.setTemplateResolver(templateResolver);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void createTimezoneCookie(HttpServletRequest request, HttpServletResponse response, String timezone) {
        Cookie timezoneCookie = new Cookie("lastTimezone", timezone);
        response.addCookie(timezoneCookie);
    }
    private boolean validateTimezone(String timezone) {
        return TimeZone.getTimeZone(timezone) != null;
    }
    void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String timezone = URLEncoder.encode(request.getParameter("timezone"), StandardCharsets.UTF_8);

        if (timezone != null && !timezone.isEmpty()) {
            if (validateTimezone(timezone)) {
                createTimezoneCookie(request, response, timezone);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid timezone");
                return;
            }
        }

        String lastTimezone = getLastTimezoneFromCookie(request).orElse(timezone);
        if (lastTimezone != null) {
            String currentTimeFormatted = getCurrentTimeFormatted(lastTimezone);

            WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
            context.setVariable("timezone", lastTimezone);
            context.setVariable("currentTime", currentTimeFormatted);
            templateEngine.process("time", context, response.getWriter());
        }
    }
    private String getCurrentTimeFormatted(String timezone) {
        TimeZone selectedTimeZone = TimeZone.getTimeZone(timezone);
        Date currentTime = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(selectedTimeZone);
        return sdf.format(currentTime);
    }
    private Optional<String> getLastTimezoneFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("lastTimezone".equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
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
