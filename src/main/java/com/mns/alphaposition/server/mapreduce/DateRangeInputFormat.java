package com.mns.alphaposition.server.mapreduce;

import org.apache.hadoop.mapreduce.*;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.List;

public class DateRangeInputFormat extends InputFormat<LocalDate, LocalDate> {

    @Override
    public List<InputSplit> getSplits(JobContext jobContext) throws IOException, InterruptedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RecordReader<LocalDate, LocalDate> createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
