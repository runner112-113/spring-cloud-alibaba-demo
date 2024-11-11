package com.runner.datasource.rocksdb;

import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Rocksdb helper
 *
 * implement distributed database base rocksdb + jraft
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/11 15:12
 */

public class RocksDbHelper {

    static Logger logger = LoggerFactory.getLogger(RocksDbHelper.class);

    private static final long DEFAULT_WRITE_BUFFER_MB = 32;


    public static Options createOptions(String dir) {
        DBOptions dbOptions = new DBOptions();
        // 设置 RocksDB 后台任务的最大数量，通常包括后台的 Compaction（压缩）和 Flush（写入持久化）任务
        dbOptions.setMaxBackgroundJobs(Runtime.getRuntime().availableProcessors());
        Options options = new Options(dbOptions, createColumnFamilyOptions(dir));
        // 设置数据库在路径不存在时自动创建。在没有数据库文件的情况下会创建一个新的数据库
        options.setCreateIfMissing(true);
        return options;
    }

    public static ColumnFamilyOptions createColumnFamilyOptions(String dir) {
        ColumnFamilyOptions columnFamilyOptions = new ColumnFamilyOptions();
        // 定义一个 BlockBasedTableConfig 对象，用于配置 RocksDB 的表格式。
        // BlockBasedTableConfig 是 RocksDB 最常用的数据表格式，按数据块存储，可以有效地进行数据压缩和读取。
        BlockBasedTableConfig tableFormatConfig = new BlockBasedTableConfig();
        columnFamilyOptions.setTableFormatConfig(tableFormatConfig);
        //set more write buffer size to formal config-data, reduce flush to sst file frequency.
        // 设置写缓冲区（Memtable）的大小
        columnFamilyOptions.setWriteBufferSize(getSuitFormalCacheSizeMB(dir) * 1024 * 1024);
        //once a stt file is flushed, compact it immediately to avoid too many sst file which will result in read latency.
        // 设置触发 Compaction（压缩）操作的 Level-0 文件数量阈值。当 Level-0 中的文件数达到该阈值（1）时，立即触发 Compaction 操作。
        // 这样可以减少 SST 文件数量，防止读操作遇到过多的 SST 文件，降低读延迟。
        columnFamilyOptions.setLevel0FileNumCompactionTrigger(1);
        return columnFamilyOptions;
    }

    private static long getSuitFormalCacheSizeMB(String dir) {

        boolean formal = false;
        long maxHeapSizeMB = Runtime.getRuntime().maxMemory() / 1024 / 1024;

        if (formal) {
            long formalWriteBufferSizeMB = 0;

            if (maxHeapSizeMB < 8 * 1024) {
                formalWriteBufferSizeMB = 32;
            } else if (maxHeapSizeMB < 16 * 1024) {
                formalWriteBufferSizeMB = 64;
            } else {
                formalWriteBufferSizeMB = 256;
            }
            logger.info("init formal rocksdb write buffer size {}M for dir {}, maxHeapSize={}M",
                    formalWriteBufferSizeMB, dir, maxHeapSizeMB);
            return formalWriteBufferSizeMB;
        } else {
            logger.info("init default rocksdb write buffer size {}M for dir {}, maxHeapSize={}M",
                    DEFAULT_WRITE_BUFFER_MB, dir, maxHeapSizeMB);
            return DEFAULT_WRITE_BUFFER_MB;
        }

    }

    public static void main(String[] args) {
        String homePath = "D://";
        String dir = "rocksdb";
        createDirIfEmpty(homePath + dir);
        try (RocksDB rocksDB = RocksDB.open(createOptions(dir), homePath + dir)) {
            rocksDB.put("key".getBytes(StandardCharsets.UTF_8), "value123".getBytes(StandardCharsets.UTF_8));
            System.out.println(new String(rocksDB.get("key".getBytes(StandardCharsets.UTF_8))));
        }catch (RocksDBException e) {
            logger.error("rocks db error", e);
        }
    }



    private static void createDirIfEmpty(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
