package com.ledger.gulimall.member;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
@Slf4j
class GulimallMemberApplicationTests {

    @Test
    void contextLoads() {
        int[] nums = {1, 2, 3};
        System.out.println(maxSumDivThree(nums));
    }

    @Test
    public int maxSumDivThree(int[] nums) {
        Arrays.sort(nums);
        System.out.println(nums);
        return 1;
    }

    public int getArrSum(int[] nums) {
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
        }
        return sum;
    }
}


