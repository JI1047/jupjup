package com.example.Integrated.login.Repository.User;

import com.example.Integrated.login.Entity.User.SocialAccount;
import com.example.Integrated.login.Entity.User.SocialProvider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    @EntityGraph(attributePaths = {"user", "user.userDetail"})
    SocialAccount findBySnsIdAndProvider(String snsId, SocialProvider provider);

}
