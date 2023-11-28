package com.ute.studentconsulting.util;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SortUtils {
    private Sort.Direction sortDirection(String direction) {
        if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    public List<Sort.Order> sortOrders(String[] sort) {
        if (!sort[0].contains(",")) {
            var direction = sortDirection(sort[1]);
            var order = new Sort.Order(direction, sort[0]);
            return List.of(order);
        }

        var orders = new ArrayList<Sort.Order>();
        for (var tempSort : sort) {
            var current = tempSort.split(",");
            var direction = sortDirection(current[1]);
            var order = new Sort.Order(direction, current[0]);
            orders.add(order);
        }
        return orders;
    }

}
