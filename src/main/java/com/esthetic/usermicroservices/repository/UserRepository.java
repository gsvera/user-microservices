package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.esthetic.usermicroservices.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM tbl_user u WHERE LOWER(u.email) = LOWER(?1) OR u.phone = ?2 LIMIT 1", nativeQuery = true)
    User findByEmailQueryNative(String email, String phone);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findById(String id);
    Optional<User> findByToken(String token);
    @Transactional @Modifying
    @Query(value = "UPDATE tbl_user SET token = ?2 WHERE id = ?1", nativeQuery = true)
    int updateTokenById(String id, String token);
    @Transactional @Modifying
    @Query(value = "UPDATE tbl_user SET password = ?1 WHERE email = ?2", nativeQuery = true)
    int updatePasswordByEmail(String password, String email);
    @Transactional @Modifying
    @Query(value = "UPDATE tbl_user SET first_name = ?1, last_name = ?2, phone = ?3, email = ?4 WHERE id = ?5", nativeQuery = true)
    int updateInformationPersonel(String firstName, String lastName, String phone, String email, String id);
    @Transactional @Modifying
    @Query(value = "UPDATE tbl_user SET profile_picture_b64 = ?2 WHERE token = ?1", nativeQuery = true)
    int updateProfilePicture(String token, String imgB64);
    @Transactional @Modifying
    @Query(value = "UPDATE tbl_user SET token = NULL, token_notification = NULL WHERE token = ?1", nativeQuery = true)
    int updateToken(String token);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbl_user WHERE id = ?1", nativeQuery = true)
    void deleteUserById(String id);
    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.isClient = false WHERE id = ?1")
    void removeIsClient(String id);
    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.userInfoCompany LEFT JOIN FETCH u.userLocation WHERE u.id = ?1")
    Optional<User> findUserProviderWithDetails(String idUser);
    @Query(value = "SELECT u FROM User u WHERE accountVerification = false AND (isProvider = false OR isProvider IS NULL)")
    List<User> findUserClientInactive();

    @Query(value = "SELECT u from User u WHERE isProvider = true")
    List<User> findUserProvider();
    @Query(value = "SELECT new com.esthetic.usermicroservices.dto.UserDTO(u.id, u.firstName, u.lastName, u.email, u.phone, u.lada) FROM User u WHERE id = ?1")
    Optional<UserDTO> findUserSimpleData(String idUser);
}
