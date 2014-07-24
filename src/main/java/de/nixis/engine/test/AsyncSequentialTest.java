
package de.nixis.engine.test;

import java.util.concurrent.ExecutorService;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;

/**
 *
 * @author nico.rehwaldt
 */
public class AsyncSequentialTest extends AbstractPerformanceTest {

  public AsyncSequentialTest(ProcessEngine processEngine) {
    super(processEngine);
  }

  @Override
  protected void generateLoad(ProcessEngine processEngine, ExecutorService loader) {
    final RuntimeService runtimeService = processEngine.getRuntimeService();

    for (int i = 0; i < 500; i++) {

      loader.submit(new Runnable() {

        @Override
        public void run() {
          runtimeService.startProcessInstanceByKey("AsyncSequential");
        }
      });
    }
  }

  @Override
  protected Deployment setup(ProcessEngine processEngine) {
    return processEngine.getRepositoryService()
      .createDeployment()
        .addClasspathResource("async-sequential.bpmn")
        .deploy();
  }

}
