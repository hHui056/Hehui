package com.beidouapp.et.common.enums;

/**
 * 文件传输类型枚举.<br />
 * 主动传输和主动索取.
 * 
 * @author mhuang.
 */
public enum FileTransferTypeEnum
{
    /**
     * 请求对方发送文件.
     */
    PULL ("pull", "请求对方传递文件"),

    /**
     * 主动发送文件到指定客户端.
     */
    PUSH ("push", "主动发送文件到指定客户端"),

    /**
     * 异常信息.
     */
    EXCEPTION ("exception", "异常信息");

    /** 字典类型代码. */
    private String code;

    /** 字典类型名称. */
    private String name;

    /**
     * 构造一个类型.
     * 
     * @param code 代码
     * @param name 名称
     */
    private FileTransferTypeEnum (String code, String name)
    {
        this.code = code;
        this.name = name;
    }

    /**
     * 获得类型代码.
     * 
     * @return 类型代码
     */
    public String getCode ()
    {
        return code;
    }

    /**
     * 设置类型代码.
     * 
     * @param code 类型代码
     */
    public void setCode (String code)
    {
        this.code = code;
    }

    /**
     * 获得类型名称.
     * 
     * @return 类型名称
     */
    public String getName ()
    {
        return name;
    }

    /**
     * 设置类型名称.
     * 
     * @param name 类型名称
     */
    public void setName (String name)
    {
        this.name = name;
    }

    /**
     * 通过编码查询对应枚举.
     * 
     * @param code
     * @return
     */
    public static FileTransferTypeEnum getTopicTypeEnumByCode (String code)
    {
        if (code == null || code.isEmpty ())
        {
            throw new RuntimeException ("编码不能为空!");
        }
        for (FileTransferTypeEnum e : FileTransferTypeEnum.values ())
        {
            if (e.getCode ().equalsIgnoreCase (code))
            {
                return e;
            }
        }
        throw new RuntimeException ("没有找到编码=【" + code + "】的枚举类型.");
    }
}