package com.esthetic.usermicroservices.repository;

import com.esthetic.usermicroservices.entity.PaymentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentPlanRepository extends JpaRepository<PaymentPlan, Long> {
    @Query(value = "SELECT p.id, p.amount_paid, p.discount_applied, p.payment_date, c.name FROM tbl_payment_plan AS p LEFT JOIN tbl_catalog_plan AS c ON p.plan_id = c.id WHERE id_user = ?1 ORDER BY p.payment_date DESC LIMIT 12", nativeQuery = true)
    List<Object[]> findLastPayByIdUser(String idUser);
}