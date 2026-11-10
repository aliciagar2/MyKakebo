package com.aliciagar2.mykakebo.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class DomainPurityArchTest {

    private static final JavaClasses DOMAIN_MODULE_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.aliciagar2.mykakebo");

    @Test
    void domainModuleRejectsFrameworkDependencies() {
        noClasses()
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta..", "org.hibernate..")
                .check(DOMAIN_MODULE_CLASSES);
    }
}
