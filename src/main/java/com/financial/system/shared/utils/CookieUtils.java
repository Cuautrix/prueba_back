package com.financial.system.shared.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.WebUtils;

public abstract class CookieUtils {
    private static final String ROOT_PATH = "/";
    private static final int MAX_AGE = 0;

    public static void create(HttpServletResponse httpServletResponse, String name, String value, Boolean secure, Integer maxAge, String domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(secure);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setDomain(domain);
        cookie.setPath(ROOT_PATH);
        httpServletResponse.addCookie(cookie);
    }


    public static void createCustomCookie(HttpServletResponse httpServletResponse, String name, String value, Boolean secure, Integer maxAge, String domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(secure);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setPath(ROOT_PATH);

        if (domain != null && !domain.isEmpty()) {
            cookie.setDomain(domain);
        }

        String cookieHeader = String.format(
                "%s=%s; Path=%s; HttpOnly; SameSite=None%s%s",
                name, value,
                ROOT_PATH,
                secure ? "; Secure" : "",
                "Domain=" + domain
        );

        httpServletResponse.addHeader("Set-Cookie", cookieHeader);
    }

    public static void clear(HttpServletResponse httpServletResponse, String name, String domain) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath(ROOT_PATH);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(MAX_AGE);
        cookie.setDomain(domain);
        httpServletResponse.addCookie(cookie);
    }

    public static Cookie checkSession(HttpServletRequest request, String cookieName) {
        return WebUtils.getCookie(request, cookieName);
    }
}
