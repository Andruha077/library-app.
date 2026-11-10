
package com.example.web.controller;

import com.example.web.service.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MailPreviewController {

    private final MailService mailService;

    public MailPreviewController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping(value = "/mail-preview", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String previewEmail() {

        String email = mailService.getLastGeneratedEmail();

        if (email == null) {
            return """
                    <!DOCTYPE html>
                    <html lang="uk">
                    <head>
                        <meta charset="UTF-8">
                        <title>Перегляд листа</title>
                    </head>
                    <body>
                        <h2>Лист ще не згенеровано</h2>
                        <p>Спочатку додайте нову книгу до каталогу.</p>
                    </body>
                    </html>
                    """;
        }

        return email;
    }
}
