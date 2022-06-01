package vretx_calculator.calculator;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static final String ADD_ADDRESS = "com.calculator.add";
  public static final String SUBTRACT_ADDRESS = "com.calculator.subtract";
  public static final String MULTIPLY_ADDRESS = "com.calculator.multiply";
  public static final String DIVIDE_ADDRESS = "com.calculator.divide";
  public final Router router = Router.router(vertx);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.exceptionHandler(error->
    {
      LOG.error("Unhandled : ", error);
    });
    vertx.deployVerticle(new MainVerticle(), asynchronousResult -> {
      if(asynchronousResult.failed())
      {
        LOG.error("Failed to deploy : {} ", asynchronousResult.cause());
        return;
      }
      LOG.info("Deployed {} ", MainVerticle.class);
    });
    vertx.deployVerticle((Verticle) new OutVerticle());
  }
  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    router.get("/add/:num1/:num2").handler(this :: add);
    router.get("/sub/:num1/:num2").handler(this :: sub);
    router.get("/mul/:num1/:num2").handler(this :: mul);
    router.get("/div/:num1/:num2").handler(this :: div);

    vertx.createHttpServer().requestHandler(router)
      .exceptionHandler(error -> {
        LOG.error("HTTP Server Error : ", error);
      })
      .listen(8080, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port 8080");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }



  private void div(RoutingContext routingContext) {

    String num1 = routingContext.pathParam("num1");
    String num2 = routingContext.pathParam("num2");
    String message = num1 + " " + num2;
    vertx.eventBus().request(DIVIDE_ADDRESS, message, reply -> {
      routingContext.request().response().end((String) reply.result().body());
    });

  }

  private void mul(RoutingContext routingContext) {
    String num1 = routingContext.pathParam("num1");
    String num2 = routingContext.pathParam("num2");
    String message = num1 + " " + num2;
    vertx.eventBus().request(MULTIPLY_ADDRESS, message, reply -> {
      routingContext.request().response().end((String) reply.result().body());
    });
  }

  private void sub(RoutingContext routingContext) {

    String num1 = routingContext.pathParam("num1");
    String num2 = routingContext.pathParam("num2");
    String message = num1 + " " + num2;
    vertx.eventBus().request(SUBTRACT_ADDRESS, message, reply -> {
      routingContext.request().response().end((String) reply.result().body());
    });

  }

  private void add(RoutingContext routingContext) {

    String num1 = routingContext.pathParam("num1");
    String num2 = routingContext.pathParam("num2");

    String message = num1 + " " + num2;
    vertx.eventBus().request(ADD_ADDRESS,message, reply -> {
      routingContext.request().response().end((String)reply.result().body());
    });

  }
}
