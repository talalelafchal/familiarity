package com.nastya.service.impl;

import com.nastya.dao.ReportDAO;
import com.nastya.entity.Report;
import com.nastya.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * @author Korchagina Nastya
 * @version 1.0
 */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    public ReportServiceImpl() {
        System.out.println("ReportServiceImpl()");
    }

    @Autowired
    private ReportDAO reportDAO;

    @Override
    public long createReport(Report report) {
        return reportDAO.createReport(report);
    }
    @Override
    public Report updateReport(Report report) {
        return reportDAO.updateReport(report);
    }
    @Override
    public void deleteReport(long id) {
        reportDAO.deleteReport(id);
    }
    @Override
    public List<Report> getAllMessages() {
        return reportDAO.getAllMessages();
    }
    @Override
    public Report getReport(long id) {
        return reportDAO.getReport(id);
    }
    @Override
    public List<Report> getAllMessages(String theme) {
        return reportDAO.getAllMessages(theme);
    }
}

