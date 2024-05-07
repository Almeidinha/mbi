package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.api.data.User;
import com.msoft.mbi.data.api.dtos.user.BIUserSummary;
import com.msoft.mbi.data.repositories.BIUserRepository;
import com.msoft.mbi.data.services.UserService;
import com.msoft.mbi.model.BIUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final BIUserRepository userRepository;

    @Override
    public List<BIUserEntity> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<BIUserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(BIUserEntity theUser, String verificationToken) {

    }

    @Override
    public String validateToken(String theToken) {
        return null;
    }

    @Override
    public Optional<BIUserSummary> findUserSummaryByEmail(String email) {
        return this.userRepository.findUserSummaryByEmail(email);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findUserSummaryByEmail(username)
                .map(User::new)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }
}
