package idv.liucheyu.pomreader.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class MavenCmdService {

    public void CleanInstall(List<String> projectPaths, String mavenDir, String mavenSetting) {
        for (String pjPath : projectPaths) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(mavenDir + "\\mvn.cmd", "-s", mavenSetting, "clean", "install");
            processBuilder.directory(new File(pjPath));
            try {

                Process process = processBuilder.start();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream(), "BIG5"));
                BufferedReader reader2 =
                        new BufferedReader(new InputStreamReader(process.getErrorStream(), "BIG5"));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();
                System.out.println("\nExited with error code : " + exitCode);
                String line2;
                while ((line2 = reader2.readLine()) != null) {
                    System.out.println(line2);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void CleanDeploy(List<String> projectPaths, String mavenDir, String mavenSetting) {
        for (String pjPath : projectPaths) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(mavenDir + "\\mvn.cmd","-s", mavenSetting,  "clean", "deploy");
            processBuilder.directory(new File(pjPath));

            try {

                Process process = processBuilder.start();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream(), "BIG5"));
                BufferedReader reader2 =
                        new BufferedReader(new InputStreamReader(process.getErrorStream(), "BIG5"));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();
                System.out.println("\nExited with error code : " + exitCode);
                String line2;
                while ((line2 = reader2.readLine()) != null) {
                    System.out.println(line2);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
