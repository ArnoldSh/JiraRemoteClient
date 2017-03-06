  ## JiraRemoteClient
  Atlassian Jira Remote Client (SOAP/REST implementation)
  Provides full functionality of RPC plugins.
  ## Example code
  Below places example of SSL auth method:
```java

import org.radixware.jiraclient.implementation.common.JiraClientFactory;
import org.radixware.jiraclient.wrap.Issue;
import org.radixware.jiraclient.wrap.JiraClient;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class main {
	public static void main(String[] args) throws Exception {

		// this example illustrates usage of library
		// particularly using of SSL context for authentication

		String keyStoreFileName = "C:/path/to/myCertAndPrivateKey.p12";
		char[] password = new String("myPrivateKeySecretPassphrase").toCharArray();

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		// or KeyStore keyStore = KeyStore.getInstance("JCEKS");

		FileInputStream fs = null;
		try {
			fs = new FileInputStream(keyStoreFileName);
			keyStore.load(fs, password);
		} finally {
			fs.close();
			keyStore.load(null, password);
		}

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, password);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

		// using prepared SSL-context for authentication
		JiraClient jiraClient =
				JiraClientFactory.createWithSSLContext(new URL("https://jira.mycompany.ru:443"), sslContext);

		String jql = "assignee = ashamsutdinov";

		Iterable<Issue> issues = jiraClient.getIssuesByFilterQuery(jql, 200);

		Iterator<Issue> itr = issues.iterator();

		while(itr.hasNext()) {

			Issue issue = itr.next();

			// do something with issue info
			// ...

		}
	}
}
```
