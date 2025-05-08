package com.example.Integrated.login.Repository.User;

import com.example.Integrated.login.Entity.User.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {
}
