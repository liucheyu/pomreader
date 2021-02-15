package idv.liucheyu.pomreader.exception;

public class MavenExcuteErrorException extends Exception {

    private String erorrMessage;

    public MavenExcuteErrorException(String erorrMessage){
        this.erorrMessage = erorrMessage;
    }

    public String getErorrMessage() {
        return erorrMessage;
    }

    public void setErorrMessage(String erorrMessage) {
        this.erorrMessage = erorrMessage;
    }
}
