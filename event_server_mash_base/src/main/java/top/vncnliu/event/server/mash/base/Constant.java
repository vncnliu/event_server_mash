package top.vncnliu.event.server.mash.base;

/**
 * User: liuyq
 * Date: 2018/7/25
 * Description:
 */
public class Constant {

    public enum ErrorCode {
        SUCCESS(0,"成功"),
        MASH_BAK_ERROR(100001,"回滚事件异常"),
        ORDER_ERROR(110001,"订单事件异常");
        private int code;
        private String msg;

        ErrorCode(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum TASK_STATUS {
        UN_SUBMIT(0,"未提交"),
        EXECUTED(1,"已执行");

        private int code;
        private String message;

        TASK_STATUS(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
