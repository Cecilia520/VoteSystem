package com.Cecilia.vote.util;

import com.Cecilia.vote.util.EncodingUtil.EncodingUtil;

import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Cecilia on 2017/8/1.
 */
public class FileUtilImplements {

    public static void main(String[] args) {
        FileUtilImplements fui = new FileUtilImplements();
        Set<String> set = new HashSet<>();
        set.add("ccCecilia12342");
        set.add("芷若初荨1");
        set.add("芷若初荨2");
        set.add("芷若初荨3");
        try {
            System.out.println(fui.writeFile("E:\\Test\\demo3\\cccc.txt", set, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取某个文件夹下的文件数量，仅包含该文件夹，不包含子文件夹
     *
     * @param path 文件的完整绝对路径
     * @return 文件夹中的文件数量
     */
    public static int getFileNumber(String path) {
        //判断文件路径是否合法
        if (path == null || path.trim().equals("")) {
            return 0;
        }
        File file = new File(path);//导包   Alt+Enter
        File[] files = file.listFiles();//将文件集合存储在数组中
        if (files == null) {
            return 0;
        }
        int returnValue = 0;
        for (File temFile : files) {
            if (temFile.isFile()) {
                returnValue++;
            }
        }
        return returnValue;
    }

    /**
     * 删除一个文件夹
     *
     * @param path 文件夹的完整绝对路径
     * @return 删除成功，返回true，否则返回false
     */
    public static boolean delFolder(String path) {
        if (!isFolder(path)) {
            return false;
        }
        File file = new File(path);
        return file.delete();
    }


    /**
     * 删除一个文件
     *
     * @param path 文件的完整绝对路径
     * @return 删除成功，返回true，否则返回false
     */
    public static boolean delFile(String path) {
        if (!isFile(path)) {
            return false;
        }
        File file = new File(path);
        return file.delete();
    }

    /**
     * 删除文件夹下所有文件和子文件夹，但文件夹本身不会被删除
     *
     * @param path 文件的完整绝对路径
     * @return 完全清空，返回true，否则返回false
     */
    public static boolean delAllFile(String path) {
        //判断该路径是否是文件夹
        if (!isFolder(path)) {
            return false;
        }
        File file = new File(path);
        File[] files = file.listFiles();
        //判断返回的文件数组是否为空
        if (files == null) {
            return true;//该文件夹为空
        } else {
            //遍历该文件数组
            for (File tempFile : files) {
                //判断返回的该文件是否是文件
                if (tempFile.isFile()) {
                    tempFile.delete();//则删除
                } else if (tempFile.listFiles() == null) {//不是文件，则判断返回的文件集合是否为空
                    tempFile.delete();//如果此子文件夹为空，则删除该子文件夹
                } else {
                    delAllFile(tempFile.getPath());//如果此子文件夹不为空，则删除该文件夹的路径
                    tempFile.delete();//并且还要把该文件夹中的文件删除
                }
            }
        }
        return true;
    }

    /**
     * 获取文件大小，单位根据传入的参数来决定
     *
     * @param path  文件的完整绝对路径
     * @param units 单位：KB，MB，GB
     * @return 文件的尺寸
     */
    public static double getFileSize(String path, String units) {
        //判断文件路径是否是文件
        if (!isFile(path)) {
            return 0;
        }
        File file = new File(path);//根据路经创建一个文件
        double length = file.length();//获取文件长度
        if (units == null) {             //如果尺寸为0
            return length;
        } else if ("KB".equals(units.toLowerCase())) {
            return (length / 1024);
        } else if ("MB".equals(units.toLowerCase())) {
            return (length / 1024 / 1024);
        } else if ("GB".equals(units.toLowerCase())) {
            return (length / 1024 / 1024 / 1024);
        } else {
            return length;
        }
    }

    /**
     * 把文件路径和文件名组合为完整的文件路径
     *
     * @param path     文件的完整绝对路径，不含文件名
     * @param fileName 文件名，含后缀
     * @return 组合后的完整路径
     */
    public static String getFullPath(String path, String fileName) {
        //判断该路径是否合法,文件名是否合法
        if (path == null || path.trim().equals("")
                || fileName == null || fileName.trim().equals("")) {
            return null;
        } else if (path.endsWith("\\") || path.endsWith("/")) {  //判断路径是否以/或\结尾
            return path + fileName;
        }
        return path + File.separator + fileName;
    }

    /**
     * 检查文件夹是否都不为空
     *
     * @param paths 文件夹路径集合
     * @return 如果都不为空，则返回true，只要有一个为空，就返回false
     */
    public static boolean isNotForPaths(List<String> paths) {

        if (paths == null) {
            return false;
        }

        for (String path : paths) {
            //判断该路径下的File是否是文件夹
            if (!isFolder(path)) {
                return false;
            }
            File file = new File(path);
            //判断返回的list是否是为空
            if (file.list() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 保存文件到指定路径，如果文件不存在，那么创建一个新文件
     *
     * @param data 文件内容
     * @param path 文件的完整绝对路径
     * @return 保存成功，返回true，否则，返回false
     * @throws IOException 此处选择抛出异常的原因：
     *                     1. 抛出由每一个调用者处理
     *                     2.try-catch需要根据具体的业务来处理，而在工具类中，不能完全对所有异常进行处理
     */
    public static boolean writeByteToFile(String path, byte[] data) throws IOException {
        if (data == null || data.length < 1) {
            return false;
        }
        //判断文件是否存在
        if (!checkFile(new String(data), path)) {
            return false;
        }
        //获取输出流
        OutputStream out = new FileOutputStream(path);//根据路径使用字符流操作数据
        BufferedOutputStream outBuffer = new BufferedOutputStream(out, data.length);
        outBuffer.write(data);
        outBuffer.flush();                            //及时刷新缓存区
        out.close();
        outBuffer.close();
        return true;
    }


    /**
     * 添加内容到指定文件，如果该文件不存在，则创建
     *
     * @param path        文件的完整绝对路径
     * @param fileContent 要保存的内容
     * @param flag        如果为true，则向现有的文件中添加，否则清空并重新写入
     * @return 保存完成，返回true，否则返回false
     * @throws IOException
     */
    public static boolean writeFile(String path, String fileContent, boolean flag) throws IOException {

        //检测文件是否存在
        if (!checkFile(path, fileContent)) {
            return false;
        }
        //文件存在，那么开始写文件
        OutputStream out = new FileOutputStream(path, flag);
        Writer writer = new OutputStreamWriter(out, EncodingUtil.getJavaEncode(path));
        writer.write(fileContent + "\r\n");
        writer.flush();
        out.close();
        writer.close();
        return true;
    }

    /**
     * 批量添加内容到指定文件，如果该文件不存在，则创建
     *
     * @param path        文件的完整绝对路径
     * @param fileContent 要保存的内容
     * @param flag        如果为true，则向现有的文件中添加，否则清空并重新写入
     * @return 保存完成，返回true，否则返回false
     * @throws IOException
     */
    public static boolean writeFile(String path, List<String> fileContent, boolean flag) throws IOException {
        //不能直接调用writeFile写入一行数据的方法，在功能上，是可实现多行写入数据；但是在性能上，是不可行的，
        // 原因：每一次调用，都需要new一个file对象，如果在海量数据情况下，则性能会变得很差

        //检测文件是否存在
        if (!checkFile(path, fileContent)) {
            return false;
        }
        //文件存在，那么开始写文件
        File file = new File(path);
        OutputStream out = new FileOutputStream(path, flag);
        Writer writer = new OutputStreamWriter(out, EncodingUtil.getJavaEncode(path));
        for (String str : fileContent) {
            if (str != null || "".equals(str)) {
                writer.write(str + "\r\n");
            }
        }
        writer.flush();
        out.close();
        writer.close();
        return true;
    }

    /**
     * 批量添加内容到指定文件，如果该文件不存在，则创建
     *
     * @param path        文件的完整绝对路径
     * @param fileContent 要保存的内容
     * @param flag        如果为true，则向现有的文件中添加，否则清空并重新写入
     * @return 保存完成，返回true，否则返回false
     * @throws IOException
     */
    public static boolean writeFile(String path, Set<String> fileSet, boolean flag) throws IOException {
        //不能直接调用writeFile写入一行数据的方法，在功能上，是可实现多行写入数据；但是在性能上，是不可行的，
        // 原因：每一次调用，都需要new一个file对象，如果在海量数据情况下，则性能会变得很差

        //检测文件是否存在
        if (!checkFile(path, fileSet)) {
            return false;
        }
        //文件存在，那么开始写文件
        File file = new File(path);
        OutputStream out = new FileOutputStream(path, flag);
        Writer writer = new OutputStreamWriter(out, EncodingUtil.getJavaEncode(path));
        for (String str : fileSet) {
            if (str != null || "".equals(str)) {
                writer.write(str + "\r\n");
            }
        }
        writer.flush();
        out.close();
        writer.close();
        return true;
    }

    /**
     * 添加内容到指定文件 如果该文件不存在则创建
     *
     * @param path        文件的绝对路径（不含文件名）
     * @param fileName    文件名
     * @param fileContent 要保存的内容集合
     * @param flag        如果为true，则向现有文件中添加，否则清空并新写入
     * @return 保存完成返回true，否则返回false
     * @throws IOException
     */
    public static boolean writeFile(String path, String fileName, List<String> fileContent, boolean flag) throws IOException {

        //检测fileName是否合法
        if (fileName == null || fileName.trim().equals("")) {
            return false;
        }
        String fullPath = getFullPath(path, fileName);
        //检测绝对路径是否为空
        if (fullPath == null || fullPath.trim().equals("")) {
            return false;
        }
        return writeFile(fullPath, fileContent, flag);
    }

    /**
     * 根据文件对象来读取文件,将文件内容全部读取出来，然后存到一个list集合中
     * 如果路径错误、文件不存在、为空，返回尺寸为0的list
     *
     * @param path 文件的完整绝对路径
     * @return 读取到的文件内容
     * @throws IOException
     */
    public static List<String> readFile(String path) throws IOException {
        List<String> returnValue = new ArrayList<>();
        if (!isFile(path)) {
            return returnValue;
        }
        //根据路径读取文件
        InputStream input = new FileInputStream(path);  //根据路径获取文件输入流
        Reader reader = new InputStreamReader(input, EncodingUtil.getJavaEncode(path));
        LineNumberReader lineReader = new LineNumberReader(reader);//Reader的按行读取工具

        //批量读取文件内容
        while (true) {
            String str = lineReader.readLine();//读取一行
            if (str == null) {
                break;
            }
            returnValue.add(str);
        }
        input.close();
        reader.close();
        return returnValue;
    }

    /**
     * 根据文件对象来读取文件,并将读取的内容全部存储到一个list集合中
     * 如果文件错误、文件不存在、为空，返回尺寸为0的list
     *
     * @param file 要读取的文件对象
     * @return 读取到的文件内容
     * @throws IOException
     */
    public static List<String> readFile(File file) throws IOException {

        List<String> fileList = new ArrayList<>();
        //检测File是否为空、是否是文件夹或者是否存在
        if (file == null || !file.exists() || file.isDirectory()) {
            return fileList;
        }

        //直接使用FileInputStream根据文件来读取文件
        FileInputStream fis = new FileInputStream(file);
        Reader reader = new InputStreamReader(fis, EncodingUtil.getJavaEncode(file.getPath()));
        LineNumberReader lineReader = new LineNumberReader(reader);
        String line = "";
        while ((line = lineReader.readLine()) != null) {
            fileList.add(line);
        }
        fis.close();
        reader.close();
        return fileList;

    }

    /**
     * 读取一个文件，并排重后返回
     * 如果路径错误、文件不存在、为空，返回尺寸为0的set
     *
     * @param path 文件的完整绝对路径
     * @return 读取到的文件内容
     * @throws IOException
     */
    public static Set<String> readFileNoDup(String path) throws IOException {

        Set<String> setFile = new HashSet<>();
        //检测文件是否存在
        if (!isFile(path)) {
            return setFile;
        }
        //文件存在，那么可以读文件
        InputStream input = new FileInputStream(path);
        Reader reader = new InputStreamReader(input, "utf-8");
        LineNumberReader lineReader = new LineNumberReader(reader);
        String line = "";
        while ((line = lineReader.readLine()) != null) {
            setFile.add(line);
        }
        input.close();
        reader.close();
        return setFile;
    }

    /**
     * 读取一个文件，排重后写入第二个文件，并把排重结果返回
     * 如果路径错误、文件不存在、为空，返回尺寸为0的list
     *
     * @param path1 第一个文件的完整绝对路径
     * @param path2 第二个文件的完整绝对路径
     * @return 读取到的文件内容
     * @throws IOException
     */
    public static List<String> excludeDuplicates(String path1, String path2) throws IOException {

        List<String> listContent = new ArrayList<>();
        Set<String> setContent = new HashSet<>();
        //检测两个参数
        if (path1 == null || path1.trim().equals("")
                || path2 == null || path2.trim().equals("")) {
            return listContent;
        }
        //方法一
        //读取一个文件,并将其内容排重添加到set集合中
        setContent = readFileNoDup(path1);
        //将set集合添加到list集合
        listContent.addAll(setContent);
        //调用writerFile(path,List<String> fileContent,flag)方法写入另一个文件中
        writeFile(path2, listContent, false);

        return listContent;

        //方法二
        //根据路径读取一个文件内容，并将其存储在list集合中

        //使用contains对list集合进行排重

        //将其list内容写入另一个文件中

            /*方法三
            List<String> returnValue =new ArrayList<>();
            if(!isFile(path1)||!checkFile(path2)){
                return returnValue;
            }
            Set<String> tempSet = readFileNoDup(path1);
            returnValue.addAll(tempSet);
            Collections.reverse(returnValue);
            if(!writeFile(path2,returnValue,false)){
                returnValue.clear();
                return returnValue;
            }
            return returnValue;
             */
    }

    /**
     * 检测文件是否存在
     *
     * @return 如果存在，返回true，如果不存在，那么返回false
     */
    private static boolean checkFile(String path, String content) throws IOException {
        //1.检测两个参数是否合法
        if (content == null || content.trim().equals("")
                || path == null || path.trim().equals("")) {
            return false;
        }
        //2.文件存在时，可以根据路径创建文件对象
        File file = new File(path);
        //3.判断该文件对象是一个文件还是一个文件夹
        if (!isFile(path)) {
            //文件目录存在，但是文件本身不存在，则创建文件
            String subPath = path.substring(0, path.lastIndexOf("\\"));
            File tempFile = new File(subPath);
            tempFile.mkdirs();//如果文件目录不存在，那么下创建目录
            if (!file.createNewFile()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测文件是否合法，不存在则创建（递归创建目录）
     *
     * @param path 文件完整的绝对路径
     * @param list 需要保存的内容集合
     * @return 如果存在，返回true，如果不存在，那么返回false
     * @throws IOException
     */
    private static boolean checkFile(String path, List<String> list) throws IOException {

        //1.检测两个参数
        if (list == null || list.size() < 1
                || path == null || path.trim().equals("")) {
            return false;
        }
        //2.文件存在时，可以根据路径创建文件对象
        File file = new File(path);
        //3.判断该文件对象是否是一个文件
        if (!isFile(path)) {
            //4.检测文件不存在的情形一：文件目录存在，文件本身不存在
            String subFilePath = path.substring(0, path.lastIndexOf("\\"));
            File tempFile = new File(subFilePath);
            //根据截取的文件路径创建一个新的文件对象
            tempFile.mkdirs();
            if (!file.createNewFile()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测文件是否合法，不存在则创建（递归创建目录）
     *
     * @param path 文件完整的绝对路径
     * @param list 需要保存的内容集合
     * @return 如果存在，返回true，如果不存在，那么返回false
     * @throws IOException
     */
    private static boolean checkFile(String path, Set<String> set) throws IOException {

        //1.检测两个参数
        if (set == null || set.size() < 1
                || path == null || path.trim().equals("")) {
            return false;
        }
        //2.文件存在时，可以根据路径创建文件对象
        File file = new File(path);
        //3.判断该文件对象是否是一个文件
        if (!isFile(path)) {
            //4.检测文件不存在的情形一：文件目录存在，文件本身不存在
            String subFilePath = path.substring(0, path.lastIndexOf("\\"));
            File tempFile = new File(subFilePath);
            //根据截取的文件路径创建一个新的文件对象
            tempFile.mkdirs();
            if (!file.createNewFile()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断传入的路径是否是一个文件夹
     *
     * @param path 文件夹的完整绝对路径
     * @return 路径非法、路径标示文件不存在或不是文件夹均返回false
     */
    private static boolean isFolder(String path) {
        if (path == null || path.trim().equals("")) {
            return false;
        }
        File file = new File(path);
        if (!file.exists() || file.isFile()) {
            return false;
        }
        return true;
    }

    /**
     * 判断传入的路径是否是一个文件
     *
     * @param path 文件夹的完整绝对路径
     * @return 路径非法、路径标示文件不存在或不是文件夹均返回false
     */
    public static boolean isFile(String path) {
        if ((path == null) || path.trim().equals("")) {
            return false;
        }
        File file = new File(path);
        //如果文件存在或者是个文件夹
        if ((!file.exists()) || file.isDirectory()) {
            return false;
        }
        return true;
    }

}


