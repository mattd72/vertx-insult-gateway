
package io.vertx.starter;

import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

  private Vertx vertx;

  @Before
  public void setUp(TestContext tc) {
    vertx = Vertx.vertx();

    JsonObject localConfig=new JsonObject();
    localConfig.put("gateway.host.springboot.noun", "thorntail-rest-http-thorntail-noun.b9ad.pro-us-east-1.openshiftapps.com");
    localConfig.put("gateway.host.springboot.noun.port", 80);
    localConfig.put("gateway.host.wildfly-swarm.adj", "spring-boot-rest-http-springboot-adj.b9ad.pro-us-east-1.openshiftapps.com");
    localConfig.put("gateway.host.wildfly-swarm.adj.port", 80);
    localConfig.put("gateway.host.vertx.adj", "spring-boot-rest-http-springboot-adj.b9ad.pro-us-east-1.openshiftapps.com");
    localConfig.put("gateway.host.vertx.adj.port", 80);


    vertx.deployVerticle(MainVerticle.class.getName(), tc.asyncAssertSuccess());
    vertx.deployVerticle(InsultGatewayVerticle.class.getName(),new DeploymentOptions().setConfig(localConfig), tc.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext tc) {
    vertx.close(tc.asyncAssertSuccess());
  }

  @Test
  public void testThatTheServerIsStarted(TestContext tc) {
    Async async = tc.async();
    vertx.createHttpClient().getNow(8080, "localhost", "/api/insult", response -> {

      response.bodyHandler(body -> {
    	tc.assertTrue(body.length() > 0);
    	tc.assertTrue(body.toJsonObject().containsKey("noun"));
        async.complete();
      });
    });
  }

}
