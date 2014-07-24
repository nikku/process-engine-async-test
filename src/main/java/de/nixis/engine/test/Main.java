
package de.nixis.engine.test;


import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.jobexecutor.DefaultJobExecutor;

/**
 *
 * @author nico.rehwaldt
 */
public class Main {

  private static String getProperty(String name, String defaultValue) {
    return System.getProperty(name, defaultValue);
  }

  public static void main(String[] arguments) throws Exception {

    ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();

    // align job executor configurations
    DefaultJobExecutor jobExecutor = new DefaultJobExecutor();
    jobExecutor.setMaxPoolSize(10);
    jobExecutor.setMaxJobsPerAcquisition(3);

    ((ProcessEngineConfigurationImpl) configuration).setJobExecutor(jobExecutor);

    // start job executor
    configuration.setJobExecutorActivate(true);


    // configure database
    configuration.setJdbcUrl(getProperty("db.connection.url", "jdbc:mysql://localhost:3306/test"));
    configuration.setJdbcUsername(getProperty("db.connection.username", "root"));
    configuration.setJdbcPassword(getProperty("db.connection.password", ""));
    configuration.setJdbcDriver(getProperty("db.connection.driver", "com.mysql.jdbc.Driver"));
    configuration.setJdbcMaxActiveConnections(100);

    configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP);

    ProcessEngine processEngine = configuration.buildProcessEngine();

    AbstractPerformanceTest test;

    System.out.println();
    System.out.println("> async parallel test");
    test = new AsyncParallelTest(processEngine);
    test.execute();

    System.out.println("> summary");
    test.printResults();

    System.out.println();
    System.out.println("> async sequential test");
    test = new AsyncSequentialTest(processEngine);
    test.execute();

    System.out.println("> summary");
    test.printResults();

    // shutdown process engine
    processEngine.close();
  }
}
