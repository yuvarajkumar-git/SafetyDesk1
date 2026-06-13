package com.cts.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

/**
 * Registers concrete enum converters so query/path params accept the story
 * spellings (e.g. "Chemical", "Open", "SafetyOfficer") - matching the JSON body.
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        log.info(">>> WebConfig.addFormatters CALLED - registering enum converters");

        registry.addConverter(new EnumConverters.RoleConverter());
        registry.addConverter(new EnumConverters.UserStatusConverter());
        registry.addConverter(new EnumConverters.IncidentTypeConverter());
        registry.addConverter(new EnumConverters.SeverityConverter());
        registry.addConverter(new EnumConverters.IncidentStatusConverter());
        registry.addConverter(new EnumConverters.InvestigationStatusConverter());
        registry.addConverter(new EnumConverters.CorrectiveActionStatusConverter());
        registry.addConverter(new EnumConverters.HazardTypeConverter());
        registry.addConverter(new EnumConverters.HazardStatusConverter());
        registry.addConverter(new EnumConverters.RiskAssessmentStatusConverter());
        registry.addConverter(new EnumConverters.InspectionTypeConverter());
        registry.addConverter(new EnumConverters.InspectionStatusConverter());
        registry.addConverter(new EnumConverters.FindingTypeConverter());
        registry.addConverter(new EnumConverters.RiskLevelConverter());
        registry.addConverter(new EnumConverters.FindingStatusConverter());
        registry.addConverter(new EnumConverters.PermitTypeConverter());
        registry.addConverter(new EnumConverters.PermitStatusConverter());
        registry.addConverter(new EnumConverters.ExtensionStatusConverter());
        registry.addConverter(new EnumConverters.AssessmentTypeConverter());
        registry.addConverter(new EnumConverters.FitnessDecisionConverter());
        registry.addConverter(new EnumConverters.HealthRecordStatusConverter());
        registry.addConverter(new EnumConverters.ReferralStatusConverter());
        registry.addConverter(new EnumConverters.NotificationCategoryConverter());
        registry.addConverter(new EnumConverters.NotificationStatusConverter());

        log.info(">>> WebConfig registered all enum converters");
    }
}