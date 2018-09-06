/**
 * Create with IntelliJ IDEA
 * Project name : qinniu-file
 * Package name : org.wukm.jenkins
 * Author : Wukunmeng
 * User : wukm
 * Date : 18-9-4
 * Time : 下午6:20
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
 * Time : 下午6:20
 * ---------------------------------
 * To change this template use File | Settings | File and Code Templates.
 */
public class Profile {

    //设置项名称
    private String name;

    //七牛的AccessKey
    private String accessKey;

    //七牛的SecretKey
    private String secretKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @DataBoundConstructor
    public Profile(String name, String accessKey, String secretKey){
        this.setAccessKey(accessKey);
        this.setName(name);
        this.setSecretKey(secretKey);
    }
}
