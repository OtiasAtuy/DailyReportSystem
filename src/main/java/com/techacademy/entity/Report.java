package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "reports")
@SQLRestriction("delete_flg = false")
public class Report {

    // ID (AUTO_INCREMENTに設定)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //  MySQLのAUTO_INCREMENTに対応
    @Column(name = "id", columnDefinition = "INT AUTO_INCREMENT", nullable = false)
    private String id;

    // 日付
    @NotNull
    @Column(name = "report_date", columnDefinition="DATE",nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    // タイトル
    @Column(columnDefinition="VARCHAR(100)", nullable = false)
    @NotEmpty(message = "値を入力してください") // 空欄を許可しない
    @Length(max = 100, message = "100文字以下で入力してください")
    private String title;

    // 内容
    @Column(columnDefinition="LONGTEXT", nullable = false)
    @NotEmpty(message = "値を入力してください")
    @Length(max = 600, message = "600文字以下で入力してください")
    private String content;

    // 社員番号
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;

    // 削除フラグ(論理削除を行うため)
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
