package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.InfoCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface InfoCompanyRepository extends JpaRepository<InfoCompany, Long> {
    @Query(value = "SELECT * FROM tbl_info_company WHERE id_user = ?1", nativeQuery = true)
    Optional<InfoCompany> findByIdUser(String idUser);
    @Query(value = "SELECT * FROM ( SELECT \n" +
            "DISTINCT(u.id), \n" +
            "ic.id, \n" +
            "ic.company_name, \n" +
            "ic.general_description, \n" +
            "ic.company_picture_url, \n" +
            "STRING_AGG(ts.type_service_name_es, ','), \n" +
            "ul.aux_state, \n" +
            "ul.aux_municipality, \n" +
            "(SELECT ROUND(AVG(rating),1) FROM tbl_provider_ratings WHERE id_provider = u.id AND is_pending = false AND show = true) AS rating \n" +
            "FROM tbl_user AS u \n" +
            "JOIN tbl_type_service_x_user AS tsu ON tsu.id_user = u.id \n" +
            "JOIN tbl_catalog_type_service AS ts ON tsu.id_type_service = ts.id \n" +
            "JOIN tbl_info_company AS ic ON ic.id_user = u.id \n" +
            "JOIN tbl_user_location AS ul ON u.id = ul.id_user \n" +
            "WHERE u.is_provider = true AND u.active_provider = true AND (?1 IS NULL OR ?1 = '' OR ic.company_name ILIKE %?1%) \n" +
            "AND (?2 IS NULL OR ?2 = '' OR ul.aux_state ILIKE %?2%) \n" +
            "AND (?3 IS NULL OR ?3 = '' OR ul.aux_municipality ILIKE %?3%) \n" +
            "      AND EXISTS (SELECT 1 \n" +
            "      FROM tbl_type_service_x_user tsu2 \n" +
            "      WHERE tsu2.id_user = u.id \n" +
            "  )\n" +
            "AND (SELECT t.is_active FROM tbl_user_plan t WHERE id_user = u.id AND is_active = true LIMIT 1) \n" +
            "GROUP BY u.id, ic.id, ul.aux_state, ul.aux_municipality ) sub ORDER BY RANDOM();",
            countQuery = "SELECT COUNT(DISTINCT u.id) FROM tbl_user u " +
                    "JOIN tbl_info_company ic ON ic.id_user = u.id " +
                    "JOIN tbl_user_location ul ON u.id = ul.id_user " +
                    "WHERE u.is_provider = true AND u.active_provider = true " +
                    "AND (?1 IS NULL OR ?1 = '' OR ic.company_name ILIKE CONCAT('%', ?1, '%')) " +
                    "AND (?2 IS NULL OR ?2 = '' OR ul.aux_state ILIKE CONCAT('%', ?2, '%')) " +
                    "AND (?3 IS NULL OR ?3 = '' OR ul.aux_municipality ILIKE CONCAT('%', ?3, '%'))",
            nativeQuery = true)
    Page<Object[]> getProvider(String word, String defaultState, String defaultMunicipality, Pageable pageable);

@Query(value = "SELECT * FROM ( SELECT DISTINCT(u.id), ic.id, ic.company_name, ic.general_description, ic.company_picture_url, " +
        "STRING_AGG(ts.type_service_name_es, ','), " +
        "ul.aux_state, " +
        "ul.aux_municipality, " +
        "(SELECT ROUND(AVG(rating),1) FROM tbl_provider_ratings WHERE id_provider = u.id AND is_pending = false AND show = true) AS rating " +
        "FROM tbl_user AS u " +
        "JOIN tbl_type_service_x_user AS tsu ON tsu.id_user = u.id " +
        "JOIN tbl_catalog_type_service AS ts ON tsu.id_type_service = ts.id " +
        "JOIN tbl_info_company AS ic ON ic.id_user = u.id " +
        "JOIN tbl_user_location AS ul ON u.id = ul.id_user " +
        "WHERE u.is_provider = true AND u.active_provider = true " +
        "AND (:word IS NULL OR :word = '' OR ic.company_name ILIKE %:word%) " +
        "AND (:defaultState IS NULL OR :defaultState = '' OR ul.aux_state ILIKE %:defaultState%) " +
        "AND (:defaultMunicipality IS NULL OR :defaultMunicipality = '' OR ul.aux_municipality ILIKE %:defaultMunicipality%) " +
        "AND (:types IS NULL OR EXISTS (" +
        "    SELECT 1 FROM tbl_type_service_x_user tsu2 " +
        "    WHERE tsu2.id_user = u.id " +
        "    AND tsu2.id_type_service = ANY(CAST(:types AS int[]))" +
        ")) " +
        "AND (SELECT t.is_active FROM tbl_user_plan t WHERE id_user = u.id AND is_active = true LIMIT 1) " +
        "GROUP BY u.id, ic.id, ul.aux_state, ul.aux_municipality) sub ORDER BY RANDOM() ",
        countQuery = "SELECT COUNT(*) FROM tbl_user AS u " +
                "JOIN tbl_type_service_x_user AS tsu ON tsu.id_user = u.id " +
                "JOIN tbl_catalog_type_service AS ts ON tsu.id_type_service = ts.id " +
                "JOIN tbl_info_company AS ic ON ic.id_user = u.id " +
                "JOIN tbl_user_location AS ul ON u.id = ul.id_user " +
                "WHERE u.is_provider = true AND u.active_provider = true " +
                "AND (:word IS NULL OR :word = '' OR ic.company_name ILIKE %:word%) " +
                "AND (:defaultState IS NULL OR :defaultState = '' OR ul.aux_state ILIKE %:defaultState%) " +
                "AND (:defaultMunicipality IS NULL OR :defaultMunicipality = '' OR ul.aux_municipality ILIKE %:defaultMunicipality%) " +
                "AND (:types IS NULL OR EXISTS (" +
                "    SELECT 1 FROM tbl_type_service_x_user tsu2 " +
                "    WHERE tsu2.id_user = u.id " +
                "    AND tsu2.id_type_service = ANY(CAST(:types AS int[]))" +
                "))",
        nativeQuery = true)
Page<Object[]> getProviderByTypeServices(@Param("word") String word, @Param("defaultState") String defaultState, @Param("defaultMunicipality") String defaultMunicipality, @Param("types") Integer[] typeService, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM InfoCompany WHERE user.id = ?1")
    void deleteInfoCompany(String idProvider);
}
