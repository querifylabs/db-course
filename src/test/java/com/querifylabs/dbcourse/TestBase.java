package com.querifylabs.dbcourse;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestBase {
    public static final String DATA_ROOT = "data";
    public static final String[] FILES = {
            "yellow_tripdata_2024-01.parquet",
            "yellow_tripdata_2024-02.parquet",
            "yellow_tripdata_2024-03.parquet",
    };
    public static final String DOWNLOAD_TEMPLATE = "https://d37ci6vzurychx.cloudfront.net/trip-data/";

    protected CourseOptimizer optimizer;

    @BeforeEach
    public void setUp() throws Exception {
        var tablePath = Path.of(DATA_ROOT, "public", "taxirides");

        if (!Files.exists(tablePath)) {
            var tableDir = tablePath.toFile();
            if (!tableDir.mkdirs()) {
                throw new RuntimeException("Table data directory does not exist and test failed to create it (make sure the project folder has sufficient rights): " + tableDir.getAbsolutePath());
            }
        }

        try (var stream = Files.list(tablePath)) {
            stream
                    .filter(f -> f.getFileName().toString().endsWith(".parquet.tmp"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete previously created temp file (make sure the project folder has sufficient rights): " + path.toFile().getAbsolutePath());
                        }
                    });
        }

        for (var file : FILES) {
            var filePath = tablePath.resolve(file);
            if (!filePath.toFile().exists()) {
                var tmpPath = tablePath.resolve(file + ".tmp");

                var downloadUrl = DOWNLOAD_TEMPLATE + file;
                System.out.println("Downloading '" + downloadUrl + "'");
                download(downloadUrl, tmpPath, filePath);
            }
        }

        optimizer = new CourseOptimizer(DATA_ROOT, enableScanPushdown());
    }

    protected boolean enableScanPushdown() {
        return false;
    }

    private static void download(String downloadUrl, Path tmpPath, Path dstPath) {
        var tmpFile = tmpPath.toFile();
        try (var in = new BufferedInputStream(new URL(downloadUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(tmpFile)) {
            byte[] dataBuffer = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to download '" + downloadUrl + "': " + e.getMessage(), e);
        }

        if (!tmpFile.renameTo(dstPath.toFile())) {
            throw new RuntimeException("Failed to rename '" + tmpFile.getAbsolutePath() +
                    "' to '" + dstPath.toFile().getAbsolutePath() + "'");
        }
    }

    protected void validatePlan(RelNode root, String expectedPlan) {
        Assertions.assertThat(RelOptUtil.toString(root).replace("\r", "")).isEqualTo(expectedPlan.replace("\r", ""));
    }
}
