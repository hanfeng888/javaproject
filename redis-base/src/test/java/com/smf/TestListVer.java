package com.smf;

import com.smf.redisbase.redismq.ListVer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * TestListVer TODO
 *
 * @author hf
 * @date 2024/1/6 20:57:01
 */
@SpringBootTest
public class TestListVer {

    @Resource
    private ListVer listVer;

    @Test
    public void testGet() {
        List<String> result = listVer.get("listmq");
        for (String message : result) {
            System.out.println(message);
        }
    }

    @Test
    public void testPut() {
        listVer.put("listmq", "msgtssest");
    }
}
