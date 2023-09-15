package com.rmsoft.library.repository;

import com.rmsoft.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    public boolean existsByUserIdAndUserPassword(String userId, String userPassword);

    public User findByUserId(String userId);
}
