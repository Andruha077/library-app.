
package com.example.web.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class WebArchitectureTest {

    @Test
    void webShouldNotDependOnPersistence() {
        JavaClasses importedClasses = new ClassFileImporter()
                .importPackages("com.example.web");

        noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAPackage("com.example.persistence..")
                .check(importedClasses);
    }

    @Test
    void controllersShouldBeInControllerPackage() {
        JavaClasses importedClasses = new ClassFileImporter()
                .importPackages("com.example.web");

        classes()
                .that()
                .haveSimpleNameEndingWith("Controller")
                .should()
                .resideInAPackage("com.example.web.controller..")
                .check(importedClasses);
    }
}
