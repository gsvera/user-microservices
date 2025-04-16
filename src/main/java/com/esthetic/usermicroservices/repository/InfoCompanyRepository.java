package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.InfoCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InfoCompanyRepository extends JpaRepository<InfoCompany, Long> {
    Optional<InfoCompany> findByIdUser(String idUser);
    @Query(value = "SELECT \n" +
            "\tDISTINCT(u.id),\n" +
            "\tic.id,\n" +
            "\tic.company_name,\n" +
            "\tic.general_description,\n" +
            "\tic.company_picture,\n" +
            "\tSTRING_AGG(ts.type_service_name_es, ',')\n" +
            "FROM esthetic_base.tbl_user AS u \n" +
            "JOIN esthetic_base.tbl_type_service_x_user AS tsu ON tsu.id_user = u.id\n" +
            "JOIN esthetic_base.tbl_catalog_type_service AS ts ON tsu.id_type_service = ts.id\n" +
            "JOIN esthetic_base.tbl_info_company AS ic ON ic.id_user = u.id\n" +
            "WHERE u.is_provider = true AND u.active_provider = true AND EXISTS (\n" +
            "      SELECT 1 \n" +
            "      FROM esthetic_base.tbl_type_service_x_user tsu2 \n" +
            "      WHERE tsu2.id_user = u.id \n" +
            "  )\n" +
            "GROUP BY u.id, ic.id;", nativeQuery = true)
    Page<Object[]> getProvider(Pageable pageable);
    @Query(value = "SELECT \n" +
            "\tDISTINCT(u.id),\n" +
            "\tic.id,\n" +
            "\tic.company_name,\n" +
            "\tic.general_description,\n" +
            "\tic.company_picture,\n" +
            "\tSTRING_AGG(ts.type_service_name_es, ',')\n" +
            "FROM esthetic_base.tbl_user AS u \n" +
            "JOIN esthetic_base.tbl_type_service_x_user AS tsu ON tsu.id_user = u.id\n" +
            "JOIN esthetic_base.tbl_catalog_type_service AS ts ON tsu.id_type_service = ts.id\n" +
            "JOIN esthetic_base.tbl_info_company AS ic ON ic.id_user = u.id\n" +
            "WHERE u.is_provider = true AND u.active_provider = true AND EXISTS (\n" +
            "      SELECT 1 \n" +
            "      FROM esthetic_base.tbl_type_service_x_user tsu2 \n" +
            "      WHERE tsu2.id_user = u.id AND (?1 IS NULL OR tsu2.id_type_service IN (?1))\n" +
            "  )\n" +
            "GROUP BY u.id, ic.id;", nativeQuery = true)
    Page<Object[]> getProviderByTypeServices(List<String> typeService, Pageable pageable);
}
