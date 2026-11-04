
package com.example.persistence.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class PersistenceArchitectureTest {

    @Test
    void repositoriesShouldBeInRepositoryPackage() {

        JavaClasses importedClasses = new ClassFileImporter()
                .importPackages("com.example.persistence");

        classes()
                .that()
                .haveSimpleNameEndingWith("Repository")
                .should()
                .resideInAPackage("com.example.persistence.repository..")
                .check(importedClasses);
    }
}
