package com.example.core.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class CoreArchitectureTest {

    @Test
    void coreShouldNotDependOnServletOrJdbc() {

        JavaClasses classes = new ClassFileImporter()
                .importPackages("com.example.core");

        noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "jakarta.servlet..",
                        "javax.servlet..",
                        "java.sql..",
                        "javax.sql.."
                )
                .check(classes);
    }
}
