/**
 * Create with IntelliJ IDEA
 * Project name : qinniu-file
 * Package name : org.wukm.jenkins
 * Author : Wukunmeng
 * User : wukm
 * Date : 18-9-4
 * Time : 下午6:07
 * ---------------------------------
 */
package org.wukm.jenkins;

import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.StringUtils;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.CopyOnWriteList;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Create with IntelliJ IDEA
 * Project name : qinniu-file
 * Package name : org.wukm.jenkins
 * Author : Wukunmeng
 * User : wukm
 * Date : 18-9-4
 * Time : 下午6:07
 * ---------------------------------
 * To change this template use File | Settings | File and Code Templates.
 */
public class FileUploadRecorder extends Recorder {

    public FileUploadRecorder() {
        super();
    }

    private List<FileUploadEntry> uploadEntries = new ArrayList<>();

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }

    public void addAll(List<FileUploadEntry> entries) {
        uploadEntries.addAll(entries);
    }

    public List<FileUploadEntry> getUploadEntries(){
        return uploadEntries;
    }

    @Override
    public FileUploadDescriptor getDescriptor() {
        return (FileUploadDescriptor)super.getDescriptor();
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher,
                           BuildListener listener) throws IOException, InterruptedException {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a
        // build.

        // This also shows how you can consult the global configuration of the
        // builder
        FilePath ws = build.getWorkspace();
        PrintStream logger = listener.getLogger();
        Map<String, String> envVars = build.getEnvironment(listener);
        final boolean buildFailed = build.getResult() == Result.FAILURE;
        logger.println("upload file to qi niu ...");
        for (FileUploadEntry entry : this.uploadEntries) {
            //对可输入内容字段进行变量替换
            String source = Util.replaceMacro(entry.getSource(), envVars);
            String bucket = Util.replaceMacro(entry.getBucket(), envVars);
            String prefix = Util.replaceMacro(entry.getPrefix(), envVars);
            String netUrl = Util.replaceMacro(entry.getNetUrl(), envVars);
            String urlsFile = Util.replaceMacro(entry.getUrlsFile(), envVars);

            logger.println("七牛参数生成：");
            logger.println("文件夹路径：" + source);
            logger.println("要上传到的 bucket：" + bucket);
            logger.println("上传文件路径前缀：" + prefix);
            logger.println("生成下载路径前缀：" + netUrl);
            logger.println("保存下载链接文件：" + urlsFile);

            if (entry.isNoUploadOnFailure() && buildFailed) {
                logger.println("构建失败,跳过上传");
                continue;
            }

            Profile profile = this.getDescriptor().getProfileByName(
                    entry.getProfileName());
            if (profile == null) {
                logger.println("找不到配置项,跳过");
                continue;
            }

            //清除上次的文件内容
            if (!StringUtils.isNullOrEmpty(urlsFile)) {
                logger.println("写入下载链接文件地址 " + urlsFile);
                File file = new File(urlsFile);
                if (file.exists()) file.delete();
            }

            //密钥配置
            Auth auth = Auth.create(profile.getAccessKey(), profile.getSecretKey());

            //第二种方式: 自动识别要上传的空间(bucket)的存储区域是华东、华北、华南。
            Zone z = Zone.autoZone();
            Configuration c = new Configuration(z);

            //创建上传对象
            UploadManager uploadManager = new UploadManager(c);

            FilePath[] paths = ws.list(source);
            for (FilePath path : paths) {
                String fullPath = path.getRemote();
//                String keyPath = path.getRemote().replace(wsPath, "");
//                String key = keyPath.replace(File.separator, "/");
                String name = path.getName();

                if (!StringUtils.isNullOrEmpty(prefix)) {
                    name = prefix + name;
                }

                try {
                    int insertOnley = entry.isNoUploadOnExists() ? 1 : 0;
                    //上传策略。同名文件不允许再次上传。 文件相同，名字相同，返回上传成功。文件不同，名字相同，返回上传失败提示文件已存在。
                    StringMap putPolicy = new StringMap();
                    putPolicy.put("insertOnly", insertOnley);

                    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
                    String uploadToken = auth.uploadToken(bucket, name, 3600, putPolicy);

                    //调用put方法上传 文件路径，上传后保存文件名，token
                    Response res = uploadManager.put(fullPath, name, uploadToken);

                    //打印返回的信息
                    String bodyString = res.bodyString();

                    //默认body返回hash和key值
                    DefaultPutRet defaultPutRet = new Gson().fromJson(bodyString, DefaultPutRet.class);
//                    String hashString = defaultPutRet.hash;
                    //获得文件保存在空间中的资源名。
                    String keyString = defaultPutRet.key;

                    logger.println("上传 " + fullPath + " 到 " + bucket + " 成功." + bodyString);

                    //生成下载链接
                    netUrl = netUrl + keyString;

                    logger.println("下载链接　" + netUrl);

                    try {
                        if (!StringUtils.isNullOrEmpty(urlsFile)) {
                            File urlsFile1 = new File(urlsFile);
                            FileUtils.createFile(urlsFile1, logger);

                            netUrl += FileUtils.LINE_SEPARATOR;
                            FileUtils.writeFileFromString(urlsFile1, netUrl, true, logger);
                        }
                    } catch (Exception e) {
                        logger.println("写入链接文件失败！ " + e.getMessage());
                    }

                } catch (Exception e) {
                    logger.println("上传 " + fullPath + " 到 " + bucket + " 失败 ");
                    logger.println(e);
                    build.setResult(Result.UNSTABLE);
                }

            }
        }
        launcher.isUnix();
        logger.println("上传到七牛成功...");
        return true;
    }


    @Extension
    public static class FileUploadDescriptor extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        /**
         * To persist global configuration information, simply store it in a
         * field and call save().
         * <p>
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private final CopyOnWriteList<Profile> profiles = new CopyOnWriteList();

        public List<Profile> getProfiles() {
            return Arrays.asList(profiles.toArray(new Profile[0]));
        }

        public Profile getProfileByName(String profileName) {
            List<Profile> profiles = this.getProfiles();
            for (Profile profile : profiles) {
                //按照名字寻找Profile
                if (profileName.equals(profile.getName())) {
                    return profile;
                }
            }
            return null;
        }

        /**
         * In order to load the persisted global configuration, you have to call
         * load() in the constructor.
         */
        public FileUploadDescriptor() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the
         * browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does
         * not prevent the form from being saved. It just means that a
         * message will be displayed to the user.
         */
        public FormValidation doCheckAccessKey(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("七牛 AccessKey 不能为空");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckProfileName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("设置项名称不能为空");
            return FormValidation.ok();
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "上传文件到七牛云";
        }

        @Override
        public FileUploadRecorder newInstance(StaplerRequest req,
                                              JSONObject formData) throws FormException {
            List<FileUploadEntry> entries = req.bindJSONToList(FileUploadEntry.class,
                    formData.get("e"));
            FileUploadRecorder recorder = new FileUploadRecorder();
            recorder.addAll(entries);
            return recorder;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData)
                throws FormException {
            profiles.replaceBy(req.bindJSONToList(Profile.class,
                    formData.get("profile")));
            save();
            return true;
        }
    }
}
