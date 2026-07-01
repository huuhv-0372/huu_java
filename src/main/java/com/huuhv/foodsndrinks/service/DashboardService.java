package com.huuhv.foodsndrinks.service;

import com.huuhv.foodsndrinks.dto.response.DashboardResDto;
import com.huuhv.foodsndrinks.enums.OrderStatus;
import com.huuhv.foodsndrinks.repository.OrderDetailRepository;
import com.huuhv.foodsndrinks.repository.OrderRepository;
import com.huuhv.foodsndrinks.repository.ProductRepository;
import com.huuhv.foodsndrinks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository       orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository     productRepository;
    private final UserRepository        userRepository;

    @Transactional(readOnly = true)
    public DashboardResDto getDashboardData() {
        return DashboardResDto.builder()
                .totalRevenue(orderRepository.sumCompletedRevenue())
                .totalCompletedOrders(orderRepository.countByStatus(OrderStatus.COMPLETED))
                .activeOrders(orderRepository.countActiveOrders())
                .totalProducts(productRepository.countByIsAvailableTrue())
                .totalUsers(userRepository.countByIsActiveTrue())
                .monthlyRevenue(buildMonthlyRevenue())
                .statusCounts(buildStatusCounts())
                .recentOrders(buildRecentOrders())
                .topProducts(buildTopProducts())
                .build();
    }

    // -------------------------------------------------------
    // Builders
    // -------------------------------------------------------

    /** Last 6 months revenue — fills missing months with 0 */
    private List<DashboardResDto.MonthlyRevenueDto> buildMonthlyRevenue() {
        LocalDateTime from = LocalDateTime.now().minusMonths(5).withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        Map<String, Object[]> dataMap = orderRepository.findMonthlyRevenue(from)
                .stream()
                .collect(Collectors.toMap(
                        row -> row[0] + "-" + row[1],  // "2026-1"
                        row -> row
                ));

        List<DashboardResDto.MonthlyRevenueDto> result = new ArrayList<>();
        LocalDateTime cursor = from;
        for (int i = 0; i < 6; i++) {
            int year  = cursor.getYear();
            int month = cursor.getMonthValue();
            String key = year + "-" + month;
            String label = "Th" + month + "/" + year;

            Object[] row = dataMap.get(key);
            result.add(DashboardResDto.MonthlyRevenueDto.builder()
                    .label(label)
                    .revenue(row != null ? (BigDecimal) row[2] : BigDecimal.ZERO)
                    .orderCount(row != null ? ((Number) row[3]).longValue() : 0L)
                    .build());

            cursor = cursor.plusMonths(1);
        }
        return result;
    }

    /** Order status counts excluding CART */
    private Map<String, Long> buildStatusCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        // initialize all statuses to 0
        for (OrderStatus s : OrderStatus.values()) {
            if (s != OrderStatus.CART) counts.put(s.getLabel(), 0L);
        }
        orderRepository.countGroupByStatus().forEach(row -> {
            OrderStatus s = (OrderStatus) row[0];
            counts.put(s.getLabel(), ((Number) row[1]).longValue());
        });
        return counts;
    }

    private List<DashboardResDto.RecentOrderDto> buildRecentOrders() {
        return orderRepository.findRecent(PageRequest.of(0, 8))
                .stream()
                .map(DashboardResDto.RecentOrderDto::new)
                .collect(Collectors.toList());
    }

    private List<DashboardResDto.TopProductDto> buildTopProducts() {
        return orderDetailRepository.findTopSellingProducts(PageRequest.of(0, 5))
                .stream()
                .map(row -> DashboardResDto.TopProductDto.builder()
                        .id((Long) row[0])
                        .name((String) row[1])
                        .totalQuantity(((Number) row[2]).longValue())
                        .build())
                .collect(Collectors.toList());
    }
}




