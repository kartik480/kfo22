package com.kfinone.app;

public class DocumentUploadItem {
    private int id;
    private String documentName;
    private String uploadedFile;

    public DocumentUploadItem(int id, String documentName, String uploadedFile) {
        this.id = id;
        this.documentName = documentName;
        this.uploadedFile = uploadedFile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(String uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
} 
