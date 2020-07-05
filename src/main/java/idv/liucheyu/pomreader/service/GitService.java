package idv.liucheyu.pomreader.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.storage.file.FileBasedConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class GitService {

    private FileService fileService = new FileService();

    public Repository getRepository(String projectPath) {
        RepositoryBuilder builder = new RepositoryBuilder();
        File file = new File(projectPath, ".git");
        Repository repository = null;
        if (file.exists()) {
            try {
                repository = builder.setGitDir(file).readEnvironment().findGitDir().build();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return repository;
    }

    public void discard(Repository repository, String fileName) {
        Git git = new Git(repository);

        try {
            git.checkout().addPath(fileName).call();
        } catch (GitAPIException ex) {
            ex.printStackTrace();
        }

    }

    public void commit(Repository repository, String path) {
        Git git = new Git(repository);

        try {
            String message = "";
            Optional<String> messageOp = fileService.getMessageDialog();
            if (messageOp.isPresent()) {
                message = messageOp.get().equals("") ? "none message" : messageOp.get();
                git.add().addFilepattern(path).call();
                git.commit().setOnly(path).setMessage(message).call();
            }

        } catch (GitAPIException ex) {
            ex.printStackTrace();
        }
    }

    public void commitAll(Repository repository) {
        Git git = new Git(repository);

        try {
            String message = "";
            Optional<String> messageOp = fileService.getMessageDialog();
            if (messageOp.isPresent()) {
                message = messageOp.get().equals("") ? "none message" : messageOp.get();
                git.add().addFilepattern(".").call();
                git.commit().setMessage(message).call();
            }

        } catch (GitAPIException ex) {
            ex.printStackTrace();
        }
    }

    public boolean exist(File repoDir) {
        Repository repository = null;
        try {
            RepositoryBuilder repositoryBuilder = new RepositoryBuilder().setGitDir(repoDir);
            repository = repositoryBuilder.build();
            if (repository.getConfig() instanceof FileBasedConfig) {
                return ((FileBasedConfig) repository.getConfig()).getFile().exists();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return repository.getDirectory().exists();
    }

    public void status(String projectPath) {

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("git", "status");
        File file = new File(projectPath);
        if(file.exists()){
            processBuilder.directory(file);
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
