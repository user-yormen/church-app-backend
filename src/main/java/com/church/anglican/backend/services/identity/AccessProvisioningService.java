package com.church.anglican.backend.services.identity;

import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.entities.identity.Permission;
import com.church.anglican.backend.entities.identity.AppUser;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.repositories.identity.AppRoleRepository;
import com.church.anglican.backend.repositories.identity.AppUserRepository;
import com.church.anglican.backend.repositories.identity.ChurchRepository;
import com.church.anglican.backend.repositories.identity.PermissionRepository;
import com.church.anglican.backend.repositories.identity.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccessProvisioningService {

    private static final String BACKOFFICE = "BACKOFFICE";
    private static final String CHURCH = "CHURCH";
    private static final String DEFAULT_BACKOFFICE_CHURCH_NAME = "Backoffice Administration";
    private static final String DEFAULT_BACKOFFICE_CHURCH_ADDRESS = "Diocesan Secretariat";
    private static final String DEFAULT_ADMIN_USERNAME = "yormen1@gmail.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "password";

    private final PermissionRepository permissionRepository;
    private final AppRoleRepository appRoleRepository;
    private final AppUserRepository appUserRepository;
    private final ChurchRepository churchRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public AccessProvisioningService(
            PermissionRepository permissionRepository,
            AppRoleRepository appRoleRepository,
            AppUserRepository appUserRepository,
            ChurchRepository churchRepository,
            PersonRepository personRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.permissionRepository = permissionRepository;
        this.appRoleRepository = appRoleRepository;
        this.appUserRepository = appUserRepository;
        this.churchRepository = churchRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public ApplicationRunner accessProvisioningRunner() {
        return args -> provisionAllChurches();
    }

    @Transactional
    public void provisionAllChurches() {
        List<Permission> permissions = ensurePermissions();
        Church backofficeChurch = ensureBackofficeChurch();
        churchRepository.findAll().forEach(church -> ensureRoles(church, permissions));
        ensureDefaultBackofficeAdmin(backofficeChurch, permissions);
    }

    @Transactional
    public void provisionForChurch(Church church) {
        List<Permission> permissions = ensurePermissions();
        ensureRoles(church, permissions);
    }

    private List<Permission> ensurePermissions() {
        return permissionTemplates().stream()
                .map(template -> permissionRepository.findByNameIgnoreCase(template.name())
                        .map(existing -> updatePermission(existing, template))
                        .orElseGet(() -> createPermission(template)))
                .collect(Collectors.toList());
    }

    private Church ensureBackofficeChurch() {
        return churchRepository.findByNameIgnoreCase(DEFAULT_BACKOFFICE_CHURCH_NAME)
                .orElseGet(() -> {
                    Church church = new Church();
                    church.setName(DEFAULT_BACKOFFICE_CHURCH_NAME);
                    church.setAddress(DEFAULT_BACKOFFICE_CHURCH_ADDRESS);
                    return churchRepository.save(church);
                });
    }

    private Permission updatePermission(Permission permission, PermissionTemplate template) {
        permission.setDescription(template.description());
        permission.setIdentifier(template.identifier());
        return permissionRepository.save(permission);
    }

    private Permission createPermission(PermissionTemplate template) {
        Permission permission = new Permission();
        permission.setName(template.name());
        permission.setDescription(template.description());
        permission.setIdentifier(template.identifier());
        return permissionRepository.save(permission);
    }

    private void ensureRoles(Church church, List<Permission> permissions) {
        roleTemplates().forEach(template -> appRoleRepository.findByChurchIdAndNameIgnoreCase(church.getId(), template.name())
                .map(existing -> updateRole(existing, church, template, permissions))
                .orElseGet(() -> createRole(church, template, permissions)));
    }

    private AppRole updateRole(AppRole role, Church church, RoleTemplate template, List<Permission> permissions) {
        role.setChurch(church);
        role.setDescription(template.description());
        role.setIdentifier(template.identifier());
        role.setPermissions(resolvePermissions(template.permissionNames(), permissions));
        return appRoleRepository.save(role);
    }

    private AppRole createRole(Church church, RoleTemplate template, List<Permission> permissions) {
        AppRole role = new AppRole();
        role.setChurch(church);
        role.setName(template.name());
        role.setDescription(template.description());
        role.setIdentifier(template.identifier());
        role.setPermissions(resolvePermissions(template.permissionNames(), permissions));
        return appRoleRepository.save(role);
    }

    private Set<Permission> resolvePermissions(Set<String> permissionNames, List<Permission> permissions) {
        return permissions.stream()
                .filter(permission -> permissionNames.contains(permission.getName()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void ensureDefaultBackofficeAdmin(Church church, List<Permission> permissions) {
        ensureRoles(church, permissions);
        AppRole adminRole = appRoleRepository.findByChurchIdAndNameIgnoreCase(church.getId(), "ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role missing for backoffice church"));

        AppUser existingUser = appUserRepository.findByUsername(DEFAULT_ADMIN_USERNAME).orElse(null);
        Person person = existingUser != null
                ? existingUser.getPerson()
                : createDefaultAdminPerson();

        if (existingUser == null) {
            AppUser user = new AppUser();
            user.setUsername(DEFAULT_ADMIN_USERNAME);
            user.setPasswordHash(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
            user.setEnabled(true);
            user.setPerson(person);
            user.setRoles(Collections.singleton(adminRole));
            appUserRepository.save(user);
            return;
        }

        existingUser.setEnabled(true);
        existingUser.setPasswordHash(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
        existingUser.setRoles(Collections.singleton(adminRole));
        appUserRepository.save(existingUser);
    }

    private Person createDefaultAdminPerson() {
        Person person = new Person();
        person.setFullName("Yormen Administrator");
        person.setFirstName("Yormen");
        person.setLastName("Administrator");
        person.setPreferredName("Yormen");
        person.setEmailAddress(DEFAULT_ADMIN_USERNAME);
        person.setPhoneNumber("+233200000001");
        person.setAddress(DEFAULT_BACKOFFICE_CHURCH_ADDRESS);
        person.setEmergencyContact("Church Office");
        person.setStatus(Person.PersonStatus.ACTIVE);
        return personRepository.save(person);
    }

    private List<PermissionTemplate> permissionTemplates() {
        return List.of(
                new PermissionTemplate("BACKOFFICE_CHURCH_MANAGE", "Create and manage churches from the backoffice.", BACKOFFICE),
                new PermissionTemplate("BACKOFFICE_ROLE_MANAGE", "Manage role templates and access assignments from the backoffice.", BACKOFFICE),
                new PermissionTemplate("BACKOFFICE_USER_MANAGE", "Create and manage application users from the backoffice.", BACKOFFICE),
                new PermissionTemplate("BACKOFFICE_SHARE_MANAGE", "Manage cross-church sharing from the backoffice.", BACKOFFICE),
                new PermissionTemplate("PEOPLE_MANAGE", "Create and maintain person records.", CHURCH),
                new PermissionTemplate("MEMBERSHIP_MANAGE", "Create memberships and maintain membership status history.", CHURCH),
                new PermissionTemplate("GROUP_MANAGE", "Manage church groups and their memberships.", CHURCH),
                new PermissionTemplate("ROLE_ASSIGN", "Assign executive and office-bearing roles within the church.", CHURCH),
                new PermissionTemplate("ELECTION_MANAGE", "Create elections, candidates, and finalize results.", CHURCH),
                new PermissionTemplate("ELECTION_VOTE", "Cast votes in church elections.", CHURCH),
                new PermissionTemplate("FINANCE_MANAGE", "Manage collection events, counting sessions, and amount breakdowns.", CHURCH),
                new PermissionTemplate("ACHIEVEMENT_MANAGE", "Record and review church achievements.", CHURCH),
                new PermissionTemplate("REPORT_VIEW", "Review church-facing summaries and reports.", CHURCH)
        );
    }

    private List<RoleTemplate> roleTemplates() {
        return List.of(
                new RoleTemplate(
                        "ADMIN",
                        "Backoffice super administrator for central operations.",
                        BACKOFFICE,
                        Set.of(
                                "BACKOFFICE_CHURCH_MANAGE",
                                "BACKOFFICE_ROLE_MANAGE",
                                "BACKOFFICE_USER_MANAGE",
                                "BACKOFFICE_SHARE_MANAGE",
                                "PEOPLE_MANAGE",
                                "MEMBERSHIP_MANAGE",
                                "GROUP_MANAGE",
                                "ROLE_ASSIGN",
                                "ELECTION_MANAGE",
                                "ELECTION_VOTE",
                                "FINANCE_MANAGE",
                                "ACHIEVEMENT_MANAGE",
                                "REPORT_VIEW"
                        )
                ),
                new RoleTemplate(
                        "BACKOFFICE_SUPPORT",
                        "Backoffice support operator for records and operational assistance.",
                        BACKOFFICE,
                        Set.of("BACKOFFICE_USER_MANAGE", "PEOPLE_MANAGE", "MEMBERSHIP_MANAGE", "GROUP_MANAGE", "REPORT_VIEW")
                ),
                new RoleTemplate(
                        "BACKOFFICE_AUDITOR",
                        "Backoffice auditor for read-oriented supervision and reporting.",
                        BACKOFFICE,
                        Set.of("REPORT_VIEW")
                ),
                new RoleTemplate(
                        "CHURCH_ADMIN",
                        "Church administrator with full parish workflow access.",
                        CHURCH,
                        Set.of("PEOPLE_MANAGE", "MEMBERSHIP_MANAGE", "GROUP_MANAGE", "ROLE_ASSIGN", "ELECTION_MANAGE", "ELECTION_VOTE", "FINANCE_MANAGE", "ACHIEVEMENT_MANAGE", "REPORT_VIEW")
                ),
                new RoleTemplate(
                        "PRIEST",
                        "Priest with pastoral and church-wide oversight workflows.",
                        CHURCH,
                        Set.of("PEOPLE_MANAGE", "MEMBERSHIP_MANAGE", "GROUP_MANAGE", "ROLE_ASSIGN", "ELECTION_MANAGE", "ACHIEVEMENT_MANAGE", "REPORT_VIEW")
                ),
                new RoleTemplate(
                        "SECRETARY",
                        "Secretary responsible for records, memberships, and church groups.",
                        CHURCH,
                        Set.of("PEOPLE_MANAGE", "MEMBERSHIP_MANAGE", "GROUP_MANAGE", "ROLE_ASSIGN", "ACHIEVEMENT_MANAGE", "REPORT_VIEW")
                ),
                new RoleTemplate(
                        "TREASURER",
                        "Treasurer responsible for collection and counting workflows.",
                        CHURCH,
                        Set.of("FINANCE_MANAGE", "REPORT_VIEW")
                ),
                new RoleTemplate(
                        "ELECTION_OFFICER",
                        "Election officer responsible for church election workflows.",
                        CHURCH,
                        Set.of("ELECTION_MANAGE", "ELECTION_VOTE", "REPORT_VIEW")
                ),
                new RoleTemplate(
                        "GROUP_COORDINATOR",
                        "Group coordinator responsible for group-level administration and recognition.",
                        CHURCH,
                        Set.of("GROUP_MANAGE", "ACHIEVEMENT_MANAGE", "REPORT_VIEW")
                )
        );
    }

    private record PermissionTemplate(String name, String description, String identifier) {
    }

    private record RoleTemplate(String name, String description, String identifier, Set<String> permissionNames) {
    }
}
