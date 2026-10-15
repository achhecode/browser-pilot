package com.achhecode.browser_pilot.api;

import java.time.Instant;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.achhecode.browser_pilot.entity.ApiAuditLog;
import com.achhecode.browser_pilot.website.instahyre.model.JobApplicationRequest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiAuditAspect {

    private final ApiAuditService apiAuditService;

    @Around("@annotation(auditApi)")
    public Object auditApi(
            ProceedingJoinPoint joinPoint,
            AuditApi auditApi) throws Throwable {

        Instant start = Instant.now();
        long startTime = System.currentTimeMillis();

        UUID requestId = UUID.randomUUID();
        String apiName = auditApi.value();

        String httpMethod = null;
        String requestUri = null;
        String clientIp = null;
        String requestBody = null;

        Integer httpStatus = 200;
        String responseBody = null;
        String status = "SUCCESS";
        String errorMessage = null;

        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            httpMethod = request.getMethod();
            requestUri = request.getRequestURI();
            clientIp = request.getRemoteAddr();
        }

        // Avoid serializing sensitive request objects or credentials.
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof JobApplicationRequest) {
                try {
                    requestBody =
                            new com.fasterxml.jackson.databind.ObjectMapper()
                                    .findAndRegisterModules()
                                    .writeValueAsString(arg);
                } catch (Exception ex) {
                    log.warn("Could not serialize audit request body", ex);
                }
                break;
            }
        }

        try {
            Object result = joinPoint.proceed();

            if (result instanceof ResponseEntity<?> response) {
                httpStatus = response.getStatusCode().value();
                Object body = response.getBody();
                responseBody = body == null ? null : body.toString();
            }

            if (httpStatus >= 400) {
                status = "FAILURE";
            }

            return result;

        } catch (Throwable ex) {
            status = "FAILURE";
            errorMessage = ex.getMessage();
            httpStatus = 500;
            throw ex;

        } finally {
            try {
                ApiAuditLog auditLog = ApiAuditLog.builder()
                        .requestId(requestId)
                        .apiName(apiName)
                        .httpMethod(httpMethod)
                        .requestUri(requestUri)
                        .requestBody(requestBody)
                        .responseBody(responseBody)
                        .httpStatus(httpStatus)
                        .errorMessage(errorMessage)
                        .status(status)
                        .durationMs(
                                System.currentTimeMillis() - startTime)
                        .createdAt(start)
                        .completedAt(Instant.now())
                        .clientIp(clientIp)
                        .build();

                apiAuditService.saveAsync(auditLog);

            } catch (Exception auditException) {
                log.error("Failed to submit API audit log", auditException);
            }
        }
    }
}
