package com.cts.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.cts.enums.AssessmentType;
import com.cts.enums.CorrectiveActionStatus;
import com.cts.enums.ExtensionStatus;
import com.cts.enums.FindingStatus;
import com.cts.enums.FindingType;
import com.cts.enums.FitnessDecision;
import com.cts.enums.HazardStatus;
import com.cts.enums.HazardType;
import com.cts.enums.HealthRecordStatus;
import com.cts.enums.IncidentStatus;
import com.cts.enums.IncidentType;
import com.cts.enums.InspectionStatus;
import com.cts.enums.InspectionType;
import com.cts.enums.InvestigationStatus;
import com.cts.enums.NotificationCategory;
import com.cts.enums.NotificationStatus;
import com.cts.enums.PermitStatus;
import com.cts.enums.PermitType;
import com.cts.enums.ReferralStatus;
import com.cts.enums.RiskAssessmentStatus;
import com.cts.enums.RiskLevel;
import com.cts.enums.Role;
import com.cts.enums.Severity;
import com.cts.enums.UserStatus;

/**
 * Lets query/path parameters accept the SAME story spellings (e.g. "Completed",
 * "SafetyOfficer", "NearMiss") that the JSON body accepts.
 *
 * Each enum has a static fromValue(String) that handles label OR Java-name.
 * Spring's default StringToEnum converter only matches the exact Java name,
 * so we override it for our enums here.
 */
public class StringToEnumConverterFactory {

    // A reusable converter that calls a given enum's fromValue(...) method.
    private static <T extends Enum<T>> Converter<String, T> forEnum(java.util.function.Function<String, T> fromValue) {
        return new Converter<String, T>() {
            @Override
            public T convert(String source) {
                if (source == null || source.isBlank()) {
                    return null;
                }
                return fromValue.apply(source.trim());
            }
        };
    }

    public static Converter<String, Role> role() { return forEnum(Role::fromValue); }
    public static Converter<String, UserStatus> userStatus() { return forEnum(UserStatus::fromValue); }
    public static Converter<String, IncidentType> incidentType() { return forEnum(IncidentType::fromValue); }
    public static Converter<String, Severity> severity() { return forEnum(Severity::fromValue); }
    public static Converter<String, IncidentStatus> incidentStatus() { return forEnum(IncidentStatus::fromValue); }
    public static Converter<String, InvestigationStatus> investigationStatus() { return forEnum(InvestigationStatus::fromValue); }
    public static Converter<String, CorrectiveActionStatus> correctiveActionStatus() { return forEnum(CorrectiveActionStatus::fromValue); }
    public static Converter<String, HazardType> hazardType() { return forEnum(HazardType::fromValue); }
    public static Converter<String, HazardStatus> hazardStatus() { return forEnum(HazardStatus::fromValue); }
    public static Converter<String, RiskAssessmentStatus> riskAssessmentStatus() { return forEnum(RiskAssessmentStatus::fromValue); }
    public static Converter<String, InspectionType> inspectionType() { return forEnum(InspectionType::fromValue); }
    public static Converter<String, InspectionStatus> inspectionStatus() { return forEnum(InspectionStatus::fromValue); }
    public static Converter<String, FindingType> findingType() { return forEnum(FindingType::fromValue); }
    public static Converter<String, RiskLevel> riskLevel() { return forEnum(RiskLevel::fromValue); }
    public static Converter<String, FindingStatus> findingStatus() { return forEnum(FindingStatus::fromValue); }
    public static Converter<String, PermitType> permitType() { return forEnum(PermitType::fromValue); }
    public static Converter<String, PermitStatus> permitStatus() { return forEnum(PermitStatus::fromValue); }
    public static Converter<String, ExtensionStatus> extensionStatus() { return forEnum(ExtensionStatus::fromValue); }
    public static Converter<String, AssessmentType> assessmentType() { return forEnum(AssessmentType::fromValue); }
    public static Converter<String, FitnessDecision> fitnessDecision() { return forEnum(FitnessDecision::fromValue); }
    public static Converter<String, HealthRecordStatus> healthRecordStatus() { return forEnum(HealthRecordStatus::fromValue); }
    public static Converter<String, ReferralStatus> referralStatus() { return forEnum(ReferralStatus::fromValue); }
    public static Converter<String, NotificationCategory> notificationCategory() { return forEnum(NotificationCategory::fromValue); }
    public static Converter<String, NotificationStatus> notificationStatus() { return forEnum(NotificationStatus::fromValue); }
}