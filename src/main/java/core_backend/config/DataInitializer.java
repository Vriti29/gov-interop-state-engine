package core_backend.config;

import core_backend.entity.Application;
import core_backend.entity.Consent;
import core_backend.entity.User;
import core_backend.enums.AppEnums.ApplicationStatus;
import core_backend.enums.AppEnums.ConsentStatus;
import core_backend.enums.AppEnums.Role;
import core_backend.enums.AppEnums.StepStatus;
import core_backend.repository.ApplicationRepository;
import core_backend.repository.ConsentRepository;
import core_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            ApplicationRepository applicationRepository,
            ConsentRepository consentRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Seed Citizen User
            if (!userRepository.existsByUsername("citizen_vriti")) {
                userRepository.save(User.builder()
                        .username("citizen_vriti")
                        .password(passwordEncoder.encode("pass123"))
                        .role(Role.CITIZEN)
                        .department("CITIZEN_PORTAL")
                        .jurisdiction("PUNE")
                        .build());
            }

            // 2. Seed Officer User
            if (!userRepository.existsByUsername("officer_pune")) {
                userRepository.save(User.builder()
                        .username("officer_pune")
                        .password(passwordEncoder.encode("officer123"))
                        .role(Role.OFFICER)
                        .department("HIGHER_EDUCATION")
                        .jurisdiction("PUNE")
                        .build());
            }


            if (consentRepository.findByCitizenId("MH123").isEmpty()) {
                Consent demoConsent = new Consent();
                demoConsent.setCitizenId("MH123");
                demoConsent.setConsumerDepartment("HIGHER_EDUCATION");
                demoConsent.setProviderDepartment("REVENUE");
                demoConsent.setPurpose("SCHOLARSHIP_ELIGIBILITY");
                demoConsent.setStatus(ConsentStatus.ACTIVE);
                demoConsent.setAllowedFields(List.of("annualIncome", "fullName", "dateOfBirth"));
                consentRepository.save(demoConsent);
            }

            // 3. Seed Active DPDP Consent
            Consent seedConsent = Consent.builder()
                    .citizenId("citizen_vriti")
                    .consumerDepartment("HIGHER_EDUCATION")
                    .providerDepartment("REVENUE_DEPT")
                    .purpose("SCHOLARSHIP_ELIGIBILITY")
                    .allowedFields(List.of("annualIncome", "incomeCertificateNo", "issueDate"))
                    .status(ConsentStatus.ACTIVE)
                    .grantedAt(LocalDateTime.now().minusDays(1))
                    .build();
            consentRepository.save(seedConsent);

            // 4. Seed In-Progress Application for Member 2 UI testing
            Application seedApp = Application.builder()
                    .citizenId("citizen_vriti")
                    .serviceType("STUDENT_SCHOLARSHIP")
                    .overallStatus(ApplicationStatus.VERIFICATION_IN_PROGRESS)
                    .identityStep(StepStatus.COMPLETED)
                    .revenueStep(StepStatus.PENDING)
                    .educationStep(StepStatus.PENDING)
                    .submittedAt(LocalDateTime.now().minusDays(2))
                    .deadlineAt(LocalDateTime.now().plusDays(5))
                    .build();
            applicationRepository.save(seedApp);

            System.out.println(">>> SEED DATA INITIALIZED: Pre-configured Users, Consents, and Applications Loaded. <<<");
        };
    }
}