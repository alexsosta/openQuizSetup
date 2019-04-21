

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Installer implements Runnable{

    private String path;
    private String jdkPath;
    private int port;
    private Controller controller;

    public Installer(String path, String jdkPath, int port, Controller controller) {
        this.path = path;
        this.jdkPath = jdkPath;
        this.port = port;
        this.controller = controller;
    }

    private void download(String path){
        URL website = null;
        try {
            website = new URL("https://download.jboss.org/wildfly/16.0.0.Final/wildfly-16.0.0.Final.zip");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ReadableByteChannel rbc = null;
        try {
            rbc = Channels.newChannel(website.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unpack(String path, String name){
        byte[] buffer = new byte[1024];
        String zipFileName = path + "\\" + name;
        // Создаем каталог, куда будут распакованы файлы
        final String dstDirectory = path;
        final File dstDir = new File(dstDirectory);
        if (!dstDir.exists()) {
            dstDir.mkdir();
        }
        // Получаем содержимое ZIP архива
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(zipFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ZipEntry ze = null;
        try {
            ze = zis.getNextEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String nextFileName;
        while (ze != null) {
            nextFileName = ze.getName();
            File nextFile = new File(dstDirectory + File.separator + nextFileName);
            if (ze.isDirectory()) {
                nextFile.mkdir();
            } else {
                // Создаем все родительские каталоги
                new File(nextFile.getParent()).mkdirs();
                // Записываем содержимое файла
                try (FileOutputStream fos = new FileOutputStream(nextFile)) {
                    int length;
                    while((length = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                ze = zis.getNextEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            zis.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void delete(String path) {
        File file = new File(path);
        file.delete();
    }

    static public void ExportResource(String resourceName, String dest) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = Main.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            dest = dest.replace('\\', '/');
            resStreamOut = new FileOutputStream(dest + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }
    }

    @Override
    public void run() {
        controller.progressTextField.setText("downloading wildfly");
        String warName = "simpleQuiz.war";
        String zipName = "wildfly-16.0.0.Final";
        download(path + "\\" + zipName + ".zip");
        controller.progressTextField.setText("unpacking wildfly");
        unpack(path, zipName + ".zip");
        delete(path + "\\" + zipName + ".zip");
        try {
            ////////CONFIGURATION SERVER//////////////////////////
            controller.progressTextField.setText("configuring wildfly");
            final char dm = (char) 34;
            String text = "set " + dm + "JAVA_HOME="+ jdkPath + dm;
            Files.write(Paths.get(path + "\\" + zipName + "\\bin\\standalone.conf.bat"), text.getBytes(), StandardOpenOption.APPEND);
            //////////cmd///////////////////////
            try {
                ExportResource("configure-server.cli", path + "\\" + zipName + "\\");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String command1 = "cmd /c start cmd.exe /c " +
                    path + "\\" + zipName + "\\bin\\standalone.bat -b 0.0.0.0 -Djboss.http.port=" + port + " ";
            String command2 = path + "\\" + zipName + "\\bin\\jboss-cli.bat --connect --file=" + path + "\\" + zipName + "\\configure-server.cli";
            Runtime.getRuntime().exec(command1);
            while (!Ping.getInstance().crunchifyAddressReachable("127.0.0.1",port,2000));
            Runtime.getRuntime().exec(command2);
            ////////DEPLOYING//////////////////////////
            try {
                ExportResource("resources.zip", path + "\\" + zipName + "\\standalone\\deployments\\");
            } catch (Exception e) {
                e.printStackTrace();
            }
            controller.progressTextField.setText("deploying application");
            unpack(path + "\\" + zipName + "\\standalone\\deployments\\","resources.zip");
            delete(path + "\\" + zipName + "\\standalone\\deployments\\resources.zip");
            File theDir = new File(path + "\\" + zipName + "\\modules\\com\\xartifex\\wildfly\\custom\\security\\main");
            theDir.mkdir();
            Files.createDirectories(Paths.get(path + "\\" + zipName + "\\modules\\com\\xartifex\\wildfly\\custom\\security\\main"));
            try {
                ExportResource("secure.zip", path + "\\" + zipName + "\\modules\\com\\xartifex\\wildfly\\custom\\security\\main\\");
            } catch (Exception e) {
                e.printStackTrace();
            }
            unpack(path + "\\" + zipName + "\\modules\\com\\xartifex\\wildfly\\custom\\security\\main\\", "secure.zip");
            delete(path + "\\" + zipName + "\\modules\\com\\xartifex\\wildfly\\custom\\security\\main\\secure.zip");
            delete(path + "\\" + zipName + "\\welcome-content\\index.html");
            try {
                ExportResource("index.html", path + "\\" + zipName + "\\welcome-content\\");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //////////////SETUP SQL//////////////////
            File warnik = new File(path + "\\" + zipName + "\\standalone\\deployments\\" + warName + ".deployed");
            while (!warnik.exists());
            delete(path + "\\" + zipName + "\\configure-server.cli");
            Configurator.getInstance().config();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller.progressTextField.setText("finished");
    }
}
