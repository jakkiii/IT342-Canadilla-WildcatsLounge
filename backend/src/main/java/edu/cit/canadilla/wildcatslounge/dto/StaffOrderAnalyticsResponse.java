package edu.cit.canadilla.wildcatslounge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffOrderAnalyticsResponse {
    private List<TrendPoint> daily;
    private List<TrendPoint> weekly;
    private List<TrendPoint> monthly;
    private List<TopItemPoint> topItems;
    private List<StatusPoint> statusDistribution;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendPoint {
        private String label;
        private long orders;
        private BigDecimal revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopItemPoint {
        private String itemName;
        private long quantity;
        private long orderCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusPoint {
        private String status;
        private long count;
    }
}
