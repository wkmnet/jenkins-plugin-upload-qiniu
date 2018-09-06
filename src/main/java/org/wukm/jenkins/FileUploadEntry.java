/**
 * Create with IntelliJ IDEA
 * Project name : qinniu-file
 * Package name : org.wukm.jenkins
 * Author : Wukunmeng
 * User : wukm
 * Date : 18-9-4
 * Time : 下午6:37
 * ---------------------------------
 */
package org.wukm.jenkins;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Create with IntelliJ IDEA
 * Project name : qinniu-file
 * Package name : org.wukm.jenkins
 * Author : Wukunmeng
 * User : wukm
 * Date : 18-9-4
 * Time : 下午6:37
 * ---------------------------------
 * To change this template use File | Settings | File and Code Templates.
 */
public class FileUploadEntry {

    //配置项名称
    private String profileName;

    //
    private String source;

    private String bucket;

    private String prefix;

    private String netUrl;

    private  String urlsFile;

    private boolean noUploadOnExists;

    private boolean noUploadOnFailure;

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }

    public String getUrlsFile() {
        return urlsFile;
    }

    public void setUrlsFile(String urlsFile) {
        this.urlsFile = urlsFile;
    }

    public boolean isNoUploadOnExists() {
        return noUploadOnExists;
    }

    public void setNoUploadOnExists(boolean noUploadOnExists) {
        this.noUploadOnExists = noUploadOnExists;
    }

    public boolean isNoUploadOnFailure() {
        return noUploadOnFailure;
    }

    public void setNoUploadOnFailure(boolean noUploadOnFailure) {
        this.noUploadOnFailure = noUploadOnFailure;
    }

    @DataBoundConstructor
    public FileUploadEntry(String profileName, String source,
                           String bucket, String prefix, String netUrl,
                           String urlsFile, boolean noUploadOnExists,
                           boolean noUploadOnFailure){
        this.profileName = profileName;
        this.source = source;
        this.bucket = bucket;
        this.prefix = prefix;
        this.netUrl = netUrl;
        this.urlsFile = urlsFile;
        this.noUploadOnExists = noUploadOnExists;
        this.noUploadOnFailure = noUploadOnFailure;
    }
}

