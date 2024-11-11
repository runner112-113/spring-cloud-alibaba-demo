package com.runner.remoting;

/**
 * send message:
 * encode (biz thread) --> queue --> channel flush (I/O thread)
 *
 * receive message:
 * channel read (I/O thread) --> submit biz executor --> decode (biz thread)
 *
 *
 * dubbo protocol struct:
 *
 * bit
 * 0~15       Magic Number       魔数，固定0xdabb
 * 16           Req/Res            0=Response, 1=Request
 * 17           Two Way?           仅在Request在有用。是否期望服务器返回数据
 * 18           Event              是否事件信息，如心跳
 * 19~23        Serialization ID   序列化类型ID
 * 24~31        Status             响应状态
 * 32~95        Request ID         请求唯一标识
 * 96~127       Data Length        Body的长度
 * 变长部分      Body               对象序列化的byte
 *
 *
 * 变长部分包含：
 * 1.协议Version
 * 2.ServiceName
 * 3.ServiceVersion
 * 4.MethodName
 * 5.ParameterTypesDesc
 * 6.Arguments序列化
 * 7.Attachments序列化
 *
 */