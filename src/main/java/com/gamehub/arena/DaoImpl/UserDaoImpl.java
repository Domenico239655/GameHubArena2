package com.gamehub.arena.DaoImpl;

import com.gamehub.arena.dao.UserDao;
import com.gamehub.arena.dao.UserRepository;
import com.gamehub.arena.model.User;

import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private final UserRepository repo;
    public UserDaoImpl(UserRepository repo){
        this.repo = repo;
    }

    @Override
    public User save(User user) {
        return repo.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repo.findByUsername(username);
    }
}
