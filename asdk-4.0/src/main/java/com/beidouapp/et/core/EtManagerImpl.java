package com.beidouapp.et.core;

import com.beidouapp.et.client.EtManager;
import com.beidouapp.et.client.api.IContext;
import com.beidouapp.et.client.api.IFile;
import com.beidouapp.et.client.api.IM;
import com.beidouapp.et.client.api.IWeb;
import com.beidouapp.et.client.callback.ICallback;
import com.beidouapp.et.client.callback.IFileReceiveListener;
import com.beidouapp.et.client.callback.IReceiveListener;
import com.beidouapp.et.client.callback.IUserStatusListener;
import com.beidouapp.et.core.impl.FileImpl;
import com.beidouapp.et.core.impl.IMImpl;
import com.beidouapp.et.core.impl.WebImpl;
import com.beidouapp.et.exception.EtExceptionCode;
import com.beidouapp.et.exception.EtRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模块管理器.
 *
 * @author mhuang.
 */
public class EtManagerImpl implements EtManager {
    public static final Logger logger = LoggerFactory.getLogger(EtManagerImpl.class);
    private IM im;
    private IWeb web;
    private IFile file;
    private IContext etContext;

    public EtManagerImpl(IContext etContext) {
        this.etContext = etContext;
        this.im = new IMImpl(etContext);
    }


    @Override
    public void setConnectCallback(ICallback<Void> cb) {
        im.setConnectCallBack(cb);
    }

    @Override
    public void setListener(IReceiveListener listener) {
        im.setReceiveListener(listener);
    }

    @Override
    public void setFileListener(IFileReceiveListener fileListener) {
        im.setFileReceiveListener(fileListener);
    }

    @Override
    public void setUserStatusListener(IUserStatusListener userStatusListener) {
        im.setUserStatusListener(userStatusListener);
    }

    @Override
    public IM getIm() {
        return im;
    }

    @Override
    public IWeb getWeb() {
        if (im.isConnected()) {
            web = new WebImpl(this.etContext);
            return web;
        }
        logger.error("when using a web service, please first connect im server.");
        throw new EtRuntimeException(EtExceptionCode.IM_OFFLINE,
                "when using a web service, please first connect im server");
    }

    @Override
    public void connect() {
        logger.debug("im connecting...");
        im.connect();
    }

    @Override
    public void destroy() {
        logger.debug("im destroy." + Thread.currentThread().getName());
        im.destroy();
    }

    @Override
    public IFile getFile() {
        if (im.isConnected()) {
            file = new FileImpl(this.im);
            return file;
        }
        logger.error("when using a file service, please first connect im server.");
        throw new EtRuntimeException(EtExceptionCode.IM_OFFLINE,
                "when using a file service, please first connect im server");
    }

    @Override
    public String getSdkVersion() {
        return "1.7.0";
    }

}