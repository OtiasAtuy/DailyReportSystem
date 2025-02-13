package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report, Employee loginUser) {

        // ログイン中の従業員かつ日付重複しているかのチェック
        boolean duplicate = reportRepository.existsByEmployee_CodeAndReportDate(loginUser.getCode(), report.getReportDate());

        if (duplicate) {
            report.setEmployee(loginUser);
            return ErrorKinds.DUPLICATE_ERROR;
        }

        report.setDeleteFlg(false);
        report.setEmployee(loginUser);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Report report, Employee loginUser) {
        //　IDで検索をかけ、既存のReportをupdateReportに乗せ換える
        Report updateReport = findById(report.getId());

        // ログイン中の従業員かつ日付重複しているかのチェック
        boolean duplicate = reportRepository.existsByEmployee_CodeAndReportDateAndIdNot(loginUser.getCode(), report.getReportDate(), report.getId());

        if (duplicate) {
            report.setEmployee(loginUser);
            return ErrorKinds.DUPLICATE_ERROR;
        }

        // UpdateReportに更新箇所を上書きしていき、保存する
        updateReport.setReportDate(report.getReportDate());
        updateReport.setTitle(report.getTitle());
        updateReport.setContent(report.getContent());

        LocalDateTime now = LocalDateTime.now();
        updateReport.setUpdatedAt(now);
        reportRepository.save(updateReport);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(String id) {
        Report report = findById(id);
        System.out.println("日報削除：" + id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 日報一覧表示処理
    public List<Report> findAll(Employee loginUser) {
        if (loginUser.getRole() == Employee.Role.ADMIN) {
            return reportRepository.findAll();
        } else {
            return reportRepository.findByEmployee(loginUser);
        }
    }

    // IDで1件を検索
    public Report findById(String id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // 従業員に紐づいている日報を検索
    public List<Report> findByEmployee(Employee employee) {
        List<Report> reports = reportRepository.findByEmployee(employee);

        // デバッグログ
        System.out.println("検索結果の件数: " + reports.size());
        for (Report report : reports) {
            System.out.println("取得した日報 ID: " + report.getId() + " | 日付: " + report.getReportDate());
        }

        return reportRepository.findByEmployee(employee);
    }

}
