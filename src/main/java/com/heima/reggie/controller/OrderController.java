package com.heima.reggie.controller;

import com.heima.reggie.common.R;
import com.heima.reggie.entity.Orders;
import com.heima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: cckong
 * @Date:
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submid(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }
}
