package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.InfoCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InfoCompanyRepository extends JpaRepository<InfoCompany, Long> {
    @Query(value = "SELECT * FROM tbl_info_company WHERE id_user = ?1", nativeQuery = true)
    Optional<InfoCompany> findByIdUser(String idUser);
    @Query(value = "SELECT \n" +
            "DISTINCT(u.id), \n" +
            "ic.id, \n" +
            "ic.company_name, \n" +
            "ic.general_description, \n" +
            "ic.company_picture, \n" +
            "STRING_AGG(ts.type_service_name_es, ',') \n" +
            "FROM tbl_user AS u \n" +
            "JOIN tbl_type_service_x_user AS tsu ON tsu.id_user = u.id \n" +
            "JOIN tbl_catalog_type_service AS ts ON tsu.id_type_service = ts.id \n" +
            "JOIN tbl_info_company AS ic ON ic.id_user = u.id \n" +
            "WHERE u.is_provider = true AND u.active_provider = true AND (?1 IS NULL OR ic.company_name ILIKE %?1%) AND EXISTS (\n" +
            "      SELECT 1 \n" +
            "      FROM tbl_type_service_x_user tsu2 \n" +
            "      WHERE tsu2.id_user = u.id \n" +
            "  )\n" +
            "GROUP BY u.id, ic.id;", nativeQuery = true)
    Page<Object[]> getProvider(String word, Pageable pageable);

@Query(value = "SELECT DISTINCT(u.id), ic.id, ic.company_name, ic.general_description, ic.company_picture, " +
        "STRING_AGG(ts.type_service_name_es, ',') " +
        "FROM tbl_user AS u " +
        "JOIN tbl_type_service_x_user AS tsu ON tsu.id_user = u.id " +
        "JOIN tbl_catalog_type_service AS ts ON tsu.id_type_service = ts.id " +
        "JOIN tbl_info_company AS ic ON ic.id_user = u.id " +
        "WHERE u.is_provider = true AND u.active_provider = true " +
        "AND (:word IS NULL OR ic.company_name ILIKE %:word%) " +
        "AND (:types IS NULL OR EXISTS (" +
        "    SELECT 1 FROM tbl_type_service_x_user tsu2 " +
        "    WHERE tsu2.id_user = u.id " +
        "    AND tsu2.id_type_service = ANY(CAST(:types AS int[]))" +
        ")) " +
        "GROUP BY u.id, ic.id",
        countQuery = "SELECT COUNT(*) FROM tbl_user AS u " +
                "JOIN tbl_type_service_x_user AS tsu ON tsu.id_user = u.id " +
                "JOIN tbl_catalog_type_service AS ts ON tsu.id_type_service = ts.id " +
                "JOIN tbl_info_company AS ic ON ic.id_user = u.id " +
                "WHERE u.is_provider = true AND u.active_provider = true " +
                "AND (:word IS NULL OR ic.company_name ILIKE %:word%) " +
                "AND (:types IS NULL OR EXISTS (" +
                "    SELECT 1 FROM tbl_type_service_x_user tsu2 " +
                "    WHERE tsu2.id_user = u.id " +
                "    AND tsu2.id_type_service = ANY(CAST(:types AS int[]))" +
                "))",
        nativeQuery = true)
Page<Object[]> getProviderByTypeServices(@Param("word") String word, @Param("types") Integer[] typeService, Pageable pageable);
}
