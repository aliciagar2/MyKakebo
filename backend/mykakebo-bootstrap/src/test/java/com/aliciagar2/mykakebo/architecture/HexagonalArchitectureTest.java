package com.aliciagar2.mykakebo.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexagonalArchitectureTest {

    private static final JavaClasses APPLICATION_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.aliciagar2.mykakebo");

    @Test
    void applicationLayerRejectsDependenciesOnAdaptersAndFrameworks() {
        noClasses()
                .that().resideInAPackage("com.aliciagar2.mykakebo.application..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "com.aliciagar2.mykakebo.adapter..",
                        "com.aliciagar2.mykakebo.config..",
                        "com.aliciagar2.mykakebo.dto..",
                        "com.aliciagar2.mykakebo.repository..",
                        "com.aliciagar2.mykakebo.web..",
                        "jakarta..",
                        "org.hibernate..",
                        "org.springframework..")
                .check(APPLICATION_CLASSES);
    }

    @Test
    void webAdapterRejectsDependenciesOnPersistence() {
        noClasses()
                .that().resideInAnyPackage("com.aliciagar2.mykakebo.web..", "com.aliciagar2.mykakebo.dto..")
                .and().haveSimpleNameNotStartingWith("Health")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "com.aliciagar2.mykakebo.adapter.persistence..",
                        "com.aliciagar2.mykakebo.repository..",
                        "jakarta.persistence..")
                .check(APPLICATION_CLASSES);
    }
}
