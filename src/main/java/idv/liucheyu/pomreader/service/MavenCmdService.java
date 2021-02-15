package idv.liucheyu.pomreader.service;

import idv.liucheyu.pomreader.exception.MavenExcuteErrorException;

import java.io.*;
import java.util.List;


public class MavenCmdService {

    public void CleanInstall(List<String> projectPaths, String mavenDir, String mavenSetting) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String pjPath : projectPaths) {
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command(mavenDir + "\\mvn.cmd", "-s", mavenSetting, "clean", "install");
                    processBuilder.directory(new File(pjPath));
                    Process process = null;
                    InputStream in = null;
                    InputStreamReader isrn = null;
                    BufferedReader reader = null;
                    InputStream in2 = null;
                    InputStreamReader isrn2 = null;
                    BufferedReader reader2 = null;
                    try {
                        process = processBuilder.start();
                        in = process.getInputStream();
                        isrn = new InputStreamReader(in);
                        reader = new BufferedReader(isrn);
                        in2 = process.getErrorStream();
                        isrn2 = new InputStreamReader(in2);
                        reader2 = new BufferedReader(isrn2);
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
                        if (exitCode != 0) {
                            throw new MavenExcuteErrorException("maven excute has error, stop process");
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (MavenExcuteErrorException e) {
                        System.out.println(e.getErorrMessage());
                    } finally {
                        try {
                            in.close();
                            isrn.close();
                            reader.close();
                            in2.close();
                            isrn2.close();
                            reader2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        process.destroy();
                        break;
                    }
                }
            }
        }).start();

    }

    public void CleanDeploy(List<String> projectPaths, String mavenDir, String mavenSetting) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String pjPath : projectPaths) {
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command(mavenDir + "\\mvn.cmd", "-s", mavenSetting, "clean", "deploy");
                    processBuilder.directory(new File(pjPath));
                    Process process = null;
                    InputStream in = null;
                    InputStreamReader isrn = null;
                    BufferedReader reader = null;
                    InputStream in2 = null;
                    InputStreamReader isrn2 = null;
                    BufferedReader reader2 = null;
                    try {
                        process = processBuilder.start();
                        in = process.getInputStream();
                        isrn = new InputStreamReader(in);
                        reader = new BufferedReader(isrn);
                        in2 = process.getErrorStream();
                        isrn2 = new InputStreamReader(in2);
                        reader2 = new BufferedReader(isrn2);
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
                        if (exitCode != 0) {
                            throw new MavenExcuteErrorException("maven excute has error, stop process");
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (MavenExcuteErrorException e) {
                        System.out.println(e.getErorrMessage());
                    } finally {
                        try {
                            in.close();
                            isrn.close();
                            reader.close();
                            in2.close();
                            isrn2.close();
                            reader2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        process.destroy();
                        break;
                    }
                }
            }
        }).start();
    }

}
