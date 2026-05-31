package main.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int statusCode = status != null ? Integer.parseInt(status.toString()) : 500;

        String title;
        String description;
        String errorCode;

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            title = "Page Not Found";
            description = "The page you are looking for does not exist.";
            errorCode = "NOT_FOUND";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            title = "Access Denied";
            description = "You do not have permission to access this resource.";
            errorCode = "PERMISSION_DENIED";
        } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
            title = "Unauthorized";
            description = "You must be logged in to access this page.";
            errorCode = "UNAUTHORIZED";
        } else {
            title = "Something Went Wrong";
            description = "An unexpected error occurred. Please try again later.";
            errorCode = "INTERNAL_ERROR";
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorDescription", description);
        model.addAttribute("errorCode", errorCode);

        return "error";
    }
}
