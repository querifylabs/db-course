package com.querifylabs.dbcourse;

import com.querifylabs.dbcourse.executor.ExecutionContext;
import com.querifylabs.dbcourse.executor.PlanImplementor;
import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.Float8Vector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TestTask10 extends TestBase {
    @Override
    protected boolean enableScanPushdown() {
        return true;
    }

    @Test
    public void testGlobalAggregate() throws Exception {
        var sql = "select sum(passenger_count) from public.taxirides where tpep_pickup_datetime between '2024-01-01 00:00:00' and '2024-01-02 00:00:00'";
        var plan = optimizer.optimizePhysical(optimizer.optimize(optimizer.convert(sql)));

        try (var ctx = new ExecutionContext()) {
            try (var node = new PlanImplementor().implementPlan(ctx, plan.rel)) {
                List<Long> results = new ArrayList<>();
                while (true) {
                    try (var page = node.getNextPage()) {
                        if (page == null) {
                            break;
                        }

                        assertThat(page.getColumns().size()).isEqualTo(1);
                        assertThat(page.getColumns().getFirst()).isExactlyInstanceOf(BigIntVector.class);

                        var vector = (BigIntVector)page.getColumns().getFirst();
                        for (int r = 0; r < vector.getValueCount(); r++) {
                            if (!vector.isNull(r)) {
                                results.add(vector.get(r));
                            }
                        }
                    }
                }
                assertThat(results).isEqualTo(List.of(109343L));
            }
        }
    }

    @Test
    public void testRegularAggregate() throws Exception {
        var sql = "select passenger_count, sum(trip_distance) from public.taxirides where tpep_pickup_datetime between '2024-01-01 00:00:00' and '2024-01-02 00:00:00' group by passenger_count";
        var plan = optimizer.optimizePhysical(optimizer.optimize(optimizer.convert(sql)));

        try (var ctx = new ExecutionContext()) {
            try (var node = new PlanImplementor().implementPlan(ctx, plan.rel)) {
                var results = new HashMap<Long, Double>();
                while (true) {
                    try (var page = node.getNextPage()) {
                        if (page == null) {
                            break;
                        }

                        assertThat(page.getColumns().size()).isEqualTo(2);
                        assertThat(page.getColumns().get(0)).isExactlyInstanceOf(BigIntVector.class);
                        assertThat(page.getColumns().get(1)).isExactlyInstanceOf(Float8Vector.class);

                        var vec0 = (BigIntVector)page.getColumns().get(0);
                        var vec1 = (Float8Vector)page.getColumns().get(1);
                        for (int r = 0; r < vec0.getValueCount(); r++) {
                            var cnt = !vec0.isNull(r) ? vec0.get(r) : null;
                            var sum = !vec1.isNull(r) ? vec1.get(r) : null;

                            results.put(cnt, sum);
                        }
                    }
                }
                assertThat(results).isEqualTo(calculateRawAggregate());
            }
        }
    }

    private Map<Long, Double> calculateRawAggregate() throws Exception {
        var sql = "select passenger_count, trip_distance from public.taxirides where tpep_pickup_datetime between '2024-01-01 00:00:00' and '2024-01-02 00:00:00'";

        var plan = optimizer.optimizePhysical(optimizer.optimize(optimizer.convert(sql)));
        var result = new HashMap<Long, Double>();

        try (var ctx = new ExecutionContext()) {
            try (var node = new PlanImplementor().implementPlan(ctx, plan.rel)) {
                while (true) {
                    try (var page = node.getNextPage()) {
                        if (page == null) {
                            break;
                        }

                        assertThat(page.getColumns().size()).isEqualTo(2);
                        var vec0 = (BigIntVector)page.getColumns().get(0);
                        var vec1 = (Float8Vector)page.getColumns().get(1);
                        for (int r = 0; r < vec0.getValueCount(); r++) {
                            var cnt = !vec0.isNull(r) ? vec0.get(r) : null;
                            var dist = !vec1.isNull(r) ? vec1.get(r) : null;

                            result.compute(cnt, (k, v) -> {
                                if (v == null) {
                                    return dist;
                                }
                                return v + (dist == null ? 0.d : dist);
                            });
                        }
                    }
                }
            }
        }

        return result;
    }
}
