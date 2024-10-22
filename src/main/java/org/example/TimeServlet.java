package org.example;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    private TemplateEngine templateEngine;

    @Override
    public void init() {
        templateEngine = new TemplateEngine();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String timezone = req.getParameter("timezone");
        if (timezone == null || timezone.isEmpty()) {
            timezone = getTimezoneFromCookie(req).orElse("UTC");
        }

        ZonedDateTime currentTime;
        try {
            currentTime = ZonedDateTime.now(ZoneId.of(timezone));
            saveTimezoneToCookie(resp, timezone);
        } catch (Exception e) {
            currentTime = ZonedDateTime.now(ZoneId.of("UTC"));
            timezone = "UTC";
        }


        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        WebContext context = new WebContext(req, resp, getServletContext());
        context.setVariable("time", formattedTime);
        context.setVariable("timezone", timezone);


        templateEngine.process("/WEB-INF/templates/time.html", context, resp.getWriter());
    }

    private Optional<String> getTimezoneFromCookie(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if ("lastTimezone".equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    private void saveTimezoneToCookie(HttpServletResponse resp, String timezone) {
        Cookie cookie = new Cookie("lastTimezone", timezone);
        cookie.setMaxAge(60 * 60 * 24 * 30);
        resp.addCookie(cookie);
    }
}