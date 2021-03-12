package com.app.miliwili.src.user;

import com.app.miliwili.src.user.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findBySocialIdAndStatus(String socialId, String status);
    List<User> findByIdAndStatus(Long id, String status);
}