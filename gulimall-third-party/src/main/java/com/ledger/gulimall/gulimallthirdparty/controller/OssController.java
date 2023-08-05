package com.ledger.gulimall.gulimallthirdparty.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.ledger.common.utils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ledger
 * @version 1.0
 **/
@RestController
@RequestMapping("/thirdparty")
public class OssController {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucket_name}")
    private String bucket;

    @Value("${aliyun.oss.access_key_id}")
    private String accessId;

    @Value("${aliyun.oss.access_key_secret}")
    private String accessKey;

    @Value("${aliyun.oss.dir}")
    private String dir;

    @GetMapping("/oss/policy")
    public R oss() {
        // 创建ossClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessId", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", "https://"+bucket+"."+endpoint);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            return R.ok().put("data", respMap);
        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
