package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import static org.mockito.Mockito.*;

public class TimeServletTest {

//    @Mock private ServletConfig servletConfig;
//    @Mock private ServletContext servletContext;
//    @Mock private HttpServletRequest request;
//    @Mock private HttpServletResponse response;
//
//    private TimeServlet timeServlet;
//
//    @BeforeEach
//    public void setUp() throws ServletException {
//        MockitoAnnotations.openMocks(this);
//
//        when(servletConfig.getServletContext()).thenReturn(servletContext);
//
//        timeServlet = new TimeServlet();
//        timeServlet.init(servletConfig);
//    }
//
//    @Test
//    public void testProcessRequestWithSavedTimezone() throws ServletException, IOException {
//        StringWriter stringWriter = new StringWriter();
//        PrintWriter writer = new PrintWriter(stringWriter);
//        when(response.getWriter()).thenReturn(writer);
//        when(request.getParameter("timezone")).thenReturn("UTC+2");
//
//        timeServlet.processRequest(request, response);
//        writer.flush();
//    }
//    @Test
//    public void testProcessRequestWithCookieTimezone() throws ServletException, IOException {
//        StringWriter stringWriter = new StringWriter();
//        PrintWriter writer = new PrintWriter(stringWriter);
//        when(response.getWriter()).thenReturn(writer);
//        Cookie timezoneCookie = new Cookie("timezone", "UTC+5");
//        when(request.getCookies()).thenReturn(new Cookie[] {timezoneCookie});
//
//        timeServlet.processRequest(request, response);
//        writer.flush();
//    }
//
//    @Test
//    public void testProcessRequestWithNoTimezone() throws ServletException, IOException {
//        StringWriter stringWriter = new StringWriter();
//        PrintWriter writer = new PrintWriter(stringWriter);
//        when(response.getWriter()).thenReturn(writer);
//        Cookie timezoneCookie = new Cookie("timezone", "UTC-5");
//        when(request.getCookies()).thenReturn(new Cookie[] {timezoneCookie});
//
//        timeServlet.processRequest(request, response);
//        writer.flush();
//    }
}



