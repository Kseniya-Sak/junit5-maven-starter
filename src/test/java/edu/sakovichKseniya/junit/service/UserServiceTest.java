package edu.sakovichKseniya.junit;

import edu.sakovichKseniya.junit.dto.User;
import edu.sakovichKseniya.junit.service.UserService;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    private UserService userService;

    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }

    @BeforeEach
    void prepare() {
        System.out.println("\nBefore each: " + this);
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
        assertEquals(2, users.size());

    }

    @Test
    void loginSuccessIfUserExists() {
        System.out.println("Test 3: " + this);
        userService.add(IVAN);

        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

        assertTrue(maybeUser.isPresent());
        maybeUser.ifPresent(user -> assertEquals(IVAN, maybeUser.get()));

    }

    @Test
    void loginFailIfPasswordIsNotCorrect() {
        System.out.println("Test 4: " + this);
        userService.add(IVAN);

        Optional<User> maybeUser = userService.login("Ivan", "111");

        assertTrue(maybeUser.isEmpty());
    }

    @Test
    void loginFailIfUserDoesNotExists() {
        System.out.println("Test 5: " + this);
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login("d", IVAN.getPassword());

        assertTrue(maybeUser.isEmpty());
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("\nAfter all: " + this);
    }
}
