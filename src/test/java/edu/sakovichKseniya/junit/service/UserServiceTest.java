package edu.sakovichKseniya.junit.service;

import edu.sakovichKseniya.junit.dto.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    private UserService userService;

    @BeforeAll
    static void init() {
        System.out.println("Before all: ");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
        userService = new UserService();
    }

    @Test
    void usersEmptyIfNoUsersAdded() {
        System.out.println("Test 1: " + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty(), () -> "Error");
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);

        var users = userService.getAll();

        MatcherAssert.assertThat(users, IsCollectionWithSize.hasSize(2));
    }

    @Test
    @Tag("login")
    void loginSuccessIfUserExists() {
        userService.add(IVAN);

        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

        MatcherAssert.assertThat(maybeUser, IsNull.notNullValue());
        maybeUser.ifPresent(user -> MatcherAssert.assertThat(user, IsEqual.equalTo(IVAN)));
    }

    @Test
    @Tag("login")
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);

        Optional<User> maybeUser = userService.login("Ivan", "111");

        MatcherAssert.assertThat(maybeUser, IsEqual.equalTo(Optional.empty()));
    }

    @Test
    @Tag("login")
    void loginFailIfUserDoesNotExists() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login("d", IVAN.getPassword());

        MatcherAssert.assertThat(maybeUser, IsEqual.equalTo(Optional.empty()));
    }

    @Test
    void usersConvertedToMapById() {
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

    @Test
    @Tag("login")
    void throwExceptionIfUsernameOfPasswordIsNull() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "111"),
                        "login should throw exception on null username"),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login("Ivan", null))
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
