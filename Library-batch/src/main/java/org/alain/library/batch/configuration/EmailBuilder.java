package org.alain.library.batch.configuration;

import io.swagger.client.model.LoanDto;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailBuilder {

    private TemplateEngine templateEngine;

    public EmailBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(LoanDto loanDto){
        Context context = new Context();
        context.setVariable("loan", loanDto);
        return templateEngine.process("mailTemplate", context);
    }
}
