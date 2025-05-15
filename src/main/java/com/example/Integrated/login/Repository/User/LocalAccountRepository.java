package com.example.Integrated.login.Repository.User;

import com.example.Integrated.login.Entity.User.LocalAccount;
import com.example.Integrated.login.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface LocalAccountRepository extends JpaRepository<LocalAccount, Long> {

    @Query("SELECT la FROM LocalAccount la WHERE la.email = :email")
    LocalAccount findByEmailWithLocalAccount(@Param("email") String email);

    @Query("SELECT u FROM LocalAccount la JOIN la.user u WHERE la.email = :email")
    User findUserByLocalAccountEmail(@Param("email") String email);



}
