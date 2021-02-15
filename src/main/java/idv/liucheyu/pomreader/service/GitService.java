package idv.liucheyu.pomreader.service;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GitService {

    private FileService fileService = new FileService();

    public void getRepository(String projectPath) {

    }

    public void discard(String projectPath, String fileName) {
        execGit(projectPath, "git", "checkout", "--", fileName);
    }

    public void discardAll(String projectPath) {
        execGit(projectPath, "git", "checkout", "--", ".");
    }

    public void commit(String projectPath, String fileName) {
        Optional<String> messageOp = fileService.getMessageDialog();
        if (messageOp.isPresent()) {
            String message = messageOp.get().equals("") ? "none messsage" : messageOp.get();
            execGit(projectPath, "git", "add", fileName);
            execGit(projectPath, "git", "commit", "-m", "\"" + message + "\"");
        }
    }

    public void commitOp(String projectPath, List<String> fileNameList) {
        List<String> selectString = new ArrayList<>();
        Stage stage = fileService.getGitAddOption(fileNameList, selectString);
        stage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                if (!selectString.isEmpty()) {
                    Optional<String> messageOp = fileService.getMessageDialog();
                    if (messageOp.isPresent()) {
                        String message = messageOp.get().equals("") ? "none messsage" : messageOp.get();
                        List<String> cmdAdd = new ArrayList<>();
                        cmdAdd.add("git");
                        cmdAdd.add("add");
                        cmdAdd.addAll(selectString);
                        String[] cmdArray = new String[cmdAdd.size()];
                        cmdAdd.toArray(cmdArray);
                        //execGit(projectPath, "git", "add", stringBuilder.toString());
                        execGit(projectPath, cmdArray);
                        execGit(projectPath, "git", "commit", "-m", "\"" + message + "\"");
                    }
                }

            }
        });

    }

    public void discardOp(String projectPath, Map<String, String> fileNameMap) {
        List<String> selectString = new ArrayList<>();
        List<String> fileNameList = fileNameMap.keySet().stream().collect(Collectors.toList());
        Stage stage = fileService.getGitDiscardOption(fileNameList, selectString);
        stage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                selectString.forEach(select -> {
                    fileNameMap.entrySet().forEach(en -> {
                        if (en.getKey().equals(select) && (en.getValue().equals("M") || en.getValue().equals("A"))) {
                            discard(projectPath, select);
                        }

                        if (en.getKey().equals(select) && (en.getValue().equals("?"))) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("刪除確認");
                            alert.setHeaderText("此操作會刪除以下檔案");
                            alert.setContentText(select);
                            Optional<ButtonType> btnTypeOp = alert.showAndWait();
                            if(btnTypeOp.isPresent()){
                                if(btnTypeOp.get() == ButtonType.OK){
                                    execGit(projectPath, "git", "clean", "-d", "-f", select);
                                }
                            }
                        }
                    });

                });
//                List<String> cmdDicard = new ArrayList<>();
//                cmdDicard.add("git");
//                cmdDicard.add("checkout");
//                cmdDicard.add("--");
//                cmdDicard.addAll(selectString);
//                String[] disArray = new String[cmdDicard.size()];
//                cmdDicard.toArray(disArray);
//                execGit(projectPath, disArray);

            }
        });
    }

    public void commitAll(String projectPath) {
        Optional<String> messageOp = fileService.getMessageDialog();

        if (messageOp.isPresent()) {
            String message = messageOp.get().equals("") ? "none messsage" : messageOp.get();
            execGit(projectPath, "git", "add", ".");
            execGit(projectPath, "git", "commit", "-m", "\"" + messageOp.get() + "\"");
        }
    }

    public boolean exist(String projectPath) {
        boolean bool = false;
        File file = new File(projectPath);
        if (file.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return s.contains(".git");
                }
            };
            if (file.list(filter).length > 0) {
                bool = true;
            }
        }
        return bool;
    }

    public void status(String projectPath) {
        execGit(projectPath, "git", "status");
    }

    public List<String> getModifyAndUntracked(String path) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("git", "status", "-s");
        File file = new File(path);
        List<String> mAnduList = new ArrayList();
        if (file.exists()) {
            processBuilder.directory(file);
            try {

                Process process = processBuilder.start();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader reader2 =
                        new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().substring(0, 1).equals("M")) {
                        String fileName = line.replace("M", "").trim();
                        mAnduList.add(fileName);
                    }
                    if (line.trim().substring(0, 1).equals("?")) {
                        String fileName = line.replace("?", "").trim();
                        mAnduList.add(fileName);
                    }

                    if (line.trim().substring(0, 1).equals("A")) {
                        String fileName = line.replace("A", "").trim();
                        mAnduList.add(fileName);
                    }
                    mAnduList = mAnduList.stream().distinct().collect(Collectors.toList());
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
        return mAnduList;
    }

    public Map<String, String> getModifyAndUntrackedMap(String path) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("git", "status", "-s");
        File file = new File(path);
        Map<String, String> untrackMap = new HashMap<>();
        if (file.exists()) {
            processBuilder.directory(file);
            try {

                Process process = processBuilder.start();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader reader2 =
                        new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().substring(0, 1).equals("M")) {
                        String fileName = line.replace("M", "").trim();
                        untrackMap.put(fileName, "M");
                    }
                    if (line.trim().substring(0, 1).equals("?")) {
                        String fileName = line.replace("?", "").trim();
                        untrackMap.put(fileName, "?");
                    }

                    if (line.trim().substring(0, 1).equals("A")) {
                        String fileName = line.replace("A", "").trim();
                        untrackMap.put(fileName, "A");
                    }

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
        return untrackMap;
    }

    public void execGit(String path, String... cmd) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(cmd);
        File file = new File(path);
        if (file.exists()) {
            processBuilder.directory(file);
            try {
                Process process = processBuilder.start();

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader reader2 =
                        new BufferedReader(new InputStreamReader(process.getErrorStream()));
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
