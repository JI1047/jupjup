package com.example.Integrated.login.Repository.User;

import com.example.Integrated.login.Entity.User.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"userDetail"})
    Optional<User> findById(Long id);
    @Query("""
    select u from User u
    left join fetch u.localAccount
    left join fetch u.userDetail
    where u.id = :id
    """)
    Optional<User> findWithLocalAndDetail(Long id);

}
