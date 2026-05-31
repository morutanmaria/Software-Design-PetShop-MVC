package main.controller;

import main.dto.LoggedUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public GlobalControllerAdvice(MessageSource messageSource,
                                  LocaleResolver localeResolver) {
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @ModelAttribute("loggedUser")
    public LoggedUser addUserToModel(HttpSession session) {
        return (LoggedUser) session.getAttribute("loggedUser");
    }

    @ModelAttribute("currentLang")
    public String addLangToModel(HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        return locale.getLanguage();
    }
}
