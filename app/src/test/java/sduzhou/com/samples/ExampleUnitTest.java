package sduzhou.com.samples;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String html = "javascript:gotoIfremBody('/dqmh/ssoLink.do?method=skip&platNo=10001&toStUrl=http://bj.189.cn/iframe/pay/sellCardInvoiceIndex.action','20000566')";
        String subs[] = html.split("toStUrl=");
        if(subs != null && subs.length > 1){
            String result = subs[1].substring(0,subs[1].indexOf("/iframe"));
            System.out.println(result);
        }

        String cookie = "svid=D6BA6260D906AE3; lvid=19c08f3edc72a071ea851ba57184ecaf; nvid=1; aactgsh111220=15311491775; userId=201%7C20150000000016933763; isLogin=logined; .ybtj.189.cn=B67641D1D8F9BF154D9F2F27D3113F89; cityCode=bj; SHOPID_COOKIEID=10001; loginStatus=logined; s_cc=true; trkHmClickCoords=-1%2C0%2C2487; s_fid=64CE9725DA058C04-3655E1D36F9D534C; trkId=72E94FA8-19D5-465A-9226-52A05DE6B1F2; s_sq=%5B%5BB%5D%5D; JSESSIONID_bj=XkS9ZtmhW6gXGl2d2cThH2rnNXc1kH4V2hGnQnjGTf0wGGVhHQNv!-1539535543";
        int index = cookie.indexOf("aactgsh");
        int indexEqual = cookie.indexOf("=", index);
        int indexEnd = cookie.indexOf(";", indexEqual);

        System.out.println(cookie.substring(indexEqual+1,indexEnd));
    }


}