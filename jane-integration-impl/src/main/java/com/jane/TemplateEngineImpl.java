package com.jane;

import com.jane.service.AppConfigurationProperties;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Optional;

public class TemplateEngineImpl implements TemplateEngine {

    final Logger logger = LoggerFactory.getLogger(TemplateEngineImpl.class);

    private final Configuration configuration;

    @Inject
    public TemplateEngineImpl(
            AppConfigurationProperties appConfigurationProperties,
            Environment ninjaProperties,
            Optional<ServletContext> context) throws IOException {
        TemplateLoader templateLoader;
        logger.info("EMAIL_TEMPLATES_HOME: " + appConfigurationProperties.getEmailTemplatesDirectory());
//        if (StringUtils.isBlank(reportUrl)) {
//            reportUrl = "/WEB-INF/email-templates";
//        }
        if (ninjaProperties.acceptsProfiles("dev")) {
            templateLoader = new FileTemplateLoader(new File(appConfigurationProperties.getEmailTemplatesDirectory()));
        } else {
            templateLoader = new WebappTemplateLoader(context.get(), appConfigurationProperties.getEmailTemplatesDirectory());
        }
        configuration = new Configuration(Configuration.VERSION_2_3_22);
//        configuration.setClassLoaderForTemplateLoading(TemplateEngine.class.getClassLoader(), "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//        configuration.setDirectoryForTemplateLoading(directory.toFile());
        configuration.setTemplateLoader(templateLoader);
    }

    @Override
    public String getAsString(String templatePath, Map<String, Object> bindings) {
        return new String(getBytes(templatePath, bindings));
    }

    @Override
    public byte[] getBytes(String templatePath, Map<String, Object> bindings) {
        try {
            Template tpl = configuration.getTemplate(templatePath);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(bout);
            tpl.process(bindings, writer);
            return bout.toByteArray();
        } catch (TemplateException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
