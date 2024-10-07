package com.querifylabs.dbcourse.schema;

import com.querifylabs.dbcourse.TestBase;
import org.apache.calcite.sql.type.SqlTypeName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestTask1 extends TestBase {
    @Test
    void testTableSchema() {
        var unoptimized = optimizer.convert("SELECT * from public.taxirides").rel;

        assertThat(unoptimized.getInputs())
                .hasSize(1)
                .allSatisfy(
                        node -> {
                            assertThat(node.getTable())
                                    .isNotNull()
                                    .satisfies(
                                            table ->
                                                    assertThat(table.unwrap(ParquetTable.class))
                                                            .isNotNull()
                                                            .satisfies(
                                                                    parquetTable -> {
                                                                        assertThat(parquetTable.schema()).isEqualTo("public");
                                                                        assertThat(parquetTable.name()).isEqualTo("taxirides");
                                                                    }));

                            var rowType = node.getRowType();
                            Object[][] types = new Object[][] {
                                    {"VendorID", SqlTypeName.INTEGER, 10},
                                    {"tpep_pickup_datetime", SqlTypeName.TIMESTAMP, 6},
                                    {"tpep_dropoff_datetime", SqlTypeName.TIMESTAMP, 6},
                                    {"passenger_count", SqlTypeName.BIGINT, 19},
                                    {"trip_distance", SqlTypeName.DOUBLE, 15},
                                    {"RatecodeID", SqlTypeName.BIGINT, 19},
                                    {"store_and_fwd_flag", SqlTypeName.VARCHAR, -1},
                                    {"PULocationID", SqlTypeName.INTEGER, 10},
                                    {"DOLocationID", SqlTypeName.INTEGER, 10},
                                    {"payment_type", SqlTypeName.BIGINT, 19},
                                    {"fare_amount", SqlTypeName.DOUBLE, 15},
                                    {"extra", SqlTypeName.DOUBLE, 15},
                                    {"mta_tax", SqlTypeName.DOUBLE, 15},
                                    {"tip_amount", SqlTypeName.DOUBLE, 15},
                                    {"tolls_amount", SqlTypeName.DOUBLE, 15},
                                    {"improvement_surcharge", SqlTypeName.DOUBLE, 15},
                                    {"total_amount", SqlTypeName.DOUBLE, 15},
                                    {"congestion_surcharge", SqlTypeName.DOUBLE, 15},
                                    {"Airport_fee", SqlTypeName.DOUBLE, 15},
                            };

                            assertThat(rowType.getFieldCount()).isEqualTo(19);
                            for (int f = 0; f < rowType.getFieldCount(); f++) {
                                var field = rowType.getFieldList().get(f);

                                assertThat(field.getName()).isEqualTo(types[f][0]);
                                assertThat(field.getType().getSqlTypeName()).isEqualTo(types[f][1]);
                                assertThat(field.getType().getPrecision()).isEqualTo(types[f][2]);
                                assertThat(field.getType().isNullable()).isEqualTo(true);
                            }
                        });
    }
}
