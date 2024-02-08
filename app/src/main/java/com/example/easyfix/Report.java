package com.example.easyfix;

public class Report {
    String reporter;
    String ReportMainType;
    String UrgencyLevel;
    Integer MalfunctionArea;
    String TimeReported;
    String ExtraInformation;
    String ReportPhoto;
    String MalfunctionFixer;
    String TimeFixed;
    String FixedPhoto;

    public Report() {
        // בנאי ריק.
    }


    public Report(String reporter, String reportMainType, Integer malfunctionArea, String timeReported, String extraInformation) {
        // בנאי בסיסי, עבור דיווח ללא פרטים נוספים. אם אין תיאור, יהיה ריק ExtraInformation

        this.reporter = reporter;
        ReportMainType = reportMainType;
        MalfunctionArea = malfunctionArea;
        TimeReported = timeReported;
        ExtraInformation = extraInformation;
    }

    public Report(String reporter, String reportMainType, Integer malfunctionArea, String timeReported, String extraInformation, String reportPhoto) {
        // בנאי מלא, עבור מדווח, בתוספת תמונה של התקלה, אם אין תיאור, יהיה ריק ExtraInformation.
        this.reporter = reporter;
        ReportMainType = reportMainType;
        MalfunctionArea = malfunctionArea;
        TimeReported = timeReported;
        ExtraInformation = extraInformation;
        ReportPhoto = reportPhoto;
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
