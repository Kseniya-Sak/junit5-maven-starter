package edu.sakovichKseniya.junit.service;

import edu.sakovichKseniya.junit.dto.User;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserService {

    private final List<User> users = new ArrayList<>();

    public List<User> getAll() {
        return users;
    }

    public boolean add(User user) {
        return users.add(user);
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("username or password is null");
        }
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public void addAll(User... users) {
        this.users.addAll(Arrays.asList(users));
    }

    public Map<Integer, User> getAllConvertedById() {
        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
