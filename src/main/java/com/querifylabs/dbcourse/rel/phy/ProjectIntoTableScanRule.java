package com.querifylabs.dbcourse.rel.phy;

import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelRule;
import org.immutables.value.Value;

public class ProjectIntoTableScanRule
        extends RelRule<ProjectIntoTableScanRule.ProjectIntoTableScanRuleConfig> {
    protected ProjectIntoTableScanRule(ProjectIntoTableScanRuleConfig config) {
        super(config);
    }

    @Override
    public void onMatch(RelOptRuleCall call) {
        var project = (PhysicalProject)call.rel(0);
        var scan = (PhysicalTableScan)call.rel(1);

        // TODO Task 6: Implement conversion.
        // Use call.transformTo() to convert the given project-scan pair to a new rel subtree.
    }

    @Value.Immutable(singleton = false)
    public interface ProjectIntoTableScanRuleConfig extends RelRule.Config {
        ProjectIntoTableScanRuleConfig DEFAULT =
                ImmutableProjectIntoTableScanRuleConfig.builder()
                        .operandSupplier(
                                b0 ->
                                        b0.operand(PhysicalProject.class).oneInput(b1 -> b1.operand(PhysicalTableScan.class).noInputs())
                        ).build();

        @Override
        default ProjectIntoTableScanRule toRule() {
            return new ProjectIntoTableScanRule(this);
        }
    }
}
