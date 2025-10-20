package com.atp.fwfe.model.work;

public enum WorkStatus {
    PENDING("Đang chờ"),
    COMPLETED("Đã hoàn thành"),
    CANCELLED("Đã hủy");

    private final String label;

    WorkStatus(String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
