package com.querifylabs.dbcourse;

import com.querifylabs.dbcourse.executor.ExecutionContext;
import com.querifylabs.dbcourse.executor.PlanImplementor;
import org.apache.arrow.vector.BigIntVector;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestTask7 extends TestBase {
    @Test
    public void testProjectedScan() throws Exception {
        var sql = "select passenger_count from public.taxirides";

        var plan = optimizer.optimizePhysical(optimizer.optimize(optimizer.convert(sql)));
        try (var ctx = new ExecutionContext()) {
            try (var node = new PlanImplementor().implementPlan(ctx, plan.rel)) {
                var passCnt = new HashSet<Long>();
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
                                passCnt.add(vector.get(r));
                            }
                        }
                    }
                }
                assertThat(passCnt).isEqualTo(new HashSet<>(List.of(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)));
            }
        }
    }

    @Test
    public void testAllColumnsScan() throws Exception {
        var sql = "select * from public.taxirides";

        var plan = optimizer.optimizePhysical(optimizer.optimize(optimizer.convert(sql)));
        try (var ctx = new ExecutionContext()) {
            try (var node = new PlanImplementor().implementPlan(ctx, plan.rel)) {
                try (var page = node.getNextPage()) {
                    assertThat(page).isNotNull();

                    assertThat(page.getColumns().size()).isEqualTo(19);
                }
            }
        }
    }

    @Override
    protected boolean enableScanPushdown() {
        return true;
    }
}
