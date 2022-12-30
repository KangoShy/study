package com.flink.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.flink.service.PFBankInterFace;
import com.flink.util.HttpClient;
import com.flink.vo.PFBankUser;
import com.flink.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 浦发bank
 */
@RestController
@RequestMapping("bank")
public class PFBankController {

    @Autowired
    PFBankInterFace pfBankInterFace;

    @RequestMapping(value = "addBlance", method = RequestMethod.GET)
    public Map<String, String> addBlance(@RequestParam BigDecimal value) {

        HashMap<String, String> result = new HashMap<>();
        try{
            String userId = "2";
            PFBankUser pfBankUser = pfBankInterFace.selectOne(Wrappers.<PFBankUser>lambdaQuery().eq(PFBankUser::getUserId, userId));

            Optional.ofNullable(pfBankUser).orElseThrow(() -> new IllegalArgumentException("用户不存在！"));

            BigDecimal balance = pfBankUser.getBalance();

            pfBankUser.setBalance(balance.add(value));
            pfBankInterFace.updateById(pfBankUser);


            result.put("code", "200");
            result.put("success", "true");
        }catch (Exception e) {
            result.put("code", "500");
            result.put("success", "false");
        }
        return result;
    }

    /**
     * 浦发 -转账- 杭州Bank
     *
     * @param userId 用户id
     * @param value  转账金额
     * @return Result
     */
    @RequestMapping(value = "transferMoneyToHzBank", method = RequestMethod.POST)
    @Transactional
    public Result<?> transferMoneyToHzBank(@RequestParam String userId, @RequestParam BigDecimal value) {

        PFBankUser pfBankUser = pfBankInterFace.selectOne(Wrappers.<PFBankUser>lambdaQuery().eq(PFBankUser::getUserId, userId));

        Optional.ofNullable(pfBankUser).orElseThrow(() -> new IllegalArgumentException("用户不存在！"));

        BigDecimal balance = pfBankUser.getBalance();

        if (balance.compareTo(value) < 0) {
            throw new IllegalArgumentException("余额不足，当前余额" + balance);
        }

        BigDecimal multiply = balance.subtract(value);
        pfBankUser.setBalance(multiply);
        pfBankInterFace.updateById(pfBankUser);

        // 调用杭州银行转账接口
        HttpClient httpClient = new HttpClient();
        try{
            Map<String, Object> map = httpClient.fetchGetMap("http://127.0.0.1:8089/bank/addBlance?value=" + value);
            Object code = map.get("code");
            if ("200".equals(code)) {
                return Result.success("成功！");
            }
        }catch (Exception ignore) {

        }
        return Result.success("成功！");
    }

}
