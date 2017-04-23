import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.HttpStatus
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.methods.GetMethod

object MainProxyTest extends App {
  val client = new HttpClient();
  val method = new GetMethod("https://api.thousandeyes.com:443/web/http-server/225572?format=json&from=2017-03-21T10:00:00&aid=443");
  val config = client.getHostConfiguration()

  //proxy settings
  config.setProxy("bcpxy.nycnet", 8080)
  val pxyCredentials = new UsernamePasswordCredentials("lali", "Llwz1962#")
  val pxyAuthScope = new AuthScope("bcpxy.nycnet", 8080);
  client.getState().setProxyCredentials(pxyAuthScope, pxyCredentials)

  val authScope = new AuthScope("api.thousandeyes.com", 443, AuthScope.ANY_REALM)
  val credentials = new UsernamePasswordCredentials("lali@doitt.nyc.gov", "hvkudi5gsum8a45ccm27vzeorcqvy2oh");
  client.getState().setCredentials(authScope, credentials)
  method.setDoAuthentication(true)
  //  client.getParams().setAuthenticationPreemptive(true)

  try {
    client.executeMethod(method);

    if (method.getStatusCode() == HttpStatus.SC_OK) {
      val response = method.getResponseBodyAsString();
      System.out.println(response);
    }
  } catch {
    case e: Exception => e.printStackTrace();
  }

}