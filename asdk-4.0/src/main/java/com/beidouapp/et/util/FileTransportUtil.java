package com.beidouapp.et.util;

import com.beidouapp.et.client.callback.FileCallBack;
import com.beidouapp.et.client.domain.DocumentInfo;
import com.beidouapp.et.common.constant.EtConstants;
import com.beidouapp.et.exception.EtExceptionCode;
import com.beidouapp.et.exception.EtRuntimeException;
import com.beidouapp.et.util.param.CheckingUtil;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * 文件模块处理. 工具类.
 *
 * @author mhuang.
 */
public class FileTransportUtil {
    public static final Logger logger = LoggerFactory.getLogger(FileTransportUtil.class);

    /**
     * 最小字节数处理单元大小.
     */
    public static final int MINIMUM_UNIT_BLOCK = 1024 * 256;

    static {
        ClientGlobal.g_connect_timeout = ClientGlobal.DEFAULT_CONNECT_TIMEOUT * 1000;
        ClientGlobal.g_network_timeout = ClientGlobal.DEFAULT_NETWORK_TIMEOUT * 1000;
        ClientGlobal.g_charset = "ISO8859-1";
        ClientGlobal.g_tracker_http_port = 8080;
        ClientGlobal.g_anti_steal_token = false;
        ClientGlobal.g_secret_key = "FastDFS1234567890";
    }

    /**
     * 带校验的删除文件.
     *
     * @param documentInfo 文件信息.
     * @param params       Map对象. 必填key包含appkey 和 uid.
     * @return 成功返回0.否则返回-1.
     */
    public static int removeFile(DocumentInfo documentInfo,
                                 Map<String, Object> params) {
        CheckingUtil.checkNull(documentInfo,
                "The file information can not be empty!");
        String fileId = documentInfo.getFileId();
        CheckingUtil.checkNullOrEmpty(fileId, "The fileId can not be empty!");
        CheckingUtil.checkNull(documentInfo.getIp(),
                "The file server IP can not be empty!");
        CheckingUtil.checkNull(documentInfo.getPort(),
                "The file server port can not be empty!");
        CheckingUtil.checkNull(params, "The params is not null!");
        CheckingUtil.checkNull(params.get(EtConstants.ENCRYPT_KEY),
                "Encrypt info is not null!");
        CheckingUtil.checkNull(params.get(EtConstants.UID),
                "The 'uid' key is not null!");
        CheckingUtil.checkNull(params.get(EtConstants.RANDOM_KEY),
                "The 'random number' key is not null!");

        TrackerGroup tg = new TrackerGroup(
                new InetSocketAddress[]{new InetSocketAddress(
                        documentInfo.getIp(), documentInfo.getPort())});
        // 建立连接
        TrackerClient tracker = new TrackerClient(tg);
        TrackerServer trackerServer = null;
        try {
            trackerServer = tracker.getConnection();
        } catch (Exception e) {
            logger.error("create a connection file server exception!", e);
            throw new RuntimeException(
                    "create a connection file server exception!", e);
        }
        if (trackerServer == null) {
            InetSocketAddress[] inetSocketAddresses = tracker.tracker_group.tracker_servers;
            throw new RuntimeException(
                    "Create a connection file server exception! please server address "
                            + Arrays.toString(inetSocketAddresses));
        }
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);

        int result;
        try {
            result = client.delete_file1_with_check(fileId, params);
            if (result != 0) {
                throw new EtRuntimeException(
                        EtExceptionCode.FILE_BASE + result,
                        "remove exception. Please check the fileId \"" + fileId
                                + "\" does exist on this server("
                                + documentInfo.getIp() + ":"
                                + documentInfo.getPort()
                                + "), or network problems");
            }
        } catch (Exception e) {
            logger.error("remove file【" + documentInfo + "】 is failed!", e);
            throw new EtRuntimeException(EtExceptionCode.FILE_REMOVE, e);
        } finally {
            try {
                trackerServer.close();
            } catch (IOException e) {
                try {
                    trackerServer.close();
                } catch (IOException e1) {
                    logger.error("Release File Server Resource Exception. {}",
                            e1.getLocalizedMessage(), e1);
                }
            }
        }
        return result;
    }

    /**
     * 通过校验的方式下载指定文件.
     *
     * @param documentInfo 文件信息.
     * @param saveFilePath 文件的保存路径.
     * @param params       Map对象. 必填key包含appkey 和 uid.
     * @return 成功返回0.否则返回-1.
     */
    public static int downloadFile(DocumentInfo documentInfo,
                                   String saveFilePath, Map<String, Object> params) {
        CheckingUtil.checkNull(documentInfo,
                "The file information can not be empty!");
        String fileId = documentInfo.getFileId();
        CheckingUtil.checkNullOrEmpty(fileId, "The fileId can not be empty!");
        CheckingUtil.checkNull(saveFilePath,
                "Save the file location is not specified!");
        CheckingUtil.checkNull(documentInfo.getIp(),
                "File Server IP can not be empty!");
        CheckingUtil.checkNull(documentInfo.getPort(),
                "File server port can not be empty!");
        CheckingUtil.checkNull(params, "The params is not null!");
        CheckingUtil.checkNull(params.get(EtConstants.ENCRYPT_KEY),
                "Encrypt info is not null!");
        CheckingUtil.checkNull(params.get(EtConstants.UID),
                "The 'uid' key is not null!");
        CheckingUtil.checkNull(params.get(EtConstants.RANDOM_KEY),
                "The 'random number' key is not null!");

        TrackerGroup tg = new TrackerGroup(
                new InetSocketAddress[]{new InetSocketAddress(
                        documentInfo.getIp(), documentInfo.getPort())});
        // 建立连接
        TrackerClient tracker = new TrackerClient(tg);
        TrackerServer trackerServer = null;
        try {
            trackerServer = tracker.getConnection();
        } catch (Exception e) {
            logger.error("Create a connection file server exception!", e);
            throw new RuntimeException(
                    "Create a connection file server exception!", e);
        }
        if (trackerServer == null) {
            InetSocketAddress[] inetSocketAddresses = tracker.tracker_group.tracker_servers;
            throw new RuntimeException(
                    "Create a connection file server exception! please server address "
                            + Arrays.toString(inetSocketAddresses));
        }
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        int result = 0;
        try {
            result = client.download_file1_with_check(fileId, saveFilePath,
                    params);
            if (result != 0) {
                throw new EtRuntimeException(
                        EtExceptionCode.FILE_BASE + result,
                        "download exception. Please check the fileId \""
                                + documentInfo.getFileId()
                                + "\" does exist on this server("
                                + documentInfo.getIp() + ":"
                                + documentInfo.getPort()
                                + "), or network problems.");
            }
        } catch (Exception e) {
            logger.error("download file【" + documentInfo + "】 is failed!", e);
            throw new EtRuntimeException(EtExceptionCode.FILE_DOWNLOAD, e);
        } finally {
            try {
                trackerServer.close();
            } catch (IOException e) {
                try {
                    trackerServer.close();
                } catch (IOException e1) {
                    logger.error("Release File Server Resource Exception. {}",
                            e1.getLocalizedMessage(), e1);
                }
            }
        }
        return result;
    }

    /**
     * 通过校验的回调方式下载指定文件<br/>
     * 支持下载进度.
     *
     * @param documentInfo 下载的文件信息.
     * @param saveFilePath 保存文件路径.
     * @param fileCallBack 文件回调.
     * @param params       Map对象. 必填key包含appkey 和 uid.
     * @return 成功返回0.否则返回-1.
     */
    public static int downloadFile(final DocumentInfo documentInfo,
                                   final String saveFilePath, final FileCallBack fileCallBack,
                                   Map<String, Object> params) {
        CheckingUtil.checkNull(documentInfo,
                "The file information can not be empty!");
        String fileId = documentInfo.getFileId();
        CheckingUtil.checkNullOrEmpty(fileId, "The fileId can not be empty!");
        CheckingUtil.checkNull(saveFilePath,
                "Save the file location is not specified!");
        CheckingUtil.checkNull(documentInfo.getIp(),
                "The file server IP can not be empty!");
        CheckingUtil.checkNull(documentInfo.getPort(),
                "The file server port can not be empty!");
        CheckingUtil.checkNull(params, "The params is not null!");
        CheckingUtil.checkNull(params.get(EtConstants.ENCRYPT_KEY),
                "Encrypt info is not null!");
        CheckingUtil.checkNull(params.get(EtConstants.UID),
                "The 'uid' key is not null!");
        CheckingUtil.checkNull(params.get(EtConstants.RANDOM_KEY),
                "The 'random number' key is not null!");
        TrackerGroup tg = new TrackerGroup(
                new InetSocketAddress[]{new InetSocketAddress(
                        documentInfo.getIp(), documentInfo.getPort())});
        // 建立连接
        TrackerClient tracker = new TrackerClient(tg);
        TrackerServer trackerServer = null;
        try {
            trackerServer = tracker.getConnection();
        } catch (Exception e) {
            logger.error("Create a connection file server exception!", e);
            throw new RuntimeException(
                    "Create a connection file server exception!", e);
        }
        if (trackerServer == null) {
            InetSocketAddress[] inetSocketAddresses = tracker.tracker_group.tracker_servers;
            throw new RuntimeException(
                    "Create a connection file server exception! please server address "
                            + Arrays.toString(inetSocketAddresses));
        }
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        int result = 0;
        BufferedOutputStream bos = null;
        try {
            @SuppressWarnings("resource")
            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(saveFilePath));
            bos = bufferedOutputStream;
            result = client.download_file1_with_check(fileId,
                    new DownloadCallback() {
                        long currentIndex = 0;
                        int count = 0;

                        @Override
                        public int recv(long file_size, byte[] data, int bytes) {
                            currentIndex += bytes;
                            try {
                                bufferedOutputStream.write(data, 0, bytes);
                                count++;
                                if (count % 5 == 0) {
                                    bufferedOutputStream.flush();// 清空缓冲区
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                // ignore.
                            }
                            if (fileCallBack != null) {
                                fileCallBack.onProcess(documentInfo,
                                        saveFilePath, currentIndex, file_size);
                            }
                            return 0;
                        }
                    }, params);
            if (result != 0) {
                throw new EtRuntimeException(
                        EtExceptionCode.FILE_BASE + result,
                        "download exception. Please check the fileId \""
                                + documentInfo.getFileId()
                                + "\" does exist on this server("
                                + documentInfo.getIp() + ":"
                                + documentInfo.getPort()
                                + "), or network problems.");
            }
            FileInfo fileInfo = client.get_file_info1(fileId);
            if (fileInfo != null) {
                documentInfo.setCrc(Long.toString(fileInfo.getCrc32()));
            }
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("download file【" + documentInfo + "】 is failed! reason " + e);
            logger.error("download file【" + documentInfo + "】 is failed!", e);
            if (fileCallBack != null) {
                fileCallBack.onFailure(saveFilePath, new EtRuntimeException(
                        EtExceptionCode.FILE_DOWNLOAD, e));
                return result;
            }
        } finally {
            try {
                bos.flush();
                bos.close();
                trackerServer.close();
            } catch (IOException e) {
                try {
                    trackerServer.close();
                } catch (IOException e1) {
                    logger.error("Release File Server Resource Exception. {}",
                            e1.getLocalizedMessage(), e1);
                }
            }
        }
        if (fileCallBack != null) {
            fileCallBack.onSuccess(documentInfo, saveFilePath);
        }
        return result;
    }

    /**
     * 通过校验的方式异步下载文件.
     *
     * @param documentInfo 下载的文件信息.
     * @param saveFilePath 保存文件路径.
     * @param fileCallBack 文件回调.
     * @param params       Map对象. 必填key包含appkey 和 uid.
     */
    public static void asynDownloadFile(final DocumentInfo documentInfo,
                                        final String saveFilePath, final FileCallBack fileCallBack,
                                        final Map<String, Object> params) {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    downloadFile(documentInfo, saveFilePath, fileCallBack,
                            params);
                }
            });
            t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    fileCallBack.onFailure(saveFilePath, e);
                }
            });
            t.start();

        } catch (Exception e) {
            if (fileCallBack != null) {
                fileCallBack.onFailure(saveFilePath, e);
                return;
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件信息.
     *
     * @param fileId 文件在服务器上的ID.
     * @param ip     文件服务器IP.
     * @param port   文件服务器端口.
     * @return FileInfo 文件服务信息.
     */
    public static FileInfo getFileInfo(String fileId, String ip, int port) {
        CheckingUtil.checkNull(fileId, "fileId can not be empty!");
        CheckingUtil.checkNull(ip, "The file server IP can not be empty!");
        CheckingUtil.checkState((port > 0 && port <= 65535),
                "The file server port range is incorrect!");

        TrackerGroup tg = new TrackerGroup(
                new InetSocketAddress[]{new InetSocketAddress(ip, port)});
        // 建立连接
        TrackerClient tracker = new TrackerClient(tg);
        TrackerServer trackerServer = null;
        try {
            trackerServer = tracker.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Create a connection file server exception!", e);
        }
        if (trackerServer == null) {
            InetSocketAddress[] inetSocketAddresses = tracker.tracker_group.tracker_servers;
            throw new RuntimeException(
                    "Create a connection file server exception! please server address "
                            + Arrays.toString(inetSocketAddresses));
        }
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);

        FileInfo fi = null;
        try {
            fi = client.get_file_info1(fileId);
        } catch (Exception e) {
            throw new RuntimeException("get【" + ip + ":" + port + "】【" + fileId
                    + "】 occurs an exception", e);
        } finally {
            try {
                trackerServer.close();
            } catch (IOException e) {
                try {
                    trackerServer.close();
                } catch (IOException e1) {
                    logger.error("Release File Server Resource Exception. {}",
                            e1.getLocalizedMessage(), e1);
                }
            }
        }
        return fi;
    }

    /**
     * 通过校验的方式异步上传文件<br />
     * 上传的文件服务器由初始化时通过IM服务器自动获取，用户不需要关心.<br/>
     * 但下载时，用户需要关心之前文件存放所在服务器的IP及端口.
     *
     * @param fileFullPath 文件所在位置全路径.
     * @param fileCallBack 回调对象.
     * @param params       Map对象. 必填key包含appkey 和 uid.
     */
    public static void asynUploadFile(final String fileFullPath,
                                      final FileCallBack fileCallBack, final Map<String, Object> params) {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    realUploadFile(fileFullPath, fileCallBack, params);
                }
            });
            t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    fileCallBack.onFailure(fileFullPath, e);
                }
            });
            t.start();
        } catch (Exception e) {
            if (fileCallBack != null) {
                fileCallBack.onFailure(fileFullPath, e);
                return;
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过校验的同步方式上传文件.<br/>
     * 需要对返回的 DocumentInfo 检查是否为空.
     *
     * @param fileFullPath 文件所在位置全路径.
     * @param fileCallBack 回调对象.
     * @param params       Map对象. 必填key包含appkey 和 uid.
     * @return DocumentInfo SDK的文件信息对象.
     */
    public static DocumentInfo realUploadFile(final String fileFullPath, final FileCallBack fileCallBack, Map<String, Object> params) {
        CheckingUtil
                .checkNull(fileFullPath, "Full file path can not be empty!");
        CheckingUtil.checkNull(params, "The params is not null!");
        CheckingUtil.checkNull(params.get(EtConstants.ENCRYPT_KEY),
                "Encrypt info is not null!");
        CheckingUtil.checkNull(params.get(EtConstants.UID),
                "The 'uid' key is not null!");
        CheckingUtil.checkNull(params.get(EtConstants.RANDOM_KEY),
                "The 'random number' key is not null!");
        File f = new File(fileFullPath);
        if (!f.exists()) {
            fileCallBack.onFailure(fileFullPath, new IllegalArgumentException(
                    "file " + fileFullPath + " does not exist."));
            return null;
        }
        if (!f.isFile()) {
            fileCallBack.onFailure(fileFullPath, new IllegalArgumentException(
                    "please input file, not the directory."));
            return null;
        }
        final File file = new File(fileFullPath);
        final long fileSize = file.length();
        String fileName = getFileName(fileFullPath);
        String fileExtName = getFileExtName(fileFullPath);
        String ip = "";
        int port = 0;
        // 建立连接
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = null;
        try {
            trackerServer = tracker.getConnection();
        } catch (Exception e) {
            // 查看源码，此处永远不可能抛IO异常.源码待改进.
            throw new RuntimeException("Create a connection file server exception!", e);
        }
        if (trackerServer == null) {
            InetSocketAddress[] inetSocketAddresses = tracker.tracker_group.tracker_servers;
            throw new RuntimeException("Create a connection file server exception! please server address " + Arrays.toString(inetSocketAddresses));
        }
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        ip = trackerServer.getInetSocketAddress().getAddress().getHostAddress();
        port = trackerServer.getInetSocketAddress().getPort();
        // 设置元信息
        NameValuePair[] metaList = new NameValuePair[3];
        metaList[0] = new NameValuePair("fileName", fileName);
        metaList[1] = new NameValuePair("fileExtName", fileExtName);
        metaList[2] = new NameValuePair("fileLength", String.valueOf(fileSize));
        final DocumentInfo fi = new DocumentInfo();
        fi.setIp(ip);
        fi.setPort(port);
        fi.setFileName(fileName);
        fi.setSize(String.valueOf(fileSize));
        // 上传文件
        try {
            String fileId = client.upload_file1_with_check(null, fileSize,
                    new UploadCallback() {
                        @Override
                        public int send(OutputStream out) throws IOException {
                            byte[] fileBuff = new byte[MINIMUM_UNIT_BLOCK];
                            int i = 0;
                            long currentIndex = 0;
                            FileInputStream fis = null;
                            try {
                                fis = new FileInputStream(file);
                                while ((i = fis.read(fileBuff)) >= 0) {
                                    if (i == 0) {
                                        continue;
                                    }
                                    out.write(fileBuff, 0, i);
                                    currentIndex += i;
                                    if (fileCallBack != null) {
                                        fileCallBack.onProcess(fi,
                                                fileFullPath, currentIndex,
                                                fileSize);
                                    }
                                }
                                out.flush();
                                fis.close();
                                return 0;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            } finally {
                                if (fis != null)
                                    fis.close();
                            }
                        }
                    }, fileExtName, metaList, params);
            fi.setFileId(fileId);
            FileInfo fileInfo = client.get_file_info1(fileId);
            if (fileInfo != null) {
                fi.setCrc(Long.toString(fileInfo.getCrc32()));
            }
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("upload file [" + fileName + "] is failed! reason :" + e);
            logger.error("upload file [" + fileName + "] is failed!", e);
            throw new RuntimeException("upload file [" + fileName
                    + "] is failed! because " + e.getLocalizedMessage(), e);
        } finally {
            try {
                trackerServer.close();
            } catch (IOException e) {
                try {
                    trackerServer.close();
                } catch (IOException e1) {
                    logger.error("Release File Server Resource Exception. {}",
                            e1.getLocalizedMessage(), e1);
                }
            }
        }
        if (fileCallBack != null) {
            fileCallBack.onSuccess(fi, fileFullPath);
        }
        return fi;
    }

    /**
     * 获取文件后缀名.<br />
     * 不包含.符号,如果不存在则返回空字符串.
     *
     * @param fullPath
     * @return
     */
    public static String getFileExtName(String fullPath) {
        if (fullPath.contains(EtConstants.SEPARATOR_DOT)) {
            return fullPath.substring(fullPath
                    .lastIndexOf(EtConstants.SEPARATOR_DOT) + 1);
        }
        return "";
    }

    /**
     * 获取文件名称.
     *
     * @param fullPath
     * @return
     */
    public static String getFileName(String fullPath) {
        if (fullPath.contains("/")) {
            return fullPath.substring(fullPath.lastIndexOf("/") + 1);
        } else if (fullPath.contains("\\")) {
            return fullPath.substring(fullPath.lastIndexOf("\\") + 1);
        } else if (fullPath.contains(File.separator)) {
            return fullPath.substring(fullPath.lastIndexOf(File.separator) + 1);
        }
        throw new IllegalArgumentException("get file name failed! fullPath = "
                + fullPath);
    }
}