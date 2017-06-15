
package com.nastya.dao;

import java.util.List;

import com.nastya.entity.Report;

/**
 * @author Korchagina Nastya
 * @version 1.0
 */
public interface ReportDAO {
    public long createReport(Report report);
    public Report updateReport(Report report);
    public void deleteReport(long id);
    public List<Report> getAllMessages();
    public Report getReport(long id);
    public List<Report> getAllMessages(String theme);
}