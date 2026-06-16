package com.cts.entity;

import java.util.ArrayList;
import java.util.List;

import com.cts.enums.Role;
import com.cts.enums.UserStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User (Story 9): system user with role-based and site-scoped access.
 * Referenced (inverse) side of the system's bidirectional relationships.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "site_id", nullable = false)
    private Long siteId;

    @Column(name = "department_id")
    private Long departmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    // Needed for Story 10 (Login). Never returned in responses.
    @Column(name = "password", nullable = false)
    private String password;

    // Story 10: account lockout tracking
    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "account_locked", nullable = false)
    @Builder.Default
    private boolean accountLocked = false;

    // ===== Inverse (mappedBy) sides =====
    // No cascade / no orphanRemoval: deleting a user must never delete
    // incidents, permits, health records, etc. @JsonIgnore prevents
    // serialization recursion (entities are never returned directly anyway).

    @JsonIgnore
    @OneToMany(mappedBy = "reportedBy")
    @Builder.Default
    private List<IncidentReport> reportedIncidents = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "assignedInvestigator")
    @Builder.Default
    private List<IncidentReport> investigatingIncidents = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "investigator")
    @Builder.Default
    private List<IncidentInvestigation> investigations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "assignedTo")
    @Builder.Default
    private List<CorrectiveAction> assignedActions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "verifiedBy")
    @Builder.Default
    private List<CorrectiveAction> verifiedActions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "identifiedBy")
    @Builder.Default
    private List<HazardRecord> identifiedHazards = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "assessedBy")
    @Builder.Default
    private List<RiskAssessment> assessments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "assignedOfficer")
    @Builder.Default
    private List<InspectionSchedule> assignedInspections = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "assignedTo")
    @Builder.Default
    private List<InspectionFinding> assignedFindings = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "issuedTo")
    @Builder.Default
    private List<WorkPermit> issuedPermits = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "approvedBy")
    @Builder.Default
    private List<WorkPermit> approvedPermits = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "requestedBy")
    @Builder.Default
    private List<PermitExtension> requestedExtensions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "approvedBy")
    @Builder.Default
    private List<PermitExtension> approvedExtensions = new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "employee")
    @Builder.Default
    private List<HealthRecord> healthRecords = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "conductedBy")
    @Builder.Default
    private List<HealthRecord> conductedAssessments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "employee")
    @Builder.Default
    private List<MedicalReferral> referrals = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();
}