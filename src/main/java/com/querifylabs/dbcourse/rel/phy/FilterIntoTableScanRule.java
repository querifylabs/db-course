package com.querifylabs.dbcourse.rel.phy;

import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelRule;
import org.immutables.value.Value;

public class FilterIntoTableScanRule
        extends RelRule<FilterIntoTableScanRule.FilterIntoTableScanRuleConfig> {
    protected FilterIntoTableScanRule(FilterIntoTableScanRuleConfig config) {
        super(config);
    }

    @Override
    public void onMatch(RelOptRuleCall call) {
        var filter = (PhysicalFilter)call.rel(0);
        var scan = (PhysicalTableScan)call.rel(1);

        // TODO Task 6: Implement conversion.
        // Use call.transformTo() to convert the given project-scan pair to a new rel subtree.
    }

    @Value.Immutable(singleton = false)
    public interface FilterIntoTableScanRuleConfig extends RelRule.Config {
        FilterIntoTableScanRuleConfig DEFAULT =
                ImmutableFilterIntoTableScanRuleConfig.builder()
                        .operandSupplier(
                                b0 ->
                                        b0.operand(PhysicalFilter.class).oneInput(b1 -> b1.operand(PhysicalTableScan.class).noInputs())
                        ).build();

        @Override
        default FilterIntoTableScanRule toRule() {
            return new FilterIntoTableScanRule(this);
        }
    }
}
