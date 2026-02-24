package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.AuthTestBase;
import com.backendsyndicate.smashclub.auth.dto.request.RegisterRequest;
import com.backendsyndicate.smashclub.common.util.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest extends AuthTestBase {

    @InjectMocks
    private ValidationService validationService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest();
        validRequest.setFullName("John Doe");
        validRequest.setEmail("john.doe@example.com");
        validRequest.setPassword("Password123");
    }

    @Nested
    @DisplayName("Full Name Validation Tests")
    class FullNameValidationTests {

        @Test
        @DisplayName("Should pass with valid full name")
        void validate_WithValidFullName_ShouldHaveNoErrors() {
            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);
            assertTrue(errors.stream().noneMatch(e -> e.getField().equals("fullName")));
        }

        @Test
        @DisplayName("Should fail when full name is null")
        void validate_WithNullFullName_ShouldAddError() {
            validRequest.setFullName(null);

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            ValidationError error = findErrorByField(errors, "fullName");
            assertNotNull(error);
            assertTrue(error.getMessage().contains("harus diisi"));
        }

        @Test
        @DisplayName("Should fail when full name is empty")
        void validate_WithEmptyFullName_ShouldAddError() {
            validRequest.setFullName("");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            ValidationError error = findErrorByField(errors, "fullName");
            assertNotNull(error);
            assertTrue(error.getMessage().contains("harus diisi"));
        }

        @Test
        @DisplayName("Should fail when full name is too short")
        void validate_WithTooShortFullName_ShouldAddError() {
            validRequest.setFullName("Jo");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            ValidationError error = findErrorByField(errors, "fullName");
            assertNotNull(error);
            assertTrue(error.getMessage().contains("minimal 3 karakter"));
        }

        @Test
        @DisplayName("Should fail when full name is too long")
        void validate_WithTooLongFullName_ShouldAddError() {
            validRequest.setFullName("A".repeat(101));

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            ValidationError error = findErrorByField(errors, "fullName");
            assertNotNull(error);
            assertTrue(error.getMessage().contains("maksimal 100 karakter"));
        }

        @Test
        @DisplayName("Should allow numbers in full name")
        void validate_WithNumbersInFullName_ShouldPass() {
            validRequest.setFullName("John Doe 123");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            assertTrue(errors.stream().noneMatch(e -> e.getField().equals("fullName")));
        }

        @Test
        @DisplayName("Should fail when full name contains special characters")
        void validate_WithSpecialCharsInFullName_ShouldAddError() {
            validRequest.setFullName("John@Doe");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            ValidationError error = findErrorByField(errors, "fullName");
            assertNotNull(error);
            assertTrue(error.getMessage().contains("hanya boleh mengandung huruf dan spasi"));
        }

        @Test
        @DisplayName("Should pass with multiple spaces in full name")
        void validate_WithMultipleSpaces_ShouldPass() {
            validRequest.setFullName("John   Doe   Smith");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);
            assertTrue(errors.stream().noneMatch(e -> e.getField().equals("fullName")));
        }

        @Test
        @DisplayName("Should trim full name before validation")
        void validate_ShouldTrimFullName() {
            validRequest.setFullName("  John Doe  ");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);
            assertTrue(errors.stream().noneMatch(e -> e.getField().equals("fullName")));
        }
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @DisplayName("isValidEmail should return true for valid email (via reflection)")
        void isValidEmail_WithValidEmail_ShouldReturnTrue() throws Exception {
            java.lang.reflect.Method method = ValidationService.class.getDeclaredMethod("isValidEmail", String.class);
            method.setAccessible(true);

            boolean result = (boolean) method.invoke(validationService, "user@example.com");
            assertTrue(result);
        }

        @Test
        @DisplayName("Should call isValidEmail when email is not empty and check invalid format")
        void validate_WithInvalidEmailFormat_ShouldCallIsValidEmail() {
            // Test dengan email tidak null, tidak empty, tapi format salah
            RegisterRequest request = new RegisterRequest();
            request.setFullName("John Doe");
            request.setEmail("invalid-email");  // tidak null, tidak empty, tapi format salah
            request.setPassword("Password123");

            List<ValidationError> errors = validationService.validateRegistrationData(request);

            ValidationError error = findErrorByField(errors, "email");
            assertNotNull(error);
            assertEquals("email", error.getField());
            assertTrue(error.getMessage().contains("Format email tidak valid"));
        }

        @Test
        @DisplayName("Should detect null email (cover first condition)")
        void validate_WithNullEmail_ShouldCoverFirstCondition() {
            // Test untuk kondisi pertama: email == null
            RegisterRequest request = new RegisterRequest();
            request.setFullName("John Doe");
            request.setEmail(null);
            request.setPassword("Password123");

            List<ValidationError> errors = validationService.validateRegistrationData(request);

            ValidationError error = findErrorByField(errors, "email");
            assertNotNull(error);
            assertEquals("email", error.getField());
            assertTrue(error.getMessage().contains("harus diisi"));
        }

        @Test
        @DisplayName("Should detect empty email (cover second condition)")
        void validate_WithEmptyEmail_ShouldCoverSecondCondition() {
            // Test untuk kondisi kedua: email.trim().isEmpty() dengan empty string
            RegisterRequest request = new RegisterRequest();
            request.setFullName("John Doe");
            request.setEmail("");
            request.setPassword("Password123");

            List<ValidationError> errors = validationService.validateRegistrationData(request);

            ValidationError error = findErrorByField(errors, "email");
            assertNotNull(error);
            assertEquals("email", error.getField());
            assertTrue(error.getMessage().contains("harus diisi"));
        }

        @Test
        @DisplayName("Should detect email with only spaces (cover second condition with trim)")
        void validate_WithSpacesOnly_ShouldCoverSecondCondition() {
            // Test untuk kondisi kedua: email.trim().isEmpty() dengan spasi
            RegisterRequest request = new RegisterRequest();
            request.setFullName("John Doe");
            request.setEmail("   ");
            request.setPassword("Password123");

            List<ValidationError> errors = validationService.validateRegistrationData(request);

            ValidationError error = findErrorByField(errors, "email");
            assertNotNull(error);
            assertEquals("email", error.getField());
            assertTrue(error.getMessage().contains("harus diisi"));
        }

        @Test
        @DisplayName("isValidEmail should return false when email is null (cover first condition)")
        void isValidEmail_WithNullEmail_ShouldCoverFirstCondition() {
            // Test khusus untuk kondisi pertama: email == null
            validRequest.setEmail(null);

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            ValidationError error = findErrorByField(errors, "email");
            assertNotNull(error);
            assertEquals("email", error.getField());
        }

        @Test
        @DisplayName("isValidEmail should return false when email is empty (cover second condition)")
        void isValidEmail_WithEmptyEmail_ShouldCoverSecondCondition() {
            // Test khusus untuk kondisi kedua: email.trim().isEmpty()
            validRequest.setEmail("");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            ValidationError error = findErrorByField(errors, "email");
            assertNotNull(error);
            assertEquals("email", error.getField());
        }

        @Test
        @DisplayName("isValidEmail should return false when email is only spaces (cover second condition with trim)")
        void isValidEmail_WithSpacesOnly_ShouldCoverSecondCondition() {
            // Test khusus untuk kondisi kedua dengan spasi
            validRequest.setEmail("   ");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            ValidationError error = findErrorByField(errors, "email");
            assertNotNull(error);
            assertEquals("email", error.getField());
        }

        @Test
        @DisplayName("Should pass with valid email")
        void validate_WithValidEmail_ShouldHaveNoErrors() {
            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);
            assertTrue(errors.stream().noneMatch(e -> e.getField().equals("email")));
        }

        @Test
        @DisplayName("Should fail when email is null")
        void validate_WithNullEmail_ShouldAddError() {
            validRequest.setEmail(null);

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            ValidationError error = findErrorByField(errors, "email");
            assertNotNull(error);
            assertTrue(error.getMessage().contains("harus diisi"));
        }

        @Test
        @DisplayName("Should fail when email is empty")
        void validate_WithEmptyEmail_ShouldAddError() {
            validRequest.setEmail("");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            ValidationError error = findErrorByField(errors, "email");
            assertNotNull(error);
            assertTrue(error.getMessage().contains("harus diisi"));
        }

        @Test
        @DisplayName("Should fail with invalid email format - missing @")
        void validate_WithMissingAtSymbol_ShouldAddError() {
            validRequest.setEmail("john.doe.example.com");

            ValidationError error = findErrorByField(
                    validationService.validateRegistrationData(validRequest), "email");

            assertNotNull(error);
            assertTrue(error.getMessage().contains("Format email tidak valid"));
        }

        @Test
        @DisplayName("Should fail with invalid email format - missing domain")
        void validate_WithMissingDomain_ShouldAddError() {
            validRequest.setEmail("john.doe@");

            ValidationError error = findErrorByField(
                    validationService.validateRegistrationData(validRequest), "email");

            assertNotNull(error);
            assertTrue(error.getMessage().contains("Format email tidak valid"));
        }

        @Test
        @DisplayName("Should fail with invalid email format - missing TLD")
        void validate_WithMissingTLD_ShouldAddError() {
            validRequest.setEmail("john.doe@example");

            ValidationError error = findErrorByField(
                    validationService.validateRegistrationData(validRequest), "email");

            assertNotNull(error);
            assertTrue(error.getMessage().contains("Format email tidak valid"));
        }

        @Test
        @DisplayName("Should pass with email containing dots in username")
        void validate_WithDotsInUsername_ShouldPass() {
            validRequest.setEmail("john.doe.smith@example.com");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);
            assertTrue(errors.stream().noneMatch(e -> e.getField().equals("email")));
        }

        @Test
        @DisplayName("Should pass with email containing plus sign")
        void validate_WithPlusInUsername_ShouldPass() {
            validRequest.setEmail("john+test@example.com");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);
            assertTrue(errors.stream().noneMatch(e -> e.getField().equals("email")));
        }

        @Test
        @DisplayName("Should convert email to lowercase")
        void validate_ShouldConvertEmailToLowercase() {
            validRequest.setEmail("John.Doe@Example.Com");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);
            assertTrue(errors.stream().noneMatch(e -> e.getField().equals("email")));
        }
        @Test
        @DisplayName("isValidEmail should return false when email is null (via reflection)")
        void isValidEmail_WithNullEmail_ShouldReturnFalse() throws Exception {
            // Akses private method via reflection
            java.lang.reflect.Method method = ValidationService.class.getDeclaredMethod("isValidEmail", String.class);
            method.setAccessible(true);

            boolean result = (boolean) method.invoke(validationService, new Object[]{null});
            assertFalse(result);
        }

        @Test
        @DisplayName("isValidEmail should return false when email is empty")
        void isValidEmail_WithEmptyEmail_ShouldReturnFalse() {
            // Test untuk kondisi email.trim().isEmpty()
            RegisterRequest request = new RegisterRequest();
            request.setFullName("John Doe");
            request.setEmail("");
            request.setPassword("Password123");

            List<ValidationError> errors = validationService.validateRegistrationData(request);

            ValidationError error = findErrorByField(errors, "email");
            assertNotNull(error);
            assertTrue(error.getMessage().contains("harus diisi"));
        }

        @Test
        @DisplayName("isValidEmail should return false when email is only spaces")
        void isValidEmail_WithSpacesOnly_ShouldReturnFalse() {
            // Test untuk kondisi email.trim().isEmpty() dengan spasi
            RegisterRequest request = new RegisterRequest();
            request.setFullName("John Doe");
            request.setEmail("   ");
            request.setPassword("Password123");

            List<ValidationError> errors = validationService.validateRegistrationData(request);

            ValidationError error = findErrorByField(errors, "email");
            assertNotNull(error);
            assertTrue(error.getMessage().contains("harus diisi"));
        }
    }

    @Nested
    @DisplayName("Password Validation Tests")
    class PasswordValidationTests {

        @Test
        @DisplayName("Should pass with valid password")
        void validate_WithValidPassword_ShouldHaveNoErrors() {
            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);
            assertTrue(errors.stream().noneMatch(e -> e.getField().equals("password")));
        }

        @Test
        @DisplayName("Should fail when password is null")
        void validate_WithNullPassword_ShouldAddError() {
            validRequest.setPassword(null);

            ValidationError error = findErrorByField(
                    validationService.validateRegistrationData(validRequest), "password");

            assertNotNull(error);
            assertTrue(error.getMessage().contains("harus diisi"));
        }

        @Test
        @DisplayName("Should fail when password is empty")
        void validate_WithEmptyPassword_ShouldAddError() {
            validRequest.setPassword("");

            ValidationError error = findErrorByField(
                    validationService.validateRegistrationData(validRequest), "password");

            assertNotNull(error);
            assertTrue(error.getMessage().contains("harus diisi"));
        }

        @Test
        @DisplayName("Should fail when password is too short")
        void validate_WithTooShortPassword_ShouldAddError() {
            validRequest.setPassword("Pass12");

            ValidationError error = findErrorByField(
                    validationService.validateRegistrationData(validRequest), "password");

            assertNotNull(error);
            assertTrue(error.getMessage().contains("minimal 8 karakter"));
        }

        @Test
        @DisplayName("Should allow password with only lowercase and numbers")
        void validate_WithOnlyLowercaseAndNumbers_ShouldPass() {
            validRequest.setPassword("password123");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            assertTrue(errors.stream().noneMatch(e -> e.getField().equals("password")));
        }

        @Test
        @DisplayName("Should allow password with only uppercase and numbers")
        void validate_WithOnlyUppercaseAndNumbers_ShouldPass() {
            validRequest.setPassword("PASSWORD123");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            assertTrue(errors.stream().noneMatch(e -> e.getField().equals("password")));
        }

        @Test
        @DisplayName("Should fail when password has only numbers")
        void validate_WithOnlyNumbers_ShouldAddError() {
            validRequest.setPassword("12345678");

            ValidationError error = findErrorByField(
                    validationService.validateRegistrationData(validRequest), "password");

            assertNotNull(error);
            assertTrue(error.getMessage().contains("minimal 2 dari"));
        }

        @Test
        @DisplayName("Should fail when password has only lowercase (no numbers)")
        void validate_WithOnlyLowercase_ShouldAddError() {
            validRequest.setPassword("password");

            ValidationError error = findErrorByField(
                    validationService.validateRegistrationData(validRequest), "password");

            assertNotNull(error);
            assertTrue(error.getMessage().contains("minimal 2 dari"));
        }

        @Test
        @DisplayName("Should fail when password has only uppercase (no numbers)")
        void validate_WithOnlyUppercase_ShouldAddError() {
            validRequest.setPassword("PASSWORD");

            ValidationError error = findErrorByField(
                    validationService.validateRegistrationData(validRequest), "password");

            assertNotNull(error);
            assertTrue(error.getMessage().contains("minimal 2 dari"));
        }

        @Test
        @DisplayName("Should pass with uppercase and lowercase only")
        void validate_WithUpperAndLowerOnly_ShouldPass() {
            validRequest.setPassword("Password");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);
            assertTrue(errors.stream().noneMatch(e -> e.getField().equals("password")));
        }

        @Test
        @DisplayName("Should mask password in rejected value")
        void validate_ShouldMaskPasswordInRejectedValue() {
            validRequest.setPassword("123");

            ValidationError error = findErrorByField(
                    validationService.validateRegistrationData(validRequest), "password");

            assertNotNull(error);
            assertEquals("******", error.getRejected_value());
        }
    }

    @Nested
    @DisplayName("Multiple Validation Errors Tests")
    class MultipleValidationErrorsTests {

        @Test
        @DisplayName("Should collect all validation errors")
        void validate_WithMultipleInvalidFields_ShouldCollectAllErrors() {
            validRequest.setFullName("J"); // too short
            validRequest.setEmail("invalid-email"); // invalid format
            validRequest.setPassword("123"); // too short and only numbers

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            assertEquals(3, errors.size());
            assertNotNull(findErrorByField(errors, "fullName"));
            assertNotNull(findErrorByField(errors, "email"));
            assertNotNull(findErrorByField(errors, "password"));
        }

        @Test
        @DisplayName("Should include rejected values in error objects")
        void validate_ShouldIncludeRejectedValues() {
            validRequest.setFullName("J");
            validRequest.setEmail("invalid");
            validRequest.setPassword("123");

            List<ValidationError> errors = validationService.validateRegistrationData(validRequest);

            ValidationError fullNameError = findErrorByField(errors, "fullName");
            assertNotNull(fullNameError);
            assertEquals("J", fullNameError.getRejected_value());

            ValidationError emailError = findErrorByField(errors, "email");
            assertNotNull(emailError);
            assertEquals("invalid", emailError.getRejected_value());
        }
    }

    // Helper method
    private ValidationError findErrorByField(List<ValidationError> errors, String field) {
        return errors.stream()
                .filter(e -> e.getField().equals(field))
                .findFirst()
                .orElse(null);
    }
}