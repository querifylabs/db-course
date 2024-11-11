package com.querifylabs.dbcourse;

import org.junit.jupiter.api.Test;

public class TestTask6 extends TestBase {
    @Override
    protected boolean enableScanPushdown() {
        return true;
    }

    @Test
    public void testProjectPushdown() {
        var converted = optimizer.convert(
                "select total_amount, passenger_count from public.taxirides");

        var logicalOptimized = optimizer.optimize(converted);

        var physicalOptimized = optimizer.optimizePhysical(logicalOptimized);

        String expectedPlan = """
                PhysicalTableScan(table=[[public, taxirides]], projected=[[16, 3]])
                """;

        validatePlan(physicalOptimized.rel, expectedPlan);
    }

    @Test
    public void testFilterPushdown() {
        var converted = optimizer.convert(
                "select total_amount, passenger_count from public.taxirides where tip_amount > 1");

        var logicalOptimized = optimizer.optimize(converted);

        var physicalOptimized = optimizer.optimizePhysical(logicalOptimized);

        String expectedPlan = """
                PhysicalProject(total_amount=[$2], passenger_count=[$0])
                  PhysicalFilter(condition=[>($1, 1)])
                    PhysicalTableScan(table=[[public, taxirides]], projected=[[3, 13, 16]], filter=[[>($13, 1)]])
                """;

        validatePlan(physicalOptimized.rel, expectedPlan);
    }

    @Test
    public void testUniqueColumns() {
        var converted = optimizer.convert(
                "select total_amount, total_amount from public.taxirides");

        var logicalOptimized = optimizer.optimize(converted);

        var physicalOptimized = optimizer.optimizePhysical(logicalOptimized);

        String expectedPlan = """
                PhysicalProject(total_amount=[$0], total_amount0=[$0])
                  PhysicalTableScan(table=[[public, taxirides]], projected=[[16]])
                """;

        validatePlan(physicalOptimized.rel, expectedPlan);
    }
}
