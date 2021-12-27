package com.sparta.showmethecode.user.repository;

import com.sparta.showmethecode.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserDao {

    Optional<User> findByUsername(String username);
}
