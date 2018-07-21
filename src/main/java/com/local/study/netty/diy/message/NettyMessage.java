package com.local.study.netty.diy.message;

import java.io.Serializable;

/**
 * 自定义协议栈数据结构
 */
public class NettyMessage implements Serializable{

    private static final  long  serialVersionUID = 42L;
    private Header header;

    private Object body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NettyMessage{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
