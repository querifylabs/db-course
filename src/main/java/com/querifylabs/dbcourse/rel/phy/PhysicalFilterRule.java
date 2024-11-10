package com.querifylabs.dbcourse.rel.phy;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.logical.LogicalFilter;

import java.util.List;

public class PhysicalFilterRule extends ConverterRule {
    public static final Config DEFAULT_CONFIG =
        Config.INSTANCE
            .withConversion(
                LogicalFilter.class,
                f -> true,
                Convention.NONE,
                PhysicalConvention.INSTANCE,
                PhysicalFilterRule.class.getSimpleName())
            .withRuleFactory(PhysicalFilterRule::new);

    protected PhysicalFilterRule(Config config) {
        super(config);
    }

    @Override
    public RelNode convert(RelNode rel) {
        var filter = (LogicalFilter) rel;
        return new PhysicalFilter(
            filter.getCluster(),
            filter.getTraitSet().replace(PhysicalConvention.INSTANCE),
            List.of(),
            // Task 5: Note that it is required to convert inputs to physical convention as well
            // when performing conversion. This means that conversion will be actually executed bottom-up.
            convert(
                filter.getInput(), filter.getInput().getTraitSet().replace(PhysicalConvention.INSTANCE)),
            filter.getCondition());
    }
}
