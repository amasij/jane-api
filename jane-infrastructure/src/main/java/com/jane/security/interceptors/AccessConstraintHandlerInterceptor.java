/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jane.security.interceptors;

import com.jane.CustomerRepository;
import com.jane.UserService;
import com.jane.constants.Constants;
import com.jane.entity.Customer;
import com.jane.security.constraint.Public;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.webmvc.api.OpenApiResource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class AccessConstraintHandlerInterceptor extends HandlerInterceptorAdapter {

    private final ApplicationContext applicationContext;

    @Autowired
    private UserService userService;


    @Autowired
    private CustomerRepository customerRepository;

    public AccessConstraintHandlerInterceptor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        try {
            List<Annotation> accessConstraints = collectAccessConstraints(handlerMethod.getMethod().getDeclaringClass().getAnnotations());
            accessConstraints.addAll(collectAccessConstraints(handlerMethod.getMethod().getDeclaredAnnotations()));
            if (accessConstraints.isEmpty() && (
                    handlerMethod.hasMethodAnnotation(Public.class)
                            || handlerMethod.getMethod().getDeclaringClass().isAnnotationPresent(Public.class)
                            || (BasicErrorController.class.isAssignableFrom(handlerMethod.getBeanType()))
                            || (OpenApiResource.class.isAssignableFrom(handlerMethod.getBeanType())))) {
                return true;
            }
            return hasValidToken(request, response);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<Annotation> collectAccessConstraints(Annotation[] stream) {
        return Arrays.stream(stream)
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(AccessConstraint.class))
                .collect(Collectors.toList());
    }


    private boolean hasValidToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!Objects.isNull(request.getHeader("token"))) {
            String token = request.getHeader("token");
            try {
                Long userId = Long.valueOf(userService.decodeToken(token));
                Optional<Customer> optionalCustomer = customerRepository.findActiveById(userId);
                if (optionalCustomer.isPresent()) {
                    RequestAttributes currentRequestAttributes = RequestContextHolder.currentRequestAttributes();
                    currentRequestAttributes.setAttribute(Constants.LOGGED_IN_CUSTOMER, optionalCustomer.get(), RequestAttributes.SCOPE_REQUEST);
                    return true;
                }
                unAuthorize(response);
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        unAuthorize(response);
        return false;
    }

    private void unAuthorize(HttpServletResponse response) throws IOException {
        response.setStatus(401);
        response.getWriter().append("Unauthorized");
    }

    public static FactoryBean<Customer> loggedInCustomer() {
        return new FactoryBean<Customer>() {

            @Override
            public Customer getObject() {
                return (Customer) RequestContextHolder.currentRequestAttributes().getAttribute(Constants.LOGGED_IN_CUSTOMER,
                        RequestAttributes.SCOPE_REQUEST);
            }

            @Override
            public Class<?> getObjectType() {
                return Customer.class;
            }

            @Override
            public boolean isSingleton() {
                return false;
            }
        };
    }
}
