package com.nastya.controller;
import com.nastya.entity.Report;
import com.nastya.service.ReportService;

import org.jboss.logging.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Korchagina Nastya
 * @version 1.0
 */
@Controller
public class ReportController {

    private static final Logger logger = Logger.getLogger(ReportController.class);

    public ReportController() {
        System.out.println("ReportController()");
    }

    @Autowired
    private ReportService reportService;

    @RequestMapping("createReport")
    public ModelAndView createReport(@ModelAttribute Report report) {
        logger.info("Creating Report. Data: "+report);
        return new ModelAndView("reportForm");
    }

    @RequestMapping("editReport")
    public ModelAndView editEmployee(@RequestParam long id, @ModelAttribute Report report) {
        logger.info("Updating the Report for the Id "+id);
        report = reportService.getReport(id);
        return new ModelAndView("reportForm", "reportObject", report);
    }

    @RequestMapping("saveReport")
    public ModelAndView saveReport(@ModelAttribute Report report) {
        logger.info("Saving the Report. Data : "+report);
        if(report.getId() == 0){
            reportService.createReport(report);
        } else {
            reportService.updateReport(report);
        }
        return new ModelAndView("redirect:getAllMessages");
    }

    @RequestMapping("deleteReport")
    public ModelAndView deleteReport(@RequestParam long id) {
        logger.info("Deleting the Report. Id : "+id);
        reportService.deleteReport(id);
        return new ModelAndView("redirect:getAllMessages");
    }

    @RequestMapping(value = {"getAllMessages", "/"})
    public ModelAndView getAllMessages() {
        logger.info("Getting the all Messages.");
        List<Report> reportList = reportService.getAllMessages();
        return new ModelAndView("reportList", "reportList", reportList);
    }

    @RequestMapping("searchReport")
    public ModelAndView searchReport(@RequestParam("searchTheme") String searchTheme) {
        logger.info("Searching the Report. Reporter Themes: "+searchTheme);
        List<Report> reportList = reportService.getAllMessages(searchTheme);
        return new ModelAndView("reportList", "reportList", reportList);
    }
}