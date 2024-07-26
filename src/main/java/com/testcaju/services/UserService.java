package com.testcaju.services;

import com.testcaju.domain.user.User;
import com.testcaju.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public Optional<User> getById(String id) {
        return this.repository.findById(id);
    }

    public void saveUser(User user) {
        this.repository.save(user);
    }

    public List<User> all() {
        return this.repository.findAll();
    }
}
