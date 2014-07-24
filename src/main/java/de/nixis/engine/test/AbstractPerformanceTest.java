package de.nixis.engine.test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.Deployment;



/**
 *
 * @author nico.rehwaldt
 */
public abstract class AbstractPerformanceTest {

  private final ProcessEngine processEngine;

  private final List<Trace> traces;

  private long start;

  private ScheduledExecutorService scheduler;

  private ExecutorService loader;

  private Deployment deployment;

  public static class Trace {

    public final long time;
    public final long processInstances;

    public Trace(long time, long processInstances) {
      this.time = time;
      this.processInstances = processInstances;
    }

    @Override
    public String toString() {
      return String.format("%d\t%d", this.time, this.processInstances);
    }
  }


  public AbstractPerformanceTest(ProcessEngine processEngine) {
    this.processEngine = processEngine;

    this.traces = new LinkedList<>();
  }

  protected abstract void generateLoad(ProcessEngine processEngine, ExecutorService loadGenerator);
  protected abstract Deployment setup(ProcessEngine processEngine);

  private class Logger implements Runnable {

    @Override
    public void run() {
      long executions = processEngine.getRuntimeService().createProcessInstanceQuery().count();
      trace(executions);

      if (executions == 0) {
        scheduler.shutdown();
      }
    }
  }

  protected void trace(long executions) {
    Trace t = new Trace(System.currentTimeMillis() - start, executions);
    System.out.println(t.toString());

    traces.add(t);
  }

  private void start() throws Exception {
    loader = Executors.newFixedThreadPool(2);
    scheduler = Executors.newSingleThreadScheduledExecutor();

    scheduler.scheduleAtFixedRate(new Logger(), 2, 2, TimeUnit.SECONDS);

    deployment = setup(processEngine);

    start = System.currentTimeMillis();
  }

  private void stop() {
    loader.shutdownNow();
    scheduler.shutdownNow();

    processEngine.getRepositoryService().deleteDeployment(deployment.getId(), true);
  }

  public void execute() throws Exception {

    System.out.println("> starting");

    start();

    // initial trace
    trace(0);

    // generate load
    generateLoad(processEngine, loader);

    System.out.println("> generated load");

    scheduler.awaitTermination(10, TimeUnit.MINUTES);

    System.out.println("> test execution done");

    stop();

    System.out.println("> executors shut down");
  }

  public void printResults() {

    StringBuilder builder = new StringBuilder();

    for (Trace t: traces) {
      builder.append(t.toString()).append("\n");
    }

    System.out.println(builder.toString());
  }
}