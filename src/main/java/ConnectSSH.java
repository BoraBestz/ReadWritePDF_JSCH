import com.jcraft.jsch.*;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Vector;

public class ConnectSSH {

    String getInputLocalDir = "src/main/resources/";
    String getOutputLocalDir = "target/generated-sources/";
    String getInputRemoteDir = "/opt/nfs/sftp/test/Best/in/";
    String getOutputRemoteDir = "/opt/nfs/sftp/test/Best/out/";

    public ChannelSftp setupJsch() throws JSchException, IOException, SftpException {
        String username = "root";
        String password = "password@1";
        String host = "10.10.10.29";
        JSch jsch = new JSch();
        Session jschSession = jsch.getSession(username, host);
        jschSession.setOutputStream(System.out);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        jschSession.setConfig(config);
        jschSession.setPassword(password);
        System.out.println("jschSession connecting...");
        jschSession.connect();
        System.out.println("---jschSession connect suscessful---");
        Channel channel = jschSession.openChannel("sftp");
        System.out.println("channel connecting...");
        channel.connect();
        System.out.println("---channel connect suscessful---");
        ChannelSftp channelSftp = (ChannelSftp) channel;
        return channelSftp;
    }

    public void downloadFile(ChannelSftp channelSftp) throws SftpException {
        channelSftp.cd(getInputRemoteDir);
        Vector<ChannelSftp.LsEntry> lsFiles = channelSftp.ls(getInputRemoteDir);
        if (lsFiles != null && !lsFiles.isEmpty()) {
            for (ChannelSftp.LsEntry files : lsFiles) {
                if (files != null) {
                    String fileNameToget = files.getFilename();
                    String filePath = getInputRemoteDir + "/" + files.getFilename();

                    if (fileNameToget.equals(".") || fileNameToget.equals("..")) {
                        continue;
                    }
                    channelSftp.get("/" + filePath, getInputLocalDir + fileNameToget);
                }
            }
        }
        System.out.println("Download File Complete");
    }

    public ArrayList<String> convertToBase64() throws IOException {
        File dir = new File(getInputLocalDir);
        ArrayList<String> listB64 = new ArrayList<>();
        for (File u : dir.listFiles()) {
            if (u.isFile() && u.getName().endsWith(".pdf")) {
                byte[] bytes = Files.readAllBytes(u.toPath());
                String b64 = Base64.getEncoder().encodeToString(bytes);
                listB64.add(b64);
            }
        }
        System.out.println("convertToBase64 Complete");
        return listB64;
    }

    public void convertToFile(ChannelSftp channelSftp, ArrayList<String> listB64) throws IOException {
        File dir = new File(getInputLocalDir);
        int num = 0;
        for (File u : dir.listFiles()) {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] decodedBytes = decoder.decodeBuffer(listB64.get(num));
            String fileName = new Date().getTime() + ".pdf";
            File file = new File(getOutputLocalDir + fileName);
            FileOutputStream fop = new FileOutputStream(file);
            fop.write(decodedBytes);
            fop.flush();
            fop.close();
            num++;
        }
        System.out.println("convertToFile Complete");
    }

    public void uploadFile(ChannelSftp channelSftp) throws FileNotFoundException, SftpException {
        File dir = new File(getOutputLocalDir);
        for (File u : dir.listFiles()) {
            if (u.isFile() && u.getName().endsWith(".pdf")) {
                System.out.println(("Uploading..."));
                channelSftp.put(new FileInputStream(getOutputLocalDir + u.getName()), getOutputRemoteDir + u.getName(), ChannelSftp.OVERWRITE);
                System.out.println("Upload Complete!");
            }
        }
        System.out.println("UploadFile Complete");
    }

}



