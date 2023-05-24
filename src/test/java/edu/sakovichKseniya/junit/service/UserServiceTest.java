package edu.sakovichKseniya.junit.service;

import edu.sakovichKseniya.junit.dto.User;
import edu.sakovichKseniya.junit.paramResolver.UserServiceParamResolver;
import lombok.Value;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith({
        UserServiceParamResolver.class
})
public class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    private UserService userService;

    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
    static void init() {
        System.out.println("Before all: ");

    }

    @BeforeEach
    void prepare(UserService userService) {
        System.out.println("Before each: " + this);
        this.userService = userService;
    }

    @Test
    void usersEmptyIfNoUsersAdded() {
        System.out.println("Test 1: " + this);
        System.out.println("userService " + userService);
        var users = userService.getAll();
        assertTrue(users.isEmpty(), () -> "Error");
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        System.out.println("userService " + userService);
        userService.add(IVAN);
        userService.add(PETR);

        var users = userService.getAll();

        MatcherAssert.assertThat(users, IsCollectionWithSize.hasSize(2));
    }

    @Test
    void usersConvertedToMapById() {
        System.out.println("userService " + userService);
        userService.addAll(IVAN, PETR);

        Map<Integer, User> users = userService.getAllConvertedById();

        MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.getId()));

        assertAll(
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.getId())),
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasKey(PETR.getId())),
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasValue(IVAN)),
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasValue(PETR))
        );
    }

    @Nested
    @DisplayName("test user login functionality")
    @Tag("login")
    class LoginTest {
        @Test
        void loginSuccessIfUserExists() {
            userService.add(IVAN);

            Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

            MatcherAssert.assertThat(maybeUser, IsNull.notNullValue());
            maybeUser.ifPresent(user -> MatcherAssert.assertThat(user, IsEqual.equalTo(IVAN)));
        }

        @Test
        void loginFailIfPasswordIsNotCorrect() {
            userService.add(IVAN);

            Optional<User> maybeUser = userService.login("Ivan", "111");

            MatcherAssert.assertThat(maybeUser, IsEqual.equalTo(Optional.empty()));
        }

        @Test
        void loginFailIfUserDoesNotExists() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login("d", IVAN.getPassword());

            MatcherAssert.assertThat(maybeUser, IsEqual.equalTo(Optional.empty()));
        }

        @Test
        void throwExceptionIfUsernameOfPasswordIsNull() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "111"),
                            "login should throw exception on null username"),
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("Ivan", null))
            );
        }

        @ParameterizedTest
        @MethodSource("edu.sakovichKseniya.junit.service.UserServiceTest#getArgumentsForLoginTest")
        void loginParametrizedTest(String username, String password, Optional<User> user) {
            userService.addAll(IVAN, PETR);
            Optional<User> maybeUser = userService.login(username, password);

            org.assertj.core.api.Assertions.assertThat(maybeUser).isEqualTo(user);

        }
    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "111", Optional.of(PETR)),
                Arguments.of("Petr", "ddd", Optional.empty()),
                Arguments.of("ddd", "111", Optional.empty())
        );
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this);
        System.out.println();
    }

    @AfterAll
    static void closeConnectionPool() {
        System.out.println("After all: ");
    }
}
