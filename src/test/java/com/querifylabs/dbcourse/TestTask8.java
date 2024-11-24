package com.querifylabs.dbcourse;

import com.querifylabs.dbcourse.executor.ExecutionContext;
import com.querifylabs.dbcourse.executor.PlanImplementor;
import org.apache.arrow.vector.TimeStampMicroVector;
import org.apache.arrow.vector.holders.NullableTimeStampMicroHolder;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class TestTask8 extends TestBase {
    // Verify that manually filtered full scan expects the same results.
    @Test
    public void testFullScan() throws Exception {
        var sql = "select tpep_pickup_datetime from public.taxirides";

        long fromMicros = Instant.parse("2024-01-01T00:00:00.000Z").toEpochMilli() * 1000;
        long toMicros = Instant.parse("2024-01-02T00:00:00.000Z").toEpochMilli() * 1000;

        var plan = optimizer.optimizePhysical(optimizer.optimize(optimizer.convert(sql)));
        try (var ctx = new ExecutionContext()) {
            try (var node = new PlanImplementor().implementPlan(ctx, plan.rel)) {
                var rowCount = 0L;
                while (true) {
                    try (var page = node.getNextPage()) {
                        if (page == null) {
                            break;
                        }

                        assertThat(page.getColumns().size()).isEqualTo(1);
                        assertThat(page.getColumns().getFirst()).isExactlyInstanceOf(TimeStampMicroVector.class);

                        var vector = (TimeStampMicroVector)page.getColumns().getFirst();
                        assertThat(vector.getNullCount()).isEqualTo(0);
                        NullableTimeStampMicroHolder holder = new NullableTimeStampMicroHolder();
                        for (int r = 0; r < vector.getValueCount(); r++) {
                            vector.get(r, holder);
                            if (holder.value >= fromMicros && holder.value <= toMicros) {
                                rowCount++;
                            }
                        }
                    }
                }
                assertThat(rowCount).isEqualTo(81014L);
            }
        }
    }

    @Test
    public void testFilteredScan() throws Exception {
        var sql = "select tpep_pickup_datetime from public.taxirides where tpep_pickup_datetime between '2024-01-01 00:00:00' and '2024-01-02 00:00:00'";

        var plan = optimizer.optimizePhysical(optimizer.optimize(optimizer.convert(sql)));
        try (var ctx = new ExecutionContext()) {
            try (var node = new PlanImplementor().implementPlan(ctx, plan.rel)) {
                var rowCount = 0L;
                while (true) {
                    try (var page = node.getNextPage()) {
                        if (page == null) {
                            break;
                        }

                        assertThat(page.getColumns().size()).isEqualTo(1);
                        assertThat(page.getColumns().getFirst()).isExactlyInstanceOf(TimeStampMicroVector.class);

                        rowCount += page.getRowCount();
                        var vector = (TimeStampMicroVector)page.getColumns().getFirst();
                        assertThat(vector.getNullCount()).isEqualTo(0);
                    }
                }
                assertThat(rowCount).isEqualTo(81014L);
            }
        }
    }

    @Override
    protected boolean enableScanPushdown() {
        return true;
    }
}
