package org.springframework.security.facebook;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: porter
 * Date: Jul 27, 2010
 * Time: 12:29:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class FacebookUtil {
    public boolean verifySignature(Map<String,String[]> cookieParams, String secret){
        return com.google.code.facebookapi.FacebookSignatureUtil.autoVerifySignature(cookieParams, secret);
    }
}
