package com.zk.demo.util;


import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class NativeLoader {

    /**
     * 加载项目下的native文件，DLL或SO
     *
     * @param dirPath 需要扫描的文件路径，项目下的相对路径
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public synchronized static void loader(String dirPath) throws IOException, ClassNotFoundException {
        Enumeration<URL> dir = Thread.currentThread().getContextClassLoader().getResources(dirPath);
        // 获取操作系统类型
        String systemType = System.getProperty("os.name");
        //String systemArch = System.getProperty("os.arch");
        // 获取动态链接库后缀名
        String ext = (systemType.toLowerCase().indexOf("win") != -1) ? ".dll" : ".so";
        while (dir.hasMoreElements()) {
            URL url = dir.nextElement();
            String protocol = url.getProtocol();
            if ("jar".equals(protocol)) {
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                JarFile jarFile = jarURLConnection.getJarFile();
                // 遍历Jar包
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String entityName = jarEntry.getName();
                    if (jarEntry.isDirectory() || !entityName.startsWith(dirPath)) {
                        continue;
                    }
                    if (entityName.endsWith(ext)) {
                        loadJarNative(jarEntry);
                    }
                }
            } else if ("file".equals(protocol)) {
                File file = new File(url.getPath());
                loadFileNative(file, ext);
            }

        }
    }

    private static void loadFileNative(File file, String ext) {
        if (null == file) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File f : files) {
                    loadFileNative(f, ext);
                }
            }
        }
        if (file.canRead() && file.getName().endsWith(ext)) {
            try {
                System.load(file.getPath());
                System.out.println("加载native文件 :" + file + "成功!!");
            } catch (UnsatisfiedLinkError e) {
                System.out.println("加载native文件 :" + file + "失败!!请确认操作系统是X86还是X64!!!");
            }
        }
    }

    /**
     * @throws IOException
     * @throws ClassNotFoundException
     * @Title: scanJ
     * @Description 扫描Jar包下所有class
     */
    /**
     * 创建动态链接库缓存文件，然后加载资源文件
     *
     * @param jarEntry
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void loadJarNative(JarEntry jarEntry) throws IOException, ClassNotFoundException {

        File path = new File(".");
        //将所有动态链接库dll/so文件都放在一个临时文件夹下，然后进行加载
        //这是应为项目为可执行jar文件的时候不能很方便的扫描里面文件
        //此目录放置在与项目同目录下的natives文件夹下
        String rootOutputPath = path.getAbsoluteFile().getParent() + File.separator;
        String entityName = jarEntry.getName();
        String fileName = entityName.substring(entityName.lastIndexOf("/") + 1);
        System.out.println(entityName);
        System.out.println(fileName);
        File tempFile = new File(rootOutputPath + File.separator + entityName);
        // 如果缓存文件路径不存在，则创建路径
        if (!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdirs();
        }
        // 如果缓存文件存在，则删除
        if (tempFile.exists()) {
            tempFile.delete();
        }
        InputStream in = null;
        BufferedInputStream reader = null;
        FileOutputStream writer = null;
        try {
            //读取文件形成输入流
            in = NativeLoader.class.getResourceAsStream(entityName);
            if (in == null) {
                in = NativeLoader.class.getResourceAsStream("/" + entityName);
                if (null == in) {
                    return;
                }
            }
            NativeLoader.class.getResource(fileName);
            reader = new BufferedInputStream(in);
            writer = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];

            while (reader.read(buffer) > 0) {
                writer.write(buffer);
                buffer = new byte[1024];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (in != null) {
                in.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.load(tempFile.getPath());
            System.out.println("加载native文件 :" + tempFile + "成功!!");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("加载native文件 :" + tempFile + "失败!!请确认操作系统是X86还是X64!!!");
        }

    }

}
