package com.ledger.common.constant;

/**
 * @author ledger
 * @version 1.0
 **/

public class WareConstant {
    public enum PurchaseStatusEnum{
        CREATE(1,"新建"),ASSIGNED(0,"已分配"),
        RECEIVE(2,"已领取"),FINISH(3,"已完成"),
        HAS_ERROR(4,"有异常");

        PurchaseStatusEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        private int code;
        private String msg;

        public int getCode() {
            return code;
        }
    }
    public enum PurchaseDetailStatusEnum{
        CREATE(1,"新建"),ASSIGNED(0,"已分配"),
        BUYING(2,"正在采购"),FINISH(3,"已完成"),
        HAS_ERROR(4,"采购失败");

        PurchaseDetailStatusEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        private int code;
        private String msg;

        public int getCode() {
            return code;
        }
    }
}
