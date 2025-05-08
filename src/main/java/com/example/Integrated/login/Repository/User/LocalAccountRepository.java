package com.example.Integrated.login.Repository.User;

import com.example.Integrated.login.Entity.User.LocalAccount;
import com.example.Integrated.login.Entity.User.SocialAccount;
import com.example.Integrated.login.Entity.User.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LocalAccountRepository extends JpaRepository<LocalAccount, Long> {

    LocalAccount findByEmail(String email);
}
