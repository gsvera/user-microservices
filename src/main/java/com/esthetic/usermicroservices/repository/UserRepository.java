package com.esthetic.usermicroservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.esthetic.usermicroservices.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from tbl_user u where upper(u.email) = ?1 or u.phone = ?2", nativeQuery = true)
    User findByEmailQueryNative(String email, String phone);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByToken(String token);
    @Transactional @Modifying
    @Query(value = "UPDATE tbl_user u SET u.token = ?2 WHERE u.id = ?1", nativeQuery = true)
    int updateTokenById(String id, String token);
    @Transactional @Modifying
    @Query(value = "UPDATE tbl_user u SET u.password = ?1 WHERE u.email = ?2", nativeQuery = true)
    int updatePasswordByEmail(String password, String email);
    @Transactional @Modifying
    @Query(value = "UPDATE tbl_user u SET u.first_name = ?1, u.last_name = ?2, u.phone = ?3, u.email = ?4 WHERE u.id = ?5", nativeQuery = true)
    int updateInformationPersonel(String firstName, String lastName, String phone, String email, String id);
    @Transactional @Modifying
    @Query(value = "UPDATE tbl_user u SET u.profile_picture_b64 = ?2 WHERE u.token = ?1", nativeQuery = true)
    int updateProfilePicture(String token, String imgB64);
    @Transactional @Modifying
    @Query(value = "UPDATE tbl_user u SET u.token = '' WHERE u.token = ?1", nativeQuery = true)
    int updateToken(String token);
}
