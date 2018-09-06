/**
 * Create with IntelliJ IDEA
 * Project name : qinniu-file
 * Package name : org.wukm.jenkins
 * Author : Wukunmeng
 * User : wukm
 * Date : 18-9-4
 * Time : 下午7:37
 * ---------------------------------
 */
package org.wukm.jenkins;

import java.io.*;

/**
 * Create with IntelliJ IDEA
 * Project name : qinniu-file
 * Package name : org.wukm.jenkins
 * Author : Wukunmeng
 * User : wukm
 * Date : 18-9-4
 * Time : 下午7:37
 * ---------------------------------
 * To change this template use File | Settings | File and Code Templates.
 */
public class FileUtils {

    private FileUtils(){}

    public static String LINE_SEPARATOR = System.getProperty("line.separator");

    public static boolean createFile(File file, PrintStream logger) {
        if(file == null) {
            logger.println("file == null");
            return false;
        }
        if(file.exists()) {
            logger.println("file exist");
            return true;
        }
        if(!createDirectory(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e){
            logger.println("create file[" + file.getAbsolutePath() + "] exception:" + e.getMessage());
        }
        return false;
    }

    private static boolean createDirectory(File file){
        if(file.exists()) {
            return true;
        }
        createDirectory(file.getParentFile());
        file.mkdir();
        return true;
    }

    public static boolean writeFileFromString(final File file, final String content,
                                              final boolean append, PrintStream logger) {
        if (file == null || content == null) return false;
        if (!createFile(file, logger)) return false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;
        } catch (IOException e) {
            logger.println("create file[" + file.getAbsolutePath() + "] exception:" + e.getMessage());
            return false;
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                logger.println("close buffered exception:" + e.getMessage());
            }
        }
    }
}
