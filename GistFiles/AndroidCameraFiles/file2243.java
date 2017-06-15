package com.nastya.service;
import com.nastya.entity.Report;

import java.util.List;

/**
 * @author Korchagina Nastya
 * @version 1.0
 */
public interface ReportService {
        public long createReport(Report report);
        public Report updateReport(Report report);
        public void deleteReport(long id);
        public List<Report> getAllMessages();
        public Report getReport(long id);
        public List<Report> getAllMessages(String theme);
}
