package ir.store.product;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("ir.store.product");

        noClasses()
            .that()
                .resideInAnyPackage("ir.store.product.service..")
            .or()
                .resideInAnyPackage("ir.store.product.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..ir.store.product.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
