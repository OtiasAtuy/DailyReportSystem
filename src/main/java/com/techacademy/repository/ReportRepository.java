package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
    // レポートを全件検索
    List<Report> findAll();
    // 従業員コードに紐付くレポートを検索する
    List<Report> findByEmployee(Employee employee);
    //　新規登録用：ログイン中のIDで同じReportDateが既に登録されているかチェック
    boolean existsByEmployee_CodeAndReportDate(String employeeCode, LocalDate reportDate);
    // 更新用：特定のIDを除外して重複をチェック
    boolean existsByEmployee_CodeAndReportDateAndIdNot(String employeeCode, LocalDate reportDate, String id);
}