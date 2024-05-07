package com.msoft.mbi.data.services;

import com.msoft.mbi.data.api.data.User;
import com.msoft.mbi.data.api.dtos.user.BIUserSummary;
import com.msoft.mbi.model.BIUserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    List<BIUserEntity> getUsers();

    Optional<BIUserEntity> findByEmail(String email);

    void saveUserVerificationToken(BIUserEntity theUser, String verificationToken);

    String validateToken(String theToken);

    Optional<BIUserSummary> findUserSummaryByEmail(String email);

}
