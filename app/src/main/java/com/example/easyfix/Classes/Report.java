package com.example.easyfix.Classes;

public class Report {
    private String reporter; // The User who reported
    private String ReportMainType; // Report Title
    private String UrgencyLevel; // UrgencyLevel decided by technician
    private Integer MalfunctionArea; // The Area of the problem
    private Integer MalfunctionRoom; // The Room which the problem is in
    private String TimeReported; // Time Reported
    private String ExtraInformation; // Description
    private String ReportPhoto; // Photo
    private String MalfunctionFixer; // Technician that Fixed the problem
    private String TimeFixed; // Fixed Time
    private String FixedPhoto; // Photo of the Fixed Problem

    public Report() {
        // בנאי ריק.
    }


    public Report(String reporter, String reportMainType, Integer malfunctionArea, Integer malfunctionRoom, String timeReported, String extraInformation, String reportPhoto) {
        // בנאי מלא, עבור מדווח, בתוספת תמונה של התקלה, אם אין תיאור, יהיה ריק ExtraInformation.
        this.reporter = reporter;
        ReportMainType = reportMainType;
        MalfunctionArea = malfunctionArea;
        MalfunctionRoom = malfunctionRoom;
        TimeReported = timeReported;
        ExtraInformation = extraInformation;
        ReportPhoto = reportPhoto;

        UrgencyLevel = "Null"; //  ערכים אלה יתעדכנו רק כאשר טכנאי יתקן את התקלה
        MalfunctionFixer = "Null";
        TimeFixed = "Null";
        FixedPhoto = "Null";
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReportMainType() {
        return ReportMainType;
    }

    public void setReportMainType(String reportMainType) {
        ReportMainType = reportMainType;
    }

    public String getUrgencyLevel() {
        return UrgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        UrgencyLevel = urgencyLevel;
    }

    public Integer getMalfunctionArea() {
        return MalfunctionArea;
    }

    public void setMalfunctionArea(Integer malfunctionArea) {
        MalfunctionArea = malfunctionArea;
    }

    public String getTimeReported() {
        return TimeReported;
    }

    public void setTimeReported(String timeReported) {
        TimeReported = timeReported;
    }

    public String getExtraInformation() {
        return ExtraInformation;
    }

    public void setExtraInformation(String extraInformation) {
        ExtraInformation = extraInformation;
    }

    public String getReportPhoto() {
        return ReportPhoto;
    }

    public void setReportPhoto(String reportPhoto) {
        ReportPhoto = reportPhoto;
    }

    public String getMalfunctionFixer() {
        return MalfunctionFixer;
    }

    public void setMalfunctionFixer(String malfunctionFixer) {
        MalfunctionFixer = malfunctionFixer;
    }

    public String getTimeFixed() {
        return TimeFixed;
    }

    public void setTimeFixed(String timeFixed) {
        TimeFixed = timeFixed;
    }

    public String getFixedPhoto() {
        return FixedPhoto;
    }

    public void setFixedPhoto(String fixedPhoto) {
        FixedPhoto = fixedPhoto;
    }

    public Integer getMalfunctionRoom() {
        return MalfunctionRoom;
    }

    public void setMalfunctionRoom(Integer malfunctionRoom) {
        MalfunctionRoom = malfunctionRoom;
    }
}
/*
מדווח התקלה - מסוג String
נושא התקלה - מסוג String
דחיפות התקלה (הטכנאי מחליט) - מסוג String
מתחם התקלה - מסוג Integer
תאריך ושעת הדיווח -מסוג String
תיאור התקלה (אופציונלי עבור המדווח) - מסוג String
תמונה של התקלה (אופציונלי עבור המדווח) - מסוג String
מי סגר את התקלה - מסוג String
תאריך סגירת תקלה - מסוג String
תמונת סגירת תקלה - מסוג String

 */
