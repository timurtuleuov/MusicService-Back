package com.timur.spotify.service.auth;

import com.timur.spotify.entity.auth.User;
import com.timur.spotify.repository.auth.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // save user
    public User save(User user){
        return repository.save(user);
    }

    // getById user
    public User getById(Long id){
        return repository.getReferenceById(id);
    }

    // get all users
    public List<User> getAllUser(){
        return repository.findAll();
    }
    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }
    // update user
    public User update(User user){
        User updatedUser = getById(user.getId());
        updatedUser.setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setUsername(user.getUsername());
        return repository.save(updatedUser);
    }

    // delete user
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
