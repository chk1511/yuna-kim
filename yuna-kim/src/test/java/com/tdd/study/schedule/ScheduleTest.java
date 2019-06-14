package com.tdd.study.schedule;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jdk.nashorn.internal.ir.annotations.Ignore;

@SpringBootTest
@Ignore
public class ScheduleTest {

    @Test
    public void 스케줄러테스트() {
        Schedule schedule = new Schedule();
        schedule.start();
    }

}