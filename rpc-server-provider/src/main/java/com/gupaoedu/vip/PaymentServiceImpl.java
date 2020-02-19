package com.gupaoedu.vip;

@RpcService(value = IPaymentService.class, version = "v2.0")
public class PaymentServiceImpl implements IPaymentService {
    @Override
    public void doPay() {
        System.out.println("执行doPay方法");
    }
}
