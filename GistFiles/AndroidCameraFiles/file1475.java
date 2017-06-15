
package com.nastya.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;
/**
 * @author Korchagina Nastya
 * @version 1.0
 *
 */
@Entity
@Table(name = "Messages")
public class Report implements Serializable {

    private static final long serialVersionUID = -7988799579036225137L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String name;

    @Column
    private char tag;

    @Column
    private String theme;

    @Column
    private String textReport;

    public Report() {
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getNameReporter() {
        return name;
    }
    public void setNameReporter(String name) {
        this.name = name;
    }
    public char getTag() {
        return tag;
    }
    public void setTag(char tag) {
        this.tag = tag;
    }
    public String getTheme() {
        return theme;
    }
    public void setTheme(String theme) {
        this.theme = theme;
    }
    public String getTextReport() {
        return textReport;
    }
    public void setTextReport(String textReport) {
        this.textReport = textReport;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tag=" + tag +
                ", theme=" + theme +
                ", textReport=" + textReport +
                '}';
    }
}
