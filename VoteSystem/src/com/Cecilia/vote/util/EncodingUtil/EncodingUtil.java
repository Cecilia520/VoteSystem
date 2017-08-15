package com.Cecilia.vote.util.EncodingUtil;

/**
 * 编码工具类
 * Created by Cecilia on 2017/8/6.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

public class EncodingUtil {

    /**
     * 得到文件的编码
     * @param filePath 文件路径
     * @return 文件的编码
     */
    public static String getJavaEncode(String filePath){
        BytesEncodingDetect s = new BytesEncodingDetect();
        String fileCode = BytesEncodingDetect.javaname[s.detectEncoding(new File(filePath))];
        return fileCode;
    }
}

