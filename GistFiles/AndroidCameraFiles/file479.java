package com.nastya.dao.impl;

import com.nastya.dao.ReportDAO;
import com.nastya.entity.Report;
import com.nastya.util.HibernateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Korchagina Nastya
 * @version 1.0
 */

@Repository
public class ReportDAOImpl implements ReportDAO {

    @Autowired
    private HibernateUtil hibernateUtil;

    @Override
    public long createReport(Report report) {
        return (Long) hibernateUtil.create(report);
    }

    @Override
    public Report updateReport(Report report) {
        return hibernateUtil.update(report);
    }

    @Override
    public void deleteReport(long id) {
        Report report = new Report();
        report.setId(id);
        hibernateUtil.delete(report);
    }

    @Override
    public List<Report> getAllMessages() {
        return hibernateUtil.fetchAll(Report.class);
    }

    @Override
    public Report getReport(long id) {
        return hibernateUtil.fetchById(id,Report.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Report> getAllMessages(String Theme) {
        String query = "SELECT e.* FROM Messages e WHERE e.theme like '%"+ Theme +"%'";
        List<Object[]> reportObjects = hibernateUtil.fetchAll(query);
        List<Report> reports = new ArrayList<Report>();
        for(Object[] reportObject:reportObjects) {
            Report report = new Report();
            long id = ((BigInteger) reportObject[0]).longValue();
            char tag = (Character) reportObject[1];
            String name = (String) reportObject[2];
            String theme = (String) reportObject[3];
            String textReport = (String) reportObject[4];
            report.setId(id);
            report.setNameReporter(name);
            report.setTag(tag);
            report.setTheme(theme);
            report.setTextReport(textReport);
            reports.add(report);
        }
        System.out.println(reports);
        return reports;
    }
}

