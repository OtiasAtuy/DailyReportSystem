package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {

        //　ログインユーザー情報を取得する
        Employee loginUser = userDetail.getEmployee();

        List<Report> reportList = reportService.findAll(loginUser);
        model.addAttribute("listSize", reportList.size());
        model.addAttribute("reportList", reportList);

        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable("id") String id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@AuthenticationPrincipal UserDetail userDetail, @ModelAttribute Report report, Model model) {

        //　ログインユーザー情報を取得する
        Employee loginUser = userDetail.getEmployee();

        report.setEmployee(loginUser); // ログインユーザーの情報をセット

        model.addAttribute("report", report); // Modelにreportを追加
        model.addAttribute("employee", loginUser);

        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@AuthenticationPrincipal UserDetail userDetail, @Validated Report report, BindingResult res, Model model) {
     //　ログインユーザー情報を取得する
        Employee loginUser = userDetail.getEmployee();

        // 入力チェック
        if (res.hasErrors()) {
            report.setEmployee(loginUser);
            return "reports/new";
        }

        // 『日報テーブルに、「ログイン中の従業員 かつ 入力した日付」の日報データが存在する場合エラー』
        ErrorKinds result = reportService.save(report, loginUser);
                if (result == ErrorKinds.DUPLICATE_ERROR) {
                    res.rejectValue("reportDate", "error.date", "既に登録されている日付です");
                    return "reports/new";
                }

        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") String id, Model model) {

        ErrorKinds result = reportService.delete(id);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id));
            return "reports/detail";
        }

        return "redirect:/reports";
    }

    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable("id") String id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping(value = "/{id}/update")
    public String update(@AuthenticationPrincipal UserDetail userDetail, @PathVariable("id") String id, @Validated Report report, BindingResult res, Model model) {
     //　ログインユーザー情報を取得する
        Employee loginUser = userDetail.getEmployee();
        //report = reportService.findById(id);


        // 入力チェック
        if (res.hasErrors()) {
            report.setEmployee(loginUser);
            return "reports/update";
        }

        // 『日報テーブルに、「ログイン中の従業員 かつ 入力した日付」の日報データが存在する場合エラー』
        ErrorKinds result = reportService.update(report, loginUser);
                if (result == ErrorKinds.DUPLICATE_ERROR) {
                    res.rejectValue("reportDate", "error.date", "既に登録されている日付です");
                    return "reports/update";
                }

        return "redirect:/reports";
    }



}
