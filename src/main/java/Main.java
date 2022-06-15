import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws IOException, JSchException, SftpException {
        ArrayList<String> listB64;
        ConnectSSH connectSSH =  new ConnectSSH();
        ChannelSftp channelSftp = connectSSH.setupJsch();
        connectSSH.downloadFile(channelSftp);
        listB64 = connectSSH.convertToBase64();
        connectSSH.convertToFile(channelSftp, listB64);
        connectSSH.uploadFile(channelSftp);
    }
}
