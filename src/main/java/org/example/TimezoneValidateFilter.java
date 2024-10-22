package org.example;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.zone.ZoneRulesException;
import java.time.ZoneId;

@WebFilter("/time")
public class TimezoneValidateFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String timezone = req.getParameter("timezone");
        if (timezone != null && !timezone.isEmpty()) {
            try {
                ZoneId.of(timezone);
                chain.doFilter(request, response);
            } catch (ZoneRulesException e) {

                resp.setContentType("text/html");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("<html><body><h1>Invalid timezone</h1></body></html>");
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}