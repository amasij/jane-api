package com.jane.configuration;

import ch.qos.logback.core.util.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jane.*;
import com.jane.constants.Constants;
import com.jane.constants.TimeFormatConstant;
import com.jane.converter.LocalDateTimeConverter;
import com.jane.converter.OffsetTimeConverter;
import com.jane.entity.Customer;
import com.jane.mail.MailService;
import com.jane.security.interceptors.AccessConstraintHandlerInterceptor;
import com.jane.service.AppConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.persistence.EntityManagerFactory;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import java.time.LocalDateTime;
import java.time.OffsetTime;

@Configuration
@EnableAsync
@EnableJpaAuditing
@Slf4j
public class CustomConfiguration {
    @Autowired
    private AppConfigurationProperties appConfigurationProperties;

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setDateFormat(TimeFormatConstant.DEFAULT_DATE_TIME_FORMAT)
                .registerTypeAdapter(OffsetTime.class, new OffsetTimeConverter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
                .create();
    }

    @Bean
    public PhoneNumberService phoneNumberService() {
        return new PhoneNumberServiceImpl();
    }

    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient();
    }

    @Bean
    public TimeUtil timeUtil() {
        return new TimeUtil();
    }

    @Bean
    @Profile("!test")
    public MailService mailService(ApplicationContext applicationContext) {
        return applicationContext.getAutowireCapableBeanFactory().createBean(MailServiceImpl.class);
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public FactoryBean<Customer> loggedInCustomer(){
        return AccessConstraintHandlerInterceptor.loggedInCustomer();
    }


    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:/templates"); //defines the classpath location of the freemarker templates freeMarkerConfigurer.setDefaultEncoding("UTF-8"); // Default encoding of the template files
        return freeMarkerConfigurer;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Logger getLogger(InjectionPoint injectionPoint) {
        Class<?> classOnWired = injectionPoint.getMember().getDeclaringClass();
        return LoggerFactory.getLogger(classOnWired);
    }




    @Bean
    @Profile("!test")
    public TemplateEngine templateEngine(ApplicationContext applicationContext) {
        return applicationContext.getAutowireCapableBeanFactory().createBean(TemplateEngineImpl.class);
    }



    @Bean
    public ConstraintValidatorFactory constraintValidatorFactory(final AutowireCapableBeanFactory beanFactory) {
        return new ConstraintValidatorFactory() {

            @Override
            public void releaseInstance(
                    ConstraintValidator<?, ?> arg0) {
                beanFactory.destroyBean(arg0);
            }

            @Override
            public <T extends ConstraintValidator<?, ?>> T getInstance(
                    Class<T> arg0) {
                try {
                    return beanFactory.getBean(arg0);
                } catch (NoSuchBeanDefinitionException e) {
                    if (arg0.isInterface()) {
                        throw e;
                    }
                    return beanFactory.createBean(arg0);
                }
            }
        };
    }

}
