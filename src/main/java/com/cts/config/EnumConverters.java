package com.cts.config;

import org.springframework.core.convert.converter.Converter;

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
 * One concrete converter class per enum. Each explicitly implements
 * Converter<String, ThatEnum>, so Spring can read the target type and
 * register it correctly (a generic helper would erase the type to Enum).
 *
 * Each delegates to the enum's fromValue(...) so query/path params accept
 * the same story spellings the JSON body accepts.
 */
public final class EnumConverters {

    private EnumConverters() { }

    public static class RoleConverter implements Converter<String, Role> {
        public Role convert(String s) { return s == null || s.isBlank() ? null : Role.fromValue(s.trim()); }
    }
    public static class UserStatusConverter implements Converter<String, UserStatus> {
        public UserStatus convert(String s) { return s == null || s.isBlank() ? null : UserStatus.fromValue(s.trim()); }
    }
    public static class IncidentTypeConverter implements Converter<String, IncidentType> {
        public IncidentType convert(String s) { return s == null || s.isBlank() ? null : IncidentType.fromValue(s.trim()); }
    }
    public static class SeverityConverter implements Converter<String, Severity> {
        public Severity convert(String s) { return s == null || s.isBlank() ? null : Severity.fromValue(s.trim()); }
    }
    public static class IncidentStatusConverter implements Converter<String, IncidentStatus> {
        public IncidentStatus convert(String s) { return s == null || s.isBlank() ? null : IncidentStatus.fromValue(s.trim()); }
    }
    public static class InvestigationStatusConverter implements Converter<String, InvestigationStatus> {
        public InvestigationStatus convert(String s) { return s == null || s.isBlank() ? null : InvestigationStatus.fromValue(s.trim()); }
    }
    public static class CorrectiveActionStatusConverter implements Converter<String, CorrectiveActionStatus> {
        public CorrectiveActionStatus convert(String s) { return s == null || s.isBlank() ? null : CorrectiveActionStatus.fromValue(s.trim()); }
    }
    public static class HazardTypeConverter implements Converter<String, HazardType> {
        public HazardType convert(String s) { return s == null || s.isBlank() ? null : HazardType.fromValue(s.trim()); }
    }
    public static class HazardStatusConverter implements Converter<String, HazardStatus> {
        public HazardStatus convert(String s) { return s == null || s.isBlank() ? null : HazardStatus.fromValue(s.trim()); }
    }
    public static class RiskAssessmentStatusConverter implements Converter<String, RiskAssessmentStatus> {
        public RiskAssessmentStatus convert(String s) { return s == null || s.isBlank() ? null : RiskAssessmentStatus.fromValue(s.trim()); }
    }
    public static class InspectionTypeConverter implements Converter<String, InspectionType> {
        public InspectionType convert(String s) { return s == null || s.isBlank() ? null : InspectionType.fromValue(s.trim()); }
    }
    public static class InspectionStatusConverter implements Converter<String, InspectionStatus> {
        public InspectionStatus convert(String s) { return s == null || s.isBlank() ? null : InspectionStatus.fromValue(s.trim()); }
    }
    public static class FindingTypeConverter implements Converter<String, FindingType> {
        public FindingType convert(String s) { return s == null || s.isBlank() ? null : FindingType.fromValue(s.trim()); }
    }
    public static class RiskLevelConverter implements Converter<String, RiskLevel> {
        public RiskLevel convert(String s) { return s == null || s.isBlank() ? null : RiskLevel.fromValue(s.trim()); }
    }
    public static class FindingStatusConverter implements Converter<String, FindingStatus> {
        public FindingStatus convert(String s) { return s == null || s.isBlank() ? null : FindingStatus.fromValue(s.trim()); }
    }
    public static class PermitTypeConverter implements Converter<String, PermitType> {
        public PermitType convert(String s) { return s == null || s.isBlank() ? null : PermitType.fromValue(s.trim()); }
    }
    public static class PermitStatusConverter implements Converter<String, PermitStatus> {
        public PermitStatus convert(String s) { return s == null || s.isBlank() ? null : PermitStatus.fromValue(s.trim()); }
    }
    public static class ExtensionStatusConverter implements Converter<String, ExtensionStatus> {
        public ExtensionStatus convert(String s) { return s == null || s.isBlank() ? null : ExtensionStatus.fromValue(s.trim()); }
    }
    public static class AssessmentTypeConverter implements Converter<String, AssessmentType> {
        public AssessmentType convert(String s) { return s == null || s.isBlank() ? null : AssessmentType.fromValue(s.trim()); }
    }
    public static class FitnessDecisionConverter implements Converter<String, FitnessDecision> {
        public FitnessDecision convert(String s) { return s == null || s.isBlank() ? null : FitnessDecision.fromValue(s.trim()); }
    }
    public static class HealthRecordStatusConverter implements Converter<String, HealthRecordStatus> {
        public HealthRecordStatus convert(String s) { return s == null || s.isBlank() ? null : HealthRecordStatus.fromValue(s.trim()); }
    }
    public static class ReferralStatusConverter implements Converter<String, ReferralStatus> {
        public ReferralStatus convert(String s) { return s == null || s.isBlank() ? null : ReferralStatus.fromValue(s.trim()); }
    }
    public static class NotificationCategoryConverter implements Converter<String, NotificationCategory> {
        public NotificationCategory convert(String s) { return s == null || s.isBlank() ? null : NotificationCategory.fromValue(s.trim()); }
    }
    public static class NotificationStatusConverter implements Converter<String, NotificationStatus> {
        public NotificationStatus convert(String s) { return s == null || s.isBlank() ? null : NotificationStatus.fromValue(s.trim()); }
    }
}