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

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine templateEngine;
    private static final String TIMEZONE_COOKIE_NAME = "lastTimezone";
    @Override
    public void init() throws ServletException {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String timezoneParam = request.getParameter("timezone");
        TimeZone timezone = parseTimeZone(timezoneParam);

        response.addCookie(createTimezoneCookie(timezone.getID()));

        Date currentDate = new Date();
        String currentTime = convertToTimeZoneFormat(currentDate, timezone);

        WebContext webContext = new WebContext(request, response, getServletContext());
        webContext.setVariable("timezone", timezone.getID());
        webContext.setVariable("currentTime", currentTime);


        templateEngine.process("time", webContext, response.getWriter());
    }

    public Cookie createTimezoneCookie(String timezone) {
        Cookie cookie = new Cookie(TIMEZONE_COOKIE_NAME, timezone);
        cookie.setMaxAge(365 * 24 * 60 * 60); // 1 year
        cookie.setPath("/");
        return cookie;
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
